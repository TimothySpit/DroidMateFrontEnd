package com.droidmate.user;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import com.droidmate.processes.AAPTInformation;

public class APKInformation {

	private AAPTInformation aaptInfo;

	public APKInformation(AAPTInformation aaptInfo) {
		if (aaptInfo == null) {
			throw new NullPointerException();
		}

		this.aaptInfo = aaptInfo;
	}

	public String getAPKName() {
		return aaptInfo.getAPKName();
	}

	public File getAPKFile() {
		return aaptInfo.getAPKFile();
	}

	public String getAPKPackageName() {
		return aaptInfo.getAPKPackageName();
	}

	public String getAPKPackageVersionCode() {
		return aaptInfo.getAPKPackageVersionCode();
	}

	public String getAPKPackageVersionName() {
		return aaptInfo.getAPKPackageVersionName();
	}

	public String getAPKActivityName() {
		return aaptInfo.getAPKActivityName();
	}

	public long getAPKFileSizeInBytes() {
		return aaptInfo.getAPKFileSizeInBytes();
	}

	public JSONObject toJSONObject() {
		JSONObject result = new JSONObject();
		result.put("name", getAPKName());
		result.put("packageName", getAPKPackageName());
		result.put("packageVersionCode", getAPKPackageVersionCode());
		result.put("packageVersionName", getAPKPackageVersionName());
		result.put("activityName", getAPKActivityName());
		result.put("sizeByte", getAPKFileSizeInBytes());
		result.put("sizeReadable", FileUtils.byteCountToDisplaySize(getAPKFile().length()));
		
		return result;
	}

	@Override
	public String toString() {
		return "APKInformation [APK-Name: " + getAPKName() + ", " + "Path: " + getAPKFile().getAbsolutePath() + "]";
	}

}
