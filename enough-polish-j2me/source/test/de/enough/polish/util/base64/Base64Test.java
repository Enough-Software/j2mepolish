/*
 * Created on Dec 13, 2007 at 5:49:03 PM.
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
package de.enough.polish.util.base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007 - 2008</p>
 * <pre>
 * history
 *        Dec 13, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Base64Test extends TestCase
{
	
	public Base64Test(String name) {
		super(name);
	}
	
	public void testEncoding() {
		String original = "Hello World <!> How do you do?";
		String translated;
		
		
		translated = Base64.encode(original);
		assertEquals( "SGVsbG8gV29ybGQgPCE+IEhvdyBkbyB5b3UgZG8/", translated );
		assertEquals( original, new String( Base64.decode(translated) ) );
	}
	
	public void testStreams() throws IOException {
		String original = "Hello World <!> How do you do?";
		String translated;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		Base64OutputStream out = new Base64OutputStream(byteOut);
		out.write( original.getBytes() );
		out.flush();
		byte[] data = byteOut.toByteArray();
		assertTrue( data.length > 0 );
		assertEquals( Base64.encode(original),  new String( data) );
		ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
		Base64InputStream in = new Base64InputStream(byteIn);
		byteOut = new ByteArrayOutputStream();
		int read;
		while (( read = in.read(data)) != -1) {
			byteOut.write(data, 0, read);
		}
		translated = new String( byteOut.toByteArray() );
		assertEquals( original, translated);
		
		original = "tututu LDK==XX%^*&#@*$^$%";
		translated = Base64.encode(original);
		byteIn = new ByteArrayInputStream(translated.getBytes());
		in = new Base64InputStream(byteIn);
		byteOut = new ByteArrayOutputStream();
		while (( read = in.read(data)) != -1) {
			byteOut.write(data, 0, read);
		}
		translated = new String( byteOut.toByteArray() );
		assertEquals( original, translated);
		
	}

}
