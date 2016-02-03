package com.droidmate.user;

import java.io.File;
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

	/** The gui and InlinerProcess may access the status concurrently. */
	private final AtomicReference<InliningStatus> inliningStatusReference = new AtomicReference<>();

	/**
	 * @param aaptInfo
	 *            The AAPTInformation
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
		
		return result;
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
	 * Sets inlining status for
	 * 
	 * @param inliningStatus
	 *            The inling status
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
}