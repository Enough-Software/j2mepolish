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
public class ConfigurationManager {
	
	private final HashMap configurationsByIdentifier;
	
	/**
	 * Creates a new configuration manager.
	 * 
	 * @param capabilityManager manages device capabilities
	 * @param is The input stream containing the platform-definitions. This is usually "./configurations.xml".
	 * @throws JDOMException when there are syntax errors in configurations.xml
	 * @throws IOException when configurations.xml could not be read
	 * @throws InvalidComponentException when a configurations definition has errors
	 */
	public ConfigurationManager( CapabilityManager capabilityManager, InputStream is ) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		this.configurationsByIdentifier = new HashMap();
		loadConfigurations( capabilityManager, is );
		is.close();
	}
	
	/**
	 * Loads all known configurations from the given file.
	 * 
	 * @param capabilityManager manages device capabilities
	 * @param is The input stream containing the platform-definitions. This is usually "./configurations.xml".
	 * @throws JDOMException when there are syntax errors in configurations.xml
	 * @throws IOException when configurations.xml could not be read
	 * @throws InvalidComponentException when a configurations definition has errors
	 */
	private void loadConfigurations(CapabilityManager capabilityManager, InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (is == null) {
			throw new BuildException("Unable to load configurations.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element deviceElement = (Element) iter.next();
			Configuration configuration = new Configuration( deviceElement, capabilityManager );
			this.configurationsByIdentifier.put( configuration.getIdentifier(), configuration );
		}
	}

	/**
	 * Retrieves the specified configuration.
	 * 
	 * @param identifier The identifier of the configuration
	 * @return The configuration or null of that configuration has not been defined.
	 */
	public Configuration getConfiguration( String identifier ) {
		return (Configuration) this.configurationsByIdentifier.get( identifier );
	}

	/**
	 * Retrieves all known configurations.
	 * 
	 * @return an array with all known configurations
	 */
	public Configuration[] getConfigurations() {
		Configuration[] configs = (Configuration[]) this.configurationsByIdentifier.values().toArray( new Configuration[ this.configurationsByIdentifier.size() ] );
		Arrays.sort( configs );
		return configs;
	}

	/**
	 * Loads the custom-configurations.xml of the user from the current project.
	 * @param customConfigurations
	 * @param capabilityManager
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomConfigurations(File customConfigurations, CapabilityManager capabilityManager ) 
	throws JDOMException, InvalidComponentException {
		if (customConfigurations.exists()) {
			try {
				loadConfigurations( capabilityManager, new FileInputStream( customConfigurations ) );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-configurations.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-configurations.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (InvalidComponentException e) {
				// this can happen
				String message = e.getMessage();
				message = StringUtil.replace( message, "configurations.xml", "custom-configurations.xml" );
				throw new InvalidComponentException( message, e );
			}
		}
	}

	/**
	 * Clears all stored configurations from memory.
	 */
	public void clear() {
		this.configurationsByIdentifier.clear();
	}
}
