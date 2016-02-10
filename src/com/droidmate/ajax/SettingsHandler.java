package com.droidmate.ajax;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droidmate.user.GUISettings;

/**
 * Instance of Servlet implementation: SettingsHandler. This class helps
 * generating the change settings page.
 */
@WebServlet("/SettingsHandler")
public class SettingsHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String SETTINGS_SET = "setSettings";
	private static final String SETTINGS_REPORTS_OUTPUT_PATH = "outputPath";
	private static final String SETTINGS_DROIDMATE_PATH = "droidmatePath";
	private static final String SETTINGS_AAPT_PATH = "aaptPath";
	private static final String SETTINGS_EXPLORATION_TIME = "time";

	/**	The logger which is useful for debugging.	*/
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Creates a new instance of the SettingsHandler class.
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public SettingsHandler() {
		super();
	}

	/**
	 * Handling for Setting changes.
	 * 
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

		GUISettings settings = new GUISettings();

		JSONObject result = new JSONObject();
		
		// handle SETTINGS_SET request
		String settingsSet = request.getParameter(SETTINGS_SET);
		boolean allSettingsCorrect = true;
		if (settingsSet != null) {
			logger.debug("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), SETTINGS_SET, settingsSet);

			// check for reports path parameter
			String settingsParameter = request.getParameter(SETTINGS_REPORTS_OUTPUT_PATH);
			if (settingsParameter != null) {
				logger.debug("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), SETTINGS_REPORTS_OUTPUT_PATH,
						settingsParameter);

				JSONObject reportsPathSetResult = new JSONObject();
				// check reports path
				Path reportsPath = Paths.get(settingsParameter);
				if (!reportsPath.toFile().exists()) {
					// path does not exist
					reportsPathSetResult.put("result", false);
					reportsPathSetResult.put("message", "Report path " + reportsPath + " does not exist.");
					allSettingsCorrect = false;
				} else if (!reportsPath.toFile().isDirectory()) {
					// path is no valid directory
					reportsPathSetResult.put("result", false);
					reportsPathSetResult.put("message", "Report path " + reportsPath + " is no valid directory.");
					allSettingsCorrect = false;
				} else {
					// valid path, save it
					settings.setOutputFolder(reportsPath);
					reportsPathSetResult.put("result", true);
					reportsPathSetResult.put("message", "Report path set to: " + reportsPath);
				}
				result.put(SETTINGS_REPORTS_OUTPUT_PATH, reportsPathSetResult);
			}

			// check for DroidMate path parameter
			settingsParameter = request.getParameter(SETTINGS_DROIDMATE_PATH);
			if (settingsParameter != null) {
				logger.debug("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), SETTINGS_DROIDMATE_PATH,
						settingsParameter);

				JSONObject droidMatePathSetResult = new JSONObject();
				// check DroidMate path
				Path droidMatePath = Paths.get(settingsParameter);
				if (!droidMatePath.toFile().exists()) {
					// path does not exist
					droidMatePathSetResult.put("result", false);
					droidMatePathSetResult.put("message", "DroidMate path " + droidMatePath + " does not exist.");
					allSettingsCorrect = false;
				} else if (!droidMatePath.toFile().isDirectory()) {
					// path is no valid directory
					droidMatePathSetResult.put("result", false);
					droidMatePathSetResult.put("message", "DroidMate path " + droidMatePath + " is no valid directory.");
					allSettingsCorrect = false;
				} else {
					// valid path, save it
					settings.setDroidMatePath(droidMatePath);
					droidMatePathSetResult.put("result", true);
					droidMatePathSetResult.put("message", "DroidMate path set to: " + droidMatePath);
				}
				result.put(SETTINGS_DROIDMATE_PATH, droidMatePathSetResult);
			}

			// check for AAPT path parameter
			settingsParameter = request.getParameter(SETTINGS_AAPT_PATH);
			if (settingsParameter != null) {
				logger.debug("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), SETTINGS_AAPT_PATH, settingsParameter);

				JSONObject aaptPathSetResult = new JSONObject();
				// check DroidMate path
				Path aaptPath = Paths.get(settingsParameter);
				if (!aaptPath.toFile().exists()) {
					// path does not exist
					aaptPathSetResult.put("result", false);
					aaptPathSetResult.put("message", "AAPT path " + aaptPath + " does not exist.");
					allSettingsCorrect = false;
				} else if (!aaptPath.toFile().isDirectory()) {
					// path is no valid directory
					aaptPathSetResult.put("result", false);
					aaptPathSetResult.put("message", "AAPT path " + aaptPath + " is no valid directory.");
					allSettingsCorrect = false;
				} else {
					// valid path, save it
					settings.setAaptToolPath(aaptPath);
					aaptPathSetResult.put("result", true);
					aaptPathSetResult.put("message", "AAPT path set to: " + aaptPath);
				}
				result.put(SETTINGS_AAPT_PATH, aaptPathSetResult);
			}

			// check for exploration time parameter
			settingsParameter = request.getParameter(SETTINGS_EXPLORATION_TIME);
			if (settingsParameter != null) {
				logger.debug("{}: Handle {} parameter {} with value {}", request.getRequestURI(), request.getMethod(), SETTINGS_EXPLORATION_TIME,
						settingsParameter);

				JSONObject explorationTimeSetResult = new JSONObject();
				// check DroidMate path
				if (!NumberUtils.isDigits(settingsParameter)) {
					// exploration time is no number
					explorationTimeSetResult.put("result", false);
					explorationTimeSetResult.put("message", "Exploration time " + settingsParameter + " is no integer number.");
					allSettingsCorrect = false;
				}
				// try to cast exploration time
				try {
					int explorationTime = Integer.parseInt(settingsParameter);

					if (explorationTime <= 0) {
						// exploration time must be grater than zero
						explorationTimeSetResult.put("result", false);
						explorationTimeSetResult.put("message", "Exploration time " + settingsParameter + " is negative.");
						allSettingsCorrect = false;
					} else {
						// valid exploration time, save it
						settings.setExplorationTimeout(explorationTime);
						explorationTimeSetResult.put("result", true);
						explorationTimeSetResult.put("message", "Exploration time set to " + explorationTime);
					}
				} catch (NumberFormatException e) {
					explorationTimeSetResult.put("result", false);
					explorationTimeSetResult.put("message", "Exploration time " + settingsParameter + " is no valid number.");
					allSettingsCorrect = false;
				}
				result.put(SETTINGS_EXPLORATION_TIME, explorationTimeSetResult);
			}
		}

		JSONObject settingsSetResult = new JSONObject();
		settingsSetResult.put("result", allSettingsCorrect);
		if (allSettingsCorrect) {
			settingsSetResult.put("message", "All Settings have been saved successfully.");
		} else {
			settingsSetResult.put("message", "There were errors in saving the settings.");
		}
		result.put(SETTINGS_SET, settingsSetResult);

		logger.debug("{}: Request result: {}", request.getRequestURI(), result);
		response.getWriter().print(result);
	}

}
