package com.droidmate.apk;

import java.io.File;

public class APKInformation {

	private final File file;
	private int progress = 0;
	private APKExplorationStatus status = APKExplorationStatus.NOT_RUNNING;

	private boolean selected = false;
	
	public APKInformation(File apk) {
		this.file = apk;
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

}
