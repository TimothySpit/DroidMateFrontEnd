package com.droidmate.servlets.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;

import com.droidmate.settings.AjaxConstants;
import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class ConsoleOutput
 */
@WebServlet("/ConsoleOutput")
public class ConsoleOutput extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConsoleOutput() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		List<String> consoleOutput =  (List<String>) getServletContext().getAttribute("consoleOutput");
		PrintWriter out = response.getWriter();

		JSONObject result = new JSONObject();
		result.put("lines", 0);
		result.put("text", "");
		
		String getOutput = request.getParameter("get");
		if (getOutput != null) {
			if (NumberUtils.isDigits(getOutput) && NumberUtils.isParsable(getOutput)) {
				int lines = Integer.parseInt(getOutput);
				synchronized (consoleOutput) {
					if (lines < consoleOutput.size()) {
						result.put("lines", consoleOutput.size()-lines);
						String res = "";
						for (int i = lines; i < consoleOutput.size(); i++) {
							res += consoleOutput.get(i) + System.lineSeparator();
						}
						result.put("text", res);
					}
				}
			}
		}
		
		out.print(result);
		out.flush();
	}

}
