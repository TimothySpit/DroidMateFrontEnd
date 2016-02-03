package com.droidmate.user;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * Class representing the users settings for the web front-end.
 * The eploration timeout, the output folder, the DroidMate path
 * and the AAPT-Tool path are saved here.
 */
public class GUISettings
{
	//default values
	private static final int DEFAULT_EXPLORATION_TIMEOUT = 10;
	private static final String DEFAULT_OUTPUT_PATH = (new File("")).getAbsolutePath();
	private static final String DEFAULT_DROIDMATE_PATH = (new File("")).getAbsolutePath();
	private static final String DEFAULT_AAPT_TOOL_PATH = (new File("")).getAbsolutePath();

	private Preferences prefs;

	//current values
	private int explorationTimeout;
	private Path outputFolder;
	private Path droidMatePath;
	private Path aaptToolPath;

	/**
	 * Creates a new instance of the GUISettings class
	 */
	public GUISettings() {
		this.prefs = Preferences.userNodeForPackage(GUISettings.class);
	}

	/**
	 * Returns the current exploration timeout.
	 * @return the current exploration timeout
	 */
	public int getExplorationTimeout()
	{
		explorationTimeout = prefs.getInt("ExplorationTimeout", DEFAULT_EXPLORATION_TIMEOUT);
		if (explorationTimeout <= 0)
		{
			setExplorationTimeout(DEFAULT_EXPLORATION_TIMEOUT);
		}
		return explorationTimeout;
	}

	/**
	 * Sets the exploration timeout to the provided value.
	 * @param newTimeout the new timeout to be used
	 * @throws IllegalArgumentException if timeout is less zero.
	 */
	public void setExplorationTimeout(int newTimeout)
	{
		if (newTimeout <= 0) {
			throw new IllegalArgumentException("Timeout must be greater zero.");
		}
		this.explorationTimeout = newTimeout;
		try {
			prefs.putInt("ExplorationTimeout", explorationTimeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the path to the current output folder.
	 * @return the path to the current output folder.
	 */
	public Path getOutputFolder()
	{
		String pathString = prefs.get("OutputFolderPath", DEFAULT_OUTPUT_PATH);
		try {
			outputFolder = (Paths.get(pathString));
		} catch (InvalidPathException e) {
			setOutputFolder(Paths.get(DEFAULT_DROIDMATE_PATH));
		}
		return outputFolder;
	}

	/**
	 * Sets the output folder to the provided value.
	 * @param outputFolder the new output folder to be used
	 * @throws IllegalArgumentException if the path is null
	 */
	public void setOutputFolder(Path outputFolder)
	{
		if (outputFolder == null) {
			throw new IllegalArgumentException("Path must not be null.");
		}
		this.outputFolder = outputFolder;
		try {
			prefs.put("OutputFolderPath", outputFolder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the current DroidMate path.
	 * @return the current DroidMate path.
	 */
	public Path getDroidMatePath()
	{
		String dmPath = prefs.get("DroidMatePath", DEFAULT_DROIDMATE_PATH);
		try {
			droidMatePath = (Paths.get(dmPath));
		} catch (InvalidPathException e) {
			setDroidMatePath(Paths.get(DEFAULT_DROIDMATE_PATH));
		}
		
		return droidMatePath;
	}

	/**
	 * Sets the DroidMate path to the provided value.
	 * @param outputFolder the new DroidMate path to be used.
	 * @throws IllegalArgumentException if the path is null
	 */
	public void setDroidMatePath(Path droidMatePath)
	{
		if (droidMatePath == null) {
			throw new IllegalArgumentException("Path must not be null.");
		}
		this.droidMatePath = droidMatePath;
		try {
			prefs.put("DroidMatePath", droidMatePath.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current AAPT-Tool path.
	 * @return the current AAPT-Tool path.
	 */
	public Path getAaptToolPath()
	{
		String aaptPath = prefs.get("AAPTPath", DEFAULT_AAPT_TOOL_PATH);
		try {
			setAaptToolPath((Paths.get(aaptPath)));
		} catch (InvalidPathException e) {
			setAaptToolPath((Paths.get(DEFAULT_AAPT_TOOL_PATH)));
		}
		return aaptToolPath;
	}

	/**
	 * Sets the AAPT-Tool path to the provided value.
	 * @param outputFolder the new AAPT-Tool path to be used.
	 * @throws IllegalArgumentException if the path is null
	 */
	public void setAaptToolPath(Path aaptToolPath)
	{
		if (aaptToolPath == null) {
			throw new IllegalArgumentException("Path must not be null.");
		}
		this.aaptToolPath = aaptToolPath;
		try {
			prefs.put("AAPTPath", aaptToolPath.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}