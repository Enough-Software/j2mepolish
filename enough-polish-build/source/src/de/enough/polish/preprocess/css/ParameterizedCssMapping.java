/*
 * Created on 02-Nov-2005 at 02:19:42.
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
package de.enough.polish.preprocess.css;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.enough.polish.BuildException;

import org.jdom.Element;

/**
 * <p>Maps a short CSS name to any string.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        02-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ParameterizedCssMapping extends CssMapping {
	
	private List parameters;
	private Map parametersByName;
	
	/**
	 * Creates a new mapping.
	 * 
	 * @param definition the definition 
	 */
	public ParameterizedCssMapping( CssAttribute parent, Element definition ) {
		super(definition);
		CssAttributesManager manager = CssAttributesManager.getInstance();
		if (manager == null) {
			throw new BuildException("Unable to get CssAttributesManager for reading parameterized CSS attributes.");
		}
		this.parameters = new ArrayList();
		this.parametersByName = new HashMap();
		List parameterList = definition.getChildren("param");
		for (Iterator iter = parameterList.iterator(); iter.hasNext();) {
			Element parameterDefinition = (Element) iter.next();
			CssAttribute attribute = manager.createCssAttribute(parameterDefinition);
			this.parameters.add( attribute );
			this.parametersByName.put( attribute.getName(), attribute );
			manager.addImplicitAttribute( attribute, getFrom(), parent );
		}
	}

	/**
	 * Retrieves all known parameters
	 * @return all parameters of this mapping
	 */
	public CssAttribute[] getParameters() {
		return (CssAttribute[]) this.parameters.toArray( new CssAttribute[ this.parameters.size() ] );
	}

	/**
	 * Retrieves the specfied attribute.
	 * 
	 * @param name the name of the attribute
	 * @return the attribute or null when it is not defined
	 */
	public CssAttribute getParameter(String name)
	{
		return (CssAttribute) this.parametersByName.get(name);
	}

}
