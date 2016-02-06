package com.droidmate.processes;

public class DroidMateProcessEvent {

	public enum EventType {
		STARTED, FINISHED, ERROR 
	}

	private final EventType event;
	
	public DroidMateProcessEvent(EventType event) {
		this.event = event;
	}

	public EventType getEvent() {
		return event;
	}
	
}
