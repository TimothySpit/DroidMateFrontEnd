package com.droidmate.apk.inlining;

/**
 * Enum for possible inlining status.
 * An .apk inlining can be not started, running (inlining),
 * have an error or be finished.
 */
public enum InliningStatus {

	NOT_STARTED("NOT_STARTED"),
	INLINING("INLINING"),
	ERROR("ERROR"),
	FINISHED("FINISHED");
	
	private final String name;

	InliningStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
