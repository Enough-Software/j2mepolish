/*
 * Created on 24-Feb-2005 at 12:37:15.
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
package de.enough.polish.ant.requirements;

import de.enough.polish.BuildException;

import de.enough.polish.util.StringUtil;

/**
 * <p>Matches the two dimensional size of a property.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        24-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SizeMatcher implements Matcher {
	
	protected IntegerMatcher widthMatcher;
	protected IntegerMatcher  heightMatcher;
	
	public SizeMatcher( String requirement ) {
		String[] values = StringUtil.split( requirement, 'x');
		if (values.length != 2) {
			throw new BuildException( "The requirement-value  [" + requirement + "] is not valid, it needs to be in the form \"[width] x [height]\".");
		}
		this.widthMatcher = new IntegerMatcher( values[0] );
		this.heightMatcher = new IntegerMatcher( values[1] );

	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Matcher#matches(java.lang.String)
	 */
	public boolean matches(String deviceValue) {
		String[] parts = StringUtil.split( deviceValue, 'x' );
		if (parts.length != 2) {
			throw new BuildException("The device-property [" + deviceValue + "]  is not valid. It meeds to be in the form \"[width] x [height]\".");
		}
		return this.widthMatcher.matches( parts[0] ) && this.heightMatcher.matches( parts[1] );
	}
}
