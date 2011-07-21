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

import java.util.Locale;

import de.enough.polish.Environment;
import de.enough.polish.util.StringUtil;

/**
 * <p>Gets a locale like "fr" or "en_US" and returns the localized country name for this.</p>
 * <p>This function makes only sense, when dynamic localization is used.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        14-Dec-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DisplayCountryFunction extends PropertyFunction {

	/**
	 * Create a new function
	 */
	public DisplayCountryFunction() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments, Environment env) {
		input = input.replace( '-', '_' );
		String[] elements = StringUtil.split( input, '_' );
		Locale locale;
		if (elements.length == 3) {
			locale = new Locale( elements[0], elements[1], elements[2] );
		} else if (elements.length == 2) {
			locale = new Locale( elements[0], elements[1] );
		} else {
			locale = new Locale( elements[0] );
		}
		return locale.getDisplayCountry(locale);
	}

}
