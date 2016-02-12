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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droidmate.user.DroidMateUser;

/**
 * Instance of Servlet implementation: This class sets and retrieves the root
 * folder of the .apks to be explored.
 */
@WebServlet("/APKRootFolderHandler")
public class APKRootFolderHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String APKS_ROOT_GET = "getAPKsRoot";
	private static final String APKS_ROOT_SET = "setAPKsRoot";

	/** The logger which is useful for debugging. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Creates a new instance of the APKRootFolderHandler class
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public APKRootFolderHandler() {
		super();
	}

	/**
	 * Sets and retrieves the root folder of the .apks to be explored in a .json
	 * file.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Serve {} page request.", request.getRequestURI());
		// return json
		response.setContentType("application/json");
		// Do not cache
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");

		// serve request
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute("user");
		JSONObject result = new JSONObject();

		// handle APKS_ROOT_GET request
		String apkGetRoot = request.getParameter(APKS_ROOT_GET);
		if (apkGetRoot != null) {
			logger.debug("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), APKS_ROOT_GET, apkGetRoot);

			JSONResponseWrapper apkGetRootResult = new JSONResponseWrapper();

			if (user.getAPKPath() == null) {
				apkGetRootResult = new JSONResponseWrapper(false, "Path is not yet set.");
			} else {
				apkGetRootResult = new JSONResponseWrapper(true, "Path successfully get.");
				// set payload
				JSONObject payload = new JSONObject();
				payload.put("data", user.getAPKPath());
				apkGetRootResult.setPayload(payload);
			}
			result.put(APKS_ROOT_GET, apkGetRootResult.toJSONObject());
		}

		// handle APKS_ROOT_SET request
		String apkSaveRoot = request.getParameter(APKS_ROOT_SET);
		if (apkSaveRoot != null) {
			logger.debug("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), APKS_ROOT_SET, apkSaveRoot);

			JSONResponseWrapper setAPKRootResult = new JSONResponseWrapper();

			try {
				user.setAPKPath(Paths.get(apkSaveRoot));
				setAPKRootResult = new JSONResponseWrapper(true, "APK Root path has been set to: " + apkSaveRoot);
			} catch (FileNotFoundException e) {
				setAPKRootResult = new JSONResponseWrapper(false, e.getMessage());
			} catch (Exception e) {
				setAPKRootResult = new JSONResponseWrapper(false, "APK Root path could not been set to : " + apkSaveRoot + ". Details: " + e.getMessage());
			}
			result.put(APKS_ROOT_SET, setAPKRootResult.toJSONObject());
		}

		logger.debug("{}: Request result: {}", request.getRequestURI(), result);
		response.getWriter().print(result);
	}

}
