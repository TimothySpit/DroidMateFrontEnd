package com.droidmate.servlets.index;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.apk.APKInformation;

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
		if (request.getParameter("apkInfo") != null) {
			// apk info
			String path = request.getParameter("apkInfo");
			session.setAttribute("path", path);
			File apkFolder = new File(path);
			if (apkFolder.exists() && apkFolder.isDirectory()) {
				PrintWriter out = response.getWriter();
				JSONObject apks = generateAPKInfoForFolder(apkFolder, session);
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
		//change later from list to something else
		session.setAttribute("selectedAPKS", files);
		JSONObject res = new JSONObject();
		res.put("data", apkInfoArray);
		return res;
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

}
