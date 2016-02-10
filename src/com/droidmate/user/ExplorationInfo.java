package com.droidmate.user;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *	Class which provides information (real-time) while exploring the apks
 * 	for example the number of seen elements, screens, the explored widgets
 * 	and the start time and end time of the exploration.
 */
public class ExplorationInfo {

	private AtomicLong startingTime = new AtomicLong(0);
	private AtomicLong endTime = new AtomicLong(0);

	private AtomicInteger elementsSeen = new AtomicInteger(0);
	private AtomicInteger screensSeen = new AtomicInteger(0);
	private AtomicInteger widgetsExplored = new AtomicInteger(0);

	private ConcurrentSkipListMap<Long, Integer> elementsSeenHistory;
	private ConcurrentSkipListMap<Long, Integer> screensSeenHistory;
	private ConcurrentSkipListMap<Long, Integer> widgetsExploredHistory;

	/**
	 * Creates an new instance of ExplorationInfo class.
	 */
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

	/**
	 * Sets the stariong time.
	 * 
	 * @param startingTime the new starting time
	 */
	public synchronized void setStartingTime(long startingTime) {
		this.startingTime.set(startingTime);
	}

	/**
	 * Returns the starting time.
	 * 
	 * @return the sterting time.
	 */
	public synchronized long getStartingTime() {
		return startingTime.get();
	}

	/**
	 * Sets the end time.
	 * 
	 * @param endTime the new end time
	 */
	public synchronized void setEndTime(long endTime) {
		this.endTime.set(endTime);
	}

	/**
	 * Returns the end time.
	 * 
	 * @return the end time
	 */
	public synchronized long getEndTime() {
		return endTime.get();
	}

	/**
	 * Returns the elements seen.
	 * 
	 * @return the elements seen
	 */
	public synchronized int getElementsSeen() {
		return elementsSeen.get();
	}

	/**
	 * Returns the screens seen.
	 * 
	 * @return the screens seen
	 */
	public synchronized int getScreensSeen() {
		return screensSeen.get();
	}

	/**
	 * Returns the widgets explored.
	 * 
	 * @return the widgets explored
	 */
	public synchronized int getWidgetsExplored() {
		return widgetsExplored.get();
	}

	/**
	 * Sets the number of explored elements.
	 * 
	 * @param newExplored the new number of explored elements
	 */
	public synchronized void addElementsExplored(int newExplored) {
		widgetsExplored.addAndGet(newExplored);
		widgetsExploredHistory.put(System.currentTimeMillis() - getStartingTime(), getWidgetsExplored());
	}

	/**
	 * Sets the number of seen elements.
	 * 
	 * @param newElements the new number of elements seen
	 */
	public synchronized void addElementsSeen(int newElements) {
		elementsSeen.addAndGet(newElements);
		elementsSeenHistory.put(System.currentTimeMillis() - getStartingTime(), getElementsSeen());
	}

	/**
	 * Sets the number of screens seen.
	 * 
	 * @param newScreens the new number of screens seen
	 */
	public synchronized void addScreensSeen(int newScreens) {
		screensSeen.addAndGet(newScreens);
		screensSeenHistory.put(System.currentTimeMillis() - getStartingTime(), getScreensSeen());
	}

	/**
	 * Returns the history of elements seen.
	 * 
	 * @return the history of elements seen
	 */
	public ConcurrentSkipListMap<Long, Integer> getElementsSeenHistory() {
		return elementsSeenHistory;
	}

	/**
	 * Returns the history of screens seen.
	 * 
	 * @return the history of screens seen
	 */
	public synchronized ConcurrentSkipListMap<Long, Integer> getScreensSeenHistory() {
		return screensSeenHistory;
	}
	

	/**
	 * Returns the history of widgets explored.
	 * 
	 * @return the history of widgets explored
	 */
	public synchronized ConcurrentSkipListMap<Long, Integer> getWidgetsExploredHistory() {
		return widgetsExploredHistory;
	}

	/**
	 * Creates a JSON object out of this classes attributes.
	 * 
	 * @return a JSON object out of this classes attributes
	 */
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

		synchronized (endTime) {
			synchronized (startingTime) {
				if (startingTime.get() != 0 && endTime.get() != 0) {
					json.put("timeSeconds", (endTime.get() - startingTime.get()) / 1000);
				} else if(startingTime.get() != 0){
					long endTime = System.currentTimeMillis();
					json.put("timeSeconds", (endTime - startingTime.get()) / 1000);
				} else {
					json.put("timeSeconds",0);
				}
			}
		}

		return json;
	}
}