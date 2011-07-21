/*
 * Created on Jun 27, 2008 at 10:34:45 AM.
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
package de.enough.polish.preprocess.css;

import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;

/**
 * <p>Stores information about a single declaration block.</p>
 * Introduced for allowing several animations of the same CSS attribute.
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssDeclarationBlock
{
	private String blockName;
	private final Map declarationsByAttribute;
	private final String[] attributes;
	private final String[] values;

	/**
	 * Creates a new CSS declaration block
	 * @param blockName the name of the parent attribute
	 * @param declarations the declarations
	 */
	public CssDeclarationBlock( String blockName, String[] declarations ) {
		
		this.blockName = blockName;
		this.declarationsByAttribute = new HashMap();
		int length = declarations.length;
		String lastDeclaration = declarations[ declarations.length - 1];
		if ("".equals(lastDeclaration)) {
			length--;
		}
		this.attributes = new String[ length ];
		this.values = new String[ length ];
		
		for (int i = 0; i < length; i++)
		{
			String declaration = declarations[i];
			int colonPos = declaration.indexOf(':');
			if (colonPos == -1) {
				throw new BuildException("Invalid CSS declaration block \"" + blockName + "\" : unable to parse declaration \"" + declaration + ";\" - found no colon between attribute and value.");
			}
			String attribute = declaration.substring(0, colonPos).trim();
			String value = declaration.substring(colonPos + 1).trim();
			this.declarationsByAttribute.put(attribute, value);
			this.attributes[i] = attribute;
			this.values[i] = value;
		}
	}
	
	/**
	 * Retrieves the name of this block, e.g. font or "text-color-animation"
	 * @return the name of this attribute block
	 */
	public String getBlockName() {
		return this.blockName;
	}
	
	/**
	 * Retrieves all declarations of this block
	 * @return all declarations with the attribute names as keys of the map
	 */
	public Map getDeclarationsByAttribute() {
		return this.declarationsByAttribute;
	}
	
	/**
	 * Retrieves all attributes
	 * @return the attributes as a String array
	 */
	public String[] getAttributes() {
		return this.attributes;
	}
	
	/**
	 * Retrieves all values
	 * @return the values as a String array
	 */
	public String[] getValues() {
		return this.values;
	}
	
	/**
	 * Retrieves the specified attribute
	 * @param index the index of the attribute
	 * @return the attribute
	 */
	public String getAttribute(int index) {
		return this.attributes[index];
	}

	/**
	 * Retrieves the specified value
	 * @param index the index of the value
	 * @return the value
	 */
	public String getValue(int index) {
		return this.values[index];
	}
	
	/**
	 * Retrieves the size of this block
	 * @return the number of attributes within this block
	 */
	public int size() {
		return this.attributes.length;
	}

	/**
	 * Sets the name of this block - this can be used for CSS animations of border and background attributes, for example
	 * @param blockName the new name of this block
	 */
	public void setBlockName(String blockName)
	{
		this.blockName = blockName;
	}
}
