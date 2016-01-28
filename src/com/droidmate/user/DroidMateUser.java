package com.droidmate.user;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import com.droidmate.apk.AAPTHelper;
import com.droidmate.apk.AAPTInformation;
import com.droidmate.apk.APKExplorationStatus;
import com.droidmate.apk.APKInformation;
import com.droidmate.settings.GUISettings;

public class DroidMateUser {

	private Path apkPath = null;
	private List<APKInformation> apks = new LinkedList<>();
	private APKExplorationStatus explorationStatus = APKExplorationStatus.UNKNOWN;

	private List<String> droidMateOutput = new LinkedList<>();

	public synchronized boolean setAPKPath(Path apkPathToAnalyse) {
		if (apkPathToAnalyse == null) {
			throw new NullPointerException();
		}
		if (!(apkPathToAnalyse.toFile().exists() && apkPathToAnalyse.toFile().isDirectory())) {
			throw new IllegalArgumentException();
		}

		apks.clear();
		droidMateOutput.clear();
		
		apkPath = apkPathToAnalyse;

		AAPTHelper aaptHelper;
		try {
			aaptHelper = new AAPTHelper(apkPath);
			List<AAPTInformation> aaptInfo = aaptHelper.loadAPKInformation();

			Path inlineTempPath = Paths.get((new GUISettings()).getDroidMatePath().toString(), "/projects/apk-inliner/output-apks/");

			for (AAPTInformation aaptInformation : aaptInfo) {
				apks.add(new APKInformation(aaptInformation, inlineTempPath));
			}

			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized Path getAPKPath() {
		return apkPath;
	}

	public synchronized List<APKInformation> getAPKS() {
		return apks;
	}

	public int getSelectedAPKSCount() {
		int counter = 0;
		for (APKInformation apk : apks) {
			if (apk.isSelected()) {
				counter++;
			}
		}
		return counter;
	}

	public boolean isExplorationStarted() {
		return explorationStatus.equals(APKExplorationStatus.STARTED);
	}

	public void setStatus(APKExplorationStatus newStatus) {
		this.explorationStatus = newStatus;
	}

	public List<String> getDroidMateOutput() {
		return droidMateOutput;
	}

	public void setDroidMateOutput(List<String> droidMateOutput) {
		this.droidMateOutput = droidMateOutput;
	}

	public void clear() {
		apks.clear();
		droidMateOutput.clear();
		setAPKPath(apkPath);
	}

}
