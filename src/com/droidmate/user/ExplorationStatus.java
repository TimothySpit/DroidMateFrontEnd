package com.droidmate.user;

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
