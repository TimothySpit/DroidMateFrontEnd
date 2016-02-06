package com.droidmate.processes.logfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Behaves exactly like FileInputStream, but never returns EOF until stop()
 * is called.
 */
public class ForeverFileInputStream extends FileInputStream {

	private AtomicBoolean stopFlag = new AtomicBoolean(false);

	public ForeverFileInputStream(File file) throws FileNotFoundException {
		super(file);
	}

	public static final long REFRESH_INTERVAL = 100;

	public void stop() {
		this.stopFlag.set(true);
	}
	
	@Override
	public int read() throws IOException {
		if (stopFlag.get()) {
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
		if (stopFlag.get()) {
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
}