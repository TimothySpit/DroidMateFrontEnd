package com.droidmate.ajax;

import org.json.JSONArray;
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
	
	public void setPayload(JSONObject payload) {
		object.put("payload", payload);
	}
	
	public void setPayload(JSONArray payload) {
		object.put("payload", payload);
	}
	
	private void setValues(JSONObject object, boolean result, String message) {
		object.put("result", result);
		object.put("message", message);
	}
	
	public JSONObject toJSONObject() {
		return object;
	}
	
	@Override
	public String toString() {
		return object.toString();
	}
}