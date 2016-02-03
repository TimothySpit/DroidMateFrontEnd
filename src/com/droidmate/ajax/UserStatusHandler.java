package com.droidmate.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class UserStatusHandler
 */
@WebServlet("/UserStatusHandler")
public class UserStatusHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String USER_STATUS_GET = "getUserStatus";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserStatusHandler() {
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
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");

		// serve request
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute("user");
		JSONObject result = new JSONObject();

		// handle user status get
		String getUserStatusString = request.getParameter(USER_STATUS_GET);
		if (getUserStatusString != null) {
			JSONResponseWrapper getUserStatusResult = new JSONResponseWrapper();
			
			//always return a status
			getUserStatusResult = new JSONResponseWrapper(true, "Status successfully returned.");
			//set payload
			JSONObject payload = new JSONObject();
			payload.put("data", user.getStatus().getName());
			getUserStatusResult.setPayload(payload);
			result.put(USER_STATUS_GET, getUserStatusResult.toJSONObject());
		}

		response.getWriter().print(result);
	}
}
