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

/**
 * This interface defines methods common to all JSON-specific items (arrays, objects, numbers)
 * @author Ovidiu Iliescu
 *
 */
public interface JsonItem {

	/**
	 * Serializes the item to a StringBuffer, in a JSON-compatible format.
	 * @param stringBuffer the StringBuffer to serialize to
	 */
	void serializeToStringBuffer(StringBuffer stringBuffer) ;
	
	/**
	 * Serializes the item to a String, in a JSON-compatible format.
	 * @return the resulting String
	 */
	String serializeToString() ;
}
