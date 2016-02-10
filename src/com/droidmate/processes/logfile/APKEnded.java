package com.droidmate.processes.logfile;

/**
 * Logs APK ended
 */
public class APKEnded extends APKLogFileEvent {

	private final String name;
	private final long endTime;
	private final boolean success;

	/**
	 * Creates a new instance of the APKEnded class
	 * @param name the name
	 * @param time the ending time
	 * @param success boolean indicating the success
	 */
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

	/**
	 * Returns the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the end time.
	 * @return the end time
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * Returns whether this apk ended successfully.
	 * @return whether this apk ended successfully
	 */
	public boolean isSuccess() {
		return success;
	}
	
}
