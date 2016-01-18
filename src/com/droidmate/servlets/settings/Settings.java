package com.droidmate.servlets.settings;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.settings.GUISettings;
import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class Settings
 */
@WebServlet("/Settings")
public class Settings extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Settings() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		JSONObject result = new JSONObject();
		GUISettings settings = new GUISettings();

		// return requested setting
		if (request.getParameterValues("get[]") != null) {
			try {
				JSONArray parameters = new JSONArray(request.getParameterValues("get[]"));
				for (int i = 0; i < parameters.length(); i++) {
					String current = parameters.optString(i);
					switch (current) {
					case "outputPath":
						result.put("outputPath", settings.getOutputFolder());
						break;
					case "droidmatePath":
						result.put("droidmatePath", settings.getDroidMatePath());
						break;
					case "time":
						result.put("time", settings.getExplorationTimeout());
					default:
						break;
					}
				}
				out.print(result);
				out.flush();
			} catch (Exception e) {
				// Json not parsable
				e.printStackTrace();
				return;
			}
		}

		// save
		if (request.getParameter("save") != null && request.getParameter("save").equals("true")) {
			if (request.getParameter("outputPath") == null || request.getParameter("time") == null
					|| request.getParameter("droidmatePath") == null) {
				return;
			}

			String outputPath = request.getParameter("outputPath");
			String droidmatePath = request.getParameter("droidmatePath");
			String explorationTime = request.getParameter("time");
			boolean settingsCorrect = true;

			try {
				settings.setOutputFolder(Paths.get(outputPath));
			} catch (InvalidPathException e) {
				result.put("reason", "Not a valid path.");
				settingsCorrect = false;
			}

			if (settingsCorrect) {
				try {
					settings.setDroidMatePath(Paths.get(droidmatePath));
				} catch (InvalidPathException e) {
					result.put("reason", "Not a valid path.");
					settingsCorrect = false;
				}
			}

			if (settingsCorrect && NumberUtils.isDigits(explorationTime)) {
				try {
					int number = Integer.parseInt(explorationTime);
					settings.setExplorationTimeout(number);
				} catch (Exception e) {
					result.put("reason", "Timeout must be a valid number (not too big).");
					settingsCorrect = false;
				}
			} else {
				result.put("reason", "Timeout must be a number greater than 0.");
				settingsCorrect = false;
			}

			result.put("success", settingsCorrect);
			out.print(result);
			out.flush();
		}

		if (request.getParameterMap().size() == 0) {
			if (user.isExplorationStarted()) {
				// no access to this page, redirect to exploration
				request.getRequestDispatcher("/WEB-INF/views/pages/explore/explore.jsp").forward(request, response);
				return;
			} else {
				request.getRequestDispatcher("/WEB-INF/views/pages/settings/settings.jsp").forward(request, response);
			}
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
