package data;

import res.ResourceManager;
import wrapper.Document;
import wrapper.DocumentStatus;
import wrapper.File;
import wrapper.History;
import wrapper.User;
import wrapper.UserType;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException; 

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a connection mechanism for the app database
 * @author 	Fernando
 * @date	02/08/2015
 */
public class Connector {
	private static String ENV_NAME = "VCAP_SERVICES";
	private static String POSGRESQL_KEY = "elephantsql";
	private String dbUrl;
	private Connection conn;
	private Exception last;
	
	/**
	 * Default constructor. Obtains url for further connections.
	 * @author 	Fernando
	 * @date	02/08/2015 
	 */
	public Connector() {
		// obtain db ulr
		obtainDbUrl();
	}
	
	/**
	 * @return  
	 */
	public String getDbUrl() {
		return dbUrl;
	}
	
	/**
	 * Connects to the database.
	 * @author 	Fernando
	 * @date	02/08/2015  
	 * @return	True if connection was successful, false otherwise
	 */
	private boolean connect() {
		URI uri;
		String userInfo;
		String[] credentials;
		
		if (dbUrl == null)
			return false;
		
		try {			
			uri = new URI(dbUrl);
			userInfo = uri.getUserInfo();
			
			if (userInfo == null || 
					(credentials = userInfo.split(":")).length < 2)
				return false;

			conn = DriverManager.getConnection(
				"jdbc:postgresql://" + uri.getHost() + // db url
					':' + uri.getPort() + uri.getPath(), 
				credentials[0], // username
				credentials[1]); // password
			//conn.setAutoCommit(false);
			return true;
		} catch (URISyntaxException | SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Terminates the connection, if one existed.	 * 
	 * @author		Fernando
	 * @date		Feb 17, 2016
	 */
	private void disconnect() {
		if (conn != null) 
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		// release resources anyway
		conn = null;
	}
	
	/**
	 * Resets the database. Clears all tables, view, relationships, etc,
	 * plus, it deletes all rows.
	 * @author 	Fernando
	 * @date	02/08/2015 
	 * @return	true is success, false otherwise.
	 */
	public boolean create() {
		if (conn == null && !connect())
			return false;
		
		try {
			PreparedStatement st;
			String sql = ResourceManager.getResourceAsText("sql", "db.sql");
			
			st = conn.prepareStatement(sql);
			st.executeUpdate();
			st.close();
			
			disconnect();
			return true;
		} catch (SQLException e) {
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			last = e;
		}
		
		disconnect();
		return false;
	}
	
	/**
	 * Return a page of 20 users starting from the specified offset.
	 * @author 	Fernando
	 * @date	02/08/2015 
	 * @return	User[] is success, null otherwise.
	 */
	public User[] getUsers(int offset, String textFilter) {
		if (conn == null && !connect())
			return null;

		try {
			String f = "%" + (textFilter == null ? "" : textFilter) + "%";
			List<User> users = new ArrayList<User>();
			PreparedStatement st;
			ResultSet rs;
			String sql = ResourceManager.getResourceAsText("sql", 
					"getUsers.sql");
			
			st = conn.prepareStatement(sql);
			st.setString(1, f);
			st.setString(2, f);
			st.setString(3, f);
			st.setString(4, f);
			st.setString(5, "firstName");
			st.setInt(6, offset);
			
			rs = st.executeQuery();
			while (rs.next())
				users.add(new User(
					rs.getInt("userId"),
					rs.getString("firstName"),
					rs.getString("lastName"),
					rs.getString("companyName"),
					rs.getString("email"),
					UserType.valueOf(rs.getString("type")),
					rs.getTimestamp("createdOn"),
					rs.getBoolean("isTrial"),
					rs.getInt("trialDuration"),
					rs.getBoolean("isLocked"),
					rs.getTimestamp("modifiedOn"),
					rs.getBoolean("isActive")));
			
			st.close();
			disconnect();
			return users.toArray(new User[users.size()]);	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return null;
	}
	
	/**
	 * Updates the properties of the specified user.
	 * @author 	Fernando
	 * @date	02/23/2015 
	 * @return	true if successful, false otherwise
	 */
	public boolean updateUser(User user) {
		if (conn == null && !connect())
			return false;

		try {
			PreparedStatement st;
			int rs;
			String sql = ResourceManager.getResourceAsText("sql", 
					"updateUser.sql");
			
			st = conn.prepareStatement(sql);
			st.setString(1, user.getFirstName());
			st.setString(2, user.getLastName());
			st.setString(3, user.getCompanyName());
			st.setString(4, user.getEmail());
			st.setString(5, user.getType().toString());
			st.setBoolean(6, user.isTrial());
			st.setInt(7, user.getTrialDuration());
			st.setBoolean(8, user.isLocked());
			st.setBoolean(9, user.isActive());
			st.setInt(10, user.getUserId());
			
			rs = st.executeUpdate();
			st.close();
			disconnect();
			return rs > 0;	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return false;
	}
	
	/**
	 * Returns the User with the specified credentials
	 * @author 	Fernando
	 * @date	02/23/2015 
	 * @return	User if success, null if not found (non existent user
	 * 			or invalid credentials), user is locked.
	 */
	public User getUserByCredentials(String un, String pw) {
		if (conn == null && !connect())
			return null;

		try {
			User user = null;
			PreparedStatement st;
			ResultSet rs;
			String sql = ResourceManager.getResourceAsText("sql", 
					"getUserByCredentials.sql");
			
			st = conn.prepareStatement(sql);
			st.setString(1, un);
			st.setString(2, pw);
			
			rs = st.executeQuery();
			if (rs.next())
				user = new User(
					rs.getInt("userId"),
					rs.getString("firstName"),
					rs.getString("lastName"),
					rs.getString("companyName"),
					rs.getString("email"),
					UserType.valueOf(rs.getString("type")),
					rs.getTimestamp("createdOn"),
					rs.getBoolean("isTrial"),
					rs.getInt("trialDuration"),
					false,
					rs.getTimestamp("modifiedOn"),
					true);
			
			st.close();
			disconnect();
			return user;	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return null;
	}
	
	/**
	 * Return all settings in the database
	 * @author 	Fernando
	 * @date	03/10/2015
	 * @return
	 */
	public Map<String, String> getSettings() {
		if (conn == null && !connect())
			return null;
		
		try {
			Map<String, String> result = new HashMap<>();
			PreparedStatement st;
			ResultSet rs;
			String sql = ResourceManager.getResourceAsText("sql", 
					"getSettings.sql");
			
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			
			while (rs.next()) 
				result.put(rs.getString("name"), 
						rs.getString("value"));
			
			st.close();
			disconnect();
			return result;	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return null;
	}
	
	/**
	 * Return all settings in the database
	 * @author 	Fernando
	 * @date	03/10/2015
	 * @return
	 */
	public boolean saveSettings(Map<String, String> settings) {
		if (conn == null && !connect())
			return false;
		
		try {
			PreparedStatement st;
			String sql = ResourceManager.getResourceAsText("sql", 
					"updateSettings.sql");
			
			for (String key : settings.keySet()) {
				st = conn.prepareStatement(sql);
				st.setString(1, key);
				st.setString(2, settings.get(key));
				st.executeUpdate();
				st.close();
			}
			
			disconnect();
			return true;	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return false;
	}
	
	/**
	 * Insert a document into the database
	 * @author 	Fernando
	 * @date	03/10/2015
	 * @return
	 */
	public boolean addDocument(Document document) {
		if (conn == null && !connect())
			return false;
		
		try {
			ResultSet rs;
			PreparedStatement stFile, stDocument;
			String sql = ResourceManager.getResourceAsText("sql", 
					"insertFile.sql");
			
			// insert file
			stFile = conn.prepareStatement(sql, 
					Statement.RETURN_GENERATED_KEYS);
			stFile.setString(1, document.getFile().getName());
			stFile.setLong(2, document.getFile().getSize());
			stFile.setString(3, document.getFile().getMimeType());
			stFile.setBytes(4, document.getFile().getBody());
			stFile.executeUpdate();
			rs = stFile.getGeneratedKeys();
			rs.next();
			// update the file
			document.getFile().setFileId(rs.getInt(1));
			stFile.close();
			
			// insert document
			sql = ResourceManager.getResourceAsText("sql", 
					"insertDocument.sql");
			stDocument = conn.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			stDocument.setTimestamp(1, Timestamp.from(
					document.getUploadedOn().toInstant()));
			stDocument.setInt(2, document.getUploadedBy().getUserId());
			stDocument.setInt(3, document.getFile().getFileId());
			stDocument.setString(4, document.getPlainText());
			stDocument.setString(5, document.getStatus().toString());
			stDocument.executeUpdate();
			rs = stDocument.getGeneratedKeys();
			rs.next();
			// update the document
			document.setDocumentId(rs.getInt(1));
			stDocument.close();
			
			disconnect();
			return true;	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return false;
	}
	
	/**
	 * Update a document in the database
	 * @author 	Fernando
	 * @date	03/10/2015
	 * @return
	 */
	public boolean updateDocument(Document document) {
		if (conn == null && !connect())
			return false;
		
		try {
			PreparedStatement stDocument;
			String sql = ResourceManager.getResourceAsText("sql", 
					"updateDocument.sql");
			stDocument = conn.prepareStatement(sql);
			stDocument.setString(1, document.getStatus().toString());
			stDocument.setInt(2, document.getDocumentId());
			stDocument.executeUpdate();
			stDocument.close();
			
			disconnect();
			return true;	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return false;
	}
	
	/**
	 * Add history.
	 * History items are queries performed by users.
	 * @author 	Fernando
	 * @date	03/10/2015
	 * @return
	 */
	public boolean addHistory(History history) {
		if (conn == null && !connect())
			return false;
		
		try {
			PreparedStatement stHistory;
			String sql = ResourceManager.getResourceAsText("sql", 
					"insertHistory.sql");
			stHistory = conn.prepareStatement(sql);
			stHistory.setString(1, history.getSearchText());
			stHistory.setInt(2, history.getUser().getUserId());
			stHistory.executeUpdate();
			stHistory.close();
			
			disconnect();
			return true;	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return false;
	}
	
	/**
	 * Retrieves the recent search history of a specified user
	 * @author 	Fernando
	 * @date	03/10/2015
	 * @return
	 */
	public History[] getHistory(User user) {
		if (conn == null && !connect())
			return null;
		
		try {
			ArrayList<History> history = new ArrayList<>();
			ResultSet rsHistory;
			PreparedStatement stHistory;
			String sqlHistory = ResourceManager.getResourceAsText("sql", 
					"getHistory.sql");			
			
			stHistory = conn.prepareStatement(sqlHistory);
			stHistory.setInt(1, user.getUserId());
			rsHistory = stHistory.executeQuery();
			
			while (rsHistory.next()) {				
				history.add(
					new History(
						rsHistory.getInt("historyId"),
						rsHistory.getString("searchText"),
						rsHistory.getTimestamp("performedOn"), 
						user));
				System.out.println(rsHistory.getDate("performedOn"));
			}			
			
			stHistory.close();
			
			disconnect();
			return history.toArray(new History[history.size()]);	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return null;
	}
	
	/**
	 * Rerieves a FILE including its bytes
	 * @author 	Fernando
	 * @date	03/10/2015
	 * @return
	 */
	public File getFile(int fileId) {
		if (conn == null && !connect())
			return null;
		
		try {
			File result = null;
			ResultSet rs;
			PreparedStatement stFile;
			String sql = ResourceManager.getResourceAsText("sql", 
					"getFileToDownload.sql");
			stFile = conn.prepareStatement(sql);
			stFile.setInt(1, fileId);
			rs = stFile.executeQuery();
			
			if (rs.next())
				result = new File(
						rs.getInt("fileId"),
						rs.getString("name"),
						rs.getLong("size"),
						rs.getString("mimeType"),
						rs.getBytes("body"));
			
			stFile.close();			
			disconnect();
			return result;	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return null;
	}
	
	/**
	 * Retrieves a list of documents
	 * @author 	Fernando
	 * @date	03/10/2015
	 * @return
	 */
	public Document[] getDocuments(int offset) {
		if (conn == null && !connect())
			return null;
		
		try {
			ArrayList<Document> documents = new ArrayList<>();
			ResultSet rsDoc;
			PreparedStatement stDocument;
			String sqlDoc = ResourceManager.getResourceAsText("sql", 
					"getDocuments.sql");			
			
			stDocument = conn.prepareStatement(sqlDoc);
			stDocument.setInt(1, offset);
			rsDoc = stDocument.executeQuery();
			
			while (rsDoc.next()) {				
				documents.add(new Document(
						rsDoc.getInt("documentId"), 
						rsDoc.getDate("uploadedOn"), 
						null, 
						rsDoc.getInt("fileId") > 0 ? 
							new File(
								rsDoc.getInt("fileId"), 
								rsDoc.getString("name"),
								rsDoc.getLong("size"),
								rsDoc.getString("mimeType"), 
								null) : 
							null, 
						null, 
						DocumentStatus.valueOf(
								rsDoc.getString("status"))));
			}			
			
			stDocument.close();
			
			disconnect();
			return documents.toArray(new Document[documents.size()]);	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return null;
	}
	
	/**
	 * Retrieves a documents mapped by their ids
	 * @author 	Fernando
	 * @date	03/10/2015
	 * @param	docIds
	 * @return
	 */
	public Map<Integer, Document> getDocuments(Set<Integer> docIds) {
		if (conn == null && !connect())
			return null;
		
		try {
			Map<Integer, Document> documents = new HashMap<>();
			// proceed only if there are docs to fins
			if (!docIds.isEmpty()) {
				ResultSet rsDoc;
				PreparedStatement stDocument, stFile;
				String sqlDoc = ResourceManager.getResourceAsText("sql", 
						"getDocumentsWithFiles.sql").replace(
								"?", 
								"(" + StringUtils.join(docIds, ",") + ")");			
				
				stDocument = conn.prepareStatement(sqlDoc);			
				rsDoc = stDocument.executeQuery();
				
				while (rsDoc.next()) {				
					documents.put(
						rsDoc.getInt("documentId"),	
						new Document(
							rsDoc.getInt("documentId"), 
							rsDoc.getDate("uploadedOn"), 
							null, 
							rsDoc.getInt("fileId") > 0 ? 
								new File(
									rsDoc.getInt("fileId"), 
									rsDoc.getString("name"),
									rsDoc.getLong("size"),
									rsDoc.getString("mimeType"), 
									null) : 
								null, 
							null, 
							DocumentStatus.valueOf(
									rsDoc.getString("status"))));
				}			
				
				stDocument.close();
			}
			
			disconnect();
			return documents;	
		} catch (SQLException e) {
			e.printStackTrace();
			last = e.getNextException() != null ? 
					e.getNextException() : e;
		} catch (IOException e) {
			e.printStackTrace();
			last = e;
		}
		
		disconnect();
		return null;
	}

	/**
	 * Private helper that obtains the url of the database linked to this
	 * app. Should work both remotely and locally as the 
	 * ResourceManager.getEnv method will use the local version of
	 * the environment variables if the System returns a null value. This
	 * local version contains the URL specified in the xml file "env.xml",
	 * which should contain a tag named VCAP_SERVICES with the local url
	 * to be used in case the app is running locally. Change this value
	 * to your local database (using the proper JSON formatting):<br/>
	 * {"postgresql-9.1":[{"credentials":{uri": "[URL]"}}]}
	 * @author 	Fernando
	 * @date	02/08/2015
	 * @return
	 */
	private void obtainDbUrl() {
		JSONParser parser;
		JSONObject obj1, obj2;
		JSONArray arr;
		String envValue;
		
		envValue = ResourceManager.getEnv(ENV_NAME);
		// env information should come as a JSON string, which needs 
		// parsing...
		parser = new JSONParser();
		try {
			obj1 = (JSONObject)parser.parse(envValue);
			// the structure of the JSON is expected to be
			// {"postgresql-9.1":[{"credentials":{uri": "[URL]"}}]}
			if (obj1.containsKey(POSGRESQL_KEY)) {
				arr = (JSONArray)obj1.get(POSGRESQL_KEY);				
				if (!arr.isEmpty()) {
					obj2 = (JSONObject)arr.get(0);
					if (obj2.containsKey("credentials"))
						dbUrl = (String)((JSONObject)obj2.get(
								"credentials")).get("uri");
				}
			}
		} catch (ParseException | RuntimeException e) {
			// null at this point;
			dbUrl = null;
			e.printStackTrace();
		}
	}
	
	/**
	 * @author 	Fernando 
	 * @return	A message describing the last error occurred
	 */
	public String getLastError() {
		return last == null ? null : last.getMessage();
	}
}
