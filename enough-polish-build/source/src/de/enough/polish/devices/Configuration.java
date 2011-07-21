/*
 * Created on 23-May-2005 at 16:04:52.
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

import de.enough.polish.exceptions.InvalidComponentException;

import org.jdom.Element;


/**
 * <p>Represents a Java based platform like MIDP/1.0, WIPI/2.0 or DoJa/3.0.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Configuration extends PolishComponent {

	
	/**
	 * Creates a new Platform.
	 * 
	 * @param definition the XML definition of this platform.
	 * @param capabilityManager manages capabilities
	 * @throws InvalidComponentException when the given definition contains errors
	 */
	public Configuration( Element definition, CapabilityManager capabilityManager )
	throws InvalidComponentException
	{
		super( null, capabilityManager, definition );
		this.identifier = definition.getChildTextTrim( "identifier");
		if (this.identifier == null) {
			System.out.println("configuration-definition=" + definition.toString() );
			throw new InvalidComponentException("Every configuration needs to define the element <identifier> - please check your configurations.xml.");
		}
		// load all capabilities:
		loadCapabilities(definition, this.identifier, "configurations.xml");
	}


}
