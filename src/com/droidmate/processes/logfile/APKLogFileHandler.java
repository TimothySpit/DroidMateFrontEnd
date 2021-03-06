package com.droidmate.processes.logfile;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.droidmate.interfaces.APKLogFileObservable;

/**
 * Handler for the APK log file
 */
public class APKLogFileHandler extends APKLogFileObservable {

	private final File inputFileToParse;

	private final boolean waitForFileCreation;
	private final AtomicBoolean stopProcessing = new AtomicBoolean(false);

	private final ReentrantLock inputStreamLock = new ReentrantLock();
	private final Condition messagesLeft = inputStreamLock.newCondition();
	private boolean messagesLeftFlag = true;

	private ForeverFileInputStream inputFileStream = null;

	/**
	 * Creates an instance of the APKLogFileHandler
	 * 
	 * @param inputFileToParse
	 *            the file to parse the log out
	 * @param waitForFileCreation
	 *            boolean indicating whether it should be waited for the file
	 *            creation
	 * @throws FileNotFoundException
	 *             if the input file was not found
	 */
	public APKLogFileHandler(File inputFileToParse, boolean waitForFileCreation) throws FileNotFoundException {
		this.waitForFileCreation = waitForFileCreation;
		if (!waitForFileCreation) {
			if (inputFileToParse == null) {
				throw new IllegalArgumentException("XML input file must be non null.");
			}
			if (!inputFileToParse.exists()) {
				throw new FileNotFoundException("XML file path " + inputFileToParse + " does not exist.");
			}
			if (inputFileToParse.isDirectory()) {
				throw new IllegalArgumentException("XML file path must must not be a directory.");
			}
		}

		this.inputFileToParse = inputFileToParse;
	}

	/**
	 * Help method to wait for file creation
	 */
	private void waitForFileCreation() {
		while (!inputFileToParse.exists() && !stopProcessing.get()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Starts Parsing
	 */
	public void start() {
		if (waitForFileCreation) {
			// wait until input file is created
			waitForFileCreation();
		}

		if (!inputFileToParse.exists()) {
			// process was stopped
			return;
		}

		// start parsing
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();
			inputStreamLock.lock();
			try {
				inputFileStream = new ForeverFileInputStream(inputFileToParse);
			} finally {
				inputStreamLock.unlock();
			}
			if (inputFileStream == null) {
				return;
			}
			Reader reader = new InputStreamReader(inputFileStream, StandardCharsets.UTF_8);
			parser.setInput(reader);
			int eventType = parser.getEventType();

			// info variables
			String currentAPKName = "";
			String text = "";
			List<String> exploredElements = new ArrayList<String>();
			// --------------

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if (tagname.equalsIgnoreCase("exploration")) {
						// DroidMate exploration started
						notifyObservers(new APKExplorationStarted(System.currentTimeMillis()));
					}
					break;

				case XmlPullParser.TEXT:
					text = parser.getText();
					break;

				case XmlPullParser.END_TAG:
					if (tagname.equalsIgnoreCase("gui_screens_seen")) {
						notifyObservers(new APKScreensSeenChanged(currentAPKName, Integer.parseInt(text)));
					} else if (tagname.equalsIgnoreCase("elements_seen")) {
						notifyObservers(new APKElementsSeenChanged(currentAPKName, Integer.parseInt(text)));
					} else if (tagname.equalsIgnoreCase("success")) {
						notifyObservers(new APKEnded(currentAPKName, System.currentTimeMillis(), Boolean.parseBoolean(text)));
					} else if (tagname.equalsIgnoreCase("widget_explored")) {
						if (!exploredElements.contains(text)) {
							exploredElements.add(text);
							notifyObservers(new APKElementsExploredChanged(currentAPKName, 1));
						}
					} else if (tagname.equalsIgnoreCase("exploration")) {
						notifyObservers(new APKExplorationEnded(System.currentTimeMillis()));
					} else if (tagname.equalsIgnoreCase("name")) {
						// new apk, remove inlined postfix
						int inlinedIndex = text.lastIndexOf("-inlined.apk");
						if (inlinedIndex >= 0) {
							// apk name has inlined prefix, remove it
							currentAPKName = text.substring(0, inlinedIndex) + ".apk";
						}
						exploredElements = new ArrayList<String>();
						notifyObservers(new APKStarted(currentAPKName, System.currentTimeMillis()));
					}
					break;

				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			// stream has been stopped
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// all messages read, signal
			inputStreamLock.lock();
			try {
				messagesLeftFlag = false;
				messagesLeft.signalAll();
			} finally {
				inputStreamLock.unlock();
			}
		}
	}

	/**
	 * Stops parsing
	 */
	public void stop() {
		stopProcessing.set(true);
		inputStreamLock.lock();
		try {
			if (inputFileStream != null) {
				inputFileStream.stop();

				// read all remaining messages
				while (messagesLeftFlag)
					messagesLeft.await();

				try {
					inputFileStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			inputStreamLock.unlock();
		}
	}

	public File getInputFileToParse() {
		return inputFileToParse;
	}

}
