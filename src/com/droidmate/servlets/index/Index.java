package com.droidmate.servlets.index;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
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
import com.droidmate.user.DroidMateUser;

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		if (request.getParameter("apkInfo") != null) {
			//create new user
			String path = request.getParameter("apkInfo");
			DroidMateUser newUser = new DroidMateUser(Paths.get(path));
			session.setAttribute("user", newUser);
			File apkFolder = new File(path);
			if (apkFolder.exists() && apkFolder.isDirectory()) {
				PrintWriter out = response.getWriter();
				JSONObject apks = generateAPKInfoForFolder(newUser);
				out.print(apks);
				out.flush();
			}
		} else {
			// standard request
			request.getRequestDispatcher("/WEB-INF/views/pages/index/index.jsp").forward(request, response);
		}
	}

	private JSONObject generateAPKInfoForFolder(DroidMateUser user) {
		JSONArray apkInfoArray = new JSONArray();
		int counter = 0;
		for (APKInformation apk : user.getAPKS()) {
			JSONArray apkInfo = new JSONArray();
			apkInfo.put(counter++);
			apkInfo.put(apk.getFile().getName());
			apkInfo.put(apk.getFile().length());
			apkInfo.put("package");
			apkInfo.put("version");
			apkInfoArray.put(apkInfo);
		}
		// change later from list to something else
		JSONObject res = new JSONObject();
		res.put("data", apkInfoArray);
		return res;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		doGet(request, response);
	}

}
