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
import java.util.Hashtable;


/**
 * This class implements a JSON object, according to the JSON specifications.
 * @author Ovidiu Iliescu
 *
 */
public class JsonObject implements JsonItem {

	/**
	 * The object members are stored here
	 */
	Hashtable members = new Hashtable();
	
	/**
	 * Adds a member to the object
	 * @param key the member name
	 * @param value the member's value.
	 */
	public void put(String key, Object value) {
		this.members.put(key, value);
	}
	
	/**
	 * Retrieves a member by name/key
	 * @param key the name of the member
	 * @return its value
	 */
	public Object get(String key) {
		return this.members.get(key);
	}
	
	/**
	 * Removes a given member by name/key
	 * @param key the name/key of the member
	 */
	public void remove(String key) {
		this.members.remove(key);
	}
	
	/**
	 * Retrieves all member values as an Enumeration
	 * @return the member values
	 */
	public Enumeration getValues() {
		return this.members.elements();
	}
	
	/**
	 * Retrieves all the member names/keys, as an enumeration
	 * @return the member names/keys
	 */
	public Enumeration getKeys() {
		return this.members.keys();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.json.JSONItem#serializeToStringBuffer(java.lang.StringBuffer)
	 */
	public void serializeToStringBuffer(StringBuffer stringBuffer) {
		Enumeration values = getValues();
		Enumeration keys = getKeys();
		
		stringBuffer.append(JsonParser.TOKEN_BEGIN_OBJECT);
		
		String currentKey;
		Object currentValue;
		while (keys.hasMoreElements()) {
			currentKey = (String) keys.nextElement();
			currentValue = values.nextElement();
			
			JsonUtil.toJsonValue(currentKey, stringBuffer);
			
			stringBuffer.append(JsonParser.TOKEN_NAME_SEPARATOR);
			
			if ( currentValue instanceof JsonItem) {
				 ( (JsonItem) currentValue ).serializeToStringBuffer(stringBuffer);
			} else {
				JsonUtil.toJsonValue(currentValue, stringBuffer);
			}					
			
			if ( keys.hasMoreElements() ) {
				stringBuffer.append(JsonParser.TOKEN_VALUE_SEPARATOR);
			}
		}
		
		stringBuffer.append(JsonParser.TOKEN_END_OBJECT);
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
