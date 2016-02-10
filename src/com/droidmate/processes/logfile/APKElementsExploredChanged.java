package com.droidmate.processes.logfile;

/**
 * Logging APK elements explored changed.
 */
public class APKElementsExploredChanged extends APKLogFileEvent {

	private final String name;
	private final int changeInElementsExplored;

	/**
	 * Creates an ew instance of the APKElementsExploredChanged class.
	 * 
	 * @param name
	 *            the name
	 * @param changeInElementsExplored
	 *            the change in the apks explored
	 */
	public APKElementsExploredChanged(String name, int changeInElementsExplored) {
		if (name == null) {
			throw new IllegalArgumentException("APK name must be not null.");
		}

		this.name = name;
		this.changeInElementsExplored = changeInElementsExplored;

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
	 * Returns the change in the elements explored.
	 * 
	 * @return the change in the elements explored
	 */
	public int getChangeInElementsExplored() {
		return changeInElementsExplored;
	}

}
