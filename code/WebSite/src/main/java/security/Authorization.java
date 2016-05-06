package security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import res.StringUtils;
import wrapper.User;
import data.Connector;

/**
 * Provides authorization mechanism such as login, logout, session check, etc.
 * This class extracts and inject authorization data into http requests.
 * Each time a page or service is requested, this class should be used to
 * determine wither the user is properly logged in and has the right 
 * privileges.
 * @author Fernando Gomez
 */
public class Authorization {
	private final String SESSION_TITLE = "BM_SESSION";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Map<String, String> cookies;
	private User user;
	
	/**
	 * @author Fernando Gomez
	 * @param request
	 */
	public Authorization(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
		cookies = new HashMap<>();
		
		// round up cookies
		if (request.getCookies() != null)
			for (Cookie c : request.getCookies())
				cookies.put(c.getName(), c.getValue());
		
		// obtain user
		user = cookies.containsKey(SESSION_TITLE) ? 
				getUser(cookies.get(SESSION_TITLE)) : null;		
	}
	
	/**
	 * Validates the current session.
	 * @return true is the session contains a valid user
	 */
	public boolean isValidSession() {
		return user != null;
	}
	
	/**
	 * Return the current user.
	 * @return the user currently logged in, or null if none
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Determines whether the token in a request is valid
	 * @return
	 */
	public boolean isValidToken() {
		return true;
	}
	
	/**
	 * Logs in an user. The credential must be parameter of the request
	 * used to instantiate this Authorization class. This method
	 * is ideal for a POST made by a login form submitted by an user.
	 * 
	 * @return 	true if credentials are valid, false otherwise
	 */
	public boolean login() {
		String un, pw, hashed = null, token;
		Connector conn;
		user = null;
		
		// obtain username and password from parameter
		un = request.getParameter("un");
		pw = request.getParameter("pw");
		
		// sha1 the password as it is stored in the database 
		// (no original passwords are stored)
		if (un != null && pw != null) {
			try {
				hashed = StringUtils.getHash(pw);
			} catch (NoSuchAlgorithmException | 
						UnsupportedEncodingException e) {
				// simply disregard
			}
			
			// attempt database connection, and request user information
			conn = new Connector(); 
			user = conn.getUserByCredentials(un, hashed);
		}
		
		if (user != null && 
				(token = getSession(user, hashed)) != null) {
			// login succeeded
			response.addCookie(new Cookie(SESSION_TITLE, token));
			return true;
		} else 
			return false;
	}
	
	/**
	 * Logsout any existing user by removing the session cookie 
	 * (emptying its value).
	 */
	public void logout() {
		response.addCookie(new Cookie(SESSION_TITLE, ""));
	}
	
	/**
	 * Creates a session token given an user
	 * @param user
	 * @param hashed
	 * @return a token containing encrypted user information
	 */
	private String getSession(User user, String hashed) {
		try {
			// return an encrypted version
			return StringUtils.encrypt(
				user.getEmail() + ":" + hashed + ":" + (new Date()).getTime());
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| IllegalBlockSizeException | NoSuchPaddingException
				| BadPaddingException e) {
			// fail on any error
			return null;
		} 
	}
	
	/**
	 * Decrypts and parses the token. Uses the information to obtain the 
	 * current user. 
	 * @param token
	 * @return an user if successful, null otherwise
	 */
	private User getUser(String token) {
		String decrypted, email, hashed, newToken;
		long createdOn, now = (new Date()).getTime(), 
				threshold = 1000 * 60 * 60;
		User user = null;
		String[] parts;
		Connector conn = new Connector();
		
		try {
			decrypted = StringUtils.decrypt(token);
			// parts should be formatted like 
			// [email]:[hashed password]:[session_created_on]
			parts = decrypted.split(":");
			
			// parts must contain 3 pieces, 
			if (parts.length == 3) {
				email = parts[0];
				hashed = parts[1];
				createdOn = Long.valueOf(parts[2]);
				
				// proceed only if less than 30 minutes have passed
				// since last activity
				if ((now - createdOn) <= threshold) {
					user = conn.getUserByCredentials(email, hashed);
					
					if (user != null) {
						newToken = getSession(user, hashed);
						response.addCookie(
								new Cookie(SESSION_TITLE, newToken));
					}
				}
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IllegalArgumentException e) {
			// disregard
		}
		
		return user;
	}
}
