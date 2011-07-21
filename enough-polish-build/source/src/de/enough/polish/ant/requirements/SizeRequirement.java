/*
 * Created on 10-Feb-2004 at 22:38:46.
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

import de.enough.polish.Device;
import de.enough.polish.util.StringUtil;

import de.enough.polish.BuildException;

/**
 * <p>Selects devices by the size of specific features.</p>
 * <p>The size-based capability needs to be defined as "11x22" etc.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class SizeRequirement extends Requirement {

	protected IntegerMatcher widthMatcher;
	protected IntegerMatcher  heightMatcher;
	
	/**
	 * Creates a new Size requirement.
	 * 
	 * @param value the needed size 
	 * @param propertyName the name of the capability
	 */
	public SizeRequirement(String value, String propertyName) {
		super(value, propertyName);
		String[] values = StringUtil.split( value, 'x');
		if (values.length != 2) {
			throw new BuildException( "The value of the requirement [" + this.propertyName + "] is not valid, it needs to be in the form \"[width] x [height]\".");
		}
		this.widthMatcher = new IntegerMatcher( values[0] );
		this.heightMatcher = new IntegerMatcher( values[1] );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		String[] parts = StringUtil.split( property, 'x' );
		if (parts.length != 2) {
			throw new BuildException("The property [" + this.propertyName + "] of the device [" + device.getIdentifier() + "] is not valid. It meeds to be in the form \"[width] x [height]\".");
		}
		return this.widthMatcher.matches( parts[0] )
				&& this.heightMatcher.matches( parts[1] );
	}

}
