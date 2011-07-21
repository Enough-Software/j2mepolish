/*
 * Created on Feb 10, 2007 at 10:13:07 AM.
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
package de.enough.webprocessor.test;

import de.enough.webprocessor.util.StringList;
import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Feb 10, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StringListTest extends TestCase {
	
	
	public void testInsert() {
		String[] lines = new String[]{
			"one", "two", "three", "four"	
		};
		StringList list = new StringList( lines );
		list.insert( new String[]{ "zero" } );
		assertEquals( 5, list.length() );
		assertEquals( true, list.next() );
		assertEquals( "zero", list.getCurrent() );
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			assertEquals( true, list.next() );
			assertEquals( line, list.getCurrent() );
		}
		list.next();
	}

}
