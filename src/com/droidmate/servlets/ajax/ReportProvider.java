package com.droidmate.servlets.ajax;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.droidmate.apk.APKInformation;
import com.droidmate.apk.ExplorationReport;
import com.droidmate.settings.AjaxConstants;
import com.droidmate.settings.GUISettings;
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
		}else
		if (request.getParameter(AjaxConstants.ReportProvider_SAVE_REPORT) != null) {
			String requestedAPK = (String) request.getParameter(AjaxConstants.ReportProvider_SAVE_REPORT);
			for (APKInformation apk : user.getAPKS()) {
				System.out.println("Saving report...");
				if (apk.getFile().getName().equals(requestedAPK)) {
					HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response) {
						private final StringWriter sw = new StringWriter();

						@Override
						public PrintWriter getWriter() throws IOException {
							return new PrintWriter(sw);
						}

						@Override
						public String toString() {
							return sw.toString();
						}
					};
					ExplorationReport report = apk.getReport();
					request.setAttribute("apk_name", apk.getFile().getName());
					request.setAttribute("elements_seen", Integer.toString(report.getElementsSeen()));
					request.setAttribute("apk_successful", report.isSuccess());
					request.getRequestDispatcher("/WEB-INF/views/pages/report/report.jsp").include(request, responseWrapper);
					String content = responseWrapper.toString();
					GUISettings settings = new GUISettings();
					File reportFile = new File(settings.getOutputFolder() + "/Report_"
							+ (new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())) + "_" + apk.getFile().getName() + ".html");
					reportFile.createNewFile();
					PrintWriter writer = new PrintWriter(reportFile, "UTF-8");
					writer.println(content);
					writer.close();
					break;
				}
			}
		}else {
			System.out.println("Illegal GET request:");
			for (Entry<String, String[]> s : request.getParameterMap().entrySet()) {
				System.out.println(s.getKey() + " -> " + Arrays.toString(s.getValue()));
			}
		}
	}
}
