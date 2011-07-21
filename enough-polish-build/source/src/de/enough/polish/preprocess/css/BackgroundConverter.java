/*
 * Created on 09-Mar-2004 at 20:47:44.
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
 * <p>The base class for all backgrounds.</p>
 * <p>The background creator is responsible for creating backgrounds
 *    for styles.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class BackgroundConverter extends Converter {

	protected static final String BACKGROUNDS_PACKAGE = "de.enough.polish.ui.backgrounds.";

	protected String color;
	protected String colorConstructor;
	protected String borderWidth;
	protected String borderColor;
	protected String borderColorConstructor;
	protected boolean hasBorder;
	
	protected String styleName;


	/**
	 * Creates a new empty background
	 */
	public BackgroundConverter() {
		super();
	}
		
	/**
	 * Adds the J2ME code for the given background.
	 * 
	 * @param codeList the list at which the generated could should be appended to
	 * @param background the map containing all background settings
	 * @param backgroundName the name of this background
	 * @param style the parent style if any
	 * @param styleSheet the style-sheet into which the style is embedded
	 * @param isStandalone true when a new public background-field should be created,
	 *        otherwise the background will be embedded in a style instantiation. 
	 * @throws BuildException when there are invalid CSS declarations in the given background
	 */
	public void addBackground( ArrayList codeList, 
			Map background,
			String backgroundName,
			Style style, 
			StyleSheet styleSheet,
			boolean isStandalone)
	throws BuildException
	{
		this.styleName = backgroundName;
		// check if no background at all should be used:
		String bg = (String) background.get("background");
		if (bg != null && "none".equals(bg) ) {
			if (isStandalone) {
				codeList.add( STANDALONE_MODIFIER + "Background " + backgroundName + "Background = null;\t// background:none was specified");
			} else {
				codeList.add( "\t\tnull,\t// background:none was specified");
			}
			return;
		}
		// parse standard values:
		this.color = (String) background.get("color");
		if (this.color == null) {
			this.color = "0xFFFFFF"; // white is default background color
		} else {
			this.color = this.colorConverter.parseColor(this.color);
		}
		this.colorConstructor = this.colorConverter.generateColorConstructor( this.color );
		
		this.borderWidth = (String) background.get("border-width");
		if (this.borderWidth != null) {
			// check if the border with is a correct value:
			this.borderWidth = Integer.toString( parseInt( "border-width", this.borderWidth ) );
			this.hasBorder = true;
		}
		this.borderColor = (String) background.get("border-color");
		if (this.borderColor != null) {
			this.hasBorder = true;
			this.borderColor = this.colorConverter.parseColor( this.borderColor );
			if (this.borderWidth == null) {
				this.borderWidth = "1";
			}
			this.borderColorConstructor = this.colorConverter.generateColorConstructor( this.borderColor );
		} else if (this.borderWidth != null) {
			this.borderColor = "0x000000"; // default border color is black
			this.borderColorConstructor = this.colorConverter.generateColorConstructor( this.borderColor );
		}
		if (isStandalone) {
			codeList.add( STANDALONE_MODIFIER + "Background " + backgroundName + "Background = ");
		}
		String newStatement = createNewStatement( background, style, styleSheet );
		if (isStandalone) {
			newStatement = "\t\t" + newStatement + ";";
		} else {
			newStatement = "\t\t" + newStatement + ",";
		}
		codeList.add( newStatement);
	}
	
	/**
	 * Creates the statement for a new background based on the given properties.
	 * 
	 * @param background the map containing all background settings
	 * @param style the parent style
	 * @param styleSheet the style-sheet into which the style is embedded
	 * @return the new statement, e.g. "new de.enough.polish.ui.backgrounds.SimpleBackground( 0x000000 )"
	 * 		no semicolon or comma must appended.
	 * @throws BuildException when there are invalid CSS declarations in the given background
	 */
	protected abstract String createNewStatement( 
			Map background, 
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
		return parseInt( this.styleName, "background", name, value );
	}
	
	/**
	 * Parse the given stroke setting
	 * 
	 * @param name the name of the field
	 * @param value the float value as a String. 
	 * @return the resulting stroke value, either "Graphics.SOLID" or "Graphics.DOTTED"
	 * @throws BuildException when the value could not be parsed.
	 */
	public String parseStroke( String name, String value) {
		return parseStroke( this.styleName, "background", name, value );
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
		return parseBoolean( this.styleName, "background", name, value );
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
		return parseFloat( this.styleName, "background", name, value );
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
