/*
 * Created on 12-Jan-2006 at 12:18:43.
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
 * <p>Is used to traverse through maps and lists.</p>
 * <p>The iterator passes through the original internal array
 *    of the parent map/list, so no memory overhead occurs.
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        12-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface Iterator
//#if polish.java5
<K>
//#endif
{
	
	/**
	 * Determines whether there is a following object in this iterator.
	 * 
	 * @return true if the iteration has more elements. (In other words, returns true if next would return an element rather than throwing an exception.)
	 */
	boolean hasNext();
	
	/**
	 * Returns the next element in the iteration.
	 * 
	 * @return the next element in the iteration.
	 * @throws IllegalStateException when all elements have been iterated through (on Non-MIDP platforms just a RuntimeException is raised).
	 */
	//#if polish.java5
	K next();
  //#else
  //# Object next();
	//#endif
	
	/**
	 * Removes from the underlying collection the last element returned by the iterator (optional operation). This method can be called only once per call to next. The behavior of an iterator is unspecified if the underlying collection is modified while the iteration is in progress in any way other than by calling this method.
	 * 
	 * @throws IllegalStateException when the map/list does not support this operation (on Non-MIDP platforms just a RuntimeException is raised).
	 */
	void remove();

}
