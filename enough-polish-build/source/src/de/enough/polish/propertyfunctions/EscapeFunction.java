/*
 * Created on 07-Nov-2005 at 02:12:59.
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

import de.enough.polish.Environment;

/**
 * <p>Escapes the given text for inclusion of the characters within Java source code.</p>
 * <p>Sample usage:
 * <pre>
 * //#= String symbols = "${ escape( config.Symbols )}"; 
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *         30-May-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EscapeFunction extends PropertyFunction {

	/**
	 * Creates a new escape function
	 */
	public EscapeFunction() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments, Environment env) {
		StringBuffer buffer = new StringBuffer( input.length() * 2 );
		char[] characters = input.toCharArray();
		for (int i = 0; i < characters.length; i++) {
			char c = characters[i];
			switch (c) {
			case '\\' : 
				buffer.append("\\\\");
				break;
			case '"':
				buffer.append("\\\"");
				break;
			default:
				buffer.append( c );
			}
		}
		return buffer.toString();
	}

}
