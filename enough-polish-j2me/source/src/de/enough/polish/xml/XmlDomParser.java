/*
 * Created on 08-Apr-2006 at 19:20:28.
 * 
 * Copyright (c) 2009 Michael Koch / Enough Software
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * <p>Parses XML documents in a single step.</p>
 * <p>This is more comfortable than using the XmlPullParser but uses somewhat more memory since the complete document 
 *    is held memory.
 * </p>
 * <p>Following example code demonstrates the usage:</p>
 * <pre>
	String document = &quot;&lt;rootelement&gt;&lt;childnode x=\&quot;10\&quot;&gt;textvalue10&lt;/childnode&gt;&lt;childnode x=\&quot;20\&quot;&gt;textvalue20&lt;/childnode&gt;&lt;/rootelement&gt;&quot;;
	XmlDomNode root = XmlDomParser.parseTree(document);
	System.out.println(&quot;root &quot; + root.getName() + &quot;, has &quot; + root.getChildCount() + &quot; children&quot;);
	// indirect access:
	for (int i=0; i &lt; root.getChildCount(); i++ ) {
		XmlDomNode child = root.getChild(i);
		System.out.println(&quot;child: &quot; + child.getName() );
		System.out.println(&quot;child: &quot; + child.getName() + &quot;, x=&quot; + child.getAttribute(&quot;x&quot;) + &quot;, text=&quot; + child.getText() );
	}
	// direct access:
	XmlDomNode child = root.getChild(&quot;childnode&quot;);		
	System.out.println(&quot;first child: &quot; + child.getName() + &quot;, x=&quot; + child.getAttribute(&quot;x&quot;) + &quot;, text=&quot; + child.getText() );

 * </pre>
 * 
 * 
 * <p>Copyright Enough Software 2006 - 2009</p>
 * 
 * <pre>
 * history
 *        Apr 8, 2006 - mkoch creation
 * </pre>
 * 
 * @author Michael Koch, michael.koch@enough.de
 */
public class XmlDomParser
{

	/**
	 * Parses an XML document and returns it's root element as an XmlDomNode.
	 * When the XML document has no root element, it will return an artificial root element that contains
	 * all root elements of the document.
	 * 
	 * @param document the document.
	 * @return the root element of the document as XmlDomNode
	 * @throws RuntimeException when the parsing fails
	 */
	public static XmlDomNode parseTree(String document)
	{
		try {
			return parseTree(document, null);
		} catch (UnsupportedEncodingException e) {
			//#debug error
			System.out.println("Unable to parse stream in default encoding: " + e);
			throw new RuntimeException( e.toString() );
		}
	}
	
	/**
	 * Parses an XML document and returns it's root element as an XmlDomNode.
	 * When the XML document has no root element, it will return an artificial root element that contains
	 * all root elements of the document.
	 * 
	 * @param document the document.
	 * @param encoding the encoding, e.g. "UTF-8"
	 * @return the root element of the document as XmlDomNode
	 * @throws UnsupportedEncodingException 
	 * @throws RuntimeException when the parsing fails
	 */
	public static XmlDomNode parseTree(String document, String encoding) throws UnsupportedEncodingException
	{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(document.getBytes());
		return parseTree(byteArrayInputStream, null);
	}

	/**
	 * Parses an XML document and returns it's root element as an XmlDomNode.
	 * When the XML document has no root element, it will return an artificial root element that contains
	 * all root elements of the document.
	 * 
	 * @param in input stream of the document.
	 * @return the root element of the document as XmlDomNode
	 * @throws RuntimeException when the parsing fails
	 */
	public static XmlDomNode parseTree(InputStream in)
    {
		try {
			return parseTree( in, null );
		} catch (UnsupportedEncodingException e) {
			//#debug error
			System.out.println("Unable to parse stream in default encoding: " + e);
			throw new RuntimeException( e.toString() );
		}
    }

	/**
	 * Parses an XML document and returns it's root element as an XmlDomNode.
	 * When the XML document has no root element, it will return an artificial root element that contains
	 * all root elements of the document.
	 * 
	 * @param in input stream of the document.
	 * @param encoding the encoding, e.g. "UTF-8"
	 * @return the root element of the document as XmlDomNode
	 * @throws UnsupportedEncodingException  
	 * @throws RuntimeException when the parsing fails
	 */
	public static XmlDomNode parseTree(InputStream in, String encoding) throws UnsupportedEncodingException
    {
        InputStreamReader inputStreamReader;
        if (encoding != null) {
        	inputStreamReader = new InputStreamReader(in, encoding);
        } else {
        	inputStreamReader = new InputStreamReader(in);
        }
        return parseTree( inputStreamReader );
    }

	/**
	 * Parses an XML document and returns it's root element as an XmlDomNode.
	 * When the XML document has no root element, it will return an artificial root element that contains
	 * all root elements of the document.
	 * 
	 * @param reader the reader for the XML document that should be parsed 
	 * @return the root element of the document as XmlDomNode
	 * @throws RuntimeException when the parsing fails
	 */
	public static XmlDomNode parseTree(Reader reader) 
	{
		XmlPullParser parser;
        try {
            parser = new XmlPullParser(reader);
        } catch (IOException exception) {
            throw new RuntimeException("Could not create xml parser."+exception);
        }

        XmlDomNode root = new XmlDomNode(null, null, -1);
        XmlDomNode currentNode = root;
        String newName;
        int newType;
        
        try {
            while ((parser.next()) != SimplePullParser.END_DOCUMENT) {
                newName = parser.getName();
                newType = parser.getType();
                
                if(newType == SimplePullParser.START_TAG) {
                	Hashtable attributes = null;
                	int attributeCount = parser.getAttributeCount(); 

                	if (attributeCount > 0) {
                		attributes = new Hashtable();

                		for (int i = 0; i < attributeCount; i++) {
                			attributes.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                		}
                	}

                    XmlDomNode newNode = new XmlDomNode(currentNode, newName, attributes, newType);
                    currentNode = newNode;
                }
                
                else if(newType == SimplePullParser.END_TAG) {
                    currentNode = currentNode.getParent();
                }
                
                else if(newType == SimplePullParser.TEXT) {
                    String text = parser.getText();
                    currentNode.setText(text);
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException("parse error:"+exception);
        }
        if (root.getChildCount() == 1) {
        	return root.getChild(0);
        } else {
        	return root;
        }
    }
}
