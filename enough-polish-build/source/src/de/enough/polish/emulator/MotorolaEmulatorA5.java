/*
 * Created on Mar 8, 2007 at 7:43:23 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.emulator;

import java.io.File;

/**
 * <p>Launches Motorola A.1 emulators</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Mar 8, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MotorolaEmulatorA5 extends MotorolaEmulator {
	
	protected File getEmulatorExecutable( File emulatorBinDir ) {
		return new File( emulatorBinDir, "jblend.exe" );
	}

	/**
	 * Subclasses can override this to limit search to certain Emulator directories within Motorola's SDK
	 * The default implementation accepts all directories starting with "Emulator".
	 * 
	 * @param dir the directory
	 * @return true when the given directory should be searched for the actual device specific resource properties file.
	 */
	protected boolean acceptsEmulatorDirectory(File dir) {
		return dir.getName().startsWith("EmulatorA.5");
	}
}
