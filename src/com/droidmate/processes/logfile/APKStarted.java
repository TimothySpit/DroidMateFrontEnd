package com.droidmate.processes.logfile;

/**
 * Logs APK started
 */
public class APKStarted extends APKLogFileEvent {

	private final String name;
	private final long startTime;

	/**
	 * Creates a new instance of the APKStarted class
	 * 
	 * @param name
	 *            the name
	 * @param time
	 *            the starting time
	 */
	public APKStarted(String name, long time) {
		if (name == null) {
			throw new IllegalArgumentException("APK name must be not null.");
		}
		if (time < 0) {
			throw new IllegalArgumentException("Time " + time + " must be non negative.");
		}

		this.name = name;
		this.startTime = time;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the start time.
	 * 
	 * @return the start time
	 */
	public long getStartTime() {
		return startTime;
	}

}
