/**
 * 
 */
package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.parser.ParseException;

import data.Connector;
import wrapper.User;

/**
 * @author Fernando
 *
 */
public class UpdateUserService extends Service {
	// user that will be updated
	private User user;
	
	/**
	 * Main constructor
	 * @param request
	 * @param response
	 */
	public UpdateUserService(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	/* (non-Javadoc)
	 * @see service.Service#execute()
	 */
	@Override
	public void execute() {
		try {
			// the user should be in the payload of the request
			// as a JSON string
			extractUser();
		} catch (IOException | RuntimeException | ParseException e) {
			e.printStackTrace();
			setResponse(400, getBasicResponseJson(400, 
					"User information is invalid: " + e.getMessage()));
			return;
		}
		
		// at this point user have been extracted
		// we connect to the database and update
		Connector conn = new Connector();
		
		if (conn.updateUser(user))
			setResponse(200, "{\"success\": true}");
		else
			setResponse(500, getBasicResponseJson(500, 
					"Could not update user: " + conn.getLastError()));
	}
	
	/**
	 * Extracts the user, which should be in the payload of the request
	 * as a JSON string
	 * 
	 * @param request
	 * @return
	 */
	private void extractUser() throws 
		IOException, RuntimeException, ParseException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		
		while ((line = reader.readLine()) != null)
			sb.append(line);
		
		user = User.fromJson(sb.toString());
	}
}
