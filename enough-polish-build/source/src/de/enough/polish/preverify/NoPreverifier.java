/*
 * Created on 30-Jul-2005 at 20:49:10.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.preverify;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.util.FileUtil;

/**
 * <p>A dummy preverifier that can be used for devices/platforms that don't need preverification, e.g. the Korean WIPI platform.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        30-Jul-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class NoPreverifier extends Preverifier {

	public NoPreverifier() {
		super();
	}

	/**
	 * Does not preverify for the given device, but just returns quietly.
	 *  
	 * @param device the target device 
	 * @param sourceDir the directory containing the class files
	 * @param targetDir the directory to which the preverfied class files should be written to
	 * @param bootClassPath the boot class path of the device
	 * @param classPath  the class path of the device
	 * @throws IOException when the process could not be executed
	 */
	public void preverify(Device device, File sourceDir, File targetDir,
			Path bootClassPath, Path classPath) throws IOException 
	{
		// don't preverify at all!
		if ( !sourceDir.equals(targetDir)) {
			FileUtil.copyDirectoryContents( sourceDir, targetDir, true );
		}
		System.out.println("(skipping preverification)");
	}

}
