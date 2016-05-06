package service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.Connector;

/**
 * 
 * @author Fernando
 *
 */
public class CreateService extends Service {
	/**
	 * Main constructor
	 * @param request
	 * @param response
	 */
	public CreateService(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	@Override
	public void execute() {
		boolean r;
		Connector c = new Connector();
		r = c.create();
		setResponse(r ? 200 : 500, 
			r ? "Database structure created" : 
				"Could not create structure");
	}

}
