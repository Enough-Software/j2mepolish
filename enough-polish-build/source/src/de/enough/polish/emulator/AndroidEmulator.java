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
package de.enough.polish.emulator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.android.ArgumentHelper;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.emulator.process.ProcessCondition;
import de.enough.polish.emulator.process.ProcessWait;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Launches the Android emulator</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Oct , 2007 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class AndroidEmulator 
extends Emulator
{
	private static final byte[] NEWLINE = "\n".getBytes();

	public static final String QVGA_P = "QVGA-P";
	public static final String QVGA_L = "QVGA-L";
	public static final String HVGA = "HVGA";
	public static final String HVGA_P = "HVGA-P";
	public static final String HVGA_L = "HVGA-L";
	public static final String WVGA800 = "WVGA800";
		
	String[] stateArguments;
	ArrayList emulatorArguments;
	String[] waitArguments;
	String[] installArguments;

	private String errorMessage;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, de.enough.polish.Environment)
	 */
	public boolean init(Device dev, EmulatorSetting setting, Environment env) {
		
		ArrayList conditionList = new ArrayList();
		conditionList.add(ArgumentHelper.adb(env) );
		conditionList.add("get-state");
		this.stateArguments = toArray(conditionList);
		
		ArrayList emulatorList = new ArrayList();
		emulatorList.add(ArgumentHelper.emulator(env));
		emulatorList.add("-skin");
		String skin = getSkin(dev);
		//System.out.println("Using skin " + skin);
		emulatorList.add(skin);
		this.emulatorArguments = emulatorList;
		 
		ArrayList waitList = new ArrayList();
		waitList.add(ArgumentHelper.adb(env));
		waitList.add("wait-for-device");
		waitList.add("logcat");
		waitList.add("ActivityManager:D");
		waitList.add("*:S");
		this.waitArguments = toArray(waitList);
		
		ArrayList installList = new ArrayList();
		installList.add(ArgumentHelper.adb(env));
		installList.add("wait-for-device");
		installList.add("install");
		installList.add("-r");
		installList.add(ArgumentHelper.getPackage("apk", env));
		this.installArguments = toArray(installList);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#run()
	 */
	public synchronized void run()
	{
		try {
			// check if an Android Virtual Device (avd) needs to be created:
			Environment env = this.environment;
			String avd = getAVDforIdentifier(this.device.getIdentifier());
			if (ArgumentHelper.isAndroidVersionHigherOrEquals15(env)) {
				String[] args = new String[] {
						ArgumentHelper.android(env),
						"list",
						"avd"
				};
				String[] output = ProcessUtil.toStringArray( args );
				boolean avdExists = false;
				for (int i = 0; i < output.length; i++) {
					String line = output[i];
					if (line.indexOf("Name:") != -1 && line.indexOf(avd) != -1) {
						avdExists = true;
						break;
					}
				}
				if (!avdExists) {
					args = new String[] {
							ArgumentHelper.android(env),
							"create",
							"avd",
							"-n",
							avd,
							"-t",
							ArgumentHelper.getTargetId(env),
							"-s",
							getSkin(this.device)
					};
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec( args, null, null );
					OutputStream out = process.getOutputStream();
					int result = -1;
					try {
						Thread.sleep(500);
						out.write( NEWLINE );
						out.write( NEWLINE );
						out.write( NEWLINE );
						out.write( NEWLINE );
						out.flush();
						result = process.waitFor();
					} catch (InterruptedException e) {
						// ignore
					}
					if (result != 0) {
						System.out.println( this.device.getIdentifier() + ": Failed to create avd with name \"" + avd + "\": result=" + result + ", please create this avd yourself by calling \"${android.home}/tools/emulator create avd --name " + avd + " --target " + ArgumentHelper.getTargetId(env) + "\" or ignore this message when this avd already exists." );
					} else {
						System.out.println(this.device.getIdentifier() + ": Created avd with name \"" + avd + "\"." );
					}
				}
			}
			// If an emulator is not already running ...`
			if(new ProcessCondition(this.stateArguments,".*unknown.*").isMet())
			{
				// Open the emulator
				if (ArgumentHelper.isAndroidVersionHigherOrEquals15(env)) {
					this.emulatorArguments.add(1, "-avd");
					this.emulatorArguments.add(2, avd);
				}
				System.out.println(this.device.getIdentifier() + ": launching emulator:");
				print(this.emulatorArguments);
				ProcessUtil.exec( this.emulatorArguments, this.device.getIdentifier() + ": ", false);
				
				System.out.println(this.device.getIdentifier() + ": Waiting for emulator to start up...");
				
				// Wait for the debug output to flag that
				// the ActivityManager starts running
				// which indicates that the package manager
				// is ready
				new ProcessWait(this.waitArguments, new String[]{ ".*Start running!.*", ".*Stopping service.*"}, 2 * 60 * 1000 );
			} else {
				System.out.println(this.device.getIdentifier() + ": Emulator has been launched already.");
			}

			System.out.println(this.device.getIdentifier() + ": Installing application...");
			
			// Install the application
			this.errorMessage = null;
			int result = ProcessUtil.exec(this.installArguments, this.device.getIdentifier() + ": ", true, 
			new OutputFilter() {
				public void filter(String message, PrintStream output) {
					output.println(message);
					if (message.indexOf("Error") != -1) {
						AndroidEmulator.this.errorMessage = message;
					}
				}
			},  null);
			if(result != 0 || this.errorMessage != null) {
				System.out.println("Arguments were:");
				System.out.println( ProcessUtil.toString(this.installArguments) );
				String message = "Could not install application. The process returned " + result + ". ";
				if (this.errorMessage != null) {
					message += this.errorMessage;
				}
				throw new BuildException(message);
			}
			System.out.println(this.device.getIdentifier() + ": Successfully installed application.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			this.isFinished = true;
		}
	}

	private void print(ArrayList args) {
		for (int i=0; i<args.size(); i++) {
			String arg = (String) args.get(i);
			if (arg.indexOf(' ') != -1) {
				arg = '"' + arg + "\" ";
			} else {
				arg += ' ';
			}
			System.out.print(arg);
		}
		System.out.println();
	}

	private String getAVDforIdentifier(String identifier) {
		identifier = identifier.replace('/', '_');
		identifier = identifier.replace(' ', '_');
		identifier = identifier.replace('.', '_');
		return identifier;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getArguments()
	 */
	public String[] getArguments() {
		return null;
	}
	
	/**
	 * Returns a simple <code>String[]</code> from an <code>ArrayList</code> 
	 * @param list the ArrayList
	 * @return the String[]
	 */
	String[] toArray(ArrayList list)
	{
		return (String[]) list.toArray( new String[ list.size() ] );
	}
	
	/**
	 * Returns the -skin argument to start the emulator in
	 * the resolution of the device 
	 * @param dev the device
	 * @return the identifier for the skin
	 */
	String getSkin(Device dev)
	{
		String skin = dev.getCapability("polish.Emulator.Skin");
		if (skin != null) {
			return skin;
		}
		String screenSize = dev.getCapability("polish.ScreenSize");
		if("240x320".equals(screenSize)) {
			return QVGA_P;
		}
		if("320x240".equals(screenSize)) {
			return QVGA_L;
		}
		if("320x480".equals(screenSize)) {
			return HVGA_P;
		}
		if("480x320".equals(screenSize)) {
			return HVGA_L;
		}
		if("800x480".equals(screenSize)) {
			return WVGA800;
		}
		return HVGA;
	}
}
