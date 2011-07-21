/*
 * Created on 23-May-2005 at 15:55:25.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.PolishProject;

/**
 * <p>Manages the complete device database.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        23-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DeviceDatabase {

	private static Map instanceByPolishHome = new HashMap();
    
	private LibraryManager libraryManager;
	private DeviceManager deviceManager;
	private CapabilityManager capabilityManager;
	private DeviceGroupManager groupManager;
	private VendorManager vendorManager;
	private ConfigurationManager configurationManager;
	private PlatformManager platformManager;
    private File polishHome;
    private File apisHome;

	/**
	 * Creates a new device database, please call init() right after the initialization.
	 * 
	 * @see #init(Map, File, File, File, PolishProject, Map, Map)
	 * 
	 */
	public DeviceDatabase() 
	{
		// use init( Map properties, File polishHome, File projectHome, File apisHome, 
		//           PolishProject polishProject, Map inputStreamsByFileName, Map customFilesByFileName ) method
		// for initialization...
	}
	
	/**
	 * Creates a new device database.
	 * 
	 * @param polishHome the installation directory of J2ME Polish
	 * 
	 */
	public DeviceDatabase( File polishHome ) 
	{
		this(null, polishHome, null, null, null, null, null );
	}


	/**
	 * Creates a new device database.
	 * 
	 * @param properties configuration settings, like the optional wtk.home key
	 * @param polishHome the installation directory of J2ME Polish
	 * @param projectHome the project's directory
	 * @param apisHome the default import folder, can be null (in which case ${polish.home}/import is used)
	 * @param polishProject basic settings, can be null
	 * @param inputStreamsByFileName the configured input streams, can be null
	 * @param customFilesByFileName user-defined XLM configuration files, can be null
	 */
	public DeviceDatabase( Map properties, File polishHome, File projectHome, File apisHome, 
			PolishProject polishProject, Map inputStreamsByFileName, Map customFilesByFileName ) 
	{
		init(properties, polishHome, projectHome, apisHome, polishProject, 
				inputStreamsByFileName, customFilesByFileName);
	}
	
	/**
	 * Initialises a new Device Database.
	 * 
	 * @param polishHomeDir the polish.home setting
	 */
	public void init( File polishHomeDir ) {
		init( null, polishHomeDir, null, null, null, null, null  );
	}

	/**
	 * Initializes a new device database.
	 * 
	 * @param properties configuration settings, like the optional wtk.home key
	 * @param polishHomeDir the installation directory of J2ME Polish
	 * @param projectHomeDir the project's directory
	 * @param apisHomeDir the default import folder, can be null (in which case ${polish.home}/import is used)
	 * @param polishProject basic settings, can be null
	 * @param inputStreamsByFileName the configured input streams, can be null
	 * @param customFilesByFileName user-defined XLM configuration files, can be null
	 */
	public void init( Map properties, File polishHomeDir, File projectHomeDir, File apisHomeDir, 
			PolishProject polishProject, Map inputStreamsByFileName, Map customFilesByFileName ) 
	{
		if (customFilesByFileName == null) {
			customFilesByFileName = new HashMap();
		}
		if (properties == null ) {
			properties = new HashMap();
		}
		String wtkHomePath = (String) properties.get( "wtk.home" );
		File wtkHome = null;
		if (wtkHomePath != null) {
			wtkHome = new File( wtkHomePath );
			//throw new BuildException("Unable to initialise device database - found no wtk.home property.");
		}
		properties.put("polish.home", polishHomeDir.getAbsolutePath() );
				
		try {			
			// load capability-definitions:
			InputStream is = getInputStream( "capabilities.xml", polishHomeDir, inputStreamsByFileName ); 
			this.capabilityManager = new CapabilityManager( properties, is );
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read capabilities.xml: " + e.getMessage(), e );
		}
		try {	
			// load libraries:
			InputStream is = getInputStream( "apis.xml", polishHomeDir, inputStreamsByFileName );
			this.libraryManager = new LibraryManager( properties, apisHomeDir, wtkHome, is );
			File file = (File) customFilesByFileName.get("custom-apis.xml");
			if ( file != null ) {
				this.libraryManager.loadCustomLibraries( file );
			}  else {
				// use default libraries:
				file = new File( polishHomeDir, "custom-apis.xml");
				if (file.exists()) {
					this.libraryManager.loadCustomLibraries( file );
				}
				file = new File( projectHomeDir, "custom-apis.xml");
				if (file.exists()) {
					this.libraryManager.loadCustomLibraries( file );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read apis.xml/custom-apis.xml: " + e.getMessage(), e );
		}
		try {	
			// load configurations:
			InputStream is = getInputStream("configurations.xml", polishHomeDir, inputStreamsByFileName);
			this.configurationManager = new ConfigurationManager( this.capabilityManager, is );
			File file = (File) customFilesByFileName.get("custom-configurations.xml");
			if ( file != null ) {
				this.configurationManager.loadCustomConfigurations( file, this.capabilityManager );
			} else {
				// use default libraries:
				file = new File( polishHomeDir, "custom-configurations.xml");
				if (file.exists()) {
					this.configurationManager.loadCustomConfigurations( file, this.capabilityManager );
				}
				file = new File( projectHomeDir, "custom-configurations.xml");
				if (file.exists()) {
					this.configurationManager.loadCustomConfigurations( file, this.capabilityManager );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read configurations.xml/custom-configurations.xml: " + e.getMessage(), e );
		}
		try {	
			// load platforms:
			InputStream is = getInputStream("platforms.xml", polishHomeDir, inputStreamsByFileName);
			this.platformManager = new PlatformManager( this.capabilityManager, is );
			File file = (File) customFilesByFileName.get("custom-platforms.xml");
			if ( file != null ) {
				this.platformManager.loadCustomPlatforms( file, this.capabilityManager );
			} else {
				// use default vendors:
				file = new File( polishHomeDir, "custom-platforms.xml");
				if (file.exists()) {
					this.platformManager.loadCustomPlatforms( file, this.capabilityManager );
				}
				file = new File( projectHomeDir, "custom-platforms.xml");
				if (file.exists()) {
					this.platformManager.loadCustomPlatforms( file, this.capabilityManager );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read platforms.xml/custom-platforms.xml: " + e.getMessage(), e );
		}
		try {	
			// load device groups:
			InputStream is = getInputStream("groups.xml", polishHomeDir, inputStreamsByFileName);
			this.groupManager = new DeviceGroupManager( is, this.capabilityManager );
			File file = (File) customFilesByFileName.get("custom-groups.xml");
			if ( file != null ) {
				this.groupManager.loadCustomGroups( file, this.capabilityManager );
			} else {
				// use default vendors:
				file = new File( polishHomeDir, "custom-groups.xml");
				if (file.exists()) {
					this.groupManager.loadCustomGroups( file, this.capabilityManager );
				}
				file = new File( projectHomeDir, "custom-groups.xml");
				if (file.exists()) {
					this.groupManager.loadCustomGroups( file, this.capabilityManager );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read groups.xml/custom-groups.xml: " + e.getMessage(), e );
		}
		try {				
			// load vendors:
			InputStream is = getInputStream("vendors.xml", polishHomeDir, inputStreamsByFileName);
			this.vendorManager = new VendorManager( polishProject,  is, this.capabilityManager, this.groupManager);
			File file = (File) customFilesByFileName.get("custom-vendors.xml");
			if ( file != null ) {
				this.vendorManager.loadCustomVendors( file, polishProject, this.capabilityManager, this.groupManager );
			} else {
				// use default vendors:
				file = new File( polishHomeDir, "custom-vendors.xml");
				if (file.exists()) {
					this.vendorManager.loadCustomVendors( file, polishProject, this.capabilityManager, this.groupManager );
				}
				file = new File( projectHomeDir, "custom-vendors.xml");
				if (file.exists()) {
					this.vendorManager.loadCustomVendors( file, polishProject, this.capabilityManager, this.groupManager );
				}
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			throw new BuildException("unable to read vendors.xml/custom-vendors.xml: " + e.getMessage(), e );
		}
		try {				
			// at last load devices:
			List requiredIdentifiers = (List) properties.get("polish.devicedatabase.identifiers");
			InputStream is = getInputStream("devices.xml", polishHomeDir, inputStreamsByFileName);
			//this.deviceManager = new DeviceManager( this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, is );
			this.deviceManager = new DeviceManager( this.vendorManager );
			this.deviceManager.loadDevices(requiredIdentifiers, this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, is );
			File file = (File) customFilesByFileName.get("custom-devices.xml");
			if ( file != null ) {
				this.deviceManager.loadCustomDevices( requiredIdentifiers, this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, file );
			} else {
				// use default vendors:
				file = new File( polishHomeDir, "custom-devices.xml");
				if (file.exists()) {
					this.deviceManager.loadCustomDevices( requiredIdentifiers, this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, file );
				}
				file = new File( projectHomeDir, "custom-devices.xml");
				if (file.exists()) {
					this.deviceManager.loadCustomDevices( requiredIdentifiers, this.configurationManager, this.platformManager, this.vendorManager, this.groupManager, this.libraryManager, this.capabilityManager, file );
				}
			}
			if (requiredIdentifiers != null && requiredIdentifiers.size() != 0) {
				System.out.println("Warning: unable to find following requested device-identifiers:");
				for (Iterator iter = requiredIdentifiers.iterator(); iter.hasNext();) 
				{
					String identifier = (String) iter.next();
					System.out.println("  " + identifier);
				}
				System.out.println("Please check your <deviceRequirements> section in your build.xml script.");
			}
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage(); 
			if ( message == null) {
				message = e.toString();
			}
			throw new BuildException("unable to read devices.xml/custom-devices.xml: " + message, e );
		}
        this.polishHome = polishHomeDir;
        this.apisHome = apisHomeDir;
	}
	
	/**
	 * Gets the input stream for the specified resource.
	 * 
	 * @param fileName the name of the resource
	 * @param polishHomeDir the installation directory of J2ME Polish
	 * @param inputStreamsByFileName the map containing configured input streams, can be null
	 * @return the input stream or null when neither the stream is defined, nor the file can be found in polishHome
	 */
	private InputStream getInputStream(String fileName, File polishHomeDir, Map inputStreamsByFileName) {
		InputStream is = null;
		if (inputStreamsByFileName != null)  {
			is = (InputStream) inputStreamsByFileName.get( fileName );
		}
		if (is == null) {
			File file = new File( polishHomeDir, fileName );
			if ( file.exists() ) {
				try {
					return new FileInputStream( file );
				} catch (FileNotFoundException e) {
					// this should not happen, since we explicitely test this case
					e.printStackTrace();
				}
			}
			// now try to load file from default position in build.xml:
			is = getClass().getResourceAsStream( "/" + fileName );
		}
		return is;
	}
	
	/**
	 * Creates a new device database.
	 * 
	 * @param polishHome the installation directory of J2ME Polish
     * @return a DeviceDatabase object.
     * @throws BuildException thrown when something went wrong.
	 */
	public static final  DeviceDatabase getInstance( File polishHome ){
		return getInstance(null, polishHome, null, null, null, null, null );
	}


	/**
	 * Creates a new device database.
	 * 
	 * @param properties configuration settings, like the optional wtk.home key
	 * @param polishHome the installation directory of J2ME Polish. Must not be null.
	 * @param projectHome the project's directory
	 * @param apisHome the default import folder, can be null (in which case ${polish.home}/import is used)
	 * @param polishProject basic settings, can be null
	 * @param inputStreamsByFileName the configured input streams, can be null
	 * @param customFilesByFileName user-defined XML configuration files, can be null
     * @return a DeviceDatabase object.
     * @throws BuildException thrown when something went wrong.
	 */
	public static final DeviceDatabase getInstance( Map properties, File polishHome, File projectHome, File apisHome, 
			PolishProject polishProject, Map inputStreamsByFileName, Map customFilesByFileName ) 
	{
        DeviceDatabase deviceDatabase;
        String polishHomePath = null;
        if (polishHome != null) {
	        polishHomePath = polishHome.getAbsolutePath();
	        
	        deviceDatabase = (DeviceDatabase)instanceByPolishHome.get(polishHomePath);
	        if(deviceDatabase != null) {
	            return deviceDatabase;
	        }
        }
        
        deviceDatabase = new DeviceDatabase( properties, polishHome, projectHome, apisHome, polishProject, 
		inputStreamsByFileName, customFilesByFileName);
        if (polishHomePath != null) {
        	instanceByPolishHome.put(polishHomePath,deviceDatabase);
        }
        return deviceDatabase;
	}
	
	/**
	 * Loads only the specified devices.
	 * This call ignores all other device definitions and is thus less memory intensive and much faster than using a regular device database.
	 * @param polishHome the location of the J2ME Polish installation dir
	 * @param identifiers the identifiers of the devices like "Nokia/N70"
	 * @return the found device definitions, not necessarily the same length as the specified identifiers, when a device has not been found.
	 */
	public static Device[] loadDevices( File polishHome, String[] identifiers ) {
		Map properties = new HashMap();
		List deviceIdentifiersList =  new ArrayList(); //Arrays.asList(identifiers);
		for (int i = 0; i < identifiers.length; i++) {
			String identifier = identifiers[i];
			deviceIdentifiersList.add( identifier );
		}
		properties.put( "polish.devicedatabase.identifiers", deviceIdentifiersList );
		if (polishHome != null) {
			properties.put( "polish.home", polishHome.getAbsolutePath() );
		}
		DeviceDatabase db = new DeviceDatabase(properties, polishHome, null, null, null, null, null );
		List devicesList = new ArrayList();
		Device[] devices = db.getDevices();
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			//if (deviceIdentifiersList.contains(device.getIdentifier())) {
				devicesList.add(device);
//			} else {
//				System.out.println("ignoring device " + device.getIdentifier() );
//			}
		}
		if (devicesList.size() == devices.length) {
			return devices;
		} else {
			return (Device[]) devicesList.toArray( new Device[ devicesList.size() ] );
		}
	}
	
	/**
	 * Loads only the specified device.
	 * This call ignores all other device definitions and is thus less memory intensive and much faster than using a regular device database.
	 * @param polishHome the location of the J2ME Polish installation dir
	 * @param identifier the identifier of the devic like "Nokia/N70"
	 * @return the found device definition
	 * @throws IllegalArgumentException when the device with the given identifier was not found
	 */
	public static Device loadDevice( File polishHome, String identifier ) {
		Device[] devices = loadDevices( polishHome, new String[]{ identifier } );
		if (devices.length == 1){
			return devices[0];
		} else {
			for (int i = 0; i < devices.length; i++) {
				Device device = devices[i];
				if (device.getIdentifier().equals( identifier)) {
					return device;
				}
 			}
		}
		throw new IllegalArgumentException("Device " + identifier + " is unknown.");
	}



	/**
	 * @return Returns the capabilityManager.
	 */
	public CapabilityManager getCapabilityManager() {
		return this.capabilityManager;
	}
	/**
	 * @return Returns the deviceManager.
	 */
	public DeviceManager getDeviceManager() {
		return this.deviceManager;
	}
	/**
	 * @return Returns the groupManager.
	 */
	public DeviceGroupManager getGroupManager() {
		return this.groupManager;
	}
	/**
	 * @return Returns the libraryManager.
	 */
	public LibraryManager getLibraryManager() {
		return this.libraryManager;
	}
	/**
	 * @return Returns the vendorManager.
	 */
	public VendorManager getVendorManager() {
		return this.vendorManager;
	}
	/**
	 * @return Returns the manager of platforms.
	 */
	public PlatformManager getPlatformManager() {
		return this.platformManager;
	}

	/**
	 * @return Returns the configurationManager.
	 */
	public ConfigurationManager getConfigurationManager() {
		return this.configurationManager;
	}

    public File getPolishHome() {
        return this.polishHome;
    }

    public File getApisHome() {
        return this.apisHome;
    }

	public Configuration[] getConfigurations() {
		return this.configurationManager.getConfigurations();
	}
    
	public Platform[] getPlatforms() {
		return this.platformManager.getPlatforms();
	}

	public Library[] getLibraries() {
		return this.libraryManager.getLibraries();
	}
	
	
	public Device[] getDevices() {
		return this.deviceManager.getDevices();
	}
	
	/**
	 * Retrieves the specified device or null when not found.
	 * 
	 * @param identifier the identifier like "Nokia/6630", "Generic/midp2"
	 * @return the device or null when not found.
	 */
	public Device getDevice( String identifier ) {
		Device[] devices = getDevices();
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			if (device.identifier.equals( identifier )) {
				return device;
			}
		}
		return null;
	}

	/**
	 * Clears all referenced objects and databases from the memory.
	 */
	public void clear() {
		if (instanceByPolishHome != null) {
			instanceByPolishHome.clear();
		}
		if (this.deviceManager != null) {
			this.deviceManager.clear();
		}
		if (this.vendorManager != null) {
			this.vendorManager.clear();
		}
		if (this.groupManager != null) {
			this.groupManager.clear();
		}
		// following managers are required by other build phases:
//		if (this.capabilityManager != null) {
//			this.capabilityManager.clear();
//		}
//		if (this.configurationManager != null) {
//			this.configurationManager.clear();
//		}
//		if (this.libraryManager != null) {
//			this.libraryManager.clear();
//		}
//		if (this.platformManager != null) {
//			this.platformManager.clear();
//		}
		System.gc();
	}

	/**
	 * Retrieves all devices with support for the specified platform.
	 * @param platform the platform that should be supported by the device
	 * @return a list of devices suporting that specific platform, can be empty
	 */
	public Device[] getDevices(Platform platform) {
		return this.deviceManager.getDevices(platform);
	}
	
	/**
	 * Retrieves all devices with support for the specified configuration.
	 * @param configuration the configuration that should be supported by the device
	 * @return a list of devices suporting that specific configuration, can be empty
	 */
	public Device[] getDevices(Configuration configuration) {
		return this.deviceManager.getDevices(configuration);
	}
        
}
