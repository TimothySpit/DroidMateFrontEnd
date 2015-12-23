package com.droidmate.servlets.ajax;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.filehandling.FileType;

/**
 * Servlet implementation class FileSystem
 */
@WebServlet("/FileSystem")
public class FileSystem extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileSystem() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("selectedAPKS") == null) {
			return;
		}

		// get requested types to return
		FileType fileType = requestedFileType(request.getParameter("type"));
		if (fileType.equals(FileType.UNKNOWN))
			return;

		String path = request.getParameter("path");
		if (path == null || (!path.equals("root") && !(new File(path)).exists()))
			return;

		// get current path to get data for
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(getFileData(fileType, path));
		out.flush();
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
		}

		JSONArray result = new JSONArray();
		for (File file : currentRoots) {
			JSONObject child = new JSONObject();
			child.put("text", file.getPath());
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
			}else {
				child.put("icon","jstree-file");
			}
			child.put("children", hasSubDirectories);
			result.put(child);
		}

		return result;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private FileType requestedFileType(String parameter) {
		if (parameter == null) {
			return FileType.UNKNOWN;
		}
		if (parameter.equals("dir"))
			return FileType.DIRECTORY;
		if (parameter.equals("all"))
			return FileType.ALL;
		return FileType.UNKNOWN;
	}
}
