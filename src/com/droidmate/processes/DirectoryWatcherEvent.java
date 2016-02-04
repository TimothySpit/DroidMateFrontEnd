package com.droidmate.processes;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class DirectoryWatcherEvent {

	private final WatchEvent<?> event;
	private final Path changedPath;
	
	public DirectoryWatcherEvent(WatchEvent<?> event, Path changedPath) {
		if(event == null) {
			throw new IllegalArgumentException("Event must not be null.");
		}
		if(changedPath == null) {
			throw new IllegalArgumentException("Path must not be null.");
		}
		
		this.event = event;
		this.changedPath = changedPath;
	}

	public WatchEvent<?> getEvent() {
		return event;
	}

	public Path getChangedPath() {
		return changedPath;
	}
	
}
