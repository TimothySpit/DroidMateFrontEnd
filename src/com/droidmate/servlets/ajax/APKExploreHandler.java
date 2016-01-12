package com.droidmate.servlets.ajax;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;

import com.droidmate.apk.APKInformation;
import com.droidmate.hook.APKExplorationInfo;
import com.droidmate.hook.XMLLogReader;
import com.droidmate.settings.AjaxConstants;
import com.droidmate.settings.GUISettings;
import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class APKExploreHandler
 */
@WebServlet("/APKExploreHandler")
public class APKExploreHandler extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Process droidmateProcess;
	private File logFile;
	private XMLLogReader logReader;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APKExploreHandler() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		(new Thread(new Runnable() {
			@Override
			public void run() {
				if (request.getParameter(AjaxConstants.EXPLORE_START) != null) {
					startDroidmate(user.getAPKS());
				} else if (request.getParameter(AjaxConstants.EXPLORE_STOP) != null) {
					stopDroidmateForcibly();
				} else if (request.getParameter(AjaxConstants.EXPLORE_RESTART) != null) {
					stopDroidmateForcibly();
					startDroidmate(user.getAPKS());
				}
			}
		})).start();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		if (request.getParameter(AjaxConstants.EXPLORE_GET_INFO) != null) {
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			JSONArray result = new JSONArray();
			if (logReader != null) {
				for (APKExplorationInfo apk : logReader.getApksInfo()) {
					result.put(apk.toJSONObject());
				}
			}

			out.print(result);
			out.flush();
		}
	}

	private boolean startDroidmate(APKInformation[] apks) {
		System.out.println("Starting droidmate");

		GUISettings settings = new GUISettings();
		Path droidMateRoot = settings.getDroidMatePath();
		String gradlewName = "/gradlew.bat";
		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			gradlewName = "/gradlew";
		}
		Path droidMateExecutable = Paths.get(droidMateRoot.toString(), gradlewName);

		Path inputAPKsPath = Paths.get(droidMateRoot.toString(), "/apks/inlined/");
		logFile = new File(droidMateRoot.toString(), "/dev1/logs/gui.xml");
		logFile.delete();
		logReader = new XMLLogReader(logFile);

		// empty apks directory
		try {
			FileUtils.deleteDirectory(inputAPKsPath.toFile());
			inputAPKsPath.toFile().mkdir();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// for each apk, copy it
		for (APKInformation apkInfo : apks) {
			if (apkInfo.isSelected()) {
				try {
					Path inlinedAPK = Paths.get(apkInfo.getFile().getParent().toString(), "/inlined",
							FilenameUtils.removeExtension(apkInfo.getFile().getName()) + "-inlined.apk");
					Path target = Paths.get(inputAPKsPath.toString(), apkInfo.getFile().getName());
					target.toFile().mkdirs();
					Files.copy(inlinedAPK, target, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		ProcessBuilder pb = new ProcessBuilder(droidMateExecutable.toString(), "--stacktrace", ":projects:core:run");
		pb.directory(droidMateRoot.toFile());
		pb.redirectErrorStream(true);
		try {
			droidmateProcess = pb.start();
			logReader.startConcurrentReading();
			String s;
			BufferedReader stdout = new BufferedReader(new InputStreamReader(droidmateProcess.getInputStream()));
			while ((s = stdout.readLine()) != null) {
				System.out.println(s);
			}
			if (!droidmateProcess.waitFor(settings.getExplorationTimeout(), TimeUnit.SECONDS)) {
				System.out.println("Timeout has been reached, killing droidmate process.");
				stopDroidmateForcibly();
				return false;
			}

			logReader.stopReading();
			System.out.println("Exit value: " + droidmateProcess.exitValue());
			droidmateProcess.getInputStream().close();
			droidmateProcess.getOutputStream().close();
			droidmateProcess.getErrorStream().close();

			if (droidmateProcess.exitValue() != 0) {
				return false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	private void stopDroidmateForcibly() {
		System.out.println("Stopping droidmate...");

		logReader.stopReading();

		try {
			droidmateProcess.getInputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			droidmateProcess.getOutputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			droidmateProcess.getErrorStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		droidmateProcess.destroyForcibly();
	}

	private void generateReport() {
		// http://codepen.io/tjoen/pen/LEpeq
		String css = "@import \"compass/css3\";\r\n" + "\r\n" + "/*\r\n" + "\r\n" + "RESPONSTABLE 2.0 by jordyvanraaij\r\n"
				+ "  Designed mobile first!\r\n" + "\r\n"
				+ "If you like this solution, you might also want to check out the 1.0 version:\r\n"
				+ "  https://gist.github.com/jordyvanraaij/9069194\r\n" + "\r\n" + "*/\r\n" + "\r\n"
				+ "// Default options for table style\r\n" + "$table-breakpoint: 480px;\r\n"
				+ "$table-background-color: #FFF;\r\n" + "$table-text-color: #024457;\r\n"
				+ "$table-outer-border: 1px solid #167F92;\r\n" + "$table-cell-border: 1px solid #D9E4E6;\r\n" + "\r\n"
				+ "// Extra options for table style (parse these arguments when including your mixin)\r\n"
				+ "$table-border-radius: 10px;\r\n" + "$table-highlight-color: #EAF3F3;\r\n"
				+ "$table-header-background-color: #167F92;\r\n" + "$table-header-text-color: #FFF;\r\n"
				+ "$table-header-border: 1px solid #FFF;\r\n" + "\r\n" + "// The Responstable mixin\r\n" + "\r\n"
				+ "@mixin responstable(\r\n" + "  $breakpoint: $table-breakpoint,\r\n"
				+ "  $background-color: $table-background-color,\r\n" + "  $text-color: $table-text-color,\r\n"
				+ "  $outer-border: $table-outer-border,\r\n" + "  $cell-border: $table-cell-border,\r\n"
				+ "  $border-radius: none,\r\n" + "  $highlight-color: none,\r\n"
				+ "  $header-background-color: $table-background-color,\r\n" + "  $header-text-color: $table-text-color,\r\n"
				+ "  $header-border: $table-cell-border) {\r\n" + "  \r\n" + "  .responstable {\r\n" + "    margin: 1em 0;\r\n"
				+ "    width: 100%;\r\n" + "    overflow: hidden;  \r\n" + "    background: $background-color;\r\n"
				+ "    color: $text-color;\r\n" + "    border-radius: $border-radius;\r\n" + "    border: $outer-border;\r\n"
				+ "  \r\n" + "    tr {\r\n" + "      border: $cell-border; \r\n"
				+ "      &:nth-child(odd) { // highlight the odd rows with a color\r\n"
				+ "        background-color: $highlight-color;\r\n" + "      }  \r\n" + "    }\r\n" + "  \r\n" + "    th {\r\n"
				+ "      display: none; // hide all the table header for mobile\r\n" + "      border: $header-border;\r\n"
				+ "      background-color: $header-background-color;\r\n" + "      color: $header-text-color;\r\n"
				+ "      padding: 1em;  \r\n" + "      &:first-child { // show the first table header for mobile\r\n"
				+ "        display: table-cell;\r\n" + "        text-align: center;\r\n" + "      }\r\n"
				+ "      &:nth-child(2) { // show the second table header but replace the content with the data-th from the markup for mobile\r\n"
				+ "        display: table-cell;\r\n" + "        span {display:none;}\r\n"
				+ "        &:after {content:attr(data-th);}\r\n" + "      }\r\n" + "      @media (min-width: $breakpoint) {\r\n"
				+ "        &:nth-child(2) { // hide the data-th and show the normal header for tablet and desktop\r\n"
				+ "          span {display: block;}\r\n" + "          &:after {display: none;}\r\n" + "        }\r\n"
				+ "      }\r\n" + "    }\r\n" + "  \r\n" + "    td {\r\n"
				+ "      display: block; // display the table data as one block for mobile\r\n"
				+ "      word-wrap: break-word;\r\n" + "      max-width: 7em;\r\n" + "      &:first-child { \r\n"
				+ "        display: table-cell; // display the first one as a table cell (radio button) for mobile\r\n"
				+ "        text-align: center;\r\n" + "        border-right: $cell-border;\r\n" + "      }\r\n"
				+ "      @media (min-width: $breakpoint) {\r\n" + "        border: $cell-border;\r\n" + "      }\r\n"
				+ "    }\r\n" + "  \r\n" + "    th, td {\r\n" + "      text-align: left;\r\n" + "      margin: .5em 1em;  \r\n"
				+ "      @media (min-width: $breakpoint) {\r\n"
				+ "        display: table-cell; // show the table as a normal table for tablet and desktop\r\n"
				+ "        padding: 1em;\r\n" + "      }\r\n" + "    }  \r\n" + "  }    \r\n" + "}\r\n" + "\r\n"
				+ "// Include the mixin (with extra options as overrides)\r\n" + "\r\n" + "@include responstable(\r\n"
				+ "  $border-radius: $table-border-radius,\r\n" + "  $highlight-color: $table-highlight-color,\r\n"
				+ "  $header-background-color: $table-header-background-color,\r\n"
				+ "  $header-text-color: $table-header-text-color,\r\n" + "  $header-border: $table-header-border);\r\n" + "\r\n"
				+ "// General styles\r\n" + "\r\n" + "body {\r\n" + "  padding: 0 2em;\r\n"
				+ "  font-family: Arial, sans-serif;\r\n" + "  color: #024457;\r\n" + "  background: #f2f2f2;\r\n" + "}\r\n"
				+ "\r\n" + "h1 {\r\n" + "  font-family: Verdana;\r\n" + "  font-weight: normal;\r\n" + "  color: #024457;\r\n"
				+ "  span {color: #167F92;}\r\n" + "}";

	}

}