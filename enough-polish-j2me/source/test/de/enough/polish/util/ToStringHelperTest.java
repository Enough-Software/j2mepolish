/*
 * Created on Jul 18, 2010 at 8:59:58 AM.
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

import junit.framework.TestCase;

public class ToStringHelperTest extends TestCase {

	public void testToString() {
		ToStringHelper helper = new ToStringHelper( "name" );
		helper.set("param1", "value1");
		helper.set("param2", 42);
		ArrayList list = new ArrayList();
		list.add("a");
		list.add("b");
		helper.set( "list", list);
		
		assertEquals( "name [param1:\"value1\" / param2:42 / list:a , b]", helper.toString());
		
		helper.clear();
		helper.set("param3", 1024);
		assertEquals( "name [param3:1024]", helper.toString() );
		helper.setName("secondrun");
		assertEquals( "secondrun [param3:1024]", helper.toString() );
		assertEquals( "secondrun [param3:1024]", helper.toString() );
		assertEquals( "secondrun [param3:1024]", helper.toString() );
	}
}
