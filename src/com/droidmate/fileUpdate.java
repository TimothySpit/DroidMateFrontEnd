package com.droidmate;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class fileUpdate
 */
@WebServlet("/fileUpdate")
public class fileUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public fileUpdate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if (session.isNew() || session.getAttribute("title") == null) {
			response.getWriter().append("You are not authorized!");
			return;
		} 
		
		List<JSONObject> files = (List) session.getAttribute("files");
		int[] status = (int[]) session.getAttribute("filestatus");
		
		if(files == null)
			return;
		
		if(status == null) {
			status = new int[files.size()];
			session.setAttribute("filestatus", status);
		}
		
		String paramValue = request.getParameter("fileNr");
		if(paramValue == null || Integer.parseInt(paramValue) < 0 || Integer.parseInt(paramValue) >= status.length) {
			 response.getWriter().append("{\"status\":" + 0 + "}");
			 return;
		}
		response.getWriter().append("{\"status\":" + status[Integer.parseInt(paramValue)] + "}");
		status[Integer.parseInt(paramValue)] = Math.min(100, status[Integer.parseInt(paramValue)] + (new Random()).nextInt(5));
		session.setAttribute("filestatus", status);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
