package com.droidmate.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import com.droidmate.apk.APKInformation;

public class DroidMateUser {
	
	private Path apkPath = null;
	
	private List<APKInformation> apks = new LinkedList<>();
	
	private boolean explorationStarted = false;
	
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
	
	public synchronized Path getAPKPath() {
		return apkPath;
	}
	
	public synchronized List<APKInformation> getAPKS() {
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
		
		int idCounter = 0;
		for (File apk : apkFiles) {
			String output = getAaptOutput(apk);
			String packageName = getValueFromAaptOutput(output, "name");
			String packageVersionCode = getValueFromAaptOutput(output, "versionCode");
			String packageVersionName = getValueFromAaptOutput(output, "versionName");
			apks.add(new APKInformation(idCounter++, apk, packageName, packageVersionCode, packageVersionName));
		}
	}
	
	private String getValueFromAaptOutput(String output, String value) {
		int index = output.indexOf(value + "='") + value.length() + 2;
		return output.substring(index, output.indexOf("'", index + 1));
	}
	
	private String getAaptOutput(File apk) {
		ProcessBuilder pb = new ProcessBuilder("aapt", "d", "badging", apk.getAbsolutePath());
		pb.redirectErrorStream(false);
		try {
			Process p = pb.start();
			StringBuilder output = new StringBuilder();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s;
			while ((s = stdout.readLine()) != null) {
				output.append(s);
			}
			stdout.close();
			p.getOutputStream().close();
			p.getErrorStream().close();
			p.waitFor();
			
			return output.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
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
	
	public boolean isExplorationStarted() {
		return explorationStarted;
	}

	public void setExplorationStarted(boolean explorationStarted) {
		this.explorationStarted = explorationStarted;
	}
	
}
