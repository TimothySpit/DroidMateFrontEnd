package com.droidmate.interfaces;

import com.droidmate.processes.logfile.APKElementsExploredChanged;
import com.droidmate.processes.logfile.APKElementsSeenChanged;
import com.droidmate.processes.logfile.APKEnded;
import com.droidmate.processes.logfile.APKScreensSeenChanged;
import com.droidmate.processes.logfile.APKStarted;

public interface APKLogFileObserver {
	
	
	public void update(APKLogFileObservable o, APKStarted arg);
	public void update(APKLogFileObservable o, APKEnded arg);
	
	public void update(APKLogFileObservable o, APKElementsExploredChanged arg);
	public void update(APKLogFileObservable o, APKElementsSeenChanged arg);
	public void update(APKLogFileObservable o, APKScreensSeenChanged arg);
	
}
