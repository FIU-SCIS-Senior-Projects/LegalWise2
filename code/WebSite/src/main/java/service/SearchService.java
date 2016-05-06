/**
 * 
 */
package service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.parser.ParseException;

import data.Connector;
import qa.RetrieveAndRankWrapper;
import wrapper.History;
import wrapper.SearchResult;
import wrapper.User;

/**
 * @author Fernando
 *
 */
public class SearchService extends Service {
	private User user;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param user
	 */
	public SearchService(User user,
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
		String query;
		RetrieveAndRankWrapper ranker;
		SearchResult result;
		Connector conn;
		
		try {
			query = request.getParameter("query");
			
			if (query == null || query.isEmpty())
				setResponse(400, getBasicResponseJson(400, 
						"No search query specified"));
			else {				
				// add current search to history
				conn = new Connector();
				conn.addHistory(new History(null, query, null, user));
				
				ranker = new RetrieveAndRankWrapper();
				result = ranker.searchSolr(query);				
				setResponse(200, result.toString());
			}
		} catch (IOException | ParseException | 
				RuntimeException | SolrServerException e) {
			e.printStackTrace();
			setResponse(500, getBasicResponseJson(500, e.getMessage()));
		}
	}

}
