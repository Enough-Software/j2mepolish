/*
 * Created on 24-Jan-2004 at 11:52:41.
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
 * <p>Provides help for comparing integers, e.g. 150 with "40+"</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class IntegerMatcher 
implements Matcher 
{

	protected int number;
	protected boolean equalsOrGreater;

	/**
	 * Creates a new integer matcher.
	 * 
	 * @param value the requirement, e.g. "20" or "20+"
	 * @throws NumberFormatException when no integer number is given, e.g. "j5+"
	 */
	public IntegerMatcher( String value ) {
		value = value.trim();
		if (value.endsWith("+")) {
			this.equalsOrGreater = true;
			value = value.substring(0, value.length() -1).trim();
		}
		this.number = Integer.parseInt( value );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.Matcher#matches(java.lang.String)
	 */
	public boolean matches(String deviceValue) {
		int value = Integer.parseInt( deviceValue );
		if (this.equalsOrGreater) {
			return value >= this.number;
		} else {
			return value == this.number;
		}
	}

}
