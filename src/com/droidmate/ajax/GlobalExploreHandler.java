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
import com.droidmate.user.ExplorationInfo;

/**
 * Servlet implementation class GlobalExploreHandler
 */
@WebServlet("/GlobalExploreHandler")
public class GlobalExploreHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String GLOBAL_INFORMATION_GET = "getGlobalExploration";

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GlobalExploreHandler() {
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
		String[] getGlobalInformationString = request.getParameterValues(GLOBAL_INFORMATION_GET);
		if (getGlobalInformationString != null) {
			logger.info("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), GLOBAL_INFORMATION_GET, getGlobalInformationString);

			JSONResponseWrapper getGlobalInformationResult = new JSONResponseWrapper();
			
			try {
				ExplorationInfo explorationInfo = user.getGloblExplorationInfo();
				
				// set payload
				JSONObject payload = new JSONObject();
				payload.put("data", explorationInfo.toJSONObject());
				getGlobalInformationResult = new JSONResponseWrapper(true, "Exploration info successfully returned.");
				getGlobalInformationResult.setPayload(payload);
			} catch (Exception e) {
				getGlobalInformationResult = new JSONResponseWrapper(false, e.getMessage());
			}
			
			result.put(GLOBAL_INFORMATION_GET, getGlobalInformationResult.toJSONObject());
		}

		logger.info("{}: Request result: {}", request.getRequestURI(), result);
		response.getWriter().print(result);
	}

}
