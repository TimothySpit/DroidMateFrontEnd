package com.droidmate.hook;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class APKExplorationInfo {
	
	private final String name;
	private AtomicBoolean success = new AtomicBoolean(false);
	private AtomicInteger elementsSeen = new AtomicInteger(0);
	
	public APKExplorationInfo(String name) {
		super();
		this.name = name;
	}

	public int getElementsSeen() {
		return elementsSeen.get();
	}
	
	public void addElementsSeen(int newElements) {
		elementsSeen.addAndGet(newElements);
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
}