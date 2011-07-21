/*
 * Created on Jan 28, 2009 at 6:12:18 PM.
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
 * <p>Provides a lifo stack for primitive boolean types</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BooleanStack
implements Externalizable
{
	
	private boolean[] storedObjects;
	private int growthFactor;
	private int size;


	/**
	 * Creates a new stack with the initial capacity of 10 and a growth factor of 75%
	 */
	public BooleanStack() {
		this(10, 75);
	}

	/**
	 * Creates a new stack with the given initial capacity and a growth factor of 75%
	 * 
	 * @param initialCapacity the capacity of this stack.
	 */
	public BooleanStack(int initialCapacity) {
		this(initialCapacity, 75);
	}

	/**
	 * Creates a new stack
	 * 
	 * @param initialCapacity the capacity of this stack.
	 * @param growthFactor the factor in % for increasing the capacity 
	 * 								  when there's not enough room in this list anymore 
	 */
	public BooleanStack(int initialCapacity, int growthFactor) {
		this.storedObjects = new boolean[ initialCapacity ];
		this.growthFactor = growthFactor;
	}
	
	/**
	 * Pushes an element on the stack.
	 * 
	 * @param element the element
	 */
	public void push( boolean element ) {
		if (this.size >= this.storedObjects.length) {
			increaseCapacity();
		}
		this.storedObjects[ this.size ] = element;
		this.size++;
	}
	
	/**
	 * Returns the element that has been added last without removing it.
	 * 
	 * @return the last element
	 */
	public boolean peek() {
		if(this.size == 0) {
			return false;
		} else {
			return this.storedObjects[ this.size - 1 ];
		}
	}

	/**
	 * Returns the element that has been added last and removes it at the same time.
	 * 
	 * @return the last element
	 */
	public boolean pop() {
		this.size--;
		return this.storedObjects[ this.size ];
	}
	
	/**
	 * Determines whether this stack is empty
	 * 
	 * @return true when it is empty
	 */
	public boolean empty() {
		return this.size == 0;
	}
	
	/**
	 * Retrieves the current size of this stack.
	 *  
	 * @return the number of stored booleans in this stack.
	 */
	public int size() {
		return this.size;
	}
	
	/**
	 * Returns the 1-based position where an object is on this stack. 
	 * If the element occurs as an item in this stack, this method returns the distance from the top of the stack of the occurrence nearest the top of the stack; the topmost item on the stack is considered to be at distance 1.
	 * 
	 * @param element the boolean which might be on this stack
	 * @return the 1-based position or -1 if the element is not found.
	 */
	public int search( boolean element ) {
		boolean[] array = getInternalArray();
		int index = this.size - 1;
		int position = 1;
		while (index >= 0) {
			if ( array[index] == element) {
				return position;
			}
			index--;
			position++;
		}
		return -1;
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
		boolean[] newStore = new boolean[ newCapacity ];
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
	public boolean[] getInternalArray() {
		return this.storedObjects;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int storeSize = in.readInt();
		int growFactor = in.readInt();
		boolean[] store = new boolean[ storeSize ];
		for (int i = 0; i < store.length; i++) {
			store[i] = in.readBoolean();
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
			boolean o = this.storedObjects[i];
			out.writeBoolean( o );
		}
	}
}
