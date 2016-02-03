package com.droidmate.user;

public enum InliningStatus {

    NOT_INLINED("NOT_INLINED"), INLINED("INLINED"), INLINING("INLINING");

    private final String name;

    InliningStatus(String name) {
    	this.name = name;
    }

    public String getName() {
    	return name;
    }
}