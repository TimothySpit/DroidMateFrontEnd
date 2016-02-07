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

import com.droidmate.interfaces.ProcessStreamObservable;

public class ProcessWrapper extends ProcessStreamObservable {

	private ProcessBuilder processBuilder;
	private StringWriter infoWriter = new StringWriter();
	private StringWriter errorWriter = new StringWriter();
	private int exitValue;
	private Process process = null;
	private StreamBoozer seInfo= null;
	private StreamBoozer seError= null;

	public ProcessWrapper(File directory, List<String> command) throws FileNotFoundException {
		if (directory == null || command == null) {
			throw new IllegalArgumentException("Arguments must not be null.");
		}
		if (!directory.exists()) {
			throw new FileNotFoundException("The directory file does not exist.");
		}
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Argument 'directory' must refer to an existing directory.");
		}

		processBuilder = new ProcessBuilder(command);
		processBuilder.directory(directory);
	}

	public void start() throws InterruptedException, IOException {
		process  = processBuilder.start();
		seInfo = new StreamBoozer(process.getInputStream(), new PrintWriter(infoWriter, true),StreamCallbackType.STDOUT);
		seError = new StreamBoozer(process.getErrorStream(), new PrintWriter(errorWriter, true),StreamCallbackType.ERROR);
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

	public enum StreamCallbackType {
		ERROR,
		STDOUT
	}
	
	public class ProcessStreamEvent {
		private final StreamCallbackType type;
		private final String message;

		public ProcessStreamEvent(StreamCallbackType type, String message) {
			this.type = type;
			this.message = message;
		}

		public StreamCallbackType getType() {
			return type;
		}

		public String getMessage() {
			return message;
		}
	}
	
	private class StreamBoozer extends Thread {
		private InputStream in;
		private PrintWriter pw;
		private final StreamCallbackType cbt;

		StreamBoozer(InputStream in, PrintWriter pw, StreamCallbackType callbackType) {
			this.in = in;
			this.pw = pw;
			
			this.cbt = callbackType;
		}

		@Override
		public void run() {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = br.readLine()) != null) {
					pw.println(line);
					notifyStreamObservers(new ProcessStreamEvent(cbt,line));
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

	private void killAdb() {
		System.out.println("Killing adb process...");
		Runtime rt = Runtime.getRuntime();
		try {
			if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
				rt.exec("taskkill /F /IM " + "adb.exe");
			else
				rt.exec("kill -9 " + "adb");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if(process != null) {
			killAdb();
			
			try {
				process.destroyForcibly().waitFor();
				seInfo.join();
				seError.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}