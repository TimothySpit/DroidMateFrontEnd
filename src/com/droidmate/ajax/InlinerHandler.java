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
 * Servlet implementation class InlinerHandler
 */
@WebServlet("/InlinerHandler")
public class InlinerHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String START_INLINER = "startInlining";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InlinerHandler() {
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

		// handle inliner start
		String startInliner = request.getParameter(START_INLINER);
		if (startInliner != null) {
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

		response.getWriter().print(result);
	}

}
