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
package de.enough.polish.android.precompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.android.ArgumentHelper;
import de.enough.polish.precompile.PreCompiler;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Compiles .aidl files to .java files, currently not used
 * in the build process</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class AidlPreCompiler extends PreCompiler{
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.precompile.PreCompiler#preCompile(java.io.File,
	 * de.enough.polish.Device)
	 */
	public void preCompile(File classesDir, Device device)
			throws BuildException {
		Environment env = device.getEnvironment();
		
		ArrayList arguments = getDefaultArguments(ArgumentHelper.aidl(env),env);
		File directory = new File(ArgumentHelper.getPlatformTools(env));  
		
		try {
			ProcessUtil.exec( arguments, "aidl: ", true, null, directory );
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("aidl arguments were:");
			System.out.println(ProcessUtil.toString(arguments));
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
		arguments.add("-p" + ArgumentHelper.getFramework(env));
		arguments.add("-I" + ArgumentHelper.getSrc(env));
		
		String[] files = FileUtil.filterDirectory(new File(ArgumentHelper.getSrc(env)), "*.aidl", true);
		
		for (int i = 0; i < files.length; i++) {
			arguments.add(files[i]);
		}
		
		return arguments;
	}
	
}
