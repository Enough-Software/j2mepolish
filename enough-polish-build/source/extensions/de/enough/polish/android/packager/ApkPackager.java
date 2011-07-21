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
import de.enough.polish.ant.build.SignSetting;
import de.enough.polish.jar.Packager;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Creates an .apk package</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class ApkPackager extends Packager{

	public final static String extension = "apk";

	/* (non-Javadoc)
	 * @see de.enough.polish.jar.Packager#createPackage(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void createPackage(File sourceDir, File targetFile, Device device,
			Locale locale, Environment env) 
	throws IOException, BuildException 
	{
		String pathToApkbuilder = ArgumentHelper.getPathForApkbuilder(env);
		if (pathToApkbuilder != null) {
			SignSetting signSetting = env.getBuildSetting().getSignSetting();
			boolean signApplication = signSetting != null && signSetting.isActive(env);
			ArrayList arguments = getDefaultArguments(pathToApkbuilder,env,signApplication);
			String tools = ArgumentHelper.getPlatformTools(env);
			File directory = new File(tools);

			String package1 = ArgumentHelper.getPackage("apk", env);
			System.out.println("apk: Packaging " + package1);
			if(signApplication) {
				System.out.println("apk: The application will not be signed with a debug signature.");
			} else {
				System.out.println("apk: The application will be signed with a debug signature.");
			}

			try {
				int result = ProcessUtil.exec( arguments, "apk: ", true, null, directory );
				if (result != 0) {
					System.out.println("apk arguments were:");
					System.out.println(ProcessUtil.toString(arguments));
					throw new BuildException("apk was unable to create package - got result " + result);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("apk arguments were:");
				System.out.println(ProcessUtil.toString(arguments));
				throw new BuildException("apk was unable to create package - got error " + e);
			}
		} else {
			throw new BuildException("Could not find apkBuilder tool. Please check your path to the android home.");
		}
	}

	/**
	 * Returns the default arguments for executable
	 * @param executable the executable
	 * @param env the environment
	 * @param signApplication true if the application should be signed with a real signature. The application will not be signed with the default debug signature.
	 * @return the ArrayList
	 */
	static ArrayList getDefaultArguments(String executable, Environment env, boolean signApplication)
	{
		ArrayList arguments = new ArrayList();
		arguments.add(executable);
		arguments.add(ArgumentHelper.getPackage(extension,env));
		if(signApplication) {
			arguments.add("-u");
		}
		arguments.add("-z");
		arguments.add(ArgumentHelper.getPackage("ap_",env));
		arguments.add("-f");
		arguments.add(ArgumentHelper.getDex(env));
		arguments.add("-rj");
		arguments.add(ArgumentHelper.getLibs(env));
		return arguments;
	}
}
