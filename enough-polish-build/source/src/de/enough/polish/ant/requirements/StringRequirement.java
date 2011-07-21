/*
 * Created on 10-Feb-2004 at 22:50:02.
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
 * <p>Selects devices by a specific string within the given capability.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class StringRequirement extends Requirement {

	private String[] features;
	private boolean or;
	
	/**
	 * Creates a new requirement for a specific string.
	 * @param value the string which needs to be defined within the capability
	 * @param propertyName the name of the capability
	 */
	public StringRequirement(String value, String propertyName) {
		this( value, propertyName, false );
	}
	
	/**
	 * Creates a new requirement for a specific string.
	 * @param value the string which needs to be defined within the capability
	 * @param propertyName the name of the capability
	 * @param or true when only one of the given elements needs to be found,
	 * 			 otherwiese all elements need to match.
	 */
	public StringRequirement(String value, String propertyName, boolean or ) {
		super(value, propertyName);
		this.or = or;
		String[] neededFeatures = StringUtil.split( value, ',');
		this.features = new String[ neededFeatures.length ];
		for (int i = 0; i < neededFeatures.length; i++) {
			this.features[i] = "polish." + propertyName + "." + neededFeatures[i];
		}
	}
	
	public boolean isMet( Device device ) {
		for (int i = 0; i < this.features.length; i++) {
			if (device.hasFeature( this.features[i])) {
				if (this.or) {
					return true;
				}
			} else if (!this.or) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		// not needed since isMet( Device ) is used instead
		return false;
	}
	
	
}
