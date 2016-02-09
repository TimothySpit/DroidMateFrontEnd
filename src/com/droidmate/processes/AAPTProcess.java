package com.droidmate.processes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *	Class which creates a aapt process.
 */
public class AAPTProcess {

	private final File aaptPath;

	/**
	 * Creates a new instance of the AAPTProcess class.
	 * 
	 * @param aaptPath the aapt path.
	 * @throws FileNotFoundException if the aapt path does not exist
	 */
	public AAPTProcess(File aaptPath) throws FileNotFoundException {
		if (aaptPath == null) {
			throw new IllegalArgumentException("AAPT path must not be null.");
		}
		if (!aaptPath.exists()) {
			throw new FileNotFoundException("AAPT path " + aaptPath + " not found.");
		}
		if (!aaptPath.isDirectory()) {
			throw new IllegalArgumentException("AAPT path " + aaptPath + " is no directory.");
		}

		this.aaptPath = aaptPath;
	}

	public List<AAPTInformation> loadInformation(List<File> apks) throws IOException {
		if (apks == null) {
			throw new IllegalArgumentException("APKS list must not be null");
		}

		// create Process arguments and start AAPT tool
		List<String> arguments = new LinkedList<>();
		arguments.add(aaptPath + "/aapt");
		arguments.add("d");
		arguments.add("badging");

		return collectAAPTInformation(apks, arguments);
	}

	private List<AAPTInformation> collectAAPTInformation(List<File> apks, List<String> arguments) throws IOException {
		List<AAPTInformation> result = new LinkedList<>();
		for (File apk : apks) {
			arguments.add(apk.getAbsolutePath());
			// start process and collect data

			ProcessWrapper pbd = new ProcessWrapper(aaptPath, arguments);
			try {
				pbd.start();
			} catch (InterruptedException e) {
				/** do nothing */
			}
			if (pbd.getExitValue() != 0) {
				// there was an intern error
				arguments.remove(arguments.size() - 1);
				continue;
			}
			result.add(collectInformationFromResultingStream(apk, pbd.getInfos()));
			arguments.remove(arguments.size() - 1);
		}
		return result;
	}

	private AAPTInformation collectInformationFromResultingStream(File apk, String infos) throws FileNotFoundException {
		String packageName = getValueFromAaptOutput(infos, "name");
		String packageVersionCode = getValueFromAaptOutput(infos, "versionCode");
		String packageVersionName = getValueFromAaptOutput(infos, "versionName");
		String activityName = getValueFromAaptOutput(infos, "launchable-activity: name");

		return new AAPTInformation(apk, packageName, packageVersionCode, packageVersionName, activityName);
	}

	private String getValueFromAaptOutput(String output, String value) {
		int index = output.indexOf(value + "='") + value.length() + 2;
		if (index == -1)
			return "";

		return output.substring(index, output.indexOf("'", index + 1));
	}

	public File getAAPTPath() {
		return aaptPath;
	}
}
