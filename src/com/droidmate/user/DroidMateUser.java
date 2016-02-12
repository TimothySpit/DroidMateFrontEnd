package com.droidmate.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.interfaces.Observable;
import com.droidmate.interfaces.Observer;
import com.droidmate.processes.AAPTInformation;
import com.droidmate.processes.AAPTProcess;
import com.droidmate.processes.DroidMateProcess;
import com.droidmate.processes.DroidMateProcessEvent;
import com.droidmate.processes.InlinerProcess;

/**
 * Sets the path for the .apks to be explored and tracks the exploration status
 * for all .apks.
 */
public class DroidMateUser implements Observer<DroidMateProcessEvent> {
	private final static String INLINED_APKS_FOLDER_NAME = "inlined";

	/** Path to the folder containing .apk files */
	private Path apksRootPath = null;

	/** List of .apks informations from the selected .apks ordner. */
	private Map<String, APKInformation> apksInformation = new ConcurrentHashMap<>();

	/** Instance of GUISettings */
	private final GUISettings settings;

	private List<String> consoleOutput = new LinkedList<>();

	/**
	 * The current status the user is in.
	 */
	private final AtomicReference<UserStatus> userStatus = new AtomicReference<>(UserStatus.IDLE);

	// Processes the user can start
	private InlinerProcess inlinerProcess = null;
	private DroidMateProcess droidMateProcess = null;
	// ----------------------------

	/**
	 * Creates a new instance of the DroidMateUser class
	 */
	public DroidMateUser() {
		// get current settings
		this.settings = new GUISettings();
	}

	/**
	 * Gets the .apks path.
	 * 
	 * @return The .apks path
	 */
	public Path getAPKPath() {
		return apksRootPath;
	}

	/**
	 * Sets the path for the users given .apks.
	 * 
	 * @param newPath
	 *            the path for the apks
	 * @throws NullPointerException
	 *             if the given path ist null
	 * @throws IllegalArgumentException
	 *             if the given path not a directory or does not exist
	 * @throws IOException
	 *             if a IO Error occured
	 * @throws InterruptedException
	 *             in crazy thread stuff
	 */
	public synchronized void setAPKPath(Path newPath) throws IOException, InterruptedException {
		// exception handling
		if (newPath == null) {
			throw new NullPointerException("APK root path was null.");
		}
		if (!(newPath.toFile().exists() && newPath.toFile().isDirectory())) {
			throw new IllegalArgumentException("APK root path must exist and must be a directory.");
		}
		if (userStatus.get() != UserStatus.IDLE) {
			throw new IllegalStateException("User is not in IDLE state. Cannot set APK path now.");
		}

		apksInformation.clear();
		apksRootPath = newPath;

		// collect new information
		Path aaptPath = settings.getAaptToolPath();

		if (!Files.exists(aaptPath)) {
			throw new FileNotFoundException("AAPT path not found at: " + aaptPath);
		}
		if (!Files.isDirectory(aaptPath)) {
			throw new FileNotFoundException("AAPT path: " + aaptPath + " is no directory.");
		}

		// aapt path exists, try to load info
		try {
			List<AAPTInformation> aaptResult = getAAPTInformation(aaptPath.toFile());
			// create APKInformation out of AAPT information
			List<APKInformation> apksInfos = setUpAPKInformations(aaptResult);
			// save all apks
			for (APKInformation apkInfo : apksInfos) {
				this.apksInformation.put(apkInfo.getAPKName(), apkInfo);
			}

		} catch (IOException e) {
			// error, reset apk path
			apksRootPath = null;
			throw e;
		}

	}

	/**
	 * Sets the .apks information and return a list of them
	 * 
	 * @param aaptResults
	 * @return List of .apks information
	 */
	private synchronized List<APKInformation> setUpAPKInformations(List<AAPTInformation> aaptResults) {
		List<APKInformation> apksInfos = new LinkedList<>();

		for (AAPTInformation aaptInfo : aaptResults) {
			// create new APKInformation
			APKInformation apkInfo = new APKInformation(aaptInfo);
			setAPKInlinedInformation(apkInfo);
			apksInfos.add(apkInfo);
		}

		return apksInfos;
	}

	/**
	 * Sets the .apks information and checks whether it is inlined or not.
	 * 
	 * @param apkInfo
	 *            the information to be set
	 */
	private void setAPKInlinedInformation(APKInformation apkInfo) {
		assert apkInfo != null;

		// check if inlined apks folder exists
		Path inlinedAPKSPath = Paths.get(apksRootPath.toString(), INLINED_APKS_FOLDER_NAME);
		if (Files.exists(inlinedAPKSPath)) {
			// check if apk is in that folder, if yes, the file is already
			// inlined.
			String inlinedAPKName = FilenameUtils.removeExtension(apkInfo.getAPKFile().getName()) + "-inlined.apk";
			Path inlinedAPKPath = Paths.get(inlinedAPKSPath.toString(), inlinedAPKName);

			if (Files.exists(inlinedAPKPath)) {
				// File is already inlined
				apkInfo.setInliningStatus(InliningStatus.INLINED);
			} else {
				// File is not inlined
				apkInfo.setInliningStatus(InliningStatus.NOT_INLINED);
			}
		}
	}

	/**
	 * Gets .apks aapt informations.
	 * 
	 * @param aaptPath
	 * @return List of .apks aapt informations
	 * @throws InterruptedException
	 * @throws InterruptedException
	 */
	private List<AAPTInformation> getAAPTInformation(File aaptPath) throws IOException, InterruptedException {
		assert aaptPath != null && aaptPath.exists() && aaptPath.isDirectory();

		// create AAPT process
		AAPTProcess aaptHelper = new AAPTProcess(aaptPath);

		List<File> apks = getAPKFilesFromDirectory(apksRootPath);

		// load apk aapt information and return it
		List<AAPTInformation> aaptResult = aaptHelper.loadInformation(apks);
		return aaptResult;
	}

	/**
	 * Gets found .apks files from given path's directory.
	 * 
	 * @param apksPath
	 * @return List of all found .apks in the given directory
	 */
	private List<File> getAPKFilesFromDirectory(Path apksPath) {
		assert apksPath != null && apksPath.toFile().exists() && apksPath.toFile().isDirectory();

		// Filter apks
		File[] apkFiles = apksPath.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return FilenameUtils.getExtension(name).toLowerCase().equals("apk");
			}
		});

		// There was an IO error, or apksPath is no directory
		if (apkFiles == null) {
			return new LinkedList<>();
		}

		// return all found apks
		return Arrays.asList(apkFiles);
	}

	/**
	 * Gets the .apks informations.
	 * 
	 * @return List of .apks informations
	 */
	public Map<String, APKInformation> getAPKS() {
		return apksInformation;
	}

	/**
	 * Returns whether the inliner is started.
	 * 
	 * @return true if the inliner is started
	 */
	public boolean isInlinerStarted() {
		return userStatus.get() == UserStatus.INLINING;
	}

	/**
	 * Starts the inliner.
	 * 
	 * @return true if the inliner was successfully started
	 * @throws IOException
	 *             if an IO error occured
	 */
	public synchronized boolean startInliner() throws IOException {
		if (userStatus.get() != UserStatus.IDLE) {
			throw new IllegalStateException("Inliner can not be started in state " + userStatus.get().getName());
		}
		if (apksRootPath == null) {
			throw new IllegalArgumentException("APK Path has not been set.");
		}
		if (!apksRootPath.toFile().exists()) {
			throw new FileNotFoundException("APK path does not exist.");
		}
		if (!apksRootPath.toFile().isDirectory()) {
			throw new IllegalArgumentException("APK path must be a directory");
		}

		// change user state
		userStatus.set(UserStatus.INLINING);

		// set up apks to inline
		List<APKInformation> apksToInline = new LinkedList<>();
		for (APKInformation apk : apksInformation.values()) {
			if (apk.getInliningStatus() != InliningStatus.INLINED) {
				apksToInline.add(apk);
			}
		}

		// if there are no apks to inline, no need to inline, return true
		if (apksToInline.size() == 0) {
			this.userStatus.set(UserStatus.IDLE);
			return true;
		}

		// if inliner was never used, create it
		boolean inlineResult = false;
		try {
			if (inlinerProcess == null) {
				// Get inliner path and otput path
				Path inlinerPath = settings.getDroidMatePath();
				Path outputPath = apksRootPath;
				inlinerProcess = new InlinerProcess(inlinerPath.toFile(), outputPath.toFile());
			}

			// start inliner
			inlineResult = inlinerProcess.inlineAPKS(apksToInline);
		} catch (Exception e) {
			// rethrow exception
			throw e;
		} finally {
			// change user state
			this.userStatus.set(UserStatus.IDLE);
		}

		return inlineResult;
	}

	/**
	 * Returns the users status
	 * 
	 * @return the users status
	 */
	public UserStatus getStatus() {
		return userStatus.get();
	}

	/**
	 * Says whether the exploration is started
	 * 
	 * @return tru if the exploration is started, false otherwise
	 */
	public boolean isExplorationStarted() {
		UserStatus currentStatus = userStatus.get();
		// exploratioin is currently running or was running, in both cases,
		// exploration is (was) started and not reset to IDLE
		return currentStatus == UserStatus.EXPLORING || currentStatus == UserStatus.FINISHED || currentStatus == UserStatus.ERROR;
	}

	/**
	 * Starts DroidMate and eo ipso the exploration
	 * 
	 * @return true if DroidMate started the exploration
	 * @throws Exception
	 *             if something went wrong
	 */
	public synchronized boolean startDroidMate() throws Exception {
		if (userStatus.get() != UserStatus.IDLE) {
			throw new IllegalStateException("DroidMate can not be started in state " + userStatus.get().getName());
		}
		if (apksRootPath == null) {
			throw new IllegalArgumentException("APK Path has not been set.");
		}
		if (!apksRootPath.toFile().exists()) {
			throw new FileNotFoundException("APK path does not exist.");
		}
		if (!apksRootPath.toFile().isDirectory()) {
			throw new IllegalArgumentException("APK path must be a directory");
		}

		// get all APKs to explore
		List<APKInformation> apksToExplore = new LinkedList<>();

		// there are apks to explore, check if all selected APKs are inlined
		for (APKInformation apk : apksInformation.values()) {
			if (apk.isAPKSelected()) {
				if (apk.getInliningStatus() != InliningStatus.INLINED) {
					throw new IllegalStateException(
							"APK " + apk.getAPKName() + " is selected for DroidMate, but not inlined yet. DroidMate can only be run on inlined APKs.");
				} else {
					apksToExplore.add(apk);
				}
			}
		}

		// check if there are apks to explore
		if (apksToExplore.size() == 0) {
			throw new IllegalStateException("No selected apks specified to explore. Please select a path with APKs and select a subset of them.");
		}

		// all APKs are inlined, change state and start DroidMate
		userStatus.set(UserStatus.STARTING);

		// start DroidMate
		try {
			if (droidMateProcess == null) {
				// Get inliner path and otput path
				Path droidMatePath = settings.getDroidMatePath();
				Path logFilePath = Paths.get(droidMatePath.toString(), "/dev1/logs/gui.xml");
				droidMateProcess = new DroidMateProcess(droidMatePath.toFile(), logFilePath.toFile());
				droidMateProcess.setPrintStackTrace(true);
			}

			// register observer to signal finished exploration
			droidMateProcess.addObserver(this);

			// start inliner
			droidMateProcess.startExploration(apksToExplore, settings.getExplorationTimeout());
		} catch (Exception e) {
			// error in DroidMate
			// if exploration was already finished, dont change state
			if (userStatus.get() != UserStatus.FINISHED) {
				userStatus.set(UserStatus.ERROR);
			}
			throw e;
		}

		return true;
	}

	@Override
	public void update(Observable<DroidMateProcessEvent> o, DroidMateProcessEvent event) {
		switch (event.getEvent()) {
		case EXPLORATION_STARTED: {
			userStatus.set(UserStatus.EXPLORING);
			break;
		}
		case EXPLORATION_FINISHED: {
			userStatus.set(UserStatus.FINISHED);
			saveReport();
			break;
		}
		case DROIDMATE_ERROR: {
			synchronized (userStatus) {
				// if not exploring, ignore error, because exploration is
				// already
				// finished or aborted
				if (userStatus.get() == UserStatus.FINISHED || userStatus.get() == UserStatus.ERROR) {
					return;
				}

				if (userStatus.get() == UserStatus.IDLE) {
					// user got already cleared
					return;
				}

				if (userStatus.get() != UserStatus.STARTING) {
					// apks were explored, report saving necessary
					saveReport();
				}
				userStatus.set(UserStatus.ERROR);
			}
			break;
		}
		case CONSOLE_OUTPUT_STDOUT:
			synchronized (consoleOutput) {
				consoleOutput.add(event.getMessage());
			}
			break;
		case CONSOLE_OUTPUT_STDERR:
			synchronized (consoleOutput) {
				consoleOutput.add(event.getMessage());
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Saves the exploration report
	 */
	private void saveReport() {
		// get report output path
		Path outputFolder = settings.getOutputFolder();

		// check if path exists
		if (!outputFolder.toFile().exists()) {
			return;
		}

		// get resource path to report template
		URL url = getClass().getResource("/../reportTemplate");

		// create report output folder
		String reportTimestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		Path reportOutputFolder = Paths.get(outputFolder.toString(), "Report_" + reportTimestamp);
		reportOutputFolder.toFile().mkdir();

		// copy it to output folder
		try {
			Path walkPath = (new File(url.toURI())).toPath();
			Files.walk(walkPath).forEach(path -> {
				if (!walkPath.equals(path)) {
					try {
						Path dst = Paths.get(reportOutputFolder.toString(), path.toString().replace(walkPath.toString(), ""));
						Files.copy(path, dst);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// get data.js and insert data
		Path dataPath = Paths.get(reportOutputFolder.toString(), "resources/js/data.js");

		List<String> consoleOutputList = this.getConsoleOutput(0, getConsoleOutputSize());
		String consoleOutputString = "";
		for (String string : consoleOutputList) {
			consoleOutputString += string + "\\n";
		}

		StringBuilder dataString = new StringBuilder();
		dataString.append("$(function() {");
		dataString.append("var APKData = " + collectAPKData().toString() + ";");
		dataString.append("$.APKData = APKData;");
		dataString.append("$.APK_CONSOLE_DATA = \"" + StringEscapeUtils.escapeHtml(consoleOutputString) + "\";");
		dataString.append("});");

		// save file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(dataPath.toString(), "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		writer.print(dataString.toString());
		writer.close();
	}

	/**
	 * Returns the overall apk data as a JSON object
	 * 
	 * @return the overall apk data as a JSON object
	 */
	private JSONObject collectAPKData() {
		JSONObject result = new JSONObject();
		// per apk data
		JSONArray apkInfoString = new JSONArray();
		for (APKInformation apk : apksInformation.values()) {
			if (apk.isAPKSelected()) {
				apkInfoString.put(apk.toJSONObject());
			}
		}
		result.put("apks", apkInfoString);

		// global data
		result.put("globalExploration", getGloblExplorationInfo().toJSONObject());

		return result;
	}

	/**
	 * Returns the global exploration info
	 * 
	 * @return the global exploration info
	 */
	public ExplorationInfo getGloblExplorationInfo() {
		if (droidMateProcess == null) {
			throw new IllegalStateException("DroidMate was never started.");
		}

		return droidMateProcess.getGlobalExplorationInfo();
	}

	/**
	 * Returns the console output's size
	 * 
	 * @return the console output's size
	 */
	public int getConsoleOutputSize() {
		int size = 0;
		synchronized (consoleOutput) {
			size = consoleOutput.size();
		}
		return size;
	}

	/**
	 * Retrieves a specified subset of the console output.
	 * 
	 * @param from
	 *            the line the subset should start
	 * @param to
	 *            the line the subset shuld end
	 * @return the specified console output's subset
	 */
	public List<String> getConsoleOutput(int from, int to) {
		List<String> copyList = new LinkedList<>();
		synchronized (consoleOutput) {
			for (int i = from; i < to; i++) {
				copyList.add(consoleOutput.get(i));
			}
		}
		return copyList;
	}

	public GUISettings getSettings() {
		return settings;
	}

	/**
	 * Stops the exploration
	 */
	public void stopExploration() {
		synchronized (userStatus) {
			if (userStatus.get() != UserStatus.EXPLORING) {
				return;
			}

			// check for errors
			if (droidMateProcess == null) {
				throw new IllegalStateException("DroidMate is not running.");
			}

			// stop exploration
			droidMateProcess.stopExploration();

			userStatus.set(UserStatus.FINISHED);
			saveReport();
		}
	}

	/**
	 * Resets the web-front-end.
	 */
	public void clear() {
		synchronized (userStatus) {
			if (userStatus.get() == UserStatus.EXPLORING) {
				// DroidMate still running
				throw new IllegalStateException("DroidMate is still running. Please stop it first.");
			}

			// clear user
			inlinerProcess = null;
			droidMateProcess = null;
			apksRootPath = null;
			apksInformation = new ConcurrentHashMap<>();
			consoleOutput.clear();
			userStatus.set(UserStatus.IDLE);
		}
	}
}
