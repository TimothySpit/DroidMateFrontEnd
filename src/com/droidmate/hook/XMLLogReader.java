package com.droidmate.hook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicInteger;

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
		// Flags to determine state
		boolean readExploration = false;
		boolean readApk = false;
		boolean readName = false;
		boolean readEvents = false;
		boolean readElementsSeen = false;
		boolean readSuccess = false;

		@Override
		public void startDocument() {
			System.out.println("Document starts.");
		}

		@Override
		public void endDocument() {
			System.out.println("Document ends.");
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
		public void characters(char ch[], int start, int length) throws SAXException {
			if (readName) {
				// Not used yet
				System.out.println("Name : " + new String(ch, start, length));
				readName = false;
			} else if (readElementsSeen) {
				System.out.println("ElementsSeen : " + new String(ch, start, length));
				addElementsSeen(Integer.parseInt(new String(ch, start, length)));
				readElementsSeen = false;
			} else
			// Just for debugging
			if (readApk) {
				System.out.println("Apk: " + new String(ch, start, length));
				readApk = false;
			} else if (readEvents) {
				System.out.println("Events : " + new String(ch, start, length));
				readEvents = false;
			}

		}

	}

	private File sourceFile;
	private AtomicInteger elementsSeen = new AtomicInteger(0);

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

			DefaultHandler handler = new LogReaderHandler();

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

	private void addElementsSeen(int newElementsSeen) {
		elementsSeen.addAndGet(newElementsSeen);
	}

	public int getElementsSeen() {
		return elementsSeen.get();
	}
}