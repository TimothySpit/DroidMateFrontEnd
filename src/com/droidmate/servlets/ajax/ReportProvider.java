package com.droidmate.servlets.ajax;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		String html = request.getParameter(AjaxConstants.ReportProvider_SAVE_REPORT_HTML);
		if (html != null) {
			saveReport(html);
		} else {
			System.out.println("Illegal POST request in ReportProvider:");
			for (Entry<String, String[]> s : request.getParameterMap().entrySet()) {
				System.out.println(s.getKey() + " -> " + Arrays.toString(s.getValue()));
			}
			return;
		}
	}

	private boolean saveReport(String rawHtml) {
		String html = generateReport(rawHtml);
		File outputFolder = (new GUISettings()).getOutputFolder().toFile();
		
		File resourcesSourceFolder = new File(
				getServletContext().getRealPath("/resources"));
		try {
			System.out.println("Saving report...");
			System.out.println("Copying resource files from " + resourcesSourceFolder + " to " + (new File(outputFolder + File.separator + "resources")));
			// Copy resources
			FileUtils.copyDirectory(resourcesSourceFolder, new File(outputFolder + File.separator + "resources"), true);

			// Save report
			File reportFile = new File(
					outputFolder + "/Report_" + (new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())) + ".html");
			reportFile.createNewFile();
			PrintWriter writer = new PrintWriter(reportFile, "UTF-8");
			writer.println(html);
			writer.close();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private String generateReport(String rawHtml) {
		String html = rawHtml.replace("href=\"/DroidMate/", "href=\"");
		// Remove return to start button
		html = html.replace("<button class=\"btn btn-default\" id=\"back-to-index\" type=\"button\">Return to start</button>",
				"");
		html = html.replace("<button id=\"stopAllBtn\" class=\"btn btn-default\" type=\"button\">Stop\r\n"
				+ "						All</button>", "");

		return html;
	}
}
