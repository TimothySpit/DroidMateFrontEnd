package com.droidmate.processes;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Event for the DirectoryWatcher
 */
public class DirectoryWatcherEvent {

	/**	An event or a repeated event for an object that is registered with a WatchService	*/
	private final WatchEvent<?> event;
	
	/**	The changed path	*/
	private final Path changedPath;

	/**
	 * Creates a new instance of the DirectoryWatcherEvent class
	 * 
	 * @param event
	 *            the happened WatchEvent
	 * @param changedPath
	 *            the changed path
	 */
	public DirectoryWatcherEvent(WatchEvent<?> event, Path changedPath) {
		if (event == null) {
			throw new IllegalArgumentException("Event must not be null.");
		}
		if (changedPath == null) {
			throw new IllegalArgumentException("Path must not be null.");
		}

		this.event = event;
		this.changedPath = changedPath;
	}

	/**
	 * Returns the happened event.
	 * 
	 * @return the happened event
	 */
	public WatchEvent<?> getEvent() {
		return event;
	}

	/**
	 * Returns the changed path.
	 * 
	 * @return the changed path
	 */
	public Path getChangedPath() {
		return changedPath;
	}

}
