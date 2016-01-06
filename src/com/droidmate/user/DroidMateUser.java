package com.droidmate.user;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import com.droidmate.apk.APKInformation;

public class DroidMateUser {
	
	private Path apkPath = null;
	
	private List<APKInformation> apks = new LinkedList<>();
	
	private boolean apksInlined = false;
	
	private boolean explorationStated = false;
	
	public void setAPKPath(Path apkPathToAnalyse) {
		if (apkPathToAnalyse == null) {
			throw new NullPointerException();
		}
		if (!(apkPathToAnalyse.toFile().exists() && apkPathToAnalyse.toFile().isDirectory()))  {
			throw new IllegalArgumentException();
		}
		
		apkPath = apkPathToAnalyse;
		
		loadAPKInformationForPath();
	}
	
	public Path getAPKPath() {
		return apkPath;
	}
	
	public List<APKInformation> getAPKS() {
		return apks;
	}

	private void loadAPKInformationForPath() {
		File[] apkFiles = apkPath.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return name.toLowerCase().endsWith(".apk");
			}
		});
		
		apks.clear();
		
		for (File apk : apkFiles) {
			apks.add(new APKInformation(apk));
		}
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
	
	public boolean isApksInlined() {
		return apksInlined;
	}

	public void setApksInlined(boolean apksInlined) {
		this.apksInlined = apksInlined;
	}

	public boolean isExplorationStated() {
		return explorationStated;
	}

	public void setExplorationStated(boolean explorationStated) {
		this.explorationStated = explorationStated;
	}
	
}
