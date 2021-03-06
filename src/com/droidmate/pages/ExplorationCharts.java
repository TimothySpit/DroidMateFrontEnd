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
 * Instance of Servlet implementation: ExplorationCharts Responsible for the
 * page with the charts tracking the exploration progress.
 */
@WebServlet("/ExplorationCharts")
public class ExplorationCharts extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/** The logger which is useful for debugging. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Creates a new instance of the ExplorationCharts class.
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public ExplorationCharts() {
		super();
	}

	/**
	 * Sends back the html-code for the page with the exploration charts.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Serve {} page request.", request.getRequestURI());

		request.getRequestDispatcher("/WEB-INF/pages/explorationCharts/explorationCharts.jsp").forward(request, response);
	}

}
