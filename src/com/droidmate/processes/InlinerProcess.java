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

public class InlinerProcess {

	private final File outputPath;
	private final File inlinerPath;
	private boolean printStackTrace = false;

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

	public InlinerProcess(File inlinerPath, File outputPath, boolean printStackTrace) throws FileNotFoundException {
		this(inlinerPath, outputPath);

		this.setPrintStackTrace(printStackTrace);
	}

	public boolean inlineAPKS(List<APKInformation> apks) throws IOException {
		if (apks == null) {
			throw new IllegalArgumentException("APKS list must not be null.");
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

	private boolean tryStartInlineAPKS(List<APKInformation> apks, List<String> arguments) throws IOException {
		assert apks != null && arguments != null;

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
		for (APKInformation apk : apks) {
			File currentAPKFile = apk.getAPKFile();
			Path destinationFile = Paths.get(inlinerInputAPKSPath.toString(), apk.getAPKFile().getName());
			Files.copy(currentAPKFile.toPath(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
		}

		try {
			// register Observer for apk folder changes
			DirectoryWatcher watcher = registerWatchOutputDirectory(apks, inlinerOutputAPKSPath, inlinedOutputPath);

			// set apk status to inlining
			for (APKInformation apk : apks) {
				apk.setInliningStatus(InliningStatus.INLINING);
			}

			// start the inliner
			if (!startInliner(watcher, arguments)) {
				// reset status of apks
				resetAllAPKs(apks);
				return false;
			} else {
				return true;
			}
		} catch (InterruptedException e) {
			return false;
		} catch (Exception e) {
			throw e;
		} finally {
			resetAllAPKs(apks);
		}
	}

	private void resetAllAPKs(List<APKInformation> apks) {
		for (APKInformation apkInformation : apks) {
			apkInformation.setInliningStatus(InliningStatus.NOT_INLINED);
		}
	}

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

	private File createInlinedOutputFolder() {
		File inlinedOutputPath = new File(outputPath, "/inlined/");
		if (!inlinedOutputPath.exists()) {
			// create it
			inlinedOutputPath.mkdir();
		}
		return inlinedOutputPath;
	}

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

	private void checkInlinerDirectories(File inlinerDir, File inlinerInputAPKSPath, File inlinerOutputAPKSPath) throws IOException {
		if (!inlinerInputAPKSPath.exists()) {
			throw new IOException("APK inliner input path " + inlinerInputAPKSPath + " does not exist.");
		}
		if (!inlinerInputAPKSPath.isDirectory()) {
			throw new IOException("APK inliner input path " + inlinerInputAPKSPath + " must be a directory.");
		}
		if (!inlinerDir.exists()) {
			throw new IOException("APK inliner path " + inlinerDir + " does not exist.");
		}
		if (!inlinerDir.isDirectory()) {
			throw new IOException("APK inliner path " + inlinerDir + " must be a directory.");
		}
		if (!inlinerOutputAPKSPath.exists()) {
			throw new IOException("APK inliner output path " + inlinerOutputAPKSPath + " does not exist.");
		}
		if (!inlinerOutputAPKSPath.isDirectory()) {
			throw new IOException("APK inliner output path " + inlinerOutputAPKSPath + " must be a directory.");
		}
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

	public File getInlinerPath() {
		return inlinerPath;
	}

	public boolean isPrintStackTrace() {
		return printStackTrace;
	}

	public void setPrintStackTrace(boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
	}

	public File getOutputPath() {
		return outputPath;
	}

}
