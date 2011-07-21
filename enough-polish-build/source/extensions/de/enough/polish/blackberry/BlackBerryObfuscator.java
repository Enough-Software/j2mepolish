/*
 * Created on Jul 14, 2010 at 1:18:09 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.blackberry;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.obfuscate.ProGuardObfuscator;
import de.enough.polish.util.OrderedMultipleEntriesMap;

/**
 * <p>Configures ProGuard for BlackBerry optimized obfuscation</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BlackBerryObfuscator extends ProGuardObfuscator {

	/**
	 * Creates a new BB obfuscator
	 */
	public BlackBerryObfuscator() {
		// use adaptParameters() for configuration
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.ProGuardObfuscator#adaptParameters(de.enough.polish.util.OrderedMultipleEntriesMap, de.enough.polish.Device, de.enough.polish.Environment)
	 */
	protected void adaptParameters(OrderedMultipleEntriesMap params,
			Device device, Environment env) {
		params.put( "-classobfuscationdictionary", env.resolveVariable("${polish.home}/bbclassdictionary.txt"));
		params.put( "-dontnote", "" );
		params.put( "-dontusemixedcaseclassnames", "");
		params.put( "-optimizationpasses", "3" );
	}
	
	
}
