/*
 * Created on Dec 9, 2008 at 7:54:59 PM.
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

/**
 * <p>Provides a list that may contain several duplicate keys</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @param <K> type of keys; when you use the enough-polish-client-java5.jar you can parameterize the KeyValueList, e.g. KeyValueList&lt;Integer, String&gt; = new KeyValueList&lt;Integer, String&gt;(10); 
 * @param <V> type of values; when you use the enough-polish-client-java5.jar you can parameterize the KeyValueList, e.g. KeyValueList&lt;Integer, String&gt; = new KeyValueList&lt;Integer, String&gt;(10); 
 */
public class KeyValueList 
//#if polish.java5
<K, V>
//#endif
implements Externalizable
{
	public final ArrayList
		//#if polish.java5
		<K>
		//#endif
			keys;
	public final ArrayList 
		//#if polish.java5
		<V>
		//#endif
		values;
	

	/**
	 * Creates an KeyValueList with the initial capacity of 10 and a growth factor of 75%
	 */
	public KeyValueList() {
		this( 10, 75 );
	}
	
	/**
	 * Creates an KeyValueList with the given initial capacity and a growth factor of 75%
	 * 
	 * @param initialCapacity the capacity of this list.
	 */
	public KeyValueList( int initialCapacity ) {
		this( initialCapacity, 75 );
	}

	/**
	 * Creates a new KeyValueList
	 * 
	 * @param initialCapacity the capacity of this list.
	 * @param growthFactor the factor in % for increasing the capacity 
	 * 								  when there's not enough room in this list anymore 
	 */
	public KeyValueList( int initialCapacity, int growthFactor ) {
		this.keys = new ArrayList
			//#if polish.java5
			<K>
			//#endif
			(initialCapacity, growthFactor );
		this.values = new ArrayList
			//#if polish.java5
			<V>
			//#endif
			(initialCapacity, growthFactor );
	}
	
	/**
	 * Retrieves the current size of this array list.
	 *  
	 * @return the number of stored elements in this list.
	 */
	public int size() {
		return this.keys.size();
	}
	

	/**
	 * Determines whether the given key is stored in this list.
	 * 
	 * @param element the key which might be stored in this list
	 * @return true when the given element is stored in this list
	 * @throws IllegalArgumentException when the given element is null
	 * @see #remove(Object)
	 */
	//#if polish.java5
		public boolean containsKey( K element ) {
	//#else
		//# public boolean containsKey( Object element ) {
	//#endif
			return this.keys.contains(element);
	}
		
	/**
	 * Determines whether the given value is stored in this list.
	 * 
	 * @param element the value which might be stored in this list
	 * @return true when the given element is stored in this list
	 * @throws IllegalArgumentException when the given element is null
	 * @see #remove(Object)
	 */
	//#if polish.java5
		public boolean containsValue( V element ) {
	//#else
		//# public boolean containsValue( Object element ) {
	//#endif
			return this.values.contains(element);
	}
	
	/**
	 * Retrieves the (first) index of the given key.
	 * 
	 * @param element the key which is part of this list.
	 * @return the index of the key or -1 when the object is not part of this list.
	 * @throws IllegalArgumentException when the given element is null
	 */
	//#if polish.java5
		public int indexOfKey(K element) {
	//#else
		//# public int indexOfKey(Object element) {
	//#endif
		return this.keys.indexOf(element);
	}
		
	/**
	 * Retrieves the (first) index of the given value.
	 * 
	 * @param element the value which is part of this list.
	 * @return the index of the value or -1 when the object is not part of this list.
	 * @throws IllegalArgumentException when the given element is null
	 */
	//#if polish.java5
		public int indexOfValue(V element) {
	//#else
		//# public int indexOfValue(Object element) {
	//#endif
		return this.values.indexOf(element);
	}
	
	/**
	 * Returns the key at the specified position in this list.
	 *  
	 * @param index the position of the desired element.
	 * @return the key stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	//#if polish.java5
		public K getKey( int index ) {
	//#else
		//# public Object getKey( int index ) {
	//#endif
		return this.keys.get(index);
	}
		
	/**
	 * Returns the value at the specified position in this list.
	 *  
	 * @param index the position of the desired element.
	 * @return the value stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	//#if polish.java5
		public V getValue( int index ) {
	//#else
		//# public Object getValue( int index ) {
	//#endif
		return this.values.get(index);
	}
		
	/**
	 * Removes the key-value pair at the specified position in this list.
	 *  
	 * @param index the position of the desired element.
	 * @return the key stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	//#if polish.java5
		public K remove( int index ) {
	//#else
		//# public Object remove( int index ) {
	//#endif
		this.values.remove( index );
		return this.keys.remove(index);
	}
	
	/**
	 * Removes the given key and the corresponding value.
	 * 
	 * @param element the element which should be removed.
	 * @return true when the element was found in this list.
	 * @throws IllegalArgumentException when the given element is null
	 * @see #containsKey(Object)
	 */
	//#if polish.java5
		public boolean remove( K element ) {
	//#else
		//# public boolean remove( Object element ) {
	//#endif
		if (element == null) {
			throw new IllegalArgumentException();
		}
		int index = indexOfKey(element);
		if (index == -1) {
			return false;
		}
		remove( index );
		return true; 
	}
	
	/**
	 * Removes all of the elements from this list. 
	 * The list will be empty after this call returns. 
	 */
	public void clear() {
		this.keys.clear();
		this.values.clear();
	}

	/**
	 * Stores the given key-value pair in this list.
	 * 
	 * @param key the key
	 * @param value the value
	 * @throws IllegalArgumentException when the given key or value is null
	 * @see #add( int, Object, Object )
	 */
	//#if polish.java5
		public void add( K key, V value) {
	//#else
		//# public void add( Object key, Object value) {
	//#endif
		this.keys.add(key);
		this.values.add(value);
	}
	
	/**
	 * Inserts the given element at the defined position.
	 * Any following elements are shifted one position to the back.
	 * 
	 * @param index the position at which the element should be inserted, 
	 * 					 use 0 when the element should be inserted in the front of this list.
	 * @param key the key
	 * @param value the value
	 * @throws IllegalArgumentException when the given element is null
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	//#if polish.java5
		public void add( int index, K key, V value ) {
	//#else
		//# public void add( int index, Object key, Object value ) {
	//#endif
		this.keys.add(index, key);
		this.values.add(index, value);
	}
	
	/**
	 * Replaces the key-value pair at the specified position in this list with the specified element. 
	 * 
	 * @param index the position of the element, the first element has the index 0.
	 * @param key the key
	 * @param value the value
	 * @return the replaced key element
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	//#if polish.java5
		public K set( int index, K key, V value ) {
	//#else
		//# public Object set( int index, Object key, Object value ) {
	//#endif
		this.values.set(index, value);
		return this.keys.set( index, key);
	}
	

	/**
	 * Retrieves the internal key array - use with care!
	 * This method allows to access stored objects without creating an intermediate
	 * array. You really should refrain from changing any elements in the returned array
	 * unless you are 110% sure about what you are doing. It is safe to cycle through this
	 * array to access it's elements, though. Note that some array positions might contain null.
	 * Also note that the internal array is changed whenever this list has to be increased.
	 * 
	 * @return the internal array
	 */
	public Object[] getInternalKeyArray() {
		return this.keys.getInternalArray();
	}
	
	/**
	 * Retrieves the internal value array - use with care!
	 * This method allows to access stored objects without creating an intermediate
	 * array. You really should refrain from changing any elements in the returned array
	 * unless you are 110% sure about what you are doing. It is safe to cycle through this
	 * array to access it's elements, though. Note that some array positions might contain null.
	 * Also note that the internal array is changed whenever this list has to be increased.
	 * 
	 * @return the internal array
	 */
	public Object[] getInternalValueArray() {
		return this.keys.getInternalArray();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		this.keys.read(in);
		this.values.read(in);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		this.keys.write( out );
		this.values.write( out );
	}
}
