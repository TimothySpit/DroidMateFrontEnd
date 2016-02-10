package com.droidmate.interfaces;

import java.util.ArrayList;
import java.util.List;

import com.droidmate.processes.ProcessWrapper;

/**
 * Class which enables another classes (process stream) to be observed
 */
public class ProcessStreamObservable {

	private final List<ProcessStreamObserver> observers = new ArrayList<ProcessStreamObserver>();

	/**
	 * Registers an observer at this observable.
	 * 
	 * @param observer
	 *            the Observer to be added
	 */
	public void addStreamObserver(ProcessStreamObserver observer) {
		if (!observers.contains(observer))
			observers.add(observer);
	}

	/**
	 * Removes an observer from this observable
	 * 
	 * @param observer
	 *            the observer to be removed
	 */
	public void deleteStreamObserver(ProcessStreamObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Notifies all observers that a change happened
	 * 
	 * @param arg
	 *            the change which happend
	 */
	public void notifyStreamObservers(ProcessWrapper.ProcessStreamEvent arg) {
		for (ProcessStreamObserver observer : observers)
			observer.update(this, arg);
	}

}
