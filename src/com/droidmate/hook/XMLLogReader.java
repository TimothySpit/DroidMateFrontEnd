package com.droidmate.hook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * Parses xml log using the following format:
 *	<exploration>
 *		<apk>
 *			<name>org.bla.droidmate</name>
 *			<events>
 *				<elements_seen>16</elements>seen>
 *			</events\>
 *			<success>true</success>
 *		</apk>
 *		<apk>
 *			...
 *	</exploration> *
 */
public class XMLLogReader {

	private class LogReaderHandler extends DefaultHandler {

		private Map<String, APKExplorationInfo> apks;
		private APKExplorationInfo currentApk;
		
		// Flags to determine state
		boolean readExploration = false;
		boolean readApk = false;
		boolean readName = false;
		boolean readEvents = false;
		boolean readElementsSeen = false;
		boolean readSuccess = false;

		public LogReaderHandler(Map<String, APKExplorationInfo> apks) {
			this.apks = apks;
		}

		@Override
		public void characters(char ch[], int start, int length) throws SAXException {
			String value = new String(ch, start, length);
			if (readName) {
				currentApk = new APKExplorationInfo(value);
				apks.put(value, currentApk);
				
				System.out.println("Name : " + value);
				readName = false;
			} else if (readElementsSeen) {
				currentApk.addElementsSeen(Integer.parseInt(value));
				
				System.out.println("ElementsSeen : " + new String(ch, start, length));
				readElementsSeen = false;
			} else if (readSuccess) {
				currentApk.setSuccess(Boolean.parseBoolean(value));
				
				System.out.println("Success:" + new String(ch, start, length));
				readSuccess = false;
			}
			// Just for debugging
			if (readApk) {
				System.out.println("Apk: " + new String(ch, start, length));
				readApk = false;
			} else if (readEvents) {
				System.out.println("Events : " + new String(ch, start, length));
				readEvents = false;
			}else if (readExploration) {
				System.out.println("Exploration: " + new String(ch, start, length));
				readExploration = false;
			}

		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			System.out.println("Start Element :" + qName);

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

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			System.out.println("End Element :" + qName);
		}

		@Override
		public void startDocument() {
			System.out.println("Document starts.");
		}

		@Override
		public void endDocument() {
			System.out.println("Document ends.");
		}
	}

	private final File sourceFile;
	private final ConcurrentHashMap<String, APKExplorationInfo> apks = new ConcurrentHashMap<>();

	public XMLLogReader(File source) {
		this.sourceFile = source;
	}

	public void startConcurrentReading() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				read();
			}
		}).start();
	}

	private void read() {
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new LogReaderHandler(apks);

			// Necessary for utf-8
			InputStream inputStream = new FileInputStream(sourceFile);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(is, handler);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, APKExplorationInfo> getApksMap() {
		return apks;
	}
}