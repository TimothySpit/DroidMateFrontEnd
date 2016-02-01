package com.droidmate.exploration;

public enum ExplorationStatus {
	UNKNOWN("UNKNOWN"), NOT_RUNNING("NOT_RUNNING"), RUNNING("RUNNING");

	private final String name;

	ExplorationStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
