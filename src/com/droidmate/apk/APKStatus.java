package com.droidmate.apk;

public enum APKStatus {
	UNKNOWN("UNKNOWN"), NOT_RUNNING("NOT_RUNNING"), RUNNING("RUNNING");

	private final String name;

	APKStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
