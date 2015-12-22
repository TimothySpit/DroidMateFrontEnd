package com.droidmate.servlets.index;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.apk.APKInformation;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Servlet implementation class DroidMateServlet
 */
@WebServlet("/Index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Index() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {
		// initialization
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if (request.getParameter("dir") != null && request.getParameter("path") != null) {
			// file tree stuff
			// standard request for root directories
			if (request.getParameter("dir") != null && request.getParameter("path") != null) {
				String path = request.getParameter("path");

				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				JSONArray requestResult;
				if (path.equals("root")) {
					requestResult = generateRootJSON();
				} else {
					requestResult = generateInnerNodeJSON(path);
				}

				out.print(requestResult);
				out.flush();
			}

		} else if (request.getParameter("apkInfo") != null) {
			// apk info
			String path = request.getParameter("apkInfo");
			File apkFolder = new File(path);
			if (apkFolder.exists() && apkFolder.isDirectory()) {
				PrintWriter out = response.getWriter();
				JSONObject apks = generateAPKInfoForFolder(apkFolder,session);
				out.print(apks);
				out.flush();
			}
		} else {
			// standard request
			request.getRequestDispatcher("/WEB-INF/views/pages/index/index.jsp").forward(request, response);
		}
	}

	private JSONObject generateAPKInfoForFolder(File apkFolder, HttpSession session) {
		File[] apks = apkFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return name.toLowerCase().endsWith(".apk");
			}
		});

		JSONArray apkInfoArray = new JSONArray();
		List<APKInformation> files = new LinkedList<>();
		int counter = 0;
		for (File apk : apks) {
			JSONArray apkInfo = new JSONArray();
			apkInfo.put(counter++);
			apkInfo.put(apk.getName());
			apkInfo.put(apk.length());
			apkInfo.put("package");
			apkInfo.put("version");
			apkInfoArray.put(apkInfo);
			files.add(new APKInformation(apk));
		}
		session.setAttribute("selectedAPKS", files);
		JSONObject res = new JSONObject();
		res.put("data", apkInfoArray);
		return res;
	}

	private JSONArray generateRootJSON() {
		JSONArray children = new JSONArray();

		for (File file : File.listRoots()) {
			JSONObject child = new JSONObject();
			child.put("text", file.getPath());
			boolean hasSubDirectories = false;
			File[] directories = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});
			if (directories != null && directories.length > 0)
				hasSubDirectories = true;
			child.put("children", hasSubDirectories);
			children.put(child);
		}

		return children;
	}

	private JSONArray generateInnerNodeJSON(String path) {
		File currentFile = new File(path);
		if (!currentFile.exists() || !currentFile.isDirectory())
			return new JSONArray();

		JSONArray children = new JSONArray();

		File[] directories = currentFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});

		for (File file : directories) {
			JSONObject child = new JSONObject();
			child.put("text", file.getPath());
			boolean hasSubDirectories = false;
			File[] subdirectories = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});
			if (subdirectories != null && subdirectories.length > 0)
				hasSubDirectories = true;
			child.put("children", hasSubDirectories);
			children.put(child);
		}
		return children;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		doGet(request, response);
	}

	private JSONArray addDirectories(HashMap<String, File> idFileMapping, String requestedID) {
		JSONArray ret = new JSONArray();
		if (requestedID == "#") {
			JSONObject root = new JSONObject();
			ret.put(root);
			File[] dirs = File.listRoots();
			root.put("id", 1);
			root.put("text", "\\");
			JSONArray children = new JSONArray();

			int counter = 2;
			for (File file : dirs) {
				JSONObject child = new JSONObject();
				child.put("id", counter);
				child.put("text", file.getPath());
				boolean hasSubDirectories = false;
				File[] directories = file.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File current, String name) {
						return new File(current, name).isDirectory();
					}
				});
				if (directories != null && directories.length > 0)
					hasSubDirectories = true;
				child.put("children", hasSubDirectories);
				idFileMapping.put(String.valueOf(counter), file);
				children.put(child);
				counter++;
			}
			root.put("children", children);
		} else {
			JSONObject currDir = new JSONObject();
			ret.put(currDir);
			File dir = idFileMapping.get(requestedID);
			currDir.put("id", Integer.parseInt(requestedID));
			currDir.put("text", dir.getPath());
			File[] directories = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});
			JSONArray children = new JSONArray();

			int id = Integer.parseInt(requestedID) + 1;
			for (File file : directories) {
				JSONObject child = new JSONObject();
				child.put("id", id);
				child.put("text", file.getName());
				boolean hasSubDirectories = false;
				File[] subdirectories = file.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File current, String name) {
						return new File(current, name).isDirectory();
					}
				});
				if (subdirectories != null && subdirectories.length > 0)
					hasSubDirectories = true;
				child.put("children", hasSubDirectories);
				idFileMapping.put(String.valueOf(id), file);
				children.put(child);
				id++;
			}
			currDir.put("children", children);
		}

		return ret;
	}

}
