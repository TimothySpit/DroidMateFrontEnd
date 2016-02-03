package com.droidmate.pages;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Instance of Servlet implementation: ExplorationCharts
 * Responsible for the page with the charts tracking the exploration progress.
 */
@WebServlet("/ExplorationCharts")
public class ExplorationCharts extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of the ExplorationCharts class.
	 * @see HttpServlet#HttpServlet()
	 */
	public ExplorationCharts() {
		super();
	}

	/**
	 * Sends back the html-code for the page with the eploration charts.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/pages/explore/explorationCharts.jsp").forward(request, response);
	}

}
