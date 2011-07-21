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
package de.enough.polish.android.packager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.android.ArgumentHelper;
import de.enough.polish.jar.Packager;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Creates an resource (.ap_) package</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class ResourcesPackager extends Packager{

	public final static String extension = "ap_";

	/* (non-Javadoc)
	 * @see de.enough.polish.jar.Packager#createPackage(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void createPackage(File sourceDir, File targetFile, Device device, Locale locale, Environment env) 
	throws IOException, BuildException 
	{
		String aapt = ArgumentHelper.aapt(env);
		if (aapt != null) {
			ArrayList arguments = getDefaultArguments(aapt,env);
			File directory = new File(ArgumentHelper.getPlatformTools(env));
			
			System.out.println("resource package (aapt): Packaging resources...");
			
			try {
				int result = ProcessUtil.exec( arguments, "aapt: ", true, null, directory );
				if (result != 0) {
					System.out.println("aapt arguments were:");
					System.out.println(ProcessUtil.toString(arguments));
					throw new BuildException("aapt was unable to create package - got result " + result);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("aapt arguments were:");
				System.out.println(ProcessUtil.toString(arguments));
				throw new BuildException("aapt was unable to create package - got error " + e);
			}
		} else {
			throw new BuildException(env.writeProperties("unable to resolve appt tool, please check your android.home setting, which currently reads \"${android.home}\".", false) );
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
		String androidJar = ArgumentHelper.getAndroidJar(env);
		
		ArrayList arguments = new ArrayList();
		arguments.add(executable);
		arguments.add("package");
		arguments.add("-f");
		arguments.add("-M");
		arguments.add(ArgumentHelper.getActivity(env) + "/AndroidManifest.xml");
		arguments.add("-S");
		arguments.add(ArgumentHelper.getRes(env));
		arguments.add("-I");
		arguments.add(androidJar);
		arguments.add("-F");
		arguments.add(ArgumentHelper.getPackage(extension, env));
		
		return arguments;
	}

}
