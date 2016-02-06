package com.droidmate.processes.logfile;

public class APKStarted extends APKLogFileEvent {

	private final String name;
	private final long startTime;

	public APKStarted(String name, long time) {
		if(name == null) {
			throw new IllegalArgumentException("APK name must be not null.");
		}
		if(time < 0) {
			throw new IllegalArgumentException("Time " + time + " must be non negative.");
		}
		
		this.name = name;
		this.startTime = time;
	}

	public String getName() {
		return name;
	}

	public long getStartTime() {
		return startTime;
	}
	
}
