package com.droidmate.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.droidmate.processes.AAPTInformation;
import com.droidmate.processes.AAPTProcess;

public class DroidMateUser {

	private Path apksRootPath = null;
	private List<APKInformation> apksInformation = new LinkedList<>();

	private final GUISettings settings;

	public DroidMateUser() {
		// get current settings
		this.settings = new GUISettings();
	}

	public synchronized Path getAPKPath() {
		return apksRootPath;
	}

	public synchronized void setAPKPath(Path newPath) throws Exception {
		if (newPath == null) {
			throw new NullPointerException("APK root path was null.");
		}
		if (!(newPath.toFile().exists() && newPath.toFile().isDirectory())) {
			throw new IllegalArgumentException("APK root path must exist and must be a directory.");
		}

		apksInformation.clear();
		apksRootPath = newPath;

		// collect new information
		Path aaptPath = settings.getAaptToolPath();

		if (!Files.exists(aaptPath)) {
			throw new FileNotFoundException("AAPT path not found at: " + aaptPath);
		}
		if (!Files.isDirectory(aaptPath)) {
			throw new FileNotFoundException("AAPT path: " + aaptPath + " is no directory.");
		}

		// aapt path exists, try to load info
		List<AAPTInformation> aaptResult = getAAPTInformation(aaptPath.toFile());

		// create APKInformation out of AAPT information
		List<APKInformation> apksInfos = setUpAPKInformations(aaptResult);
		
		this.apksInformation = apksInfos;
	}

	private List<APKInformation> setUpAPKInformations(List<AAPTInformation> aaptResults) {
		List<APKInformation> apksInfos = new LinkedList<>();
		
		for (AAPTInformation aaptInfo : aaptResults) {
			//create new APKInformation
			APKInformation apkInfo = new APKInformation(aaptInfo);
			apksInfos.add(apkInfo);
		}
		
		return apksInfos;
	}

	private List<AAPTInformation> getAAPTInformation(File aaptPath) throws Exception {
		assert aaptPath != null && aaptPath.exists() && aaptPath.isDirectory();
		
		//create AAPT process
		AAPTProcess aaptHelper = new AAPTProcess(aaptPath);

		List<File> apks = getAPKFilesFromDirectory(apksRootPath);
		
		//load apk aapt information and return it
		List<AAPTInformation> aaptResult = aaptHelper.loadInformation(apks);
		return aaptResult;
	}

	private List<File> getAPKFilesFromDirectory(Path apksPath) {
		assert apksPath != null && apksPath.toFile().exists() && apksPath.toFile().isDirectory();
		
		// Filter apks
		File[] apkFiles = apksPath.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return FilenameUtils.getExtension(name).toLowerCase().equals("apk");
			}
		});

		// There was an IO error, or apksPath is no directory
		if (apkFiles == null) {
			return new LinkedList<>();
		}

		// return all found apks
		return Arrays.asList(apkFiles);
	}

	public List<APKInformation> getAPKS() {
		return apksInformation;
	}

	public GUISettings getSettings() {
		return settings;
	}
}
