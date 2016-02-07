package com.droidmate.user;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExplorationInfo {

	private AtomicLong startingTime = new AtomicLong(0);
	private AtomicLong endTime = new AtomicLong(0);
	
	private AtomicInteger elementsSeen = new AtomicInteger(0);
	private AtomicInteger screensSeen = new AtomicInteger(0);
	private AtomicInteger widgetsExplored = new AtomicInteger(0);

	private ConcurrentSkipListMap<Long, Integer> elementsSeenHistory;
	private ConcurrentSkipListMap<Long, Integer> screensSeenHistory;
	private ConcurrentSkipListMap<Long, Integer> widgetsExploredHistory;

	public ExplorationInfo() {

		Comparator<Long> comparator = new Comparator<Long>() {
			@Override
			public int compare(Long arg0, Long arg1) {
				return arg0.compareTo(arg1);
			}
		};
		elementsSeenHistory = new ConcurrentSkipListMap<>(comparator);
		screensSeenHistory = new ConcurrentSkipListMap<>(comparator);
		widgetsExploredHistory = new ConcurrentSkipListMap<>(comparator);
		elementsSeenHistory.put(0l, 0);
		screensSeenHistory.put(0l, 0);
		widgetsExploredHistory.put(0l, 0);
	}

	
	public synchronized void setStartingTime(long startingTime) {
		this.startingTime.set(startingTime);
	}
	public synchronized long getStartingTime() {
		return startingTime.get();
	}

	public synchronized void setEndTime(long endTime) {
		this.endTime.set(endTime);
	}
	public synchronized long getEndTime() {
		return endTime.get();
	}
	
	public synchronized int getElementsSeen() {
		return elementsSeen.get();
	}

	public synchronized int getScreensSeen() {
		return screensSeen.get();
	}

	public synchronized int getWidgetsExplored() {
		return widgetsExplored.get();
	}

	public synchronized void addElementsExplored(int newExplored) {
		widgetsExplored.addAndGet(newExplored);
		widgetsExploredHistory.put(System.currentTimeMillis() - getStartingTime(), getWidgetsExplored());
	}

	public synchronized void addElementsSeen(int newElements) {
		elementsSeen.addAndGet(newElements);
		elementsSeenHistory.put(System.currentTimeMillis() - getStartingTime(), getElementsSeen());
	}

	public synchronized void addScreensSeen(int newScreens) {
		screensSeen.addAndGet(newScreens);
		screensSeenHistory.put(System.currentTimeMillis() - getStartingTime(), getScreensSeen());
	}

	public ConcurrentSkipListMap<Long, Integer> getElementsSeenHistory() {
		return elementsSeenHistory;
	}

	public synchronized ConcurrentSkipListMap<Long, Integer> getScreensSeenHistory() {
		return screensSeenHistory;
	}

	public synchronized JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("elementsSeen", getElementsSeen());
		json.put("screensSeen", getScreensSeen());
		json.put("widgetsExplored", getWidgetsExplored());

		JSONArray elementsHistory = new JSONArray();
		for (Entry<Long, Integer> entry : getElementsSeenHistory().entrySet()) {
			JSONArray o = new JSONArray();
			// Seconds
			o.put(Math.round(Math.round(entry.getKey() / 1000d)));
			o.put(entry.getValue());
			elementsHistory.put(o);
		}
		json.put("historyElements", elementsHistory);

		JSONArray screensHistory = new JSONArray();
		for (Entry<Long, Integer> entry : getScreensSeenHistory().entrySet()) {
			JSONArray o = new JSONArray();
			// Seconds
			o.put(Math.round(Math.round(entry.getKey() / 1000d)));
			o.put(entry.getValue());
			screensHistory.put(o);
		}
		json.put("historyScreens", screensHistory);

		JSONArray widgetsHistory = new JSONArray();
		for (Entry<Long, Integer> entry : getWidgetsExploredHistory().entrySet()) {
			JSONArray o = new JSONArray();
			// Seconds
			o.put(Math.round(Math.round(entry.getKey() / 1000d)));
			o.put(entry.getValue());
			widgetsHistory.put(o);
		}
		json.put("historyWidgets", widgetsHistory);

		long timeMilli = endTime.get() - startingTime.get();
		json.put("time", timeMilli > 0 ? timeMilli : 0);
		
		return json;
	}

	public synchronized ConcurrentSkipListMap<Long, Integer> getWidgetsExploredHistory() {
		return widgetsExploredHistory;
	}

}