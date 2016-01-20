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

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.droidmate.apk.APKInformation;
import com.droidmate.apk.inlining.APKInliner;
import com.droidmate.apk.inlining.InliningStatus;
import com.droidmate.settings.AjaxConstants;
import com.droidmate.settings.GUISettings;
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
		response.setContentType("application/json");
		JSONObject result = new JSONObject();

		// handle information get request
		String[] get_info = request.getParameterValues(AjaxConstants.APKPathHandeler_GET_INFORMATION);
		if (get_info != null) {
			result.put(AjaxConstants.APKPathHandeler_GET_INFORMATION, handleInformationGetRequest(get_info));
		}

		// handle saving get requests
		String apkSaveRoot = request.getParameter(AjaxConstants.APKPathHandler_SAVE_APKROOT);
		if (apkSaveRoot != null) {
			result.put("success", handleSaveAPKRoot(apkSaveRoot));
		}

		//handle save selected apk indices
		String[] save_selectedAPKS = request.getParameterValues(AjaxConstants.APKPathHandler_SAVE_SELECTED_APKS);
		if (save_selectedAPKS != null) {
			handleSaveSelectedAPKS(save_selectedAPKS);
		}
		
		out.print(result);
		out.flush();
	}

	private void handleSaveSelectedAPKS(String[] save_selectedAPKS) {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		for (APKInformation apk : user.getAPKS()) {
			apk.setSelected(false);
		}
		
		for (String id : save_selectedAPKS) {
			if (NumberUtils.isDigits(id) && NumberUtils.isNumber(id)) {
				int index = Integer.parseInt(id);
				APKInformation[] apks = user.getAPKS();
				if (index < apks.length) {
					apks[index].setSelected(true);
				}
			}
		}
	}

	private JSONObject handleInformationGetRequest(String[] get_info) {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);

		JSONObject apkInfoResult = new JSONObject();

		for (String info_data : get_info) {
			switch (info_data) {
			case AjaxConstants.APKPathHandeler_GET_INFORMATION_SELECTEDAPKS: {
				JSONArray selectedAPKInfo = new JSONArray();
				for (APKInformation apk : user.getAPKS()) {
					if (!apk.isSelected())
						continue;
					selectedAPKInfo.put(apk.toJSONObject());
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
				APKInliner inliner = (APKInliner) getServletContext().getAttribute(ServletContextConstants.APK_INLINER);
				InliningStatus status;
				if (inliner != null) {
					status = inliner.getInliningStatus();
				} else {
					status = InliningStatus.NOT_STARTED;
				}
				
				// returns the apks in the folder
				JSONArray selectedAPKInfo = new JSONArray();
				for (APKInformation apk : user.getAPKS()) {
					JSONObject apkObject = apk.toJSONObject();
					apkObject.put("inliningStatus", status);
					selectedAPKInfo.put(apkObject);
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

	private boolean handleSaveAPKRoot(String saveAPKRoot) {
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute(ServletContextConstants.DROIDMATE_USER);
		Path newAPKRoot = Paths.get(saveAPKRoot);
		
		return user.setAPKPath(newAPKRoot);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
