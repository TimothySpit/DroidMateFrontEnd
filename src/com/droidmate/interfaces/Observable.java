package com.droidmate.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which enables another classes to be observed.
 *
 * @param <T>
 */
public class Observable<T> {

	/** List of observers */
	private final List<Observer<T>> observers = new ArrayList<Observer<T>>();

	/**
	 * Registers an observer at this observable.
	 * 
	 * @param observer
	 *            the Observer to be added
	 */
	public synchronized void addObserver(Observer<T> observer) {
		if (!observers.contains(observer))
			observers.add(observer);
	}

	/**
	 * Removes an observer from this observable
	 * 
	 * @param observer
	 *            the observer to be removed
	 */
	public synchronized void deleteObserver(Observer<?> observer) {
		observers.remove(observer);
	}

	/**
	 * Notifies all observers that a change happened
	 * 
	 * @param arg
	 *            the change which happend
	 */
	public synchronized void notifyObservers(T arg) {
		for (Observer<T> observer : observers)
			observer.update(this, arg);
	}
}
