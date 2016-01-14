package com.droidmate.apk;

public class ExplorationReport {

	private int elementsSeen;
	private boolean success;
	private int screensSeen;

	public ExplorationReport(int elementsSeen, int screensSeen, boolean success) {
		this.elementsSeen = (elementsSeen);
		this.success = (success);
		this.screensSeen = screensSeen;
	}

	public int getElementsSeen() {
		return elementsSeen;
	}

	public boolean isSuccess() {
		return success;
	}

	public int getScreensSeen() {
		return screensSeen;
	}

	static ExplorationReport getDefaultReport() {
		return new ExplorationReport(0, 0, false);
	}
}

