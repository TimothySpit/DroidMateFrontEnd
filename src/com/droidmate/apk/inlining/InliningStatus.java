package com.droidmate.apk.inlining;

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
