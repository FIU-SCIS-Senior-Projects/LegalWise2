package service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import qa.RetrieveAndRankWrapper;
import wrapper.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles the addition of Question and Answers pairs
 * @author Fernando
 *
 */
public class QaAdditionService extends Service {
	List<Pair> pairs;
	
	/**
	 * Main constructor
	 * @param request
	 * @param response
	 */
	public QaAdditionService(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
		pairs = new ArrayList<Pair>();
	}

	@Override
	public void execute() {
		RetrieveAndRankWrapper ranker;		
		if (getQas()) {
			try {
				ranker = new RetrieveAndRankWrapper();
				if (ranker.addQaPairs(pairs))
					setResponse(200, null);
				else
					setResponse(500, null);
			} catch (IOException | RuntimeException | SolrServerException e) {
				e.printStackTrace();
				setResponse(500, e.getMessage());
			}
		}
	}

	/**
	 * Parse the request and obtains the list of questions and answers
	 * @return
	 */
	private boolean getQas() {
		JSONParser parser;
		JSONObject obj;
		JSONArray arr;
		Iterator<JSONObject> i;
		
		try {
			parser = new JSONParser();
			arr = (JSONArray)parser.parse(request.getReader());			
			i = arr.iterator();
			
			while (i.hasNext()) {
				obj = i.next();
				pairs.add(new Pair(
						obj.get("question").toString(), 
						obj.get("answer").toString()));
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			setResponse(400, getBasicResponseJson(400, e.getMessage()));
			return false;
		}
		
		return true;
	}
}
