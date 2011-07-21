/*
 * Created on Dec 10, 2007 at 2:22:46 AM.
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
package de.enough.polish.io;

import java.io.IOException;
import java.util.Calendar;

import de.enough.polish.io.xmlrpc.XmlRpcSerializer;
import de.enough.polish.xml.XmlDomParser;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007 - 2008</p>
 * <pre>
 * history
 *        Dec 10, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class XmlRpcSerializerTest extends TestCase
{

	public XmlRpcSerializerTest(String arg0)
	{
		super(arg0);
	}
	
	public void testSerialize() throws IOException {
		StringBuffer buffer = new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.YEAR, 2007 );
		calendar.set( Calendar.MONTH, 11 );
		calendar.set( Calendar.DAY_OF_MONTH, 10 );
		calendar.set( Calendar.HOUR_OF_DAY, 13 );
		calendar.set( Calendar.MINUTE, 24 );
		calendar.set( Calendar.SECOND, 45 );
		XmlRpcSerializer.serialize(buffer, calendar);
		assertEquals("<dateTime.iso8601>20071210T13:24:45</dateTime.iso8601>", buffer.toString() );
		Calendar deserialized = (Calendar) XmlRpcSerializer.deserialize( XmlDomParser.parseTree( buffer.toString() ));
		assertEquals( deserialized.get(Calendar.YEAR), calendar.get(Calendar.YEAR) );
		assertEquals( deserialized.get(Calendar.MONTH), calendar.get(Calendar.MONTH) );
		assertEquals( deserialized.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_MONTH) );
		assertEquals( deserialized.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.HOUR_OF_DAY) );
		assertEquals( deserialized.get(Calendar.MINUTE), calendar.get(Calendar.MINUTE) );
		assertEquals( deserialized.get(Calendar.SECOND), calendar.get(Calendar.SECOND) );
		
		buffer.delete(0, buffer.length());
		String[] values = new String[]{"alpha", "beta", "omega" };
		XmlRpcSerializer.serialize(buffer, values);
		assertEquals("<array><data><value><string>alpha</string></value><value><string>beta</string></value><value><string>omega</string></value></data></array>", buffer.toString() );
		Object[] deserializedArray = (Object[]) XmlRpcSerializer.deserialize( XmlDomParser.parseTree(buffer.toString()));
		for (int i = 0; i < deserializedArray.length; i++)
		{
			Object des = deserializedArray[i];
			assertEquals( des,values[i] );
		}
		
		buffer.delete(0, buffer.length());
		String value = "1 < 12 & 4 > 3";
		XmlRpcSerializer.serialize(buffer, value);
		assertEquals("<string>1 &lt; 12 &amp; 4 > 3</string>", buffer.toString() );
		String deserializedStr = (String) XmlRpcSerializer.deserialize( XmlDomParser.parseTree(buffer.toString()));
		assertEquals( value, deserializedStr );
	}

}
