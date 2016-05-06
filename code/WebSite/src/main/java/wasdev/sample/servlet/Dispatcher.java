package wasdev.sample.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dispatch.PageDispatcher;
import dispatch.ServiceDispatcher;

/**
 * Servlet implementation class Dispatcher
 */
@WebServlet(urlPatterns = 
	{"/public", "",
	"/login", "/index", "/documents", "/users", "/profile", 
	"/qa", "/logout", 
	"/service/create", "/service/upload", "/service/users",
	"/service/documents", "/service/download",
	"/service/user/update",
	"/service/search", "/service/qa",
	"/service/history"})
@MultipartConfig()
public class Dispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Dispatcher() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, 
	 * 		HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, 
			HttpServletResponse response) 
					throws ServletException, IOException {
		String path = null;
		dispatch.Dispatchable d = null;
		
		// extract requested page from path
		path = getPath(request);
		
		if (path.equals("/"))
			path = "/index";
		
		switch (path) {
			case "/service/create":
			case "/service/users":
			case "/service/search":
			case "/service/documents":
			case "/service/download":
			case "/service/history":
				d = new ServiceDispatcher(path.substring(9));
				break;
			case "/login":
			case "/index":
			case "/documents":
			case "/users":
			case "/profile":
			case "/qa":
			case "/logout":
				d = new PageDispatcher(path);
				break;
			default:
				ServiceDispatcher.setResponse(
					response, 404, "The resource was not found");
		}
		
		if (d != null)
			d.dispatchGet(request, response);		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, 
	 * 		HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, 
			HttpServletResponse response) 
					throws ServletException, IOException {
		String path;
		dispatch.Dispatchable d = null;
		
		// extract requested page from path
		path = getPath(request);		
		
		switch (path) {
			case "/login":
				d = new PageDispatcher("login");
				break;
			case "/service/upload":
			case "/service/user/update":
			case "/service/qa":	
				d = new ServiceDispatcher(path.substring(9));
				break;
			default:
				ServiceDispatcher.setResponse(
						response, 404, "The resource was not found");
				return;
		}
		
		if (d != null)
			d.dispatchPost(request, response);
		
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	private String getPath(HttpServletRequest request) {
		return request.getRequestURI().substring(
				request.getContextPath().length()).toLowerCase();		
	}
}
