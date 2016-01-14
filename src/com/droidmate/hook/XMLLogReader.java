package com.droidmate.hook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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

	private class LogReaderHandler extends DefaultHandler {

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

		public LogReaderHandler(Map<String, APKExplorationInfo> apks) {
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

		@Override
		public void characters(char ch[], int start, int length) throws SAXException {
			String value = new String(ch, start, length);
			if (readName) {
				currentApkExplorationInfo = new APKExplorationInfo(value);
				apksMapLogReader.put(value, currentApkExplorationInfo);

				readName = false;

				for (APKInformation apk : apks) {
					String name = String.copyValueOf(ch).substring(start, start+length);
					if (apk.getFile().getName().equals(name)) {
						currentAPK = apk;
						break;
					}
				}

			} else if(readScreensSeen) {
				System.out.println("New screens: " + value);
				int newScreensSeen = Integer.parseInt(value);
				currentApkExplorationInfo.addScreensSeen(newScreensSeen);
				globalScreensSeenHistory.put(System.currentTimeMillis() - globalStartingTime, newScreensSeen);

				readScreensSeen = false;
			}else if (readElementsSeen) {
				int newElementsSeen = Integer.parseInt(value);
				currentApkExplorationInfo.addElementsSeen(newElementsSeen);
				globalElementsSeenHistory.put(System.currentTimeMillis() - globalStartingTime, newElementsSeen);

				readElementsSeen = false;
			} else if (readSuccess) {
				currentApkExplorationInfo.setSuccess(Boolean.parseBoolean(value));
				currentApkExplorationInfo.setFinished(true);

				readSuccess = false;

				ExplorationReport report = new ExplorationReport(currentApkExplorationInfo.getElementsSeen(), currentApkExplorationInfo.getScreensSeen(), currentApkExplorationInfo.isSuccess());
				currentAPK.setReport(report);
			}
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// System.out.println("Start Element :" + qName);

			switch (qName.toLowerCase()) {
			case "exploration":
				readExploration = true;
				break;
			case "gui_screens_seen":
				System.out.println("Found a screen!");
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
	private LogReaderHandler handler;

	public XMLLogReader(File source, APKInformation[] apks) {
		this.sourceFile = source;
		this.apks = apks;

		handler = new LogReaderHandler(apksMapReaderHandler);
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
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			inputStream = new ForeverFileInputStream(sourceFile);
			// Necessary for utf-8
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(inputStream, handler);
		} catch (Exception e) {
			//XML is not properly closed, so DroidMate probably crashed
			if (!e.getMessage().contains("XML-Dokumentstrukturen müssen innerhalb derselben Entität beginnen und enden.")
					&& !e.getMessage().contains("XML document structures must start and end within the same entity.")) {
				e.printStackTrace();
			}
		}
	}

	public ConcurrentSkipListMap<Long, Integer> getGlobalElementsSeenHistory() {
		return handler.getGlobalElementsSeenHistory();
	}

	public ConcurrentSkipListMap<Long, Integer> getGlobalScreensSeenHistory() {
		return handler.getGlobalScreensSeenHistory();
	}

	public Collection<APKExplorationInfo> getApksInfo() {
		return getApksMap().values();
	}

	public Map<String, APKExplorationInfo> getApksMap() {
		return apksMapReaderHandler;
	}
}