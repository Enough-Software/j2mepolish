/*
 * Created on Mar 8, 2007 at 7:43:23 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.emulator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;

/**
 * <p>Launches Motorola emulators</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Mar 8, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MotorolaEmulator extends Emulator {
	
	static File[] motorolaSdkHomes;
	protected File emulatorProperties;
	private String[] commandLineArguments;

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getArguments()
	 */
	public String[] getArguments() {
		return this.commandLineArguments;
	}

	/**
	 * Can be overriden by subclasses for adding emulator specific command line arguments to the given list.
	 * 
	 * @param arguments the arguments
	 * @param emulatorProps
	 * @param jadPath
	 * @param jarPath
	 * @return true when all arguments could be added
	 */
	protected boolean addArguments(List arguments, File emulatorProps, String jadPath, String jarPath, Device dev, Environment env ) {
		File executable;
		// emulatorProps is in ${emulator.home}/res/devices in newer (2007+) SDKs:
		if (emulatorProps.getName().endsWith(".xml")) {
			executable = 
				new File( emulatorProps.getParentFile().getParentFile().getParentFile(),
						"bin/emulator.exe" );
		} else {
			// emulatorProps is in ${emulator.home}/Resources in older SDKs:
			executable = getEmulatorExecutable(emulatorProps.getParentFile().getParentFile() );
		}
		if (!executable.exists()) {
			System.out.println("Warning: unable to find emulator executable at " + executable.getAbsolutePath());
			return false;
		}
		arguments.add( executable.getAbsolutePath() );
		return addAdditionalArguments(arguments, emulatorProps, jadPath, jarPath, dev, env, executable );
	}

	protected boolean addAdditionalArguments(List arguments, File emulatorProps, String jadPath, String jarPath, Device dev, Environment env, File executable) {
		arguments.add("-Xdescriptor:" + jadPath );
		arguments.add("-Xdevice:" + getDeviceName( emulatorProps, dev, env ) );
		return true;
	}

	protected String getDeviceName(File emulatorProps, Device dev, Environment env) {
		String propsName;
		if (emulatorProps.getName().endsWith(".xml")) {
			propsName = emulatorProps.getName().substring( 0, emulatorProps.getName().length() - ".xml".length() );
		} else {
			propsName = emulatorProps.getName().substring( 0, emulatorProps.getName().length() - ".props".length() );
		}
		return propsName;
	}

	protected File getEmulatorExecutable( File emulatorBinDir ) {
		return new File( emulatorBinDir, "jblend.exe" );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, de.enough.polish.Environment)
	 */
	public boolean init(Device dev, EmulatorSetting setting, Environment env) {
		if (motorolaSdkHomes == null) {
			motorolaSdkHomes = getMotorolaHomes( env );
			if (motorolaSdkHomes == null) {
				// did not find the SDK path:
				return false;
			}
		}
		this.emulatorProperties = getEmulatorProperties( dev, env );
		if (this.emulatorProperties == null) {
			return false;
		}
		ArrayList arguments = new ArrayList();
		String jadPath= env.getVariable("polish.jadPath");
		String jarPath= env.getVariable("polish.jarPath");
		if (File.separatorChar != '\\') {
			// use wine:
			arguments.add( "wine" );
		}
		if (!addArguments( arguments, this.emulatorProperties, jadPath, jarPath, dev, env )) {
			return false;
		}
		this.commandLineArguments = (String[]) arguments.toArray( new String[ arguments.size() ]);
		return true;
	}

	protected File getEmulatorProperties(Device dev, Environment env) {
		String propName = env.getVariable("polish.Emulator.Skin");
		if (propName == null) {
			propName = dev.getName();
		}
		for (int i = 0; i < motorolaSdkHomes.length; i++) {
			File motorolaSdkHome = motorolaSdkHomes[i];
			File emulatorProps =  getEmulatorProperties( motorolaSdkHome, propName, dev, env );
			if (emulatorProps != null) {
				return emulatorProps;
			}
		}
		return null;
	}

	protected File getEmulatorProperties(File motorolaSdkHome, String propName, Device dev, Environment env) {
		System.out.println("getting emulator file from " + motorolaSdkHome.getAbsolutePath() );
		// first check for newer (2007+) SDK, which store properties in [proName].xml files:
		String realSdk = "";
		if (motorolaSdkHome.getName().indexOf("JMESDK") == -1) {
			realSdk = "/JMESDK";
		}
		File propertiesFolder = new File( motorolaSdkHome, realSdk + "/res/devices/" );
		if (propertiesFolder.exists()) {
			File[] files = propertiesFolder.listFiles();
			if (files != null) {
				String xmlPropName;
				if (propName.endsWith(".props")) {
					xmlPropName = propName.substring(0, propName.length() - ".props".length() ); 
				} else {
					xmlPropName = propName;
				}
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					System.out.println("checking " + file.getName() + " for " + xmlPropName );
					if (file.getName().indexOf(xmlPropName) != -1) {
						// found it!
						return file;
					}
				}
			}
		}
		// now check for older SDKs:
		// when we're lucky the definitiom says "EmulatorA.1/bin/Resources/name.props" or similar:
		File emulatorFile = new File( motorolaSdkHome, propName );
		if (emulatorFile.exists()) {
			return emulatorFile;
		}
		// search for potential matches in all Emulator props files:
		File[] files = motorolaSdkHome.listFiles();
		if (files == null) {
			return null;
		}
		Arrays.sort( files );
		boolean propNameContainsExtension = propName.endsWith(".props");
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if ( file.isDirectory() && acceptsEmulatorDirectory( file )) {
				emulatorFile = getEmulatorProperties( file, propName, propNameContainsExtension );
				if (emulatorFile != null && emulatorFile.exists()) {
					return emulatorFile;
				}
			}
		}
		System.out.println("Warning: unable to find Motorola emulator for " + propName );
		return null;
	}

	/**
	 * Subclasses can override this to limit search to certain Emulator directories within Motorola's SDK
	 * The default implementation accepts all directories starting with "Emulator".
	 * 
	 * @param dir the directory
	 * @return true when the given directory should be searched for the actual device specific resource properties file.
	 */
	protected boolean acceptsEmulatorDirectory(File dir) {
		return dir.getName().startsWith("Emulator");
	}

	protected File getEmulatorProperties(File emulatorHome, String propName, boolean propNameContainsExtension ) {
		File[] propertyFiles = (new File( emulatorHome, "bin/Resources" )).listFiles();
		if (propertyFiles == null) {
			return null;
		}
		for (int i = 0; i < propertyFiles.length; i++) {
			File file = propertyFiles[i];
			if (propNameContainsExtension) {
				if (file.getName().equals(propName)) {
					return file;
				}
			} else if (file.getName().endsWith(".props") && file.getName().indexOf(propName) != -1) {
				return file;
			}
		}
		return null;
	}

	protected File[] getMotorolaHomes(Environment env) {
		String motorolaHomeProperty = env.getVariable("motorola.home");
		File motorolaHomeDir;
		ArrayList homesList = new ArrayList();
		if (motorolaHomeProperty != null) {
			motorolaHomeDir = new File( motorolaHomeProperty );
			if (!(motorolaHomeDir.exists())) {
				System.err.println("Warning: unable to start motorola emulator - the property \"motorola.home\" points to the invalid directory " + motorolaHomeProperty + ". Please change this property either in your build.xml script or in ${polish.home}/global.properties.");
				return null;
			}
			if (motorolaHomeDir.getName().indexOf("SDK") != -1) {
				motorolaHomeDir = motorolaHomeDir.getParentFile();
			}
		} else {
			motorolaHomeDir = new File("C:/Program Files/Motorola");
			if (!(motorolaHomeDir.exists())) {
				motorolaHomeDir = new File("C:/Programme/Motorola");
				if (!(motorolaHomeDir.exists())) {
					System.err.println("Warning: unable to start motorola emulator - no property \"motorola.home\" is defined in your build.xml script or in ${polish.home}/global.properties.");
					return null;
				}
			}	
		}
		// check for default installation:		
		File[] files = motorolaHomeDir.listFiles();
		if (files == null) {
			return null;
		}
		Arrays.sort(files);
		for (int i = files.length - 1; i >= 0 ; i--) {
			File file = files[i];
			if (file.getName().indexOf("MOTODEV") != -1  // a "new"[2007+] SDK
					|| file.getName().indexOf("SDK") != -1) // (this is an older SDK) 
			{
				homesList.add( file );
			}
		}
		if (homesList.size() == 0) {
			System.out.println("Warning: Unable to find any Motorola SDK in " + motorolaHomeDir.getAbsolutePath()+ ". Please change this property either in your build.xml script or in ${polish.home}/global.properties.");
			return null;
		} else {
			return (File[]) homesList.toArray( new File[ homesList.size() ]);
		}
	}
	 

}
