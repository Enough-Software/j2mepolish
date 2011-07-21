/*
 * Created on 24-Jan-2004 at 17:58:55.
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
package de.enough.polish.ant.requirements;

/**
 * <p>Matches several string-values.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class StringMatcher implements Matcher {

	private String[] elements;
	private boolean or;

	/**
	 * Creates a new string matcher.
	 * 
	 * @param element the element which needs to be found in the device value.
	 */
	public StringMatcher(String element ) {
		this( new String[]{ element }, true );
	}
	
	/**
	 * Creates a new string matcher.
	 * 
	 * @param elements array of elements which needs to be found in the device value.
	 * @param or true when only one of the given elements needs to be found,
	 * 			 otherwiese all elements need to match.
	 */
	public StringMatcher(String[] elements, boolean or ) {
		for (int i = 0; i < elements.length; i++) {
			elements[i] = elements[i].trim().toLowerCase();
		}
		this.elements = elements;
		this.or = or;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Matcher#matches(java.lang.String)
	 */
	public boolean matches(String deviceValue) {
		deviceValue = deviceValue.toLowerCase();
		boolean found = false;
		for (int i = 0; i < this.elements.length; i++) {
			String element = this.elements[i];
			if (deviceValue.indexOf( element ) != -1) {
				if (this.or) {
					return true;
				} else {
					found = true;
				}
			} else if (! this.or) {
				return false;
			}
		}
		return found;
	}

}
