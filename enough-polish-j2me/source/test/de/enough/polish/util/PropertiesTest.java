/*
 * Created on Jul 10, 2007 at 12:53:20 PM.
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
package de.enough.polish.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007 - 2008</p>
 * <pre>
 * history
 *        Jul 10, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PropertiesTest extends TestCase {

	public PropertiesTest(String name) {
		super(name);
	}
	
	public void testLoad() {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter( byteOut );
		writer.println("key=value");
		for (int i = 0; i < 1000; i++) {
			writer.println("key" + i + "=value" + i );
		}
		writer.flush();
		writer.close();
		
		ByteArrayInputStream in = new ByteArrayInputStream( byteOut.toByteArray() );
		
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			fail( e.toString() );
		}
		assertEquals( "value", properties.getProperty( "key" ) );
		
		for (int i = 0; i < 1000; i++) {
			String key = "key" + i;
			assertEquals( "value" + i, properties.getProperty(key));
		}
	}
}
