package com.droidmate.ajax;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class APKRootFolderHandler
 */
@WebServlet("/APKRootFolderHandler")
public class APKRootFolderHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String APKS_ROOT_GET = "getAPKsRoot";
	private static final String APKS_ROOT_SET = "setAPKsRoot";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APKRootFolderHandler() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// return json
		response.setContentType("application/json");
		// Do not cache
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");

		// serve request
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute("user");
		JSONObject result = new JSONObject();

		// get apks root
		String apkGetRoot = request.getParameter(APKS_ROOT_GET);
		if (apkGetRoot != null && user.getAPKPath() != null) {
			result.put(APKS_ROOT_GET, user.getAPKPath());
		}

		// save apks root
		String apkSaveRoot = request.getParameter(APKS_ROOT_SET);
		if (apkSaveRoot != null) {
			JSONObject setAPKRootresult = new JSONObject();
			try {
				user.setAPKPath(Paths.get(apkSaveRoot));
				setAPKRootresult.put("result", true);
				setAPKRootresult.put("message", "APK Root path has been set to: " + apkSaveRoot);
			} catch (FileNotFoundException e) {
				setAPKRootresult.put("result", false);
				setAPKRootresult.put("message", e.getMessage());
			} catch (Exception e) {
				setAPKRootresult.put("result", false);
				setAPKRootresult.put("message", "APK Root path could not been set to : " + apkSaveRoot + ". Details: " + e.getMessage());
			}
			result.put(APKS_ROOT_SET, setAPKRootresult);
		}

		response.getWriter().print(result);
	}

}
