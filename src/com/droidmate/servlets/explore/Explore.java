package com.droidmate.servlets.explore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.droidmate.settings.GUISettings;
import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class Explore
 */
@WebServlet("/Explore")
public class Explore extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Explore() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		// set up datatable from selected apks
		String[] requestedIDs = request.getParameterValues("id[]");
		
		//no apk was selected, redirect to index
		if (requestedIDs == null) {
			request.getRequestDispatcher("/WEB-INF/views/pages/index/index.jsp").forward(request, response);
		}
		for (String id : requestedIDs) {
			if (NumberUtils.isDigits(id) && NumberUtils.isNumber(id)) {
				int index = Integer.parseInt(id);
					if (index < user.getAPKS().size()) {
						user.getAPKS().get(index).setSelected(true);
						}
				}
			}
				
		//no valid apks were selected, redirect to index
		if (user.getSelectedAPKSCount() <= 0) {
			request.getRequestDispatcher("/WEB-INF/views/pages/index/index.jsp").forward(request, response);
		}

		if(!user.isApksInlined()) {
			boolean status = inlineAPKS(user.getAPKPath());
			if (status) {
				user.setApksInlined(status);
				user.setExplorationStated(startDroidMate());
			}
		}

		request.getRequestDispatcher("/WEB-INF/views/pages/explore/explore.jsp").forward(request, response);
	}



	private boolean startDroidMate() {
		
		return true;
	}

	private boolean inlineAPKS(Path path) {
		GUISettings settings = new GUISettings();
		Path droidMateRoot = settings.getDroidMatePath();
		Path droidMatePath = Paths.get(droidMateRoot.toString(), "/gradlew.bat");
		Path inputAPKsPath = Paths.get(droidMateRoot.toString(), "/projects/apk-inliner/input-apks");

		// empty inline directory
		try {
			FileUtils.deleteDirectory(inputAPKsPath.toFile());
			inputAPKsPath.toFile().mkdir();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// copy new apks for inlining
		try {
			FileUtils.copyDirectory(path.toFile(), inputAPKsPath.toFile());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// start inliner
		ProcessBuilder pb = new ProcessBuilder(droidMatePath.toString(), "--stacktrace", ":projects:core:prepareInlinedApks");
		pb.directory(droidMateRoot.toFile());
		pb.redirectErrorStream(true);
		try {
			Process p = pb.start();
			String s;
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = stdout.readLine()) != null) {
				System.out.println(s);
			}
			System.out.println("Exit value: " + p.waitFor());
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();

			if (p.exitValue() != 0)
				return false;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}