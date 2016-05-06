package qa;
/*
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
*/
import java.util.ArrayList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URI;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers.AnswerUnits;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.Ranking;

import res.ResourceManager;
import wrapper.Document;
import wrapper.Pair;
import wrapper.SearchResult;
import wrapper.Settings;

/**
 * Wraps the retrieve an rank functionality
 * 
 * @author Fernando Gomez
 * @date 3/10/2016
 */
public class RetrieveAndRankWrapper {
	private static String ENV_NAME = "RETRIEVE_AND_RANK";
	private static final String HEADNOTES_TOKEN = 
			"LexisNexis(.*)Headnotes";
	private static final String HN_HEADER_TOKEN = 
				"(\\sHN\\d+\\s)(.*?)(?=\\sHN\\d+\\s|$)";
	private static final String HN_SENTENCE_TOKEN = 
			"[\"']?[A-Z][^.?!]((?![.?!]['\"]?\\s[\"']?[A-Z][^.?!]).)+[.?!'\"]";
	
	private RetrieveAndRank service;
	private String username;
	private String password;
	private String clusterId;
	private String configurationId;
	private String collectionId;
	private String rankerId;

	/**
	 * Constructor
	 * 
	 * @author Fernando Gomez
	 * @throws IOException
	 * @date 3/10/2016
	 */
	public RetrieveAndRankWrapper() throws IOException {
		service = new RetrieveAndRank();
		
		// authenticate the service
		login();

		// obtain instance properties
		Settings settings = Settings.getInstance();
		clusterId = settings.getSetting("solrClusterId");
		configurationId = settings.getSetting("solrConfigurationId");
		collectionId = settings.getSetting("solrCollectionId");
		rankerId = settings.getSetting("solrRankerId");
	}

	/**
	 * Search the Solr indexed documents. This will basically consume the
	 * "Search Solr standard query parser" service of the Retrieve and Rank.
	 * 
	 * @author Fernando Gomez
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParseException
	 * @throws SolrServerException 
	 * @date 3/10/2016
	 */
	public SearchResult searchSolr(String criteria)
			throws MalformedURLException, UnsupportedEncodingException,
			IOException, ParseException, SolrServerException {
		HttpSolrClient solrClient;
		SolrQuery query;
		QueryResponse response;
		Ranking rank;
		
		solrClient = getSolrClient(service.getSolrUrl(clusterId), 
				username, password);
		
		query = new SolrQuery(criteria);
		query.setParam("fl", "id,title,author,bibliography,body");
		query.setParam("wt", "json");
		response = solrClient.query(collectionId, query);		
		//rank = service.rank(rankerId, response);
		
		return new SearchResult(response);
	}

	/**
	 * Submit a document to Solr for indexing
	 * 
	 * @param document
	 * @throws IOException
	 * @throws ParseException
	 * @throws SolrServerException
	 */
	synchronized
	public boolean addDocuments(Document doc, Answers answers)
			throws IOException, ParseException, SolrServerException {
		HttpSolrClient solrClient;
		List<SolrInputDocument> docs = new ArrayList<>();
		SolrInputDocument newdoc;
		Pattern r = Pattern.compile(HEADNOTES_TOKEN);
		String title, body, headnotes = null;
		int	fileId = doc.getFile().getFileId();
		String fileName = doc.getFile().getName();
	    Matcher m;
	    long date = (new Date()).getTime();
	    int cnt = 0;
	    boolean isHeadNote;
	    UpdateResponse addResponse;

		solrClient = getSolrClient(service.getSolrUrl(clusterId), 
				username, password);

		for (AnswerUnits aUnit : answers.getAnswerUnits()) {
			isHeadNote = false;
			title = aUnit.getTitle();
			body = aUnit.getContent().get(0).getText(); 
			
			// if headnotes has not been found
			if (headnotes == null) {
				// search if head notes is current unit
				m = r.matcher(title);
				if (m.find()) {
					headnotes = body;
					isHeadNote = true;
				}
			}
			
			// add only if its not a headnote
			if (!isHeadNote) {
				newdoc = new SolrInputDocument();
				newdoc.addField("id", aUnit.getId());
				newdoc.addField("author", fileId);
				newdoc.addField("bibliography", fileName);
				newdoc.addField("title", aUnit.getTitle());
				newdoc.addField("body", body);
				docs.add(newdoc);
			}
		}
		
		// process headnotes
		if (headnotes != null)
			for (Pair pair : parseHeadnotes(headnotes)) {
				newdoc = new SolrInputDocument();
				newdoc.addField("id", "HN" + date + "#" + cnt++);
				newdoc.addField("author", fileId);
				newdoc.addField("bibliography", fileName);
				newdoc.addField("title", pair.getKey());
				newdoc.addField("body", pair.getValue());
				docs.add(newdoc);				
			}

		addResponse = solrClient.add(collectionId, docs);
		System.out.println(addResponse);
		
		if (addResponse.getStatus() >= 400)
			return false;
		
		addResponse = solrClient.commit(collectionId);
		System.out.println(addResponse);
		return addResponse.getStatus() < 400;
	}
	
	/**
	 * Submit a list of QA pairs for indexing
	 * @param pairs
	 * @return
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	synchronized
	public boolean addQaPairs(List<Pair> pairs) 
			throws SolrServerException, IOException {
		HttpSolrClient solrClient;
		List<SolrInputDocument> docs = new ArrayList<>();
		SolrInputDocument newdoc;
		UpdateResponse addResponse;
		long date = (new Date()).getTime();
		String id;
		int cnt = 0;

		solrClient = getSolrClient(service.getSolrUrl(clusterId), 
				username, password);
		
		for (Pair pair : pairs) {
			// create id (now plus counter for uniqueness)
			id = "QA" + date + "#" + cnt++;
			
			// add the document
			newdoc = new SolrInputDocument();
			newdoc.addField("id", id);
			newdoc.addField("author", "");
			newdoc.addField("bibliography", "");
			newdoc.addField("title", pair.getKey());
			newdoc.addField("body", pair.getValue());
			docs.add(newdoc);
		}
		
		addResponse = solrClient.add(collectionId, docs);
		System.out.println(addResponse);
		
		if (addResponse.getStatus() >= 400)
			return false;
		
		addResponse = solrClient.commit(collectionId);
		System.out.println(addResponse);
		
		return addResponse.getStatus() < 400;
	}

	/**
	 * Authenticates the service
	 * 
	 * @author Fernando Gomez
	 * @throws IOException
	 * @date 3/10/2016
	 */
	private void login() throws IOException {
		JSONParser parser;
		JSONObject obj1, obj2;
		String envValue;
		boolean logged = false;

		envValue = ResourceManager.getEnv(ENV_NAME);
		// parsing...
		parser = new JSONParser();
		try {
			obj1 = (JSONObject) parser.parse(envValue);
			// the structure of the JSON is expected to be
			// {"credentials":{"url":[URL],
			// "username":[UN],"password":[PWD]}}
			if (obj1.containsKey("credentials")) {
				obj2 = (JSONObject) obj1.get("credentials");
				username = obj2.get("username").toString();
				password = obj2.get("password").toString();
				service.setUsernameAndPassword(username, password);
				logged = true;
			}
		} catch (ParseException | RuntimeException e) {
			e.printStackTrace();
		}

		if (!logged)
			throw new IOException("Could not login");
	}

	/**
	 * @return the clusterId
	 */
	public String getClusterId() {
		return clusterId;
	}

	/**
	 * @return the configurationId
	 */
	public String getConfigurationId() {
		return configurationId;
	}

	/**
	 * @return the collectionId
	 */
	public String getCollectionId() {
		return collectionId;
	}

	/**
	 * 
	 * @param uri
	 * @param username
	 * @param password
	 * @return
	 */
	private HttpSolrClient getSolrClient(String uri, String username,
			String password) {
		return new HttpSolrClient(service.getSolrUrl(clusterId),
				createHttpClient(uri, username, password));
	}

	/**
	 * 
	 * @param uri
	 * @param username
	 * @param password
	 * @return
	 */
	private HttpClient createHttpClient(String uri, String username,
			String password) {
		final URI scopeUri = URI.create(uri);

		final BasicCredentialsProvider credentialsProvider = 
				new BasicCredentialsProvider();
		credentialsProvider.setCredentials(
			new AuthScope(scopeUri.getHost(),
				scopeUri.getPort()), 
				new UsernamePasswordCredentials(username,
				password));

		final HttpClientBuilder builder = HttpClientBuilder
				.create()
				.setMaxConnTotal(128)
				.setMaxConnPerRoute(32)
				.setDefaultRequestConfig(
						RequestConfig.copy(RequestConfig.DEFAULT)
								.setRedirectsEnabled(true).build())
				.setDefaultCredentialsProvider(credentialsProvider)
				.addInterceptorFirst(new PreemptiveAuthInterceptor());
		return builder.build();
	}

	/**
	 * @throws IOException
	 * @throws MalformedURLException
	 * 
	 */
	/*
	private String sendRequest(String method, 
			String endpoint, String data)
			throws MalformedURLException, IOException {
		DataOutputStream pr;
		HttpURLConnection conn;
		byte[] bytes = data.getBytes();
		StringBuilder buff;
		BufferedReader in;

		// connection
		conn = (HttpURLConnection) (new URL(endpoint)).openConnection();
		// connection headers
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty(
				"Authorization",
				"Basic " + Base64.getEncoder().encodeToString(
						(username + ":" + password)
								.getBytes(StandardCharsets.UTF_8)));
		// conn.setConnectTimeout(150000);
		conn.setRequestProperty("Content-Length", bytes.length + "");

		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setUseCaches(false);

		// write json to request body
		pr = new DataOutputStream(conn.getOutputStream());
		pr.write(bytes);
		pr.close();

		buff = new StringBuilder();
		try {
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), StandardCharsets.UTF_8));

			for (int c = in.read(); c != -1; c = in.read())
				buff.append((char) c);
			in.close();
		} catch (IOException e) {
			in = new BufferedReader(new InputStreamReader(
					conn.getErrorStream(), StandardCharsets.UTF_8));

			for (int c = in.read(); c != -1; c = in.read())
				buff.append((char) c);

			throw new IOException(buff.toString());
		}

		return buff.toString();
	}
	*/
	
	/**
	 * 
	 * @author Fernando
	 *
	 */
	private class PreemptiveAuthInterceptor implements
			HttpRequestInterceptor {
		public void process(
				final HttpRequest request, 
				final HttpContext context)
				throws HttpException {
			final AuthState authState = (AuthState)context
					.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

			if (authState.getAuthScheme() == null) {
				final CredentialsProvider credsProvider = 
						(CredentialsProvider) context
						.getAttribute(HttpClientContext.CREDS_PROVIDER);
				final HttpHost targetHost = (HttpHost) context
						.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
				final Credentials creds = credsProvider
						.getCredentials(
								new AuthScope(targetHost.getHostName(),
								targetHost.getPort()));
				if (creds == null) {
					throw new HttpException(
						"No creds provided for preemptive auth.");
				}
				authState.update(new BasicScheme(), creds);
			}
		}
	}
	
	/**
	 * Creates QA pairs from the headnotes
	 * @param headnotes
	 * @return
	 */
	private static List<Pair> parseHeadnotes(String headnotes) {
		String body, sentence;
		Pattern pattern1, pattern2; 
		Matcher m1, m2;
		List<Pair> result;
		
		result = new ArrayList<>();
		pattern1 = Pattern.compile(HN_HEADER_TOKEN);
		pattern2 = Pattern.compile(HN_SENTENCE_TOKEN);
		
		// beak headnote section into headnotes
	    m1 = pattern1.matcher(headnotes);
	    
	    while (m1.find()) {
	    	// extract full body text after the HN# indicator
	    	body = m1.group(2);	    	
	    	// break the body into sentences
	    	m2 = pattern2.matcher(m1.group(2));
	    	
	    	while (m2.find()) {
	    		// obtain sentence
	    		sentence = m2.group();	    		
	    		// skip abbreviations..
	    		if (sentence.length() > 5) {
	    			result.add(new Pair(sentence, body));
	    			break;
	    		}
	    	}
	    }
		
		return result;
	}
			
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws IOException, ParseException {
		for (Pair pair : parseHeadnotes("Real Property Law > Financing > Foreclosures > General Overview Real Property Law > Financing > Mortgages & Other Security Instruments > Mortgagee's Interests Real Property Law > Financing > Mortgages & Other Security Instruments > Mortgagor's Interests HN1 The Illinois MortgageForeclosure Lawprovides that in mortgage foreclosure cases involving nonresidential real estate, a mortgagee is entitled to be placed in possession of the property prior to the entry of a judgment of foreclosure upon request, provided that the mortgagee shows (1) that the mortgage or other written instrument authorizes such possession and (2) that there is a reasonable probability that the mortgagee will prevail on a final hearing of the cause. However, if the mortgagor objects and demonstrates \"good cause,\" a court shall allow the mortgagor to remain in possession. 735 ILCS 5/15-1701(b)(2) (2006). Civil Procedure > ... > Receiverships > Receivers > Appointment of Receivers Real Property Law > Financing > Foreclosures > General Overview Real Property Law > Financing > Mortgages & Other Security Instruments > Mortgagee's Interests Real Property Law > Financing > Mortgages & Other Security Instruments > Mortgagor's Interests HN2 The Illinois Mortgage Foreclosure Law ( Foreclosure Law) provides that whenever a mortgagee entitled to possession so requests, a court shall appoint a receiver, 735 ILCS 5/15-1702(a) (2006), and according to 735 ILCS 5/15-1105(b) (2006), \"shall\" means mandatory and not permissive. Therefore, the Foreclosure Law creates a presumption in favor of the mortgagee's right to possession of nonresidential property during the pendency of a mortgage foreclosure proceeding, and a mortgagor can retain possession only if it can show \"good cause\" for permitting it to do so. Civil Procedure > Appeals > Standards of Review > De Novo Review Real Property Law > Financing > Foreclosures > General Overview HN3 Under the Illinois Mortgage Foreclosure Law, the proper standard of review of rulings is de novo. Real Property Law > Financing > Foreclosures > General Overview HN4 A proven default establishes a reasonable probability of succeeding in a mortgage foreclosure action. Whether a default in fact exists will typically turn on the interpretation of documentary evidence, which is a non-discretionary function. Civil Procedure > Appeals > Appellate Jurisdiction > General Overview Civil Procedure > Appeals > Notice of Appeal Governments > Courts > Rule Application & Interpretation HN5 Ill. Sup. Ct. R. 303(b)(2) states that a notice of appeal shall specify the judgment or part thereof or other orders appealed from and the relief sought from the reviewing court. The rule gives an appellant the option of naming either the entire judgment or only a part of the judgment, as the subject of his or her appeal. Ill. Sup. Ct. R. 303(b)(2) . Once the judgment or part is named, the notice of appeal confers jurisdiction on a court of review to consider only the judgments or parts thereof specified in the notice. However, while a notice of appeal is jurisdictional, it is generally accepted that such a notice is to be construed liberally. Therefore, a reviewing court should find that a defect in a notice of appeal is not fatal to the appeal if (1) the defect is in form rather than in substance; and (2) the defect has not prejudiced the opposing party. Civil Procedure > Appeals > Appellate Jurisdiction > General Overview Civil Procedure > Appeals > Notice of Appeal Governments > Courts > Rule Application & Interpretation HN6 An exception to Ill. Sup. Ct. R. 303(b)(2) 's requirement to name the \"judgment or part,\" is for rulings that were necessary steps to the judgment named in the notice. An unspecified judgment is reviewable if the specified judgment directly relates back to it. Contracts Law > Contract Conditions & Provisions > Waivers > General Overview Contracts Law > Defenses > General Overview HN7 A decision by a party to contractually agree to waive all defenses is permitted under Illinois law. Contracts Law > ... > Affirmative Defenses > Coercion & Duress > Economic Duress HN8 Economic duress, also known as business compulsion, is an affirmative defense to a contract, which releases the party signing under duress from all contractual obligations. Duress occurs where one is induced by a wrongful act or threat of another to make a contract under circumstances that deprive one of the exercise of one's own free will. To establish duress, one must demonstrate that the threat has left the individual bereft of the quality of mind essential to the making of a contract. The acts or threats complained of must be wrongful; however, the term \"wrongful\" is not limited to acts that are criminal, tortious, or in violation of a contractual duty. They must extend to acts that are wrongful in a moral sense as well. Contracts Law > ... > Affirmative Defenses > Coercion & Duress > Economic Duress HN9 Where consent to an agreement is secured merely through a demand that is lawful or upon doing or threatening to do that which a party has a legal right to do, economic duress does not exist. Nor can such a defense be predicated on an allegation that consent to an agreement was obtained through hard bargaining positions or financial pressures. Evidence > Burdens of Proof > Allocation Evidence > Burdens of Proof > Ultimate Burden of Persuasion Real Property Law > Financing > Foreclosures > General Overview HN10 The burden to establish good cause under the Illinois Mortgage Foreclosure Law is on the mortgagor, not on the mortgagee. Civil Procedure > ... > Receiverships > Receivers > Appointment of Receivers Real Property Law > Financing > Foreclosures > General Overview Real Property Law > Financing > Mortgages & Other Security Instruments > Mortgagee's Interests Real Property Law > Financing > Mortgages & Other Security Instruments > Mortgagor's Interests HN11 Pursuant to the Illinois Mortgage Foreclosure Law, 735 ILCS 5/15-1702(b) (2006), a mortgagee of nonresidential real estate is entitled to designate a receiver and a court should reject such selection only when the mortgagor objects and shows good cause. Counsel: For Appellants: Lynch & Stern LLP, of Chicago (Daniel Lynch, Avidan J. Stern and Justin D. Kaplan, of counsel) and Jenner & Block, LLP, of Chicago (Robert L. Graham, Barry Levenstam, R. Douglas Rees and J. Andrew Hirth, of counsel). ForAppellee: Seyfarth Shaw, LLP, of Chicago (John H.Anderson, GusA. Paloian and Jerome F. Buch, of counsel). Judges: JUSTICE QUINN delivered the opinion of the court. STEELE and COLEMAN, JJ., concur. Opinion by: QUINN")) {
			System.out.println("Title:");
			System.out.println("-------");
			System.out.println(pair.getKey());
			System.out.println("Body:");
			System.out.println("-------");
			System.out.println(pair.getValue());
			System.out.println("\n\n\n");
		}
	}
}
