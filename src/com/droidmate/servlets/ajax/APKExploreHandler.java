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

		if (request.getParameter(AjaxConstants.EXPLORE_START) != null) {
			startDroidmate(user.getAPKS());
		} else if (request.getParameter(AjaxConstants.EXPLORE_STOP) != null) {
			stopDroidmateForcibly();
		} else if (request.getParameter(AjaxConstants.EXPLORE_RESTART) != null) {
			stopDroidmateForcibly();
			startDroidmate(user.getAPKS());
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		if(request.getParameter(AjaxConstants.EXPLORE_GET_INFO) != null) {
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			JSONArray result = new JSONArray();
			for(APKExplorationInfo apk : logReader.getApksInfo()) {
				result.put(apk.toJSONObject());
			}
			
			out.print(result);
			out.flush();
		}
	}

	private boolean startDroidmate(APKInformation[] apks) {
		GUISettings settings = new GUISettings();
		Path droidMateRoot = settings.getDroidMatePath();
		String gradlewName = "/gradlew.bat";
		if (System.getProperty("os.name").equals("Linux")) {
			gradlewName = "/gradlew";
		}
		Path droidMateExecutable = Paths.get(droidMateRoot.toString(), gradlewName);

		Path inputAPKsPath = Paths.get(droidMateRoot.toString(), "/apks/inlined/");
		logFile = new File(droidMateRoot.toString(), "/dev1/logs/gui.xml");
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
			try {
				Path inlinedAPK = Paths.get(apkInfo.getFile().getParent().toString(), "/inlined",
						FilenameUtils.removeExtension(apkInfo.getFile().getName()) + "-inlined.apk");
				Files.copy(inlinedAPK, Paths.get(inputAPKsPath.toString(), apkInfo.getFile().getName()),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		ProcessBuilder pb = new ProcessBuilder(droidMateExecutable.toString(), "--stacktrace", ":projects:core:run");
		pb.directory(droidMateRoot.toFile());
		pb.redirectErrorStream(true);
		try {
			droidmateProcess = pb.start();
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

}