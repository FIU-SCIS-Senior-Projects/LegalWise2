/**
 * 
 */
package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.parser.ParseException;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;
import data.Connector;
import qa.DocumentConverter;
import qa.RetrieveAndRankWrapper;
import wrapper.Document;
import wrapper.DocumentStatus;
import wrapper.User;

/**
 * @author Fernando
 *
 */
public class UploadService extends Service {
	private User user;
	private Document document;
	private Answers answers;
	
	/**
	 * Main constructor
	 * @param request
	 * @param response
	 */
	public UploadService(User user,
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
		RetrieveAndRankWrapper ranker;
		Connector conn = new Connector();
		
		if (getDocument()) {			
			if (!conn.addDocument(document))
				document.setStatus(DocumentStatus.ERROR);
			else {
				// activate document async to avoid 
				// blocking user experience
				try {
					ranker = new RetrieveAndRankWrapper();
					document.setStatus(
							ranker.addDocuments(document, answers) ? 
									DocumentStatus.ACTIVE :
									DocumentStatus.INACTIVE);
				} catch (IOException | ParseException |
						RuntimeException | SolrServerException e) {
					e.printStackTrace();
					document.setStatus(DocumentStatus.INACTIVE);
				}
				
				conn.updateDocument(document);
			}
			
			setResponse(200, document.toString());			
		} 
	}
	
	/**
	 * Parses the request and obtains the document to add
	 * @return
	 */
	private boolean getDocument() {
		Part part = null;
		DocumentConverter d = null;
		Path p;
		File f = null;
		wrapper.File file;
		String fileName = null; //, plainText;
		byte[] body;
		
		// obtain parts, or halt with error in invalid format
		try {
			part = request.getPart("file");
			fileName = getFileName(part);
		} catch (IOException | ServletException e) {
			e.printStackTrace();
			setResponse(400, getBasicResponseJson(400, e.getMessage()));
			return false;
		}
		
		try {
			// create the document converter			
			d = new DocumentConverter();
			
			// place the file binary data in the body array
			body = new byte[(int)part.getSize()];			
			part.getInputStream().read(body);
			
			// create the file and write
			p = Paths.get(fileName);
			Files.write(p, body);
			
			// create File instance to invoke conversion
			f = new File(p.toUri());
			
			// extract text
			answers = d.convertToAnswers(f);
			//plainText = d.convertToText(f);
			
			// create document
			file = new wrapper.File(null, fileName, part.getSize(), 
					part.getContentType(), body);
			document = new Document(null, new Date(), user, file, 
					null, //plainText, 
					DocumentStatus.PENDING_ACTIVATION);
			
			return true;
		} catch (Exception e) {			
			e.printStackTrace();
			setResponse(500, getBasicResponseJson(500, e.getMessage()));
			return false;
		}
	}
	
	/**
	 * 
	 * @param part
	 * @return
	 * @throws IOException 
	 */
	private String getFileName(final Part part) throws IOException {
	    for (String content : 
	    	part.getHeader("Content-Disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	        	return content.substring(
	        			content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    
	    throw new IOException("Filename header not found");
	}
	
	/**
	 * Abstract class. Implementations will be used to run operations
	 * asynchronously.
	 *
	 * @author		Fernando
	 * @date		Nov 03, 2015
	 */
	/*
	private abstract class Async implements Runnable {
		protected final Document document;
		
		public Async(Document document) {
			this.document = document;
		}	
	}
	*/
}
