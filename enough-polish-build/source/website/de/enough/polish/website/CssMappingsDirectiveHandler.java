/*
 * Created on Dec 21, 2007 at 2:32:05 PM.
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
package de.enough.polish.website;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.preprocess.css.ParameterizedCssMapping;
import de.enough.webprocessor.util.StringList;

/**
 * <p>Handles cssattributes tag and includes all CSS attribute definitions for a specific class</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Dec 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssMappingsDirectiveHandler extends CssDirectiveHandler
{
	
	/**
	 * Creates a new handler
	 *
	 */
	public CssMappingsDirectiveHandler() {
		// the handler
	}
	

	/* (non-Javadoc)
	 * @see de.enough.webprocessor.DirectiveHandler#processDirective(java.lang.String, java.lang.String, java.lang.String, de.enough.webprocessor.util.StringList)
	 */
	public String processDirective(String directiveName, String directive, String fileName, StringList lines)
	{
		String attributeAndMappingName = directive.substring( directiveName.length() ).trim();
		int mappingStart = attributeAndMappingName.indexOf(' ');
		if (mappingStart == -1) {
			System.err.println("Invalid syntax for <%" + directive + " %>: first parameter needs to  be the attribute name, e.g. \"background\" and the second, space separated parameter the type, e.g. \"combined\", now got [" + attributeAndMappingName + "]." );
			return "";
		}
		String attributeName = attributeAndMappingName.substring(0, mappingStart );
		String mappingName = attributeAndMappingName.substring(mappingStart).trim();
		CssAttribute main = this.cssManager.getAttribute(attributeName);
		if (main == null) {
			System.err.println("Unable to load CSS attribute \"" + attributeName + "\" - not found.");
			return "";
		}
		CssMapping mapping = main.getMapping(mappingName);
		if (mapping == null) {			
			System.err.println("Unable to load CSS mapping \"" + mappingName + "\" of type \"" + attributeName + "\": not found.");
			return "";
		} else if (! (mapping instanceof ParameterizedCssMapping)) {
			System.err.println("CSS mapping \"" + mappingName + "\" is no a ParameterizedCssMapping: " + mapping );
			return "";
		}
		CssAttribute[] attributes = ((ParameterizedCssMapping)mapping).getParameters();  
		return processAttributes(attributes, mapping.getClass().getName(), mapping.getClass() );
	}

}
