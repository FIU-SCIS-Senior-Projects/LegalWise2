package dispatch;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.Connector;
import security.Authorization;
import service.CreateService;
import service.DownloadService;
import service.HistoryService;
import service.ListDocumentsService;
import service.ListUsersService;
import service.QaAdditionService;
import service.SearchService;
import service.Service;
import service.UpdateUserService;
import service.UploadService;

/**
 * 
 * @author Fernando
 *
 */
public class ServiceDispatcher implements Dispatchable {
	private String service;
	
	/**
	 * 
	 * @param service
	 */
	public ServiceDispatcher(String service) {
		this.service = service;
	}
	
	@Override
	public void dispatchGet(HttpServletRequest request,
			HttpServletResponse response) 
					throws ServletException, IOException {
		Authorization auth = new Authorization(request, response);
		Service service = null;
		
		if (auth.isValidToken()) {	
			switch (this.service) {
				case "create":					
					service = new CreateService(request, response);
					break;
				case "users":					
					service = new ListUsersService(request, response);
					break;
				case "search":					
					service = new SearchService(auth.getUser(),
							request, response);
					break;
				case "documents":					
					service = new ListDocumentsService(request, response);
					break;
				case "download":					
					service = new DownloadService(request, response);
					break;
				case "history":					
					service = new HistoryService(auth.getUser(),
							request, response);
					break;
				default:
					setResponse(response, 404, 
							"{\"msg\":\"Service not found\"}");
					return;
			}
			// perform service
			service.execute();
		} else
			setResponse(response, 401, "Unauthorized");
	}

	@Override
	public void dispatchPost(HttpServletRequest request,
			HttpServletResponse response) {
		Authorization auth = new Authorization(request, response);
		Service service = null;
		
		if (auth.isValidToken()) {	
			switch (this.service) {
				case "upload":					
					service = new UploadService(
							auth.getUser(), request, response);
					break;
				case "user/update":
					service = new UpdateUserService(request, response);
					break;
				case "qa":
					service = new QaAdditionService(request, response);
					break;
				default:
					setResponse(response, 404, 
							"{\"msg\":\"Service not found\"}");
					return;
			}
			// perform service
			service.execute();
		} else
			setResponse(response, 401, "Unauthorized");
	}

	/**
	 * Sets the status and body of a response.
	 * @param response
	 * @param status
	 * @param content
	 * @throws IOException 
	 */
	public static void setResponse(HttpServletResponse response,
			int status, Object content) {
		response.setStatus(status);
		try {
			response.getOutputStream().write(
				(content != null ? content.toString() : "").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
