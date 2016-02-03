package com.droidmate.processes;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

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

		private ConcurrentSkipListMap<Long, Integer> globalElementsSeenHistory;

		private ConcurrentSkipListMap<Long, Integer> globalScreensSeenHistory;

		private ConcurrentSkipListMap<Long, Integer> globalWidgetsExploredHistory;

		// Flags to determine parsing state
		private boolean readExploration = false;
		private boolean readApk = false;
		private boolean readName = false;
		private boolean readEvents = false;
		private boolean readElementsSeen = false;
		private boolean readScreensSeen = false;
		private boolean readSuccess = false;
		private boolean readWidgetExplored = false;

		private long globalStartingTime;
		private int globalElementsSeen;
		private int globalScreensSeen;
		private int globalWidgetsExplored;

		public XMLLogParser(Map<String, APKExplorationInfo> apks) {

			this.apksMapLogReader = apks;

			globalStartingTime = System.currentTimeMillis();

			Comparator<Long> c = new Comparator<Long>() {

				@Override

				public int compare(Long arg0, Long arg1) {

					return arg0.compareTo(arg1);

				}

			};

			globalElementsSeenHistory = new ConcurrentSkipListMap<>(c);

			globalScreensSeenHistory = new ConcurrentSkipListMap<>(c);

			globalWidgetsExploredHistory = new ConcurrentSkipListMap<>(c);

			globalElementsSeenHistory.put(0l, 0);

			globalScreensSeenHistory.put(0l, 0);

			globalWidgetsExploredHistory.put(0l, 0);

		}

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

				System.out.println("Invalid xml log found, continue parsing.");

				e.printStackTrace();

				parse(xpp);

			}

		}

		private void readName(String name) {

			currentApkExplorationInfo = apksMapLogReader.get(name);

			for (APKInformation apk : apks) {

				if (apk.getFile().getName().equals(name)) {

					currentAPK = apk;

					break;

				}

			}

		}

		private void readElementsSeen(int newElementsSeen) {

			currentApkExplorationInfo.addElementsSeen(newElementsSeen);

			globalElementsSeenHistory.put(System.currentTimeMillis() - globalStartingTime,

					addGlobalElementsSeen(newElementsSeen));

		}

		private void readScreensSeen(int newScreensSeen) {

			currentApkExplorationInfo.addScreensSeen(newScreensSeen);

			globalScreensSeenHistory.put(System.currentTimeMillis() - globalStartingTime, addGlobalScreensSeen(newScreensSeen));

		}

		private void readSuccess(boolean success) {

			currentApkExplorationInfo.setSuccess(success);

			currentApkExplorationInfo.setFinished(true);

		}

		private void readWidgetExplored() {

			currentApkExplorationInfo.addWidgetsExplored(1);

			globalWidgetsExploredHistory.put(System.currentTimeMillis() - globalStartingTime, addGlobalWidgetsExplored(1));

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

		private int addGlobalElementsSeen(int newGlobalElementsSeen) {

			this.globalElementsSeen += newGlobalElementsSeen;

			return globalElementsSeen;

		}

		private int addGlobalScreensSeen(int newGlobalScreensSeen) {

			this.globalScreensSeen += newGlobalScreensSeen;

			return globalScreensSeen;

		}

		private int addGlobalWidgetsExplored(int newWidgetsExplored) {

			this.globalWidgetsExplored += newWidgetsExplored;

			return globalWidgetsExplored;

		}

		public int getGlobalElementsSeen() {

			return globalElementsSeen;

		}

		public int getGlobalScreensSeen() {

			return globalScreensSeen;

		}

		public int getGlobalWidgetsExplored() {

			return globalWidgetsExplored;

		}

		public ConcurrentSkipListMap<Long, Integer> getGlobalElementsSeenHistory() {

			return globalElementsSeenHistory;

		}

		public ConcurrentSkipListMap<Long, Integer> getGlobalScreensSeenHistory() {

			return globalScreensSeenHistory;

		}

		public ConcurrentSkipListMap<Long, Integer> getGlobalWidgetsExploredHistory() {

			return globalWidgetsExploredHistory;

		}

		public long getGlobalStartingTime() {

			return globalStartingTime;

		}

	}

	private final File sourceFile;

	private final ConcurrentHashMap<String, APKExplorationInfo> apksMapReaderHandler = new ConcurrentHashMap<>();

	private ForeverFileInputStream inputStream;

	private List<APKInformation> apks;

	private XMLLogParser parser;

	public XMLLogReader(File source, List<APKInformation> apks) {

	                this.sourceFile = source;

	                this.apks = apks;

	                for (APKInformation apkInformation : apks) {

	                        if (apkInformation.isSelected()) {

	                                apksMapReaderHandler.put(apkInformation.getFile().getName(),

	                                                new APKExplorationInfo(apkInformation.getFile().getName()));

	                        }

	                }

	                parser = new XMLLogParser(apksMapReaderHandler);

	        }

	public void stopReading() {
		if (inputStream != null) {
			inputStream.stop();
		}

		System.out.println("Stopped log reading.");
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
		System.out.println("Starting log reading...");

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser xpp = factory.newPullParser();
		inputStream = new ForeverFileInputStream(sourceFile);
		// Necessary for utf-8
		Reader reader = new InputStreamReader(inputStream, "UTF-8");

		xpp.setInput(reader);
		parser.parse(xpp);

		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ConcurrentSkipListMap<Long, Integer> getGlobalElementsSeenHistory() {

		return parser.getGlobalElementsSeenHistory();

	}

	public ConcurrentSkipListMap<Long, Integer> getGlobalScreensSeenHistory() {

		return parser.getGlobalScreensSeenHistory();

	}

	public ConcurrentSkipListMap<Long, Integer> getGlobalWidgetsExploredHistory() {

		return parser.getGlobalWidgetsExploredHistory();

	}

	public int getGlobalElementsSeen() {

		return parser.getGlobalElementsSeen();

	}

	public int getGlobalScreensSeen() {

		return parser.getGlobalScreensSeen();

	}

	public int getGlobalWidgetsExplored() {

		return parser.getGlobalWidgetsExplored();

	}

	public long getGlobalStartingTime() {

		return parser.getGlobalStartingTime();

	}

	public Collection<APKExplorationInfo> getApksInfo() {

		return getApksMap().values();

	}

	public Map<String, APKExplorationInfo> getApksMap() {

		return apksMapReaderHandler;

	}

}
