package com.droidmate.interfaces;

import java.util.ArrayList;
import java.util.List;

import com.droidmate.processes.logfile.APKElementsExploredChanged;
import com.droidmate.processes.logfile.APKElementsSeenChanged;
import com.droidmate.processes.logfile.APKEnded;
import com.droidmate.processes.logfile.APKExplorationEnded;
import com.droidmate.processes.logfile.APKExplorationStarted;
import com.droidmate.processes.logfile.APKScreensSeenChanged;
import com.droidmate.processes.logfile.APKStarted;

/**
 *	Class which enables another classes (apks log files) to be observed
 */
public class APKLogFileObservable {
	
	private final List<APKLogFileObserver> observers = new ArrayList<APKLogFileObserver>();

	/**
	 * Registers an observer at this observable.
	 * 
	 * @param observer the Observer to be added
	 */
	public void addObserver(APKLogFileObserver observer) {
		if (!observers.contains(observer))
			observers.add(observer);
	}

	/**
	 * Removes an observer from this observable
	 * 
	 * @param observer the observer to be removed
	 */
	public void deleteObserver(APKLogFileObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Notifies all observers that an exploration started
	 * 
	 * @param arg the exploration that started
	 */
	public void notifyObservers(APKExplorationStarted arg) {
		for (APKLogFileObserver observer : observers)
			observer.update(this, arg);
	}
	
	/**
	 * Notifies all observers that an exploration ended
	 * 
	 * @param arg the exploration that started
	 */
	public void notifyObservers(APKExplorationEnded arg) {
		for (APKLogFileObserver observer : observers)
			observer.update(this, arg);
	}
	
	/**
	 * Notifies all observers that an exploration started
	 * 
	 * @param arg the exploration that started
	 */
	public void notifyObservers(APKStarted arg) {
		for (APKLogFileObserver observer : observers)
			observer.update(this, arg);
	}
	/**
	 * Notifies all observers that an exploration ended
	 * 
	 * @param arg the exploration that ended
	 */
	public void notifyObservers(APKEnded arg) {
		for (APKLogFileObserver observer : observers)
			observer.update(this, arg);
	}
	
	/**
	 * Notifies all observers that the elements seen have changed
	 * 
	 * @param arg the elements seen change
	 */
	public void notifyObservers(APKElementsSeenChanged arg) {
		for (APKLogFileObserver observer : observers)
			observer.update(this, arg);
	}
	
	/**
	 * Notifies all observers that the elements explored have changed
	 * 
	 * @param arg the elements explored change
	 */
	public void notifyObservers(APKElementsExploredChanged arg) {
		for (APKLogFileObserver observer : observers)
			observer.update(this, arg);
	}
	
	/**
	 * Notifies all observers that the screens seen have changed
	 * 
	 * @param arg the screens seen change
	 */
	public void notifyObservers(APKScreensSeenChanged arg) {
		for (APKLogFileObserver observer : observers)
			observer.update(this, arg);
	}
	
}
