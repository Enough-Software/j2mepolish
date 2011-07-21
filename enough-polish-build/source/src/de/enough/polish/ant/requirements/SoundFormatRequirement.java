/*
 * Created on 28-Aug-2004 at 21:14:24.
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
 * <p>Selects a device by the supported audio formats.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class SoundFormatRequirement extends Requirement {
	
	private String[] neededFormats;

	/**
	 * Creates a new requirement for the java platform of a device.
	 * 
	 * @param value The value of the platform
	 */
	public SoundFormatRequirement(String value ) {
		super(value, "SoundFormat" );
		this.neededFormats = StringUtil.splitAndTrim( value , ',' );
		for (int i = 0; i < this.neededFormats.length; i++) {
			this.neededFormats[i] = "polish.audio." + this.neededFormats[i];
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		for (int i = 0; i < this.neededFormats.length; i++) {
			String format = this.neededFormats[i];
			if ( ! device.hasFeature( format )) {
				return false;
			}
		}
		return true;
	}
}
