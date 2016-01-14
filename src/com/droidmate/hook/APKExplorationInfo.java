package com.droidmate.hook;

import java.io.File;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;


public class APKExplorationInfo {
	
	private final String name;
	private AtomicBoolean success = new AtomicBoolean(false);
	private AtomicInteger elementsSeen = new AtomicInteger(0);
	private AtomicInteger screensSeen = new AtomicInteger(0);
	private AtomicBoolean finished = new AtomicBoolean(false);
	private ConcurrentHashMap<Long, Integer> elementsSeenHistory = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, Integer> screensSeenHistory = new ConcurrentHashMap<>();
	private File reportFile;
	
	public APKExplorationInfo(String name) {
		super();
		this.name = name;
		elementsSeenHistory.put(0l, 0);
	}

	public int getElementsSeen() {
		return elementsSeen.get();
	}

	public int getScreensSeen() {
		return screensSeen.get();
	}
	
	public void addElementsSeen(long time, int newElements) {
		elementsSeen.addAndGet(newElements);
		elementsSeenHistory.put(time, getElementsSeen());
	}
	
	public void addScreensSeen(long time, int newScreens) {
		screensSeen.addAndGet(newScreens);
		screensSeenHistory.put(time, getScreensSeen());
	}

	public ConcurrentHashMap<Long, Integer> getElementsSeenHistory() {
		return elementsSeenHistory;
	}

	public ConcurrentHashMap<Long, Integer> getScreensSeenHistory() {
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
	
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("name", getName());
		json.put("success", isSuccess());
		json.put("elementsSeen", getElementsSeen());
		json.put("screensSeen", getScreensSeen());
		json.put("finished", isFinished());
		
		JSONArray elementsHistory = new JSONArray();
		for(Entry<Long, Integer> entry : getElementsSeenHistory().entrySet()) {
			JSONObject o = new JSONObject();
			o.put("time", entry.getKey());
			o.put("elementsSeen", entry.getValue());
			elementsHistory.put(o);
		}
		json.put("history", elementsHistory);
		
		JSONArray screensHistory = new JSONArray();
		for(Entry<Long, Integer> entry : getScreensSeenHistory().entrySet()) {
			JSONObject o = new JSONObject();
			o.put("time", entry.getKey());
			o.put("screensSeen", entry.getValue());
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