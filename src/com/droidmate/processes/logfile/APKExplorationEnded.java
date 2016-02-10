package com.droidmate.processes.logfile;

/**
 * Logs APK exploration ended
 */
public class APKExplorationEnded extends APKLogFileEvent {

	private final long endTime;

	/**
	 * Creates a new instance of the APKExplorationEnded class
	 * @param time the exploration end time
	 */
	public APKExplorationEnded(long time) {
		if(time < 0) {
			throw new IllegalArgumentException("Time " + time + " must be non negative.");
		}
		
		this.endTime = time;
	}

	/**
	 * Returns the exploration end time.
	 * @return the exploration end time
	 */
	public long getEndTime() {
		return endTime;
	}
	
}
