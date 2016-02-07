package com.droidmate.interfaces;

import com.droidmate.processes.ProcessWrapper;

public interface ProcessStreamObserver {

	public void update(ProcessStreamObservable o, ProcessWrapper.ProcessStreamEvent arg);
	
}
