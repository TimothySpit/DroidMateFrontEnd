package com.droidmate.interfaces;

import com.droidmate.processes.ProcessWrapper;

/**
 *	Interface for classes which observe a process stream.
 */
public interface ProcessStreamObserver {

	public void update(ProcessStreamObservable o, ProcessWrapper.ProcessStreamEvent arg);
	
}
