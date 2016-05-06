package dispatch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import security.Authorization;
import wrapper.User;

/**
 * 
 * @author Fernando
 *
 */
public class PageDispatcher implements Dispatchable {
	private String page;
	
	/**
	 * 
	 */
	public PageDispatcher(String page) {
		this.page = page;
	}
	
	@Override
	public void dispatchGet(HttpServletRequest request,
			HttpServletResponse response) 
					throws ServletException, IOException {
		RequestDispatcher view;
		User user;
		Authorization auth = new Authorization(request, response);
		
		if (page.equals("/logout")) {
			auth.logout();
			response.sendRedirect("login.jsp");
			return;
		} else if (!auth.isValidSession()) {
			try {
				request.setAttribute("retUrl", 
					URLEncoder.encode(request.getRequestURI(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// disregard attr
			}	
			request.removeAttribute("userFirstName");
			request.removeAttribute("userLastName");
			request.removeAttribute("userEmail");
			request.removeAttribute("userType");
			request.removeAttribute("userIsTrial");
			request.removeAttribute("userTrialDuration");
			view = request.getRequestDispatcher("login.jsp");
		} else {
			user = auth.getUser();
			// put user info in request
			request.setAttribute("userFirstName", user.getFirstName());
			request.setAttribute("userLastName", user.getLastName());
			request.setAttribute("userEmail", user.getEmail());
			request.setAttribute("userType", user.getType().toString());
			request.setAttribute("userIsTrial", user.isTrial());
			request.setAttribute("userTrialDuration", user.getTrialDuration());
			// send to requested
			view = request.getRequestDispatcher(page + ".jsp");
		}
			
		view.forward(request, response);
	}

	@Override
	public void dispatchPost(HttpServletRequest request,
			HttpServletResponse response) 
					throws ServletException, IOException {
		RequestDispatcher view;
		Authorization auth = new Authorization(request, response);
		// attribute that says where to go after login
		String retUrl = request.getParameter("retUrl");
				
		switch (page) {
			case "login":
				if (auth.login()) {
					// login succeeded
					if (retUrl == null || retUrl.isEmpty())
						retUrl = "index";					
					// we use sendRedirect to force GET,
					// the RequestDispatcher will retain the POST
					response.sendRedirect(retUrl);
				} else {
					// login failed
					if (retUrl != null)
						request.setAttribute("retUrl", retUrl);
					
					request.setAttribute("error", "Invalid credentials");
					view = request.getRequestDispatcher("login.jsp");
					view.forward(request, response);
				}
				break;
		}
	}
}
