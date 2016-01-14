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
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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

		Runnable r = null;
		if (request.getParameter(AjaxConstants.EXPLORE_START) != null) {
			r = new Runnable() {
				@Override
				public void run() {
					if (!startDroidmate(user))
						user.setExplorationStarted(false);
					else
						user.setExplorationStarted(true);
				}
			};
		} else if (request.getParameter(AjaxConstants.EXPLORE_STOP) != null) {
			stopDroidmateForcibly();
			user.setExplorationStarted(false);
		} else if (request.getParameter(AjaxConstants.EXPLORE_RESTART) != null) {
			stopDroidmateForcibly();
			r = new Runnable() {
				@Override
				public void run() {
					if (!startDroidmate(user))
						user.setExplorationStarted(false);
				}
			};
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
					if(!found) {
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
				int elementsSeen = 0;
				for (APKExplorationInfo apk : logReader.getApksInfo()) {
					elementsSeen += apk.getElementsSeen();
				}
				out.print(elementsSeen);
			} else {
				out.print(0);
			}
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_ELEMENTS_SEEN_HISTORY) != null) {
			JSONArray result = new JSONArray();
			if (logReader != null) {
				for (Entry<Long, Integer> entry : logReader.getGlobalElementsSeenHistory().entrySet()) {
					JSONObject o = new JSONObject();
					o.put("time", entry.getKey());
					o.put("elementsSeen", entry.getValue());
					result.put(o);
				}
			}

			out.print(result);
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_SCREENS_SEEN) != null) {
			if (logReader != null) {
				int screensSeen = 0;
				for (APKExplorationInfo apk : logReader.getApksInfo()) {
					screensSeen += apk.getScreensSeen();
				}
				out.print(screensSeen);
			} else {
				out.print(0);
			}
		} else if (request.getParameter(AjaxConstants.EXPLORE_GET_GLOBAL_SCREENS_SEEN_HISTORY) != null) {
			JSONArray result = new JSONArray();
			if (logReader != null) {
				for (Entry<Long, Integer> entry : logReader.getGlobalScreensSeenHistory().entrySet()) {
					JSONObject o = new JSONObject();
					o.put("time", entry.getKey());
					o.put("screensSeen", entry.getValue());
					result.put(o);
				}
			}

			out.print(result);
		}else {
			System.out.println("Illegal GET request:");
			for (Entry<String, String[]> s : request.getParameterMap().entrySet()) {
				System.out.println(s.getKey() + " -> " + Arrays.toString(s.getValue()));
			}
		}
		out.flush();
	}

	private boolean startDroidmate(DroidMateUser user) {
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

						Path inlinedAPK = Paths.get(apkInfo.getFile().getParent().toString(), "/inlined",
								FilenameUtils.removeExtension(apkInfo.getFile().getName()) + "-inlined.apk");
						Path target = Paths.get(inputAPKsPath.toString(), apkInfo.getFile().getName());
						target.toFile().mkdirs();
						Files.copy(inlinedAPK, target, StandardCopyOption.REPLACE_EXISTING);
					} catch (AccessDeniedException e) {
						Runtime rt = Runtime.getRuntime();
						try {
							if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
								rt.exec("taskkill /F /IM " + "adb.exe");
							else
								rt.exec("kill -9 " + "adb");
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						restart = true;
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
		}
		Runtime rt = Runtime.getRuntime();
		try {
			if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
				rt.exec("taskkill /F /IM " + "adb.exe");
			else
				rt.exec("kill -9 " + "adb");
		} catch (IOException e) {
			e.printStackTrace();
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
		} catch (IOException ex) {
		}
		try {
			droidmateProcess.waitFor();
		} catch (InterruptedException e1) {
		}

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
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Runtime rt = Runtime.getRuntime();
		try {
			if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
				rt.exec("taskkill /F /IM " + "adb.exe");
			else
				rt.exec("kill -9 " + "adb");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}