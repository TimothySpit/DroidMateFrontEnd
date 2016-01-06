package com.droidmate.servlets.ajax;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.droidmate.apk.inlining.APKInliner;
import com.droidmate.settings.AjaxConstants;
import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class APKInliningHandler
 */
@WebServlet("/APKInliningHandler")
public class APKInliningHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APKInliningHandler() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		PrintWriter out = response.getWriter();
		JSONObject result = new JSONObject();

		// handle information get request
		String get_inline = request.getParameter(AjaxConstants.APKInlineHandler_SAVE_INLINE);
		if (get_inline != null) {
			// check if inlining is not yet started
			APKInliner inliner = (APKInliner) getServletContext().getAttribute(ServletContextConstants.APK_INLINER);
			if (inliner == null) {
				inliner = new APKInliner(user);
				getServletContext().setAttribute(ServletContextConstants.APK_INLINER, inliner);
				(new Thread(inliner)).start();
			}
		}

		String get_inlineStatus = request.getParameter(AjaxConstants.APKInlineHandler_GET_INLINE_STATUS);
		if (get_inlineStatus != null) {
			APKInliner inliner = (APKInliner) getServletContext().getAttribute(ServletContextConstants.APK_INLINER);
			if (inliner != null) {
				result.put(AjaxConstants.APKInlineHandler_GET_INLINE_STATUS, inliner.getInliningStatus());
			}
		}
		
		out.print(result);
		out.flush();
	}

}
