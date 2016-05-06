package wrapper;

/**
 * Represent a key-value pair for String types
 * @author Fernando
 */
public class Pair {
	private String key;
	private String value;
	
	/**
	 * Constructor
	 * @param key
	 * @param value
	 */
	public Pair(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
