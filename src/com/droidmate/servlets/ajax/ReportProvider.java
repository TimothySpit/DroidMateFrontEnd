package com.droidmate.servlets.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.droidmate.apk.APKInformation;
import com.droidmate.apk.ExplorationReport;
import com.droidmate.settings.AjaxConstants;
import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class ReportProvider
 */
@WebServlet("/ReportProvider")
public class ReportProvider extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReportProvider() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);
		if (request.getParameter(AjaxConstants.ReportProvider_GET_REPORT) != null) {
			String requestedAPK = (String) request.getParameter(AjaxConstants.ReportProvider_GET_REPORT);
			for (APKInformation apk : user.getAPKS()) {
				if (apk.getFile().getName().equals(requestedAPK)) {
					ExplorationReport report = apk.getReport();
					request.setAttribute("apk_name", apk.getFile().getName());
					request.setAttribute("elements_seen", Integer.toString(report.getElementsSeen()));
					request.setAttribute("apk_successful", report.isSuccess());
					break;
				}
			}
			
			request.getRequestDispatcher("/WEB-INF/views/pages/report/report.jsp").forward(request, response);
		}
  	}

}