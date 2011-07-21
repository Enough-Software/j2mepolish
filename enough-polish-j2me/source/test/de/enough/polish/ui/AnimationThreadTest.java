/*
 * Created on Mar 17, 2010 at 7:33:42 PM.
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
package de.enough.polish.ui;

import junit.framework.TestCase;

public class AnimationThreadTest extends TestCase {

	public AnimationThreadTest() {
		super();
	}

	public AnimationThreadTest(String name) {
		super(name);
	}

	public void testAddRemoveIdleEvent() {
		assertEquals( null, AnimationThread.getIdleEventNames() );
		assertEquals( null, AnimationThread.getIdleEventTimeouts() );
		AnimationThread.addIdleEvent("test", 1000);
		assertEquals( 1, AnimationThread.getIdleEventNames().length );
		assertEquals( 1, AnimationThread.getIdleEventTimeouts().length );
		AnimationThread.addIdleEvent("test2", 1000);
		assertEquals( 2, AnimationThread.getIdleEventNames().length );
		assertEquals( 2, AnimationThread.getIdleEventTimeouts().length );
		AnimationThread.addIdleEvent("test3", 1000);
		assertEquals( 3, AnimationThread.getIdleEventNames().length );
		assertEquals( 3, AnimationThread.getIdleEventTimeouts().length );
		AnimationThread.addIdleEvent("test4", 1000);
		assertEquals( 4, AnimationThread.getIdleEventNames().length );
		assertEquals( 4, AnimationThread.getIdleEventTimeouts().length );
		AnimationThread.addIdleEvent("test5", 1000);
		assertEquals( 5, AnimationThread.getIdleEventNames().length );
		assertEquals( 5, AnimationThread.getIdleEventTimeouts().length );
		
		
		AnimationThread.removeIdleEvent("test3");
		assertEquals( 4, AnimationThread.getIdleEventNames().length );
		assertEquals( 4, AnimationThread.getIdleEventTimeouts().length );
		assertEquals( "test", AnimationThread.getIdleEventNames()[0] );
		assertEquals( "test2", AnimationThread.getIdleEventNames()[1] );
		assertEquals( "test4", AnimationThread.getIdleEventNames()[2] );
		assertEquals( "test5", AnimationThread.getIdleEventNames()[3] );

		AnimationThread.removeIdleEvent("test");
		assertEquals( 3, AnimationThread.getIdleEventNames().length );
		assertEquals( 3, AnimationThread.getIdleEventTimeouts().length );
		assertEquals( "test2", AnimationThread.getIdleEventNames()[0] );
		assertEquals( "test4", AnimationThread.getIdleEventNames()[1] );
		assertEquals( "test5", AnimationThread.getIdleEventNames()[2] );

		AnimationThread.removeIdleEvent("test5");
		assertEquals( 2, AnimationThread.getIdleEventNames().length );
		assertEquals( 2, AnimationThread.getIdleEventTimeouts().length );		
		assertEquals( "test2", AnimationThread.getIdleEventNames()[0] );
		assertEquals( "test4", AnimationThread.getIdleEventNames()[1] );
		
		AnimationThread.addIdleEvent("test2", 1000);
		assertEquals( 3, AnimationThread.getIdleEventNames().length );
		assertEquals( 3, AnimationThread.getIdleEventTimeouts().length );
		AnimationThread.addIdleEvent("test2", 1000);
		assertEquals( 4, AnimationThread.getIdleEventNames().length );
		assertEquals( 4, AnimationThread.getIdleEventTimeouts().length );
		
		AnimationThread.removeIdleEvent("test2");
		assertEquals( 1, AnimationThread.getIdleEventNames().length );
		assertEquals( 1, AnimationThread.getIdleEventTimeouts().length );
		assertEquals( "test4", AnimationThread.getIdleEventNames()[0] );
		
		AnimationThread.removeIdleEvent("test4");
		assertEquals( null, AnimationThread.getIdleEventNames() );
		assertEquals( null, AnimationThread.getIdleEventTimeouts() );

	}
	
	
	public static void main(String[] args) {
		AnimationThreadTest test = new AnimationThreadTest();
		test.testAddRemoveIdleEvent();
	}
}
