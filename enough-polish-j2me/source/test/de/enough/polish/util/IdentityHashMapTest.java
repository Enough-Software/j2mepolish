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


import java.util.Collection;
import java.util.Hashtable;
import java.util.Random;

import junit.framework.TestCase;

public class IdentityHashMapTest extends TestCase {
	
	private static final int TEST_RUNS = 50000;
	private final Integer[] integerKeys;
	private final String[] stringKeys;
	private final Integer[] integerKeysMixed;
	private final String[] stringKeysMixed;

	public IdentityHashMapTest(String name) {
		super(name);
		this.integerKeys = new Integer[ TEST_RUNS ];
		this.stringKeys = new String[ TEST_RUNS ];
		Random random = new Random( System.currentTimeMillis() );
		java.util.HashMap map = new java.util.HashMap( TEST_RUNS );
		int i = 0;
		do {
			Integer key = new Integer( random.nextInt() );
			// make sure that each key is unique:
			if (!map.containsKey(key)) {
				this.integerKeys[i] = key;
				this.stringKeys[i] = key.toString();
				 i++;
				 map.put( key, key );
			}
		} while (i < TEST_RUNS);
		// mix integer keyus
		this.integerKeysMixed = new Integer[ TEST_RUNS ];
		this.stringKeysMixed = new String[ TEST_RUNS ];
		i = 0;
		do {
			int position = random.nextInt( TEST_RUNS );
			Integer key = this.integerKeys[ position ]; 
			if (map.containsKey(key)) {
				this.integerKeysMixed[i] = key;
				this.stringKeysMixed[i] = this.stringKeys[position];
				 i++;
				 map.remove( key );
			}
		} while (i < TEST_RUNS);
	}
	
	public void testKeysIterator() {
		System.out.println(">>> keysIterator()");
		IdentityHashMap map = new IdentityHashMap();
		Hashtable table = new Hashtable();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
			table.put( key, value );
		}
		System.gc();
		long time = System.currentTimeMillis();
		Iterator iterator = map.keysIterator();
		assertTrue( iterator.hasNext() );
		int size = 0;
		while (iterator.hasNext()) {
			Object key = iterator.next();
			assertNotNull(key);
			Object value = table.remove(key);
			assertNotNull(value);
			size++;
		}
		assertEquals( map.size(), size );
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for iterating over " + TEST_RUNS + " keys from de.enough.polish.util.IdentityHashMap.");
	}

	public void testKeysIteratorRemove() {
		System.out.println(">>> keysIteratorRemove()");
		IdentityHashMap map = new IdentityHashMap();
		Hashtable table = new Hashtable();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
			table.put( key, value );
		}
		System.gc();
		long time = System.currentTimeMillis();
		Iterator iterator = map.keysIterator();
		assertTrue( iterator.hasNext() );
		while (iterator.hasNext()) {
			Object key = iterator.next();
			assertNotNull(key);
			Object value = table.remove(key);
			assertNotNull(value);
			
			iterator.remove();
			
			assertNull( map.get(key));
		}
		assertEquals( 0, map.size() );
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for iterating over and removing " + TEST_RUNS + " keys from de.enough.polish.util.HashMap.");
	}	
	public void testPut() {
		System.out.println(">>> put()");
		IdentityHashMap map = new IdentityHashMap();
		System.gc();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
			assertEquals( i + 1, map.size() );
		}
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for putting " + TEST_RUNS + " values into de.enough.polish.util.HashMap.");
		
		java.util.HashMap j2semap = new java.util.HashMap(); 
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			j2semap.put( key, value );
			assertEquals( i + 1, j2semap.size() );
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for putting " + TEST_RUNS + " values into java.util.HashMap.");
		
		Hashtable table = new Hashtable(); 
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			table.put( key, value );
			assertEquals( i + 1, table.size() );
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for putting " + TEST_RUNS + " values into java.util.Hashtable.");

	}
	
	
	public void testRemove() {
		System.out.println(">>> remove()");
		
		IdentityHashMap map = new IdentityHashMap();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
		}
		int size = TEST_RUNS;
		System.gc();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeysMixed[i];
			Object value = this.stringKeysMixed[i];
			Object previous = map.remove(key);
			assertNotNull( previous );
			assertEquals( value, previous );
			size--;
			assertEquals( size, map.size() );
		}
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for removing " + TEST_RUNS + " keys from de.enough.polish.util.HashMap.");
		
		java.util.HashMap j2semap = new java.util.HashMap(); 
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			j2semap.put( key, value );
		}
		size = TEST_RUNS;
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeysMixed[i];
			Object value = this.stringKeysMixed[i];
			Object previous = j2semap.remove(key);
			assertNotNull( previous );
			assertEquals( value, previous );
			size--;
			assertEquals( size, j2semap.size() );
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for removing " + TEST_RUNS + " keys from java.util.HashMap.");
		
		Hashtable table = new Hashtable(); 
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			table.put( key, value );
		}
		size = TEST_RUNS;
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeysMixed[i];
			Object value = this.stringKeysMixed[i];
			Object previous = table.remove(key);
			assertNotNull( previous );
			assertEquals( value, previous );
			size--;
			assertEquals( size, table.size() );
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for removing " + TEST_RUNS + " keys from java.util.Hashtable.");

	}
	
	public void testPutWithSameKeys() {
		IdentityHashMap map = new IdentityHashMap();
		String key = "key";
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
		IdentityHashMap map = new IdentityHashMap();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
		}
		System.gc();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeysMixed[i];
			Object value = this.stringKeysMixed[i];			
			assertEquals( value, map.get( key ));
		}
		
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for getting " + TEST_RUNS + " values in de.enough.polish.util.HashMap.");
		
		java.util.HashMap j2semap = new java.util.HashMap(); 
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			j2semap.put( key, value );
		}
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeysMixed[i];
			Object value = this.stringKeysMixed[i];			
			assertEquals( value, j2semap.get( key ));
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for getting " + TEST_RUNS + " values in java.util.HashMap.");
		
		Hashtable table = new Hashtable(); 
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			table.put( key, value );
		}
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeysMixed[i];
			Object value = this.stringKeysMixed[i];			
			assertEquals( value, table.get( key ));
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for getting " + TEST_RUNS + " values in java.util.Hashtable.");
	}
	
	public void testContainsKey() {
		System.out.println(">>> containsKey()");
		IdentityHashMap map = new IdentityHashMap();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			map.put( key, value );
		}
		System.gc();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeysMixed[i];
			assertTrue( map.containsKey( key ));
		}		
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys in de.enough.polish.util.HashMap.");
		
		java.util.HashMap j2semap = new java.util.HashMap(); 
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			j2semap.put( key, value );
		}
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeysMixed[i];
			assertTrue( j2semap.containsKey( key ));
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys in java.util.HashMap.");
		
		Hashtable table = new Hashtable(); 
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			table.put( key, value );
		}
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeysMixed[i];
			assertTrue( table.containsKey( key ));
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys in java.util.Hashtable.");
	}
	
	public void testContainsValue() {
		System.out.println(">>> containsValue()");
		IdentityHashMap map = new IdentityHashMap();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
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
		
		java.util.HashMap j2semap = new java.util.HashMap(); 
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			j2semap.put( key, value );
		}
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS/10; i++) {
			Object value = this.stringKeysMixed[i];			
			assertTrue( j2semap.containsValue( value ));
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS/10 + " values in java.util.HashMap.");
		
		Hashtable table = new Hashtable(); 
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			table.put( key, value );
		}
		System.gc();
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS/10; i++) {
			Object value = this.stringKeysMixed[i];			
			assertTrue( table.get( value ) != null );
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS/10 + " values in java.util.Hashtable.");
	}

	public void testValues() {
		System.out.println(">>> values()");
		IdentityHashMap map = new IdentityHashMap();
		Object[] values = map.values();
		assertEquals( 0, values.length );
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
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

		
		
		java.util.HashMap j2semap = new java.util.HashMap();
		Collection col = j2semap.values();
		values = col.toArray();
		assertEquals( 0, values.length  );
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = this.integerKeys[i];
			Object value = this.stringKeys[i];
			j2semap.put( key, value );
		}
		System.gc();
		time = System.currentTimeMillis();
		col = j2semap.values();
		values = col.toArray();
		assertNotNull( values );
		assertEquals( j2semap.size(), values.length );
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for extracting " + TEST_RUNS + " values from java.util.HashMap.");

//		Hashtable table = new Hashtable();
//		col = table.values();
//		values = col.toArray();
//		assertEquals( 0, values.length  );
//		for (int i = 0; i < TEST_RUNS; i++) {
//			Object key = this.integerKeys[i];
//			Object value = this.stringKeys[i];
//			table.put( key, value );
//		}
//		System.gc();
//		time = System.currentTimeMillis();
//		col = table.values();
//		values = col.toArray();
//		assertNotNull( values );
//		assertEquals( table.size(), values.length );
//		neededTime = System.currentTimeMillis() - time;
//		System.out.println("needed " + neededTime + "ms for extracting " + TEST_RUNS + " values from java.util.Hashtable.");
	}
}
