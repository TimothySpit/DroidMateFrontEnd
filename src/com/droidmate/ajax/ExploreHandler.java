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
 * Servlet implementation class ExploreHandler
 */
@WebServlet("/ExploreHandler")
public class ExploreHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String START_DROIDMATE = "startExploration";

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExploreHandler() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Serve {} page request.", request.getRequestURI());

		// return json
		response.setContentType("application/json");
		// Do not cache
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");

		// serve request
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute("user");
		JSONObject result = new JSONObject();

		// handle SELECTED_APKS_SET request
		String[] startDroidMateString = request.getParameterValues(START_DROIDMATE);
		if (startDroidMateString != null) {
			logger.info("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), START_DROIDMATE, startDroidMateString);

			JSONResponseWrapper startDroidMateResult = new JSONResponseWrapper();

			if (user.isExplorationStarted()) {
				startDroidMateResult = new JSONResponseWrapper(false, "DroidMate was already started. Please start a new Exploration.");
			} else {
				//try start DroidMate
				try {
					boolean startResult = user.startDroidMate();
					if (startResult) {
						startDroidMateResult = new JSONResponseWrapper(startResult, "DroidMate successfully started.");
					} else {
						startDroidMateResult = new JSONResponseWrapper(startResult, "DroidMate had an intern error.");
					}
				} catch (Exception e) {
					startDroidMateResult = new JSONResponseWrapper(false, e.getMessage());
				}
			}
			
			result.put(START_DROIDMATE, startDroidMateResult.toJSONObject());
		}

		logger.info("{}: Request result: {}", request.getRequestURI(), result);
		response.getWriter().print(result);
	}

}
