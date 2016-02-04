package com.droidmate.processes;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.droidmate.user.ExplorationInfo;

/**
 * Parses xml log using the following format:
 * 
 * <exploration> <apk> <name>org.bla.droidmate</name>
 * 
 * <events> <elements_seen>16</elements>seen> </events\>
 * 
 * <success>true</success> </apk> <apk> ... </exploration> *
 * 
 */

public class LogReaderProcess {

	/**
	 * Behaves exactly like FileInputStream, but never returns EOF until stop()
	 * is called.
	 */
	private class ForeverFileInputStream extends FileInputStream {

		private boolean stop = false;

		public ForeverFileInputStream(File file) throws FileNotFoundException {
			super(file);
		}

		public static final long REFRESH_INTERVAL = 100;

		@Override
		public int read() throws IOException {
			if (stop) {
				return -1;
			} else {
				int value = super.read();
				if (value == -1) {
					try {
						Thread.sleep(REFRESH_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					return this.read();
				} else {
					return value;
				}
			}
		}

		@Override
		public int read(byte[] b) throws IOException {
			if (stop) {
				return -1;
			} else {
				int value = super.read(b);
				if (value == -1) {
					try {
						Thread.sleep(REFRESH_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					return this.read(b);
				} else {
					return value;
				}
			}
		}

		@Override

		public int read(byte[] b, int off, int len) throws IOException {
			if (stop) {
				return -1;
			} else {
				int value = super.read(b, off, len);
				if (value == -1) {
					try {
						Thread.sleep(REFRESH_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					return this.read(b, off, len);
				} else {
					return value;
				}
			}
		}

		public void stop() {
			stop = true;
		}
	}

	private class XMLLogParser {

		private Map<String, ExplorationInfo> apksMap = new ConcurrentHashMap<>();
		private ExplorationInfo globalExplorationInfo = new ExplorationInfo();
		private ExplorationInfo currentApkExplorationInfo;

		// Flags to determine parsing state
		private boolean readExploration = false;
		private boolean readApk = false;
		private boolean readName = false;
		private boolean readEvents = false;
		private boolean readElementsSeen = false;
		private boolean readScreensSeen = false;
		private boolean readSuccess = false;
		private boolean readWidgetExplored = false;

		public void parse(XmlPullParser xpp) {
			try {
				int type = xpp.getEventType();
				while (type != XmlPullParser.END_DOCUMENT) {
					switch (type) {
					case XmlPullParser.START_TAG:
						startElement(xpp.getName());
						break;
					case XmlPullParser.END_TAG:
						if (xpp.getName().equalsIgnoreCase("exploration")) {
							// End of log file
							return;
						}
						break;
					case XmlPullParser.TEXT:
						characters(xpp.getText());
						break;
					}
					try {
						type = xpp.next();
					} catch (EOFException e) {
						// ForeverFileInputStream has been stopped, so just stop
						// parsing
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (XmlPullParserException e) {
				// Ignore error and continue parsing
				logger.info("Invalid xml log found, continue parsing...");
				e.printStackTrace();
				parse(xpp);
			}
		}

		private void readName(String name) {
			if (apksMap.containsKey(name)) {
				currentApkExplorationInfo = apksMap.get(name);
			} else {
				currentApkExplorationInfo = new ExplorationInfo();
				apksMap.put(name, currentApkExplorationInfo);
			}
		}

		private void readElementsSeen(int newElementsSeen) {
			currentApkExplorationInfo.addElementsSeen(newElementsSeen);
			globalExplorationInfo.addElementsSeen(newElementsSeen);
		}

		private void readScreensSeen(int newScreensSeen) {
			currentApkExplorationInfo.addScreensSeen(newScreensSeen);
			globalExplorationInfo.addScreensSeen(newScreensSeen);

		}

		private void readWidgetExplored() {
			currentApkExplorationInfo.addWidgetsExplored(1);
			globalExplorationInfo.addWidgetsExplored(1);
		}

		private void readSuccess(boolean success) {
			currentApkExplorationInfo.setSuccess(success);
			currentApkExplorationInfo.setFinished(true);
			globalExplorationInfo.setSuccess(success);
			globalExplorationInfo.setFinished(true);
		}

		public void characters(String text) {
			if (readName) {
				readName(text);
				readName = false;
			} else if (readScreensSeen) {
				readScreensSeen(Integer.parseInt(text));
				readScreensSeen = false;
			} else if (readElementsSeen) {
				readElementsSeen(Integer.parseInt(text));
				readElementsSeen = false;
			} else if (readSuccess) {
				readSuccess(Boolean.parseBoolean(text));
				readSuccess = false;
			} else if (readWidgetExplored) {
				readWidgetExplored();
				readWidgetExplored = false;
			}
			//Ignore the other states, they don't contain necessary information
		}

		public void startElement(String name) {
			switch (name.toLowerCase()) {
			case "exploration":
				readExploration = true;
				break;

			case "gui_screens_seen":
				readScreensSeen = true;
				break;

			case "apk":
				readApk = true;
				break;

			case "name":
				readName = true;
				break;

			case "events":
				readEvents = true;
				break;

			case "elements_seen":
				readElementsSeen = true;
				break;

			case "success":
				readSuccess = true;
				break;

			case "widget_explored":
				readWidgetExplored = true;
				break;
			}

		}

	}

	private final Logger logger = LoggerFactory.getLogger(LogReaderProcess.class);

	private final File sourceFile;
	private ForeverFileInputStream inputStream;
	private XMLLogParser parser;

	public LogReaderProcess(File source) throws FileNotFoundException {
		this.sourceFile = source;
		parser = new XMLLogParser();
	}

	public void stopReading() {
		if (inputStream != null) {
			inputStream.stop();
		}

		logger.debug("Set stop flag in ForeverFileInputStream...");
	}

	public void startConcurrentReading() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Wait for droidmate to create log file
				while (!sourceFile.exists()) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				read();
			}
		}).start();
	}

	private void read() {
		logger.info("Starting log reading...");

		try {
			inputStream = new ForeverFileInputStream(sourceFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		// Necessary for utf-8
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(reader);
			parser.parse(xpp);
			logger.info("Finished log reading.");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
