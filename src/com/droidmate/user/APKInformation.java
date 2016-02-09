package com.droidmate.user;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import com.droidmate.processes.AAPTInformation;

/**
 * This class provides all information about all .apks (name, package name,
 * version code, etc...) and provides the inlining status of them.
 */
public class APKInformation {

	/** The aapt information. */
	private AAPTInformation aaptInfo;
	/** The exploration info. */
	private ExplorationInfo explorationInfo = new ExplorationInfo();

	private AtomicBoolean isSelected = new AtomicBoolean(false);

	/** The gui and InlinerProcess may access the status concurrently. */
	private final AtomicReference<InliningStatus> inliningStatusReference = new AtomicReference<>(InliningStatus.NOT_INLINED);

	private final AtomicReference<ExplorationStatus> explorationStatusReference = new AtomicReference<>(ExplorationStatus.NOT_RUNNING);

	
	/**
	 * @param aaptInfo The AAPTInformation
	 */
	public APKInformation(AAPTInformation aaptInfo) {
		if (aaptInfo == null) {
			throw new NullPointerException();
		}

		this.aaptInfo = aaptInfo;
		inliningStatusReference.set(InliningStatus.NOT_INLINED);
	}

	/**
	 * @return JSONObject with all apk's information
	 */
	public JSONObject toJSONObject() {
		JSONObject result = new JSONObject();
		result.put("name", getAPKName());
		result.put("packageName", getAPKPackageName());
		result.put("packageVersionCode", getAPKPackageVersionCode());
		result.put("packageVersionName", getAPKPackageVersionName());
		result.put("activityName", getAPKActivityName());
		result.put("sizeByte", getAPKFileSizeInBytes());
		result.put("sizeReadable", FileUtils.byteCountToDisplaySize(getAPKFile().length()));
		result.put("inlineStatus", inliningStatusReference.get().getName());
		result.put("explorationStatus", explorationStatusReference.get().getName());
		result.put("explorationInfo", getExplorationInfo().toJSONObject());
		result.put("isSelected", isSelected.get());
		
		return result;
	}

	public ExplorationInfo getExplorationInfo() {
		return explorationInfo;
	}

	/**
	 * Gets inlining status for a .apk file
	 * 
	 * @return Inlining status
	 */
	public InliningStatus getInliningStatus() {
		return inliningStatusReference.get();
	}

	/**
	 * Sets inlining status for the last .apk.
	 * 
	 * @param inliningStatus The inling status
	 */
	public void setInliningStatus(InliningStatus inliningStatus) {
		inliningStatusReference.set(inliningStatus);
	}

	/**
	 * Gets the apk's name.
	 * 
	 * @return The apk's name
	 */
	public String getAPKName() {
		return aaptInfo.getAPKName();
	}

	/**
	 * Gets the apk's file.
	 * 
	 * @return The apk's file
	 */
	public File getAPKFile() {
		return aaptInfo.getAPKFile();
	}

	/**
	 * Gets the apk's package name.
	 * 
	 * @return The apk's package name
	 */
	public String getAPKPackageName() {
		return aaptInfo.getAPKPackageName();
	}

	/**
	 * Gets the apk's package version code.
	 * 
	 * @return The apk's package version code
	 */
	public String getAPKPackageVersionCode() {
		return aaptInfo.getAPKPackageVersionCode();
	}

	/**
	 * Gets the apk's package version name.
	 * 
	 * @return The apk's package version name
	 */
	public String getAPKPackageVersionName() {
		return aaptInfo.getAPKPackageVersionName();
	}

	/**
	 * Gets the apk's activity name.
	 * 
	 * @return The apk's activity name
	 */
	public String getAPKActivityName() {
		return aaptInfo.getAPKActivityName();
	}

	/**
	 * Gets the apk's file size in bytes.
	 * 
	 * @return The apk's file size in bytes
	 */
	public long getAPKFileSizeInBytes() {
		return aaptInfo.getAPKFileSizeInBytes();
	}

	@Override
	public String toString() {
		return "APKInformation [APK-Name: " + getAPKName() + ", " + "Path: " + getAPKFile().getAbsolutePath() + "]";
	}

	/**
	 * Return whether the last .apk is selected.
	 * @return true if the last .apk is selected
	 */
	public boolean isAPKSelected() {
		return isSelected.get();
	}

	/**
	 * Sets whether the last .apk is selected.
	 * @param isSelected the status the last apk should be set to
	 */
	public void setAPKSelected(boolean isSelected) {
		if (inliningStatusReference.get() != InliningStatus.INLINED) {
			throw new IllegalStateException("APK is not yet inlined and cannot be selected.");
		}
		if(explorationStatusReference.get() != ExplorationStatus.NOT_RUNNING) {
			throw new IllegalStateException("APK is not in State NOT_RUNNING, so it cannot be selected for exploration.");
		}
		
		this.isSelected.set(isSelected);
	}

	/**
	 * Returns the exploration status.
	 * @return the exploration status
	 */
	public ExplorationStatus getExplorationStatus() {
		return explorationStatusReference.get();
	}
	
	/**
	 * Sets the exploration status
	 * @param explorationStatus the new exploration status
	 */
	public void setExplorationStatus(ExplorationStatus explorationStatus) {
		if (inliningStatusReference.get() == InliningStatus.INLINING && explorationStatus != ExplorationStatus.NOT_RUNNING) {
			throw new IllegalStateException("APK is still inlining. Exploration state cannot be set to " + explorationStatus.getName());
		}
		if (inliningStatusReference.get() != InliningStatus.INLINED && explorationStatus != ExplorationStatus.NOT_RUNNING) {
			throw new IllegalStateException("APK is not inlined. Exploration state cannot be set to " + explorationStatus.getName());
		}
		explorationStatusReference.set(explorationStatus);
	}
}