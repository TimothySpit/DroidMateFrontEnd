package com.droidmate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.apk.APKInformation;

/**
 * Servlet implementation class apkListingStatic
 */
@WebServlet("/apkListingStatic")
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("selectedAPKS") == null) {
			return;
		}
		
		List<APKInformation> apkInfos = (List<APKInformation>) session.getAttribute("selectedAPKS");
		JSONObject apkData = new JSONObject();
		JSONArray ticks = new JSONArray();
		JSONArray sizes = new JSONArray();
		int counter = 0;
		for (Iterator<APKInformation> iterator = apkInfos.iterator(); iterator.hasNext();) {
			APKInformation apkInformation = (APKInformation) iterator.next();
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
