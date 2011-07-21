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

/**
 * <p>Provides a lifo stack for primitive int types</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class IntStack extends IntList {

	/**
	 * Creates a new stack with the initial capacity of 10 and a growth factor of 75%
	 */
	public IntStack() {
		super();
	}

	/**
	 * Creates a new stack with the given initial capacity and a growth factor of 75%
	 * 
	 * @param initialCapacity the capacity of this stack.
	 */
	public IntStack(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new stack
	 * 
	 * @param initialCapacity the capacity of this stack.
	 * @param growthFactor the factor in % for increasing the capacity 
	 * 								  when there's not enough room in this list anymore 
	 */
	public IntStack(int initialCapacity, int growthFactor) {
		super(initialCapacity, growthFactor);
	}
	
	/**
	 * Pushes an element on the stack.
	 * 
	 * @param element the element
	 */
	public void push( int element ) {
		add( element );
	}
	
	/**
	 * Returns the element that has been added last without removing it.
	 * 
	 * @return the last element
	 */
	public int peek() {
		if(size() == 0)
			return -1;
		else
			return get( size() - 1 );
	}

	/**
	 * Returns the element that has been added last and removes it at the same time.
	 * 
	 * @return the last element
	 */
	public int pop() {
		return removeElementAt( size() - 1 );
	}
	
	/**
	 * Determines whether this stack is empty
	 * 
	 * @return true when it is empty
	 */
	public boolean empty() {
		return size() == 0;
	}
	
	/**
	 * Returns the 1-based position where an object is on this stack. 
	 * If the element occurs as an item in this stack, this method returns the distance from the top of the stack of the occurrence nearest the top of the stack; the topmost item on the stack is considered to be at distance 1.
	 * 
	 * @param element the element which might be on this stack
	 * @return the 1-based position or -1 if the element is not found.
	 */
	public int search( int element ) {
		int[] array = getInternalArray();
		int index = size() - 1;
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

}
