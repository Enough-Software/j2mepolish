/*
 * Created on 23-May-2004 at 23:04:35.
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

import java.util.ArrayList;

import org.jdom.Element;

import de.enough.polish.Device;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Represents a single library.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-May-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Bug extends PolishComponent {
	
	private final String name;
	private final String[] areas;
	//private final String description;
	private final String solution;

	/**
	 * Creates a new issue description.
	 * 
	 * @param definition the xml definition of this library
	 * @param manager the issue/bug manager
	 * @throws InvalidComponentException when the given bug definition has errors
	 */
	public Bug( Element definition,
			BugManager manager) 
	throws InvalidComponentException 
	{
		super( definition );
		this.name = definition.getChildTextTrim( "name");
		if (this.name == null) {
			throw new InvalidComponentException("A bug listed in bugs.xml does not define its name. Please insert the <name> element into the file [bugs.xml] for this issue.");
		}
		this.identifier = this.name;
		//this.description = definition.getChildTextTrim( "description");
		if (this.description == null) {
			throw new InvalidComponentException("The bug [" + this.name + "] listed in bugs.xml does not define its description. Please insert the <description> element into the file [bugs.xml] for this issue.");
		}
		
		String areasString = definition.getChildTextTrim( "area");
		if (areasString == null) {
			throw new InvalidComponentException("The bug [" + this.name + "] does not define the areas of this issue. Please insert the <area> element into the file [bugs.xml] for this issue.");
		}
		this.areas = StringUtil.splitAndTrim( areasString, ',' );
		this.solution = definition.getChildTextTrim( "solution");

	}
	
	
	public String[] getAreas() {
		return this.areas;
	}
	public String getDescription() {
		return this.description;
	}
	public String getName() {
		return this.name;
	}
	public String getSolution() {
		return this.solution;
	}


	/**
	 * @param area
	 * @return true when the bug belongs to the specified area
	 */
	public boolean isInArea(String area) {
		for (int i = 0; i < this.areas.length; i++) {
			if (area.equals( this.areas[i] )) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param manager
	 * @return all devices that have this bug.
	 */
	public Device[] getDevices(DeviceManager manager) {
		ArrayList list = new ArrayList();
		Device[] devices = manager.getDevices();
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			String bugsCapability = device.getCapability("polish.Bugs");
			if (bugsCapability != null && bugsCapability.indexOf( this.name ) != -1) {
				String[] bugs = StringUtil.splitAndTrim(bugsCapability, ',');
				for (int j = 0; j < bugs.length; j++) {
					String bugName = bugs[j];
					if (this.name.equals(bugName)) {
						list.add( device );
						break;
					}
				}
			}
		}
		return (Device[]) list.toArray( new Device[ list.size() ] );
	}
	
	
	public int compareTo(Object o) {
		if (o instanceof Bug) {
			return this.name.compareToIgnoreCase( ((Bug)o).name ); 
		} else {
			return 0;
		}
	}
}
