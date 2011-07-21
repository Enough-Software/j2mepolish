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
package de.enough.polish.ant.android;

import java.io.File;
import java.util.Arrays;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;
import de.enough.polish.util.OsUtil;


/**
 * <p>Provides static methods to help in argument
 * creation for android processes</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Oct-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class ArgumentHelper {
	private static boolean isAndroidVersionHigherOrEquals15;
	private static boolean isAndroidVersionResolved;

	/**
	 * Returns the absolute path of the created activity
	 * @param env the environment
	 * @return the absolute path of the created activity
	 */
	public static String getActivity(Environment env)
	{
		return env.getDevice().getBaseDir() + File.separator + "activity";
	}
	
	/**
	 * Returns the android build folder of enough-polish-build
	 * @param env the environment
	 * @return the android build folder of enough-polish-build
	 */
	public static String getBuild(Environment env)
	{
		return env.getVariable("polish.home") + File.separator + "android";
	}
	
	/**
	 * Returns the /src folder of the activity
	 * @param env the environment
	 * @return the /src folder of the activity
	 */
	public static String getSrc(Environment env)
	{
		return getActivity(env) + File.separator + "src";
	}
	
	/**
	 * Returns the /res folder of the activity
	 * @param env the environment
	 * @return the /res folder of the activity
	 */
	public static String getRes(Environment env)
	{
		return getActivity(env) + File.separator + "res";
	}
	
	/**
	 * Returns the /libs folder of the activity
	 * @param env the environment
	 * @return the /libs folder of the activity
	 */
	public static String getLibs(Environment env)
	{
		return getActivity(env) + File.separator + "libs";
	}
	
	/**
	 * Returns the /bin folder of the activity
	 * @param env the environment
	 * @return the /bin folder of the activity
	 */
	public static String getBin(Environment env)
	{
		return getActivity(env) + File.separator + "bin";
	}
	
	/**
	 * Returns the /res/raw folder of the activity
	 * @param env the environment
	 * @return the /res/raw folder of the activity
	 */
	public static String getRaw(Environment env)
	{
		return getRes(env) + File.separator + "raw";
	}
	
	/**
	 * Returns the android home folder
	 * @param env the environment
	 * @return the android home folder
	 */
	public static String getHome(Environment env)
	{
		String home = env.getVariable("android.home");
		if (home == null || "".equals(home)) {
			throw new BuildException("Please specify the \"android.home\" property in your build.xml script, ${polish.home}/global.properties or in ${user.name}.properties.");
		}
		return home;
	}
	
	/**
	 * Returns the /tools folder of the android installation
	 * @param env the environment
	 * @return the /tools folder of the android installation
	 */
	public static String getPlatformTools(Environment env)
	{
		String newPath = getHome(env) + File.separator + "platform-tools";
		String oldPath = getPlatformHome(env) + File.separator + "tools"; 
		return resolveValidPath(newPath, oldPath);
	}
	
	/**
	 * Returns the framework.aidl of the android installation
	 * @param env the environment
	 * @return the framework.aidl of the android installation
	 */
	public static String getFramework(Environment env)
	{
		return getPlatformTools(env) + File.separator + "lib" + File.separator + "framework.aidl";
	}
	
	/**
	 * Returns /bin/classes.dex of the activity 
	 * @param env the environment
	 * @return the /bin/classes.dex of the activity 
	 */
	public static String getDex(Environment env)
	{
		return getBin(env) + File.separator + "classes.dex";
	}
	
	/**
	 * Returns /bin/classes of the activity 
	 * @param env the environment
	 * @return the /bin/classes of the activity 
	 */
	public static String getClasses(Environment env)
	{
		return getBin(env) + File.separator + "classes";
	}
	
	/**
	 * Returns a package filename with the given extension  
	 * @param env the environment
	 * @return a package filename with the given extension 
	 */
	public static String getPackage(String extension, Environment env)
	{
		String name = env.getVariable("polish.jarName");
		name = name.substring( 0, name.length() - ".jar".length() );
		return env.getBuildSetting().getDestDir(env) + File.separator + name + "." + extension;
	}
	
	/**
	 * Returns the name for the activity used in .project  
	 * @param env the environment
	 * @return the name for the activity 
	 */
	public static String getName(Environment env)
	{
		String fullName = env.getVariable("polish.classes.midlet-1");
		String name = fullName.substring(fullName.lastIndexOf(".") + 1);
		return name;
	}
	
	/**
	 * Returns the os-dependent executable for the activity creator
	 * @return the os-dependent executable for the activity creator
	 */
	public static String activityCreator(Environment env)
	{
		if(OsUtil.isRunningWindows())
		{
			return resolve( getPlatformTools(env) + "\\activitycreator.bat" );
		}
		else
		{
			return resolve( getPlatformTools(env) + "/activitycreator" );
		}
	}
	
	private static String resolve(String path) {
		File file = new File( path );
		if (file.exists()) {
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}

	public static String android(Environment env) {
		if(OsUtil.isRunningWindows())
		{
			return resolve( getHome(env) + "\\tools\\android.bat");
		} else {
			return resolve( getHome(env) + "/tools/android");
		}
	}
	
	/**
	 * Returns the os-dependent executable for adb
	 * @return the os-dependent executable for adb
	 */
	public static String adb(Environment env)
	{
		String path;
		if(OsUtil.isRunningWindows())
		{
			path = getHome(env) + "\\platform-tools\\adb.exe";
		}
		else
		{
			path = getHome(env) + "/platform-tools/adb";
		}
		String alternativePath;
		if(OsUtil.isRunningWindows())
		{
			alternativePath = getHome(env) + "\\tools\\adb.exe";
		}
		else
		{
			alternativePath = getHome(env) + "/tools/adb";
		}
		return resolveValidPath( path, alternativePath );
	}
	
	private static String resolveValidPath(String path, String alternativePath) {
		File file = new File( path );
		if (file.exists()) {
			return path;
		}
		return alternativePath;
	}

	/**
	 * Returns the os-dependent executable for aidl
	 * @return the os-dependent executable for aidl
	 */
	public static String aidl(Environment env)
	{
		String path;
		if(OsUtil.isRunningWindows())
		{
			path = getHome(env) + "\\platform-tools\\aidl.exe";
		}
		else
		{
			path = getPlatformTools(env) + "/platform-tools/aidl";
		}
		String alternativePath;
		if(OsUtil.isRunningWindows())
		{
			alternativePath = getPlatformTools(env) + "\\aidl.exe";
		}
		else
		{
			alternativePath = getPlatformTools(env) + "/aidl";
		}
		return resolveValidPath(path, alternativePath);
	}
	
	/**
	 * Returns the os-dependent executable for aapt
	 * @return the os-dependent executable for aapt
	 */
	public static String aapt(Environment env)
	{
		String path;
		if(OsUtil.isRunningWindows())
		{
			path = getHome(env) + "\\platform-tools\\aapt.exe";
		}
		else
		{
			path = getHome(env) + "/platform-tools/aapt";
		}
		String alternativePath;
		if(OsUtil.isRunningWindows())
		{
			alternativePath = getPlatformTools(env) + "\\aapt.exe";
		}
		else
		{
			alternativePath = getPlatformTools(env) + "/aapt";
		}
		return resolveValidPath(path, alternativePath);
	}
	
	/**
	 * Returns the os-dependent executable for dx
	 * @return the os-dependent executable for dx
	 */
	public static String dx(Environment env)
	{
		String path;
		if(OsUtil.isRunningWindows())
		{
			path = getHome(env) + "\\platform-tools\\dx.bat";
		}
		else
		{
			path = getHome(env) + "/platform-tools/dx";
		}
		String alternativePath;
		if(OsUtil.isRunningWindows())
		{
			alternativePath = getPlatformTools(env) + "\\dx.bat";
		}
		else
		{
			alternativePath = getPlatformTools(env) + "/dx";
		}
		return resolveValidPath(path, alternativePath);
	}
	
	/**
	 * Returns the os-dependent executable for apk
	 * @return the os-dependent executable for apk
	 */
	public static String getPathForApkbuilder(Environment env)
	{
		if(OsUtil.isRunningWindows())
		{
			return getHome(env) +  "\\tools\\apkbuilder.bat";
		}
		else
		{
			return getHome(env) + "/tools/apkbuilder";
		}
	}
	
	/**
	 * Returns the os-dependent executable for the emulator
	 * @return the os-dependent executable for the emulator
	 */
	public static String emulator(Environment env)
	{
		if(OsUtil.isRunningWindows())
		{
			return getHome(env) + "\\tools\\emulator.exe";
		}
		else
		{
			return getHome(env)+ "/tools/emulator";
		}
	}
	
	/**
	 * Determines whether the underlying Android SDK has version 1.5 or higher.
	 * From then onwards various build chain tools have been changed.
	 * 
	 * @param env the environment
	 * @return true when the SDK is 1.5 or higher
	 */
	public static boolean isAndroidVersionHigherOrEquals15(Environment env) {
		if (!isAndroidVersionResolved) {
			isAndroidVersionHigherOrEquals15 = android(env) != null;
			isAndroidVersionResolved = true;
		}
		return isAndroidVersionHigherOrEquals15;
	}
	
	public static String getAndroidJar(Environment env) {
		return getPlatformHome(env) + File.separatorChar + "android.jar";
	}

	public static String getPlatformHome(Environment env) {
		if (isAndroidVersionHigherOrEquals15(env)) {
			String platformHome = env.getVariable("build.android.platform.home");
			if (platformHome != null) {
				return platformHome;
			}
			String platformVersion = env.getVariable("polish.build.Android.Platform.Version");
			File platformsHome = new File( getHome(env) + "/platforms");
			String[] availablePlatforms = platformsHome.list();
			if (availablePlatforms == null || availablePlatforms.length == 0) {
				platformHome = getHome(env);
			} else if (platformVersion != null) {
				String targetVersion = env.getVariable("polish.build.Android.Platform.Target");
				for (int i = 0; i < availablePlatforms.length; i++) {
					String name = availablePlatforms[i];
					if ((name.indexOf(platformVersion) != -1)
						|| (targetVersion != null && name.indexOf(targetVersion) != -1) 
					){
						File file = new File( platformsHome, name );
						if (file.isDirectory()) {
							platformHome = file.getAbsolutePath();
							break;
						}
					}
				}
			} 
			if (platformHome == null) {
				// use the latest available platform:
				Arrays.sort(availablePlatforms);
				platformHome = (new File(platformsHome, availablePlatforms[availablePlatforms.length-1])).getAbsolutePath();
			}
			env.setVariable("build.android.platform.home", platformHome);
			return platformHome;
		} else {
			return getHome(env);
		}
	}

	public static String getTargetId(Environment env) {
		String targetVersion = env.getVariable("polish.build.Android.Platform.Target");
		if (targetVersion == null) {
			targetVersion = "2";
			String version = env.getVariable("polish.build.Android.Platform.Version");
			if ("1.1".equals(version)) {
				targetVersion = "1";
			}
		}
		return targetVersion;
	}
}
