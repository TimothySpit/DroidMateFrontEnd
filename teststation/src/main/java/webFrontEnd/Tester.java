package webFrontEnd;

import java.io.File;
import java.sql.Time;
import java.util.Random;

/*
 * This is a class to simulate droidMate. We start a test by calling test() on the path to a file.
 * After this, we call getProgress() until we get -1. 
*/
public class Tester {
	static Random random = new Random();
	public static File currentFile = null;
	public static long length = 0;
	public static long lengthleft = 0;

	public static boolean test(String absoluteFilePath) {
		currentFile = new File(absoluteFilePath);
		if (currentFile.isFile()) {
			length = currentFile.length();
			lengthleft = currentFile.length();
			return true;
		} else {
			currentFile = null;
			return false;
		}
	}

	// Sleeps a while, then produces the next result. Returns -1 if we are done
	public static double getProgress() {
		if (lengthleft == 0 || currentFile == null) {
			System.out.println("returned ");
			return -1;
		}
		try {
			Thread.sleep((long) (Math.random() * 300 + 50));
		} catch (InterruptedException e) {
		}
		long val = Math.min(random.nextInt(100), lengthleft);
		lengthleft -= val;
		double progress = 0;
		if (length != 0) {
			progress = (double) ((double) (length - lengthleft)) / ((double) (Math.max(1, length)));
		} else {
			progress -= 1;
		}
		return progress;
	}

}
