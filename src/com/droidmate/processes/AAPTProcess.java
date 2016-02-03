package com.droidmate.processes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class AAPTProcess {

	private final File aaptFile;

	public AAPTProcess(File aaptPath) throws FileNotFoundException {
		if (aaptPath == null) {
			throw new NullPointerException();
		}
		if (!aaptPath.exists()) {
			throw new FileNotFoundException();
		}

		this.aaptFile = aaptPath;
	}

	public List<AAPTInformation> loadInformation(List<File> apks) throws Exception {
		// create Process
		List<String> arguments = new LinkedList<>();
		arguments.add("aapt");
		arguments.add("d");
		arguments.add("badging");

		return collectAAPTInformation(apks, arguments);
	}

	private List<AAPTInformation> collectAAPTInformation(List<File> apks, List<String> arguments) throws Exception {
		List<AAPTInformation> result = new LinkedList<>();
		for (File apk : apks) {
			arguments.add(apk.getAbsolutePath());
			// start process and collect data
			ProcessWrapper pbd = new ProcessWrapper(aaptFile, arguments);
			pbd.start();
			if (pbd.getExitValue() != 0) {
				// there was an intern error
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
}
