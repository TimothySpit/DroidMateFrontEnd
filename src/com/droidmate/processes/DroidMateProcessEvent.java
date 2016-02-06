package com.droidmate.processes;

public class DroidMateProcessEvent {

	public enum EventType {
		EXPLORATION_STARTED, EXPLORATION_FINISHED, DROIDMATE_ERROR 
	}

	private final EventType event;
	
	public DroidMateProcessEvent(EventType event) {
		this.event = event;
	}

	public EventType getEvent() {
		return event;
	}
	
}
