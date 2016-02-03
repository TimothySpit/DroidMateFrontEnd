package com.droidmate.pages;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class Index
 */
@WebServlet(urlPatterns = { "/Index" }, loadOnStartup = 1)
public class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public Index() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();

		// create new DroidMate-user for the whole server run
		DroidMateUser globalUser = new DroidMateUser();
		getServletContext().setAttribute("user", globalUser);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getRequestURI().equals(request.getContextPath())) {
			response.sendRedirect("Index");
			return;
		}

		// standard request
		request.getRequestDispatcher("/WEB-INF/pages/index/index.jsp").forward(request, response);
	}

}
