/*
 * Created on Dec 9, 2007 at 8:11:17 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.io.xmlrpc;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import de.enough.polish.util.TextUtil;
import de.enough.polish.util.base64.Base64;
import de.enough.polish.xml.XmlDomNode;

/**
 * <p>Serializes and deserializes some objects using the XML-RPC notation</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Dec 9, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class XmlRpcSerializer
{
	/**
	 * Serializes an object into an XML-RPC value
	 * @param buffer a StringBuffer to which the value should be added
	 * @param object the object that should be serialized
	 * @throws IOException when the object could not get serialized
	 */
	public static void serialize( StringBuffer buffer, Object object ) 
	throws IOException 
	{
		// check for scalar types:
		if (object instanceof Integer || object instanceof Short || object instanceof Byte) {
			buffer.append("<i4>").append( object.toString() ).append("</i4>");
		} else if (object instanceof Long) {
			buffer.append("<i8>").append( object.toString() ).append("</i8>");
		//#if polish.hasFloatingPoint
		} else if (object instanceof Double || object instanceof Float) {
			buffer.append("<double>").append( object.toString() ).append("</double>");
		//#endif
		} else if (object instanceof Boolean) {
			buffer.append("<boolean>").append( ((Boolean)object).booleanValue() ? '1' : '0' ).append("</boolean>");
		} else if (object instanceof String) {
			String value = (String) object;
			value = TextUtil.replace(value, "&", "&amp;");
			value = TextUtil.replace(value, "<", "&lt;");
			buffer.append( "<string>").append(value).append("</string>");
		} else if (object instanceof Date) {
			Date date = (Date) object;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			serialize( buffer, calendar );
		} else if (object instanceof Calendar) {
			Calendar calendar = (Calendar) object;
			buffer.append( "<dateTime.iso8601>");
			buffer.append( calendar.get(Calendar.YEAR));
			int month = calendar.get( Calendar.MONTH );
			if (month < 9) {
				buffer.append('0');
			}
			buffer.append( ++month );
			int day = calendar.get( Calendar.DAY_OF_MONTH );
			if (day < 10) {
				buffer.append( '0' );
			}
			buffer.append( day );
			buffer.append('T');
			int hour = calendar.get( Calendar.HOUR_OF_DAY );
			if (hour < 10) {
				buffer.append('0');
			}
			buffer.append( hour ).append(':');
			int minute = calendar.get( Calendar.MINUTE );
			if (minute < 10) {
				buffer.append('0');
			}
			buffer.append(minute).append(':');
			int second = calendar.get( Calendar.SECOND );
			if (second < 10) {
				buffer.append('0');
			}
			buffer.append(second);
			buffer.append("</dateTime.iso8601>");
		} else if (object instanceof byte[] ){
			String value = Base64.encodeBytes( (byte[])object );
			buffer.append("<base64>")
				.append(value)
				.append("</base64>");
		} else if (object instanceof Vector) {
			buffer.append("<array><data>");
			Vector vector = (Vector)object;
			Enumeration enumeration = vector.elements();
			while (enumeration.hasMoreElements()) {
				Object o = enumeration.nextElement();
				buffer.append("<value>");
				serialize(buffer, o);
				buffer.append("</value>");
			}
			buffer.append("</data></array>");
		} else if (object instanceof Object[]) {
			Object[] objects = (Object[]) object;
			buffer.append("<array><data>");
			for (int i = 0; i < objects.length; i++)
			{
				Object o = objects[i];
				buffer.append("<value>");
				serialize(buffer, o);
				buffer.append("</value>");				
			}
			buffer.append("</data></array>");		
		} else if (object instanceof Hashtable) {
			buffer.append("<struct>");
			Hashtable table = (Hashtable) object;
			Enumeration enumeration = table.keys();
			while (enumeration.hasMoreElements()) {
				Object key = enumeration.nextElement();
				Object value = table.get(key);
				
				buffer.append("<member>");
				buffer.append("<name>").append(key.toString()).append("</name>"); // TODO check for invalid chars & and < in name
				buffer.append("<value>");
				serialize(buffer, value);
				buffer.append("</value>");
				buffer.append("</member>");
			}			
			buffer.append("</struct>");
		} else {
			throw new IOException("Unable to xml-rpc serialize " + object );
		}
	}
	
	/**
	 * Deserializes an XML-RPC value
	 * @param node the XML DOM node that contains the value definition, e.g. <i4>12</i4>
	 * @return the deserialized object
	 * @throws IOException when the object could not get deserialized
	 */
	public static Object deserialize(XmlDomNode node) throws IOException {
		//support default param type string
		if(node.getChildCount() == 0){
			String value = node.getText();
			value = TextUtil.replace(value, "&lt;", "<" );
			value = TextUtil.replace(value, "&amp;", "&" );
			return value;
		}
		XmlDomNode nextNode = node.getChild( 0 );
		String nextElement = nextNode.getName();
		if (nextElement.equals("int") || nextElement.equals("i4")) {
			String value = nextNode.getText();
			return new Integer( Integer.parseInt(value));
		} else if (nextElement.equals("i8")) {
			String value = nextNode.getText();
			return new Long( Long.parseLong(value));
		//#if polish.hasFloatingPoint
		} else if (nextElement.equals("double")) {
			String value = nextNode.getText();
			return new Double( Double.parseDouble(value));
		//#endif
		} else if (nextElement.equals("boolean")) {
			String value = nextNode.getText();
			Boolean result;
			//#if polish.cldc1.1
				result = value.equals("1") ? Boolean.TRUE : Boolean.FALSE;
			//#else
				result = value.equals("1") ? new Boolean(true) : new Boolean(false);
			//#endif
			return result;
		} else if (nextElement.equals("string")) {
			String value = nextNode.getText();
			value = TextUtil.replace(value, "&lt;", "<" );
			value = TextUtil.replace(value, "&amp;", "&" );
			return value;
		} else if (nextElement.equals("dateTime.iso8601")) {
			String value = nextNode.getText();
			// format is 'YYYYMMDDTHH:MM:SS'
			if (value.length() != 17) {
				throw new IOException("Unable to deserialize dateTime " + value + " - not 17 chars long" ); 
			}
			int year = Integer.parseInt( value.substring(0,4) );
			int month = Integer.parseInt( value.substring(4,6) ) - 1;
			int day = Integer.parseInt( value.substring(6,8) );
			int hour = Integer.parseInt( value.substring(9,11) );
			int minute = Integer.parseInt( value.substring(12,14) );
			int seconds = Integer.parseInt( value.substring(15,17) );
			Calendar calendar = Calendar.getInstance();
			calendar.set( Calendar.YEAR, year );
			calendar.set( Calendar.MONTH, month );
			calendar.set( Calendar.DAY_OF_MONTH, day );
			calendar.set( Calendar.HOUR_OF_DAY, hour );
			calendar.set( Calendar.MINUTE, minute );
			calendar.set( Calendar.SECOND, seconds );
			return  calendar;
		} else if (nextElement.equals("base64")) {
			String dataStr = nextNode.getText();
			return Base64.decode(dataStr);
		} else if (nextElement.equals("array")) {
			XmlDomNode dataNode = nextNode.getChild("data");
			Object[] results = new Object[dataNode.getChildCount()];
			for (int i = 0; i < results.length; i++)
			{
				results[i] = deserialize(dataNode.getChild(i));
			}
			return  results;
		} else if (nextElement.equals("struct")) {
			Hashtable table = new Hashtable( nextNode.getChildCount() );
			for (int i=0; i< nextNode.getChildCount(); i++) {
				XmlDomNode memberNode = nextNode.getChild(i);
				String name = memberNode.getChild("name").getText();
				Object value = deserialize( memberNode.getChild("value") );
				table.put(name, value);
			}
			return table;
		} else {
			throw new IOException("Unable to deserialize " + nextElement );
		}
	}

	
}
