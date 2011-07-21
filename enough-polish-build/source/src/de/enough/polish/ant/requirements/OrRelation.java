/*
 * Created on 15-Feb-2004 at 20:01:23.
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

/**
 * <p>Contains a list of requirements of which at least one requirement must be met by the device.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        15-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class OrRelation extends RequirementContainer {
	
	/**
	 * Creates a new empty or relation.
	 */
	public OrRelation() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.DeviceFilter#isMet(de.enough.polish.Device)
	 */
	public boolean isMet(Device device) {
		DeviceFilter[] filters = getFilters();
		for (int i = 0; i < filters.length; i++) {
			DeviceFilter filter = filters[i];
			if (filter.isMet(device)) {
				return true;
			}
		}
		return false;
	}
}
