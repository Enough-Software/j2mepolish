/*
 * Created on 10-Mar-2004 at 11:14:38.
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
package de.enough.polish.preprocess.css;

import de.enough.polish.BuildException;

import java.util.ArrayList;
import java.util.Map;

/**
 * <p>Creates different borders from CSS declarations.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class BorderConverter extends Converter {

	protected static final String BORDERS_PACKAGE = "de.enough.polish.ui.borders.";
	
	protected String color;
	protected String width;
	
	protected String styleName;

	/**
	 * Creates a new instance 
	 */
	public BorderConverter() {
		super();
	}
	
	/**
	 * Adds the J2ME code for the given border.
	 * 
	 * @param codeList the list at which the generated could should be appended to
	 * @param border the map containing all border settings
	 * @param borderName the name of this border
	 * @param style the parent style
	 * @param styleSheet the style-sheet into which the style is embedded
	 * @param isStandalone true when a new public border-field should be created,
	 *        otherwise the border will be embedded in a style instantiation. 
	 * @throws BuildException when there are invalid CSS declarations in the given background
	 */
	public void addBorder( ArrayList codeList, 
			Map border, 
			String borderName,
			Style style, 
			StyleSheet styleSheet,
			boolean isStandalone)
	throws BuildException
	{
		this.styleName = borderName;
		// parse standard values:
		this.color = (String) border.get("color");
		if (this.color == null) {
			this.color = "0x000000"; // black is default border color
		} else {
			this.color = this.colorConverter.parseColor(this.color);
		}
		this.width = (String) border.get("width");
		if (this.width == null) {
			this.width = "1";	// 1 is the default border width
		} else {
			this.width = Integer.toString( parseInt( "width", this.width ) );
		}
		if (isStandalone) {
			codeList.add( STANDALONE_MODIFIER + "Border " + borderName + "Border = ");
		}
		String newStatement = createNewStatement(border, style, styleSheet);
		if (isStandalone) {
			newStatement = "\t\t" + newStatement + ";";
		} else {
			newStatement = "\t\t" + newStatement + ","; 
		}
		codeList.add( newStatement);
	}
	
	/**
	 * Creates the J2ME code for a new border based on the given properties.
	 * 
	 * @param border the map containing all border settings
	 * @param style the parent style
	 * @param styleSheet the style-sheet into which the style is embedded
	 * @return the new statement, e.g. "new de.enough.polish.ui.borders.SimpleBorder( 0x000000, 1 )"
	 * 		no semicolon or comma must appended.
	 * @throws BuildException when there are invalid CSS declarations in the given background
	 */
	protected abstract String createNewStatement( 
			Map border, 
			Style style, 
			StyleSheet styleSheet )
	throws BuildException;

	/**
	 * Parses the given integer.
	 * 
	 * @param name the name of the field
	 * @param value the int value as a String
	 * @return the int value.
	 * @throws BuildException when the value could not be parsed.
	 */
	public int parseInt( String name, String value) {
		return parseInt( this.styleName, "border", name, value );
	}
	
	/**
	 * Parses the given boolean value.
	 * 
	 * @param name the name of the field
	 * @param value the boolean value as a String
	 * @return the boolean value
	 * @throws BuildException when the value could not be parsed.
	 */
	public boolean parseBoolean(String name, String value) {
		return parseBoolean( this.styleName, "border", name, value );
	}
	
	
	/**
	 * Parses the given float.
	 * 
	 * @param name the name of the field
	 * @param value the float value as a String
	 * @return the float value.
	 * @throws BuildException when the value could not be parsed.
	 */
	public float parseFloat( String name, String value) {
		return parseFloat( this.styleName, "border", name, value );
	}
	
	/**
	 * Parses the given anchor value.
	 * 
	 * @param attributeName the name of the attribute
	 * @param anchorValue the actual value, e.g. "top | left"
	 * @return a string containing the correct Java code, e.g. "Graphics.TOP | Graphics.LEFT"
	 */
	public String parseAnchor( String attributeName, String anchorValue  ) {
		return parseAnchor( this.styleName, "background", attributeName, anchorValue );
	}


}
