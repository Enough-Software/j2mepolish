/*
 * Created on 08-Apr-2006 at 19:20:28.
 * 
 * Copyright (c) 2009 Richard Nkrumah / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.xml;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;

import de.enough.polish.util.ArrayList;

/**
 * <p>Holds information about an XML node.</p>
 * <p>This is typically used for parsing XML documents using the XmlDomParser.</p>
 * <p>Copyright Enough Software 2006 - 2009</p>
 * 
 * <pre>
 * history
 *        Apr 8, 2006 - rickyn creation
 * </pre>
 * 
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 * @see XmlDomParser
 */
public class XmlDomNode
{
    private XmlDomNode parent;
    private final ArrayList childList;
    private Hashtable attributes;
    private String name;
    private int type;
    private String text;
    
    /**
     * Creates a new XmlDomNode
     * @param parent the parent node
     * @param name the name of this element
     * @param type the type of the element, see SimplePullParser
     * @see SimplePullParser
     */
    public XmlDomNode(XmlDomNode parent, String name, int type) {
    	this(parent, name, null, type);
    }

    /**
     * Creates a new XmlDomNode
     * @param parent the parent node
     * @param name the name of this element
     * @param attributes the attributes of this node
     * @param type the type of the element, see SimplePullParser
     * @see SimplePullParser
     */
    public XmlDomNode(XmlDomNode parent, String name, Hashtable attributes, int type) {
        this.parent = parent;

        if(this.parent != null) {
            this.parent.addChild(this);
        }

        this.name = name;
        this.attributes = attributes;
        this.type = type;
        this.childList = new ArrayList(); 
    }
    
    /**
     * Retrieves the named child element
     * @param childName the name of the child node
     * @return the first child with the given name or null if no child node is found.
     */
    public XmlDomNode getChild(String childName) {
        XmlDomNode child;

        for(int i = 0; i < this.childList.size();i++) {
            child = (XmlDomNode) this.childList.get(i);

            if(childName.equals(child.getName())) {
                return child;
            }
        }

        childName = childName.toLowerCase();

        for(int i = 0; i < this.childList.size();i++) {
            child = (XmlDomNode)this.childList.get(i);

            if(childName.equals(child.getName())) {
                return child;
            }
        }

        return null;
    }
   
    /**
     * Retrieves the numbered child
     * @param index the index of the child
     * @return the associated child
     * @throws ArrayIndexOutOfBoundsException when the index is illegal
     * @see #getChildCount() for getting the number of children
     */
    public XmlDomNode getChild(int index) {
        return (XmlDomNode) this.childList.get(index);
    }
    
    /**
     * Adds a child node
     * 
     * @param childNode the childNode
     */
    public void addChild(XmlDomNode childNode) {
        this.childList.add(childNode);
    }
    
    /**
     * Retrieves the number of children
     * @return number of Childs.
     */
    public int getChildCount() {
        return this.childList.size();
    }
    
    /**
     * Retrieves the text embedded in this element
     * @return the text
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets the text of the node
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Retrieves the name of this node
     * @return the name of this node
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of this node
     * @param name the name of this node
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the parent of this node
     * @return the parent or null if there is no parent
     */
    public XmlDomNode getParent() {
        return this.parent;
    }
    
    /**
     * Sets the parent of this node
     * @param parent the parent node
     */
    public void setParent(XmlDomNode parent) {
        this.parent = parent;
    }

    /**
     * Retrieves the type of this node
     * @return the type
     * @see SimplePullParser
     */
    public int getType() {
        return this.type;
    }
    
    /**
     * Sets the type of this node
     * @param type the type
     * @see SimplePullParser
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Retrieves all attributes
     * @return all attributes, can be null
     */
	public Hashtable getAttributes() {
		return this.attributes;
	}
	
    /**
     * Sets all attributes
     * @param attributes all attributes, can be null
     */
	public void setAttributes(Hashtable attributes) {
		this.attributes = attributes;
	}

	/**
	 * Retrieves the specified attribute
	 * @param attributeName the attribute name
	 * @return the attribute value, null if it is not known
	 */
	public String getAttribute(String attributeName) {
		if (this.attributes == null) {
			return null;
		}
		return (String) this.attributes.get(attributeName);
	}

	/**
	 * Retrieves the specified child's text 
	 * @param childName the child's name
	 * @return either the child's text or null when the child is unknown
	 */
	public String getChildText(String childName)
	{
		XmlDomNode child = getChild(childName);
		return child != null ? child.getText() : null;
	}

	/**
	 * Retrieves all children of this node.
	 * @return all children in an array - may be empty but not null;
	 */
	public XmlDomNode[] getChildren()
	{
		return (XmlDomNode[]) this.childList.toArray( new XmlDomNode[ this.childList.size() ] );
	}
	
	/**
	 * Retrieves the children of this node with the specified name.
	 * 
	 * @param childName the name of the children
	 * @return all children with the specified name in an array - may be empty but not null;
	 */
	public XmlDomNode[] getChildren(String childName)
	{
		return getChildren( childName, false );
	}
	
	/**
	 * Retrieves the children of this node and optionally this children nodes with the specified name.
	 * 
	 * @param childName the name of the children
	 * @param recursive true when all children of this node should also be investigated
	 * @return all children with the specified name in an array - may be empty but not null;
	 */
	public XmlDomNode[] getChildren(String childName, boolean recursive)
	{
		ArrayList children = new ArrayList();
		getChildren( childName, recursive, children );
		return (XmlDomNode[]) children.toArray( new XmlDomNode[ children.size() ] );
	}
	
	/**
	 * Retrieves the children of this node and optionally this children nodes with the specified name.
	 * 
	 * @param childName the name of the children
	 * @param recursive true when all children of this node should also be investigated
	 * @param resultList the result list that will contain XmlDomNode elements
	 */
	public void getChildren(String childName, boolean recursive, ArrayList resultList)
	{
		Object[] objects = this.childList.getInternalArray();
		for (int i = 0; i < objects.length; i++) {
			XmlDomNode child = (XmlDomNode) objects[i];
			if (child == null) {
				break;
			}
			if (childName.equals(child.getName())) {
				resultList.add(child);
			}
			if (recursive) {
				child.getChildren(childName, recursive, resultList);
			}
		}
	}

	/**
	 * Retrieves an XML representation of this XML node and its nested children.
	 * @return the node as an XML string.
	 */
	public String toXmlString() {
		return toXmlString(true);
	}
	
	/**
	 * Retrieves an XML representation of this XML node and its nested children.
	 * @param useWhitespace specifies if whitespace should be used between tags or if the representation should be as compact as possible
	 * @return the node as XML string
	 */
	public String toXmlString(boolean useWhitespace) {
		StringBuffer xml = new StringBuffer();
		appendXmlString("", xml,useWhitespace);
		return xml.toString();
	}
	
	/**
	 * Appends this node's information in XML format to the given StringBuffer
	 * @param indent the current indentation
	 * @param xml the buffer
	 */
	protected void appendXmlString(String indent, StringBuffer xml) {
		appendXmlString(indent, xml, true);
	}

	/**
	 * Appends this node's information in XML format to the given StringBuffer
	 * @param indent the current indentation
	 * @param xml the buffer
	 * @param useWhitespace specifies if whitespace should be used between tags or if the representation should be as compact as possible
	 */
	protected void appendXmlString(String indent, StringBuffer xml, boolean useWhitespace) {
		xml.append( indent ).append( '<' ).append( this.name );
		if (this.attributes != null) {
			Enumeration  keys = this.attributes.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = (String) this.attributes.get(key);
				xml.append(' ').append( key ).append( "=\"").append( value ).append("\"");
			}
		}
		xml.append('>');
		String childIndent = "";
		if ( useWhitespace ) {
			xml.append('\n');
			childIndent = ' ' + indent;
		}
		if (this.text != null) {
			xml.append(childIndent).append(this.text);
			if ( useWhitespace ) {
				xml.append('\n');
			}
		}
		if (this.childList != null) {
			for (int i=0; i<this.childList.size(); i++) {
				XmlDomNode node = (XmlDomNode) this.childList.get(i);
				node.appendXmlString(childIndent, xml,useWhitespace);
			}
		}
		xml.append( indent ).append( "</" ).append( this.name ).append(">");
		if ( useWhitespace ) {
			xml.append('\n');
		}
	}

	/**
	 * Helper method for logging.
	 * Just prints the complete node to the standard out.
	 */
	public void print() {
		print( System.out );
	}
	
	/**
	 * Helper method for logging.
	 * Just prints the complete node to the specified output.
	 * @param out the output stream to which the complete node should be printed
	 */
	public void print( PrintStream out ) {
		print( this, "", out );
	}

	private void print(XmlDomNode node, String start, PrintStream out) {
		out.print( start );
		out.print( '<' );
		out.print( node.name );
		if (node.attributes != null) {
			Enumeration attrEnum = node.attributes.keys();
			while (attrEnum.hasMoreElements()) {
				String attrName = (String) attrEnum.nextElement();
				String attrValue = (String) node.attributes.get(attrName);
				out.print(' ');
				out.print(attrName);
				out.print('=');
				out.print('"');
				out.print(attrValue);
				out.print('"');
			}
		}
		if (node.text == null && node.childList == null) {
			out.println("/>");
		} else {
			out.println('>');
			if (node.text != null) {
				out.print(start);
				out.println(node.text);
			}
			if (node.childList != null) {
				Object[] objects = node.childList.getInternalArray();
				String childStart = start+ "  ";
				for (int i = 0; i < objects.length; i++) {
					XmlDomNode child = (XmlDomNode) objects[i];
					if (child == null) {
						break;
					}
					print( child, childStart, out );
				}
			}
			out.print(start);
			out.print("</");
			out.print( node.name );
			out.println('>');
		}
	}
}
