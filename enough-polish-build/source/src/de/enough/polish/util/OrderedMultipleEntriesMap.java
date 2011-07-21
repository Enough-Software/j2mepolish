/*
 * Created on May 22, 2008 at 2:34:08 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
 * <p>Provides a map that can contain several keys which are the same</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class OrderedMultipleEntriesMap
{
	private final java.util.ArrayList keys;
	private final java.util.ArrayList values;
	
	public OrderedMultipleEntriesMap() {
		this.keys = new java.util.ArrayList();
		this.values = new java.util.ArrayList();
	}
	
	public void add( Object key, Object value) {
		this.keys.add(key);
		this.values.add(value);
	}
	
	public void put( Object key, Object value) {
		add( key, value );
	}
	

	public int size() {
		return this.keys.size();
	}
	
	public Object getKey( int i ) {
		return this.keys.get(i);
	}
	
	public Object getValue(int i) {
		return this.values.get(i);
	}
	
	
}
