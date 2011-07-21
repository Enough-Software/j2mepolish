/*
 * Created on 10-Mar-2004 at 11:15:57.
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

import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;

import de.enough.polish.util.CastUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Base class for several Creator classes.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Converter {
	protected final static String STANDALONE_MODIFIER = "\tpublic final static ";
	protected final static String STANDALONE_MODIFIER_NON_FINAL = "\tpublic static ";
	protected ColorConverter colorConverter;

	public static final Map ANCHORS = new HashMap();
	static {
		ANCHORS.put( "left", "Graphics.LEFT" );
		ANCHORS.put( "right", "Graphics.RIGHT" );
		ANCHORS.put( "center", "Graphics.HCENTER" );
		ANCHORS.put( "hcenter", "Graphics.HCENTER" );
		ANCHORS.put( "h-center", "Graphics.HCENTER" );
		ANCHORS.put( "horizontal-center", "Graphics.HCENTER" );
		ANCHORS.put( "top", "Graphics.TOP" );
		ANCHORS.put( "bottom", "Graphics.BOTTOM" );
		ANCHORS.put( "vcenter", "Graphics.VCENTER" );
		ANCHORS.put( "v-center", "Graphics.VCENTER" );
		ANCHORS.put( "vertical-center", "Graphics.VCENTER" );
	}

	/**
	 * Creates a new instance. 
	 */
	public Converter() {
		super();
	}
	
	/**
	 * Sets the color converter.
	 * 
	 * @param colorConverter the initialised color converter
	 */
	public void setColorConverter( ColorConverter colorConverter ) {
		this.colorConverter = colorConverter;
	}

	/**
	 * Parses the given integer.
	 * 
	 * @param styleName the style name
	 * @param groupName the name of the group
	 * @param name the name of the field
	 * @param value the int value as a String
	 * @return the int value.
	 * @throws BuildException when the value could not be parsed.
	 */
	public static final int parseInt(String styleName, String groupName, String name, String value) {
		try {
			int l = value.length();
			if (l > 2 && value.endsWith("px")) {
				value = value.substring(0, l - 2);
			}
			return Integer.parseInt( value );
		} catch (Exception e) {
			// check for property functions:
			Environment env = Environment.getInstance();
			try {
				String processedValue = env.getProperty( "calculate(" + value + ")", true);
				if ( !processedValue.equals(value) ) {
					return Integer.parseInt( processedValue );
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				if (value.indexOf('(') != -1) {
					e = e1;
				}
			}
			throw new BuildException("Unable to parse the field [" + groupName + "-" + name + "] with the value [" + value + "] from the style [" + styleName + "]: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Parses the given boolean value.
	 * 
	 * @param styleName the style name
	 * @param groupName the name of the group
	 * @param name the name of the field
	 * @param value the boolean value as a String
	 * @return the boolean value.
	 * @throws BuildException when the value could not be parsed.
	 */
	public boolean parseBoolean(String styleName, String groupName, String name, String value) {
		return CastUtil.getBoolean( value );
	}

	
	/**
	 * Parses the given number which can be a percentage value.
	 * If the number is a percentage value, 
	 *  
	 * @param styleName the style name
	 * @param groupName the name of the group
	 * @param name the name of the field
	 * @param value the int or percentage value as a String. A percentage value needs
	 *          to end with a "%" character.
	 * @param relativeValue the number representing 100% if a percentage value is given.
	 * @return the resulting int value
	 * @throws BuildException when the value could not be parsed or when a percentage value
	 * 			is given and the given relativeValue is -1.
	 */
	public int parseInt(String styleName, String groupName, String name, String value, int relativeValue) {
		if (value.length() == 0) {
			throw new BuildException("Unable to parse the field [" + groupName + "-" + name + "] with the value [" + value + "] from the style [" + styleName + "]: this is not an integer value.");
		}
		int lastCharPos = value.length() - 1;
		if (value.charAt( lastCharPos ) == '%') {
			if (relativeValue == -1) {
				throw new BuildException("Unable to parse the field [" + groupName + "-" + name + "] with the value [" + value + "] from the style [" + styleName + "]: No relative value is known for the current device, e.g. no ScreenSize is defined.");
			}
			value = value.substring( 0, lastCharPos );
			int percentageValue = parseInt(styleName, groupName, name, value);
			int realValue = (percentageValue * relativeValue) / 100;
			return realValue;
		} else {
			return parseInt(styleName, groupName, name, value);
		}
	}
	
	
	/**
	 * Parse the given number into a float.
	 * 
	 * @param styleName the style name
	 * @param groupName the name of the group
	 * @param name the name of the field
	 * @param value the float value as a String. 
	 * @return the resulting float value
	 * @throws BuildException when the value could not be parsed.
	 */
	public float parseFloat( String styleName, String groupName, String name, String value ) { 
		try {
			return Float.parseFloat( value );
		} catch (NumberFormatException e) {
			throw new BuildException("Unable to parse the field [" + groupName + "-" + name + "] with the value [" + value + "] from the style [" + styleName + "]: " + e.getMessage(), e );
		}
		
	}
	
	/**
	 * Parse the given stroke setting
	 * 
	 * @param styleName the style name
	 * @param groupName the name of the group
	 * @param name the name of the field
	 * @param value the float value as a String. 
	 * @return the resulting stroke value, either "Graphics.SOLID" or "Graphics.DOTTED"
	 * @throws BuildException when the value could not be parsed.
	 */
	public String parseStroke( String styleName, String groupName, String name, String value) {
		if ("dotted".equalsIgnoreCase(value)) {
			return "Graphics.DOTTED";
		}
		return "Graphics.SOLID";
	}

	
	/**
	 * Extracts the correct url from the given resource URL.
	 * 
	 * @param url the URL of the resource, e.g. "url( myPic.png )"
	 * @return the clean J2ME url, e.g. "/myPic.png"
	 * @throws NullPointerException when the url is null
	 */
	public static final String getUrl(String url) {
		if ("none".equals( url )) {
			return null;
		}
		if ( url.startsWith("url") ) {
			int startPos = url.indexOf('(');
			int endPos = url.lastIndexOf(')');
			if (startPos != -1 && endPos != -1) {
				url = url.substring( startPos + 1, endPos ).trim();
			}
		}
		if ( url.charAt(0) != '/' ) {
			url = "/" + url;
		}
		return url;
	}
	
	/**
	 * Parses the given color.
	 * 
	 * @param value the color, e.g. "rgb( 12, 12, 12 )" or "red"
	 * @return the color in a hexadecimal representation.
	 */
	public String parseColor( String value ) {
		return this.colorConverter.parseColor(value);
	}
	
	/**
	 * Parses the given anchor value.
	 * 
	 * @param styleName the name of the style
	 * @param groupName the name of the group
	 * @param attributeName the name of the attribute
	 * @param anchorValue the actual value, e.g. "top | left"
	 * @return a string containing the correct Java code, e.g. "Graphics.TOP | Graphics.LEFT"
	 */
	public String parseAnchor( String styleName, String groupName, String attributeName, String anchorValue  ) {
		anchorValue = anchorValue.toLowerCase();
		String[] anchors;
		// the anchor value can combine several directives, e.g. "vcenter | hcenter"
		if ( anchorValue.indexOf('|') != -1 ) {
			anchors = StringUtil.splitAndTrim( anchorValue, '|');
		} else if ( anchorValue.indexOf('&') != -1 ) {
				anchors = StringUtil.splitAndTrim( anchorValue, '&');
		} else if ( anchorValue.indexOf(',') != -1 ) {
			anchors = StringUtil.splitAndTrim( anchorValue, ',');
		} else if ( anchorValue.indexOf(" and ") != -1 ) {
			anchors = StringUtil.splitAndTrim( anchorValue, " and ");
		} else if ( anchorValue.indexOf(" or ") != -1 ) {
			anchors = StringUtil.splitAndTrim( anchorValue, " or ");
		} else {
			anchors = new String[]{ anchorValue };
		}
		// now add definition for each layout:
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < anchors.length; i++) {
			if (i > 0) {
				buffer.append(" | ");
			}
			String value = anchors[i];
			String anchor = (String) ANCHORS.get( value );
			if (anchor == null) {
				throw new BuildException("Invalid CSS: the [" + attributeName + "]-value \"" + anchorValue + "\" contains the invalid value \"" + value + "\". Please adjust the style \"" + styleName+ "\" - \"" + groupName + "\".");
			}
			buffer.append( anchor );
		}
		return buffer.toString();
	}
	
	/**
	 * Determines whether the given color has a defined alpha channel.
	 * 
	 * @param color the color as a hexadecimal value
	 * @return true when the color has an alpha channel defined.
	 */
	public boolean isAlphaColor( String color ) {
		return this.colorConverter.isAlphaColor( color );
	}

	
}
