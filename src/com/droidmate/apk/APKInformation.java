package com.droidmate.apk;

import java.io.File;

public class APKInformation {

	private final File file;
	private int progress = 0;
	private APKExplorationStatus status = APKExplorationStatus.NOT_RUNNING;

	private boolean selected = true;
	
	public APKInformation(File apk) {
		this.file = apk;
	}

	public File getFile() {
		return file;
	}

	public APKExplorationStatus getStatus() {
		return status;
	}

	public void setStatus(APKExplorationStatus status) {
		this.status = status;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
