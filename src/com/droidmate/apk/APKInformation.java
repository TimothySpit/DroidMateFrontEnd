package com.droidmate.apk;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;

public class APKInformation {

	private final int id;
	private final File file;
	private int progress = 0;
	private APKExplorationStatus status = APKExplorationStatus.NOT_RUNNING;
	private String packageName, versionCode, versionName;

	public int getId() {
		return id;
	}

	private boolean selected = false;

	public APKInformation(int id, File file, String packageName, String versionCode, String versionName) {
		super();
		this.id = id;
		this.file = file;
		this.packageName = packageName;
		this.versionCode = versionCode;
		this.versionName = versionName;
	}

	public synchronized File getFile() {
		return file;
	}

	public synchronized APKExplorationStatus getStatus() {
		return status;
	}

	public synchronized void setStatus(APKExplorationStatus status) {
		this.status = status;
	}

	public synchronized int getProgress() {
		return progress;
	}

	public synchronized void setProgress(int progress) {
		this.progress = progress;
	}

	public synchronized boolean isSelected() {
		return selected;
	}

	public synchronized void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public JSONArray toJSONArray() {
		JSONArray array = new JSONArray();
		array.put(getId());
		array.put(getFile().getName());
		array.put(FileUtils.byteCountToDisplaySize(getFile().length()));
		array.put(getPackageName());
		array.put(getVersionName() + " (#" + getVersionCode() + ")");
		
		return array;
	}

}
