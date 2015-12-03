package com.droidmate;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Cpu
 */
@WebServlet("/cpu")
public class Cpu extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Cpu() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Random rnd = new Random();
		 
	    int CPUloading = rnd.nextInt(100);
	    int CPUcore = CPUloading - 30 < 0 ? 0 : rnd.nextInt( Math.max(1,CPUloading - 30));
	    int Disk = rnd.nextInt(1024-56)+56;
	 
	    response.getWriter().append("{\"cpu\":" + CPUloading + ", \"core\":" + CPUcore + ", \"disk\":" + Disk + "}");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
