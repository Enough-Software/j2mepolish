/*
 * Created on 24-Jan-2004 at 18:30:12.
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

/**
 * <p>Selects a device by its identifier.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class IdentifierRequirement extends Requirement {

	private final String[] identifiers;

	/**
	 * Creates a new identifier requirement.
	 * 
	 * @param value the allowed identifiers seperated by commas.
	 */
	public IdentifierRequirement(String value) {
		super(value, "Identifier");
		this.identifiers = StringUtil.splitAndTrim(value, ',');
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		String deviceIdentifier = device.getIdentifier();
		for (int i = 0; i < this.identifiers.length; i++) {
			String identifier = this.identifiers[i];
			if (identifier.equalsIgnoreCase( deviceIdentifier)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves all desired identifiers.
	 * 
	 * @return all desired identifiers
	 */
	public String[] getIdentifers() {
		return this.identifiers;
	}
	
}
