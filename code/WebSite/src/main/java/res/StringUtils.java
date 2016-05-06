package res;

import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * Contains helper methods to work with strings
 * @author		Fernando
 * @date		2/8/2016
 */
public class StringUtils {
	// 16 bytes for AES
	private static final String SECRET_KEY = "XyYYSxpppNmReRMA"; 
	private static final String ALGORYTHN =  "AES";
	
	/**
	 * @author		Fernando
	 * @date		2/8/2016
	 * @param it		Any implementation of Iterator
	 * @param separator	String that will separate each 'it'	 element
	 * @return			A String containing a junction of all strings in the
	 *					iterator list separated by separator
	 */
	public static String join(Iterator<String> it, String separator) {
		if (!it.hasNext())
			return "";
		
		StringBuilder result = new StringBuilder();
		while (true) {
			result.append(it.next());
			if (it.hasNext())
				result.append(separator);
			else
				break;
		}
		
		return result.toString();
	}
	
	/**
	 * @author		Fernando
	 * @date		2/8/2016 
	 * @param src
	 * @param ids
	 * @return 
	 */
	public static String replace(String src, Map<String, String> ids) {
		if (ids == null || ids.isEmpty())
			return src;
		
		StringBuilder bl = new StringBuilder(src);
		Iterator<Map.Entry<String, String>> it = ids.entrySet().iterator();
		String curr;
		Map.Entry<String, String> pair;
		int start, end;
		// replace all identifiers
		while (it.hasNext()) {			
			pair = it.next();			
			curr = pair.getValue() == null ? "" : pair.getValue();
			start = bl.indexOf(pair.getKey());
			
			while (start != -1) {
				end = start + pair.getKey().length();
				bl.replace(start, end, curr);
				// move next
				start = bl.indexOf(pair.getKey(), start + curr.length());
			}
		}
		
		return bl.toString();
	}
	
	/**	 
	 * Encodes a string to be able to use a text for an XML node with
	 * without creating conflicts with tags and directives.
	 * @author		Fernando
	 * @date		2/8/2016 
	 * @param src
	 * @return 
	 */
	public static String encodeXml(String src) {		
		Map<String, String> chars = new LinkedHashMap<>();		
		chars.put("&", "&amp;");
		chars.put("\"", "&quot;");
		chars.put("'", "&apos;");
		chars.put("<", "&lt;");
		chars.put(">", "&gt;");
		
		return StringUtils.replace(src, chars);
	}
	
	/**	 
	 * Repeats a string as many time as request.
	 * @author		Fernando
	 * @date		2/8/2016
	 * @param src
	 * @param times
	 * @return 
	 */
	public static String repeat(String src, int times) {
		StringBuilder result = new StringBuilder();
		
		while (times-- > 0) 
			result.append(src);
		
		return result.toString();
	}
	
	/**
	 * Encrypts a specified string using a predetermined encryption algorithm
	 * and private key. The algorithm and key a re not public, therefore, to 
	 * reverse the result of the method the method 'decrypt' included in this
	 * class should be used.
	 * 
	 * @author		Fernando
	 * @date		2/8/2016 
	 * @param src
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException 
	 */
	public static String encrypt(String src) 
			throws InvalidKeyException, NoSuchAlgorithmException, 
				IllegalBlockSizeException, NoSuchPaddingException, 
				BadPaddingException {
		Key key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORYTHN);
        Cipher c = Cipher.getInstance(ALGORYTHN);
        c.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(
				c.doFinal(src.getBytes()));
	}
	
	/**
	 * Reverses the encryption created by the encrypt method of this class.
	 * Make sure the encrypted data passed to this method was encrypted 
	 * by this same class to preserve key information which is private.
	 * 
	 * @author		Fernando
	 * @date		2/8/2016
	 * @param src
	 * @return
	 * @throws Exception 
	 */
	public static String decrypt(String src) throws NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORYTHN);
        Cipher c = Cipher.getInstance(ALGORYTHN);
        c.init(Cipher.DECRYPT_MODE, key);
        return new String(
				c.doFinal(Base64.getDecoder().decode(src.getBytes())));
    }
	
	/**
	 * @author		Fernando
	 * @date		Sep 22, 2015
	 * @version		1.0.0 
	 * @param src
	 * @return		A one-way hashed version of 'src' 
	 */
	public static String getHash(String src) 
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] output;
		MessageDigest digest;
		
		digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		
		output = digest.digest(src.getBytes("UTF-8"));		
		return Base64.getEncoder().encodeToString(output);
	}
	
	/**
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) 
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if (args.length == 0)
			System.out.println("Invalid option");
		else 
			switch (args[0]) {
				case "hash1":
					if (args.length < 2)
						System.out.println("Missing string to hash");
					else {
						System.out.println("One way hashing string: '" + 
							args[1] + "': ");
						System.out.println(getHash(args[1]));
					}
					break;
				default:
					System.out.println("Method not found");
					break;
			}
	} 
}

