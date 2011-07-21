/*
 * Created on 24-April-2009 at 17:40:13.
 * 
 * Copyright (c) 2009 Robert Virkus / Enough Software
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
import java.util.ArrayList;
import java.util.Iterator;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Uses the microemulator for emulation.</p>
 * <p>For more information visit http://www.microemu.org.</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MicroEmulator extends Emulator {

	private String microemulatorPlayerPath;
	private String microemulatorHomePath;
	private String librariesPath;
	private EmulatorSetting setting;

	/**
	 * Creates a new emulator launcher.
	 */
	public MicroEmulator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, de.enough.polish.Environment, org.apache.tools.ant.Project, de.enough.polish.BooleanEvaluator, java.lang.String)
	 */
	public boolean init(Device dev, EmulatorSetting setting, Environment env) {
		this.setting = setting;
		String microemulatorHome = env.getVariable("microemulator.home");
		if (microemulatorHome == null) {
			microemulatorHome = env.getVariable("polish.home") + "/lib/microemulator";
		}
		File playerJar = new File( microemulatorHome + File.separatorChar + "microemulator.jar" );
		if (!playerJar.exists()) {
			System.err.println("Warning: unable to launch the microemulator - the \"microemulator.home\" property in your build.xml script points to the invcalid path [" + microemulatorHome + "]. \"microemulator.home\" needs to point to the installation folder of the microemulator (=the folder in which the \"microemulator.jar\" is located).");
			return false;
		}
		this.microemulatorHomePath = microemulatorHome;
		File libHome = new File( microemulatorHome + "/lib");
		String[] libraries = libHome.list();
		if (libraries == null) {
			System.err.println("Warning: unable to find additional libraries of microemulator at \"" +  microemulatorHome + "/lib\".");
		} else {
			StringBuffer libsBuffer = new StringBuffer();
			for (int i = 0; i < libraries.length; i++) {
				String lib = libraries[i];
				if (lib.startsWith("micro") && lib.endsWith(".jar")) {
					libsBuffer.append("lib" + File.separatorChar + lib + File.pathSeparatorChar);
				}
			}
			this.librariesPath = libsBuffer.toString();
		}
		this.microemulatorPlayerPath = playerJar.getAbsolutePath();
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getArguments()
	 */
	public String[] getArguments() {
		Environment env = this.environment;
		String screenWidth = env.getVariable("polish.FullCanvasWidth");
		String screenHeight = env.getVariable("polish.FullCanvasHeight");
		if (screenWidth == null) {
			screenWidth = env.getVariable("polish.ScreenWidth");
			screenHeight = env.getVariable("polish.ScreenHeight");
			if (screenWidth == null) {
				screenWidth = "240";
				screenHeight = "320";
			}
		}
		ArrayList args = new ArrayList();
		args.add( "java");
		if (this.setting.enableProfiler() || this.setting.enableMemoryMonitor()) {
			args.add( "-Dcom.sun.management.jmxremote" );
		}
		args.add( "-cp");
		args.add( this.microemulatorPlayerPath + File.pathSeparatorChar + this.microemulatorHomePath + "/devices/microemu-device-resizable.jar" + File.pathSeparatorChar + this.librariesPath );
		args.add( "org.microemu.app.Main" );
		args.add( "--resizableDevice" ); args.add( screenWidth); args.add( screenHeight );
		args.add( "--device" ); args.add( "org/microemu/device/resizable/device.xml" );
		args.add( "--appclasspath" ); args.add( this.librariesPath );
		args.add( this.environment.getVariable("polish.jadPath") );
		return (String[]) args.toArray( new String[args.size()] );
	}
	
	/**
	 * Adds the debugging settings to the arguments list.
	 * By default the UEI arguments -Xdebug and -Xrunjdwp arguments are added by calling debugger.addDebugArguments( List ).
	 * 
	 * @param argsList the arguments list
	 * @param debugger the debugger
	 */
	protected void addDebugArguments(ArrayList argsList, Debugger debugger) {
		ArrayList debugArguments = new ArrayList();
		super.addDebugArguments( debugArguments, debugger);
		int index = 1;
		for (Iterator iter = debugArguments.iterator(); iter.hasNext();) {
			String arg = (String) iter.next();
			argsList.add(index, arg);
			index++;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getExecutionDir()
	 */
	protected File getExecutionDir() {
		return new File( this.microemulatorHomePath );
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#exec(java.lang.String[], java.lang.String, boolean, de.enough.polish.util.OutputFilter, java.io.File)
	 */
	protected int exec( String[] arguments, String info, boolean wait, OutputFilter filter, File executionDir ) 
	throws IOException 
	{
		if (this.setting.enableProfiler() || this.setting.enableMemoryMonitor()) {
			ProcessUtil.exec(new String[]{"jvisualvm"}, "jvisualvm: ", false );
		}
		return super.exec(arguments, info, wait, filter, executionDir);
	}
	


}
