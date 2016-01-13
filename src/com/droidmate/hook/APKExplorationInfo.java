package com.droidmate.hook;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;


public class APKExplorationInfo {
	
	private final String name;
	private AtomicBoolean success = new AtomicBoolean(false);
	private AtomicInteger elementsSeen = new AtomicInteger(0);
	private AtomicBoolean finished = new AtomicBoolean(false);
	private ConcurrentHashMap<Long, Integer> elementsSeenHistory = new ConcurrentHashMap<>();
	private File reportFile;
	
	public APKExplorationInfo(String name) {
		super();
		this.name = name;
	}

	public int getElementsSeen() {
		return elementsSeen.get();
	}
	
	public void addElementsSeen(int newElements) {
		elementsSeen.addAndGet(newElements);
		elementsSeenHistory.put(System.currentTimeMillis(), getElementsSeen());
	}

	public ConcurrentHashMap<Long, Integer> getElementsSeenHistory() {
		return elementsSeenHistory;
	}

	public void setElementsSeen(int elementsSeen) {
		this.elementsSeen.set(elementsSeen);
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
		json.put("finished", isFinished());
		
		return json;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("APKExplorationInfo [name=");
		builder.append(name);
		builder.append(", success=");
		builder.append(success);
		builder.append(", elementsSeen=");
		builder.append(elementsSeen);
		builder.append("]");
		return builder.toString();
	}

	public void setReportFile(File reportFile) {
		this.reportFile = reportFile;
	}
	
	public File getReportFile() {
		return reportFile;
	}
}