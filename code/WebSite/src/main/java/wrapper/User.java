package wrapper;

import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author Fernando
 *
 */
public class User {
	private int userId;
	private String firstName;
	private String lastName;
	private String companyName;
	private String email;
	private UserType type;
	private Date createdOn;
	private boolean isTrial;
	private int trialDuration; // in days
	private boolean isLocked;
	private Date modifiedOn;
	private boolean isActive;
	
	/**
	 * Base constructor, no params
	 */
	public User() {
		this(-1, null, null, null, null, UserType.GUEST, 
				null, false, 0, false, null, true);
	}
	
	/**
	 * Constructor. All information.
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param companyName
	 * @param email
	 * @param type
	 * @param createdOn
	 * @param isTrial
	 * @param trialDuration
	 * @param isLocked
	 */
	public User(int userId,
			String firstName,
			String lastName,
			String companyName,
			String email,
			UserType type,
			Date createdOn,
			boolean isTrial,
			int trialDuration,
			boolean isLocked,
			Date modifiedOn,
			boolean isActive) {
		setUserId(userId);
		setFirstName(firstName);
		setLastName(lastName);
		setCompanyName(companyName);
		setEmail(email);
		setType(type);
		setCreatedOn(createdOn);
		setTrial(isTrial);
		setTrialDuration(trialDuration);
		setLocked(isLocked);
		setModifiedOn(modifiedOn);
		setActive(isActive);
	}

	/**
	 * @return the id
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param id the id to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the type
	 */
	public UserType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(UserType type) {
		this.type = type;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the isTrial
	 */
	public boolean isTrial() {
		return isTrial;
	}

	/**
	 * @param isTrial the isTrial to set
	 */
	public void setTrial(boolean isTrial) {
		this.isTrial = isTrial;
	}

	/**
	 * @return the trialDuration
	 */
	public int getTrialDuration() {
		return trialDuration;
	}

	/**
	 * @param trialDuration the trialDuration to set
	 */
	public void setTrialDuration(int trialDuration) {
		this.trialDuration = trialDuration;
	}
	
	/**
	 * @return whether this user is locked
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * @param isLocked the isLocked to set
	 */
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
	
	/**
	 * @return the modifiedOn
	 */
	public Date getModifiedOn() {
		return modifiedOn;
	}

	/**
	 * @param createdOn the modifiedOn to set
	 */
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	/**
	 * @return whether this user is active
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * Return a JSON version of the user
	 * @return JSONObject
	 */
	public JSONObject getAsJson() {
		JSONObject obj = new JSONObject();		
		obj.put("userId", userId);
		obj.put("firstName", firstName);
		obj.put("lastName", lastName);
		obj.put("companyName", companyName);
		obj.put("email", email);
		obj.put("type", type == null ? null : type.toString());
		obj.put("createdOn", createdOn == null ? 
				null : createdOn.getTime());
		obj.put("isTrial", isTrial);
		obj.put("trialDuration", trialDuration);
		obj.put("isLocked", isLocked);
		obj.put("modifiedOn", modifiedOn == null ? 
				null : modifiedOn.getTime());
		obj.put("isActive", isActive);
		return obj;
	}
	
	/**
	 * 
	 * @param json
	 * @return
	 * @throws ParseException
	 */
	public static User fromJson(String json) 
			throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject obj;
		
		obj = (JSONObject)parser.parse(json);
		return new User(
			Integer.valueOf(obj.get("userId").toString()),
			obj.get("firstName").toString(),
			obj.get("lastName").toString(),
			obj.get("companyName").toString(),
			obj.get("email").toString(),
			UserType.valueOf(obj.get("type").toString()),
			new Date(Long.valueOf(obj.get("createdOn").toString())),
			Boolean.valueOf(obj.get("isTrial").toString()),
			obj.get("trialDuration") == null ?
				0 : Integer.valueOf(obj.get("trialDuration").toString()),
			Boolean.valueOf(obj.get("isLocked").toString()),
			new Date(Long.valueOf(obj.get("modifiedOn").toString())),
			Boolean.valueOf(obj.get("isActive").toString())
		);
	}
	
	@Override
	public String toString() {		
		return getAsJson().toString();		
	}
}
