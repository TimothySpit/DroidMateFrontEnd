package webFrontEnd;

import java.awt.event.ActionEvent;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.swing.JFileChooser;

@WebServlet(name = "FirstServlet", urlPatterns = { "/select" })
// @MultipartConfig
public class FirstServlet extends HttpServlet {
	static String result = "";
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("selectAction");

		req.setAttribute("fileList", FileList.getInstance());
		System.out.println("doGet " + action);
		if (action != null) {
			switch (action) {
			case "selected":

				break;
			case "searchByName":
				// searchEmployeeByName(req, resp);
				break;
			}
		} else {
			System.out.println("else");
			String nextJSP = "/jsp/startingscreen.jsp";
			// String nextJSP = "/jsp/list-employees.jsp";
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
			req.setAttribute("employeeList", result);
			req.setAttribute("fileList", FileList.getInstance());
			dispatcher.forward(req, resp);

		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action2 = req.getParameter("selectFileButton");
		System.out.println("selectfile ");
		System.out.println(req.getParameter("selectFileButton"));
		String action = req.getParameter("selectAction");
		System.out.println("doPost " + action);
		System.out.println("test");
		System.out.println(req.getParts().size());
		PrintWriter writer = resp.getWriter();

		if (req.getParts().size() != 0) {
			for (Part a : req.getParts()) {
				System.out.println(a.getContentType());
				System.out.println("b");
				if (a.getContentType() != null && a.getContentType().equals("text/plain")) {
					System.out.println("a");
					result = "";
					InputStream x = a.getInputStream();
					int read = x.read();
					while (read != -1) {
						System.out.print((char) read);
						writer.write((char) read);
						result += (char) read;
						if (read == '\n') {
							// result += "<BR />";
						}

						read = x.read();
					}

					System.out.println(result);

				}
			}

		}
		;

		System.out.println("selectfile ");
		System.out.println(req.getParameter("selectFileButton"));
		if (action2.equals("selectFile")) {
			JFileChooser filechooser = new JFileChooser();

			filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (filechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				FileList.getInstance().clear();
				System.out.println(filechooser.getSelectedFile().getAbsolutePath());
				for (File list : filechooser.getSelectedFile().listFiles()) {
					FileList.getInstance().add(new FileContainer(list.getName(), list.getAbsolutePath()));
					System.out.println(list.getName());

				}
			}
			

			System.out.println("done");
		}

		if (action != null) {
			switch (action) {
			case "select":

				System.out.println("test");
				break;
			case "selectFile":

				break;
			}

		}
		req.setAttribute("fileList", FileList.getInstance());
		String nextJSP = "/jsp/startingscreen.jsp";
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
		dispatcher.forward(req, resp);
	}
	


}
