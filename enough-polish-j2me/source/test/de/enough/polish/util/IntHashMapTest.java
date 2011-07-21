/*
 * Created on 01-Dec-2005 at 00:04:40.
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
package de.enough.polish.util;


import java.util.Random;

import junit.framework.TestCase;

public class IntHashMapTest extends TestCase {
	
	private static final int TEST_RUNS = 50000;
	private final int[] integerKeys;
	private final String[] stringKeys;
	private final int[] integerKeysMixed;
	private final String[] stringKeysMixed;

	public IntHashMapTest(String name) {
		super(name);
		this.integerKeys = new int[ TEST_RUNS ];
		this.stringKeys = new String[ TEST_RUNS ];
		Random random = new Random( System.currentTimeMillis() );
		java.util.HashMap map = new java.util.HashMap( TEST_RUNS );
		int i = 0;
		do {
			int key = random.nextInt();
			Integer keyObj = new Integer( key );
			// make sure that each key is unique:
			if (!map.containsKey(keyObj)) {
				this.integerKeys[i] = key;
				this.stringKeys[i] = keyObj.toString();
				 i++;
				 map.put( keyObj, keyObj );
			}
		} while (i < TEST_RUNS);
		// mix integer keyus
		this.integerKeysMixed = new int[ TEST_RUNS ];
		this.stringKeysMixed = new String[ TEST_RUNS ];
		i = 0;
		do {
			int position = random.nextInt( TEST_RUNS );
			int key = this.integerKeys[ position ];
			Integer keyObj = new Integer( key );
			if (map.containsKey(keyObj)) {
				this.integerKeysMixed[i] = key;
				this.stringKeysMixed[i] = this.stringKeys[position];
				 i++;
				 map.remove( keyObj );
			}
		} while (i < TEST_RUNS);
	}
	
	public void testPut() {
		System.out.println(">>> put()");
		IntHashMap map = new IntHashMap();
		System.gc();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			int key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
			assertEquals( i + 1, map.size() );
		}
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for putting " + TEST_RUNS + " values into de.enough.polish.util.HashMap.");
		

	}
	
	
	public void testRemove() {
		System.out.println(">>> remove()");
		
		IntHashMap map = new IntHashMap();
		for (int i = 0; i < TEST_RUNS; i++) {
			int key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
		}
		int size = TEST_RUNS;
		System.gc();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			int key = this.integerKeysMixed[i];
			Object value = this.stringKeysMixed[i];
			Object previous = map.remove(key);
			assertNotNull( previous );
			assertEquals( value, previous );
			size--;
			assertEquals( size, map.size() );
		}
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for removing " + TEST_RUNS + " keys from de.enough.polish.util.HashMap.");
		
	}
	
	public void testPutWithSameKeys() {
		IntHashMap map = new IntHashMap();
		int key = 12;
		Object[] values = new Object[]{"one", "two", new Integer(3), "four"};
		
		Object previous = map.put( key, values[0]);
		assertNull( previous );
		
		previous = map.put( key, values[1]);
		assertEquals( values[0], previous );
		assertEquals( 1, map.size() );
		
		previous = map.put( key, values[2]);
		assertEquals( values[1], previous );
		assertEquals( 1, map.size() );

		previous = map.put( key, values[3]);
		assertEquals( values[2], previous );
		assertEquals( 1, map.size() );
		
		assertEquals( values[3], map.get( key ));
		assertEquals( 1, map.size() );
		assertEquals( values[3], map.remove( key ));
		assertEquals( 0, map.size() );
	}
	
	public void testGet() {
		System.out.println(">>> get()");
		IntHashMap map = new IntHashMap();
		for (int i = 0; i < TEST_RUNS; i++) {
			int key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
		}
		System.gc();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			int key = this.integerKeysMixed[i];
			Object value = this.stringKeysMixed[i];			
			assertEquals( value, map.get( key ));
		}
		
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for getting " + TEST_RUNS + " values in de.enough.polish.util.HashMap.");
	}
	
	public void testContainsKey() {
		System.out.println(">>> containsKey()");
		IntHashMap map = new IntHashMap();
		for (int i = 0; i < TEST_RUNS; i++) {
			int key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
		}
		System.gc();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			int key = this.integerKeysMixed[i];
			assertTrue( map.containsKey( key ));
		}		
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys in de.enough.polish.util.HashMap.");
			}
	
	public void testContainsValue() {
		System.out.println(">>> containsValue()");
		IntHashMap map = new IntHashMap();
		for (int i = 0; i < TEST_RUNS; i++) {
			int key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
		}
		System.gc();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS/10; i++) {
			Object value = this.stringKeysMixed[i];			
			assertTrue( map.containsValue( value ));
		}		
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS/10 + " values in de.enough.polish.util.HashMap.");
			}

	public void testValues() {
		System.out.println(">>> values()");
		IntHashMap map = new IntHashMap();
		Object[] values = map.values();
		assertEquals( 0, values.length );
		for (int i = 0; i < TEST_RUNS; i++) {
			int key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
		}
		System.gc();
		long time = System.currentTimeMillis();
		values = map.values();
		assertNotNull( values );
		assertEquals( map.size(), values.length );
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for extracting " + TEST_RUNS + " values from de.enough.polish.util.HashMap.");
		System.gc();
		time = System.currentTimeMillis();
		String[] stringValues = (String[]) map.values( new String[ map.size() ]);
		assertNotNull( stringValues );
		assertEquals( map.size(), stringValues.length );
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for extracting " + TEST_RUNS + " String values from de.enough.polish.util.HashMap.");

			}
}
