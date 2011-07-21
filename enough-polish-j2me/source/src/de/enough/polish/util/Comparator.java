/*
 * Created on 05.01.2006 at 15:40:34.
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
 * <p>Allows to compare two objects</p>
 *
 * @param <K> when you use the enough-polish-client-java5.jar you can parameterize the Comparator, e.g. Comparator&lt;Integer&gt; = new Comparator&lt;Integer&gt;() { public int compare( Integer i1, Integer i2 ) { return i1.intValue() - i2.intValue(); } ); 
 * <p>Copyright Enough Software 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface Comparator 
//#if polish.java5
<K>
//#endif
{

	/**
	 * Compares two objects.
	 * In the foregoing description, the notation sgn(expression) designates the mathematical signum function, which is defined to return one of -1, 0, or 1 according to whether the value of expression is negative, zero or positive.
	 * The implementor must ensure that sgn(compare(x, y)) == -sgn(compare(y, x)) for all x and y. (This implies that compare(x, y) must throw an exception if and only if compare(y, x) throws an exception.)
	 * The implementor must also ensure that the relation is transitive: ((compare(x, y)>0) && (compare(y, z)>0)) implies compare(x, z)>0.
	 * Finally, the implementor must ensure that compare(x, y)==0 implies that sgn(compare(x, z))==sgn(compare(y, z)) for all z.
	 * It is generally the case, but not strictly required that (compare(x, y)==0) == (x.equals(y)). Generally speaking, any comparator that violates this condition should clearly indicate this fact. The recommended language is "Note: this comparator imposes orderings that are inconsistent with equals." 
	 * 
	 * @param o1 the first object
	 * @param o2 the object that is compared with o1
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second. 
	 * @throws ClassCastException if the arguments' types prevent them from being compared by this comparator.
	 */
	int compare(
			//#if polish.java5
				K o1, K o2
			//#else
				//# Object o1, Object o2
			//#endif
	);
}
