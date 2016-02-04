package com.droidmate.ajax;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.droidmate.user.APKInformation;
import com.droidmate.user.DroidMateUser;

/**
 * Servlet implementation class SaveSelectedAPKSHandler
 */
@WebServlet("/SaveSelectedAPKSHandler")
public class SaveSelectedAPKSHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// request parameters
	private static final String SELECTED_APKS_SET = "setSelectedAPKS[]";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveSelectedAPKSHandler() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// return json
		response.setContentType("application/json");
		// Do not cache
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");

		// serve request
		DroidMateUser user = (DroidMateUser) getServletContext().getAttribute("user");
		JSONObject result = new JSONObject();

		// handle SELECTED_APKS_SET request
		String[] setSelectedAPKSString = request.getParameterValues(SELECTED_APKS_SET);
		if (setSelectedAPKSString != null) {
			JSONResponseWrapper setSelectedAPKSResult = new JSONResponseWrapper();

			if (setSelectedAPKSString.length == 0) {
				// no apks set for selection
				setSelectedAPKSResult = new JSONResponseWrapper(false, "No APKS set for selection.");
			} else {
				//more than zero apks specified, test if apks exist
				Map<String,APKInformation> apkMap = user.getAPKS();
				boolean apksCorrect = true;
				//list containing the apks to set selected if no error occurs
				List<APKInformation> apksToSetSelected = new LinkedList<>();
				for (String apkStringToTest : setSelectedAPKSString) {
					if(apkMap.containsKey(apkStringToTest)) {
						APKInformation apkInfo = apkMap.get(apkStringToTest);
						apksToSetSelected.add(apkInfo);
					} else {
						apksCorrect = false;
						setSelectedAPKSResult = new JSONResponseWrapper(false, "APK " + apkStringToTest + " does not exist for selection.");
						break;
					}
				}
				
				//test if all apks could be selected
				if(apksCorrect) {
					for (APKInformation apk : apksToSetSelected) {
						apk.setAPKSelected(true);
					}
					setSelectedAPKSResult = new JSONResponseWrapper(true, "APKS successfully selected.");
				}
			}
			result.put(SELECTED_APKS_SET, setSelectedAPKSResult.toJSONObject());
		}

		response.getWriter().print(result);
	}

}
