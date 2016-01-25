package com.droidmate.apk;

import java.io.File;

public class AAPTInformation {

	private final File apk;

	private final String packageName;
	private final String packageVersionCode;
	private final String packageVersionName;

	public AAPTInformation(File apk, String packageName, String packageVersionCode, String packageVersionName) {
		if (apk == null) {
			throw new NullPointerException();
		}

		this.apk = apk;
		this.packageName = packageName;
		this.packageVersionCode = packageVersionCode;
		this.packageVersionName = packageVersionName;
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

}
