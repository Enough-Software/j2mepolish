/*
 * Created on 10-Feb-2004 at 22:45:41.
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

import de.enough.polish.BuildException;

import de.enough.polish.Device;

/**
 * <p>Selects devices by matching simple numbers, e.g. "4" matches "2+".</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class IntRequirement extends Requirement {

	private IntegerMatcher matcher;
	
	/**
	 * Creates a new integer requirement.
	 * 
	 * @param value the value
	 * @param propertyName the name of the property
	 */
	public IntRequirement(String value, String propertyName) {
		super(value, propertyName);
		if (this.needsToBeUndefined) {
			return;
		}
		try {
			this.matcher = new IntegerMatcher( value );
		} catch (NumberFormatException e) {
			throw new BuildException("Unable to create int-requirement for value [" + value + "]: " + e );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		return this.matcher.matches( property );
	}

}
