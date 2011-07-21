/*
 * Copyright (c) 2011 Robert Virkus / Enough Software
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
package de.enough.polish.json;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class implements a JSON array, according to the JSON specifications.
 * @author Ovidiu Iliescu
 *
 */
public class JsonArray implements JsonItem {

	/**
	 * The elements of the array
	 */
	protected Vector elements = new Vector();
	
	/**
	 * Get the total count of elements in the array
	 * @return the number of elements in the array
	 */
	public int getCount() {
		return this.elements.size();
	}
	
	/**
	 * Adds an element to the array
	 * @param object the element to add
	 */
	public void put(Object object) {
		this.elements.addElement(object);
	}
	
	/**
	 * Removes an element from the array
	 * @param object the element to remove
	 */
	public void remove(Object object) {
		this.elements.removeElement(object);
	}
	
	/**
	 * Retrieves the element of the array with the given index
	 * @param index the desired index
	 * @return the corresponding element
	 */
	public Object get(int index) {
		return this.elements.elementAt(index);
	}
	
	/**
	 * Retrieves all the elements in the array as an Enumeration
	 * @return an Enumeration containing all the elements
	 */
	public Enumeration getAll() {
		return this.elements.elements();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.json.JSONItem#serializeToStringBuffer(java.lang.StringBuffer)
	 */
	public void serializeToStringBuffer(StringBuffer stringBuffer) {
		Enumeration tmpWlements = getAll();
		
		stringBuffer.append(JsonParser.TOKEN_BEGIN_ARRAY);
		
		Object currentElement;
		while (tmpWlements.hasMoreElements()) {
			currentElement = tmpWlements.nextElement();
			
			if ( currentElement instanceof JsonItem) {
				 ( (JsonItem) currentElement ).serializeToStringBuffer(stringBuffer);
			} else {
				JsonUtil.toJsonValue(currentElement, stringBuffer);
			}
			
			if ( tmpWlements.hasMoreElements() ) {
				stringBuffer.append(JsonParser.TOKEN_VALUE_SEPARATOR);
			}
		}
		stringBuffer.append(JsonParser.TOKEN_END_ARRAY);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.json.JSONItem#serializeToString()
	 */
	public String serializeToString() {
		StringBuffer buffer = new StringBuffer();
		serializeToStringBuffer(buffer);
		return buffer.toString();
	}	
	
}
