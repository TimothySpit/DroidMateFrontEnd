package com.droidmate.processes;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.io.FilenameUtils;

/**
 *	Class which provides all getable information about an apk (package name,
 *	version code, version name, etc...). 
 */
public class AAPTInformation {

	private final File apkFile;
	private final String packageName;
	private final String packageVersionCode;
	private final String packageVersionName;
	private final String activityName;

	/**
	 * Creates a new instance of the AAPTInformation class.
	 * 
	 * @param apk apk file
	 * @param packageName the apk's package name
	 * @param packageVersionCode the apk's package version code
	 * @param packageVersionName the apk's package version name
	 * @param activityName the apk's activity name
	 * @throws FileNotFoundException
	 */
	public AAPTInformation(File apk, String packageName, String packageVersionCode, String packageVersionName, String activityName)
			throws FileNotFoundException {
		if (apk == null || packageName == null || packageVersionCode == null || packageVersionName == null || activityName == null) {
			throw new IllegalArgumentException("Arguments must be not null.");
		}
		if (!FilenameUtils.getExtension(apk.getName()).equals("apk")) {
			throw new IllegalArgumentException("File 'apk' must be an .apk file.");
		}
		if (!apk.exists()) {
			throw new FileNotFoundException("File " + apk + " does not exist.");
		}

		this.apkFile = apk;
		this.packageName = packageName;
		this.packageVersionCode = packageVersionCode;
		this.packageVersionName = packageVersionName;
		this.activityName = activityName;
	}

	public String getAPKName() {
		return apkFile.getName();
	}

	public String getAPKPackageName() {
		return packageName;
	}

	public String getAPKPackageVersionCode() {
		return packageVersionCode;
	}

	public String getAPKPackageVersionName() {
		return packageVersionName;
	}

	public String getAPKActivityName() {
		return activityName;
	}

	public long getAPKFileSizeInBytes() {
		return apkFile.length();
	}

	public File getAPKFile() {
		return apkFile;
	}

	@Override
	public String toString() {
		return "AAPTInformation [APK=" + getAPKName() + ", packageName=" + getAPKPackageName() + ", packageVersionCode=" + getAPKPackageVersionCode()
				+ ", packageVersionName=" + getAPKPackageVersionName() + ", activityName=" + getAPKActivityName() + "]";
	}

}
