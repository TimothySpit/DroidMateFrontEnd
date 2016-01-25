package com.droidmate.apk;

import java.time.Duration;
import java.time.LocalTime;

public class ExplorationInformation {

	private int elementsSeen;
	private int screensSeen;
	private int widgetsSeen;

	private LocalTime startTime = LocalTime.MIN;
	private LocalTime endTime = LocalTime.MIN;

	private APKExplorationStatus explorationStatus;

	public ExplorationInformation() {

	}

	public int getElementsSeen() {
		return elementsSeen;
	}

	public void setElementsSeen(int elementsSeen) {
		this.elementsSeen = elementsSeen;
	}

	public void addElementsSeen(int value) {
		elementsSeen += value;
	}

	public int getScreensSeen() {
		return screensSeen;
	}

	public void setScreensSeen(int screensSeen) {
		this.screensSeen = screensSeen;
	}

	public void addScreensSeen(int value) {
		screensSeen += value;
	}

	public int getWidgetsSeen() {
		return widgetsSeen;
	}

	public void setWidgetsSeen(int widgetsSeen) {
		this.widgetsSeen = widgetsSeen;
	}

	public void addWidgetsSeen(int value) {
		widgetsSeen += value;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public Duration getRunningDuration() {
		return Duration.between(startTime, endTime);
	}

	public APKExplorationStatus getExplorationStatus() {
		return explorationStatus;
	}

	public void setExplorationStatus(APKExplorationStatus explorationStatus) {
		switch (explorationStatus) {
		case STARTED:
			startTime = LocalTime.now();
			endTime = LocalTime.now();
			break;
		case FINISHED:
		case ABORTED:
		case ERROR:
			endTime = LocalTime.now();
			break;
		default:
			break;
		}

		this.explorationStatus = explorationStatus;
	}
}
