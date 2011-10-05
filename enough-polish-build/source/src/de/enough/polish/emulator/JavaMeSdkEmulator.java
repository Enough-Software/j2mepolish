/*
 * Created on 05-Sep-2004 at 15:35:23.
 * 
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
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Variable;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.http.HttpServer;
import de.enough.polish.util.StringUtil;

/**
 * <p>
 * Configures and starts the Java ME SDK emulator 3.0 and above. 
 * </p>
 * <p>
 * Copyright Enough Software 2011
 * </p>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class JavaMeSdkEmulator extends Emulator {

	protected String[] arguments;
	protected boolean appendXdeviceArgument;
	protected String[] environmentArguments;
	protected File workingDirectory;
	private File devicesFolder;

	/**
	 * Creates a new emulator
	 */
	public JavaMeSdkEmulator() {
		super();
		this.appendXdeviceArgument = true;
	}


	/**
	 * Indicates whether this emulator supports "-Xdomain"-parameter. Subclasses
	 * can override this method for adjustments.
	 * 
	 * @return true by default, subclasses can change this behavior.
	 */
	protected boolean supportsSecurityDomain() {
		return true;
	}


	/**
	 * Retrieves the executable for the given device.
	 * 
	 * @param sdkHome
	 *            the path to the Java ME SDK
	 * @param xDevice
	 *            the name of the skin
	 * @param dev
	 *            the device
	 * @param env
	 *            environment
	 * @return the file which points to the emulator-application
	 */
	protected File getEmulatorExcecutable(String sdkHome, String xDevice,
			Device dev, Environment env) {
		if (sdkHome == null) {
			sdkHome = ".";
		}
		return getEmulatorExcecutable(new File(sdkHome), xDevice, dev, env);
	}

	/**
	 * Retrieves the executable for the given device.
	 * 
	 * @param sdkHome
	 *            the path to the Java ME SDK
	 * @param xDevice
	 *            the name of the skin
	 * @param dev
	 *            the device
	 * @param env
	 *            environment
	 * @return the file which points to the emulator-application
	 */
	protected File getEmulatorExcecutable(File sdkHome, String xDevice, Device dev, Environment env) 
	{
		File executable = null;
		if (File.separatorChar == '/') {
			executable = new File(sdkHome, "bin/emulator");
		} else {
			executable = new File(sdkHome, "bin/emulator.exe");
		}
		if (!executable.exists()) {
			// on Mac we have a more nested directory structure:
			executable = new File(sdkHome, "Contents/Resources/bin/emulator");
		}
		System.out.println("found emulator executable: " + executable.exists() + " for " + executable.getAbsolutePath());
		return executable;
	}

	protected String[] getEmulatorSkinNames(Environment env) {
		String skinNamesStr = env.getVariable("polish.build.Emulator.Skin");
		if (skinNamesStr == null) {
			skinNamesStr = env.getVariable("polish.Emulator.Skin");
		}
		if (skinNamesStr == null) {
			return new String[0];
		} else {
			StringBuffer buffer = new StringBuffer(skinNamesStr);
			// add backwards compatible old skin definitions:
			int i = 1;
			String skin = null;
			do {
				i++;
				skin = env.getVariable("polish.Emulator.Skin." + i);
				if (skin != null) {
					buffer.append(':').append(skin);
				}
			} while (skin != null);
			// get all supported skins:
			return StringUtil.splitAndTrim(buffer.toString(), ':');
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ant.emulator.Emulator#init(de.enough.polish.Device,
	 * de.enough.polish.ant.emulator.EmulatorSetting,
	 * de.enough.polish.Environment)
	 */
	public boolean init(Device dev, EmulatorSetting setting, Environment env) {
		// okay, now create the arguments:
		ArrayList<String> argumentsList = new ArrayList<String>();

		Variable[] parameters = getParameters(setting, env);

		String xDevice = getParameterValue("-Xdevice", parameters);
		boolean xDeviceParameterGiven = true;
		//		if (xDevice == null) {
		//			xDeviceParameterGiven = false;
		//			xDevice = dev.getCapability("polish.build.Emulator.Skin");
		//			if (xDevice == null) {
		//				xDevice = dev.getCapability("polish.Emulator.Skin");
		//			}
		//		}
		String sdkHome = env.getVariable("javamesdk.home");
		if (xDevice == null) {
			String[] skinNames = getEmulatorSkinNames(env);
			if (skinNames.length > 0) {
				xDevice=skinNames[0];
			} else {
				xDevice = "DefaultFxPhone1";
			}
		}

		// get emulator executable:
		File execFile = getEmulatorExcecutable(sdkHome, xDevice, dev, env);
		String executable = execFile.getAbsolutePath();
		if (!execFile.exists()) {
			System.out.println("Warning: unable to find the emulator [" + executable + "].");
			return false;
		}

		argumentsList.add(executable);

		if (this.appendXdeviceArgument && (xDevice != null)) {
			argumentsList.add("-Xdevice:" + xDevice);
		}

		// add -Xdescriptor-parameter:
		if (setting != null && setting.isTransient()) {
			int port = setting.getTransientPort();
			String transientParam = "-Xjam:transient=http://127.0.0.1:" + port
					+ "/" + env.getVariable("polish.jadName");
			//System.out.println("transient param: " + transientParam );
			argumentsList.add(transientParam);
			// fire up HttpServer:
			File dir = (new File(env.getVariable("polish.jadPath")))
					.getParentFile();
			HttpServer httpServer = new HttpServer(port, dir);
			httpServer.start();
		} else {
			argumentsList.add("-Xdescriptor:" + env.getVariable("polish.jadPath"));
		}

		// add the -Xverbose-parameter:
		String trace = setting.getTrace();
		if (trace != null) {
			argumentsList.add("-Xverbose:" + trace);
		}


		if (supportsSecurityDomain()) {
			// add the -Xdomain-parameter:
			String securityDomain = setting.getSecurityDomain();
			if (securityDomain != null) {
				argumentsList.add("-Xdomain:" + securityDomain);
			}
		}

		//now add other user-defined parameters:
		for (int i = 0; i < parameters.length; i++) {
			Variable parameter = parameters[i];
			String name = parameter.getName();
			if (xDeviceParameterGiven && "-xDevice".equals(parameter.getName())) {
				continue;
			}
			String value = parameter.getValue();
			if (value.length() > 0) {
				if ((name.charAt(0) == '-')
						&& ((name.charAt(1) == 'X') || (name.charAt(1) == 'D'))) {
					argumentsList.add(name + ":" + value);
				} else {
					argumentsList.add(name + " " + value);
				}
			} else {
				argumentsList.add(name);
			}
		}

		this.arguments = (String[]) argumentsList
				.toArray(new String[argumentsList.size()]);
		return true;
	}


	protected File getExecutionDir() {
		return this.workingDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.emulator.Emulator#getArguments()
	 */
	public String[] getArguments() {
		return this.arguments;
	}

}
