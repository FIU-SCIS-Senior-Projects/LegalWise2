package res;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author		Fernando R Gomez Reyss
 * @date		2/8/2016
 * @description	XmlNode	
 */
public class XmlNode {
	// navigation references
	private XmlNode nextSibling;
	private XmlNode prevSibling;
	private XmlNode firstChild;
	private XmlNode lastChild;
	private XmlNode parent;
	
	// node attrs and values
	// private Map<String, String> attrs;
	private String name;
	private String value;
	
	/**
	 * Class constructor. This overload 
	 * construct the node structure
	 * based on an xml string
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016	 
	 * @throws		javax.xml.stream.XMLStreamException
	 * @param xml 
	 */
	public XmlNode(String xml) throws XMLStreamException {
		parseXml(xml);
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0
	 * @return		the XmlNode that is the first child of the current XmlNode
	 *				If no children, null is returned
	 */
	public XmlNode getFirstChild() {
		return firstChild;
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0
	 * @return		the XmlNode that is the last child of the current XmlNode
	 *				If no children, null is returned
	 */
	public XmlNode getLastChild() {
		return lastChild;
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0
	 * @return		The XmlNode that is the next of the current XmlNode.
	 *				If no more siblings, null is returned
	 */
	public XmlNode getNextSibling() {
		return nextSibling;
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0
	 * @return		The XmlNode that is the parent of the current XmlNode.
	 *				If parent (root), null is returned
	 */
	public XmlNode getParent() {
		return parent;
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0
	 * @return		The name of the current XmlNode
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0
	 * @return		The text inside the current XmlNode.
	 *				If this is not a text node, this value will be null.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Updates the value of the node.
	 * @author		Fernando R Gomez Reyss
	 * @param value
	 * @date		2/8/2016
	 * @version		1.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}
			
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0
	 * @return		whether the current xml node has any children
	 */
	public boolean hasChildren() {
		return firstChild != null;
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0
	 * @return		whether the current xml node has another sibling following
	 */
	public boolean hasNext() {
		return nextSibling != null;
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0
	 * @return		whether the current xml node is the root
	 */
	public boolean isRoot() {
		return parent == null;
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @version		1.0.0 
	 * @return		Map&lt;String, String&gt;
	 *				A map with all the text nodes mapped by their path.
	 *				The map with the following format:<br/>
	 *				Key:<br/>	
	 *				The path of the text no separated by '::'. 
	 *				Value:<br/>
	 *				The inner text os the text node.
	 *				Example:<br/>
	 *				For the xml &lt;a&gt;&lt;b&gt;text&lt;/b&gt;&lt;/a&gt; 
	 *				the entry in the map will be as follows:
	 *				Key:	'a::b'
	 *				Value:	'text'
	 */
	public Map<String, String> getValuesMap() {
		Map<String, String> result = new HashMap<>();
		XmlNode node = this.name == null ? this.firstChild : this;
		XmlNode initial = node;
		Stack<String> names = new Stack<>();
		
		while (node != null) {							
			if (node.hasChildren()) {				
				names.push(node.name);
				node = node.firstChild;
			} else {				
				result.put(String.join("::", names) + 
						"::" + node.name, node.value);
				
				while (!node.equals(initial) && !node.hasNext()) {
					node = node.parent;
					names.pop();
				}
				
				if (node.equals(initial))
					break;
								
				node = node.nextSibling;
			}
		}
		
		return result;
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016
	 * @param nodeName
	 * @return		<p>
	 *				The first appearance of the node with the specified
	 *				name. Null is returned if no match is found.
	 *				</p>
	 *				<p>
	 *				This node can be used to search among sibling, children, or
	 *				even fall back to the parent. Note that only the first 
	 *				match is returned. For more result, uss the navigation
	 *				methods.
	 *				</p>
	 *				<p>
	 *				The search is performed horizontally; that is, all nodes in
	 *				level 1 (siblings) will be searched before moving into level
	 *				2, which entails the children of each node in level 1. If
	 *				still no match is found, the search moves on to level 3,
	 *				which is the grand children of level 1, children of level 2
	 *				and so on. 
	 *				</p>
	 *				<p>
	 *				&nbsp;&nbsp;&nbsp;&nbsp;A<br/>
	 *				&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;\<br/>
	 *				&nbsp;&nbsp;B&nbsp;&nbsp;&nbsp;&nbsp;C1<br/>
	 *				&nbsp;/<br/>
	 *				C2<br/>
	 *				</p>
	 *				<p>
	 *				In the above tree, if the search criteria matches both
	 *				C1 and C2, C1 is returned as it is in a higher level.
	 *				</p>
	 */
	public XmlNode search(String nodeName) {
		if (nodeName == null)
			return null;
		
		XmlNode node = this.firstChild;
		XmlNode temp;
		LinkedList<XmlNode> l = new LinkedList<>();
		Iterator<XmlNode> it; 
		
		// search level 1
		while (node != null) {
			// return if a match is found
			if (node.name.equals(nodeName))
				return node;
			// no match, add node so its children are considered
			l.add(node);
			node = node.nextSibling;
		}
		
		// no matches in first level, move on to next		
		it = l.iterator();
		while (it.hasNext()) {
			temp = it.next().search(nodeName);
			if (temp != null)
				return temp;
		}
		
		return null;
	}
	
	/**
	 * Returns an XML representation (text) of the node. 
	 * @author		Fernando R Gomez Reyss
	 * @date		2/8/2016  
	 * @return		A string representation of the XML. This would be the
	 *				actual XML.
	 */
	public String getXml() {
		// xml header
		StringBuilder sb = 
			new StringBuilder(); 
		//"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");				
		
		XmlNode node = this.name == null ? this.firstChild : this;
		
		XmlNode initial = node;
		int tabs = 0;
		
		while (node != null) {
			sb.append("\n");
			sb.append(StringUtils.repeat("\t", tabs));
			sb.append("<");
			sb.append(node.getName());
			sb.append(">");
			
			if (node.hasChildren()) {
				tabs++;
				node = node.firstChild;
			} else {				
				sb.append(node.getValue() == null ? "" :
						StringUtils.encodeXml(node.getValue()));
				sb.append("</");
				sb.append(node.getName());
				sb.append(">");
				
				while (!node.equals(initial) && !node.hasNext()) {
					node = node.parent;	
					sb.append("\n");
					sb.append(StringUtils.repeat("\t", --tabs));
					sb.append("</");
					sb.append(node.getName());
					sb.append(">");
				}
				
				if (node.equals(initial))
					break;
								
				node = node.nextSibling;
			}
		}		
		
		return sb.toString();
	}
	
	/**
	 * @author		Fernando R Gomez Reyss
	 * @date		Sep 11, 2015
	 * @version		1.0.0
	 * @param parent
	 * @param name
	 * @description	Private constructor, to be used within the class logic.
	 *				This is the actual constructor	
	 */
	private XmlNode(XmlNode parent, String name) {
		// attrs = new HashMap<>();
		this.name = name;
		this.parent = parent;
	}
	
	/**
	 * 
	 * @param xml 
	 * @throws javax.xml.stream.XMLStreamException 
	 */
	private void parseXml(String xml) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = 
			factory.createXMLStreamReader(new StringReader(xml));
		
		XmlNode node = this;
		XmlNode next;
		String nodeText;
		while (reader.hasNext()) {	
		   	reader.next();
			
			switch (reader.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:				
					next = new XmlNode(node, reader.getLocalName());
					
					if (node.firstChild == null)
						node.firstChild = next;						
					else {
						next.prevSibling = node.lastChild;
						node.lastChild.nextSibling = next;
					}
					
					node.lastChild = next;
					node = next;
					break;
				case XMLStreamConstants.CHARACTERS:
					nodeText = reader.getText().trim();
					if (!nodeText.isEmpty())
						node.value = reader.getText();
					break;
				case XMLStreamConstants.END_ELEMENT:
					node = node.parent;
					break;			
	   		}
	   	}
	}
}
