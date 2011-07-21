/*
 * Created on Mar 20, 2009 at 11:21:09 AM.
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
package de.enough.polish.ui;

import junit.framework.TestCase;

/**
 * <p>Tests the PercentOrAbsoluteInteger implementation.</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PercentOrAbsoluteIntegerTest extends TestCase
{
	
	public PercentOrAbsoluteIntegerTest()
	{
		super();
	}

	public PercentOrAbsoluteIntegerTest(String name)
	{
		super(name);
	}
	
	public void testGetValue() {
		Dimension n;
		
		n = new Dimension(35, false);
		assertEquals( 35, n.getValue(100) );
		assertEquals( 35, n.getValue(1000) );
		
		n = new Dimension(35, true);
		assertEquals( 35, n.getValue(100) );
		assertEquals( 350, n.getValue(1000) );

		n = new Dimension(35, 1);
		assertEquals( 35, n.getValue(100) );
		assertEquals( 350, n.getValue(1000) );
		
		n = new Dimension(35, 10);
		assertEquals( 3, n.getValue(100) );
		assertEquals( 35, n.getValue(1000) );

	}

}
