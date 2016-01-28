package com.droidmate.apk;

import java.io.File;

public class AAPTInformation {

	private final File apk;

	private final String packageName;
	private final String packageVersionCode;
	private final String packageVersionName;
	private final String activityName;

	public AAPTInformation(File apk, String packageName, String packageVersionCode, String packageVersionName, String activityName) {
		if (apk == null) {
			throw new NullPointerException();
		}

		this.apk = apk;
		this.packageName = packageName;
		this.packageVersionCode = packageVersionCode;
		this.packageVersionName = packageVersionName;
		this.activityName = activityName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getPackageVersionCode() {
		return packageVersionCode;
	}

	public String getPackageVersionName() {
		return packageVersionName;
	}

	public File getFile() {
		return apk;
	}

	public String getActivityName() {
		return activityName;
	}

}
