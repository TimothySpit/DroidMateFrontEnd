package com.droidmate.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class ProcessWrapper {
	
	private ProcessBuilder processBuilder;
	private StringWriter infoWriter = new StringWriter();
	private StringWriter errorWriter = new StringWriter();
	private int exitValue;

	public ProcessWrapper(File directory, List<String> command) throws FileNotFoundException  {
		if(directory == null || command == null) {
			throw new IllegalArgumentException("Arguments must not be null.");
		}
		if(!directory.exists()) {
			throw new FileNotFoundException("The directory file does not exist.");
		}
		if(!directory.isDirectory()) {
			throw new IllegalArgumentException("Argument 'directory' must refer to an existing directory.");
		}
		
		processBuilder = new ProcessBuilder(command);
		processBuilder.directory(directory);
	}
	
	public void start() throws InterruptedException, IOException {
		Process process = processBuilder.start();
		StreamBoozer seInfo = new StreamBoozer(process.getInputStream(), new PrintWriter(infoWriter, true));
		StreamBoozer seError = new StreamBoozer(process.getErrorStream(), new PrintWriter(errorWriter, true));
		seInfo.start();
		seError.start();
		exitValue = process.waitFor();
		seInfo.join();
		seError.join();
	}

	public String getErrors() {
		return errorWriter.toString();
	}

	public String getInfos() {
		return infoWriter.toString();
	}

	public int getExitValue() {
		return exitValue;
	}

	private class StreamBoozer extends Thread {
		private InputStream in;
		private PrintWriter pw;

		StreamBoozer(InputStream in, PrintWriter pw) {
			this.in = in;
			this.pw = pw;
		}

		@Override
		public void run() {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = br.readLine()) != null) {
					pw.println(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}