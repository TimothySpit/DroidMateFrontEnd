package com.droidmate.processes.logfile;

public class APKElementsExploredChanged extends APKLogFileEvent {

	private final String name;
	private final int changeInElementsExplored;

	public APKElementsExploredChanged(String name, int changeInElementsExplored) {
		if(name == null) {
			throw new IllegalArgumentException("APK name must be not null.");
		}
		
		this.name = name;
		this.changeInElementsExplored = changeInElementsExplored;
		
	}

	public String getName() {
		return name;
	}

	public int getChangeInElementsExplored() {
		return changeInElementsExplored;
	}

}
