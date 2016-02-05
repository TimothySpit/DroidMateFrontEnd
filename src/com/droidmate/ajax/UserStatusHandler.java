package com.droidmate.ajax;

import java.io.IOException;

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
 * Instance of Servlet implementation: UserStatusHandler. Handles the user status.
 */
@WebServlet("/UserStatusHandler")
public class UserStatusHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String USER_STATUS_GET = "getUserStatus";

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * Creates a new instance of the UserStatusHandler class.
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public UserStatusHandler() {
		super();
	}

	/**
	 * Handles the user status.
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

		// serve request
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute("user");
		JSONObject result = new JSONObject();

		// handle user status get
		String getUserStatusString = request.getParameter(USER_STATUS_GET);
		if (getUserStatusString != null) {
			logger.info("{}: Handle {} parameter {} with value {}",request.getRequestURI(),request.getMethod(), USER_STATUS_GET,getUserStatusString);
			
			JSONResponseWrapper getUserStatusResult = new JSONResponseWrapper();
			
			//always return a status
			getUserStatusResult = new JSONResponseWrapper(true, "Status successfully returned.");
			//set payload
			JSONObject payload = new JSONObject();
			payload.put("data", user.getStatus().getName());
			getUserStatusResult.setPayload(payload);
			result.put(USER_STATUS_GET, getUserStatusResult.toJSONObject());
		}

		logger.info("{}: Request result: {}",request.getRequestURI(),result);
		response.getWriter().print(result);
	}
}
