package com.droidmate.ajax;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class ConsoleOutputHandler
 */
@WebServlet("/ConsoleOutputHandler")
public class ConsoleOutputHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String CONSOLE_OUTPUT_GET = "getConsoleOutput";

	/** The logger which is useful for debugging. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConsoleOutputHandler() {
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

		// handle CONSOLE_OUTPUT_GET request
		String getConsoleOutputString = request.getParameter(CONSOLE_OUTPUT_GET);
		if (getConsoleOutputString != null) {
			logger.debug("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), CONSOLE_OUTPUT_GET, getConsoleOutputString);

			JSONResponseWrapper getConsoleOutputResult = new JSONResponseWrapper();

			if (!NumberUtils.isDigits(getConsoleOutputString)) {
				// console start line is no number
				getConsoleOutputResult = new JSONResponseWrapper(false, "Start line " + getConsoleOutputString + " is no integer number.");
			} else if (!NumberUtils.isParsable(getConsoleOutputString)) {
				getConsoleOutputResult = new JSONResponseWrapper(false, "Start line " + getConsoleOutputString + " is not parsable.");
			} else {
				// numberis parsable, test indices
				int consoleLinesCount = user.getConsoleOutputSize();
				int requestedConsoleLineStart = Integer.parseInt(getConsoleOutputString);

				if (requestedConsoleLineStart >= consoleLinesCount || requestedConsoleLineStart < 0) {
					// index out of bould
					getConsoleOutputResult = new JSONResponseWrapper(false, "Start line " + getConsoleOutputString + " is out of bounds.");
				} else {
					// everything is correct
					List<String> consoleOutput = user.getConsoleOutput(requestedConsoleLineStart, consoleLinesCount - 1);
					StringBuilder resultingConsoleOutputString = new StringBuilder();
					for (int i = 0; i < consoleOutput.size(); i++) {
						resultingConsoleOutputString.append(consoleOutput.get(i) + System.lineSeparator());
					}
					// set payload
					JSONObject payload = new JSONObject();
					payload.put("data", resultingConsoleOutputString.toString());
					getConsoleOutputResult = new JSONResponseWrapper(true, "Console output successfully returned.");
					getConsoleOutputResult.setPayload(payload);
				}
			}

			result.put(CONSOLE_OUTPUT_GET, getConsoleOutputResult.toJSONObject());
		}

		logger.debug("{}: Request result: {}", request.getRequestURI(), result);
		response.getWriter().print(result);
	}

}
