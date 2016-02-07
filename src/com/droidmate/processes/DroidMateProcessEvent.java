package com.droidmate.processes;

public class DroidMateProcessEvent {

	public enum EventType {
		EXPLORATION_STARTED, EXPLORATION_FINISHED, DROIDMATE_ERROR, CONSOLE_OUTPUT_STDOUT, CONSOLE_OUTPUT_ERROR 
	}

	private final EventType event;
	private final String message;
	
	public DroidMateProcessEvent(EventType event) {
		this.event = event;
		this.message = "";
	}
	
	public DroidMateProcessEvent(EventType event, String message) {
		this.event = event;
		this.message = message;
	}

	public EventType getEvent() {
		return event;
	}

	public String getMessage() {
		return message;
	}
	
}
