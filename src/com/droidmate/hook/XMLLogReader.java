package com.droidmate.hook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.droidmate.apk.APKInformation;
import com.droidmate.apk.ExplorationReport;

/**
 * 
 * Parses xml log using the following format:
 * <exploration> <apk> <name>org.bla.droidmate</name>
 * <events> <elements_seen>16</elements>seen> </events\>
 * <success>true</success> </apk> <apk> ... </exploration> *
 */
public class XMLLogReader {

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
			int value = super.read();
			if (value == -1 && !stop) {
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

		@Override
		public int read(byte[] b) throws IOException {
			int value = super.read(b);
			if (value == -1 && !stop) {
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

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int value = super.read(b, off, len);
			if (value == -1 && !stop) {
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

		public void stop() {
			stop = true;
		}
	}

	private class XMLLogParser {

		private Map<String, APKExplorationInfo> apksMapLogReader;
		private APKExplorationInfo currentApkExplorationInfo;
		private APKInformation currentAPK;
		private ConcurrentSkipListMap<Long, Integer> globalElementsSeenHistory;
		private ConcurrentSkipListMap<Long, Integer> globalScreensSeenHistory;

		// Flags to determine state
		private boolean readExploration = false;
		private boolean readApk = false;
		private boolean readName = false;
		private boolean readEvents = false;
		private boolean readElementsSeen = false;
		private boolean readScreensSeen = false;
		private boolean readSuccess = false;
		private long globalStartingTime;
		private int globalElementsSeen;
		private int globalScreensSeen;

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
			globalElementsSeenHistory.put(0l, 0);
			globalScreensSeenHistory.put(0l, 0);
		}

		public void parse(XmlPullParser xpp) throws XmlPullParserException {
			int type = xpp.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					startElement(xpp.getName());
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.TEXT:
					characters(xpp.getText());
					break;
				}
				try {
					type = xpp.next();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void readName(String name) {
			currentApkExplorationInfo = new APKExplorationInfo(name);
			apksMapLogReader.put(name, currentApkExplorationInfo);
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

			ExplorationReport report = new ExplorationReport(currentApkExplorationInfo.getElementsSeen(),
					currentApkExplorationInfo.getScreensSeen(), currentApkExplorationInfo.isSuccess());
			currentAPK.setReport(report);
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

		public int getGlobalElementsSeen() {
			return globalElementsSeen;
		}

		public int getGlobalScreensSeen() {
			return globalScreensSeen;
		}

		public ConcurrentSkipListMap<Long, Integer> getGlobalElementsSeenHistory() {
			return globalElementsSeenHistory;
		}

		public ConcurrentSkipListMap<Long, Integer> getGlobalScreensSeenHistory() {
			return globalScreensSeenHistory;
		}
	}

	private final File sourceFile;
	private final ConcurrentHashMap<String, APKExplorationInfo> apksMapReaderHandler = new ConcurrentHashMap<>();
	private ForeverFileInputStream inputStream;
	private APKInformation[] apks;
	private XMLLogParser parser;

	public XMLLogReader(File source, APKInformation[] apks) {
		this.sourceFile = source;
		this.apks = apks;

		parser = new XMLLogParser(apksMapReaderHandler);
	}

	public void stopReading() {
		if (inputStream != null) {
			inputStream.stop();
		}
	}

	public void startConcurrentReading() {
		new Thread(new Runnable() {
			@Override
			public void run() {
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
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();

			inputStream = new ForeverFileInputStream(sourceFile);
			// Necessary for utf-8
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			xpp.setInput(reader);

			try {
				parser.parse(xpp);
			} catch (XmlPullParserException e) {
				// Ignore error and continue parsing
				System.out.println("Invalid xml log found, continue parsing.");
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ConcurrentSkipListMap<Long, Integer> getGlobalElementsSeenHistory() {
		return parser.getGlobalElementsSeenHistory();
	}

	public ConcurrentSkipListMap<Long, Integer> getGlobalScreensSeenHistory() {
		return parser.getGlobalScreensSeenHistory();
	}

	public int getGlobalElementsSeen() {
		return parser.getGlobalElementsSeen();
	}

	public int getGlobalScreensSeen() {
		return parser.getGlobalScreensSeen();
	}

	public Collection<APKExplorationInfo> getApksInfo() {
		return getApksMap().values();
	}

	public Map<String, APKExplorationInfo> getApksMap() {
		return apksMapReaderHandler;
	}
}