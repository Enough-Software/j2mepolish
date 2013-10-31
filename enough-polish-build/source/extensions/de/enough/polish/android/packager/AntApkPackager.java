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

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.android.ArgumentHelper;
import de.enough.polish.ant.build.SignSetting;
import de.enough.polish.jar.Packager;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.OsUtil;
import de.enough.polish.util.ProcessUtil;
import de.enough.polish.util.ReflectionUtil;

/**
 * <p>Creates an .apk package</p>
 *
 * <p>Copyright Enough Software 2008 - 2011</p>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class AntApkPackager extends Packager{

	public final static String extension = "apk";

	/* (non-Javadoc)
	 * @see de.enough.polish.jar.Packager#createPackage(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void createPackage(File sourceDir, File targetFile, Device device,
			Locale locale, Environment env) 
	throws IOException, BuildException 
	{
		try {
			Ant antTask = new Ant();
			antTask.setProject( (Project) env.get("ant.project"));
			File baseDir = new File(ArgumentHelper.getActivity(env));
			antTask.setAntfile( baseDir.getAbsolutePath() + "/build.xml");
			antTask.setDir( baseDir );
			SignSetting signSetting = env.getBuildSetting().getSignSetting();
			boolean signApplication = signSetting != null && signSetting.isActive(env);
			if (signApplication) {
				antTask.setTarget("debug");
			} else {
				antTask.setTarget("release");
			}
			System.out.println("Launching Android build process...");
			antTask.execute();
			System.out.println("Finished Android build process.");
			String apkName;
			if (signApplication) {
				apkName = "AppActivity-debug.apk";
			} else {
				apkName = "AppActivity-release.apk";				
			}
			File apkFile = new File(baseDir, "bin/" + apkName);
			if (!apkFile.exists()) {
				if (signApplication) {
					apkName = "AppActivity-debug-unsigned.apk";
				} else {
					apkName = "AppActivity-release-unsigned.apk";				
				}
				apkFile = new File(baseDir, "bin/" + apkName);
			}
			String targetName = targetFile.getName();
			if (targetName.endsWith(".jar")) {
				targetName = targetName.substring(0, targetName.length() - ".jar".length()) + ".apk";
				targetFile = new File( targetFile.getParentFile(), targetName);
			}
			FileUtil.move(apkFile, targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("apk was unable to create package - got error " + e);
		}
	}

	/**
	 * Returns the default arguments for executable
	 * @param executable the executable
	 * @param env the environment
	 * @param signApplication true if the application should be signed with a real signature. The application will not be signed with the default debug signature.
	 * @return the ArrayList
	 */
	static ArrayList<String> getDefaultArguments(Environment env, boolean signApplication)
	{
		ArrayList<String> arguments = new ArrayList<String>();
		System.out.println("ANT_HOME=" + System.getProperty("ANT_HOME"));
		if (env.hasVariable("ANT_HOME")) {
			if (OsUtil.isRunningWindows()) {
				arguments.add(env.getVariable("ANT_HOME") + "\\bin\\ant.bat" );
			} else {
				arguments.add(env.getVariable("ANT_HOME") + "/bin/ant" );
			}
		} else {
			arguments.add("ant");
		}
		if (signApplication){
			arguments.add("debug");
		} else {
			arguments.add("release");
		}
		return arguments;
	}
}
