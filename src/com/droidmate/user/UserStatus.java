package com.droidmate.user;

public enum UserStatus {
	 	IDLE("IDLE"),
	    INLINING("INLINING"),
	    EXPLORING("EXPLORING"),
	    FINISHED("FINISHED"),
	    ERROR("ERROR");

	    private final String name;

	    UserStatus(String name) {
	    	this.name = name;
	    }

	    public String getName() {
	    	return name;
	    }
}
