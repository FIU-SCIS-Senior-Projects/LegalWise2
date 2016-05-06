package service;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wrapper.File;
import data.Connector;

/**
 * Service in charge of fulfilling a download request
 * @author Fernando
 *
 */
public class DownloadService extends Service {
	private int fileId;
	
	/**
	 * Main constructor
	 * @param request
	 * @param response
	 */
	public DownloadService(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	@Override
	public void execute() {
		ServletOutputStream out;
		Connector conn;
		File file;
		
		try {
			// the fileId must be in the query string in the request
			fileId = Integer.valueOf(request.getParameter("fileId"));
		} catch (NumberFormatException e) {
			setResponse(400, 
				getBasicResponseJson(400, 
						"Invalid fileId parameter in request"));
			return;
		}
		
		conn = new Connector();
		file = conn.getFile(fileId);
		
		// null file means the id was not found, or other
		// connection error
		if (file == null) {
			setResponse(404, getBasicResponseJson(404, "File not found"));
			return;
		}
						
		try {
			// create structure in response
			/*
			response.setHeader("Content-disposition",
	                "attachment; filename=" + file.getName());
	        */
			response.setContentType(file.getMimeType());
			response.setContentLength(file.getSize().intValue());
			
			out = response.getOutputStream();
		    out.write(file.getBody(), 0, file.getBody().length);		    
		    out.flush(); 
		    
		} catch (IOException e) {
			e.printStackTrace();
			setResponse(500, getBasicResponseJson(500, 
					e.getMessage()));
		}		
	}

}
