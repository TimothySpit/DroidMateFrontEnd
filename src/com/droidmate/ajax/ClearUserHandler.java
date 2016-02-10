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
 * Servlet implementation class ClearUserHandler
 */
@WebServlet("/ClearUserHandler")
public class ClearUserHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String CLEAR_USER = "clearUser";
	
	/**	The logger which is useful for debugging.	*/
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ClearUserHandler() {
		super();
	}

	/**
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

		// get apks root
		String clearUserString = request.getParameter(CLEAR_USER);
		if (clearUserString != null) {
			JSONResponseWrapper clearUserResult = new JSONResponseWrapper();

			try {
				user.clear();

				clearUserResult = new JSONResponseWrapper(true, "User resetted.");
			} catch (Exception e) {
				clearUserResult = new JSONResponseWrapper(false, e.getMessage());
			}

			result.put(CLEAR_USER, clearUserResult.toJSONObject());
		}

		logger.debug("{}: Request result: {}", request.getRequestURI(), result);
		response.getWriter().print(result);
	}

}
