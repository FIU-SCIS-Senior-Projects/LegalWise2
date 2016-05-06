/**
 * 
 */
package service;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import qa.RetrieveAndRankWrapper;
import data.Connector;
import wrapper.History;
import wrapper.User;

/**
 * @author Fernando
 *
 */
public class HistoryService extends Service {
	private User user;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param user
	 */
	public HistoryService(
			User user,
			HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see service.Service#execute()
	 */
	@Override
	public void execute() {
		Connector conn;
		History[] history;
		JSONArray arr;
		Iterator<History> it;
		
		try {			
			conn = new Connector();
			history = conn.getHistory(user);
			arr = new JSONArray();
			
			for (History h : history)
				arr.add(h.getAsJson());			
			
			setResponse(200, arr.toString());
		} catch (RuntimeException e) {
			e.printStackTrace();
			setResponse(500, getBasicResponseJson(500, e.getMessage()));
		}

	}

}
