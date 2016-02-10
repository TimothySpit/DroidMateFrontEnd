package com.droidmate.processes.logfile;

/**
 * Logs APK exploration started
 */
public class APKExplorationStarted extends APKLogFileEvent {

	private final long startTime;

	/**
	 * Creates a new instance of the APKExplorationStarted calss
	 * @param time the starting time
	 */
	public APKExplorationStarted(long time) {
		if(time < 0) {
			throw new IllegalArgumentException("Time " + time + " must be non negative.");
		}
		
		this.startTime = time;
	}

	/**
	 * Returns the ending time
	 * @return the ending time
	 */
	public long getStartTime() {
		return startTime;
	}

}
