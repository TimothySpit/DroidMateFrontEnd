package com.droidmate.processes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.droidmate.interfaces.Observable;
import com.droidmate.interfaces.Observer;
import com.droidmate.user.APKInformation;
import com.droidmate.user.InliningStatus;

/**
 * Class responsible for inlining the apks.
 */
public class InlinerProcess {

	private final File outputPath;
	private final File inlinerPath;
	private boolean printStackTrace = false;

	/**
	 * Creates a new instance of the InlinerProcess class
	 * 
	 * @param inlinerPath
	 *            the path to the inliner
	 * @param outputPath
	 *            the path to be outputted to
	 * @throws FileNotFoundException
	 *             if one of the paths does not exist
	 */
	public InlinerProcess(File inlinerPath, File outputPath) throws FileNotFoundException {
		if (inlinerPath == null) {
			throw new IllegalArgumentException("Inline path must not be null");
		}
		if (!inlinerPath.exists()) {
			throw new FileNotFoundException("Inliner path " + inlinerPath + " does not exist.");
		}
		if (!inlinerPath.isDirectory()) {
			throw new IllegalArgumentException("Inline path must be an directory.");
		}
		if (outputPath == null) {
			throw new IllegalArgumentException("Output path must not be null");
		}
		if (!outputPath.exists()) {
			throw new FileNotFoundException("Output path " + inlinerPath + " does not exist.");
		}
		if (!outputPath.isDirectory()) {
			throw new IllegalArgumentException("Output path must be an directory.");
		}

		this.inlinerPath = inlinerPath;
		this.outputPath = outputPath;
	}

	/**
	 * Creates a new instance of the InlinerProcess class
	 * 
	 * @param inlinerPath
	 *            the path to the inliner
	 * @param outputPath
	 *            the path to be outputted to
	 * @param printStackTrace
	 *            boolean indicating whether the stack trace should be printed
	 * @throws FileNotFoundException
	 *             if one of the paths does not exist
	 */
	public InlinerProcess(File inlinerPath, File outputPath, boolean printStackTrace) throws FileNotFoundException {
		this(inlinerPath, outputPath);

		this.setPrintStackTrace(printStackTrace);
	}

	/**
	 * Inlines the given apks.
	 * 
	 * @param apks
	 *            the apks to be inlined
	 * @return true if the inlining was successful
	 * @throws IOException
	 *             if an IO error occured
	 */
	public boolean inlineAPKS(List<APKInformation> apks) throws IOException {
		if (apks == null) {
			throw new IllegalArgumentException("APKS list must not be null.");
		}
		if (apks.size() == 0) {
			throw new IllegalArgumentException("No apks specified for inlining.");
		}

		// create Process arguments and start DroidMate inliner tool
		List<String> arguments = new LinkedList<>();
		arguments.add(inlinerPath.toString() + "/gradlew.bat"); // only support
		// windows here
		if (printStackTrace) {
			arguments.add("--stacktrace");
		}
		arguments.add(":projects:core:prepareInlinedApks");

		return tryStartInlineAPKS(apks, arguments);
	}

	/**
	 * Inlines the given apks with the given arguments
	 * 
	 * @param apksToInline
	 *            the apks to be onlined
	 * @param arguments
	 *            the arguments to be used
	 * @return true if the inlining was successful
	 * @throws IOException
	 *             if an IO error occured
	 */
	private boolean tryStartInlineAPKS(List<APKInformation> apksToInline, List<String> arguments) throws IOException {
		assert apksToInline != null && arguments != null;

		// get all needed droidmate paths and check for correctness
		File inlinerDir = new File(this.inlinerPath, "/projects/apk-inliner/");
		File inlinerInputAPKSPath = new File(inlinerDir, "/input-apks/");
		File inlinerOutputAPKSPath = new File(inlinerDir, "/output-apks/");

		// check all directories
		checkInlinerDirectories(inlinerDir, inlinerInputAPKSPath, inlinerOutputAPKSPath);

		// check if inlined folder exists in output path, if not, create it
		File inlinedOutputPath = createInlinedOutputFolder();

		// all paths seem valid. Clear inliner input/output directory and output
		// directory
		clearAPKSFromDirectory(inlinerInputAPKSPath);
		clearAPKSFromDirectory(inlinerOutputAPKSPath);

		// copy all apks to inliner input path
		for (APKInformation apk : apksToInline) {
			File currentAPKFile = apk.getAPKFile();
			Path destinationFile = Paths.get(inlinerInputAPKSPath.toString(), apk.getAPKFile().getName());
			Files.copy(currentAPKFile.toPath(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
		}

		// check each apk�s status
		for (APKInformation apk : apksToInline) {
			if (apk.getInliningStatus() == InliningStatus.INLINING) {
				// apk is still inlining
				throw new IllegalStateException("APK " + apk.getAPKName() + " is still inlining.");
			}
		}

		try {
			// register Observer for apk folder changes
			DirectoryWatcher watcher = registerWatchOutputDirectory(apksToInline, inlinerOutputAPKSPath, inlinedOutputPath);

			// set apk status to inlining
			for (APKInformation apk : apksToInline) {
				apk.setInliningStatus(InliningStatus.INLINING);
			}

			// start the inliner
			if (!startInliner(watcher, arguments)) {
				// reset status of apks
				resetAllAPKs(apksToInline);
				return false;
			} else {
				return true;
			}
		} catch (InterruptedException e) {
			resetAllAPKs(apksToInline);
			return false;
		} catch (Exception e) {
			resetAllAPKs(apksToInline);
			throw e;
		}
	}

	/**
	 * Resets all given apk's inlining status to not inined
	 * 
	 * @param apks
	 */
	private void resetAllAPKs(List<APKInformation> apks) {
		for (APKInformation apkInformation : apks) {
			apkInformation.setInliningStatus(InliningStatus.NOT_INLINED);
		}
	}

	/**
	 * Strats the inlining process.
	 * 
	 * @param watcher
	 *            the watcher for the changed directories,
	 * @param arguments
	 *            the arguments to be used
	 * @return true if the starting was successful
	 * @throws IOException
	 *             if an IO error occured
	 * @throws InterruptedException
	 *             threadstuff
	 */
	private boolean startInliner(DirectoryWatcher watcher, List<String> arguments) throws IOException, InterruptedException {
		// create inliner process
		ProcessWrapper pbd = new ProcessWrapper(inlinerPath, arguments);

		// watch directory and start the inliner
		watcher.processEvents();
		pbd.start();

		watcher.joinIfNoDataAvailable();
		if (pbd.getExitValue() != 0) {
			// there was an intern error
			return false;
		}
		return true;
	}

	/**
	 * Creates a folder where all inlined apk are moved into.
	 * 
	 * @return the created folder as a file
	 */
	private File createInlinedOutputFolder() {
		File inlinedOutputPath = new File(outputPath, "/inlined/");
		if (!inlinedOutputPath.exists()) {
			// create it
			inlinedOutputPath.mkdir();
		}
		return inlinedOutputPath;
	}

	/**
	 * Registers a DirectoryWatcher for the given apks
	 * 
	 * @param apks
	 *            the apks to be watched
	 * @param inlinerOutputAPKSPath
	 *            the apks output folder
	 * @param inlinedOutputPath
	 *            the output path
	 * @return a directoryWatcher
	 * @throws IOException
	 *             if an IO error occured
	 * @throws InterruptedException
	 *             threadstuff
	 */
	private DirectoryWatcher registerWatchOutputDirectory(List<APKInformation> apks, File inlinerOutputAPKSPath, File inlinedOutputPath)
			throws IOException, InterruptedException {
		DirectoryWatcher inlinedFilesWatcher = new DirectoryWatcher(inlinerOutputAPKSPath.toPath(), false);

		inlinedFilesWatcher.addObserver(new Observer<DirectoryWatcherEvent>() {
			public void update(Observable<DirectoryWatcherEvent> o, DirectoryWatcherEvent event) {
				WatchEvent<?> we = event.getEvent();
				Path filePath = event.getChangedPath();

				// we are only interested in created apk files
				if (we.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
					// a new file was created in the folder, check for .apk file
					if (FilenameUtils.getExtension(filePath.toString()).equals("apk")) {
						// apk found, check corresponding apk
						APKInformation apk = getCorrespondingAPK(apks, filePath);
						if (apk != null) {
							// copy apk to inlined output path
							try {
								Path inlinedAPKName = Paths.get(FilenameUtils.removeExtension(apk.getAPKFile().getName()) + "-inlined.apk");
								Path dst = Paths.get(inlinedOutputPath.getAbsolutePath(), inlinedAPKName.toString());
								Files.copy(filePath.toAbsolutePath(), dst, StandardCopyOption.REPLACE_EXISTING);
							} catch (IOException e) {
								e.printStackTrace();
							}
							// set status to inlined
							apk.setInliningStatus(InliningStatus.INLINED);
						}
					}
				}
			}

			/**
			 * Gets the APKInformation from a list of apks and a path
			 * 
			 * @param apks
			 *            the apks containing the crucial one
			 * @param filePath
			 *            the path t the crucial one
			 * @return the APKInformation for the crucial apk
			 */
			private APKInformation getCorrespondingAPK(List<APKInformation> apks, Path filePath) {
				for (APKInformation apk : apks) {
					// get -inlined postfix
					String filePathString = filePath.toString();
					int indexOfInlinedPostfix = filePathString.lastIndexOf("-inlined.apk");
					if (indexOfInlinedPostfix < 0) {
						// no inlined file
						return null;
					}
					// remove postfix
					filePathString = filePathString.substring(0, indexOfInlinedPostfix) + ".apk";

					if (apk.getAPKFile().getName().equals(FilenameUtils.getName(filePathString))) {
						return apk;
					}
				}
				return null;
			}
		});

		return inlinedFilesWatcher;
	}

	/**
	 * Checks the given directories.
	 * 
	 * @param inlinerDir
	 *            the directory with the inliner
	 * @param inlinerInputAPKSPath
	 *            the directory with the apks
	 * @param inlinerOutputAPKSPath
	 *            the output directory
	 * @throws IOException
	 *             if an IO Error occurs
	 */
	private void checkInlinerDirectories(File inlinerDir, File inlinerInputAPKSPath, File inlinerOutputAPKSPath) throws IOException {
		if (!inlinerInputAPKSPath.exists()) {
			throw new FileNotFoundException("APK inliner input path " + inlinerInputAPKSPath + " does not exist.");
		}
		if (!inlinerInputAPKSPath.isDirectory()) {
			throw new IOException("APK inliner input path " + inlinerInputAPKSPath + " must be a directory.");
		}
		if (!inlinerDir.exists()) {
			throw new FileNotFoundException("APK inliner path " + inlinerDir + " does not exist.");
		}
		if (!inlinerDir.isDirectory()) {
			throw new IOException("APK inliner path " + inlinerDir + " must be a directory.");
		}
		if (!inlinerOutputAPKSPath.exists()) {
			throw new FileNotFoundException("APK inliner output path " + inlinerOutputAPKSPath + " does not exist.");
		}
		if (!inlinerOutputAPKSPath.isDirectory()) {
			throw new IOException("APK inliner output path " + inlinerOutputAPKSPath + " must be a directory.");
		}
	}

	/**
	 * Deletes all apks from the given directory.
	 * 
	 * @param path
	 *            the directory in which all apks should be deleted
	 */
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

	/**
	 * Returns the inliner path.
	 * 
	 * @return the inliner path
	 */
	public File getInlinerPath() {
		return inlinerPath;
	}

	/**
	 * Returns whether the stack trace is printed.
	 * 
	 * @return whether the stack trace is printed
	 */
	public boolean isPrintStackTrace() {
		return printStackTrace;
	}

	/**
	 * Sets whether the stack trace is printed.
	 * 
	 * @param printStackTrace
	 *            boolean indcating whether the stack trace is printed
	 */
	public void setPrintStackTrace(boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
	}

	/**
	 * Returns the output path.
	 * 
	 * @return the output path
	 */
	public File getOutputPath() {
		return outputPath;
	}

}
