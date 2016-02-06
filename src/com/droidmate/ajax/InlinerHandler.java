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
 * Instance of Servlet implementation: InlinerHandler. This class handles the
 * inlining processes progress.
 */
@WebServlet("/InlinerHandler")
public class InlinerHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String START_INLINER = "startInlining";

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Creates a new instance of the InlinerHandler class.
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public InlinerHandler() {
		super();
	}

	/**
	 * Handles the inlining process.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
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

		// handle inliner start
		String startInliner = request.getParameter(START_INLINER);
		if (startInliner != null) {
			logger.info("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), START_INLINER, startInliner);

			JSONResponseWrapper startInliningResult = new JSONResponseWrapper();

			// Inliner is already started, return
			if (user.isInlinerStarted()) {
				startInliningResult = new JSONResponseWrapper(false, "Inliner is already started.");
			} else {
				// try start inliner
				try {
					boolean inlinerResult = user.startInliner();
					if (inlinerResult) {
						startInliningResult = new JSONResponseWrapper(inlinerResult, "Inliner successfully finished.");
					} else {
						startInliningResult = new JSONResponseWrapper(inlinerResult, "Inliner had an intern error.");
					}
				} catch (Exception e) {
					startInliningResult = new JSONResponseWrapper(false, e.getMessage());
				}
			}

			result.put(START_INLINER, startInliningResult.toJSONObject());
		}

		logger.info("{}: Request result: {}", request.getRequestURI(), result);
		response.getWriter().print(result);
	}

}
