package com.droidmate.servlets.ajax;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.apk.APKExplorationStatus;
import com.droidmate.apk.APKInformation;
import com.droidmate.apk.APKInliningStatus;
import com.droidmate.hook.APKExplorationInfo;
import com.droidmate.hook.XMLLogReader;
import com.droidmate.settings.AjaxConstants;
import com.droidmate.settings.GUISettings;
import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class APKExploreHandler
 */
@WebServlet(urlPatterns = { "/APKExploreHandler" }, asyncSupported = true)
public class APKExploreHandler extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private GUISettings settings = new GUISettings();
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

		Runnable r = null;
		if (request.getParameter(AjaxConstants.EXPLORE_START) != null) {
			r = new Runnable() {
				@Override
				public void run() {
					user.setStatus(APKExplorationStatus.STARTED);
					if (!startDroidmate(user)) {
						user.setStatus(APKExplorationStatus.ERROR);
					} else {
						user.setStatus(APKExplorationStatus.FINISHED);
					}
				}
			};
		} else if (request.getParameter(AjaxConstants.EXPLORE_STOP) != null) {
			stopDroidmateForcibly();
			user.setStatus(APKExplorationStatus.ABORTED);
		} else if (request.getParameter(AjaxConstants.RETURN_TO_INDEX) != null) {
			stopDroidmateForcibly();
			user.clear();
			user.setStatus(APKExplorationStatus.ABORTED);
		} else if (request.getParameter(AjaxConstants.EXPLORE_RESTART) != null) {
			stopDroidmateForcibly();
			r = new Runnable() {
				@Override
				public void run() {
					user.setStatus(APKExplorationStatus.STARTED);
					if (!startDroidmate(user)) {
						user.setStatus(APKExplorationStatus.ERROR);
					} else {
						user.setStatus(APKExplorationStatus.FINISHED);
					}
				}
			};
		} else if (request.getParameter(AjaxConstants.EXPLORE_OPEN_REPORT_FOLDER) != null) {
			JSONObject o = new JSONObject();
			if (openExplorerWindow(settings.getOutputFolder())) {
				o.put("status", "success");
			} else {
				o.put("status", "error");
			}
			response.setContentType("application/json");
			response.getWriter().print(o);
			response.getWriter().flush();
		} else {
			System.out.println("Illegal POST request:");
			for (Entry<String, String[]> s : request.getParameterMap().entrySet()) {
				System.out.println(s.getKey() + " -> " + Arrays.toString(s.getValue()));
			}
			return;
		}

		if (r != null)
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
			if (request.getParameter(AjaxConstants.EXPLORE_GET_INFO_APK_NAME) != null) {
				if (logReader != null) {
					boolean found = false;
					for (APKExplorationInfo apk : logReader.getApksInfo()) {
						if (apk.getName().equalsIgnoreCase(request.getParameter(AjaxConstants.EXPLORE_GET_INFO_APK_NAME))) {
							out.print(apk.toJSONObject());
							found = true;
						}
					}
					if (!found) {
						out.print(APKExplorationInfo.getDummyObject());
					}
				} else {
					out.print(new JSONObject());
				}
			} else {
				JSONArray result = new JSONArray();
				if (logReader != null) {
					for (APKExplorationInfo apk : logReader.getApksInfo()) {
						result.put(apk.toJSONObject());
					}
				}

				out.print(result);
			}
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_ELEMENTS_SEEN) != null) {
			if (logReader != null) {
				out.print(logReader.getGlobalElementsSeen());
			} else {
				out.print(0);
			}
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_ELEMENTS_SEEN_HISTORY) != null) {
			response.setContentType("application/json");
			JSONArray result = new JSONArray();
			if (logReader != null) {
				for (Entry<Long, Integer> entry : logReader.getGlobalElementsSeenHistory().entrySet()) {
					JSONArray o = new JSONArray();
					o.put(Math.round(Math.round(entry.getKey() / 1000d)));
					o.put(entry.getValue());
					result.put(o);
				}
			}

			out.print(result);
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_SCREENS_SEEN) != null) {
			if (logReader != null) {
				out.print(logReader.getGlobalScreensSeen());
			} else {
				out.print(0);
			}
		} else if (request.getParameter("status") != null) {
			out.print(user.getStatus().getName());
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_SCREENS_SEEN_HISTORY) != null) {
			response.setContentType("application/json");
			JSONArray result = new JSONArray();
			if (logReader != null) {
				for (Entry<Long, Integer> entry : logReader.getGlobalScreensSeenHistory().entrySet()) {
					JSONArray o = new JSONArray();
					o.put(Math.round(Math.round(entry.getKey() / 1000d)));
					o.put(entry.getValue());
					result.put(o);
				}
			}

			out.print(result);
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_WIDGETS_EXPLORED) != null) {
			if (logReader != null) {
				out.print(logReader.getGlobalWidgetsExplored());
			} else {
				out.print(0);
			}
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_WIDGETS_EXPLORED_HISTORY) != null) {
			response.setContentType("application/json");
			JSONArray result = new JSONArray();
			if (logReader != null) {
				for (Entry<Long, Integer> entry : logReader.getGlobalWidgetsExploredHistory().entrySet()) {
					JSONArray o = new JSONArray();
					o.put(Math.round(Math.round(entry.getKey() / 1000d)));
					o.put(entry.getValue());
					result.put(o);
				}
			}

			out.print(result);
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_STARTING_TIME) != null) {
			response.setContentType("application/json");
			JSONObject result = new JSONObject();
			if (logReader != null) {
				result.put("status", "ok");
				result.put("timestamp", logReader.getGlobalStartingTime());
			} else {
				result.put("status", "not_started");
				result.put("timestamp", "");
			}
			out.print(result);
		} else {
			System.out.println("Illegal GET request:");
			for (Entry<String, String[]> s : request.getParameterMap().entrySet()) {
				System.out.println(s.getKey() + " -> " + Arrays.toString(s.getValue()));
			}
		}
		out.flush();
	}

	private boolean openExplorerWindow(Path path) {
		try {
			int exitCode = Runtime.getRuntime().exec("explorer.exe " + path).waitFor();
			return exitCode == 0 || exitCode == 1;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean startDroidmate(DroidMateUser user) {
		System.out.println("Starting droidmate...");

		Path droidMateRoot = settings.getDroidMatePath();
		String gradlewName = "/gradlew.bat";
		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			gradlewName = "/gradlew";
		}
		Path droidMateExecutable = Paths.get(droidMateRoot.toString(), gradlewName);

		Path inputAPKsPath = Paths.get(droidMateRoot.toString(), "/apks/inlined/");
		logFile = new File(droidMateRoot.toString(), "/dev1/logs/gui.xml");
		while (logFile.exists() && !logFile.delete()) {
			System.out.println("Log file deletion failed, trying again in 500ms...");
			try {
				killAdb();
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logReader = new XMLLogReader(logFile, user.getAPKS());

		try {
			FileUtils.deleteDirectory(inputAPKsPath.toFile());
			inputAPKsPath.toFile().mkdir();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		boolean restart = true;
		while (restart == true) {
			restart = false;
			// for each apk, copy it
			for (APKInformation apkInfo : user.getAPKS()) {
				if (apkInfo.isSelected()) {
					try {
						if (!apkInfo.isInlined()) {
							return false;
						}
						Path target = Paths.get(inputAPKsPath.toString(), apkInfo.getFile().getName());
						target.toFile().mkdirs();
						Files.copy(apkInfo.getInlinedPath(), target, StandardCopyOption.REPLACE_EXISTING);
					} catch (AccessDeniedException e) {
						killAdb();
						restart = true;
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
		}
		killAdb();

		ProcessBuilder pb = new ProcessBuilder(droidMateExecutable.toString(), "--stacktrace", ":projects:core:run", "--project-prop",
				"timeLimit=" + settings.getExplorationTimeout());
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

		List<String> consoleOutput = user.getDroidMateOutput();

		synchronized (consoleOutput) {
			consoleOutput.clear();
		}

		try {
			BufferedReader stdout = new BufferedReader(new InputStreamReader(droidmateProcess.getInputStream()));
			while ((s = stdout.readLine()) != null) {
				System.out.println(s);
				synchronized (consoleOutput) {
					consoleOutput.add(s);
				}
			}
		} catch (IOException ex) {
		}
		try {
			droidmateProcess.waitFor();
		} catch (InterruptedException e1) {
		}

		logReader.stopReading();
		System.out.println("Droidmate exit value: " + droidmateProcess.exitValue());
		try {
			droidmateProcess.getInputStream().close();
			droidmateProcess.getOutputStream().close();
			droidmateProcess.getErrorStream().close();
		} catch (Exception e) {
		}

		return droidmateProcess.exitValue() == 0;
	}

	private void killAdb() {
		System.out.println("Killing adb process...");
		Runtime rt = Runtime.getRuntime();
		try {
			if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
				rt.exec("taskkill /F /IM " + "adb.exe");
			else
				rt.exec("kill -9 " + "adb");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopDroidmateForcibly() {
		System.out.println("Stopping droidmate...");

		try {
			droidmateProcess.getInputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			droidmateProcess.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			droidmateProcess.getErrorStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			droidmateProcess.destroyForcibly().waitFor();
			System.out.println("Droidmate process has been killed.");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		killAdb();

	}

}