/*
 * Created on 11-Feb-2004 at 20:27:56.
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
 * <p>Selects a device by a memory capability.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class MemoryRequirement extends Requirement {

	private MemoryMatcher matcher;

	/**
	 * Creates a new memory requirement.
	 * 
	 * @param value the needed memory, e.g. "120+ kb"
	 * @param propertyName the name of the memory-capability, e.g. "HeapSize"
	 */
	public MemoryRequirement(String value, String propertyName) {
		super(value, propertyName);
		if (this.needsToBeUndefined) {
			return;
		}
		try {
			this.matcher = new MemoryMatcher( value );
		} catch (NumberFormatException e) {
			throw new BuildException("Unable to create memory-requirement for value [" + value + "]: " + e );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		try {
		return this.matcher.matches( property );
		} catch (BuildException e) {
			throw new BuildException("Unable to compare memory with device \"" + device.getIdentifier() + "\": " + e.toString() );
		}
	}

}
