package com.droidmate.apk;

public enum APKExplorationStatus {
	UNKNOWN("UNKNOWN"), NOT_STARTED("NOT_STARTED"), STARTED("STARTED"), PROGRESSING("PROGRESSING"), ABORTED("ABORTED"), ERROR("ERROR"), FINISHED("FINISHED");

	private final String name;

	APKExplorationStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
