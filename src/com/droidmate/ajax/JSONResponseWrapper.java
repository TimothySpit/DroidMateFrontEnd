package com.droidmate.ajax;

import org.json.JSONObject;

public class JSONResponseWrapper {
	
	private JSONObject object = new JSONObject();
	
	/**
	 * Constructs a new JSONResponseWrapper object used for GET requests
	 */
	public JSONResponseWrapper() {
		
	}
	
	/**
	 * Constructs a new JSONResponseWrapper object used for POST requests
	 * @param result
	 * @param message
	 */
	public JSONResponseWrapper(boolean result, String message) {
		setValues(object, result, message);
	}
	
	public void addParameter(String parameterName, boolean result, String message) {
		JSONObject parameterObject = new JSONObject();
		setValues(parameterObject, result, message);
		object.put(parameterName, parameterObject);
	}
	
	private void setValues(JSONObject object, boolean result, String message) {
		object.put("result", result);
		object.put("message", message);
	}
	
	public String toJSON() {
		return object.toString();
	}
}