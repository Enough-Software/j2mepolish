/*
 * Created on Feb 28, 2007 at 4:03:51 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.propertyfunctions;

import junit.framework.TestCase;

/**
 * <p>Tests the version property function</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VersionFunctionTest extends TestCase {


	public VersionFunctionTest(String name) {
		super(name);
	}
	
	
	public void testVersion() {
		PropertyFunction calculate = new VersionFunction();
		String output = calculate.process("2.1.0", null, null);
		assertEquals("002001000", output );
		
		output = calculate.process("2.1", null, null);
		assertEquals("002001000", output );

		output = calculate.process("2", null, null);
		assertEquals("002000000", output );

		output = calculate.process("MIDP/2.3", null, null);
		assertEquals("002003000", output );

		output = calculate.process("MIDP/2.3.9", null, null);
		assertEquals("002003009", output );


		output = calculate.process("MIDP/123.456.789", null, null);
		assertEquals("123456789", output );

		output = calculate.process("MIDP/12.34.56", null, null);
		assertEquals("012034056", output );

		output = calculate.process("MIDP/12.34.56, BlackBerry/4.1", new String[]{"BlackBerry"}, null);
		assertEquals("004001000", output );

		output = calculate.process("MIDP/12.34.56.999, BlackBerry/4.1", new String[]{"BlackBerry"}, null);
		assertEquals("004001000", output );

		output = calculate.process("MIDP/12.34.56.9", new String[]{"BlackBerry"}, null);
		assertEquals("-1", output);

		output = calculate.process("MIDP/12.34.56, BlackBerry/4.1", new String[]{"ABCDEF"}, null);
		assertEquals("-1", output);

		try {
			calculate.process("MIDP/12.34.56.9", null, null);
			fail("invalid version got parsed");
		} catch (IllegalArgumentException e) {
			// expected
		}

		try {
			calculate.process("MIDP/12.34.56.9", new String[]{"MIDP"}, null);
			fail("invalid version got parsed");
		} catch (IllegalArgumentException e) {
			// expected
		}


	}

}
