package com.droidmate.processes.logfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.droidmate.interfaces.APKLogFileObservable;

public class APKLogFileHandler extends APKLogFileObservable {

	private final File inputFileToParse;

	private final boolean waitForFileCreation;
	private final AtomicBoolean stopProcessing = new AtomicBoolean(false);

	private ForeverFileInputStream inputFileStream = null;

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

	private void waitForFileCreation() {
		while (!inputFileToParse.exists() && !stopProcessing.get()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		if (waitForFileCreation) {
			// wait until input file is created
			waitForFileCreation();
		}
		
		if(!inputFileToParse.exists()) {
			//process was stopped
			return;
		}
		
		// start parsing
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();
			inputFileStream = new ForeverFileInputStream(inputFileToParse);
			Reader reader = new InputStreamReader(inputFileStream, StandardCharsets.UTF_8);
			parser.setInput(reader);
			int eventType = parser.getEventType();
			
			//info variables
			String currentAPKName = "";
			String text = "";
			//--------------
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if (tagname.equalsIgnoreCase("name")) {
						// new apk
						currentAPKName = tagname;
						notifyObservers(new APKStarted(currentAPKName, System.currentTimeMillis()));
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
						notifyObservers(new APKElementsExploredChanged(currentAPKName, Integer.parseInt(text)));
					}
					break;

				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		stopProcessing.set(true);
	}

	public File getInputFileToParse() {
		return inputFileToParse;
	}

}
