package com.droidmate.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.droidmate.processes.AAPTInformation;
import com.droidmate.processes.AAPTProcess;

/**
 * Sets the path for the .apks to be explored and tracks the exploration status for
 * all .apks.
 */
public class DroidMateUser
{
	/** The given .apks Path. */
	private Path apksRootPath = null;

	/** List of .apks informations from the selected .apks ordner. */
	private List<APKInformation> apksInformation = new LinkedList<>();

	/** Instance of GUISettings */
	private final GUISettings settings;

	/**
	 * Creates a new instance of the DroidMateUser class
	 */
	public DroidMateUser() {
		// get current settings
		this.settings = new GUISettings();
	}

	/**
	 * Gets the .apks path.
	 * @return The .apks path
	 */
	public synchronized Path getAPKPath() {
		return apksRootPath;
	}

	/**
	 * Sets the path for the users given .apks.
	 * @param newPath the path for the apks
	 * @throws NullPointerException if the given path ist null
	 * @throws IllegalArgumentException if the given path not a directory or does not exist
	 * @throws IOException if a IO Error occured
	 */
	public synchronized void setAPKPath(Path newPath) throws IOException
	{
		// exception handling
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

	/**
	 * Sets the .apks information and return a list of them
	 * @param aaptResults
	 * @return List of .apks information
	 */
	private List<APKInformation> setUpAPKInformations(List<AAPTInformation> aaptResults) {
		List<APKInformation> apksInfos = new LinkedList<>();

		for (AAPTInformation aaptInfo : aaptResults) {
			// create new APKInformation
			APKInformation apkInfo = new APKInformation(aaptInfo);
			apksInfos.add(apkInfo);
		}

		return apksInfos;
	}

	/**
	 * Gets .apks aapt informations.
	 * @param aaptPath
	 * @return List of .apks aapt informations
	 * @throws Exception
	 */
	private List<AAPTInformation> getAAPTInformation(File aaptPath) throws IOException {
		assert aaptPath != null && aaptPath.exists() && aaptPath.isDirectory();

		// create AAPT process
		AAPTProcess aaptHelper = new AAPTProcess(aaptPath);

		List<File> apks = getAPKFilesFromDirectory(apksRootPath);

		// load apk aapt information and return it
		List<AAPTInformation> aaptResult = aaptHelper.loadInformation(apks);
		return aaptResult;
	}

	/**
	 * Gets found .apks files from given path's directory.
	 * @param apksPath
	 * @return List of all found .apks in the given directory
	 */
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

	/**
	 * Gets the .apks informations.
	 * @return List of .apks informations
	 */
	public List<APKInformation> getAPKS() {
		return apksInformation;
	}

	/**
	 * Gets GUISettings.
	 * @return The GUISettings
	 */
	public GUISettings getSettings() {
		return settings;
	}
}
