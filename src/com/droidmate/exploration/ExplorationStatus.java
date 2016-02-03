package com.droidmate.exploration;

/**
 * Enum for possible exploration status.
 * An .apk exploration can be running, not running and be of unknown status.
 */
public enum ExplorationStatus
{
	UNKNOWN("UNKNOWN"),
	NOT_RUNNING("NOT_RUNNING"),
	RUNNING("RUNNING");

	private final String name;

	ExplorationStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
