/*
 * Created on 03-Jan-2004 at 18:12:09.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
 * <p>Tests the ArrayList</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        03-Jan-2004 - rob creation
 * </pre>
 */
public class ArrayListTest extends TestCase {
	
	String o1 = "1"; String o2 = "2"; String o3 = "3"; String o4 = "4"; String o5 = "5"; String o6 = "6";
	Object[] array1 = new Object[] { this.o1, this.o2, this.o3, this.o4, this.o5, this.o6 };
	
	public ArrayListTest() {
		super();
	}
	

	public ArrayListTest(String name) {
		super(name);
	}
	

	public void testAdd() {
		ArrayList list = new ArrayList(); // initial capacity == 10
		assertEquals( 0, list.size() );
		assertEquals( 0, list.toArray().length );
		list.add( this.o1 );
		assertEquals( 1, list.size() );
		Object[] objects = list.toArray();
		assertEquals( 1, objects.length);
		String[] strings = (String[]) list.toArray( new String[ list.size() ]);
		assertEquals( 1, strings.length);
		list.add( this.o2 );
		assertEquals( 2, list.size() );
		list.add( this.o3 );
		assertEquals( 3, list.size() );
		compareWithArray( list );
		list.add( this.o4 );
		assertEquals( 4, list.size() );
		list.add( this.o5 );
		assertEquals( 5, list.size() );
		list.add( this.o6 );
		assertEquals( 6, list.size() );
		compareWithArray( list );
		list.add( this.o1 );
		assertEquals( 7, list.size() );
		list.add( this.o2 );
		assertEquals( 8, list.size() );
		list.add( this.o3 );
		assertEquals( 9, list.size() );
		list.add( this.o2 );
		assertEquals( 10, list.size() );
		list.add( this.o3 );
		assertEquals( 11, list.size() );
		assertEquals( 11, list.toArray().length );
		try {
			list.add(null);
			fail("add(null) should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected behaviour
		}
	}
	
	public void testRemove() {
		ArrayList list = new ArrayList( 3 );
		list.add( this.o1 );
		list.add( this.o2 );
		list.add( this.o3 );
		
		Object[] objects = list.getInternalArray();
		assertEquals( this.o1, objects[0] );
		assertEquals( this.o2, objects[1] );
		assertEquals( this.o3, objects[2] );
		
		// illegal remove:
		try {
			list.remove( 3 );
			fail("ArrayList.remove(3) should fail when the list has only [" + list.size() +"] entries.");
		} catch (IndexOutOfBoundsException e) {
			// okay, expected behaviour!
		}
		try {
			list.remove( -1 );
			fail("ArrayList.remove(-1) should fail.");
		} catch (IndexOutOfBoundsException e) {
			// okay, expected behaviour!
		}
		// now try remove an element which does not exist in list:
		assertEquals( false, list.remove( this.o6 ));
		// now remove the third (and last) element:
		objects = list.getInternalArray();
		assertEquals( this.o1, objects[0] );
		assertEquals( this.o2, objects[1] );
		assertEquals( this.o3, objects[2] );
		assertEquals( 3, list.size() );
		Object o = list.remove( 2 );
		assertEquals( this.o3, o );
		assertEquals( 2, list.size() );
		// check internal array:
		objects = list.getInternalArray();
		assertEquals( this.o1, objects[0] );
		assertEquals( this.o2, objects[1] );
		assertEquals( null, objects[2] );
		list.add(this.o3);
		assertTrue( list.remove( this.o3 ) );
		list.add( this.o3 );
		assertEquals( 3, list.size() );
		compareWithArray( list );
		assertTrue(list.remove( this.o1 ) );
		assertEquals( 2, list.size() );
		assertEquals( this.o2, list.remove( 0 ) ); // == o2
		assertEquals( 1, list.size() );
		assertTrue( list.remove( this.o3 ) );
		assertEquals( 0, list.size() );
		list.add(this.o1);
		assertEquals(1, list.size() );
		try {
			list.remove(null);
			fail("remove(null) should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected behaviour
		}
		
		list = new ArrayList( 3 );
		list.add( this.o1 );
		list.add( this.o2 );
		list.add( this.o3 );
		list.remove( this.o1 );
		objects = list.getInternalArray();
		assertEquals( this.o2, objects[0] );
		assertEquals( this.o3, objects[1] );
		assertEquals( null, objects[2] );

	}
	
	public void testInsert(){
		ArrayList list = new ArrayList( 3 );
		list.add( this.o2 );
		list.add( this.o3 );
		list.add( this.o4 );
		assertEquals( 3, list.size() );
		list.add( 0, this.o1 );
		assertEquals( 4, list.size() );
		compareWithArray( list );
		assertTrue( list.remove( this.o3 ) );
		assertEquals( 3, list.size() );
		list.add( 2, this.o3 );
		assertEquals( 4, list.size() );
		compareWithArray( list );
		// invalid insert:
		try {
			list.add( 5, this.o6 );
			fail("insert should fail for an invalid position.");
		} catch (IndexOutOfBoundsException e) {
			// expected behaviour
		}
	}
	
	public void testClear() {
		ArrayList list = new ArrayList( 3 );
		list.add( this.o2 );
		list.add( this.o3 );
		list.add( this.o4 );
		assertEquals( 3, list.size() );
		list.clear();
		assertEquals( 0, list.size() );
	}
	
	public void testTrimToSize() {
		ArrayList list = new ArrayList();
		list.add( this.o1 );
		list.add( this.o2 );
		list.add( this.o3 );
		list.add( this.o4 );
		list.trimToSize();
		compareWithArray( list );
		list.clear();
		assertEquals( 0, list.size() );
		list.trimToSize();
		assertEquals( 0, list.size() );
		list.add( this.o1 );
		assertEquals( 1, list.size() );
	}
	
	public void testContains() {
		ArrayList list = new ArrayList( 5 );
		list.add( this.o1 );
		list.add( this.o2 );
		list.add( this.o3 );
		list.add( this.o4 );
		assertTrue( list.contains( this.o2 ));
		assertTrue( list.contains( this.o4 ));
		assertTrue( list.contains( this.o1 ));
		assertTrue( list.contains( this.o3 ));
		assertFalse( list.contains( this.o5 ));
		assertFalse( list.contains( this.o6 ));
	}
	
	public void testGet() {
		ArrayList list = new ArrayList( 5 );
		list.add( this.o1 );
		list.add( this.o2 );
		list.add( this.o3 );
		list.add( this.o4 );
		assertEquals( this.o1, list.get(0));
		assertEquals( this.o2, list.get(1));
		assertEquals( this.o3, list.get(2));
		assertEquals( this.o4, list.get(3));
		try {
			list.get(4);
			fail("get() should throw IndexOutOfBoundsException when invalid position is given.");
		} catch (IndexOutOfBoundsException e) {
			// expected behaviour
		}
	}
	
	public void testToArray() {
		ArrayList list = new ArrayList( 5 );
		list.add( this.o1 );
		list.add( this.o2 );
		list.add( this.o3 );
		list.add( this.o4 );
		// this should result in a class cast exception:
		try {
			String[] strings = (String[]) list.toArray();
			fail( "toArray() cannot be casted to a String[]");
			assertEquals( list.size(), strings.length );
		} catch (ClassCastException e) {
			// exptected behaviour
		}
		// this should work now:
		String[] strings = new String[ list.size() ];
		strings = (String[]) list.toArray( strings );
		assertEquals( list.size(), strings.length );
		compareWithArray( list );
	}
	
	private void compareWithArray( ArrayList list ) {
		Object[] store = list.toArray();
		assertEquals( list.size(), store.length );
		for (int i = 0; i < store.length; i++) {
			assertEquals( store[i], this.array1[i] );
		}
	}

}
