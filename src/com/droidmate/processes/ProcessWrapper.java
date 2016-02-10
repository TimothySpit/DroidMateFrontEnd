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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droidmate.interfaces.ProcessStreamObservable;

/**
 * Wrapper for processes in the web front end.
 *
 */
public class ProcessWrapper extends ProcessStreamObservable {

	private ProcessBuilder processBuilder;
	private StringWriter infoWriter = new StringWriter();
	private StringWriter errorWriter = new StringWriter();
	private int exitValue;
	protected Process process = null;
	protected StreamBoozer seInfo = null;
	protected StreamBoozer seError = null;

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * Creates a new instance of the ProcessWrapper class
	 * 
	 * @param directory the directory
	 * @param command the processes commands
	 * @throws FileNotFoundException
	 */
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

	/**
	 * Starts the process.
	 * @throws InterruptedException threadstuff
	 * @throws IOException if an IO error occured
	 */
	public void start() throws InterruptedException, IOException {
		process = processBuilder.start();
		seInfo = new StreamBoozer(process.getInputStream(), new PrintWriter(infoWriter, true), StreamCallbackType.STDOUT);
		seError = new StreamBoozer(process.getErrorStream(), new PrintWriter(errorWriter, true), StreamCallbackType.ERROR);
		seInfo.start();
		seError.start();
		exitValue = process.waitFor();
		seInfo.join();
		seError.join();
	}

	/**
	 * Returns the errors.
	 * @return the errors
	 */
	public String getErrors() {
		return errorWriter.toString();
	}

	/**
	 * Returns the info writers infos.
	 * @return the info writers infos.
	 */
	public String getInfos() {
		return infoWriter.toString();
	}

	/**
	 * Returns the exit value.
	 * @return the exit value
	 */
	public int getExitValue() {
		return exitValue;
	}

	/**
	 * Enum for callback types
	 *
	 */
	public enum StreamCallbackType {
		ERROR, STDOUT
	}

	public class ProcessStreamEvent {
		private final StreamCallbackType type;
		private final String message;

		/**
		 * Creates a new instance of the ProcessStreamEvent class.
		 * @param type the callback type
		 * @param message the message
		 */
		public ProcessStreamEvent(StreamCallbackType type, String message) {
			this.type = type;
			this.message = message;
		}

		/**
		 * Returns the stream callback type.
		 * @return the stream callback type
		 */
		public StreamCallbackType getType() {
			return type;
		}

		/**
		 * Returns the message.
		 * @return the message.
		 */
		public String getMessage() {
			return message;
		}
	}

	/**
	 * Class for being wrapped and streaming.
	 */
	protected class StreamBoozer extends Thread {
		private InputStream in;
		private PrintWriter pw;
		private final StreamCallbackType cbt;

		private boolean isRunning = false;

		/**
		 * Creates a new instance of the StreamBoozer class.
		 * 
		 * @param in the input stream
		 * @param pw the print writer
		 * @param callbackType the callback type
		 */
		StreamBoozer(InputStream in, PrintWriter pw, StreamCallbackType callbackType) {
			this.in = in;
			this.pw = pw;

			this.cbt = callbackType;
		}
		
		/**
		 * Returns whether the underlying process is running.
		 * @return whether the underlying process is running
		 */
		public synchronized boolean isRunning() {
			return isRunning;
		}

		/**
		 * Runs the underlying process.
		 */
		@Override
		public void run() {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = br.readLine()) != null) {
					isRunning = true;
					pw.println(line);
					notifyStreamObservers(new ProcessStreamEvent(cbt, line));
					logger.info("{}", line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isRunning = false;
			}
		}
	}

	/**
	 * Stops the underlying process.
	 */
	public void stop() {
		if (process != null) {
			try {
				process.destroyForcibly().waitFor();
				seInfo.join();
				seError.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}