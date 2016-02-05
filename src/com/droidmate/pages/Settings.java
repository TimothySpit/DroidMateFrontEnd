package com.droidmate.pages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance of Servlet implementation: Settings. This class handles the Settings page.
 */
@WebServlet("/Settings")
public class Settings extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
    /**
     * Creates a new instance of the Settings class.
     * @see HttpServlet#HttpServlet()
     */
    public Settings() {
        super();
    }

	/**
	 * Handles the settings page.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Serve {} page request.",request.getRequestURI());
		
		request.getRequestDispatcher("/WEB-INF/pages/settings/settings.jsp").forward(request, response);
	}

}
