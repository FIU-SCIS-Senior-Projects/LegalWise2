/**
 * 
 */
package qa;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;
import res.ResourceManager;

/**
 * @author Fernando
 *
 */
public class DocumentConverter {
	private static String ENV_NAME = "VCAP_SERVICES";
	private static String SERVICE_KEY = "document_conversion";
	
	private String username;
	private String password;
	
	private DocumentConversion service;
	
	/**
	 * Main constructor
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public DocumentConverter() 
			throws IOException, ParseException {
		obtainsServiceCredentials();
		login();
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Convert the specified file into text
	 * @param file
	 * @return
	 */
	public String convertToText(File file) {
		return service.convertDocumentToText(file);
	}
	
	/**
	 * Convert the specified file into text
	 * @param file
	 * @return
	 */
	public Answers convertToAnswers(File file) {
		return service.convertDocumentToAnswer(file);
	}

	/**
	 * Extracts the service credentials from the environment variable
	 * @throws ParseException
	 * @throws IOException 
	 */
	private void obtainsServiceCredentials() 
			throws IOException, ParseException {
		JSONParser parser;
		JSONObject obj1, obj2, obj3;
		JSONArray arr;
		String envValue;
		
		// reset credential
		username = null;
		password = null;
		
		envValue = ResourceManager.getEnv(ENV_NAME);
		// env information should come as a JSON string, which needs 
		// parsing...
		parser = new JSONParser();
		obj1 = (JSONObject)parser.parse(envValue);
		// the structure of the JSON is expected to be
		// {"postgresql-9.1":[{"credentials":{"uri": "[URL]"}}]}
		if (obj1.containsKey(SERVICE_KEY)) {
			arr = (JSONArray)obj1.get(SERVICE_KEY);				
			if (!arr.isEmpty()) {
				obj2 = (JSONObject)arr.get(0);
				if (obj2.containsKey("credentials")) {
					obj3 = (JSONObject)obj2.get("credentials");
					username = obj3.get("username").toString();
					password = obj3.get("password").toString();
				}
			}
		}
		
		// if any credential was not obtain, the format is incorrect
		if (username == null || password == null)
			throw new IOException("Could not extract credentials from ENV");
	}
	
	/**
	 * Assign proper credentials to the service 
	 */
	private void login() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		service = new DocumentConversion(sdf.format(new Date()));
		service.setUsernameAndPassword(username, password);
	}
}
