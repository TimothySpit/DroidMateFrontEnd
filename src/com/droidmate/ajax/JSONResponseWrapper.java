package com.droidmate.ajax;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Wraps a JSON Object for better usability.
 */
public class JSONResponseWrapper {

	/** The JSONObject to wrap */
	private JSONObject object = new JSONObject();

	/**
	 * Constructs a new JSONResponseWrapper object used for GET requests
	 */
	public JSONResponseWrapper() {
	}

	/**
	 * Constructs a new JSONResponseWrapper object used for POST requests
	 * 
	 * @param result
	 *            the boolean indicating the result
	 * @param message
	 *            the post message
	 */
	public JSONResponseWrapper(boolean result, String message) {
		setValues(object, result, message);
	}

	/**
	 * Adds a new parameter to the wrapped JSON object.
	 * 
	 * @param parameterName
	 *            the name of the parameter which should be added.
	 * @param result
	 *            the boolean indicating the result
	 * @param message
	 *            the parameters message
	 */
	public void addParameter(String parameterName, boolean result, String message) {
		JSONObject parameterObject = new JSONObject();
		setValues(parameterObject, result, message);
		object.put(parameterName, parameterObject);
	}

	/**
	 * Sets this JSON objects payload
	 * 
	 * @param payload
	 *            the payload to be set
	 */
	public void setPayload(JSONObject payload) {
		object.put("payload", payload);
	}

	/**
	 * Sets this JSON objects payload
	 * 
	 * @param payload
	 *            the payload to be set
	 */
	public void setPayload(JSONArray payload) {
		object.put("payload", payload);
	}

	/**
	 * Changes the given JSON object with the given values
	 * 
	 * @param object
	 *            the JSON object to be changed
	 * @param result
	 *            the boolean indicationg the result
	 * @param message
	 *            the message
	 */
	private void setValues(JSONObject object, boolean result, String message) {
		object.put("result", result);
		object.put("message", message);
	}

	/**
	 * Returns the wrapped JSON object
	 * 
	 * @return the wrapped JSON object
	 */
	public JSONObject toJSONObject() {
		return object;
	}

	@Override
	public String toString() {
		return object.toString();
	}
}