package com.droidmate.processes.logfile;

public class APKEnded extends APKLogFileEvent {

	private final String name;
	private final long endTime;
	private final boolean success;

	public APKEnded(String name, long time, boolean success) {
		if(name == null) {
			throw new IllegalArgumentException("APK name must be not null.");
		}
		if(time < 0) {
			throw new IllegalArgumentException("Time " + time + " must be non negative.");
		}
		
		this.name = name;
		this.endTime = time;
		this.success = success;
		
	}

	public String getName() {
		return name;
	}

	public long getEndTime() {
		return endTime;
	}

	public boolean isSuccess() {
		return success;
	}
	
}
