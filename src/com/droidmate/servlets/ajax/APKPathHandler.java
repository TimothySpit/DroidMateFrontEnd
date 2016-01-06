package com.droidmate.servlets.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.apk.APKInformation;
import com.droidmate.settings.AjaxConstants;
import com.droidmate.settings.ServletContextConstants;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class APKPathHandler
 */
@WebServlet("/APKPathHandler")
public class APKPathHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APKPathHandler() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		JSONObject result = new JSONObject();

		// handle information get request
		String[] get_info = request.getParameterValues(AjaxConstants.APKPathHandeler_GET_INFORMATION);
		if (get_info != null) {
			result.put(AjaxConstants.APKPathHandeler_GET_INFORMATION, handleInformationGetRequest(get_info));
		}

		// handle saving get requests
		String apkSaveRoot = request.getParameter(AjaxConstants.APKPathHandeler_SAVE_APKROOT);
		if (apkSaveRoot != null) {
			handleSaveAPKRoot(apkSaveRoot);
		}

		out.print(result);
		out.flush();
	}

	private JSONObject handleInformationGetRequest(String[] get_info) {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		JSONObject apkInfoResult = new JSONObject();

		for (String info_data : get_info) {
			switch (info_data) {
			case AjaxConstants.APKPathHandeler_GET_INFORMATION_SELECTEDAPKS: {
				JSONArray selectedAPKInfo = new JSONArray();
				int counter = 0;
				for (APKInformation apk : user.getAPKS()) {
					if (!apk.isSelected())
						continue;
					JSONArray apkInfo = new JSONArray();
					apkInfo.put(counter++);
					apkInfo.put(apk.getFile().getName());
					apkInfo.put(apk.getFile().length());
					apkInfo.put("package");
					apkInfo.put("version");
					selectedAPKInfo.put(apkInfo);
				}
				JSONObject res = new JSONObject();
				res.put("data", selectedAPKInfo);
				apkInfoResult.put(AjaxConstants.APKPathHandeler_GET_INFORMATION_SELECTEDAPKS, res);
				break;
			}
			case AjaxConstants.APKPathHandeler_GET_INFORMATION_APKROOT: {
				if (user.getAPKPath() != null)
					apkInfoResult.put(AjaxConstants.APKPathHandeler_GET_INFORMATION_APKROOT, user.getAPKPath());
				else
					apkInfoResult.put(AjaxConstants.APKPathHandeler_GET_INFORMATION_APKROOT, "");
				break;
			}
			case AjaxConstants.APKPathHandeler_GET_INFORMATION_APKS: {
				// returns the apks in the folder
				JSONArray selectedAPKInfo = new JSONArray();
				int counter = 0;
				for (APKInformation apk : user.getAPKS()) {
					JSONArray apkInfo = new JSONArray();
					apkInfo.put(counter++);
					apkInfo.put(apk.getFile().getName());
					apkInfo.put(apk.getFile().length());
					apkInfo.put("package");
					apkInfo.put("version");
					selectedAPKInfo.put(apkInfo);
				}
				JSONObject res = new JSONObject();
				res.put("data", selectedAPKInfo);
				apkInfoResult.put(AjaxConstants.APKPathHandeler_GET_INFORMATION_APKS, res);
				break;
			}
			default:
				throw new IllegalStateException();
			}
		}

		return apkInfoResult;
	}

	private void handleSaveAPKRoot(String saveAPKRoot) {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		Path newAPKRoot = Paths.get(saveAPKRoot);
		user.setAPKPath(newAPKRoot);
	}
}
