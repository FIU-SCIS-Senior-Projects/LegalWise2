package wrapper;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Wraps the result provided by a Solr Query
 * @author 	Fernando Gomez
 * @date	3/29/2016
 */
public class SearchResult {
	private QueryResponse response;
	
	/**
	 * Constructor
	 * @author 	Fernando Gomez
	 * @date	3/29/2016
	 * @param resultCount
	 * @param start
	 */
	public SearchResult(QueryResponse response) {
		this.response = response;
	} 
	
	@Override
	public String toString() {
		JSONObject obj, obt;
		JSONArray arr;
		SolrDocumentList list = response.getResults();
		
		obj = new JSONObject();
		obj.put("numFound", list.getNumFound());
		obj.put("start", list.getStart());
		
		arr = new JSONArray();
		for (SolrDocument s : list) {
			obt = new JSONObject();
			obt.put("id", s.getFieldValue("id"));
			obt.put("title", s.getFieldValue("title"));
			obt.put("author", s.getFieldValue("author"));
			obt.put("bibliography", s.getFieldValue("bibliography"));
			obt.put("body", s.getFieldValue("body"));
			arr.add(obt);
		}
		
		obj.put("docs", arr);
		return obj.toString();
	}
}
