package com.droidmate.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.user.APKInformation;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class APKInformationHandler
 */
@WebServlet("/APKInformationHandler")
public class APKInformationHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String APKS_DATA = "getAPKSData";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APKInformationHandler() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// return json
		response.setContentType("application/json");
		// Do not cache
		// HTTP 1.1
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		// HTTP 1.0
		response.setHeader("Pragma", "no-cache");

		// serve request
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute("user");

		String apksData = request.getParameter(APKS_DATA);
		JSONObject res = new JSONObject();
		if (apksData != null) {
			// insert apk data
			JSONArray apksArray = new JSONArray();
			for (APKInformation apk : user.getAPKS()) {
				apksArray.put(apk.toJSONObject());
			}

			res.put(APKS_DATA, apksArray);
		}

		response.getWriter().print(res);
	}

}
