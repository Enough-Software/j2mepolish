/*
 * Created on Aug 30, 2007 at 8:13:18 PM.
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
 * <p>Provides an flexible list for storing objects.</p>
 * <p>
 * This ArrayList is mostly compatible with the java.util.ArrayList of the J2SE.
 * It lacks, however, some not often used methods. Also some methods like add or remove 
 * do not return a boolean value, since true is always returned (or an exception is thrown) anyhow.
 * This way we can save some precious space!
 * </p>
 * <p>
 * Workarounds for some of the missing methods:
 * <ul>
 * 	<li><b>isEmpty()</b> Use "list.size() == 0" instead.</li> 
 *		<li><b>ensureCapacity(int)</b> Define the expected capacity in the constructor.</li>
 *		<li><b>indexOf(Object)/lastIndexOf(Object)</b> You have to do them manually. 
 *				Use "list.toArray()" to get the stored objects, cycle through them and test for equality.</li>
 * 	<li><b>addAll(Collection)</b> Add all elements of the collection singlely.</li> 
 * </ul>
 * </p>
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        Aug 30, 2007 - rob creation
 * </pre>
 */
public class IntList
implements Externalizable
{
	private int[] storedObjects;
	private int growthFactor;
	private int size;
	
	/**
	 * Creates an ArrayList with the initial capacity of 10 and a growth factor of 75%
	 */
	public IntList() {
		this( 10, 75 );
	}
	
	/**
	 * Creates an ArrayList with the given initial capacity and a growth factor of 75%
	 * 
	 * @param initialCapacity the capacity of this array list.
	 */
	public IntList( int initialCapacity ) {
		this( initialCapacity, 75 );
	}

	/**
	 * Creates a new ArrayList
	 * 
	 * @param initialCapacity the capacity of this array list.
	 * @param growthFactor the factor in % for increasing the capacity 
	 * 								  when there's not enough room in this list anymore 
	 */
	public IntList( int initialCapacity, int growthFactor ) {
		this.storedObjects = new int[ initialCapacity ];
		this.growthFactor = growthFactor;
	}
	
	/**
	 * Retrieves the current size of this array list.
	 *  
	 * @return the number of stored elements in this list.
	 */
	public int size() {
		return this.size;
	}
	
	/**
	 * Determines whether the given element is stored in this list.
	 * 
	 * @param element the element which might be stored in this list
	 * @return true when the given element is stored in this list
	 * @see #removeElement(int)
	 */
	public boolean contains( int element ) {
		for (int i = 0; i < this.size; i++) {
			int object = this.storedObjects[i];
			if ( object == element ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Retrieves the index of the given object.
	 * 
	 * @param element the int which is part of this list.
	 * @return the index of the object or -1 when the object is not part of this list.
	 */
	public int indexOf(int element) {
		for (int i = 0; i < this.size; i++) {
			int object = this.storedObjects[i];
			if ( object == element ) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the element at the specified position in this list.
	 *  
	 * @param index the position of the desired element.
	 * @return the element stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public int get( int index ) {
		if (index < 0 || index >= this.size ) {
			throw new IndexOutOfBoundsException("the index [" + index + "] is not valid for this list with the size [" + this.size + "].");
		}
		return this.storedObjects[ index ];
	}
		
	/**
	 * Removes the element at the specified position in this list.
	 *  
	 * @param index the position of the desired element.
	 * @return the element stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public int removeElementAt( int index ) {
		if (index < 0 || index >= this.size ) {
			throw new IndexOutOfBoundsException("the index [" + index + "] is not valid for this list with the size [" + this.size + "].");
		}
		int removed = this.storedObjects[ index ];
		for (int i = index+1; i < this.size; i++) {
			this.storedObjects[ i-1 ] = this.storedObjects[ i ];
		}
		this.size--;
		return removed;
	}
	
	/**
	 * Removes the given element.
	 * 
	 * @param element the element which should be removed.
	 * @return true when the element was found in this list.
	 * @see #contains(int)
	 */
	public boolean removeElement( int element ) {
		int index = -1;
		for (int i = 0; i < this.size; i++) {
			int object = this.storedObjects[i];
			if ( object == element ) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			return false;
		}
		for (int i = index+1; i < this.size; i++) {
			this.storedObjects[ i-1 ] = this.storedObjects[ i ];
		}
		this.size--;
		return true; 
	}
	
	/**
	 * Removes all of the elements from this list. 
	 * The list will be empty after this call returns. 
	 */
	public void clear() {
		this.size = 0;
	}

	/**
	 * Stores the given element in this list.
	 * 
	 * @param element the element which should be appended to this list.
	 * @see #add( int, int )
	 */
	public void add( int element) {
		if (this.size >= this.storedObjects.length) {
			increaseCapacity();
		}
		this.storedObjects[ this.size ] = element;
		this.size++;
	}
	
	/**
	 * Inserts the given element at the defined position.
	 * Any following elements are shifted one position to the back.
	 * 
	 * @param index the position at which the element should be inserted, 
	 * 					 use 0 when the element should be inserted in the front of this list.
	 * @param element the element which should be inserted
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public void add( int index, int element ) {
		if (index < 0 || index > this.size ) {
			throw new IndexOutOfBoundsException("the index [" + index + "] is not valid for this list with the size [" + this.size + "].");
		}
		if (this.size >= this.storedObjects.length) {
			increaseCapacity();
		}
		// shift all following elements one position to the back:
		for (int i = this.size; i > index; i--) {
			this.storedObjects[i] = this.storedObjects[ i-1 ];
		} 
		// insert the given element:
		this.storedObjects[ index ] = element;
		this.size++;
	}
	
	/**
	 * Replaces the element at the specified position in this list with the specified element. 
	 * 
	 * @param index the position of the element, the first element has the index 0.
	 * @param element the element which should be set
	 * @return the replaced element
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public int set( int index, int element ) {
		if (index < 0 || index >= this.size ) {
			throw new IndexOutOfBoundsException("the index [" + index + "] is not valid for this list with the size [" + this.size + "].");
		}
		int replaced = this.storedObjects[ index ];
		this.storedObjects[ index ] = element;
		return replaced;
	}
	
	/**
	 * Returns String containing the String representations of all objects of this list.
	 * 
	 * @return the stored elements in a String representation.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer( this.size * 2 );
		buffer.append( super.toString() ).append( "{\n" );
		for (int i = 0; i < this.size; i++) {
			buffer.append( this.storedObjects[i] );
			buffer.append('\n');
		}
		buffer.append('}');
		return buffer.toString();
	}
	
	/**
	 * Returns all stored elements as an array.
	 * 
	 * @return the stored elements as an array.
	 */
	public int[] toArray() {
		int[] copy = new int[ this.size ];
		System.arraycopy( this.storedObjects, 0, copy, 0, this.size );
		return copy;
	}
	
	/**
	 * Trims the capacity of this ArrayList instance to be the list's current size. 
	 * An application can use this operation to minimize the storage of an ArrayList instance.
	 */
	public void trimToSize() {
		if (this.storedObjects.length != this.size ) {
			int[] newStore = new int[ this.size ];
			System.arraycopy( this.storedObjects, 0, newStore, 0, this.size );
			this.storedObjects = newStore;
		}
	}
	

	/**
	 * increases the capacity of this list.
	 */
	private void increaseCapacity() {
		int currentCapacity = this.storedObjects.length;
		int newCapacity = currentCapacity + ((currentCapacity * this.growthFactor) / 100);
		if (newCapacity == currentCapacity ) {
			newCapacity++;
		}
		int[] newStore = new int[ newCapacity ];
		System.arraycopy( this.storedObjects, 0, newStore, 0, this.size );
		this.storedObjects = newStore;
	}

	/**
	 * Retrieves the internal array - use with care!
	 * This method allows to access stored objects without creating an intermediate
	 * array. You really should refrain from changing any elements in the returned array
	 * unless you are 110% sure about what you are doing. It is safe to cycle through this
	 * array to access it's elements, though. Note that some array positions might contain null.
	 * Also note that the internal array is changed whenever this list has to be increased.
	 * 
	 * @return the internal array
	 */
	public int[] getInternalArray() {
		return this.storedObjects;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int storeSize = in.readInt();
		int growFactor = in.readInt();
		int[] store = new int[ storeSize ];
		for (int i = 0; i < store.length; i++) {
			store[i] = in.readInt();
		}
		this.storedObjects = store;
		this.size = storeSize;
		this.growthFactor = growFactor;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( this.size );
		out.writeInt( this.growthFactor );
		for (int i = 0; i < this.size; i++) {
			int o = this.storedObjects[i];
			out.writeInt( o );
		}
	}

}
