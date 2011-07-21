/*
 * Created on 27-April-2007 at 18:30:12.
 *
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.ant.requirements;

import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;

/**
 * <p>Select all NetBeans configurations of a project.</p>
 *
 * <p>Copyright Enough Software  2007</p>

 * <pre>
 * history
 *        27-April-2007 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class NetBeansAllConfigurationsRequirement extends IdentifierRequirement {

	private String[] identifiers;

	/**
	 * Creates a new identifier requirement.
	 * 
	 * @param value the allowed identifiers seperated by commas.
	 */
	public NetBeansAllConfigurationsRequirement(String value) {
		super(value);
		//this.identifiers = StringUtil.splitAndTrim(value, ',');
		this.identifiers = null;
		System.out.println("NetBeansAllConfigurationsRequirement: created");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.build.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		try {
		String deviceIdentifier = device.getIdentifier();
		if (this.identifiers == null) {
			getIdentifers();
			if (this.identifiers == null) {
				System.out.println("UNABLE TO LOAD CONFIGURATIONS VALUES: environment=" + Environment.getInstance() );
				throw new BuildException("Stop");
			}
		}
		for (int i = 0; i < this.identifiers.length; i++) {
			String identifier = this.identifiers[i];
			if (identifier.equals( deviceIdentifier)) {
				return true;
			}
		}
		return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Retrieves all desired identifiers.
	 * 
	 * @return all desired identifiers
	 */
	public String[] getIdentifers() {
		try {
		if (this.identifiers == null) {
			Map variables = getBuildProperties();
			Map identifiersByName = new HashMap();
			Object[] keys = variables.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i].toString();
				if (key.startsWith("configs.")) {
					String identifier = key.substring("configs.".length());
					int dotPos = identifier.indexOf('.');
					if (dotPos != -1) {
						identifier = identifier.substring(0, dotPos );
					}
					if (identifier.indexOf('/') == -1) {
						identifier = identifier.replace('_', '/');
					}
					System.out.println("found identifier " + identifier);
					identifiersByName.put(identifier, Boolean.TRUE );
				}
			}
			this.identifiers = (String[]) identifiersByName.keySet().toArray( new String[ identifiersByName.size() ] );
		}
		return this.identifiers;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException( e.toString(), e );
		}
	}
	
}
