package com.droidmate.hook;

import java.io.File;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONArray;
import org.json.JSONObject;


public class APKExplorationInfo {
	
	private final String name;
	private AtomicBoolean success = new AtomicBoolean(false);
	private AtomicInteger elementsSeen = new AtomicInteger(0);
	private AtomicInteger screensSeen = new AtomicInteger(0);
	private AtomicBoolean finished = new AtomicBoolean(false);
	private AtomicLong startingTime = new AtomicLong();
	private ConcurrentSkipListMap<Long, Integer> elementsSeenHistory;
	private ConcurrentSkipListMap<Long, Integer> screensSeenHistory;
	private File reportFile;
	
	public APKExplorationInfo(String name) {
		super();
		this.name = name;
		startingTime.set(System.currentTimeMillis());
		
		Comparator<Long> c = new Comparator<Long>() {
			@Override
			public int compare(Long arg0, Long arg1) {
				return arg0.compareTo(arg1);
			}			
		};
		elementsSeenHistory = new ConcurrentSkipListMap<>(c);
		screensSeenHistory = new ConcurrentSkipListMap<>(c);
		elementsSeenHistory.put(0l, 0);
		screensSeenHistory.put(0l, 0);
	}
	
	public long getStartingTime() {
		return startingTime.get();
	}

	public int getElementsSeen() {
		return elementsSeen.get();
	}

	public int getScreensSeen() {
		return screensSeen.get();
	}
	
	public void addElementsSeen(int newElements) {
		elementsSeen.addAndGet(newElements);
		elementsSeenHistory.put(System.currentTimeMillis() - getStartingTime(), getElementsSeen());
	}
	
	public void addScreensSeen(int newScreens) {
		screensSeen.addAndGet(newScreens);
		screensSeenHistory.put(System.currentTimeMillis() - getStartingTime(), getScreensSeen());
	}

	public ConcurrentSkipListMap<Long, Integer> getElementsSeenHistory() {
		return elementsSeenHistory;
	}

	public ConcurrentSkipListMap<Long, Integer> getScreensSeenHistory() {
		return screensSeenHistory;
	}

	public void setElementsSeen(int elementsSeen) {
		this.elementsSeen.set(elementsSeen);
	}

	public void setScreensSeen(int screensSeen) {
		this.screensSeen.set(screensSeen);
	}
	
	public boolean isSuccess() {
		return success.get();
	}

	public void setSuccess(boolean success) {
		this.success.set(success);
	}

	public String getName() {
		return name;
	}
	
	public void setFinished(boolean finished) {
		this.finished.set(finished);
	}
	
	public boolean isFinished() {
		return finished.get();
	}
	
	public static JSONObject getDummyObject() {
		JSONObject json = new JSONObject();
		json.put("history", new JSONArray());
		json.put("historyScreens", new JSONArray());
		
		return json;
	}
	
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("name", getName());
		json.put("success", isSuccess());
		json.put("elementsSeen", getElementsSeen());
		json.put("screensSeen", getScreensSeen());
		json.put("finished", isFinished());
		
		JSONArray elementsHistory = new JSONArray();
		for(Entry<Long, Integer> entry : getElementsSeenHistory().entrySet()) {
			JSONArray o = new JSONArray();
			//Seconds
			o.put(Math.round(Math.round(entry.getKey() / 1000d)));
			o.put(entry.getValue());
			elementsHistory.put(o);
		}
		json.put("history", elementsHistory);
		
		JSONArray screensHistory = new JSONArray();
		for(Entry<Long, Integer> entry : getScreensSeenHistory().entrySet()) {
			JSONArray o = new JSONArray();
			//Seconds
			o.put(Math.round(Math.round(entry.getKey() / 1000d)));
			o.put(entry.getValue());
			screensHistory.put(o);
		}
		json.put("historyScreens", screensHistory);
		
		return json;
	}

	public void setReportFile(File reportFile) {
		this.reportFile = reportFile;
	}
	
	public File getReportFile() {
		return reportFile;
	}
}