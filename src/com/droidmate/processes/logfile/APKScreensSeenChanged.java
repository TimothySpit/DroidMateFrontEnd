package com.droidmate.processes.logfile;

/**
 * Logs APK screens changed
 */
public class APKScreensSeenChanged extends APKLogFileEvent {
	private final String name;
	private final int changeInScreensSeen;

	/**
	 * Creates a new instance of the APKScreensSeen class
	 * @param name the name
	 * @param changeInScreensSeen the change in the screens seen
	 */
	public APKScreensSeenChanged(String name, int changeInScreensSeen) {
		if(name == null) {
			throw new IllegalArgumentException("APK name must be not null.");
		}
		
		this.name = name;
		this.changeInScreensSeen = changeInScreensSeen;
		
	}

	/**
	 * Returns the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the change in the screens seen.
	 * @return the change in the screens seen
	 */
	public int getChangeInScreensSeen() {
		return changeInScreensSeen;
	}
}
