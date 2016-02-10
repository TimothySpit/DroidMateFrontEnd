package com.droidmate.processes.logfile;

/**
 * Logging APK elements seen changed
 */
public class APKElementsSeenChanged extends APKLogFileEvent {
	private final String name;
	private final int changeInElementsSeen;

	/**
	 * Creates a new instance of the APKElementsSeenChanged class.
	 * @param name the name
	 * @param changeInElementsSeen the change in the elements seen
	 */
	public APKElementsSeenChanged(String name, int changeInElementsSeen) {
		if(name == null) {
			throw new IllegalArgumentException("APK name must be not null.");
		}
		
		this.name = name;
		this.changeInElementsSeen = changeInElementsSeen;
		
	}

	/**
	 * Returns the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the change in the elements seen.
	 * @return the change in the elements seen
	 */
	public int getChangeInElementsSeen() {
		return changeInElementsSeen;
	}
}
