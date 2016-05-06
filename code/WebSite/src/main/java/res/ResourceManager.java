package res;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import javax.xml.stream.XMLStreamException;

/**
 * rovides direct access to the resources (utility files, xml, etc).
 * @author		Fernando Gomez
 * @date		Sep 10, 2015
 * @version		1.0.0
 * @description	.	
 */
public class ResourceManager {
	/**
	 * @author	Fernando Gomez
	 * @date	Sep 10, 2015
	 * @param	packageName The name of the package containing the resource
	 * @param	resourceName The name of the file representing the resource
	 * @throws	IOException
	 * @return	The resource contents as a File. All resources are located
	 *			in the /res folder. This method return the 
	 *			contents of the file (resource) as plain text.
	 */
	public static File getResourceAsFile(
			String packageName, String resourceName) 
				throws IOException {
		return new File("res/" + packageName + "/" + resourceName);
	}
	
	/**
	 * @author	Fernando Gomez
	 * @date	Sep 10, 2015
	 * @param	packageName The name of the package containing the resource
	 * @param	resourceName The name of the file representing the resource
	 * @throws	IOException
	 * @return	The resource contents as a String. All resources are located
	 *			in the /res folder. This method return the 
	 *			contents of the file (resource) as plain text.
	 */
	public static String getResourceAsText(
			String packageName, String resourceName) 
				throws IOException {
		String r;
		InputStream is;
		Scanner s;
		
		is = (new ResourceManager())
				.getClass().getClassLoader().getResourceAsStream("res/" + 
    			packageName + "/" + resourceName);		
		s = new Scanner(is);
		s.useDelimiter("\\A");		
		r = s.hasNext() ? s.next() : null;
		s.close();
		is.close();
		
		return r;
	}
	
	/**
	 * @author	Fernando Gomez
	 * @date	Sep 10, 2015
	 * @param	packageName The name of the package containing the resource
	 * @param	resourceName The name of the file representing the resource
	 * @throws	IOException	
	 * @throws	javax.xml.stream.XMLStreamException
	 * @return	The resource contents as an XML Node. All resources are located
	 *			in the /res folder. This method return the contents of the file
	 *			(resource) as a Node (DOM).
	 */
	public static XmlNode getResourceAsXml(
			String packageName, String resourceName) 
				throws IOException, XMLStreamException {		
		return new XmlNode(getResourceAsText(packageName, resourceName));
	}
	
	/**
	 * @author	Fernando Gomez 
	 * @param name The name of the environment variable to fetch
	 * @return	<p>
	 *			The value associated with the environment variable requested
	 *			in the name attribute. This method throws no exceptions; it 
	 *			returns null if the environment variable is not found.
	 *			</p>
	 *			<p>	
	 *			This method searches in two sources:
	 *			</p>
	 *			<ol>
	 *				<li>
	 *				The system environment variables such as PATH or 
	 *				JAVA_HOME.
	 *				</li>
	 *				<li>
	 *				The file env.xml which contains custom environment 
	 *				variables declared by the administrator.
	 *				</li>
	 *			</ol>
	 *			<p>
	 *			The search is performed in the order specified above. If the 
	 *			variable is found in the system, the the system value is 
	 *			returned, regardless whether there's a member with the same
	 *			name in the env.xml file.
	 *			</p>
	 *			<p>
	 *			The env.xml is searched only if is present. If this file is 
	 *			missing, this search is skipped.
	 *			</p>
	 */
	public static String getEnv(String name) {
		if (name == null)
			return null;
		
		String result = System.getenv(name);		
		if (result == null) {  
			try { 
				Map<String, String> envs =
						getResourceAsXml("xml", "env.xml").getValuesMap();
				
				if (envs.containsKey("env::" + name))
					result = envs.get("env::" + name);
			} catch (IOException | XMLStreamException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return result;
	}
}
