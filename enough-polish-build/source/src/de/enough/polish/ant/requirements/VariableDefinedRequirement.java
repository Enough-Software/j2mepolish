/*
 * Created on 07-March-2006 at 14:40:12.
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
 * <p>Selects a device if a required variable is defined.</p>
 *
 * <p>Copyright Enough Software 2004, 2005, 2006</p>

 * <pre>
 * history
 *        07-March-2006  - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VariableDefinedRequirement extends Requirement {
	
	private String[] requiredVariables;

	/**
	 * Creates a new requirement for a device variable.
	 * 
	 * @param value the variables(s) which needs to be defined, e.g. "wap.UserAgen".
	 *              When there are several variables needed, they need to be seperated by commas.
	 */
	public VariableDefinedRequirement(String value ) {
		super(value, "Capabilities");
		this.requiredVariables = StringUtil.splitAndTrim( value, ',');
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		// this is not needed, since we overried isMet(Device ) already.
		return false;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device)
	 */
	public boolean isMet(Device device) {
		for (int i = 0; i < this.requiredVariables.length; i++) {
			String var = this.requiredVariables[i];
			if (device.getCapability(var) == null) {
				return false;
			}
		}
		return true;
	}

}
