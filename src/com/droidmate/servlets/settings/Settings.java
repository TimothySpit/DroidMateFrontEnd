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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;

import com.droidmate.settings.GUISettings;

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}

		if (request.getParameter("save") != null && request.getParameter("save").equals("true")) {
			if (request.getParameter("path") == null || request.getParameter("time") == null) {
				return;
			}

			String outputPath = request.getParameter("path");
			String explorationTime = request.getParameter("time");

			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			GUISettings settings = new GUISettings();
			boolean settingsCorrect = true;

			try {
				settings.setOutputFolder(Paths.get(outputPath));
			} catch (InvalidPathException e) {
				result.put("reason", "Not a valid path.");
				settingsCorrect = false;
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
		} else {
			request.getRequestDispatcher("/WEB-INF/views/pages/settings/settings.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
