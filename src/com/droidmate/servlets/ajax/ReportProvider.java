package com.droidmate.servlets.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
		
	}
}
