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
import java.util.Arrays;
import java.util.Map.Entry;
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

		Runnable r;
		if (request.getParameter(AjaxConstants.EXPLORE_START) != null) {
			r = new Runnable() {
				@Override
				public void run() {
					startDroidmate(user.getAPKS());
				}
			};
		} else if (request.getParameter(AjaxConstants.EXPLORE_STOP) != null) {
			r = new Runnable() {
				@Override
				public void run() {
					stopDroidmateForcibly();
				}
			};
		} else if (request.getParameter(AjaxConstants.EXPLORE_RESTART) != null) {
			r = new Runnable() {
				@Override
				public void run() {
					stopDroidmateForcibly();
					startDroidmate(user.getAPKS());
				}
			};
		} else {
			System.out.println("Illegal POST request:");
			for (Entry<String, String[]> s : request.getParameterMap().entrySet()) {
				System.out.println(s.getKey() + " -> " + Arrays.toString(s.getValue()));
			}
			return;
		}

		(new Thread(r)).start();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);
		PrintWriter out = response.getWriter();

		if (request.getParameter(AjaxConstants.EXPLORE_GET_INFO) != null) {
			response.setContentType("application/json");
			JSONArray result = new JSONArray();
			if (logReader != null) {
				for (APKExplorationInfo apk : logReader.getApksInfo()) {
					result.put(apk.toJSONObject());
				}
			}

			out.print(result);
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_REPORT) != null) {
			String apkName = request.getParameter(AjaxConstants.EXPLORE_GET_REPORT);
			for (APKExplorationInfo apk : logReader.getApksInfo()) {
				if (apk.getName().equals(apkName)) {
					out.print(apk.getReportFile());
				}
			}
		} else {
			System.out.println("Illegal GET request:");
			for (Entry<String, String[]> s : request.getParameterMap().entrySet()) {
				System.out.println(s.getKey() + " -> " + Arrays.toString(s.getValue()));
			}
		}
		out.flush();
	}

	private boolean startDroidmate(APKInformation[] apks) {
		System.out.println("Starting droidmate...");

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

		ProcessBuilder pb = new ProcessBuilder(droidMateExecutable.toString(), "--stacktrace", ":projects:core:run",
				"--project-prop", "timeLimit=" + settings.getExplorationTimeout());
		pb.directory(droidMateRoot.toFile());
		pb.redirectErrorStream(true);

		try {
			droidmateProcess = pb.start();
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			return false;
		}
		logReader.startConcurrentReading();
		String s;
		try {
			BufferedReader stdout = new BufferedReader(new InputStreamReader(droidmateProcess.getInputStream()));
			while ((s = stdout.readLine()) != null) {
				System.out.println(s);
			}
		} catch (IOException ex) {}
		try {
			droidmateProcess.waitFor();
		} catch (InterruptedException e1) {}

		logReader.stopReading();
		System.out.println("Exit value: " + droidmateProcess.exitValue());
		try {
			droidmateProcess.getInputStream().close();
			droidmateProcess.getOutputStream().close();
			droidmateProcess.getErrorStream().close();
		} catch (Exception e) {
		}

		return droidmateProcess.exitValue() == 0;
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

}