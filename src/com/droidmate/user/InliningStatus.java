package com.droidmate.user;

/**
 * Enum for specifying the .apk's inlining status.
 * An .apk can be inlined (INLINED), not inlined (NOT_INLINED)
 * or it can be in the process of inlining.
 */

public enum InliningStatus
{
    NOT_INLINED("NOT_INLINED"),
    INLINED("INLINED"),
    INLINING("INLINING");

    private final String name;

    InliningStatus(String name) {
    	this.name = name;
    }

    public String getName() {
    	return name;
    }
}