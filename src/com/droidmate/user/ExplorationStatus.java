package com.droidmate.user;

/**
 * Enum for specifying the .apk's exploring status. The exploration can be not
 * running (NOT_RUNNING), exploring (EXPLORING), success (SUCCESS), there can be
 * an error(ERROR) or the process can be aborded (ABORTED).
 */
public enum ExplorationStatus {
	NOT_RUNNING("NOT_RUNNING"), EXPLORING("EXPLORING"), SUCCESS("SUCCESS"), ABORTED("ABORTED"), ERROR("ERROR");

	private final String name;

	ExplorationStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
