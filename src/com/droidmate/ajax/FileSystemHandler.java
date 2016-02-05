package com.droidmate.ajax;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Servlet implementation class FileSystem
 */
@WebServlet("/FileSystemHandler")
public class FileSystemHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//request parameters
	private final static String FILETYPE = "type";
	private final static String PATH = "path";

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private enum FileType {
		UNKNOWN("unknowm"), DIRECTORY("directory"), ALL("all");

		private final String name;

		private FileType(String s) {
			name = s;
		}

		public static FileType fromString(String s) {
			for (FileType ft : FileType.values()) {
				if (ft.equalsName(s)) {
					return ft;
				}
			}
			return FileType.UNKNOWN;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return this.name;
		}
	}

	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileSystemHandler() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Serve {} page request.",request.getRequestURI());
		
		// return json
		response.setContentType("application/json");
		// Do not cache
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		
		JSONArray result = new JSONArray();
		FileType fileType = FileType.fromString(request.getParameter(FILETYPE));
		
		String path = request.getParameter(PATH);
		if (path != null && (path.equals("root") || (new File(path)).exists())) {
			logger.info("{}: Handle {} parameter {} with value {}",request.getRequestURI(),request.getMethod(), PATH,path);
			logger.info("{}: Handle {} parameter {} with value {}",request.getRequestURI(),request.getMethod(), FILETYPE,fileType);
			
			result = getFileData(fileType, path);
		}

		logger.info("{}: Request result: {}",request.getRequestURI(),result);
		response.getWriter().print(result);
	}
	
	private JSONArray getFileData(FileType fileType, String path) {
		File[] currentRoots;
		if (path.equals("root")) {
			currentRoots = File.listRoots();
		} else {

			currentRoots = (new File(path)).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					if (fileType.equals(FileType.ALL))
						return true;
					else if (fileType.equals(FileType.DIRECTORY))
						return new File(current, name).isDirectory();

					return false;
				}
			});
			Arrays.sort(currentRoots);
		}

		JSONArray result = new JSONArray();
		for (File file : currentRoots) {
			JSONObject child = new JSONObject();
			String name = file.getName().equals("") ? file.getPath() : file.getName() + "/";
			child.put("text", name);

			boolean hasSubDirectories = false;
			if (file.isDirectory()) {
				File[] childFiles = file.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File current, String name) {
						if (fileType.equals(FileType.ALL))
							return true;
						else if (fileType.equals(FileType.DIRECTORY))
							return new File(current, name).isDirectory();

						return false;
					}
				});

				if (childFiles != null && childFiles.length > 0) {
					hasSubDirectories = true;
				}
			} else {
				child.put("icon", "jstree-file");
			}
			child.put("children", hasSubDirectories);
			result.put(child);
		}

		return result;
	}

}
