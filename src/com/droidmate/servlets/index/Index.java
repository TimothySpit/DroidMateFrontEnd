package com.droidmate.servlets.index;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class DroidMateServlet
 */
@WebServlet(urlPatterns = { "/Index" }, loadOnStartup = 1)
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Index() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();

		// create new DroidMate-user for the whole server run
		DroidMateUser globalUser = new DroidMateUser();
		getServletContext().setAttribute(ServletContextConstants.DROIDMATE_USER, globalUser);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		if (user.isExplorationStarted()) {
			//no access to this page, redirect to exploration
			request.getRequestDispatcher("/WEB-INF/views/pages/explore/explore.jsp").forward(request, response);
			return;
		}
		
		// standard request
		request.getRequestDispatcher("/WEB-INF/views/pages/index/index.jsp").forward(request, response);
	}

}
