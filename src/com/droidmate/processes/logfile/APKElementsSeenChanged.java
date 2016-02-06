package com.droidmate.processes.logfile;

public class APKElementsSeenChanged extends APKLogFileEvent {
	private final String name;
	private final int changeInElementsSeen;

	public APKElementsSeenChanged(String name, int changeInElementsSeen) {
		if(name == null) {
			throw new IllegalArgumentException("APK name must be not null.");
		}
		
		this.name = name;
		this.changeInElementsSeen = changeInElementsSeen;
		
	}

	public String getName() {
		return name;
	}

	public int getChangeInElementsSeen() {
		return changeInElementsSeen;
	}
}
