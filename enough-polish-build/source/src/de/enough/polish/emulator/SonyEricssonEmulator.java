/*
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
import java.io.IOException;
import java.util.Arrays;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.util.OutputFilter;

/**
 * <p>Starts Sony-Ericsson emulators.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09.10.2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SonyEricssonEmulator extends WtkEmulator {

	private String sonyEricssonHome;
	private String sonyWtkHome;

	/**
	 * Creates a new emulator instance
	 */
	public SonyEricssonEmulator() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.WtkEmulator#getEmulatorSkin(java.lang.String, java.lang.String)
	 */
	protected File getEmulatorSkin(String wtkHome, String xDevice  ) {
		this.sonyWtkHome = null;
		if (this.sonyEricssonHome == null ) {
			return super.getEmulatorSkin(wtkHome, xDevice);
		}
		File skinFolder;

		skinFolder = new File( this.sonyEricssonHome + "\\WTK1\\wtklib\\devices\\" + xDevice );
		if (skinFolder.exists()) {
			this.sonyWtkHome = this.sonyEricssonHome + "\\WTK1";
			return skinFolder;
		}

		skinFolder = new File( this.sonyEricssonHome + "\\WTK2\\wtklib\\devices\\" + xDevice );
		if (skinFolder.exists()) {
			this.sonyWtkHome = this.sonyEricssonHome + "\\WTK2";
			return skinFolder;
		}

		skinFolder = new File( this.sonyEricssonHome + "\\PC_Emulation\\WTK1\\wtklib\\devices\\" + xDevice );
		if (skinFolder.exists()) {
			this.sonyWtkHome = this.sonyEricssonHome + "\\PC_Emulation\\WTK1";
			return skinFolder;
		}
		
		skinFolder = new File( this.sonyEricssonHome + "\\PC_Emulation\\WTK2\\wtklib\\devices\\" + xDevice );
		if (skinFolder.exists()) {
			this.sonyWtkHome = this.sonyEricssonHome + "\\PC_Emulation\\WTK2";
			return skinFolder;
		}
		
		System.out.println("Warning: Unable to find Sony-Ericsson emulator in " + skinFolder.getAbsolutePath() );
		// try to get the emulator from the default WTK:
		return super.getEmulatorSkin(wtkHome, xDevice);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.WtkEmulator#getEmulatorExcecutable(java.lang.String, java.lang.String)
	 */
	protected File getEmulatorExcecutable(String wtkHome, String xDevice, Device dev, Environment env) {
		if (this.sonyEricssonHome == null || this.sonyWtkHome == null) {
			return super.getEmulatorExcecutable(wtkHome, xDevice, dev, env);
		}
		if (env.hasSymbol("debug.odd") && this.sonyEricssonHome != null) {
			File execDir = new File( this.sonyEricssonHome + "\\OnDeviceDebug\\bin");
			if (execDir.exists()) {
				this.workingDirectory = execDir;
				return new File( execDir, "emulator.exe");
			}
		}
		String execPath;
		if (this.sonyWtkHome != null) {
			execPath = this.sonyWtkHome + "\\bin\\emulator.exe";	
		} else {
			execPath = this.sonyEricssonHome + "\\bin\\emulator.exe";
		}
	
		
		this.workingDirectory = new File( this.sonyWtkHome + "\\bin" );
		return new File( execPath );
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, java.util.HashMap, org.apache.tools.ant.Project, de.enough.polish.preprocess.BooleanEvaluator, java.lang.String)
	 */
	public boolean init(Device dev, EmulatorSetting setting,
			Environment properties) 
	{
		String skin = dev.getCapability("polish.Emulator.Skin");
		if (skin == null) {
			// add default-emulator name:
			dev.addDirectCapability("polish.Emulator.Skin", "SonyEricsson_" + dev.getName());
		}
		String sonyHomePath = properties.getVariable("sony-ericsson.home");
		if (sonyHomePath == null) {
			sonyHomePath = properties.getVariable("sonyericsson.home");
		}
		File home = null;
		if (sonyHomePath == null) {
			if (File.separatorChar == '\\') {
				sonyHomePath = "C:\\SonyEricsson";
				home = new File( sonyHomePath );
				if (!home.exists()) {
					sonyHomePath = null;
					// try to start emulators from the standard WTK directory. [how?]
					return false;
				}
			}
		} else {
			home = new File( sonyHomePath );
			if (!home.exists()) {
				System.err.println("Unable to start emulator for device [" + dev.getIdentifier() + "]: Please adjust the \"sony-ericsson.home\" property in ${polish.home}/global.properties or in your build.xml. The path [" + sonyHomePath + "] does not exist.");
				return false;
			}
		}
		
		boolean foundSdk = false;
		
		if (home != null) {
			File pcEmulation = new File( home, "PC_Emulation");
			if (!pcEmulation.exists()) {
				File[] files = home.listFiles();
				if (files == null) {
					System.err.println("Unable to find Sony Ericsson SDK directory in " + home.getAbsolutePath() + " - please specify the \"sony-ericsson.home\" property in ${polish.home}/global.properties or in your build.xml script.");
					return false;
				}
				Arrays.sort( files );
				for (int i = files.length; --i >= 0; ) {
					File file = files[i];
					if (file.isDirectory()) {
						pcEmulation = new File( file, "PC_Emulation");
						if (pcEmulation.exists()) {
							home = file;
							sonyHomePath = home.getAbsolutePath();
							foundSdk = true;
							break;
						}
					}
				}
			}
			else
			{
				sonyHomePath = home.getAbsolutePath();
				foundSdk = true;
			}
			
			// for the new sdk
			if(!foundSdk)
			{
				File wtk2 = new File( home, "WTK2");
				if(wtk2.exists())
				{
					sonyHomePath = home.getAbsolutePath();
					foundSdk = true;
				}
			}
			
			if (!foundSdk) {
				System.out.println("Warning: unable to find correct Sony Ericsson SDK in " + home.getAbsolutePath() + ": please specify the sony-ericsson.home property in ${polish.home}/global.properties. Now trying to use default WTK.");
				sonyHomePath = null;
			}
		}
		this.sonyEricssonHome = sonyHomePath;
		return super.init(dev, setting, properties);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#exec(java.lang.String[], java.lang.String, boolean, de.enough.polish.util.OutputFilter, java.io.File)
	 */
	protected int exec(String[] args, String info, boolean wait, OutputFilter filter, File executionDir) throws IOException {
		int xDeviceParamIndex = -1;
		// there was an error - new Sony Ericsson emulators require a "_Emu" 
		// at the end of the device name - why? Who knows?
		String xDevice = null;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-Xdevice:") ){
				xDevice = arg;
				xDeviceParamIndex = i;
				args[i] = xDevice + "_Emu";
				break;
			}
		}
		long startTime = System.currentTimeMillis();
		int result = super.exec(args, info, wait, filter, executionDir);
		long usedTime = System.currentTimeMillis() - startTime;
		if ( result != 0 || usedTime < 3000) {
			// try old SonyEricsson SDK:
			if (xDevice != null) {
				System.out.println("Now trying " + xDevice + "...");
				args[ xDeviceParamIndex ] = xDevice;
				result = super.exec(args, info, wait, filter, executionDir);				
			}
		}
		return result;
	}

	

}
