package com.droidmate.processes.logfile;

public class APKExplorationEnded extends APKLogFileEvent {

	private final long endTime;

	public APKExplorationEnded(long time) {
		if(time < 0) {
			throw new IllegalArgumentException("Time " + time + " must be non negative.");
		}
		
		this.endTime = time;
	}

	public long getEndTime() {
		return endTime;
	}
	
}
