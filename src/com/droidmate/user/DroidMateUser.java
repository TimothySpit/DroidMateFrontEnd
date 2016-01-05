package com.droidmate.user;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;


import com.droidmate.apk.APKInformation;

public class DroidMateUser {
	
	private Path apkPath;
	
	private List<APKInformation> apks = new LinkedList<>();
	
	public Path getAPKPath() {
		return apkPath;
	}
	
	public List<APKInformation> getAPKS() {
		return apks;
	}
	
	public DroidMateUser(Path apkPathToAnalyse) {
		if (apkPathToAnalyse == null) {
			throw new NullPointerException();
		}
		if (!(apkPathToAnalyse.toFile().exists() && apkPathToAnalyse.toFile().isDirectory()))  {
			throw new IllegalArgumentException();
		}
		
		apkPath = apkPathToAnalyse;
		
		loadAPKInformationForPath();
	}

	private void loadAPKInformationForPath() {
		File[] apkFiles = apkPath.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return name.toLowerCase().endsWith(".apk");
			}
		});
		
		for (File apk : apkFiles) {
			apks.add(new APKInformation(apk));
		}
	}
	
}
