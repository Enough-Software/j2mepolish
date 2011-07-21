/*
 * Created on 28-Jan-2004 at 23:28:45.
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
package de.enough.polish.devices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.ant.requirements.VariableDefinedRequirement;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Manages all known J2ME devices.</p>
 * <p>The devices are defined in the devices.xml file</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        28-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceManager {

	private Device[] devices;
	private final ArrayList devicesList;
	private final HashMap devicesByIdentifier;
	private HashMap devicesByUserAgent;
	private final VendorManager vendorManager;
	private List devicesXmlList;
	private List currentDevicesXmlList;
	private ConfigurationManager configurationManager;
	private PlatformManager platformManager;
	private DeviceGroupManager groupManager;
	private LibraryManager libraryManager;
	private CapabilityManager capabilityManager;
	private HashMap allowedDuplicatesByIdentifier;
	private Map unresolvableUserAgents;

	/**
	 * Creates a new device manager with the given devices.xml file.
	 * 
	 * @param platformManager
	 * @param configurationManager
	 * @param vendorManager The manager of the device-manufacturers
	 * @param groupManager The manager for device-groups.
	 * @param libraryManager the manager for device-specific APIs
	 * @param capabilityManager
	 * @param devicesIS the InputStream containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	public DeviceManager( ConfigurationManager configurationManager, PlatformManager platformManager, VendorManager vendorManager, DeviceGroupManager groupManager, LibraryManager libraryManager, CapabilityManager capabilityManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.vendorManager = vendorManager;

		this.devicesByIdentifier = new HashMap();        
		this.devicesList = new ArrayList();
		loadDevices( configurationManager, platformManager, vendorManager, groupManager, libraryManager, capabilityManager, devicesIS );
		devicesIS.close();
	}
	
	/**
	 * Creates a new device manager with the given devices.xml file.
	 * 
	 * @param vendorManager The manager of the device-manufacturers
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	public DeviceManager( VendorManager vendorManager ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.devicesByIdentifier = new HashMap();
		this.devicesList = new ArrayList();
		this.vendorManager = vendorManager;
	}

	/**
	 * Loads the device definitions.
	 * 
	 * @param vendManager The manager of the device-manufacturers
	 * @param grManager The manager for device-groups.
	 * @param libManager the manager for API libraries
	 * @param devicesIS the InputStream containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	protected void loadDevices(  ConfigurationManager configManager, PlatformManager platfManager, VendorManager vendManager, DeviceGroupManager grManager, LibraryManager libManager, CapabilityManager capManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		loadDevices( null, configManager, platfManager, vendManager, grManager, libManager, capManager, devicesIS);
	}
	
	/**
	 * Loads the device definitions.
	 * @param identifierList a list of identifiers that should be loaded, when null all found devices are loaded
	 * @param vendManager The manager of the device-manufacturers
	 * @param grManager The manager for device-groups.
	 * @param libManager the manager for API libraries
	 * @param devicesIS the InputStream containing the device definitions.
	 * 			Usally this is the devices.xml file in the current directory.
	 * @throws JDOMException when there are syntax errors in devices.xml
	 * @throws IOException when devices.xml could not be read
	 * @throws InvalidComponentException when a device definition has errors
	 */
	protected void loadDevices(  List identifierList, ConfigurationManager configManager, PlatformManager platfManager, VendorManager vendManager, DeviceGroupManager grManager, LibraryManager libManager, CapabilityManager capManager, InputStream devicesIS ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.configurationManager = configManager;
		this.platformManager = platfManager;
		this.groupManager = grManager;
		this.libraryManager = libManager;
		this.capabilityManager = capManager;
		
		if (devicesIS == null) {
			throw new BuildException("Unable to load devices.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( devicesIS );
		
		HashMap devicesMap = this.devicesByIdentifier;
		List xmlList = document.getRootElement().getChildren();
		if (identifierList != null) {
			Object[] identifiers = identifierList.toArray();
			for (int i = 0; i < identifiers.length; i++)
			{
				String identifier = (String) identifiers[i];
				identifierList.set(i, identifier.toLowerCase() );
				//System.out.println("requested=" + identifier);
			}
			//System.out.println("loading " + identifierList.size() + " devices...");
			// optimized loading: load only devices of the specified identifiers.
			// This drastically improves loadtime but produces overhead for cases
			// when a desired device contains a parent device that is not in the list.
			// To workaround this problem, the getDevice( String identifier ) method
			// can load devices as well, if required.
			if (this.devicesXmlList == null ) {
				// this contains the devices.xml list when only devices with identifiers should be loaded:
				this.devicesXmlList = xmlList;
			}
			this.currentDevicesXmlList = xmlList;
		}
		loadDevices(identifierList, configManager, platfManager, vendManager, grManager, libManager, capManager, devicesMap, xmlList);
		this.devices = (Device[]) this.devicesList.toArray( new Device[ this.devicesList.size()]);
	}

	/**
	 * Loads the requested devices from the given xmlList
	 * 
	 * @param identifierList the requested identifiers in lower case, if null all devices are loaded 
	 * @param configuratioManager
	 * @param platfManager
	 * @param vendManager
	 * @param grManager
	 * @param libManager
	 * @param capManager
	 * @param devicesMap
	 * @param xmlList
	 * @throws InvalidComponentException
	 */
	private void loadDevices(List identifierList, ConfigurationManager configuratioManager, PlatformManager platfManager, VendorManager vendManager, DeviceGroupManager grManager, LibraryManager libManager, CapabilityManager capManager, HashMap devicesMap, List xmlList) throws InvalidComponentException {
		String lastKnownWorkingDevice = null;
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			String identifierStr = definition.getChildTextTrim( "identifier");
			if (identifierStr == null) {
				throw new InvalidComponentException("Unable to initialise device. Every device needs to define its <identifier> element. Check your \"devices.xml\" file. The last known correct definition was " + lastKnownWorkingDevice + ".");
			}
			lastKnownWorkingDevice = identifierStr;
			// one xml definition can contain several device-definitions,
			// e.g. <identifier>Nokia/3650, Nokia/5550</identifier>
			String[] identifiers = StringUtil.splitAndTrim(identifierStr,',');
			for (int i = 0; i < identifiers.length; i++) {
				String identifier = identifiers[i];
				String identifierLowerCase = identifier.toLowerCase();
				if (identifierList != null) {
					boolean isRequiredIdentifier = identifierList.remove( identifier ) || identifierList.remove( identifierLowerCase);
					if (!isRequiredIdentifier) {
						// skip the loading of devices which are not needed. When a device later onwards
						// refers to a parent device, this will be loaded in the getDevice( String identifier ) method.
						continue;
					}
//					System.out.println("found required identifier: " + identifier );
				}
				if (devicesMap.get( identifier ) != null) {
					if (this.allowedDuplicatesByIdentifier != null && this.allowedDuplicatesByIdentifier.get(identifier) != null ) {
						continue;
					}
					throw new InvalidComponentException("The device [" + identifier + "] has been defined twice in [devices.xml]. Please remove one of those definitions.");
				}
				String[] chunks = StringUtil.split( identifier, '/');
				if (chunks.length != 2) {
					throw new InvalidComponentException("The device [" + identifier + "] has an invalid [identifier] - every identifier needs to consists of the vendor and the name, e.g. \"Nokia/6600\". Please check you [devices.xml].");
				}
				String vendorName = chunks[0];
				String deviceName = chunks[1];
				Vendor vendor = vendManager.getVendor( vendorName );
				if (vendor == null) {
					throw new InvalidComponentException("Invalid device-specification in [devices.xml]: Please specify the vendor [" + vendorName + "] in the file [vendors.xml].");
				}
				Device device = new Device( configuratioManager, platfManager, definition, identifier, deviceName, vendor, grManager, libManager, this, capManager );
				devicesMap.put( identifier, device );
				devicesMap.put( identifierLowerCase, device );
				this.devicesList.add( device );
				if (identifierList != null && identifierList.size() == 0) {
//					System.out.println("done loading <identifier> devices");
					break;
				}

			}
		}
	}

	/**
	 * Retrieves all found device definitions.
	 * 
	 * @return the device definitions found in the devices.xml file.
	 */
	public Device[] getDevices() {
		if (this.devices == null) {
			this.devices = (Device[]) this.devicesList.toArray( new Device[ this.devicesList.size()]);
		}
		return this.devices;
	}

    public void addDevice(Device device) {
        Device[] newDevices = new Device[this.devices.length+1];
        System.arraycopy(this.devices,0,newDevices,0,this.devices.length);
        newDevices[this.devices.length] = device;
        this.devices = newDevices;
        this.devicesByIdentifier.put(device.getIdentifier(),device);
    }
    
	/**
	 * Retrieves a single device.
	 * 
	 *@param identifier the identifier of the device, eg Nokia/6600
	 *@return the device or null when it is not known.
	 */
	public Device getDevice(String identifier) {
		Device device = (Device) this.devicesByIdentifier.get( identifier );
		if (device == null) {
			device = (Device) this.devicesByIdentifier.get( identifier.toLowerCase() );
		}
		// when devices are only loaded for specific identifiers, 
		// J2ME Polish might need to load parent devices later onwards:
		if (device == null && this.devicesXmlList != null) {
			List identifiers = new ArrayList();
			identifiers.add(identifier);
			if (this.allowedDuplicatesByIdentifier == null) {
				this.allowedDuplicatesByIdentifier = new HashMap();
			}
			this.allowedDuplicatesByIdentifier.put( identifier, Boolean.TRUE );
			try {
				loadDevices(identifiers, this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, this.devicesByIdentifier, this.devicesXmlList);
				device = (Device) this.devicesByIdentifier.get( identifier );
				if (device == null && this.currentDevicesXmlList != this.devicesXmlList) {
					loadDevices(identifiers, this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, this.devicesByIdentifier, this.currentDevicesXmlList );
					device = (Device) this.devicesByIdentifier.get( identifier );
					if (device == null) {
						device = (Device) this.devicesByIdentifier.get( identifier.toLowerCase() );
					}
				}
			} catch (InvalidComponentException e) {
				e.printStackTrace();
			}
		}
		return device;
	}
    
    /**
     * This class tries to guess the device identifier by analyzing the fuzzy string
     * and searches for a vendor and a model. The algorithm is rudimentary but a start.
     * It checks if the fuzzy name is a regular identifer. After this the string is split into
     * a vendor and model component. The vendor part is matched against all vendor aliases.
     * @param fuzzyName
     * @return the device or null when it is not known.
     */
    public Device getDeviceByFuzzyName(String fuzzyName) {
        // Try the fuzzy name as normal identifier.
        Device device;
        device = getDevice(fuzzyName);
        if(device != null) {
            return device;
        }
        
        String vendorString;
        String modelString;
        
        String[] components = fuzzyName.split("/");
        if(components.length != 2) {
            return null;
        }
        vendorString = components[0];
        modelString = components[1];
        
        Vendor vendor = this.vendorManager.getVendor(vendorString);
        if(vendor == null) {
            return null;
        }
        device = getDevice(vendor.getIdentifier()+"/"+modelString);
        return device;
    }
	
    /**
     * Searches for a device with a given user agent. 
     * If it is not found it an algorithm will try to resolve the user agent automatically
     * @param userAgent the user agent
     * @return the device corresponding to the given userAgent or null.
     */
    public Device getDeviceByUserAgent(String userAgent) {
    	return getDeviceByUserAgent( userAgent, null );
    }
    /**
     * Searches for a device with a given user agent. 
     * If it is not found it an algorithm will try to resolve the user agent automatically
     * @param userAgent the user agent
     * @param userAgentStorage the user agent storage that stores new resolved user agents
     * @return the device corresponding to the given userAgent or null.
     */
    public Device getDeviceByUserAgent(String userAgent, UserAgentStorage userAgentStorage ) {
        if (this.devicesByUserAgent == null) {
            initializeUserAgentMapping();
        }
        Device device = (Device) this.devicesByUserAgent.get(userAgent);
//        Object device = null;
//        int userAgentLength = userAgent.length();
//        String subString;
//        for(int i = userAgentLength; i > 1; i--) {
//            subString = userAgent.substring(0,i);
//            device = this.devicesByUserAgent.get(subString);
//            if(device != null) {
//                break;
//            }
//        }
        if (device == null) {
        	if (this.unresolvableUserAgents.get(userAgent) != null) {
        		// already tried to resolve this device, do not retry:
        		return null;
        	}
        	//System.err.println("Warning: user agent [" + userAgent + "] is not registered.");
        	int index;
        	if (userAgent.startsWith("Nokia")) {
        		device = resolveNokiaUserAgent( "Nokia", userAgent );
         	} else if (userAgent.startsWith("SonyEricsson")) { 
         		device =  resolveUserAgent("Sony-Ericsson", "SonyEricsson", userAgent, false );
         	} else if (userAgent.startsWith("MOT-")) {
         		device =  resolveMotorolaUserAgent( "MOT-", userAgent );
         	} else if (userAgent.startsWith("MOTO")) {
         		device =  resolveMotorolaUserAgent( "MOTO", userAgent );
         	} else if (userAgent.startsWith("SAMSUNG-") || userAgent.startsWith("Samsung-")) { 
         		device =  resolveUserAgent("Samsung", "SAMSUNG-", userAgent, false);
         	} else if (userAgent.startsWith("LG-") ) { 
         		device =  resolveUserAgent("LG", "LG-", userAgent, false);
         	} else if (userAgent.startsWith("LG/") ) { 
         		device =  resolveUserAgent("LG", "LG/", userAgent, false);
         	} else if (userAgent.startsWith("LGE-") ) { 
         		device =  resolveUserAgent("LG", "LGE-", userAgent, false);
         	} else if (userAgent.startsWith("LG") ) { 
         		device =  resolveUserAgent("LG", "LG", userAgent, false);
         	} else if (userAgent.startsWith("SAGEM-") ) { 
         		device =  resolveUserAgent("Sagem", "SAGEM-", userAgent, true);
         	} else if (userAgent.startsWith("SIE-") ) { 
         		device =  resolveUserAgent("Siemens", "SIE-", userAgent, true);
         	} else if ( userAgent.startsWith("BlackBerry/")) {
         		device =  resolveUserAgent("BlackBerry", "BlackBerry/", userAgent, false);
         	} else if ( (index=userAgent.indexOf("Nokia")) != -1) {
         		String userAgentSubString = userAgent.substring(index);
         		device =  resolveUserAgent("Nokia", "Nokia", userAgentSubString, true);
         	} else if ( (index=userAgent.indexOf("BlackBerry")) != -1) {
         		String userAgentSubString = userAgent.substring(index);
         		device =  resolveUserAgent("BlackBerry", "BlackBerry", userAgentSubString, false);
         	} else if ( (index=userAgent.indexOf("Samsung")) != -1) {
         		String userAgentSubString = userAgent.substring(index);
         		device =  resolveUserAgent("Samsung", "Samsung", userAgentSubString, false);
         	}
        	
        	if (device != null) {
        		device.addCapability("wap.UserAgent", userAgent);
				this.devicesByUserAgent.put(userAgent, device);
				if (userAgentStorage != null) {
					userAgentStorage.notifyDeviceResolved(userAgent, device);
				}
        	} else {
        		this.unresolvableUserAgents.put( userAgent, Boolean.TRUE );
				if (userAgentStorage != null) {
					userAgentStorage.notifyDeviceUnresolved(userAgent);
				}
        	}
        }
        return device;
    }
    

	/**
	 * Resolves a Nolia user agent.
	 * Motorola device definitions often start with RAZR etc, which might not need to be used in J2ME Polish.
	 * @param userAgent the full user agent
	 * @return the found Motorola device, if any
	 */
	private Device resolveNokiaUserAgent(String userAgentVendor, String userAgent)
	{
		String vendor = "Nokia";
		String deviceName = resolveDeviceName(userAgentVendor, userAgent, true);
		Device foundDevice = getDevice( vendor + "/" + deviceName);
		if (foundDevice == null && deviceName.length() > 4) {
			// Nokia device names consist usually of 4 characters:
			StringBuffer buffer = new StringBuffer( deviceName.length() );
			boolean lastCharWasNumber = false;
			for (int i=0; i<deviceName.length(); i++) {
				char c = deviceName.charAt(i);
				if (Character.isDigit(c)) {
					buffer.append(c);
					lastCharWasNumber = true;
				} else if (lastCharWasNumber) {
					break;
				}
			}
			foundDevice = getDevice( vendor + "/" + buffer.toString() );
		}
		return foundDevice;
	}


	/**
	 * Resolves a Motorola user agent.
	 * Motorola device definitions often start with RAZR etc, which might not need to be used in J2ME Polish.
	 * @param userAgent the full user agent
	 * @return the found Motorola device, if any
	 */
	private Device resolveMotorolaUserAgent(String userAgentVendor, String userAgent)
	{
		String vendor = "Motorola";
		String deviceName = resolveDeviceName(userAgentVendor, userAgent, false);
		if ("SLVR".equals(deviceName)) {
			int startIndex = userAgent.indexOf("SLVR");
			if ( (userAgent.length() > (startIndex + "SLVR".length() + 2))
					&& (userAgent.charAt( startIndex + "SLVR".length()) == ' ')
			) {
				String realDeviceName = resolveDeviceName("SLVR", userAgent.substring(startIndex), false );
				Device device = getDevice( vendor + "/" + realDeviceName );
				if (device != null) {
					return device;
				}
			}	
		}
 		Device foundDevice = getDevice( vendor + "/" + deviceName);
		if (foundDevice == null) {
			if (deviceName.startsWith("RAZR")) {
				if (deviceName.length() > "RAZR".length()) {
					deviceName = deviceName.substring("RAZR".length());
				} else {
					deviceName = resolveDeviceName("RAZR", userAgent.substring( userAgent.indexOf("RAZR")), false);
				}
				foundDevice = getDevice( vendor + "/" + deviceName);
			} else if (deviceName.startsWith("MOTOROKR")) {
				if (deviceName.length() > "MOTOROKR".length()) {
					deviceName = deviceName.substring("MOTOROKR".length());
				} else {
					deviceName = resolveDeviceName("MOTOROKR", userAgent.substring( userAgent.indexOf("MOTOROKR")), false);
				}
				foundDevice = getDevice( vendor + "/" + deviceName);
			} else if (deviceName.startsWith("ROKR")) {
				if (deviceName.length() > "ROKR".length()) {
					deviceName = deviceName.substring("ROKR".length());
				} else {
					deviceName = resolveDeviceName("ROKR", userAgent.substring( userAgent.indexOf("ROKR")), false);
				}
				foundDevice = getDevice( vendor + "/" + deviceName);				
			} else if (deviceName.toLowerCase().startsWith("motorola")) {
				if (deviceName.length() > "motorola".length()) {
					deviceName = deviceName.substring("motorola".length());
				} else {
					deviceName = resolveDeviceName("motorola", "motorola" + userAgent.substring( userAgent.toLowerCase().indexOf("motorola") + "motorola".length()), false);
				}
				foundDevice = getDevice( vendor + "/" + deviceName);				
			}
		}
		return foundDevice;
	}

	/**
	 * Dynamically resolves a user agent for a known vendor
	 * @param vendor the J2ME Polish vendor name, e.g. "Motorola"
	 * @param userAgentVendor the vendor name at the start of the user agent, e.g. "MOT-"
	 * @param userAgent the user agent, needs to start with the userAgentVendor, e.g. "MOT-V3i/CLDC-1.0/MIDP-2.0"
	 * @param mayUseMinusSeparator true when the device name and rest of the user agent be separated by a minus sign
	 * @return the device if it can be resolved, null otherwise
	 */
	protected Device resolveUserAgent(String vendor, String userAgentVendor, String userAgent, boolean mayUseMinusSeparator)
	{
		String deviceName = resolveDeviceName(userAgentVendor, userAgent, mayUseMinusSeparator);
		Device foundDevice = getDevice( vendor + "/" + deviceName);
		if (foundDevice == null && deviceName.charAt( deviceName.length() - 1) == 'i') {
			foundDevice = getDevice( vendor + "/" + deviceName.substring(0, deviceName.length() - 1 ));
		}
		return foundDevice;
	}

	/**
	 * Extracts a device name from a user agent
	 * @param userAgentVendor the vendor name at the start of the user agent, e.g. "MOT-"
	 * @param userAgent the user agent, needs to start with the userAgentVendor, e.g. "MOT-V3i/CLDC-1.0/MIDP-2.0"
	 * @param mayUseMinusSeparator true when the device name and rest of the user agent be separated by a minus sign
	 * @return the device name
	 */
	private String resolveDeviceName(String userAgentVendor, String userAgent,
			boolean mayUseMinusSeparator)
	{
		String deviceName = userAgent.substring(userAgentVendor.length()).trim();
		int slashPos = deviceName.indexOf('/');
		int spacePos = deviceName.indexOf(' ');
		int endPos = slashPos;
		if (mayUseMinusSeparator) {
			int minusPos = deviceName.indexOf('-');
			if ((minusPos < slashPos && minusPos != -1) || (slashPos == -1)) {
				endPos = minusPos;
			}
		}
		if (spacePos < endPos && spacePos != -1) {
			endPos = spacePos;
		}
		if (endPos == 0) {
			deviceName = deviceName.substring(1);
			slashPos = deviceName.indexOf('/');
			spacePos = deviceName.indexOf(' ');
			endPos = slashPos;
			if (mayUseMinusSeparator) {
				int minusPos = deviceName.indexOf('-');
				if ((minusPos < slashPos && minusPos != -1) || (slashPos == -1)) {
					endPos = minusPos;
				}
			}
			if (spacePos < endPos && spacePos != -1) {
				endPos = spacePos;
			}
		}
		if (endPos != -1){
			deviceName = deviceName.substring(0, endPos);
		}
		return deviceName;
	}

	private void initializeUserAgentMapping() {
        this.devicesByUserAgent = new HashMap();
        this.unresolvableUserAgents = new HashMap();
        String CAPABILITY_WAP_USER_AGENT = "wap.userAgent";
        Device[] allDevices = getDevices();

        Requirements requirements = new Requirements();
        requirements.addRequirement(new VariableDefinedRequirement(CAPABILITY_WAP_USER_AGENT));
        Device[] devicesWithUA = requirements.filterDevices(allDevices);
        
        Device currentDevice;
//        String shortendUserAgentId = null;
        String currentUserAgentAsString;

//        Writer allAgentsWriter;
//        Writer filteredAgentsWriter;
//        try {
//            allAgentsWriter = new OutputStreamWriter(new FileOutputStream("/home/rickyn/test/originalAgents"));
//            filteredAgentsWriter = new OutputStreamWriter(new FileOutputStream("/home/rickyn/test/filteredAgents"));
//        } catch (FileNotFoundException exception) {
//            exception.printStackTrace();
//            throw new RuntimeException(exception);
//        }
        
        for (int deviceIndex = 0; deviceIndex < devicesWithUA.length; deviceIndex++) {
            
            currentDevice = devicesWithUA[deviceIndex];
            currentUserAgentAsString = currentDevice.getCapability(CAPABILITY_WAP_USER_AGENT);
            
            if(currentUserAgentAsString == null) {
                // Should not happen as we filtered the devices list for this capability.
                continue;
            }
            String[] currentUserAgents = StringUtil.splitAndTrim(currentUserAgentAsString,'\1');
//            Arrays.sort( currentUserAgents ); // longest one is the last one
            
            for (int userAgentIndex = currentUserAgents.length; --userAgentIndex >= 0;) {
                String currentUserAgent = currentUserAgents[userAgentIndex];
                this.devicesByUserAgent.put( currentUserAgent, currentDevice );
                
//                int lengthOfString = currentUserAgent.length();
//                int i = lengthOfString;
//                
////                try {
////                    allAgentsWriter.write(currentUserAgent + "\t" + currentDevice.toString() + "\n");
////                } catch (IOException exception) {
////                    exception.printStackTrace();
////                }
//                
//                boolean containsUserAgentAlready = this.devicesByUserAgent.containsKey(currentUserAgent);
//                if(containsUserAgentAlready) {
//                    continue;
//                }
//                
//                while(i > 1) {
//                    // Save all prefixes of the useragent.
//                    shortendUserAgentId = currentUserAgent.substring(0,i);
//                    
//                    boolean shortendKeyPresent = this.devicesByUserAgent.containsKey(shortendUserAgentId);
//                    if(shortendKeyPresent) {
//                        // The prefix exists. Remove all of them to remove ambiguity
//                        while(i > 1) {
//                            shortendUserAgentId = currentUserAgent.substring(0,i);
//                            this.devicesByUserAgent.remove(shortendUserAgentId);
//                            i--;
//                        }
//                        break;
//                    }
//                    this.devicesByUserAgent.put(currentUserAgent,currentDevice);
//                    i--;
//                }
            }
        }
//        Set entrySet = this.devicesByUserAgent.entrySet();
//        for (Iterator iterator = entrySet.iterator(); iterator.hasNext(); ) {
//            Entry element = (Entry) iterator.next();
//            try {
//                filteredAgentsWriter.write(element.getKey().toString() + "\t" + element.getValue().toString() + "\n");
//            } catch (IOException exception) {
//                exception.printStackTrace();
//            }
//        }
//        
//        try {
//            filteredAgentsWriter.close();
//            allAgentsWriter.close();
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
        
    }
    
    public Device[] getVirtualDevices() {
		return getVirtualDevices( this.devices );
	}
	
	public Device[] getVirtualDevices( Device[] filteredDevices ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			if ( device.isVirtual() ) {
				list.add( device );
			}
		}
		Device[] virtualDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( virtualDevices );
		return virtualDevices;
	}
	
	public Device[] getRealDevices() {
		return getRealDevices( getDevices() );
	}
	
	public Device[] getRealDevices( Device[] filteredDevices ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			if ( !device.isVirtual() ) {
				list.add( device );
			}
		}
		Device[] realDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( realDevices );
		return realDevices;
	}

	public Device[] getDevices( Configuration[] supportedConfigurations, Platform[] supportedPlatforms ) {
		return getDevices( this.devices, supportedConfigurations, supportedPlatforms, null );
	}
	
	public Device[] getDevices( Configuration[] supportedConfigurations, Platform[] supportedPlatforms, Library[] supportedLibraries ) {
		return getDevices( this.devices, supportedConfigurations, supportedPlatforms, supportedLibraries );
	}
	
	/**
	 * Retrieves all devices with support for the specified platform.
	 * @param platform the platform that should be supported by the device
	 * @return a list of devices suporting that specific platform, can be empty
	 */
	public Device[] getDevices(Platform platform) {
		return getDevices(null, new Platform[]{platform});
	}
	
	/**
	 * Retrieves all devices with support for the specified configuration.
	 * @param configuration the configuration that should be supported by the device
	 * @return a list of devices suporting that specific configuration, can be empty
	 */
	public Device[] getDevices(Configuration configuration) {
		return getDevices( new Configuration[]{ configuration }, null);
	}
	
	public Device[] getDevices( Device[] filteredDevices, Configuration[] supportedConfigurations, Platform[] supportedPlatforms ) {
		return getDevices( this.devices, supportedConfigurations, supportedPlatforms, null );
	}
	
	public Device[] getDevices( Device[] filteredDevices, Configuration[] supportedConfigurations, Platform[] supportedPlatforms, Library[] supportedLibraries ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			boolean addDevice = true;
			if (supportedConfigurations != null) { // at least one configuration needs to be suported
				addDevice = false;
				for (int j = 0; j < supportedConfigurations.length; j++) {
					Configuration configuration = supportedConfigurations[j];
					if (device.supportsConfiguration( configuration )) {
						addDevice = true;
						break;
					}
				}
			}
			if (addDevice && supportedPlatforms != null) { // at least one platform needs to be suported
				addDevice = false;
				for (int j = 0; j < supportedPlatforms.length; j++) {
					Platform platform = supportedPlatforms[j];
					if (device.supportsPlatform( platform )) {
						addDevice = true;
						break;
					}
				}
			}
			if (addDevice && supportedLibraries != null) { // all libraries need to be supported
				for (int j = 0; j < supportedLibraries.length; j++) {
					Library library = supportedLibraries[j];
					if (!device.supportsLibrary( library )) {
						addDevice = false;
						break;
					}
				}
			}
			if (addDevice) {
				list.add( device );
			}
		}
		Device[] platformDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( platformDevices );
		return platformDevices;
	}
	/**
	 * Retrieves all known vendors.
	 * 
	 * @return an array with all known vendors
	 */
	public Vendor[] getVendors() {
		return this.vendorManager.getVendors();		
	}


	/**
	 * Retrieves all vendors that used by the given devices.
	 * 
	 * @param filteredDevices the devices that are searched for vendors
	 * @return an array with all vendors of the given devices
	 */
	public Vendor[] getVendors( Device[] filteredDevices ) {
		HashMap list = new HashMap();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			Vendor vendor = (Vendor) device.parent;
			list.put( vendor.identifier, vendor );
		}
		Vendor[] vendors =  (Vendor[]) list.values().toArray( new Vendor[ list.size() ] );
		Arrays.sort( vendors );
		return vendors;
	}
	
	/**
	 * Gets all devices of the specified vendor.
	 * 
	 * @param vendor the vendor
	 * @return an array of device-definitions of that vendor.
	 */
	public Device[] getDevices( Vendor vendor ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < this.devices.length; i++) {
			Device device = this.devices[i];
			if ( device.parent == vendor ) {
				list.add( device );
			}
		}
		Device[] vendorDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( vendorDevices );
		return vendorDevices;
	}
	
	/**
	 * Gets all devices of the specified vendor.
	 * 
	 * @param filteredDevices the devices that are searched for vendors
	 * @param vendor the vendor
	 * @return an array of device-definitions of that vendor.
	 */
	public Device[] getDevices( Device[] filteredDevices, Vendor vendor ) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < filteredDevices.length; i++) {
			Device device = filteredDevices[i];
			if ( device.parent == vendor ) {
				list.add( device );
			}
		}
		Device[] vendorDevices = (Device[]) list.toArray( new Device[ list.size() ] );
		Arrays.sort( vendorDevices );
		return vendorDevices;
	}
	/**
	 * @param configuratioManager
	 * @param platformManager
	 * @param vendManager
	 * @param deviceGroupManager
	 * @param libManager
	 * @param capabilityManager
	 * @param customDevices
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomDevices( ConfigurationManager configuratioManager, PlatformManager platformManager, VendorManager vendManager, DeviceGroupManager deviceGroupManager, LibraryManager libManager, CapabilityManager capabilityManager, File customDevices ) 
	throws JDOMException, InvalidComponentException {
		loadCustomDevices(null, configuratioManager, platformManager, vendManager, deviceGroupManager, libManager, capabilityManager, customDevices);
	}

	/**
	 * @param identifierList
	 * @param configuratioManager
	 * @param platformManager
	 * @param vendManager
	 * @param deviceGroupManager
	 * @param libManager
	 * @param capabilityManager
	 * @param customDevices
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomDevices( List identifierList, ConfigurationManager configuratioManager, PlatformManager platformManager, VendorManager vendManager, DeviceGroupManager deviceGroupManager, LibraryManager libManager, CapabilityManager capabilityManager, File customDevices ) 
	throws JDOMException, InvalidComponentException {
		if (customDevices.exists()) {
			try {
				loadDevices( identifierList, configuratioManager, platformManager, vendManager, deviceGroupManager, libManager, capabilityManager, new FileInputStream( customDevices ) );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-devices.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-devices.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (InvalidComponentException e) {
				// this can happen
				String message = e.getMessage();
				message = StringUtil.replace( message, "devices.xml", "custom-devices.xml" );
				throw new InvalidComponentException( message, e );
				
			}
		}
	}


	/**
	 * Clears all stored devices from memory.
	 */
	public void clear()
	{
		this.devicesXmlList = null;
		this.currentDevicesXmlList = null;
		this.devices = null;
		this.devicesList.clear();
		this.devicesByIdentifier.clear();
		if (this.devicesByUserAgent != null)
		{
			this.devicesByUserAgent.clear();
		}
	}


}
