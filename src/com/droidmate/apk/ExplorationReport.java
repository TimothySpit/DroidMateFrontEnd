package com.droidmate.apk;

public class ExplorationReport {

	private int elementsSeen;
	private boolean success;

	public ExplorationReport(int elementsSeen, boolean success) {
		this.elementsSeen = (elementsSeen);
		this.success = (success);
	}

	public int getElementsSeen() {
		return elementsSeen;
	}

	public boolean isSuccess() {
		return success;
	}

	static ExplorationReport getDefaultReport() {
		return new ExplorationReport(0, false);
	}
}

