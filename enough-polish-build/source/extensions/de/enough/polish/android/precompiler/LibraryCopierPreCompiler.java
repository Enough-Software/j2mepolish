/*
 * Created on 18-Aug-2005 at 15:58:25.
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
package de.enough.polish.android.precompiler;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.android.ArgumentHelper;
import de.enough.polish.precompile.PreCompiler;
import de.enough.polish.util.FileUtil;

/**
 * <p>Creates the R.java and Manifest.java</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class LibraryCopierPreCompiler extends PreCompiler {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.precompile.PreCompiler#preCompile(java.io.File,
	 * de.enough.polish.Device)
	 */
	public void preCompile(File classesDir, Device device)
			throws BuildException 
	{
		Environment env = device.getEnvironment();

		File sourceDir = new File( device.getClassesDir() );
		if (!sourceDir.exists()) {
			return;
		}
		System.out.println("libraries: Copying binary library classes to " + ArgumentHelper.getClasses(env) + "...");
		File targetDir = new File( ArgumentHelper.getClasses(env) );
		try {
			FileUtil.copyDirectoryContents(sourceDir, targetDir, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
