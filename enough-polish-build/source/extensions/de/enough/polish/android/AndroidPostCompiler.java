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
import de.enough.polish.android.postcompiler.DexPostCompiler;
import de.enough.polish.android.postcompiler.ResourceStreamPostCompiler;
import de.enough.polish.postcompile.PostCompiler;

/**
 * <p>Calls ResourceStreamPostCompiler and DexPostCompiler</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class AndroidPostCompiler extends PostCompiler{

	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
	 */
	public void postCompile(File classesDir, Device device)
			throws BuildException {
		
		new ResourceStreamPostCompiler().postCompile(classesDir, device);
		
		// TODO: Do ProGuard obfuscation here and give dex the obfuscated jar file instead of the class files.
		
		new DexPostCompiler().postCompile(classesDir, device);
	}
	
}
