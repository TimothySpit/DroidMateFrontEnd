package com.droidmate.apk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class AAPTHelper {

	private final static int CONCURRENT_LOADING_MINIMUM = 3;

	private Path apkPath;
	private Path aaptPath;

	public AAPTHelper(Path apkPath, Path aaptPath) throws FileNotFoundException {
		if (apkPath == null) {
			throw new NullPointerException();
		}
		if (!apkPath.toFile().exists()) {
			throw new FileNotFoundException();
		}
		if (aaptPath == null) {
			throw new NullPointerException();
		}
		if (!aaptPath.toFile().exists()) {
			throw new FileNotFoundException();
		}
		
		this.apkPath = apkPath;
		this.aaptPath = aaptPath;
	}

	/**
	 * 
	 * @param apkFiles
	 * @return
	 */
	public List<AAPTInformation> loadAPKInformation() {

		if (!isAAPTAvailable())
			return new LinkedList<>();

		List<AAPTInformation> apkInformation = new LinkedList<>();

		try {
			apkInformation = loadAPKInformationForPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apkInformation;
	}

	public boolean isAAPTAvailable() {
		ProcessBuilder pb = new ProcessBuilder(aaptPath.toString() + "/aapt");
		pb.redirectErrorStream(false);
		try {
			pb.start();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	private List<AAPTInformation> loadAPKInformationForPath() {
		File[] apkFiles = apkPath.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return FilenameUtils.getExtension(name).toLowerCase().equals("apk");
			}
		});

		if (apkFiles.length > CONCURRENT_LOADING_MINIMUM) {
			return loadAPKInformationConcurrent(apkFiles);
		} else {
			return loadAPKInformationSequential(apkFiles);
		}
	}

	/**
	 * Loads the information for the given apk file
	 * 
	 * @param apk
	 *            Apk file
	 * @param id
	 *            The id
	 * @return APKInformation
	 * @throws FileNotFoundException
	 */
	private AAPTInformation loadAPKInformation(File apk) {
		String output = getAaptOutput(apk);
		String packageName = getValueFromAaptOutput(output, "name");
		String packageVersionCode = getValueFromAaptOutput(output, "versionCode");
		String packageVersionName = getValueFromAaptOutput(output, "versionName");
		String activityName = getValueFromAaptOutput(output, "launchable-activity: name");

		return new AAPTInformation(apk, packageName, packageVersionCode, packageVersionName, activityName);
	}

	/**
	 * Like loadAPKInformation, but works concurrent
	 * 
	 * @param apkFiles
	 * @return
	 */
	private List<AAPTInformation> loadAPKInformationConcurrent(File[] apkFiles) {
		List<AAPTInformation> newApks = new LinkedList<>();
		List<Thread> threads = new LinkedList<>();

		for (File apk : apkFiles) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					newApks.add(loadAPKInformation(apk));
				}
			});
			t.start();
			threads.add(t);
		}
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return newApks;
	}

	private List<AAPTInformation> loadAPKInformationSequential(File[] apkFiles) {
		List<AAPTInformation> newApks = new LinkedList<>();

		for (File apk : apkFiles) {
			newApks.add(loadAPKInformation(apk));
		}

		return newApks;
	}

	private String getAaptOutput(File apk) {
		ProcessBuilder pb = new ProcessBuilder(aaptPath.toString()+ "/aapt", "d", "badging", apk.getAbsolutePath());
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
			return "";
		}
	}

	private String getValueFromAaptOutput(String output, String value) {
		int index = output.indexOf(value + "='") + value.length() + 2;
		if (index == -1)
			return "";

		return output.substring(index, output.indexOf("'", index + 1));
	}
}