/*
 * Created on 11-Feb-2004 at 23:19:52.
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

import de.enough.polish.util.StringUtil;

/**
 * <p>Checks capabilities for a specific version.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VersionMatcher implements Matcher {
	
	private int[] versionNumbers;
	private boolean[] equalsOrGreater;

	/**
	 * Creates a new version matcher.
	 * 
	 * @param value the needed version, e.g. "1.3+" or "1.3+.2+"
	 */
	public VersionMatcher( String value ) {
		String[] chunks = StringUtil.splitAndTrim( value, '.' );
		this.versionNumbers = new int[ chunks.length ];
		this.equalsOrGreater = new boolean[ chunks.length ];
		for (int i = 0; i < chunks.length; i++) {
			IntegerMatcher matcher = new IntegerMatcher( chunks[i] );
			this.versionNumbers[i] = matcher.number;
			this.equalsOrGreater[i] = matcher.equalsOrGreater;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Matcher#matches(java.lang.String)
	 */
	public boolean matches(String deviceValue) {
		String[] chunks = StringUtil.splitAndTrim( deviceValue, '.' );
		int min = Math.min( chunks.length, this.versionNumbers.length );
		int diff = this.versionNumbers.length - chunks.length; 
		boolean carryFlag = false;
		for (int i = 0; i < min; i++) {
			int deviceNumber = Integer.parseInt( chunks[i] );
			int neededNumber = this.versionNumbers[i];
			boolean eqOrGreater = this.equalsOrGreater[i];
			if (deviceNumber < neededNumber && !carryFlag ) {
				return false;
			} else if (deviceNumber > neededNumber) {
				if (eqOrGreater) {
					carryFlag = true;
				} else {
					return false;
				}
			}
		}
		if (diff > 0) {
			// when there are more needed version chunks than the device specifies,
			// the version only matches, if the last compared version-chunk was greater
			// than the needed one.
			// example 1: needed="1.2+.3+" given="1.2" would return false
			// example 2: needed="1.2+.3+" given="1.3" would return true
			return carryFlag;
		} else {
			return true;
		}
	}

}
