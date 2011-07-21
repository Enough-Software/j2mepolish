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

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Variable;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.http.HttpServer;
import de.enough.polish.util.ConvertUtil;
import de.enough.polish.util.PropertyFileMap;
import de.enough.polish.util.StringUtil;

/**
 * <p>
 * Is responsible for configuring and starting any WTK-based emulator.
 * </p>
 * 
 * Notice:
 * If you get the error "There is insufficient storage to install this suite" when you using the WTK version
 * 3.0 or higer. You have to set in install folder of the WTK in the file "/runtimes/cldc-hi-javafx/bin/jwc_properties.ini" the
 * "system.jam_space" property to a higher value.
 * 
 * <p>
 * Copyright Enough Software 2004, 2005
 * </p>
 * 
 * <pre>
 * history
 *        05-Sep-2004 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class WtkEmulator extends Emulator {

	protected String[] arguments;
	protected boolean appendXdeviceArgument;
	protected String[] environmentArguments;
	protected File workingDirectory;
	private boolean wtkNewVersion;
	private File devicesFolder;

	/**
	 * Creates a new emulator
	 */
	public WtkEmulator() {
		super();
		this.appendXdeviceArgument = true;
	}

	/**
	 * Indicates whether this emulator supports a standard preferences file and
	 * the "-Xprefs"-parameter. Subclasses can override this method for
	 * adjustments.
	 * 
	 * @return true by default, subclasses can change this behavior.
	 */
	protected boolean supportsPreferencesFile() {
		//Not supported in the new WTK version
		if(this.wtkNewVersion) {
			return false;
		}
		return true;
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
	 * Indicates whether this emulator supports "-Xheapsize"-parameter.
	 * Subclasses can override this method for adjustments.
	 * <p>
	 * Notice: This is not supported in WTK version 3.0 or higher.
	 * </p>
	 * 
	 * @return true by default, subclasses can change this behavior.
	 */
	protected boolean supportsHeapsize() {
		//Not supported in the new WTK version
		if(this.wtkNewVersion) {
			return false;
		}
		return true;
	}

	/**
	 * Retrieves the folder which contains the emulator skin.
	 * 
	 * @param wtkHome
	 *            the path to the Wireless Toolkit
	 * @param xDevice
	 *            the name of the skin
	 * @return the file which points to the folder containing the skin
	 */
	protected File getEmulatorSkin(String wtkHome, String xDevice) {
		if (wtkHome == null) {
			wtkHome = ".";
		}
		return getEmulatorSkin(new File(wtkHome), xDevice);
	}

	/**
	 * Retrieves the folder which contains the emulator skin.
	 * 
	 * @param wtkHome
	 *            the path to the Wireless Toolkit
	 * @param xDevice
	 *            the name of the skin
	 * @return the file which points to the folder containing the skin
	 */
	protected File getEmulatorSkin(File wtkHome, String xDevice) {
		if (this.wtkNewVersion) {
			if(this.devicesFolder == null) {
				return null;
			}
			File[] fileList = this.devicesFolder.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File file = new File(fileList[i], "properties.xml");
				if (file.exists()) {
					if (parsePropertiesXML(file, xDevice)) {
						return file;
					}
				} else {
					continue;
				}
			}
			return null;

		} else {
			return new File(wtkHome, "wtklib/devices/" + xDevice);
		}

	}

	/**
	 * This method search for a given device in an properties.xml file of a wtk device.
	 * 
	 * @param propertiesFile the properties.xml file
	 * @param xDevice the device you search for
	 * @return true if the given device was found; false if the device was not in the properties.xml file or the xml file was not valid
	 */
	private boolean parsePropertiesXML(File propertiesFile, String xDevice) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(propertiesFile);

			NodeList nodes = doc.getElementsByTagName("void");

			for (int i = 0; i < nodes.getLength(); i++) {

				NamedNodeMap atrributes = nodes.item(i).getAttributes();
				if ("name".equals(atrributes.getNamedItem("property")
						.getNodeValue())) {
					NodeList voidNodes = nodes.item(i).getChildNodes();

					for (int k = 0; k < voidNodes.getLength(); k++) {
						String deviceName = voidNodes.item(k).getTextContent();
						//found the given emulator skin
						if (deviceName != null && xDevice.equals(deviceName))
							return true;
					}

				}

			}

		} catch (ParserConfigurationException e) {
			// do nothing
		} catch (SAXException e) {
			// do nothing
		} catch (IOException e) {
			// do nothing
		}
		return false;
	}

	/**
	 * Retrieves the executable for the given device.
	 * 
	 * @param wtkHome
	 *            the path to the Wireless Toolkit
	 * @param xDevice
	 *            the name of the skin
	 * @param dev
	 *            the device
	 * @param env
	 *            TODO
	 * @return the file which points to the emulator-application
	 */
	protected File getEmulatorExcecutable(String wtkHome, String xDevice,
			Device dev, Environment env) {
		if (wtkHome == null) {
			wtkHome = ".";
		}
		return getEmulatorExcecutable(new File(wtkHome), xDevice, dev, env);
	}

	/**
	 * Retrieves the executable for the given device.
	 * 
	 * @param wtkHome
	 *            the path to the Wireless Toolkit
	 * @param xDevice
	 *            the name of the skin
	 * @param dev
	 *            the device
	 * @param env
	 *            TODO
	 * @return the file which points to the emulator-application
	 */
	protected File getEmulatorExcecutable(File wtkHome, String xDevice, Device dev, Environment env) 
	{
		File executable = null;
		if (File.separatorChar == '/') {
			executable = new File(wtkHome, "bin/emulator");
		} else {
			executable = new File(wtkHome, "bin/emulator.exe");
		}
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
		ArrayList argumentsList = new ArrayList();

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
		String wtkHome = env.getVariable("wtk.home");
		if (xDevice != null) {
			File skinFile = getEmulatorSkin(wtkHome, xDevice);
			if (skinFile == null || !(skinFile.exists())) {
				//Check if the wtk is the new wtk (3.0 and higher)
				if (checkIfNewWtk(wtkHome)) {
					this.wtkNewVersion = true;
					skinFile = getEmulatorSkin(wtkHome, xDevice);
					if (skinFile == null || !skinFile.exists()) {
						System.out.println("Warning: did not find emulator referenced by -Xdevice parameter " + xDevice);
						return false;
					}
				}
			}
		} else {
			String[] skinNames = getEmulatorSkinNames(env);
			File skinFile = null;
			for (int i = 0; i < skinNames.length; i++) {
				System.out.println("New Xdevice "+skinNames[i]);
				xDevice = skinNames[i];
				skinFile = getEmulatorSkin(wtkHome, xDevice);
				
					if (skinFile != null&&!this.wtkNewVersion && !skinFile.exists()) {

						if (checkIfNewWtk(wtkHome)) {
							this.wtkNewVersion = true;

							skinFile = getEmulatorSkin(wtkHome, xDevice);
						}
					}
					if (skinFile != null&&skinFile.exists()) {
						break;
					}
				
				skinFile = null;
			}
			if (skinFile == null) {
				if (checkIfNewWtk(wtkHome)) {
					this.wtkNewVersion = true;
					
					//In the new WTK we have to set a skin
					xDevice = "DefaultCldcPhone1";
					skinFile = getEmulatorSkin(wtkHome, xDevice);
					if (skinFile == null || !skinFile.exists()) {
						System.out.println("Cannot intialize the default skin for the WTK 3.0 and higher");
						return false;
					}
				} else {
					if (skinNames.length > 0) {
						System.out
								.print("Warning: Unable to find emulator for ");
						for (int i = 0; i < skinNames.length; i++) {
							System.out.print(skinNames[i]);
							if (i < skinNames.length - 1) {
								System.out.print(", ");
							}
						}
						System.out.println();
						return false;
					} else {
						System.out
								.println("Warning: found no emulator-skin or -Xdevice-parameter for device ["
										+ dev.getIdentifier()
										+ "], now using the default emulator.");
					}
				}

			}
		}
		//			// test if this emulator exists:
		//			File skinFile = getEmulatorSkin(wtkHome, xDevice);
		//			if (!skinFile.exists()) {
		//				if (xDeviceParameterGiven) {
		//					System.out.println("Warning: unable  to start the emulator: the emulator-skin [" + xDevice + "] for the device [" + dev.getIdentifier() + "] is not installed.");
		//					return false;
		//				} else {
		//					String originalSkin = xDevice;
		//					System.out.println("Info: Emulator [" + skinFile.getAbsolutePath() + "] not found.");
		//					// check if there are other skins given:
		//					int skinNumber = 2;
		//					while (true) {
		//						xDevice = dev.getCapability("polish.Emulator.Skin." + skinNumber);
		//						if (xDevice == null) {
		//							break;
		//						}
		//						skinNumber++;
		//						skinFile = getEmulatorSkin(wtkHome, xDevice);
		//						if (skinFile.exists()) {
		//							break;
		//						}
		//						System.out.println("Info: Emulator [" + skinFile.getAbsolutePath() + "] not found.");
		//					}
		//					if (!skinFile.exists()) {
		//						System.out.println("Warning: unable  to start the emulator: the emulator-skin [" + originalSkin + "] for the device [" + dev.getIdentifier() + "] is not installed.");
		//						return false;
		//					}
		//				}
		//			}
		// okay, skin-file exists:
		//		} else {
		//			System.out.println("Warning: found no emulator-skin or -Xdevice-parameter for device [" + dev.getIdentifier() + "], now using the default emulator.");
		//		}

		// get emulator executable:
		File execFile = getEmulatorExcecutable(wtkHome, xDevice, dev, env);
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

		// add the -Xprefs-parameter:
		if (supportsPreferencesFile()) {
			File preferencesFile = setting.getPreferences();
			boolean usingPreferencesFile = false;
			if (preferencesFile != null) {
				if (preferencesFile.exists()) {
					argumentsList.add("-Xprefs:" + preferencesFile.getAbsolutePath());
					usingPreferencesFile = true;
				} else {
					System.err.println("Warning: unable to use preferences-file: the file ["
									+ preferencesFile.getAbsolutePath()
									+ "] does not exist.");
				}
			}
			if (getParameter("-Xprefs", parameters) != null) {
				usingPreferencesFile = true;
			}
			if (!usingPreferencesFile && setting.writePreferencesFile()) {
				File propertiesFile = getEmulatorPropertiesFile(env);
				PropertyFileMap emulatorPropertiesMap = new PropertyFileMap();
				if (propertiesFile != null && propertiesFile.exists()) {
					try {
						emulatorPropertiesMap.readFile(propertiesFile);
						//FileUtil.readPropertiesFile(propertiesFile, ':', '#', emulatorPropertiesMap, false );	
					} catch (IOException e) {
						e.printStackTrace();
						throw new BuildException(
								"Unable to read the default emulator properties from ["
										+ propertiesFile.getAbsolutePath()
										+ "]: "
										+ e.toString()
										+ " - please make sure to use the WTK/2.2 or higher.",
								e);
					}
				}

				addProperties(setting, emulatorPropertiesMap);
				preferencesFile = new File(dev.getBaseDir() + File.separatorChar + "emulator.properties");
				try {
					emulatorPropertiesMap.writeFile(preferencesFile);
					//FileUtil.writePropertiesFile(preferences, emulatorPropertiesMap);
					argumentsList.add("-Xprefs:" + preferencesFile.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Unable to set preferences-file for emulator: " + e.toString());
				}
			} else if (setting.writePreferencesFile()) {
				System.out.println("Warning: unable to enable any profiler/monitor, since a preferences-file is used.");
			}
		} // the device supports the -Xprefs parameter.

		if (supportsSecurityDomain()) {
			// add the -Xdomain-parameter:
			String securityDomain = setting.getSecurityDomain();
			if (securityDomain != null) {
				argumentsList.add("-Xdomain:" + securityDomain);
			}
		}

		if (supportsHeapsize()) {
			// add the -Xheapsize-parameter:
			String heapSizeStr = dev.getCapability("polish.HeapSize");
			if ((heapSizeStr != null) && !("dynamic".equals(heapSizeStr))) {
				long bytes = ConvertUtil.convertToBytes(heapSizeStr);
				argumentsList.add("-Xheapsize:" + bytes);
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

	/**
	 * This method checks if the WTK is version 3.0 or higher
	 * @param wtkHome the folder where the wtk should be located
	 * @return true if it the new WTK version; false if is not the new WTK version or the file is not valid
	 */
	private boolean checkIfNewWtk(String wtkHome) {
		if (wtkHome == null) {
			wtkHome = ".";
		}
		File devicesFolderTemp;
		if (new File(wtkHome + "/toolkit-lib/process/device-manager/").exists()) {
			String userhome = System.getProperty("user.home");
			//WTK is running on Windows
			devicesFolderTemp = new File(userhome + "/javame-sdk/3.0/work/");
			if(devicesFolderTemp.exists()){
				this.devicesFolder = devicesFolderTemp;
				return true;
			}
			//WTK is running on MacOS
			devicesFolderTemp = new File(userhome,"/Library/Application Support/javame-sdk/3.0/work/");
			if(devicesFolderTemp.exists()){
				this.devicesFolder = devicesFolderTemp;
				return true;
			}else{
				this.devicesFolder=null;
				return false;
			}
			
		}
		return false;
	}

	protected void addProperties(EmulatorSetting setting, PropertyFileMap emulatorPropertiesMap) {
		// now write a preferences-file:
		emulatorPropertiesMap.put("kvem.memory.monitor.enable", ""
				+ setting.enableMemoryMonitor());
		emulatorPropertiesMap.put("kvem.profiler.enable", ""
				+ setting.enableProfiler());
		String enableNetworkMonitor = Boolean.toString( setting.enableNetworkMonitor() );
		emulatorPropertiesMap.put("kvem.netmon.enable", enableNetworkMonitor);
		emulatorPropertiesMap.put("kvem.netmon.comm.enable",
				enableNetworkMonitor);
		emulatorPropertiesMap.put("kvem.netmon.datagram.enable",
				enableNetworkMonitor);
		emulatorPropertiesMap.put("kvem.netmon.http.enable",
				enableNetworkMonitor);
		emulatorPropertiesMap.put("kvem.netmon.https.enable",
				enableNetworkMonitor);
		emulatorPropertiesMap.put("kvem.netmon.socket.enable",
				enableNetworkMonitor);
		emulatorPropertiesMap.put("kvem.netmon.ssl.enable",
				enableNetworkMonitor);

		emulatorPropertiesMap.put("bluetooth.connected.devices.max", "7");
		emulatorPropertiesMap.put("bluetooth.device.authentication", "on");
		emulatorPropertiesMap.put("bluetooth.device.authorization", "on");
		emulatorPropertiesMap.put("bluetooth.device.discovery.enable", "true");
		emulatorPropertiesMap.put("bluetooth.device.discovery.timeout", "10000");
		emulatorPropertiesMap.put("bluetooth.device.encryption", "on");
		emulatorPropertiesMap.put("bluetooth.device.friendlyName", "WirelessToolkit");
		emulatorPropertiesMap.put("bluetooth.enable", "true");
		emulatorPropertiesMap.put("bluetooth.l2cap.receiveMTU.max", "512");
		emulatorPropertiesMap.put("bluetooth.sd.attr.retrievable.max", "10");
		emulatorPropertiesMap.put("bluetooth.sd.trans.max", "5");
		emulatorPropertiesMap.put("file.extension", "jad");
		emulatorPropertiesMap.put("heap.size", "");
		emulatorPropertiesMap.put("http.proxyHost", "");
		emulatorPropertiesMap.put("http.proxyPort", "");
		emulatorPropertiesMap.put("http.version", "HTTP/1.1");
		emulatorPropertiesMap.put("https.proxyHost", "");
		emulatorPropertiesMap.put("https.proxyPort", "");
		emulatorPropertiesMap.put("irdaobex.discoveryTimeout", "10000");
		emulatorPropertiesMap.put("irdaobex.packetLength", "4096");
		emulatorPropertiesMap.put("jammode", "");
		emulatorPropertiesMap.put("kvem.api.exclude", "");
		emulatorPropertiesMap.put("kvem.device", "DefaultColorPhone");
		emulatorPropertiesMap.put("kvem.netmon.autoclose", "false");
		emulatorPropertiesMap.put("kvem.netmon.filter_file_name", "netmon_filter.dat");
		emulatorPropertiesMap.put("kvem.netmon.fixed_font_name", "Courier New");
		emulatorPropertiesMap.put("kvem.netmon.fixed_font_size", "12");
		emulatorPropertiesMap.put("kvem.netmon.variable_font_name", "Arial");
		emulatorPropertiesMap.put("kvem.netmon.variable_font_size", "14");
		emulatorPropertiesMap.put("kvem.profiler.outfile", "");
		emulatorPropertiesMap.put("kvem.profiler.showsystem", "false");
		if ("all".equals(setting.getTrace())) {
			emulatorPropertiesMap.put("kvem.trace.all", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.all", "false");
		}
		if (setting.traceIncludes("allocation")) {
			emulatorPropertiesMap.put("kvem.trace.allocation", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.allocation", "false");
		}
		if (setting.traceIncludes("bytecodes")) {
			emulatorPropertiesMap.put("kvem.trace.bytecodes", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.bytecodes", "false");
		}
		if (setting.traceIncludes("calls")) {
			emulatorPropertiesMap.put("kvem.trace.calls", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.calls", "false");
		}
		if (setting.traceIncludes("calls.verbose")) {
			emulatorPropertiesMap.put("kvem.trace.calls.verbose", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.calls.verbose", "false");
		}
		if (setting.traceIncludes("class")) {
			emulatorPropertiesMap.put("kvem.trace.class", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.class", "false");
		}
		if (setting.traceIncludes("class.verbose")) {
			emulatorPropertiesMap.put("kvem.trace.class.verbose", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.class.verbose", "false");
		}
		if (setting.traceIncludes("events")) {
			emulatorPropertiesMap.put("kvem.trace.events", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.events", "false");
		}
		if (setting.traceIncludes("exceptions")) {
			emulatorPropertiesMap.put("kvem.trace.exceptions", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.exceptions", "false");
		}
		if (setting.traceIncludes("frames")) {
			emulatorPropertiesMap.put("kvem.trace.frames", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.frames", "false");
		}
		if (setting.traceIncludes("gc")) {
			emulatorPropertiesMap.put("kvem.trace.gc", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.gc", "false");
		}
		if (setting.traceIncludes("gc.verbose")) {
			emulatorPropertiesMap.put("kvem.trace.gc.verbose", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.gc.verbose", "false");
		}
		if (setting.traceIncludes("monitors")) {
			emulatorPropertiesMap.put("kvem.trace.monitors", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.monitors", "false");
		}
		if (setting.traceIncludes("networking")) {
			emulatorPropertiesMap.put("kvem.trace.networking", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.networking", "false");
		}
		if (setting.traceIncludes("stackchunks")) {
			emulatorPropertiesMap.put("kvem.trace.stackchunks", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.stackchunks", "false");
		}
		if (setting.traceIncludes("stackmaps")) {
			emulatorPropertiesMap.put("kvem.trace.stackmaps", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.stackmaps", "false");
		}
		if (setting.traceIncludes("threading")) {
			emulatorPropertiesMap.put("kvem.trace.threading", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.threading", "false");
		}
		if (setting.traceIncludes("verifier")) {
			emulatorPropertiesMap.put("kvem.trace.verifier", "true");
		} else {
			emulatorPropertiesMap.put("kvem.trace.verifier", "false");
		}

		emulatorPropertiesMap.put("mm.control.capture", "true");
		emulatorPropertiesMap.put("mm.control.midi", "true");
		emulatorPropertiesMap.put("mm.control.mixing", "true");
		emulatorPropertiesMap.put("mm.control.record", "true");
		emulatorPropertiesMap.put("mm.control.volume", "true");
		emulatorPropertiesMap.put("mm.format.midi", "true");
		emulatorPropertiesMap.put("mm.format.video", "true");
		emulatorPropertiesMap.put("mm.format.wav", "true");
		emulatorPropertiesMap.put("netspeed.bitpersecond", "1200");
		emulatorPropertiesMap.put("netspeed.enableSpeedEmulation", "false");
		emulatorPropertiesMap.put("prng.secure", "false");
		emulatorPropertiesMap.put("screen.graphicsLatency", "0");
		emulatorPropertiesMap.put("screen.refresh.mode", "");
		emulatorPropertiesMap.put("screen.refresh.rate", "30");
		if (setting.getSecurityDomain() != null) {
			emulatorPropertiesMap.put("security.domain", setting.getSecurityDomain());
		} else {
			emulatorPropertiesMap.put("security.domain", "untrusted");
		}
		emulatorPropertiesMap.put("storage.root", "");
		emulatorPropertiesMap.put("storage.size", "");
		emulatorPropertiesMap.put("vmspeed.bytecodespermilli", "100");
		emulatorPropertiesMap.put("vmspeed.enableEmulation", "false");
		emulatorPropertiesMap.put("vmspeed.range", "100,1000");
		emulatorPropertiesMap.put("wma.client.phoneNumber", "");
		emulatorPropertiesMap.put("wma.server.deliveryDelayMS", "");
		emulatorPropertiesMap.put("wma.server.firstAssignedPhoneNumber", "+5550000");
		emulatorPropertiesMap.put("wma.server.percentFragmentLoss", "0");
		emulatorPropertiesMap.put("wma.smsc.phoneNumber", "+1234567890");
	}

	/**
	 * Retrieves the file containing the default properties for the emulator.
	 * 
	 * @param env
	 *            the environment that helps to resolve files
	 *            (env.resolveFile(String)).
	 * @return by default ${wtk.home}/wtklib/emulator.properties is returned
	 */
	protected File getEmulatorPropertiesFile(Environment env) {
		return env.resolveFile("${wtk.home}/wtklib/emulator.properties");
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
