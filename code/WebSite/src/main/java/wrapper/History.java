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
public class History {
	private Integer historyId;
	private String searchText;
	private Date performedOn;
	private User user;
	
	/**
	 * 
	 * @param historyId
	 * @param searchText
	 * @param performedOn
	 * @param user
	 */
	public History(Integer historyId, String searchText,
			Date performedOn, User user) {
		this.historyId = historyId;
		this.searchText = searchText;
		this.performedOn = performedOn;
		this.user = user;
	}

	/**
	 * @return the historyId
	 */
	public Integer getHistoryId() {
		return historyId;
	}

	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}

	/**
	 * @return the performedOn
	 */
	public Date getPerformedOn() {
		return performedOn;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param historyId the historyId to set
	 */
	public void setHistoryId(Integer historyId) {
		this.historyId = historyId;
	}

	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	/**
	 * @param performedOn the performedOn to set
	 */
	public void setPerformedOn(Date performedOn) {
		this.performedOn = performedOn;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONObject getAsJson() {
		JSONObject obj = new JSONObject();
		obj.put("historyId", historyId);
		obj.put("searchText", searchText);
		obj.put("performedOn", 
				performedOn == null ? null : performedOn.getTime());
		obj.put("user", user == null ? null : user.getAsJson());
		return obj;
	}
	
	@Override
	public String toString() {		
		return getAsJson().toString();
	}
}
