/*
 * Created on Jan 29, 2009 at 2:48:52 PM.
 * 
 * Copyright (c) 2009 Robert Virkus / Enough Software
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
package de.enough.polish.propertyfunctions;

import de.enough.polish.Environment;
import de.enough.polish.util.StringUtil;

/**
 * <p>Coverts a version like "2.1" or "MIDP/2.1.0" into a number, e.g. 2001000 for both values.</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VersionFunction extends PropertyFunction
{

	/**
	 * Creates a new function
	 */
	public VersionFunction()
	{
		// creates a new function
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments, Environment env)
	{
		String versionIdentifier = null;
		if (arguments != null && arguments.length > 0) {
			versionIdentifier = arguments[0];
		}
		return doProcess(input, versionIdentifier);
	}

	/**
	 * Processes a simple version like 3.2.1
	 * @param version the version
	 * @return the processed version, e.g. 3200100
	 */
	public static String process(String version) {
		return doProcess(version, null);
	}
	
	/**
	 * Processes a simple version like 'MIDP/2.0, BlackBerry/4.7'
	 * @param version the version
	 * @param versionIdentifier the version identifier, e.g. BlackBerry
	 * @return the processed version, e.g. 4700000
	 */
	public static String process(String version, String versionIdentifier) {
		return doProcess(version, versionIdentifier);
	}


	private static String doProcess(String input, String versionIdentifier) {
		if (versionIdentifier != null) {
			versionIdentifier = versionIdentifier.toLowerCase().trim();
			String[] inputs = StringUtil.splitAndTrim(input, ',');
			boolean found = false;
			for (int i = 0; i < inputs.length; i++) {
				String value = inputs[i];
				if (value.toLowerCase().startsWith(versionIdentifier)) {
					found = true;
					input = value;
					break;
				}
			}
			if (!found) {
				return "-1";
			}
		}
		StringBuffer version = new StringBuffer();
		StringBuffer chunk = new StringBuffer();
		int hierarchy = 0;
		for (int i=0; i<input.length(); i++) {
			char c = input.charAt(i);
			if (Character.isDigit(c)) {
				chunk.append(c);
			} else if (c == '.') {
				for (int j=0; j < (3 - chunk.length()); j++ ) {
					version.append('0');
				}
				version.append(chunk);
				chunk.delete(0, chunk.length());
				hierarchy++;
				if (hierarchy > 2) {
					throw new IllegalArgumentException("The version function requires a version as input, e.g. 2.1 or 3.2.999 - the provided version \"" + input + "\" is not valid: contains too many version chunks.");
				}
			}
		}
		for (int j=0; j < (3 - chunk.length()); j++ ) {
			version.append('0');
		}
		version.append(chunk);
		for (int j=hierarchy; j<2; j++) {
			version.append("000");
		}
		return version.toString();
	}
	
}
