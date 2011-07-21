/*
 * Created on 23-May-2005 at 10:29:13.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.enough.polish.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.exceptions.InvalidComponentException;

/**
 * <p>Manages capabilities that are used by PolishComponents.</p>
 *
 * <p>Copyright Enough Software 2005</p>

 * <pre>
 * history
 *        23-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class CapabilityManager {
	
	private final HashMap capabilitiesByIdentifier = new HashMap();

	/**
	 * Creates a new BugManager.
	 * 
	 * @param antProperties all properties which have been defined in Ant
	 * @param is input stream for reading the capabilities.xml file
	 * @throws JDOMException when there are syntax errors in apis.xml
	 * @throws IOException when apis.xml could not be read
	 * @throws InvalidComponentException when an api definition has errors
	 */
	public CapabilityManager( Map antProperties, InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		loadCapabilities( is );
	}

	/**
	 * Loads the capabilities.xml file.
	 * 
	 * @param is input stream for reading the capabilities.xml file
	 * @throws JDOMException when there are syntax errors in capabilities.xml
	 * @throws IOException when apis.xml could not be read
	 * @throws InvalidComponentException when an api definition has errors
	 */
	private void loadCapabilities(InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (is == null) {
			throw new BuildException("Unable to load capabilities.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			Capability capability = new Capability( definition, this );
			Capability existingCapability = (Capability) this.capabilitiesByIdentifier.get( capability.getIdentifier() ); 
			
			if ( existingCapability != null ) {
//				existingCapability.add( capability );
				throw new InvalidComponentException("The capability [" + capability.getIdentifier() 
						+ "] is defined twice. Please remove one in [capabilities.xml].");
			} else {
				String identifier = capability.getIdentifier();
				this.capabilitiesByIdentifier.put( identifier, capability );
				identifier = identifier.toLowerCase();
				this.capabilitiesByIdentifier.put( identifier, capability );
				this.capabilitiesByIdentifier.put( "polish." + identifier, capability );
			}
		}		
	}
	
	/**
	 * Retrieves a capability
	 * 
	 * @param identifier the identifier
	 * @return the capability - this can very well be null, since the device database is extensible!
	 */
	public Capability getCapability( String identifier ) {
		return (Capability) this.capabilitiesByIdentifier.get( identifier );
	}

	/**
	 * Clears all stored capabilities from memory.
	 */
	public void clear() {
		this.capabilitiesByIdentifier.clear();
	}
}
