/*
 * Created on 28-Jun-2006 at 21:38:16.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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

import junit.framework.TestCase;

/**
 * <p>Tests the Native2Ascii implementation</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        28-Jun-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Native2AsciiTest extends TestCase {

	public Native2AsciiTest(String name) {
		super(name);
	}
	
	public void testAsciiToNative() {
		assertEquals( "\t", Native2Ascii.asciiToNative( "\\t" ));
		assertEquals( "\n", Native2Ascii.asciiToNative( "\\n" ));
		assertEquals( "\f", Native2Ascii.asciiToNative( "\\f" ));
		assertEquals( "\r", Native2Ascii.asciiToNative( "\\r" ));
		assertEquals( "\u00fc", Native2Ascii.asciiToNative( "\\u00fc" ) );
		assertEquals( "\u00e4", Native2Ascii.asciiToNative( "\\u00e4" ) );
		assertEquals( "\u00f6", Native2Ascii.asciiToNative( "\\u00f6" ) );
		assertEquals( "\u00df", Native2Ascii.asciiToNative( "\\u00df" ) );
	}

	public void testNativeToAscii() {
		assertEquals( "\\u00fc", Native2Ascii.nativeToAscii( "\u00fc" ) );
		assertEquals( "\\u00e4", Native2Ascii.nativeToAscii( "\u00e4" ) );
		assertEquals( "\\u00f6", Native2Ascii.nativeToAscii( "\u00f6" ) );
		assertEquals( "\\u00df", Native2Ascii.nativeToAscii( "\u00df" ) );
		assertEquals( "\\u00df\\u00f6\\u00fc\\u00df", Native2Ascii.nativeToAscii( "\u00df\u00f6\u00fc\u00df" ) );
	}
}
