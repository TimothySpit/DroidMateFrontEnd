package webFrontEnd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileList {
	private static final List<FileContainer> fileList = new ArrayList<FileContainer>();

	private FileList() {
	}

	public static List<FileContainer> getInstance() {
		return fileList;
	}

}
