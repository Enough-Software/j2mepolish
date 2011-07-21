/*
 * Created on 07-Nov-2005 at 01:23:51.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class LogEntryTest extends TestCase {


	public LogEntryTest(String name) {
		super(name);
	}
	
	public void testConversion() throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( byteOut );
		out.writeInt( 1200 );
		out.writeUTF( "debug" );
		
		ByteArrayInputStream byteIn = new ByteArrayInputStream( byteOut.toByteArray() );
		DataInputStream in = new DataInputStream( byteIn );
		
		assertEquals( 1200, in.readInt() );
		assertEquals( "debug", in.readUTF() );
		
	}
	

	
	public void testToByteArray() 
	throws IOException 
	{
		long time = System.currentTimeMillis();
		LogEntry entry = new LogEntry( "SomeClass", 12, time, "debug", "some message here", null );
		byte[] data = entry.toByteArray();
		byte[] dataCopy = new byte[ data.length ];
		System.arraycopy(data, 0, dataCopy, 0, data.length );
		assertTrue( data.length > 0 );

		ByteArrayInputStream byteIn = new ByteArrayInputStream( data );
		DataInputStream in = new DataInputStream( byteIn );
		
		assertEquals( LogEntry.VERSION, in.readInt() );
		assertEquals( "debug", in.readUTF() );
		assertEquals( time, in.readLong() );
		assertEquals( "SomeClass", in.readUTF() );
		assertEquals( 12, in.readInt() );
		assertEquals( "some message here", in.readUTF() );
		assertEquals( "", in.readUTF() );
		/*
		 * 		out.writeUTF( this.className );
		out.writeInt( this.lineNumber );
		out.writeUTF( this.message );
		out.writeUTF( this.exception );
		out.writeUTF( this.thread );

		 */
		
		byteIn = new ByteArrayInputStream( dataCopy );
		in = new DataInputStream( byteIn );
		
		LogEntry copy = LogEntry.newLogEntry( in );
		assertEquals( entry.className, copy.className );
		assertEquals( entry.level, copy.level );
		assertEquals( entry.time, copy.time );
		assertEquals( entry.lineNumber, copy.lineNumber );

	}

}
