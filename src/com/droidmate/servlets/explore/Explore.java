package com.droidmate.servlets.explore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.droidmate.apk.APKInformation;
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
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null || 
				request.getParameterValues("id[]") == null) {
			return;
		}

		// set up datatable from selected apks
		String[] requestedIDs = request.getParameterValues("id[]");
		DroidMateUser apkInfo = (DroidMateUser) session.getAttribute("user");
		
		for (String id : requestedIDs) {
			if (NumberUtils.isDigits(id) && NumberUtils.isNumber(id)) {
				int index = Integer.parseInt(id);
				if(index < apkInfo.getAPKS().size()) {
					apkInfo.getAPKS().get(index).setSelected(true);
				}
			}
		}

		// first visit?
		if (session.getAttribute("status") == null || session.getAttribute("status") == "failed") {
			session.setAttribute("status", "failed");
			// inline apks and start droidmate
			boolean status = inlineAPKS( apkInfo.getAPKPath());
			if (status)
				session.setAttribute("status", "inlined");

		}

		request.getRequestDispatcher("/WEB-INF/views/pages/explore/explore.jsp").forward(request, response);
	}

	private boolean inlineAPKS(Path path) {
		Path droidMatePath = Paths.get(System.getenv("DROIDMATE"), "/gradlew.bat");
		Path inputAPKsPath = Paths.get(System.getenv("DROIDMATE"), "/projects/apk-inliner/input-apks");

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
		pb.directory(Paths.get(System.getenv("DROIDMATE")).toFile());
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
