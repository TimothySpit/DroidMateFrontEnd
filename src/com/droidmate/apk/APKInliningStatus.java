package com.droidmate.apk;

public enum APKInliningStatus {
	UNKNOWN("UNKNOWN"), NOT_INLINED("NOT_INLINED"), INLINING("INLINING"), INLINED("INLINED");

	private final String name;

	APKInliningStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
