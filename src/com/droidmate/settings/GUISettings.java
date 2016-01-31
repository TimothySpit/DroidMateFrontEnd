package com.droidmate.settings;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class GUISettings {

	private Preferences prefs;

	private Path outputFolder;
	private Path droidMatePath;
	private Path aaptToolPath;
	
	private int explorationTimeout;

	public GUISettings() {
		try {
			this.prefs = Preferences.userNodeForPackage(GUISettings.class);

			File currentDirFile = new File("");
			String pathString = prefs.get("OutputFolderPath", currentDirFile.getAbsolutePath());
			String dmPath = prefs.get("DroidMatePath", currentDirFile.getAbsolutePath());
			String aaptPath = prefs.get("AAPTPath", currentDirFile.getAbsolutePath());

			try {
				outputFolder = (Paths.get(pathString));
			} catch (InvalidPathException e) {
				outputFolder = (Paths.get(currentDirFile.getAbsolutePath()));
				prefs.put("OutputFolderPath", currentDirFile.getAbsolutePath());
			}

			try {
				droidMatePath = (Paths.get(dmPath));
			} catch (InvalidPathException e) {
				droidMatePath = (Paths.get(currentDirFile.getAbsolutePath()));
				prefs.put("DroidMatePath", currentDirFile.getAbsolutePath());
			}

			try {
				setAaptToolPath((Paths.get(aaptPath)));
			} catch (InvalidPathException e) {
				setAaptToolPath((Paths.get(currentDirFile.getAbsolutePath())));
				prefs.put("AAPTPath", currentDirFile.getAbsolutePath());
			}
			
			explorationTimeout = prefs.getInt("ExplorationTimeout", 10);
			if (explorationTimeout <= 0) {
				explorationTimeout = 10;
				prefs.putInt("ExplorationTimeout", explorationTimeout);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getExplorationTimeout() {
		return explorationTimeout;
	}

	public void setExplorationTimeout(int newTimeout) {
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

	public Path getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(Path outputFolder) {
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

	public Path getDroidMatePath() {
		return droidMatePath;
	}

	public void setDroidMatePath(Path droidMatePath) {
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

	public Path getAaptToolPath() {
		return aaptToolPath;
	}

	public void setAaptToolPath(Path aaptToolPath) {
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
