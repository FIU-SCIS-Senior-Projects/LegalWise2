/**
 * 
 */
package wrapper;

import org.json.simple.JSONObject;

/**
 * @author Fernando
 *
 */
public class File {
	private Integer fileId;
	private String name;
	private Long size;
	private String mimeType;
	private byte[] body;
	
	/**
	 * Informationless constructor
	 */
	public File() {
		this(null, null, null, null, null);
	}
	
	/**
	 * Main Constructor
	 * @param fileId
	 * @param name
	 * @param size
	 * @param body
	 */
	public File(Integer fileId, String name, 
			Long size, String mimeType, byte[] body) {
		this.fileId = fileId;
		this.name = name;
		this.size = size;
		this.mimeType = mimeType;
		this.body = body;
	}

	/**
	 * @return the fileId
	 */
	public Integer getFileId() {
		return fileId;
	}

	/**
	 * @param fileId the fileId to set
	 */
	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param size the mimeType to set
	 */
	public void setSize(String mimeType) {
		this.mimeType = mimeType;
	}
	
	/**
	 * @return the size
	 */
	public Long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
	}

	/**
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		obj.put("fileId", fileId);
		obj.put("name", name);
		obj.put("size", size);
		obj.put("mimeType", mimeType);
		return obj;
	}
	
	@Override
	public String toString() {
		return toJson().toJSONString();
	}
}
