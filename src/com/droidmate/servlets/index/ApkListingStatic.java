package com.droidmate.servlets.index;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.apk.APKInformation;
import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class apkListingStatic
 */
@WebServlet("/ApkListingStatic")
public class ApkListingStatic extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ApkListingStatic() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		JSONObject apkData = new JSONObject();
		JSONArray ticks = new JSONArray();
		JSONArray sizes = new JSONArray();
		int counter = 0;
		for (APKInformation apkInformation : user.getAPKS()) {
			JSONArray size = new JSONArray();
			size.put(counter);
			size.put(apkInformation.getFile().length());
			sizes.put(size);

			JSONArray tick = new JSONArray();
			tick.put(counter);
			tick.put(apkInformation.getFile().getName());
			ticks.put(tick);
			counter++;
		}
		apkData.put("data", sizes);
		apkData.put("ticks", ticks);
		PrintWriter out = response.getWriter();
		out.print(apkData);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
