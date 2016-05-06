package service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

import wrapper.Document;
import data.Connector;

/**
 * Service in charge of fulfilling a list-document request
 * @author Fernando
 *
 */
public class ListDocumentsService extends Service {
	private int offset;
	//private String filter;
	private Document[] documents;
	
	/**
	 * Main constructor
	 * @param request
	 * @param response
	 */
	public ListDocumentsService(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
		offset = request.getParameter("offset") != null &&
				request.getParameter("offset").matches("\\d+") ?
					Integer.valueOf(request.getParameter("offset")) : 0;
		//sortBy = request.getParameter("sortBy");
		//filter = request.getParameter("filter");
	}
	
	@Override
	public void execute() {
		JSONArray obj = new JSONArray();
		Connector conn = new Connector();
		
		if ((documents = conn.getDocuments(offset)) != null) {
			for (Document doc : documents)
				obj.add(doc.getAsJson());
			setResponse(200, obj.toString());
		} else
			setResponse(500, getBasicResponseJson(500, 
					conn.getLastError()));
	}

}
