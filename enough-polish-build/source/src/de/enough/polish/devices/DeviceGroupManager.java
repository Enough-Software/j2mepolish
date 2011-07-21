/*
 * Created on 16-Feb-2004 at 19:48:06.
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

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Manages all known groups of devices.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        16-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DeviceGroupManager {
	
	private HashMap groups;
	
	/**
	 * Creates a new group manager.
	 * 
	 * @param groupsIS the InputStream containing the groups definitions.
	 * 			Usally this is the groups.xml file in the current directory.
	 * @param capabilityManager the manager of capabilities
	 * @throws JDOMException when there are syntax errors in groups.xml
	 * @throws IOException when groups.xml could not be read
	 * @throws InvalidComponentException when a group definition has errors
	 */
	public DeviceGroupManager( InputStream groupsIS, CapabilityManager capabilityManager ) 
	throws InvalidComponentException, JDOMException, IOException 
	{
		this.groups = new HashMap();
		loadGroups( groupsIS, capabilityManager );
		groupsIS.close();
	}
	
	/**
	 * Loads all group definitions.
	 * 
	 * @param groupsIS the InputStream containing the groups definitions.
	 * 			Usally this is the groups.xml file in the current directory.
	 * @param capabilityManager the manager of capabilities
	 * @throws JDOMException when there are syntax errors in groups.xml
	 * @throws IOException when groups.xml could not be read
	 * @throws InvalidComponentException when a group definition has errors
	 */
	private void loadGroups(InputStream groupsIS, CapabilityManager capabilityManager) 
	throws InvalidComponentException, JDOMException, IOException 
	{
		if (groupsIS == null) {
			throw new BuildException("Unable to load groups.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( groupsIS );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			DeviceGroup group = new DeviceGroup( deviceElement, capabilityManager, this );
			this.groups.put( group.getIdentifier(), group );
		}
		/*
		DeviceGroup[] groupArray = (DeviceGroup[]) this.groups.values().toArray( new DeviceGroup[ this.groups.size() ] );
		for (int i = 0; i < groupArray.length; i++) {
			DeviceGroup group = groupArray[i];
			String parentName = group.getParentIdentifier();
			if (parentName != null) {
				//System.out.println("\nsetting " + parentName + " as parent for group " + group.getIdentifier());
				DeviceGroup parent = getGroup( parentName );
				if (parent == null) {
					throw new InvalidComponentException("The group [" + group.getIdentifier() + "] has the non-existing parent [" + parentName + "]. Check your [groups.xml]");
				}
				System.out.println("group: setting parent " + parentName + " for group " + group.getIdentifier() );
				group.addComponent( parent );
			}
		}
		*/
	}

	/**
	 * Retrieves the group for the given name.
	 * 
	 * @param name The name of the group, e.g. "Series60"
	 * @return The found group or null when it has not been defined.
	 */
	public DeviceGroup getGroup( String name ) {
		return (DeviceGroup) this.groups.get( name );
	}

	/**
	 * Gets or creates the group with the specified name.
	 * 
	 * @param name the name of the group
	 * @param create when true is given, the group will be created when it does not exist
	 * @return the group with the specified name.
	 */
	public Object getGroup(String name, boolean create) {
		DeviceGroup group = (DeviceGroup) this.groups.get( name );
		if (group == null && create) {
			group = new DeviceGroup( name );
			group.isVirtual = true;
			this.groups.put( name, group );
		}
		return group;
	}

	public void loadCustomGroups(File customGroups, CapabilityManager capabilityManager ) 
	throws InvalidComponentException, JDOMException 
	{
		if (customGroups.exists()) {
			try {
				loadGroups( new FileInputStream( customGroups ), capabilityManager );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-groups.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-groups.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (InvalidComponentException e) {
				// this can happen
				String message = e.getMessage();
				message = StringUtil.replace( message, "groups.xml", "custom-groups.xml" );
				throw new InvalidComponentException( message, e );
			}
		}
	}

	/**
	 * Clears all stored groups from memory.
	 */
	public void clear() {
		this.groups.clear();
	}
	
	public boolean hasGroup( String name ) {
		return hasGroup( name, true );
	}

	/**
	 * @param name
	 * @param mustNotBeVirtual 
	 * @return true when the group exists
	 */
	public boolean hasGroup(String name, boolean mustNotBeVirtual) {
		DeviceGroup group = (DeviceGroup) this.groups.get( name );
		return group != null && (!mustNotBeVirtual || !group.isVirtual );
	}
	
	
}
