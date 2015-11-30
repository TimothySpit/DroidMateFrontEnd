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

/*
 * The FirstServlet Servlet handles the "post" and "get" requests from "/select" actions.
 * These actions from a webpage call the "doPost" and "doGet" functions, which then call 
 * apropriate java funtions, and then give it the next Pages to open.
 */

@WebServlet(name = "FirstServlet", urlPatterns = { "/select" })
// @MultipartConfig
public class FirstServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	/*
	 * Usually called when the page is opened for the first time.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		System.out.println("doGet in FirstServlet");
		String nextJSP = "/jsp/startingscreen.jsp";
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
		req.setAttribute("fileList", FileList.getInstance());
		dispatcher.forward(req, resp);

	}

	/*
	 * Usually called when we press the "Browse" button. We open a file
	 * selection window, select a root directory, and then add each file to a
	 * list. We then open the starting screen again, and give it the new file
	 * list.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("doPost ");

		// Open the file selection dialog
		String selectFileButton = req.getParameter("selectFileButton");
		if (selectFileButton != null) {
			System.out.println("selectFiles");
			JFileChooser filechooser = new JFileChooser();
			filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (filechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				FileList.getInstance().clear();
				FileContainer.resetCount();
				// Add each file to the list
				for (File file : filechooser.getSelectedFile().listFiles()) {
					// if (file.isFile()) {
					FileContainer fileContainer = new FileContainer(file.getName(), file.getAbsolutePath(),
							file.length());
					FileList.getInstance().add(fileContainer);
					System.out.println(file.getName());
					// }
				}
			}
			System.out.println("Done with selecting files");
		}

		// Forward the new Page
		req.setAttribute("fileList", FileList.getInstance());
		String nextJSP = "/jsp/startingscreen.jsp";
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
		dispatcher.forward(req, resp);
	}

}
