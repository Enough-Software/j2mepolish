/*
 * Created on 30-Nov-2005 at 23:12:37.
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


/**
 * <p>Provides the functionality of the J2SE java.util.HashMap for J2ME applications that uses Open Addressing for resolving collision.</p>
 * <p>WARNING: This Open Addressing HashMap does not support remove() operations! A RuntimeException will be thrown
 *    when remove is used. Clearing of the entire map is supported. 
 * </p> 
 * <p>
 *    This implementation uses Open Addressing for resolving collision that occur when several (different)
 *    keys share the same hash code. In that case the key-value pair will be stored in the next
 *    free slot. There are different probing strategies for finding the next free slot:
 * </p>
 * <ul>
 *   <li><b>linear probing</b><br />
 *       The next slot is used (meaning the index is increased by one until a free slot is found).
 *       This is a very simple and therefore fast operation, but is not good when there are many
 *       keys that share the same hash code (clustering). This is the default strategy and
 *       can be selecting using the AOHashMap.PROBING_LINEAR constant.
 *   </li>
 *   <li><b>quadratic probing</b><br />
 *       in which the interval between probes increases linearly (hence, the indices are described by a quadratic function).
 *       In this strategy the interval start with 3 is increased by one in each step: interval = 3 + step.
 *       You can select this strategy by using the ASHashMap.PROBING_QUADRATIC constanct. 
 *   </li>
 *   <li><b>double hashing</b><br />
 *       in which the interval between probes is fixed for each record but is computed by another hash function.
 *       This is the most expensive strategy but has the advantage that there is very little clustering.
 *       You can select this strategy by using the ASHashMap.PROBING_DOUBLE_HASHING constanct. 
 *   </li>
 * </ul>
 * <p>In contrast to the java.util.Hashtable (which is available on J2ME platforms),
 *    this implementation is not synchronized and faster.
 * </p>
 * <p>The default capacity is 17 for reducing possible collision. Feel free to use another prime number instead 
 *    (like 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113).
 *    When you use a power of 2 for the initial capacity (like 16, 32, 64 and so on), no modulo
 *    operations need to be done, which might be beneficial depending on the hash codes of your keys.  
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        30-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class OAHashMap 
//#if polish.Map.dropInterface != true
	implements Map 
//#endif
{
	
	/** The default capacity is 17 */
	public static final int DEFAULT_INITIAL_CAPACITY = 17;
	/** The default load factor is 75 (=75%), so the HashMap is increased when 75% of it's capacity is reached */ 
	public static final int DEFAULT_LOAD_FACTOR = 60;

	/**
	 * When this probing is used, the next free slot will be used for keys that have the same hash code as a previously inserted item.
	 * 
	 */
	public static final int PROBING_LINEAR = 0;
	/**
	 * When this probing is used, the interval between probes increases linearly.
	 */
	public static final int PROBING_QUADRACTIC = 1;
	/**
	 * When this probing is used, the interval between probes is fixed but calculated using another hash function.
	 */
	public static final int PROBING_DOUBLE_HASHING = 2;

	private final int loadFactor;	
	private Element[] buckets;
	private final boolean isPowerOfTwo;
	private int size;
	private final int probingStrategy;

	/**
	 * Creates a new HashMap with the default initial capacity 17, a load factor of 75% and linear probing. 
	 */
	public OAHashMap() {
		this( DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, PROBING_LINEAR );
	}
	
	/**
	 * Creates a new HashMap with the specified initial capacity, a load factor of 75% and linear probing.
	 * 
	 * @param initialCapacity the initial size of the map, remember that the default load factor 
	 *        is 75%, you if you know the maximum size ahead of time, you need to calculate 
	 *        <code>initialCapacity=maxSize * 4 / 3</code>, when you use this constructor.
	 */
	public OAHashMap(int initialCapacity ) {
		this( initialCapacity, DEFAULT_LOAD_FACTOR, PROBING_LINEAR );
	}
	
	


	/**
	 * Creates a new AOHashMap with the specified initial capacity, the specified load factor and linear probing.
	 * 
	 * @param initialCapacity the initial size of the map.
	 * @param loadFactor the loadfactor in percent, a number between 0 and 100. When the loadfactor is 100,
	 *        the size of this map is only increased after all slots have been filled. 
	 */
	public OAHashMap(int initialCapacity, int loadFactor) {
		this( initialCapacity, loadFactor, PROBING_LINEAR );
	}

	/**
	 * Creates a new AOHashMap with the specified initial capacity, the specified load factor and the specified probing strategy.
	 * 
	 * @param initialCapacity the initial size of the map.
	 * @param loadFactor the loadfactor in percent, a number between 0 and 100. When the loadfactor is 100,
	 *        the size of this map is only increased after all slots have been filled.
	 * @param probingStrategy the probing strategy, either linear, quadratic or double hashing.
	 * @see #PROBING_LINEAR
	 * @see #PROBING_QUADRACTIC
	 * @see #PROBING_DOUBLE_HASHING
	 */
	public OAHashMap(int initialCapacity, int loadFactor, int probingStrategy) {
		this.probingStrategy = probingStrategy;
		// check if initial capacity is a power of 2:
		int capacity = 1;
		while (initialCapacity > capacity) {
			capacity <<= 1;
		}
		this.isPowerOfTwo = (capacity == initialCapacity);
		//System.out.println("isPowerOfTwo: " + this.isPowerOfTwo );
		this.buckets = new Element[ initialCapacity ];
		this.loadFactor = loadFactor;
	}
	
	private int calculateInterval( int hashCode, int length ) {
		int interval = hashCode &= 0x7FFFFFFF; // = Math.abs()
		int newHashCode = 0;
		do {
			newHashCode = 31*newHashCode + (interval >> 3) - 1;
		} while ((interval >>= 3) > 0);
		newHashCode *= 28629151;
		if (this.isPowerOfTwo) {
			interval = (newHashCode & 0x7FFFFFFF) & (length - 1);
		} else {
			interval = (newHashCode & 0x7FFFFFFF) % length;
		}
		if (interval == this.buckets.length) {
			interval--;
		}
		return interval;
		
	}
		

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put( Object key, Object value ) {
		if (key == null || value == null ) {
			throw new IllegalArgumentException("HashMap cannot accept null key [" + key + "] or value [" + value + "].");
		}
		if ( (this.size * 100) / this.buckets.length > this.loadFactor ) {
			increaseSize();
		}
		
		int hashCode = key.hashCode() & 0x7FFFFFFF;
		int index;
		if (this.isPowerOfTwo) {
			index = hashCode & (this.buckets.length - 1);
		} else {
			index = hashCode % this.buckets.length;
		}
		return put( null, key, value, this.buckets, index, this.buckets[index], hashCode );
	}
	
	private Object put( Element newElement, Object key, Object value, Element[] elements, int index, Element element, int hashCode ) {
		int interval = 1;
		if ( element != null ) { 
			if (this.probingStrategy == PROBING_DOUBLE_HASHING ) {
				interval = calculateInterval( hashCode, elements.length );
			} else if (this.probingStrategy == PROBING_QUADRACTIC) {
				interval = 3;
			}
		}
		
		while (true) {
			if (element == null) {
				// found a free slot:
				if (newElement != null) {
					element = newElement;
				} else {
					element = new Element( hashCode, key, value );
					this.size++;
				}
				elements[index] = element;
				return null;
			}
			if ( newElement == null && element.hashCodeValue == hashCode && element.key.equals( key )) {
				Object oldValue = element.value;
				element.value = value;
				return oldValue;
			}
			index += interval;
			if (this.isPowerOfTwo) {
				index &= (elements.length - 1);
			} else {
				index %= elements.length;
			}
			if (this.probingStrategy == PROBING_QUADRACTIC) {
				interval++; 
			}
			element = elements[ index ];
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#get(java.lang.Object)
	 */
	public Object get( Object key ) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		int index;
		int hashCode = key.hashCode()  & 0x7FFFFFFF;
		if (this.isPowerOfTwo) {
			index = hashCode & (this.buckets.length - 1);
		} else {
			index = hashCode % this.buckets.length;
		}
		Element element = this.buckets[ index ];
		if (element == null) {
			return null;
		}
		int interval = 1;
		if (this.probingStrategy == PROBING_DOUBLE_HASHING ) {
			interval = calculateInterval( hashCode, this.buckets.length );
		} else if (this.probingStrategy == PROBING_QUADRACTIC) {
			interval = 3;
		}
		while (true) {
			if (element.hashCodeValue == hashCode && element.key.equals( key )) {
				return element.value;
			}
			index += interval;
			if (this.isPowerOfTwo) {
				index &= (this.buckets.length - 1);
			} else {
				index %= this.buckets.length;
			}
			if (this.probingStrategy == PROBING_QUADRACTIC) {
				interval++; 
			}
			element = this.buckets[ index ];
			if (element == null) {
				return null;
			}
		}
	}
	
	/**
	 * Remove is not supported by the Open Addressing HashMap.
   * @param key the key of the value to remove
	 * @return nothing
	 * @throws RuntimeException always
	 */
	public Object remove( Object key ) {
		throw new RuntimeException("remove not supported.");
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return (this.size == 0);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#size()
	 */
	public int size() {
		return this.size;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey( Object key ) {
		return get( key ) != null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue( Object value ) {
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			if (element != null && element.value.equals( value )) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#clear()
	 */
	public void clear() {
		for (int i = 0; i < this.buckets.length; i++) {
			this.buckets[i] = null;
		}
		this.size = 0;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#values()
	 */
	public Object[] values() {
		return values( new Object[ this.size ] );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#values(java.lang.Object[])
	 */
	public Object[] values(Object[] objects) {
		int index = 0;
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			if (element != null) {
				objects[index] = element.value;
				index++;
			}
		}
		return objects;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#keys()
	 */
	public Object[] keys() {
		return keys( new Object[ this.size ] );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#keys(java.lang.Object[])
	 */
	public Object[] keys(Object[] objects) {
		int index = 0;
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			if (element != null) {
				objects[index] = element.key;
				index++;
			}
		}
		return objects;
	}
	
	/**
	 * Returns String containing the String representations of all objects of this map.
	 * 
	 * @return the stored elements in a String representation.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer( this.size * 23 );
		buffer.append( super.toString() ).append( "{\n" );
		Object[] values = values();
		for (int i = 0; i < values.length; i++) {
			buffer.append( values[i] );
			buffer.append('\n');
		}
		buffer.append('}');
		return buffer.toString();
	}

	
	/**
	 * Increaases the internal capacity of this map.
	 */
	private void increaseSize() {
		int newCapacity;
		if (this.isPowerOfTwo) {
			newCapacity = this.buckets.length << 1; // * 2
		} else {
			newCapacity = (this.buckets.length << 1) - 1; // * 2 - 1 
		}
		Element[] newBuckets = new Element[ newCapacity ];
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			if (element != null) {
				int index;
				if (this.isPowerOfTwo) {
					index = element.hashCodeValue & (newCapacity - 1);
				} else {
					index = element.hashCodeValue % newCapacity;
				}
				Element existingElement = newBuckets[ index ];
				if (existingElement == null ) {
					newBuckets[ index ] = element;
				} else {
					put( element, existingElement.key, existingElement.value, newBuckets, index, existingElement, element.hashCodeValue );
				}
			}
		}
		this.buckets = newBuckets;
	}

	private static final class Element {
		public final Object key;
		public final int hashCodeValue;
		public Object value;
		public Element ( int hashCode, Object key, Object value ) {
			this.hashCodeValue = hashCode;
			this.key = key;
			this.value = value;
		}
	}

	public Iterator keysIterator() {
		// TODO enough implement keysIterator
		return null;
	}

}
