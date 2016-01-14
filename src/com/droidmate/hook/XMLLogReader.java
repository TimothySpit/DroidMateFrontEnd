package com.droidmate.hook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.droidmate.apk.APKInformation;
import com.droidmate.apk.ExplorationReport;
import com.droidmate.servlets.settings.Settings;
import com.droidmate.settings.GUISettings;

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

		private Map<String, APKExplorationInfo> apksMApLogReader;
		private APKExplorationInfo currentApkExplorationInfo;
		private APKInformation currentAPK;

		// Flags to determine state
		boolean readExploration = false;
		boolean readApk = false;
		boolean readName = false;
		boolean readEvents = false;
		boolean readElementsSeen = false;
		boolean readSuccess = false;
		long startingTime;

		public LogReaderHandler(Map<String, APKExplorationInfo> apks) {
			this.apksMApLogReader = apks;
			startingTime = System.currentTimeMillis();
		}

		@Override
		public void characters(char ch[], int start, int length) throws SAXException {
			String value = new String(ch, start, length);
			if (readName) {
				currentApkExplorationInfo = new APKExplorationInfo(value);
				apksMApLogReader.put(value, currentApkExplorationInfo);

				readName = false;

				for (APKInformation apk : apks) {
					String name = String.copyValueOf(ch).substring(start, start+length);
					if (apk.getFile().getName().equals(name)) {
						currentAPK = apk;
						break;
					}
				}

			} else if (readElementsSeen) {
				currentApkExplorationInfo.addElementsSeen(System.currentTimeMillis() - startingTime, Integer.parseInt(value));

				readElementsSeen = false;
			} else if (readSuccess) {
				currentApkExplorationInfo.setSuccess(Boolean.parseBoolean(value));
				currentApkExplorationInfo.setFinished(true);

				readSuccess = false;

				ExplorationReport report = new ExplorationReport(currentApkExplorationInfo.getElementsSeen(), currentApkExplorationInfo.isSuccess());
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
	}

	private final File sourceFile;
	private final ConcurrentHashMap<String, APKExplorationInfo> apksMapReaderHandler = new ConcurrentHashMap<>();
	private ForeverFileInputStream inputStream;
	private APKInformation[] apks;

	public XMLLogReader(File source, APKInformation[] apks) {
		this.sourceFile = source;
		this.apks = apks;
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

			DefaultHandler handler = new LogReaderHandler(apksMapReaderHandler);

			inputStream = new ForeverFileInputStream(sourceFile);
			// Necessary for utf-8
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(inputStream, handler);
		} catch (Exception e) {
			if (!e.getMessage().contains("XML-Dokumentstrukturen müssen innerhalb derselben Entität beginnen und enden.")
					&& !e.getMessage().contains("XML document structures must start and end within the same entity.")) {
				e.printStackTrace();
			}
		}
	}

	public Collection<APKExplorationInfo> getApksInfo() {
		return getApksMap().values();
	}

	public Map<String, APKExplorationInfo> getApksMap() {
		return apksMapReaderHandler;
	}
}