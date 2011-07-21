/*
 * Created on 16-Feb-2004 at 19:09:20.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.enough.polish.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.PolishProject;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Manages all known vendors.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VendorManager {
	
	private final HashMap vendors;
	
	/**
	 * Creates a new vendor manager.
	 * 
	 * @param project The j2me project settings.
	 * @param vendorsIS The input stream containing the vendor-definitions. This is usually "./vendors.xml".
	 * @param capabilityManager manages capabilities
	 * @param groupManager manager of groups
	 * @throws JDOMException when there are syntax errors in vendors.xml
	 * @throws IOException when vendors.xml could not be read
	 * @throws InvalidComponentException when a vendor definition has errors
	 */
	public VendorManager( PolishProject project, InputStream vendorsIS, CapabilityManager capabilityManager, DeviceGroupManager groupManager ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.vendors = new HashMap();
		loadVendors( project, vendorsIS, capabilityManager, groupManager );
		vendorsIS.close();
	}
	
	/**
	 * Loads all known vendors from the given file.
	 * 
	 * @param project The j2me project settings.
	 * @param vendorsIS The input stream containing the vendor-definitions. This is usually "./vendors.xml".
	 * @param capabilityManager manages capabilities
	 * @param groupManager manager of groups
	 * @throws JDOMException when there are syntax errors in vendors.xml
	 * @throws IOException when vendors.xml could not be read
	 * @throws InvalidComponentException when a vendor definition has errors
	 */
	private void loadVendors(PolishProject project, InputStream vendorsIS, CapabilityManager capabilityManager, DeviceGroupManager groupManager) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (vendorsIS == null) {
			throw new BuildException("Unable to load vendors.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( vendorsIS );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			Vendor vendor = new Vendor( project, deviceElement, capabilityManager, groupManager );
			this.vendors.put( vendor.getIdentifier(), vendor );
		}
	}

    public void addVendor(Vendor vendor) {
        this.vendors.put(vendor.getIdentifier(), vendor);
    }
    
	/**
	 * Retrieves the specified vendor.
	 * 
	 * @param name The name of the vendor, e.g. Nokia, Siemens, Motorola, etc.
	 * @return The vendor or null of that vendor has not been defined.
	 */
	public Vendor getVendor( String name ) {
		Vendor vendor =  (Vendor) this.vendors.get( name );
        if(vendor == null) {
            vendor = searchForVendorAlias(name);
        }
        return vendor;
	}
    
    private Vendor searchForVendorAlias(String vendorString) {
        Vendor[] allVendors = getVendors();
        Vendor vendor;
        for (int i = 0; i < allVendors.length; i++) {
            vendor = allVendors[i];
            String aliasesAsString = vendor.getCapability("vendor.alias");
            if(aliasesAsString == null) {
                continue;
            }
            String[] aliases = StringUtil.splitAndTrim(aliasesAsString,",");
            for (int j = 0; j < aliases.length; j++) {
                if(vendorString.equals(aliases[j])) {
                    return vendor;
                }
            }
        }
        return null;
    }
    
	/**
	 * Retrieves all known vendors.
	 * 
	 * @return an array with all known vendors
	 */
	public Vendor[] getVendors() {
		return (Vendor[]) this.vendors.values().toArray( new Vendor[ this.vendors.size() ] );
	}

	/**
	 * Loads the custom-vendors.xml of the user from the current project.
	 * @param customVendors
	 * @param polishProject
	 * @param capabilityManager manages capabilities
	 * @param groupManager manager of groups
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomVendors(File customVendors, PolishProject polishProject, CapabilityManager capabilityManager, DeviceGroupManager groupManager ) 
	throws JDOMException, InvalidComponentException {
		if (customVendors.exists()) {
			try {
				loadVendors( polishProject, new FileInputStream( customVendors ), capabilityManager, groupManager );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-vendors.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-vendors.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (InvalidComponentException e) {
				// this can happen
				String message = e.getMessage();
				message = StringUtil.replace( message, "vendors.xml", "custom-vendors.xml" );
				throw new InvalidComponentException( message, e );
			}
		}
	}
  
	/**
	 * Clears all stored devices from memory.
	 */
	public void clear()
	{
		this.vendors.clear();
	}
}
