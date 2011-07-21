/*
 * Created on Oct 16, 2008 at 7:43:23 PM.
 * 
 * Copyright (c) 2007 Andre Schmidt / Enough Software
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
package de.enough.polish.android;

import java.io.File;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.android.precompiler.ActivityPreCompiler;
import de.enough.polish.android.precompiler.LibraryCopierPreCompiler;
import de.enough.polish.android.precompiler.ResourcesPreCompiler;
import de.enough.polish.precompile.PreCompiler;

/**
 * <p>Calls ActivityPreCompiler and ResourcesPreCompiler</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class AndroidPreCompiler extends PreCompiler{
	
	/* (non-Javadoc)
	 * @see de.enough.polish.precompile.PreCompiler#preCompile(java.io.File, de.enough.polish.Device)
	 */
	public void preCompile(File classesDir, Device device)
			throws BuildException
	{
		boolean usePolishGui = device.getEnvironment().getBuildSetting().usePolishGui();
		if( ! usePolishGui) {
			throw new BuildException("In order to build for android devices, the Polish GUI must be used. Please set the property 'usePolishGui' in the 'build' tag of your 'build.xml' file to 'true'.");
		}
		new ActivityPreCompiler().preCompile(classesDir, device);
		new ResourcesPreCompiler().preCompile(classesDir, device);
		new LibraryCopierPreCompiler().preCompile(classesDir, device);
	}

}
