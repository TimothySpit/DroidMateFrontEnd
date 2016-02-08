package com.droidmate.processes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.droidmate.interfaces.APKLogFileObservable;
import com.droidmate.interfaces.APKLogFileObserver;
import com.droidmate.interfaces.Observable;
import com.droidmate.interfaces.ProcessStreamObservable;
import com.droidmate.interfaces.ProcessStreamObserver;
import com.droidmate.processes.ProcessWrapper.ProcessStreamEvent;
import com.droidmate.processes.ProcessWrapper.StreamCallbackType;
import com.droidmate.processes.logfile.APKElementsExploredChanged;
import com.droidmate.processes.logfile.APKElementsSeenChanged;
import com.droidmate.processes.logfile.APKEnded;
import com.droidmate.processes.logfile.APKExplorationEnded;
import com.droidmate.processes.logfile.APKExplorationStarted;
import com.droidmate.processes.logfile.APKLogFileHandler;
import com.droidmate.processes.logfile.APKScreensSeenChanged;
import com.droidmate.processes.logfile.APKStarted;
import com.droidmate.user.APKInformation;
import com.droidmate.user.ExplorationInfo;
import com.droidmate.user.ExplorationStatus;
import com.droidmate.user.InliningStatus;

public class DroidMateProcess extends Observable<DroidMateProcessEvent> implements APKLogFileObserver, ProcessStreamObserver {

	private final File droidMatePath;
	private final File logFilePath;
	private boolean printStackTrace;

	private Map<String, APKInformation> apksToExplore = new HashMap<>();

	// exploration variables
	private APKInformation currentAPK = null;
	// ---------------------

	//global exploration info of all apks
	private ExplorationInfo globalExplorationInfo = new ExplorationInfo();
	//----------------------
	private ProcessWrapper droidMateProcess = null;
	private APKLogFileHandler logReader = null;
	
	public DroidMateProcess(File droidMatePath, File logFilePath) throws FileNotFoundException {
		if (droidMatePath == null) {
			throw new IllegalArgumentException("DroidMate path must not be null");
		}
		if (!droidMatePath.exists()) {
			throw new FileNotFoundException("DroidMate path " + droidMatePath + " does not exist.");
		}
		if (!droidMatePath.isDirectory()) {
			throw new IllegalArgumentException("DroidMate path must be an directory.");
		}
		if (logFilePath == null) {
			throw new IllegalArgumentException("Log path must not be null");
		}
		// log file must not exist
		/*
		 * if (!logFilePath.exists()) { throw new FileNotFoundException(
		 * "Log path " + logFilePath + " does not exist."); } if
		 * (!logFilePath.isDirectory()) { throw new IllegalArgumentException(
		 * "Log path must be an directory."); }
		 */

		this.droidMatePath = droidMatePath;
		this.logFilePath = logFilePath;
	}

	public DroidMateProcess(File droidMatePath, File logFilePath, boolean printStackTrace) throws FileNotFoundException {
		this(droidMatePath, logFilePath);

		this.setPrintStackTrace(printStackTrace);
	}

	public void startExploration(List<APKInformation> apksToExplore) throws IOException {
		if (apksToExplore == null) {
			throw new IllegalArgumentException("APKS list must not be null.");
		}
		if (apksToExplore.size() == 0) {
			throw new IllegalArgumentException("No apks specified to explore.");
		}

		// create Process arguments and start DroidMate
		List<String> arguments = new LinkedList<>();
		arguments.add(droidMatePath.toString() + "/gradlew.bat"); // only
																	// support
		// windows here
		if (printStackTrace) {
			arguments.add("--stacktrace");
		}
		arguments.add(":projects:core:run");

		// set apks to explore
		for (APKInformation apk : apksToExplore) {
			this.apksToExplore.put(apk.getAPKName(), apk);
		}

		tryStartDroidMate(arguments);
	}

	private void tryStartDroidMate(List<String> arguments) throws IOException {
		assert apksToExplore != null && arguments != null;

		// get all needed droidmate paths and check for correctness
		File droidMateAPKInputPath = new File(this.droidMatePath, "/apks/inlined/");
		File droidMateOutputLogFile = new File(this.droidMatePath, "/dev1/logs/gui.xml");

		// check all paths
		if (!droidMateAPKInputPath.exists()) {
			throw new FileNotFoundException("DroidMate APK input path " + droidMateAPKInputPath + " does not exist.");
		}
		if (!droidMateAPKInputPath.isDirectory()) {
			throw new IOException("DroidMate APK input path " + droidMateAPKInputPath + " must be a directory.");
		}

		// all needed directories seem correct, clear DroidMate input folder
		clearAPKSFromDirectory(droidMateAPKInputPath);

		// copy all inlined and selected apks from /inlined folder to DroidMate
		// input directory
		for (APKInformation apk : apksToExplore.values()) {
			if (!apk.isAPKSelected()) {
				continue;
			}
			Path inlinedAPKName = Paths.get(FilenameUtils.removeExtension(apk.getAPKFile().getName()) + "-inlined.apk");
			Path targetFile = Paths.get(apk.getAPKFile().getParent().toString(), "/inlined/", inlinedAPKName.toString());
			Path destinationFile = Paths.get(droidMateAPKInputPath.toString(), inlinedAPKName.toString());

			// check if file is marked as inlined
			if (apk.getInliningStatus() != InliningStatus.INLINED) {
				throw new IllegalStateException("APK " + apk.getAPKName() + " is selected but not in INLINED state.");
			}
			// check if target file exists
			if (!targetFile.toFile().exists()) {
				throw new FileNotFoundException("Inlined APK " + targetFile + " does not exist.");
			}
			// check if no apk has been explored before
			if (apk.getExplorationStatus() != ExplorationStatus.NOT_RUNNING) {
				throw new IllegalStateException("APK " + apk.getAPKName() + " has already been explored");
			}

			Files.copy(targetFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
		}

		// delete old DroidMate log file
		if (droidMateOutputLogFile.exists() && !droidMateOutputLogFile.delete()) {
			// logfile could not be deleted
			throw new IOException(droidMateOutputLogFile + " could not be deleted.");
		}

		// create log reader in new thread and register events
		logReader  = new APKLogFileHandler(droidMateOutputLogFile, true);
		logReader.addObserver(this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				logReader.start();
			}
		}).start();
		// start DroidMate
		try {
			if (!tryStartDroidMateProcess(logReader, arguments)) {
				notifyObservers(new DroidMateProcessEvent(DroidMateProcessEvent.EventType.DROIDMATE_ERROR));
			}
		} catch (InterruptedException e) {
			notifyObservers(new DroidMateProcessEvent(DroidMateProcessEvent.EventType.DROIDMATE_ERROR));
		} catch (IOException e) {
			notifyObservers(new DroidMateProcessEvent(DroidMateProcessEvent.EventType.DROIDMATE_ERROR));
			throw e;
		}
	}

	private boolean tryStartDroidMateProcess(APKLogFileHandler logReader, List<String> arguments) throws InterruptedException, IOException {
		assert apksToExplore != null && apksToExplore.size() > 0;

		// create inliner process
		droidMateProcess  = new ProcessWrapper(droidMatePath, arguments);
		// register console observer
		droidMateProcess.addStreamObserver(this);

		droidMateProcess.start();

		// DroidMate process finished, stop logreader
		logReader.stop();

		if (droidMateProcess.getExitValue() != 0) {
			// there was an intern error
			return false;
		}
		return true;
	}

	private void clearAPKSFromDirectory(File path) {
		assert path != null && path.exists();

		// delete all APK files
		File[] filesToDelete = path.listFiles(new FilenameFilter() {
			private final String fileExtension = "apk";

			@Override
			public boolean accept(File dir, String name) {
				return FilenameUtils.getExtension(name).equals(fileExtension);
			}
		});

		for (File file : filesToDelete) {
			file.delete();
		}
	}

	public File getDroidMatePath() {
		return droidMatePath;
	}

	public File getLogFilePath() {
		return logFilePath;
	}

	public void setPrintStackTrace(boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
	}

	public ExplorationInfo getGlobalExplorationInfo() {
		return globalExplorationInfo;
	}
	
	// APKLogFileObservable interface methods
	@Override
	public void update(APKLogFileObservable o, APKExplorationStarted event) {
		// Exploration started
		notifyObservers(new DroidMateProcessEvent(DroidMateProcessEvent.EventType.EXPLORATION_STARTED));
		
		this.globalExplorationInfo.setStartingTime(event.getStartTime());
	}

	@Override
	public void update(APKLogFileObservable o, APKExplorationEnded event) {
		// Exploration ended with no errors
		notifyObservers(new DroidMateProcessEvent(DroidMateProcessEvent.EventType.EXPLORATION_FINISHED));
		
		this.globalExplorationInfo.setEndTime(event.getEndTime());
	}

	@Override
	public void update(APKLogFileObservable o, APKStarted event) {
		// set up currently explored apk
		if (!apksToExplore.containsKey(event.getName())) {
			throw new IllegalStateException("APK " + event.getName() + " was not selected for Exploration");
		}

		if(this.currentAPK != null && this.currentAPK.getExplorationStatus() == ExplorationStatus.EXPLORING) {
			this.currentAPK.setExplorationStatus(ExplorationStatus.ERROR);
			this.currentAPK.getExplorationInfo().setEndTime(event.getStartTime());
		}
		
		this.currentAPK = apksToExplore.get(event.getName());
		this.currentAPK.setExplorationStatus(ExplorationStatus.EXPLORING);
		this.currentAPK.getExplorationInfo().setStartingTime(event.getStartTime());
	}

	@Override
	public void update(APKLogFileObservable o, APKEnded event) {
		if (currentAPK == null) {
			throw new IllegalStateException("APKEnded tag before  APKStarted tag.");
		}
		// set time

		this.currentAPK.getExplorationInfo().setEndTime(event.getEndTime());
		if (event.isSuccess()) {
			this.currentAPK.setExplorationStatus(ExplorationStatus.SUCCESS);
		} else {
			this.currentAPK.setExplorationStatus(ExplorationStatus.ERROR);
		}
		this.currentAPK = null;
	}

	@Override
	public void update(APKLogFileObservable o, APKElementsExploredChanged event) {
		if (currentAPK == null) {
			throw new IllegalStateException("APKElementsExploredChanged tag before  APKStarted tag.");
		}
		this.currentAPK.getExplorationInfo().addElementsExplored(event.getChangeInElementsExplored());
		this.globalExplorationInfo.addElementsExplored(event.getChangeInElementsExplored());
	}

	@Override
	public void update(APKLogFileObservable o, APKElementsSeenChanged event) {
		if (currentAPK == null) {
			throw new IllegalStateException("APKElementsSeenChanged tag before  APKStarted tag.");
		}
		this.currentAPK.getExplorationInfo().addElementsSeen(event.getChangeInElementsSeen());
		this.globalExplorationInfo.addElementsSeen(event.getChangeInElementsSeen());
	}

	@Override
	public void update(APKLogFileObservable o, APKScreensSeenChanged event) {
		if (currentAPK == null) {
			throw new IllegalStateException("APKScreensSeenChanged tag before  APKStarted tag.");
		}
		this.currentAPK.getExplorationInfo().addScreensSeen(event.getChangeInScreensSeen());
		this.globalExplorationInfo.addScreensSeen(event.getChangeInScreensSeen());
	}

	// Process stream observer methods

	@Override
	public void update(ProcessStreamObservable o, ProcessStreamEvent arg) {
		if (arg.getType() == StreamCallbackType.STDOUT) {
			notifyObservers(new DroidMateProcessEvent(DroidMateProcessEvent.EventType.CONSOLE_OUTPUT_STDOUT, arg.getMessage()));
		} else if(arg.getType() == StreamCallbackType.ERROR){
			notifyObservers(new DroidMateProcessEvent(DroidMateProcessEvent.EventType.CONSOLE_OUTPUT_ERROR, arg.getMessage()));
		}
	}

	public void stopExploration() {
		if(logReader != null) {
			logReader.deleteObserver(this);
			if(this.currentAPK != null && this.currentAPK.getExplorationStatus() == ExplorationStatus.EXPLORING) {
				this.currentAPK.setExplorationStatus(ExplorationStatus.ERROR);
				this.currentAPK.getExplorationInfo().setEndTime(System.currentTimeMillis());
			}
			logReader.stop();
		}

		if(droidMateProcess != null) {
			droidMateProcess.deleteStreamObserver(this);
			droidMateProcess.stop();
		}
	}
}
