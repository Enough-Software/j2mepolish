/*
 * Created on Jan 25, 2007 at 3:19:22 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.license;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.enough.license.LicenseChecker;
import de.enough.polish.LicenseLoader;

/**
 * <p>Loads and verified a license.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 25, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishLicenseLoader implements LicenseLoader {

	/* (non-Javadoc)
	 * @see de.enough.polish.LicenseLoader#verifyLicense(java.io.File)
	 */
	public Map verifyLicense(File licenseFile) {
		if (!licenseFile.exists()) {
			return null;
		}
		HashMap properties = new HashMap();
		try {
			boolean isValid = LicenseChecker.verify(licenseFile, properties);
			if (!isValid) {
				throw new SecurityException("Invalid license: " + licenseFile.getAbsolutePath() );
			}
			if (properties.get("license.name") == null 
				|| properties.get("licensee.name") == null 
				|| properties.get("license.name") == null  )
			{
				throw new SecurityException("Invalid License: information missing.");
			}
			return properties;
		} catch (IOException e) {
			throw new SecurityException( e.toString() );
		}
	}

}
