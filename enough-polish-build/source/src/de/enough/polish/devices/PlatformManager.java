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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.enough.polish.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

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
public class PlatformManager {
	
	private final HashMap platformsByIdentifier;
	
	/**
	 * Creates a new platform manager.
	 * 
	 * @param capabilityManager manages device capabilities
	 * @param is The input stream containing the platform-definitions. This is usually "./platforms.xml".
	 * @throws JDOMException when there are syntax errors in platforms.xml
	 * @throws IOException when platforms.xml could not be read
	 * @throws InvalidComponentException when a platforms definition has errors
	 */
	public PlatformManager( CapabilityManager capabilityManager, InputStream is ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.platformsByIdentifier = new HashMap();
		loadPlatforms( capabilityManager, is );
		is.close();
	}
	
	/**
	 * Loads all known platforms from the given file.
	 * 
	 * @param capabilityManager manages device capabilities
	 * @param is The input stream containing the platform-definitions. This is usually "./platforms.xml".
	 * @throws JDOMException when there are syntax errors in platforms.xml
	 * @throws IOException when platforms.xml could not be read
	 * @throws InvalidComponentException when a platforms definition has errors
	 */
	private void loadPlatforms(CapabilityManager capabilityManager, InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (is == null) {
			throw new BuildException("Unable to load platforms.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			Platform platform = new Platform( deviceElement, capabilityManager, this );
			this.platformsByIdentifier.put( platform.getIdentifier(), platform );
		}
	}

	/**
	 * Retrieves the specified platform.
	 * 
	 * @param identifier The identifier of the platform, e.g. Nokia, Siemens, Motorola, etc.
	 * @return The platform or null of that platform has not been defined.
	 */
	public Platform getPlatform( String identifier ) {
		return (Platform) this.platformsByIdentifier.get( identifier );
	}

	/**
	 * Retrieves all known Platform.
	 * 
	 * @return an array with all known Platform
	 */
	public Platform[] getPlatforms() {
		Platform[] platforms = (Platform[]) this.platformsByIdentifier.values().toArray( new Platform[ this.platformsByIdentifier.size() ] );
		Arrays.sort( platforms );
		return platforms;
	}

	// TODO: This is going to change to another location. Maybe DeviceDatabase.
    public Platform[] filterSuperPlatforms(Platform[] platforms) {
        List resultList = new LinkedList();
        Arrays.sort(platforms);
        if(platforms.length == 1) {
            return new Platform[] {platforms[0]};
        }
        
        String identifier = platforms[0].getIdentifier();
        String identifierName = identifier.substring(0,identifier.indexOf("/"));
        String oldIdentifierName = identifierName;
        Platform oldPlatform = platforms[0];
        
        for (int i = 1; i < platforms.length; i++) {
            Platform platform = platforms[i];
            identifier = platform.getIdentifier();
            identifierName = identifier.substring(0,identifier.indexOf("/"));
            // Search for the end of similar platforms like MIDP/1.0, MIDP/1.1, DoJa/1.0
            // If found add the previos platform as it has the highest version.
            if( ! identifierName.equals(oldIdentifierName)) {
                resultList.add(oldPlatform);
                oldIdentifierName = identifierName;
            }
            oldPlatform = platform;
        }
        // The last of the platforms is always the highest. But the loop above does not catch this.
        resultList.add(platforms[platforms.length-1]);
        return (Platform[]) resultList.toArray(new Platform[resultList.size()]);
    }
    
	/**
	 * Loads the custom-vendors.xml of the user from the current project.
	 * @param customPlatforms
	 * @param capabilityManager
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomPlatforms(File customPlatforms, CapabilityManager capabilityManager ) 
	throws JDOMException, InvalidComponentException {
		if (customPlatforms.exists()) {
			try {
				loadPlatforms( capabilityManager, new FileInputStream( customPlatforms ) );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-platforms.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-platforms.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (InvalidComponentException e) {
				// this can happen
				String message = e.getMessage();
				message = StringUtil.replace( message, "platforms.xml", "custom-platforms.xml" );
				throw new InvalidComponentException( message, e );
			}
		}
	}

	/**
	 * Clears all stored platforms from memory.
	 */
	public void clear() {
		this.platformsByIdentifier.clear();
	}
}
