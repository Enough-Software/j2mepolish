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
package de.enough.polish.android.postcompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.android.ArgumentHelper;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Compiles the compiled classes to .dex file</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class DexPostCompiler extends PostCompiler{
	
	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
	 */
	public void postCompile(File classesDir, Device device)
			throws BuildException {
		Environment env = device.getEnvironment();
	
		String dx = ArgumentHelper.dx(env);
		if (dx != null) {
			ArrayList arguments = getDefaultArguments(dx,env);
			File directory = new File(ArgumentHelper.getPlatformTools(env));
	
			System.out.println("dx: Converting compiled files and external libraries...");
			
			int result = 0;
			try {
				result = ProcessUtil.exec( arguments, "dx: ", true, null, directory );
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("dx arguments were:");
				System.out.println(ProcessUtil.toString(arguments));
				throw new BuildException("Unable to create .dex file for device [" + device.getIdentifier() + "]: " + e.getMessage(), e );
			}
			if(result != 0) {
				//TODO: This code is never reached. The OutOfMemoryError is displayed within the process and the build continues.
				System.out.println("If an OutOfMemoryError occured, please increate the settings in the -Xmx parameter in " + dx);
				System.out.println("dx arguments were:");
				System.out.println(ProcessUtil.toString(arguments));
				throw new BuildException("Unable to create .dex file for device [" + device.getIdentifier() + "]: Result code was:"+result );
			}
		}
	}
	
	/**
	 * Returns the default arguments for executable
	 * @param executable the executable
	 * @param env the environment
	 * @return the ArrayList
	 */
	static ArrayList getDefaultArguments(String executable, Environment env)
	{
		ArrayList arguments = new ArrayList();
		arguments.add(executable);
		if (File.separatorChar != '\\') {
			// this doesn't work on Windows Systems:
			arguments.add("-JXmx512m");
		}
		arguments.add("--dex");
		//TODO add --debug only for test builds?
		arguments.add("--debug");
		arguments.add("--output=" + ArgumentHelper.getDex(env));
		arguments.add(ArgumentHelper.getClasses(env));
		return arguments;
	}

}
