package com.droidmate.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import com.droidmate.apk.APKInformation;
import com.droidmate.settings.GUISettings;

public class DroidMateUser {
	
	private Path apkPath = null;	
	private APKInformation[] apks = new APKInformation[0];	
	private boolean explorationStarted = false;
	
	private List<String> droidMateOutput = new LinkedList<>();
	
	public synchronized boolean setAPKPath(Path apkPathToAnalyse) {
		if (apkPathToAnalyse == null) {
			throw new NullPointerException();
		}
		if (!(apkPathToAnalyse.toFile().exists() && apkPathToAnalyse.toFile().isDirectory()))  {
			throw new IllegalArgumentException();
		}
		
		apkPath = apkPathToAnalyse;
		
		try {
			loadAPKInformationForPath();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public synchronized Path getAPKPath() {
		return apkPath;
	}
	
	public synchronized APKInformation[] getAPKS() {
		return apks;
	}
	
	/**
	 * Loads the information for the given apk file
	 * @param apk Apk file
	 * @param id The id
	 * @return APKInformation
	 */
	private APKInformation loadAPKInformation(File apk) {
		String output = getAaptOutput(apk);
		String packageName = getValueFromAaptOutput(output, "name");
		String packageVersionCode = getValueFromAaptOutput(output, "versionCode");
		String packageVersionName = getValueFromAaptOutput(output, "versionName");
		
		Path inlineTempPath = Paths.get((new GUISettings()).getDroidMatePath().toString(), "/projects/apk-inliner/output-apks/");
		
		return new APKInformation(apk, inlineTempPath, packageName, packageVersionCode, packageVersionName);
	}
	
	/**
	 * Like loadAPKInformation, but works concurrent
	 * @param apkFiles
	 * @return
	 */
	private APKInformation[] loadAPKInformationConcurrent(File[] apkFiles) {
		APKInformation[] newApks = new APKInformation[apkFiles.length];
		int counter = 0;
		Thread[] threads = new Thread[apkFiles.length];
		
		for (File apk : apkFiles) {
			final int id = counter;
			threads[counter] = new Thread(new Runnable() {
				@Override
				public void run() {
					newApks[id] = loadAPKInformation(apk);
				}				
			});
			threads[counter].start();
			counter++;
		}
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return newApks;
	}
	
	/**
	 * 
	 * @param apkFiles
	 * @return
	 */
	private APKInformation[] loadAPKInformation(File[] apkFiles) {
		APKInformation[] newApks = new APKInformation[apkFiles.length];
		int counter = 0;
		
		for(File apk : apkFiles) {
			newApks[counter] = loadAPKInformation(apk);
			counter++;
		}
		
		return newApks;
	}

	/**
	 * Loads the information for the apks in the saved path and saves them in local variable apks
	 */
	private void loadAPKInformationForPath() {
		File[] apkFiles = apkPath.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return name.toLowerCase().endsWith(".apk");
			}
		});
		
		if(apkFiles.length > 3) {
			apks = loadAPKInformationConcurrent(apkFiles);
		}else {
			apks = loadAPKInformation(apkFiles);
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

	public List<String> getDroidMateOutput() {
		return droidMateOutput;
	}

	public void setDroidMateOutput(List<String> droidMateOutput) {
		this.droidMateOutput = droidMateOutput;
	}
	
}
