package com.droidmate.processes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.droidmate.user.APKInformation;

public class InlinerProcess {

	private final File inlinerPath;

	public InlinerProcess(File inlinerPath) throws FileNotFoundException {
		if(inlinerPath == null) {
			throw new IllegalArgumentException("Inline path must not be null");
		}
		if(!inlinerPath.exists()) {
			throw new FileNotFoundException("Inliner path " + inlinerPath + " does not exist.");
		}
		if(!inlinerPath.isDirectory()) {
			throw new IllegalArgumentException("Inline path must be an directory.");
		}
		
		this.inlinerPath = inlinerPath;
	}

	public void inlineAPKS(List<APKInformation> apks) {
		if(apks == null) {
			throw new IllegalArgumentException("APKS list must not be null.");
		}
		
		
	}
	
	public File getInlinerPath() {
		return inlinerPath;
	}
	
}
