/*
 * Created on 23-May-2004 at 22:20:13.
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import de.enough.polish.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.Device;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Manages the issues of devices.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-May-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class BugManager {
	
	private final HashMap bugsByName = new HashMap();
	private final HashMap areasMap = new HashMap();
	//private Hashtable antProperties;
	
	/**
	 * Creates a new BugManager.
	 * 
	 * @param polishHome the home of the polish installation
	 * @throws JDOMException when there are syntax errors in bugs.xml
	 * @throws IOException when bugs.xml could not be read
	 * @throws InvalidComponentException when an issue definition has errors
	 */
	public BugManager( File polishHome) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		loadBugs( new FileInputStream( new File( polishHome, "bugs.xml")) );
	}

	/**
	 * Creates a new BugManager.
	 * 
	 * @param antProperties all properties which have been defined in Ant
	 * @param is input stream for reading the bugs.xml file
	 * @throws JDOMException when there are syntax errors in bugs.xml
	 * @throws IOException when bugs.xml could not be read
	 * @throws InvalidComponentException when an issue definition has errors
	 */
	public BugManager( Hashtable antProperties, InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		//this.antProperties = antProperties;
		loadBugs( is );
	}

	/**
	 * Loads the bugs.xml file.
	 * 
	 * @param is input stream for reading the bugs.xml file
	 * @throws JDOMException when there are syntax errors in bugs.xml
	 * @throws IOException when bugs.xml could not be read
	 * @throws InvalidComponentException when an api definition has errors
	 */
	private void loadBugs(InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (is == null) {
			throw new BuildException("Unable to load bugs.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			Bug issue = new Bug( definition, this );
			Bug existingIssue = (Bug) this.bugsByName.get( issue.getName() ); 
			if ( existingIssue != null ) {
				throw new InvalidComponentException("The issue [" + issue.getName() 
						+ "] is defined twice. Please remove one in [bugs.xml].");
			}
			String[] areas = issue.getAreas();
			for (int i = 0; i < areas.length; i++) {
				String area = areas[i];
				this.areasMap.put( area, Boolean.TRUE );
			}
			this.bugsByName.put( issue.getName(), issue );
		}		
	}
	
	/**
	 * Retrieves all known issues for the given device.
	 * 
	 * @param device the device
	 * @return either null when no issues are known or an array with all bugs
	 * @throws InvalidComponentException
	 */
	public Bug[] getBugs( Device device ) 
	throws InvalidComponentException 
	{
		String bugsDefinition = device.getCapability("polish.Bugs");
		if (bugsDefinition == null) {
			return null;
		}
		String[] bugNames = StringUtil.splitAndTrim( bugsDefinition, ',' );
		Bug[] bugs = new Bug[ bugNames.length ];
		for (int i = 0; i < bugNames.length; i++) {
			String name = bugNames[i];
			Bug bug = (Bug) this.bugsByName.get( name );
			if (bug == null) {
				throw new InvalidComponentException("The device [" + device.getIdentifier() + "] uses the undefined issue [" + name + "]: check the \"Bugs\"-capability in [devices.xml].");
			}
			bugs[i] = bug;
		}
		
		return bugs;
	}

	/**
	 * Retrieves all areas of the listed bugs.
	 * @return an array of all known areas
	 */
	public String[] getAreas() {
		String[] areas = (String[]) this.areasMap.keySet().toArray( new String[ this.areasMap.size() ] );
		Arrays.sort( areas );
		return areas;
	}
	
	
	public Bug[] getAllBugs() {
		return (Bug[]) this.bugsByName.values().toArray( new Bug[ this.bugsByName.size() ]);
	}
	
	public Bug[] getBugsForArea( String area ) {
		ArrayList bugsList = new ArrayList();
		Bug[] bugs = getAllBugs();
		Arrays.sort( bugs );
		for (int i = 0; i < bugs.length; i++) {
			Bug bug = bugs[i];
			if ( bug.isInArea( area ) ) {
				bugsList.add( bug );
			}
		}
		bugs = (Bug[]) bugsList.toArray( new Bug[ bugsList.size() ]);
		return bugs;
	}
}
