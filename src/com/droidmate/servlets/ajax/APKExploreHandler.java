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
	private File reportFile;

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
			out.print(reportFile);
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
			logReader.startConcurrentReading();
			String s;
			BufferedReader stdout = new BufferedReader(new InputStreamReader(droidmateProcess.getInputStream()));
			while ((s = stdout.readLine()) != null) {
				System.out.println(s);
			}
			droidmateProcess.waitFor();

			logReader.stopReading();
			saveReport(settings.getOutputFolder());
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

	private void saveReport(Path path) {
		try {
			reportFile = Paths.get(path.toString(), "DroidmateReport-" + System.currentTimeMillis() + ".html").toFile();
			reportFile.createNewFile();
			PrintWriter writer = new PrintWriter(reportFile);
			writer.println(generateReport());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String generateReport() {
		if (logReader == null) {
			return "Droidmate did not produce any output!";
		}

		// http://johnsardine.com/freebies/dl-html-css/simple-little-tab/
		String css = "table {\r\n" + "	font-family:Arial, Helvetica, sans-serif;\r\n" + "	color:#666;\r\n"
				+ "	text-shadow: 1px 1px 0px #fff;\r\n" + "	background:#eaebec;\r\n" + "	margin:20px;\r\n"
				+ "	border:#ccc 1px solid;\r\n" + "\r\n" + "	-moz-border-radius:3px;\r\n"
				+ "	-webkit-border-radius:3px;\r\n" + "	border-radius:3px;\r\n" + "\r\n"
				+ "	-moz-box-shadow: 0 1px 2px #d1d1d1;\r\n" + "	-webkit-box-shadow: 0 1px 2px #d1d1d1;\r\n"
				+ "	box-shadow: 0 1px 2px #d1d1d1;\r\n" + "}\r\n" + "table th {\r\n" + "	padding:21px 25px 22px 25px;\r\n"
				+ "	border-top:1px solid #fafafa;\r\n" + "	border-bottom:1px solid #e0e0e0;\r\n" + "\r\n"
				+ "	background: #ededed;\r\n"
				+ "	background: -webkit-gradient(linear, left top, left bottom, from(#ededed), to(#ebebeb));\r\n"
				+ "	background: -moz-linear-gradient(top,  #ededed,  #ebebeb);\r\n" + "}\r\n" + "table th:first-child {\r\n"
				+ "	text-align: left;\r\n" + "	padding-left:20px;\r\n" + "}\r\n" + "table tr:first-child th:first-child {\r\n"
				+ "	-moz-border-radius-topleft:3px;\r\n" + "	-webkit-border-top-left-radius:3px;\r\n"
				+ "	border-top-left-radius:3px;\r\n" + "}\r\n" + "table tr:first-child th:last-child {\r\n"
				+ "	-moz-border-radius-topright:3px;\r\n" + "	-webkit-border-top-right-radius:3px;\r\n"
				+ "	border-top-right-radius:3px;\r\n" + "}\r\n" + "table tr {\r\n" + "	text-align: center;\r\n"
				+ "	padding-left:20px;\r\n" + "}\r\n" + "table td:first-child {\r\n" + "	text-align: left;\r\n"
				+ "	padding-left:20px;\r\n" + "	border-left: 0;\r\n" + "}\r\n" + "table td {\r\n" + "	padding:18px;\r\n"
				+ "	border-top: 1px solid #ffffff;\r\n" + "	border-bottom:1px solid #e0e0e0;\r\n"
				+ "	border-left: 1px solid #e0e0e0;\r\n" + "\r\n" + "	background: #fafafa;\r\n"
				+ "	background: -webkit-gradient(linear, left top, left bottom, from(#fbfbfb), to(#fafafa));\r\n"
				+ "	background: -moz-linear-gradient(top,  #fbfbfb,  #fafafa);\r\n" + "}\r\n" + "table tr.even td {\r\n"
				+ "	background: #f6f6f6;\r\n"
				+ "	background: -webkit-gradient(linear, left top, left bottom, from(#f8f8f8), to(#f6f6f6));\r\n"
				+ "	background: -moz-linear-gradient(top,  #f8f8f8,  #f6f6f6);\r\n" + "}\r\n" + "table tr:last-child td {\r\n"
				+ "	border-bottom:0;\r\n" + "}\r\n" + "table tr:last-child td:first-child {\r\n"
				+ "	-moz-border-radius-bottomleft:3px;\r\n" + "	-webkit-border-bottom-left-radius:3px;\r\n"
				+ "	border-bottom-left-radius:3px;\r\n" + "}\r\n" + "table tr:last-child td:last-child {\r\n"
				+ "	-moz-border-radius-bottomright:3px;\r\n" + "	-webkit-border-bottom-right-radius:3px;\r\n"
				+ "	border-bottom-right-radius:3px;\r\n" + "}\r\n" + "table tr:hover td {\r\n" + "	background: #f2f2f2;\r\n"
				+ "	background: -webkit-gradient(linear, left top, left bottom, from(#f2f2f2), to(#f0f0f0));\r\n"
				+ "	background: -moz-linear-gradient(top,  #f2f2f2,  #f0f0f0);	\r\n" + "}";

		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html><html><head><title>Droidmate report</title><style>");
		html.append(css);
		html.append("</style></head><body><table><tr><th>Name</th><th>Elements seen</th><th>Success</th></tr>");
		for (APKExplorationInfo apk : logReader.getApksInfo()) {
			html.append("<tr><td>");
			html.append(apk.getName());
			html.append("</td><td>");
			html.append(apk.getElementsSeen());
			html.append("</td><td>");
			if (apk.isSuccess()) {
				html.append(
						"<img alt=\"Success\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAAsTAAALEwEAmpwYAAAKT2lDQ1BQaG90b3Nob3AgSUNDIHByb2ZpbGUAAHjanVNnVFPpFj333vRCS4iAlEtvUhUIIFJCi4AUkSYqIQkQSoghodkVUcERRUUEG8igiAOOjoCMFVEsDIoK2AfkIaKOg6OIisr74Xuja9a89+bN/rXXPues852zzwfACAyWSDNRNYAMqUIeEeCDx8TG4eQuQIEKJHAAEAizZCFz/SMBAPh+PDwrIsAHvgABeNMLCADATZvAMByH/w/qQplcAYCEAcB0kThLCIAUAEB6jkKmAEBGAYCdmCZTAKAEAGDLY2LjAFAtAGAnf+bTAICd+Jl7AQBblCEVAaCRACATZYhEAGg7AKzPVopFAFgwABRmS8Q5ANgtADBJV2ZIALC3AMDOEAuyAAgMADBRiIUpAAR7AGDIIyN4AISZABRG8lc88SuuEOcqAAB4mbI8uSQ5RYFbCC1xB1dXLh4ozkkXKxQ2YQJhmkAuwnmZGTKBNA/g88wAAKCRFRHgg/P9eM4Ors7ONo62Dl8t6r8G/yJiYuP+5c+rcEAAAOF0ftH+LC+zGoA7BoBt/qIl7gRoXgugdfeLZrIPQLUAoOnaV/Nw+H48PEWhkLnZ2eXk5NhKxEJbYcpXff5nwl/AV/1s+X48/Pf14L7iJIEyXYFHBPjgwsz0TKUcz5IJhGLc5o9H/LcL//wd0yLESWK5WCoU41EScY5EmozzMqUiiUKSKcUl0v9k4t8s+wM+3zUAsGo+AXuRLahdYwP2SycQWHTA4vcAAPK7b8HUKAgDgGiD4c93/+8//UegJQCAZkmScQAAXkQkLlTKsz/HCAAARKCBKrBBG/TBGCzABhzBBdzBC/xgNoRCJMTCQhBCCmSAHHJgKayCQiiGzbAdKmAv1EAdNMBRaIaTcA4uwlW4Dj1wD/phCJ7BKLyBCQRByAgTYSHaiAFiilgjjggXmYX4IcFIBBKLJCDJiBRRIkuRNUgxUopUIFVIHfI9cgI5h1xGupE7yAAygvyGvEcxlIGyUT3UDLVDuag3GoRGogvQZHQxmo8WoJvQcrQaPYw2oefQq2gP2o8+Q8cwwOgYBzPEbDAuxsNCsTgsCZNjy7EirAyrxhqwVqwDu4n1Y8+xdwQSgUXACTYEd0IgYR5BSFhMWE7YSKggHCQ0EdoJNwkDhFHCJyKTqEu0JroR+cQYYjIxh1hILCPWEo8TLxB7iEPENyQSiUMyJ7mQAkmxpFTSEtJG0m5SI+ksqZs0SBojk8naZGuyBzmULCAryIXkneTD5DPkG+Qh8lsKnWJAcaT4U+IoUspqShnlEOU05QZlmDJBVaOaUt2ooVQRNY9aQq2htlKvUYeoEzR1mjnNgxZJS6WtopXTGmgXaPdpr+h0uhHdlR5Ol9BX0svpR+iX6AP0dwwNhhWDx4hnKBmbGAcYZxl3GK+YTKYZ04sZx1QwNzHrmOeZD5lvVVgqtip8FZHKCpVKlSaVGyovVKmqpqreqgtV81XLVI+pXlN9rkZVM1PjqQnUlqtVqp1Q61MbU2epO6iHqmeob1Q/pH5Z/YkGWcNMw09DpFGgsV/jvMYgC2MZs3gsIWsNq4Z1gTXEJrHN2Xx2KruY/R27iz2qqaE5QzNKM1ezUvOUZj8H45hx+Jx0TgnnKKeX836K3hTvKeIpG6Y0TLkxZVxrqpaXllirSKtRq0frvTau7aedpr1Fu1n7gQ5Bx0onXCdHZ4/OBZ3nU9lT3acKpxZNPTr1ri6qa6UbobtEd79up+6Ynr5egJ5Mb6feeb3n+hx9L/1U/W36p/VHDFgGswwkBtsMzhg8xTVxbzwdL8fb8VFDXcNAQ6VhlWGX4YSRudE8o9VGjUYPjGnGXOMk423GbcajJgYmISZLTepN7ppSTbmmKaY7TDtMx83MzaLN1pk1mz0x1zLnm+eb15vft2BaeFostqi2uGVJsuRaplnutrxuhVo5WaVYVVpds0atna0l1rutu6cRp7lOk06rntZnw7Dxtsm2qbcZsOXYBtuutm22fWFnYhdnt8Wuw+6TvZN9un2N/T0HDYfZDqsdWh1+c7RyFDpWOt6azpzuP33F9JbpL2dYzxDP2DPjthPLKcRpnVOb00dnF2e5c4PziIuJS4LLLpc+Lpsbxt3IveRKdPVxXeF60vWdm7Obwu2o26/uNu5p7ofcn8w0nymeWTNz0MPIQ+BR5dE/C5+VMGvfrH5PQ0+BZ7XnIy9jL5FXrdewt6V3qvdh7xc+9j5yn+M+4zw33jLeWV/MN8C3yLfLT8Nvnl+F30N/I/9k/3r/0QCngCUBZwOJgUGBWwL7+Hp8Ib+OPzrbZfay2e1BjKC5QRVBj4KtguXBrSFoyOyQrSH355jOkc5pDoVQfujW0Adh5mGLw34MJ4WHhVeGP45wiFga0TGXNXfR3ENz30T6RJZE3ptnMU85ry1KNSo+qi5qPNo3ujS6P8YuZlnM1VidWElsSxw5LiquNm5svt/87fOH4p3iC+N7F5gvyF1weaHOwvSFpxapLhIsOpZATIhOOJTwQRAqqBaMJfITdyWOCnnCHcJnIi/RNtGI2ENcKh5O8kgqTXqS7JG8NXkkxTOlLOW5hCepkLxMDUzdmzqeFpp2IG0yPTq9MYOSkZBxQqohTZO2Z+pn5mZ2y6xlhbL+xW6Lty8elQfJa7OQrAVZLQq2QqboVFoo1yoHsmdlV2a/zYnKOZarnivN7cyzytuQN5zvn//tEsIS4ZK2pYZLVy0dWOa9rGo5sjxxedsK4xUFK4ZWBqw8uIq2Km3VT6vtV5eufr0mek1rgV7ByoLBtQFr6wtVCuWFfevc1+1dT1gvWd+1YfqGnRs+FYmKrhTbF5cVf9go3HjlG4dvyr+Z3JS0qavEuWTPZtJm6ebeLZ5bDpaql+aXDm4N2dq0Dd9WtO319kXbL5fNKNu7g7ZDuaO/PLi8ZafJzs07P1SkVPRU+lQ27tLdtWHX+G7R7ht7vPY07NXbW7z3/T7JvttVAVVN1WbVZftJ+7P3P66Jqun4lvttXa1ObXHtxwPSA/0HIw6217nU1R3SPVRSj9Yr60cOxx++/p3vdy0NNg1VjZzG4iNwRHnk6fcJ3/ceDTradox7rOEH0x92HWcdL2pCmvKaRptTmvtbYlu6T8w+0dbq3nr8R9sfD5w0PFl5SvNUyWna6YLTk2fyz4ydlZ19fi753GDborZ752PO32oPb++6EHTh0kX/i+c7vDvOXPK4dPKy2+UTV7hXmq86X23qdOo8/pPTT8e7nLuarrlca7nuer21e2b36RueN87d9L158Rb/1tWeOT3dvfN6b/fF9/XfFt1+cif9zsu72Xcn7q28T7xf9EDtQdlD3YfVP1v+3Njv3H9qwHeg89HcR/cGhYPP/pH1jw9DBY+Zj8uGDYbrnjg+OTniP3L96fynQ89kzyaeF/6i/suuFxYvfvjV69fO0ZjRoZfyl5O/bXyl/erA6xmv28bCxh6+yXgzMV70VvvtwXfcdx3vo98PT+R8IH8o/2j5sfVT0Kf7kxmTk/8EA5jz/GMzLdsAAAAgY0hSTQAAeiUAAICDAAD5/wAAgOkAAHUwAADqYAAAOpgAABdvkl/FRgAABOZJREFUeNrsWz1T6kAUPWT4AdgzY+xfEWsKwwy90DOjVJRCRyd26ZSSCp2xN/bMGIvU8g+MM+lffsIrvHkvLHfDhmxIhLczjowm7D1n7/fu1lDwaE5aJgAbgAnggv5sSx736Pc7gACAFzp+UKR8tYJAdwFcJoDnGQER8xo6vltZAmilrwFcaQCdRsYTgIfQ8aNKENCctBoA7gn8PscjgHFeImo5wU8B3ABoZFjB+OdL+N8paY6ZQYMiALPQ8ad7JaA5aVkAFgAsBcAuOTVPdbVIq2xyml0FQlYABqHjrwonoDlpjUjlt6nnbBeBUgi/UTCzcej4D4UR0Jy0FluEuNPpoCSaMQJwm0Z+6PgDrQTQxG8pKu8S+4XGbCHi3JN5yEyirbIQtZzgI7I9FyUMyjcWEiesRIKhMI8M/ArAeVngAYDmPidZxGGR7NiZALJ5S6Ly7X2p/BYSAgDtRBq9RgJhyG4CKd4+k5PZs0nInLQ0OtRSws4Ht/Kh4/dQ4dGctN4kxdY5F5ZlJrCQJRuo/uhJfMJCyQdQemsx3r5XVHzX7BMiIiFi/ME01QQo5H0yYaVXprfPESJfmIU8Sy5kXXjgngHvVhl8fzlMFk/Bc2cexCGyOWm5QrIUV66DDQ2g1f/NzHFWhXDHALcJjMX4qrvnztyljPGTef0k1oKkDxhxuX1FwU9TEjQLwEt/OVyQ7HfMMyPOCV4xDz5UEPxiSzEUj+v+cjiiylQcV2sEkMMwmYQnqiD4LJ2nW7vzK2JIMAnzXw24ZF6e/XDwsdOzJVgukwSImVOgq5lRIvi/PoGwiL7MBgCDPKXJFDuHAD4Nk9mctExDkje/HxD4KAWTbYBvOHoHuPIcJtPAv+2qpP1HBwLeS2SGEeMHLrhqMDgQ8HFRlIqtzkWADLn36rkzjyoKvs3IthEJ6szLXxLhupSBWcLfPcq9vYqBX6lgMxSFu6fSksu9bQBv/eXwuuLgodQQYYQbSQqljY7LLiSUCV5VA24zfF8mEsoGLyPgVLD7RsbvVCKhJPCnHAGi8zKF2nqXkUpCiSsvJn2eofAQdJJQstqbnAa8M0VCQ8ijtZBQJnjCJBLwXpckPjbl0J4mTQCl3GU6PK7oCwwJyAsAoEm0kFC2t2dqnm8fQI1DUQu6ic89DaaAksGLmOKiLzAkpaJJ+4OgfLpdMgm5wBMWkyv5YwJemfdu4g80cVkk5F35NSyJ8Qqsb4x8MiydJHsD/eXQwnc/vvFTwEs2R4LQ8c/ETPCJeX+tBtizJuhYeUic7xOXCj9ydQAxuG8StIAn2bla5mGDAIoGHAkbp0QKJkHXyrOyQ9jwEVPhMQOqG++i7IEEbeBJ5i7z/WNpNUjMcLsoC9EUCiBBJ3gT/ImQmdjwlZ0R+gC/7cyeu9MQHXSCb4DfOV6Fjn+u2hDhzgJZElbzaoJOmwfkrbuBckeI9tLGXDopO3e3IwlawZNsXNEzlu111hS+kIujLr6PyOYxB91q/yIBn3quUeWs8AfkR2V73AkS2jt4SekoeQAG8a6NBocnm4u1+6wEyJxKvIrSw9LUU7xMpNgBgNfnztzVpPJd5Dws/f+4/A5OJq2xUfSFCZPm3++FCUGIEY71yowg0HFemhIEneIYr80xq3WcFycZIkY4tquzKfH5uC5PbwldMRGVuz7/ZwDxmLv5ci3L1wAAAABJRU5ErkJggg==\">");
			} else {
				html.append(
						"<img alt=\"Failure\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAAsTAAALEwEAmpwYAAAKT2lDQ1BQaG90b3Nob3AgSUNDIHByb2ZpbGUAAHjanVNnVFPpFj333vRCS4iAlEtvUhUIIFJCi4AUkSYqIQkQSoghodkVUcERRUUEG8igiAOOjoCMFVEsDIoK2AfkIaKOg6OIisr74Xuja9a89+bN/rXXPues852zzwfACAyWSDNRNYAMqUIeEeCDx8TG4eQuQIEKJHAAEAizZCFz/SMBAPh+PDwrIsAHvgABeNMLCADATZvAMByH/w/qQplcAYCEAcB0kThLCIAUAEB6jkKmAEBGAYCdmCZTAKAEAGDLY2LjAFAtAGAnf+bTAICd+Jl7AQBblCEVAaCRACATZYhEAGg7AKzPVopFAFgwABRmS8Q5ANgtADBJV2ZIALC3AMDOEAuyAAgMADBRiIUpAAR7AGDIIyN4AISZABRG8lc88SuuEOcqAAB4mbI8uSQ5RYFbCC1xB1dXLh4ozkkXKxQ2YQJhmkAuwnmZGTKBNA/g88wAAKCRFRHgg/P9eM4Ors7ONo62Dl8t6r8G/yJiYuP+5c+rcEAAAOF0ftH+LC+zGoA7BoBt/qIl7gRoXgugdfeLZrIPQLUAoOnaV/Nw+H48PEWhkLnZ2eXk5NhKxEJbYcpXff5nwl/AV/1s+X48/Pf14L7iJIEyXYFHBPjgwsz0TKUcz5IJhGLc5o9H/LcL//wd0yLESWK5WCoU41EScY5EmozzMqUiiUKSKcUl0v9k4t8s+wM+3zUAsGo+AXuRLahdYwP2SycQWHTA4vcAAPK7b8HUKAgDgGiD4c93/+8//UegJQCAZkmScQAAXkQkLlTKsz/HCAAARKCBKrBBG/TBGCzABhzBBdzBC/xgNoRCJMTCQhBCCmSAHHJgKayCQiiGzbAdKmAv1EAdNMBRaIaTcA4uwlW4Dj1wD/phCJ7BKLyBCQRByAgTYSHaiAFiilgjjggXmYX4IcFIBBKLJCDJiBRRIkuRNUgxUopUIFVIHfI9cgI5h1xGupE7yAAygvyGvEcxlIGyUT3UDLVDuag3GoRGogvQZHQxmo8WoJvQcrQaPYw2oefQq2gP2o8+Q8cwwOgYBzPEbDAuxsNCsTgsCZNjy7EirAyrxhqwVqwDu4n1Y8+xdwQSgUXACTYEd0IgYR5BSFhMWE7YSKggHCQ0EdoJNwkDhFHCJyKTqEu0JroR+cQYYjIxh1hILCPWEo8TLxB7iEPENyQSiUMyJ7mQAkmxpFTSEtJG0m5SI+ksqZs0SBojk8naZGuyBzmULCAryIXkneTD5DPkG+Qh8lsKnWJAcaT4U+IoUspqShnlEOU05QZlmDJBVaOaUt2ooVQRNY9aQq2htlKvUYeoEzR1mjnNgxZJS6WtopXTGmgXaPdpr+h0uhHdlR5Ol9BX0svpR+iX6AP0dwwNhhWDx4hnKBmbGAcYZxl3GK+YTKYZ04sZx1QwNzHrmOeZD5lvVVgqtip8FZHKCpVKlSaVGyovVKmqpqreqgtV81XLVI+pXlN9rkZVM1PjqQnUlqtVqp1Q61MbU2epO6iHqmeob1Q/pH5Z/YkGWcNMw09DpFGgsV/jvMYgC2MZs3gsIWsNq4Z1gTXEJrHN2Xx2KruY/R27iz2qqaE5QzNKM1ezUvOUZj8H45hx+Jx0TgnnKKeX836K3hTvKeIpG6Y0TLkxZVxrqpaXllirSKtRq0frvTau7aedpr1Fu1n7gQ5Bx0onXCdHZ4/OBZ3nU9lT3acKpxZNPTr1ri6qa6UbobtEd79up+6Ynr5egJ5Mb6feeb3n+hx9L/1U/W36p/VHDFgGswwkBtsMzhg8xTVxbzwdL8fb8VFDXcNAQ6VhlWGX4YSRudE8o9VGjUYPjGnGXOMk423GbcajJgYmISZLTepN7ppSTbmmKaY7TDtMx83MzaLN1pk1mz0x1zLnm+eb15vft2BaeFostqi2uGVJsuRaplnutrxuhVo5WaVYVVpds0atna0l1rutu6cRp7lOk06rntZnw7Dxtsm2qbcZsOXYBtuutm22fWFnYhdnt8Wuw+6TvZN9un2N/T0HDYfZDqsdWh1+c7RyFDpWOt6azpzuP33F9JbpL2dYzxDP2DPjthPLKcRpnVOb00dnF2e5c4PziIuJS4LLLpc+Lpsbxt3IveRKdPVxXeF60vWdm7Obwu2o26/uNu5p7ofcn8w0nymeWTNz0MPIQ+BR5dE/C5+VMGvfrH5PQ0+BZ7XnIy9jL5FXrdewt6V3qvdh7xc+9j5yn+M+4zw33jLeWV/MN8C3yLfLT8Nvnl+F30N/I/9k/3r/0QCngCUBZwOJgUGBWwL7+Hp8Ib+OPzrbZfay2e1BjKC5QRVBj4KtguXBrSFoyOyQrSH355jOkc5pDoVQfujW0Adh5mGLw34MJ4WHhVeGP45wiFga0TGXNXfR3ENz30T6RJZE3ptnMU85ry1KNSo+qi5qPNo3ujS6P8YuZlnM1VidWElsSxw5LiquNm5svt/87fOH4p3iC+N7F5gvyF1weaHOwvSFpxapLhIsOpZATIhOOJTwQRAqqBaMJfITdyWOCnnCHcJnIi/RNtGI2ENcKh5O8kgqTXqS7JG8NXkkxTOlLOW5hCepkLxMDUzdmzqeFpp2IG0yPTq9MYOSkZBxQqohTZO2Z+pn5mZ2y6xlhbL+xW6Lty8elQfJa7OQrAVZLQq2QqboVFoo1yoHsmdlV2a/zYnKOZarnivN7cyzytuQN5zvn//tEsIS4ZK2pYZLVy0dWOa9rGo5sjxxedsK4xUFK4ZWBqw8uIq2Km3VT6vtV5eufr0mek1rgV7ByoLBtQFr6wtVCuWFfevc1+1dT1gvWd+1YfqGnRs+FYmKrhTbF5cVf9go3HjlG4dvyr+Z3JS0qavEuWTPZtJm6ebeLZ5bDpaql+aXDm4N2dq0Dd9WtO319kXbL5fNKNu7g7ZDuaO/PLi8ZafJzs07P1SkVPRU+lQ27tLdtWHX+G7R7ht7vPY07NXbW7z3/T7JvttVAVVN1WbVZftJ+7P3P66Jqun4lvttXa1ObXHtxwPSA/0HIw6217nU1R3SPVRSj9Yr60cOxx++/p3vdy0NNg1VjZzG4iNwRHnk6fcJ3/ceDTradox7rOEH0x92HWcdL2pCmvKaRptTmvtbYlu6T8w+0dbq3nr8R9sfD5w0PFl5SvNUyWna6YLTk2fyz4ydlZ19fi753GDborZ752PO32oPb++6EHTh0kX/i+c7vDvOXPK4dPKy2+UTV7hXmq86X23qdOo8/pPTT8e7nLuarrlca7nuer21e2b36RueN87d9L158Rb/1tWeOT3dvfN6b/fF9/XfFt1+cif9zsu72Xcn7q28T7xf9EDtQdlD3YfVP1v+3Njv3H9qwHeg89HcR/cGhYPP/pH1jw9DBY+Zj8uGDYbrnjg+OTniP3L96fynQ89kzyaeF/6i/suuFxYvfvjV69fO0ZjRoZfyl5O/bXyl/erA6xmv28bCxh6+yXgzMV70VvvtwXfcdx3vo98PT+R8IH8o/2j5sfVT0Kf7kxmTk/8EA5jz/GMzLdsAAAAgY0hSTQAAeiUAAICDAAD5/wAAgOkAAHUwAADqYAAAOpgAABdvkl/FRgAABPJJREFUeNrsW+1x4jAQfXiuAGZSgP3DBZAKAhWEuIEkFUAqCK4AUgFJA8SpIE4FuAD/sAtghhLuR5acWVaybMs2c0QzNzdDQNr3tJ/SaoCWx8b1PQBjAB6AG/p4rPh6TP9/AcgAxEGeZm3KN2gJ9BTAbQF4k5ERMR9BnkZnSwDt9AOAewugdWS8AVgFebo/CwI2rj8EsCTwXY5XAE9NiRg0BL8AMAMwrLCDh385+5tLmuNV0KA9gJcgTxedErBx/RGANYCRAeCInFpsulukVWNymlMDQhIAj0GeJq0TsHH9Oal8mXq+1BFIQ/jMwMyegjxdtUbAxvXXJUKENh2UQjPmAJ515Ad5+miVAFr4U6PyEbHfasxmEWdJ5qEyiYnJRgwagt+T7UXoYVC+sVY4YSMSHIN1VOATANd9gQcAWvuaZOFjRLKjNgFk8yOFyk+6UvkSEjIAk0IafUQCYahuAhpvX8nJdGwSKietjA4DTdjZSsVKkKcT/uHu208U43UGILqyFA2qzL9x/U9FsXUtheU/ijXXCpu/EwR7ptB0MsfO9cOrBlkarbFQhL31zvVXAEJGxJ3Cb63JX+g1YCMvuCcGMwb+0yAbTABMqmpDlfmv8vRaCJNbITqEPG12hJA3ExZ5FBzes4FwP96YANkGDwCjnesvBcco+anZhsnBo8BSYC3ioW73LyMzHcYkVAR/GHM+N8nMQ/SQO3aH7b7oQYXPpjXMuZSEmuB1MkmyPxS1oKgB0o6Gilhf98BDSUJD8KJMJHsoaYxEwL3wxRXsjxMSLIAvq0z5uD8igHJqT0h49po63woJFsFnmkyRk+AR5h8NuBV++6JZLLKlCRZ3XieThOW2SADPnDLdYQbF9NASCTbAh7o8g7BwDRkDgENJg1d1hynDS9D/SAyzzUgwA89R5M1fhotPeiYhIRlMhoRp7ChCWmwyI6ldXyRUTbElTJ6Df9dVRfs3ztt7IqFyfUGYuB+4cWyEuI5JqFVcqbBJPqBWjO+IhCbgJWxjSQPyutK1TEJT8CI2x7aULZFgA7w4HFz4sE5AS4VN5UOVJgS4ZwbeJgmuREBso9ZvGbwtEji22LFx2NEReBskeJIGfAlFwvBMwdcmgTBxAr4cReIzPmPwdUmQMGWOoki4MZy0L/BFEt4Nvythih06MuJaMDXY/WXP4H92lm6PysZUKPoyR1EqenQ/qFP9uaUMz0bG+KwzBcLiSeXxgYAP4XezCmw2OcywlTbrZJKwfPwQQLco3AweNNHAswH+Kk/3FmsHT7H7Hk4vfLLDbVcxD3gTfj9vwWZPCpuWq8gH4bM3KRV+lWyLGGx8aFJW1VkgIVPsvnS1vjohQHGBAMhdIlEbJW1DEiJD2Y8ufHgq/ITvXoAj53K4RWGCrtqo52uSsOJzk8zcMe7BLkyPCCBmpFuUtWAKoaGQcdXDjIokJGCXNCSr1OXywg98VT1CWyHJScD67kpaZIDvG5tFEw+maZE52PLRrZCmrzEJWCeJjoARLqRJ6rdNruaEMYC7tpqiawAfUlE0rrphJr3CW6hbZe/67hYlh/euklGye2UUUAyVNx4B2PIQ2TH4KfmqkabW0I7fdnlLTqaYG7y2RUShsOn2wYRhdACrK/6/JzNMoMt8NMUEXeASn80Ju3WZDycFIua4tKezmvh8WY+nS0LXgYizez7/dwB5Abd0TDmcnwAAAABJRU5ErkJggg==\">");
			}
			html.append("</td></tr>");
		}
		html.append("</table>");
		html.append("</body></html>");

		return html.toString();
	}

}