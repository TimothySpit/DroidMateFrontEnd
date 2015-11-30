package webFrontEnd;

import java.io.File;
import java.util.List;

/*
 * A class that creates a thread which tests the List of files, and then setProgress() on the FileContainer.
 * We start the class by calling performTests
 */

public class Testclass {
	public static Thread testingThread;
	private static List<FileContainer> list;

	public static void startTestThread() {
		new Thread(() -> {
			performTests();
		}).start();
	}

	
	public static void performTests() {
		list=FileList.getInstance();
		for (FileContainer file : list) {
			boolean success = Tester.test(file.getAbsoluteFilePath());
			if (success) {
				file.setLength(new File(file.getAbsoluteFilePath()).length());
				double lastresult = 0;
				while (true) {
					lastresult = Tester.getProgress();
					if (lastresult == -1)
						break;
					file.setProgress(lastresult * 100);
					
				}
			} else {
				file.setProgress(-2);
			}
		}
	}

	public static void stopTests() {
		testingThread.interrupt();
		testingThread = null;
	}
}
