package com.droidmate.pages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droidmate.user.DroidMateUser;

/**
 * Instance of Servlet implementation: Index. This class creates the index page.
 */
@WebServlet(urlPatterns = { "/Index" }, loadOnStartup = 1)
public class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** The logger which is useful for debugging. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Creates a new instance of the Index class.
	 */
	public Index() {
		super();
	}

	/**
	 * Initiates the whole web front end
	 */
	@Override
	public void init() throws ServletException {
		super.init();

		// create new DroidMate-user for the whole server run
		DroidMateUser globalUser = new DroidMateUser();
		getServletContext().setAttribute("user", globalUser);
	}

	/**
	 * Creates the init page.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Serve {} page request.", request.getRequestURI());

		if (request.getRequestURI().equals(request.getContextPath())) {
			response.sendRedirect("Index");
			return;
		}

		// standard request
		request.getRequestDispatcher("/WEB-INF/pages/index/index.jsp").forward(request, response);
	}

}
