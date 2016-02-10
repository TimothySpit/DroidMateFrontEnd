package com.droidmate.ajax;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droidmate.user.DroidMateUser;
import com.droidmate.user.GUISettings;

/**
 * Servlet implementation class OutputFolderHandler
 */
@WebServlet("/OutputFolderHandler")
public class OutputFolderHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String OPEN_OUTPUT_FOLDER_EXPLORER = "openOutputFolder";

	/**	The logger which is useful for debugging.	*/
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OutputFolderHandler() {
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

		// handle OPEN_OUTPUT_FOLDER_EXPLORER request
		String openOutputFolderString = request.getParameter(OPEN_OUTPUT_FOLDER_EXPLORER);
		if (openOutputFolderString != null) {
			JSONResponseWrapper openOutputFolderResult = new JSONResponseWrapper();

			// get output folder path
			GUISettings settings = user.getSettings();
			Path outputFolderPath = settings.getOutputFolder();

			// open explorer
			if (openExplorerWindow(outputFolderPath)) {
				// explorer could be opened
				openOutputFolderResult = new JSONResponseWrapper(true, "Explorer opened at path " + outputFolderPath + ".");
			} else {
				openOutputFolderResult = new JSONResponseWrapper(false, "Explorer could not be opened at path " + outputFolderPath + ".");
			}

			result.put(OPEN_OUTPUT_FOLDER_EXPLORER, openOutputFolderResult.toJSONObject());
		}

		logger.debug("{}: Request result: {}", request.getRequestURI(), result);
		response.getWriter().print(result);
	}

	private boolean openExplorerWindow(Path path) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(path.toFile());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

}
