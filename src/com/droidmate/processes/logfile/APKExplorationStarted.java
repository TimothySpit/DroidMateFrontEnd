package com.droidmate.processes.logfile;

public class APKExplorationStarted extends APKLogFileEvent {

	private final long startTime;

	public APKExplorationStarted(long time) {
		if(time < 0) {
			throw new IllegalArgumentException("Time " + time + " must be non negative.");
		}
		
		this.startTime = time;
	}

	public long getStartTime() {
		return startTime;
	}

}
