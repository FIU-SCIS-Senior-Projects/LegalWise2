package service;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

/**
 * Represents a service offered by this app.
 * Instances are intended to perform a service, give a request, and 
 * write the appropriate data to the response.
 * 
 * @author Fernando
 *
 */
public abstract class Service {
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	/**
	 * Base constructor
	 * @param request
	 * @param response
	 */
	public Service(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	/**
	 * Performs the actual service.
	 */
	public abstract void execute();
	
	/**
	 * Sets the status and body of the response calculated by the service.
	 * @param response
	 * @param status
	 * @param content
	 * @throws IOException 
	 */
	protected final void setResponse(int status, String content) {
		response.setStatus(status);
		try {
			response.getOutputStream().write(
				(content != null ? content.toString() : "").getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Return a JSON string with information about an error
	 * @param status
	 * @param msg
	 * @return
	 */
	protected String getBasicResponseJson(int status, String msg) {
		JSONObject obj = new JSONObject();
		obj.put("status", status);
		obj.put("msg", msg);
		return obj.toJSONString();
	}
	
	/**
	 * @author		Fernando
	 * @date		Sep 15, 2015 
	 * @param in
	 * @return
	 * @throws IOException 
	 */
	protected String parseResponse(BufferedReader in) {
		StringBuilder buff = new StringBuilder();
		try {
			for (int c = in.read(); c != -1; c = in.read())
				buff.append((char)c);			
			in.close();
		} catch (IOException e) {
			// disregard
		}
		return buff.toString();
	}
}
