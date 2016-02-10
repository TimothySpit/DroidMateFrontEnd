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
 * Servlet implementation class StopExplorationHandler
 */
@WebServlet("/StopExplorationHandler")
public class StopExplorationHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String STOP_EXPLORATION = "stopExploration";

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StopExplorationHandler() {
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
		String stopExplorationString = request.getParameter(STOP_EXPLORATION);
		if (stopExplorationString != null) {
			JSONResponseWrapper stopExplorationResult = new JSONResponseWrapper();

			try {
				user.stopExploration();

				stopExplorationResult = new JSONResponseWrapper(true, "DroidMate stopped.");
			} catch (Exception e) {
				stopExplorationResult = new JSONResponseWrapper(false, e.getMessage());
			}

			result.put(STOP_EXPLORATION, stopExplorationResult.toJSONObject());
		}

		logger.debug("{}: Request result: {}", request.getRequestURI(), result);
		response.getWriter().print(result);
	}

}
