package com.droidmate.user;

/**
 * Enum for specifying the .apk's exploring status. The exploration can be
 * running(EXPLORING), not running (IDLE), starting DroidMate (STARTING),
 * finished(FINISHED) there can be an error(ERROR) or the inlining process is
 * running(INLINING).
 */
public enum UserStatus {
	IDLE("IDLE"), INLINING("INLINING"), STARTING("STARTING"), EXPLORING("EXPLORING"), FINISHED("FINISHED"), ERROR("ERROR");

	private final String name;

	UserStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
