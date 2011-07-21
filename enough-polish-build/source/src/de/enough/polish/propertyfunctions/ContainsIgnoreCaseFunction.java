/*
 * Created on 14-Dec-2005 at 18:39:54.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.propertyfunctions;


/**
 * <p>Checks if the input contains a defined value.</p>
 * <p>Example:
 * <pre>
 * //#if ${ containsIgnoreCase( WINDOWS, polish.OS ) }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        4-April-2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ContainsIgnoreCaseFunction extends ContainsFunction {
	

	/**
	 * Create a new function
	 */
	public ContainsIgnoreCaseFunction() {
		super();
	}


	/**
	 * Determines whether the value contains the search string
	 *  
	 * @param search the string that should be contained within the value
	 * @param value the value
	 * @return true when the string is contained
	 */
	protected boolean contains(String search, String value) {
		return super.contains(search, value) || value.toLowerCase().indexOf(search.toLowerCase()) != -1;
	}

}
