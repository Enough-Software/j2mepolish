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


import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;

/**
 * <p>Launches Nokia emulators.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02.10.2004 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class NokiaEmulator extends WtkEmulator {

	private String nokiaHome;

	/**
	 * Creates a new emulator
	 */
	public NokiaEmulator() {
		super();
		this.appendXdeviceArgument = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.WtkEmulator#getEmulatorSkin(java.lang.String, java.lang.String)
	 */
	protected File getEmulatorSkin(String wtkHome, String xDevice  ) {
		//System.out.println("testing Nokia-Skin " + xDevice );
		File skinFolder = new File( this.nokiaHome + File.separatorChar + "Devices" + File.separatorChar + xDevice );
		if (!skinFolder.exists()) {
			skinFolder = new File( this.nokiaHome + File.separatorChar + xDevice );
		}
		return skinFolder; 
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.WtkEmulator#getEmulatorExcecutable(java.lang.String, java.lang.String)
	 */
	protected File getEmulatorExcecutable(File wtkHome, String xDevice, Device dev, Environment env) {
		String execName = dev.getCapability("polish.Emulator.Executable");
		if (execName == null) {
			if (File.separatorChar == '\\') {
				execName = "emulator.exe";
			} else {
				execName = "emulator";
			}
		}
		File executable = new File( this.nokiaHome + File.separatorChar + "Devices" + File.separatorChar + xDevice + File.separatorChar + "bin" + File.separatorChar + execName);
		if (!executable.exists()) {
			executable = new File( this.nokiaHome + File.separatorChar + xDevice + File.separatorChar + "bin" + File.separatorChar + execName);
		}
		return executable;
	}
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, java.util.HashMap, org.apache.tools.ant.Project, de.enough.polish.preprocess.BooleanEvaluator, java.lang.String)
	 */
	public boolean init(Device dev, EmulatorSetting setting, Environment properties) 
	{
		String skin = dev.getCapability("polish.build.Emulator.Skin");
		if (skin == null) {
			skin = dev.getCapability("polish.Emulator.Skin");
		}
		if (skin == null) {
			System.err.println("Unable to start emulator for device [" + dev.getIdentifier() + "]: no \"build.Emulator.Skin\"-capability defined in devices.xml.");
			return false;
		}
		String nokiaHomePath = properties.getVariable("nokia.home");
		if (nokiaHomePath == null) {
			if (File.separatorChar == '\\') {
				nokiaHomePath = "C:\\Nokia";
			} else {
				nokiaHomePath = System.getProperty("user.home") + "/Nokia";
			}
			File home = new File( nokiaHomePath );
			if (!home.exists()) {
				System.err.println("Unable to start emulator for device [" + dev.getIdentifier() + "]: Please define the ${nokia.home}-property in your build.xml. The default path [" + nokiaHomePath + "] does not exist.");
			}
		} else {
			File home = new File( nokiaHomePath );
			if (!home.exists()) {
				System.err.println("Unable to start emulator for device [" + dev.getIdentifier() + "]: Please adjust the ${nokia.home}-property in your build.xml. The path [" + nokiaHomePath + "] does not exist.");
			}
		}
		this.nokiaHome = nokiaHomePath;
		return super.init(dev, setting, properties);
	}

}
