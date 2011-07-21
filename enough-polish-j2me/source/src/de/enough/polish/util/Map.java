/*
 * Created on 01-Dec-2005 at 18:30:14.
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
 * <p>A common interface for all map implementations. This allows to check out different implementations without changing the code much.</p>
 * <p>
 *    When the Map interface is not used, you can remove it from the generated JAR by setting
 *    the preprocessing variable "polish.Map.dropInterface" to "true". In that case the
 *    interface will not be implemented by HashMap, ReferenceHashMap and so on. The obfuscator
 *    is then able to remove the interface definition from the JAR.
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        01-Dec-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @param <K> when you use the enough-polish-client-java5.jar you can parameterize the Map, e.g. Map&lt;User, Message&gt; = new HashMap&lt;User, Message&gt;(10); 
 * @param <V> when you use the enough-polish-client-java5.jar you can parameterize the Map, e.g. Map&lt;User, Message&gt; = new HashMap&lt;User, Message&gt;(10); 
 */
public interface Map
//#if polish.java5
	<K, V>
//#endif

{

	/**
	 * Adds a key-value pair to this map.
	 * 
	 * @param key the key
	 * @param value the value
	 * @return the value that has been stored previously for the given key, or null.
	 */
	//#if polish.java5
	Object put(K key, V value);
	//#else
		//# Object put(Object key, Object value);
	//#endif
	

	/**
	 * Gets the value that has been stored for the specified key.
	 *  
	 * @param key the key
	 * @return the value or null, when for the given key no value has been stored.
	 */
	//#if polish.java5
		V get(K key);
	//#else
		//# Object get(Object key);
	//#endif
	

	/**
	 * Removes the key-value pair from this map.
	 *  
	 * @param key the key
	 * @return the value or null, when for the given key no value has been stored.
	 */
	//#if polish.java5
		V remove(K key);
	//#else
		//# Object remove(Object key);
	//#endif
	

	/**
	 * Determines whether this map is empty.
	 * This is equivalent to calling map.getSize() == 0.
	 * 
	 * @return true when this map is empty.
	 */
	boolean isEmpty();

	/**
	 * The size of this map.
	 * 
	 * @return the size of this map, 0 when no entries have been added yet.
	 */
	int size();

	/**
	 * Checks if a value has been stored in this map.
	 * This is equivalent to calling map.get( key ) != null.
	 * 
	 * @param key the key
	 * @return true when a value has been stored in this map to the specified key.
	 */
	//#if polish.java5
		boolean containsKey(K key);
	//#else
		//# boolean containsKey(Object key);
	//#endif
	

	/**
	 * Checks the given value has been stored in this map.
	 * This is quite an expensive operation, since all elements need to be checked in the worst case.
	 * 
	 * @param value the value
	 * @return true when the value has been stored in this map.
	 */
	//#if polish.java5
		boolean containsValue(V value);
	//#else
		//# boolean containsValue(Object value);
	//#endif
	

	/**
	 * Removes all elements from this map.
	 */
	void clear();

	/**
	 * Retrieves all values that have been stored in this map.
	 * 
	 * @return an object array with all values.
	 */
	Object[] values();

	/**
	 * Retrieves all values that have been stored in this map.
	 * 
	 * @param objects the typed array in which the elements are stored
	 * @return an object array with all values.
	 */
	//#if polish.java5
		V[] values(V[] objects);
	//#else
		//# Object[] values(Object[] objects);
	//#endif
	

	/**
	 * Retrieves all keys that have been stored in this map.
	 * 
	 * @return an object array with all keys.
	 */
	Object[] keys();

	/**
	 * Retrieves all keys that have been stored in this map.
	 * 
	 * @param objects the typed array in which the keys are stored
	 * @return an object array with all keys.
	 */
	//#if polish.java5
		K[] keys(K[] objects);
	//#else
		//# Object[] keys(Object[] objects);
	//#endif
	
	
	/**
	 * Iterates over the keys of this map.
	 * In contrast to using the keys() method no additional array is created.
	 * When the map is modified while the iterator is being used, the behavior
	 * of the iterator is unspecified (unless the iterator.remove() method is used).
	 * 
	 * @return an iterator that contains all keys.
	 */
	Iterator keysIterator();

}
