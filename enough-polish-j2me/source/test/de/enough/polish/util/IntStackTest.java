/*
 * Created on Aug 30, 2007 at 9:28:57 PM.
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

/**
 * <p>tests IntStack</p>
 *
 * <p>Copyright Enough Software 2007 - 2008</p>
 * <pre>
 * history
 *        Aug 30, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class IntStackTest extends TestCase {
	
	private static final int ROUNDS = 100000;
	
	public void testPopAndPeek() {
		IntStack stack = new IntStack();
		for (int i=0; i<ROUNDS; i++) {
			stack.push( i );
			assertEquals( i, stack.peek() );
		}
		
		for (int i=ROUNDS-1; i>=0; i--) {
			assertEquals( i, stack.pop() );
		}
	}
	
	public void testSearch() {
		IntStack stack = new IntStack();
		for (int i=0; i<ROUNDS; i++) {
			stack.push( i );
			assertEquals( i, stack.peek() );
		}
		for (int i=0; i<ROUNDS; i++) {
			assertEquals( ROUNDS - i, stack.search(i));
		}
	}

}
