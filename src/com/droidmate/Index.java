package com.droidmate;

import java.io.File;
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
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.json.JSONObject;

/**
 * Servlet implementation class DroidMateServlet
 */
@WebServlet("/index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Index() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {
		// initialization
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		request.getRequestDispatcher("/WEB-INF/views/pages/index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if (request.getParameter("filebrowser") != null) {
			try {
				for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} catch (Exception e) {
				// Default look and feel
			}
			JFileChooser filechooser = new JFileChooser();
			filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (filechooser.showOpenDialog(filechooser) == JFileChooser.APPROVE_OPTION) {
				List<JSONObject> jsonFiles = new LinkedList<>();
				for (File file : filechooser.getSelectedFile().listFiles()) {
					JSONObject jsonFile = new JSONObject();
					jsonFile.put("name", file.getName());
					jsonFile.put("size", Long.toString(file.length()));
					jsonFile.put("package", file.getPath());
					jsonFile.put("version", Long.toString((new Random()).nextInt(10)));
					jsonFiles.add(jsonFile);
				}
				request.setAttribute("files", jsonFiles);
				request.setAttribute("selpath", filechooser.getSelectedFile());
				session.setAttribute("files", jsonFiles);
				session.setAttribute("title", "user");
			}
		}
		doGet(request, response);
	}

}
