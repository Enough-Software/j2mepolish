/*
 * Created on 19-Aug-2004 at 16:51:02.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Creates and stores integer-IDs instead of full string-sequences.</p>
 * <p>
 * This saves some memory and processing time.
 * </p> 
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Aug-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class IntegerIdGenerator {
	
	private int lastId;
	private Map idsByKey;
	private boolean hasChanged;

	/**
	 * Creates a new integer ID generator.
	 * 
	 */
	public IntegerIdGenerator() {
		super();
		this.idsByKey = new HashMap();
	}

	/**
	 * Creates a new generator.
	 * 
	 * @param idsMap the known IDs
	 */
	public IntegerIdGenerator( Map idsMap ) {
		setIdsMap(idsMap);
	}
	
	/**
	 * Retrieves the map containing all abbreviations.
	 *  
	 * @return the map containing all abbreviations.
	 */
	public Map getIdsMap() {
		return this.idsByKey;
	}
	
	/**
	 * Sets the IDs for this generator.
	 * 
	 * @param map a HashMap containing all IDs for full keywords 
	 */
	public void setIdsMap( Map map ) {
		this.hasChanged = false;
		this.idsByKey = new HashMap( map.size() );
		// find out the last used ID:
		int maxId = 0;
		Object[] keys = map.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String key = (String) keys[i];
			Object value = map.get( key );
			Integer integer;
			if (value instanceof Integer) {
				integer = (Integer) value;
			} else {
				integer = new Integer( Integer.parseInt( value.toString()));
			}
			this.idsByKey.put( key, integer );
			if (integer.intValue() > maxId) {
				maxId = integer.intValue();
			}
		}
		this.lastId = maxId;
	}
	
	
	/**
	 * Retrieves the ID for the given key.
	 * 
	 * @param key the key
	 * @param create true when a new ID should be generated when this key is not yet registered
	 * @return the ID for the given key, or -1 when the key is not registered
	 */
	public int getId( String key, boolean create ) {
		Integer id = (Integer) this.idsByKey.get( key );
		if (id == null) {
			if (create) {
				this.hasChanged = true;
				this.lastId++;
				//System.out.println("]]]>Creating new ID for key [" + key + "]: " + this.lastId );
				Integer newId = new Integer( this.lastId );
				this.idsByKey.put( key, newId );
				return this.lastId;
			} else {
				return -1;
			}
		} else {
			return id.intValue();
		}
	}

	/**
	 * Adds a fix ID for a key to this generator.
	 * 
	 * @param key the key
	 * @param id the ID
	 */
	public void addId(String key, int id) {
		this.idsByKey.put( key, new Integer( id ) );
		this.lastId = Math.max( this.lastId, id );
	}

	/**
	 * Determines whether any IDs have been created.
	 * 
	 * @return true when at least one new ID has been created by this generator
	 */
	public boolean hasChanged() {
		return this.hasChanged;
	}

	/**
	 * Retrieves an key for the given ID
	 * @param id the ID 
	 * @return the corresponding key, null when none is known
	 */
	public String getKey(int id)
	{
		Object[] keys = this.idsByKey.keySet().toArray();
		for (int i = 0; i < keys.length; i++)
		{
			Object key = keys[i];
			Integer value = (Integer) this.idsByKey.get(key);
			if (value.intValue() == id) {
				return (String) key;
			}
		}
		return null;
	}
}
