/**
 * 
 */
package wrapper;

import java.util.Date;

import org.json.simple.JSONObject;

/**
 * @author Fernando
 *
 */
public class Document {
	private Integer documentId;
	private Date uploadedOn;
	private User uploadedBy;
	private File file;
	private String plainText;
	private DocumentStatus status;
	
	/**
	 * Main constructor
	 * @param documentId
	 * @param uploadedOn
	 * @param uploadedBy
	 * @param file
	 * @param plainText
	 */
	public Document(Integer documentId, Date uploadedOn,
			User uploadedBy, File file, 
			String plainText, DocumentStatus status) {
		this.documentId = documentId;
		this.uploadedOn = uploadedOn;
		this.uploadedBy = uploadedBy;
		this.file = file;
		this.plainText = plainText;
		this.status = status;
	}

	/**
	 * Return the Id of the current document...
	 * @return the documentId
	 */
	public Integer getDocumentId() {
		return documentId;
	}
	
	/**
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	/**
	 * @return the uploadedOn
	 */
	public Date getUploadedOn() {
		return uploadedOn;
	}

	/**
	 * @param uploadedOn the uploadedOn to set
	 */
	public void setUploadedOn(Date uploadedOn) {
		this.uploadedOn = uploadedOn;
	}

	/**
	 * @return the uploadedBy
	 */
	public User getUploadedBy() {
		return uploadedBy;
	}

	/**
	 * @param uploadedBy the uploadedBy to set
	 */
	public void setUploadedBy(User uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the plainText
	 */
	public String getPlainText() {
		return plainText;
	}

	/**
	 * @param plainText the plainText to set
	 */
	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}
	
	/**
	 * @return the status
	 */
	public DocumentStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(DocumentStatus status) {
		this.status = status;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONObject getAsJson() {
		JSONObject obj = new JSONObject();
		obj.put("documentId", documentId);
		obj.put("uploadedOn", 
				uploadedOn == null ? null : uploadedOn.getTime());
		obj.put("uploadedBy", 
				uploadedBy == null ? null : uploadedBy.toString());
		obj.put("file", file == null ? null : file.toJson());
		obj.put("plainText", plainText == null ? null : 
			plainText.length() <= 50 ? plainText : plainText.substring(0, 50));
		obj.put("status", status == null ? null : status.toString());
		return obj;
	}
	
	@Override
	public String toString() {		
		return getAsJson().toString();
	}
}
