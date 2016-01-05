package com.droidmate.servlets.explore;

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
 * Servlet implementation class Exploration
 */
@WebServlet("/ExplorationData")
public class ExplorationData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExplorationData() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("apkInfo") == null) {
			return;
		}

		if (request.getParameter("filesCount") != null) {
			PrintWriter out = response.getWriter();
			List<APKInformation> apkInfo = (List<APKInformation>) session.getAttribute("apkInfo");
			JSONObject sizeObject = new JSONObject();
			int count = 0;
			for (APKInformation apkInformation : apkInfo) {
				if(apkInformation.isSelected()) {
					count++;
				}
			}
			sizeObject.put("count", count);
			out.print(sizeObject);
			out.flush();
		} else if (request.getParameter("apkTableData") != null) {
			List<APKInformation> apkInfos = (List<APKInformation>) session.getAttribute("apkInfo");
			JSONArray apkData = new JSONArray();
			for (Iterator<APKInformation> iterator = apkInfos.iterator(); iterator.hasNext();) {
				APKInformation apkInformation = (APKInformation) iterator.next();
				if(!apkInformation.isSelected())
					continue;
				JSONArray jsonInfo = new JSONArray();
				jsonInfo.put(apkInformation.getFile().getName());
				jsonInfo.put(apkInformation.getProgress());
				jsonInfo.put(apkInformation.getStatus());
				apkData.put(jsonInfo);
			}
			PrintWriter out = response.getWriter();
			JSONObject data = new JSONObject();
			data.put("data", apkData);
			out.print(data);
			out.flush();
		} else if (request.getParameter("update") != null) {
			List<APKInformation> apkInfos = (List<APKInformation>) session.getAttribute("apkInfo");
			String file = request.getParameter("update");
			for (Iterator<APKInformation> iterator = apkInfos.iterator(); iterator.hasNext();) {
				APKInformation apkInformation = (APKInformation) iterator.next();
				if (apkInformation.getFile().getName().equals(file)) {
					JSONObject data = new JSONObject();
					data.put("progress", apkInformation.getProgress());
					data.put("state", apkInformation.getStatus());
					PrintWriter out = response.getWriter();
					out.print(data);
					out.flush();
					break;
				}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
