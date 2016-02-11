package com.droidmate.processes;

/**
 * Class for handling DroidMate process events.
 *
 */
public class DroidMateProcessEvent {

	/**
	 * Enum for possible EventTypes. The names are self-explaining
	 */
	public enum EventType {
		EXPLORATION_STARTED, EXPLORATION_FINISHED, DROIDMATE_ERROR, CONSOLE_OUTPUT_STDOUT, CONSOLE_OUTPUT_STDERR
	}

	/**	The event type for this event	*/
	private final EventType event;
	
	/**	The message of the event*/
	private final String message;

	/**
	 * Creates a new instance of the DroidMateProcessEvent class.
	 * 
	 * @param event
	 *            the event type for this event
	 */
	public DroidMateProcessEvent(EventType event) {
		this.event = event;
		this.message = "";
	}

	/**
	 * Creates a new instance of the DroidMateProcessEvent class.
	 * 
	 * @param event
	 *            the event type for this event
	 * @param message
	 *            the message for this event
	 */
	public DroidMateProcessEvent(EventType event, String message) {
		this.event = event;
		this.message = message;
	}

	/**
	 * Returns the event type of this event.
	 * 
	 * @return the event type of this event
	 */
	public EventType getEvent() {
		return event;
	}

	/**
	 * Returns the message of this event.
	 * 
	 * @return the message of this event
	 */
	public String getMessage() {
		return message;
	}

}
