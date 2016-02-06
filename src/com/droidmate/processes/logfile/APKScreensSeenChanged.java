package com.droidmate.processes.logfile;

public class APKScreensSeenChanged extends APKLogFileEvent {
	private final String name;
	private final int changeInScreensSeen;

	public APKScreensSeenChanged(String name, int changeInScreensSeen) {
		if(name == null) {
			throw new IllegalArgumentException("APK name must be not null.");
		}
		
		this.name = name;
		this.changeInScreensSeen = changeInScreensSeen;
		
	}

	public String getName() {
		return name;
	}

	public int getChangeInScreensSeen() {
		return changeInScreensSeen;
	}
}
