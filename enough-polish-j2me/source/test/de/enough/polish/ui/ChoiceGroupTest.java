/*
 * Created on Dec 11, 2008 at 5:21:28 PM.
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
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ChoiceGroupTest extends TestCase
{

	
	public ChoiceGroupTest(String name)
	{
		super(name);
	}

	public void testGetSelectedFlags() {
		ChoiceGroup group;
		boolean[] flags;
		
		group = new ChoiceGroup( null, ChoiceGroup.MULTIPLE);
		group.append("test", null);
		group.append("test", null);
		group.append("test", null);
		group.append("test", null);
		group.append("test", null);
		flags = new boolean[ group.size() ];
		int result = group.getSelectedFlags(flags);
		assertEquals( 0, result );
		int expected = 1;
		for (int i=flags.length; --i>=0; ) {
			group.setSelectedIndex(i, true );
			result = group.getSelectedFlags(flags);
			assertEquals( expected, result );
			expected++;
			for (int j=0; j<flags.length; j++) {
				assertEquals( j >= i, flags[j] );
			}
		}
		
		
		group = new ChoiceGroup( null, ChoiceGroup.EXCLUSIVE);
		group.append("test", null);
		group.append("test", null);
		group.append("test", null);
		group.append("test", null);
		group.append("test", null);
		assertEquals( 0, group.getSelectedIndex() );
		flags = new boolean[ group.size() ];
		result = group.getSelectedFlags(flags);
		assertEquals( 1, result );
		for (int i=flags.length; --i>=0; ) {
			group.setSelectedIndex(i, true );
			result = group.getSelectedFlags(flags);
			assertEquals( i, group.getSelectedIndex() );
			assertEquals( 1, result );
			for (int j=0; j<flags.length; j++) {
				assertEquals( j == i, flags[j] );
			}
		}
		
		
	}
}
