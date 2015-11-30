package webFrontEnd;

import java.util.ArrayList;
import java.util.List;

public class FileList {
	private static final List<FileContainer> fileList = new ArrayList();

	private FileList() {}

	public static List<FileContainer> getInstance()
        {
		return fileList;
	}
}
