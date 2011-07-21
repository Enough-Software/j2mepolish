/*
 * Created on 01-Mar-2004 at 20:31:43.
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
import de.enough.polish.util.StringUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>Translates colors.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>
 * 
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 * @author Eugene Markov, fixed inheritance of colors
 */
public class ColorConverter {
	
	/** the key for retrieving the color converter from the environment */
	public static final String ENVIRONMENT_KEY = "ColorConverter";
	/**
	 * Defines the standard VGA colors.
	 * Available colors are red, green, blue, lime, black, white, silver, gray,
	 * maroon, purple, fuchsia, olive, yellow, navy, teal and aqua. 
	 */
	public static final Map COLORS = new HashMap();
	/**
	 * Defines dynamic colors like COLOR_BACKGROUND, COLOR_FOREGROUND, etc which can be used on MIDP/2.0 systems.
	 */
	public static final Map DYNAMIC_COLORS = new HashMap();
	static {
		COLORS.put("red",  	 	"0xFF0000");
		COLORS.put("lime",  	"0x00FF00");
		COLORS.put("blue",   	"0x0000FF");
		COLORS.put("black",  	"0x000000");
		COLORS.put("white",  	"0xFFFFFF");
		COLORS.put("silver",  	"0xC0C0C0");
		COLORS.put("gray",  	"0x808080");
		COLORS.put("maroon",  	"0x800000");
		COLORS.put("purple",  	"0x800080");
		COLORS.put("green",  	"0x008000");
		COLORS.put("fuchsia",  	"0xFF00FF");
		COLORS.put("olive",  	"0x808000");
		COLORS.put("yellow",  	"0xFFFF00");
		COLORS.put("navy",  	"0x000080");
		COLORS.put("teal",  	"0x008080");
		COLORS.put("aqua",  	"0x00FFFF");
		
		DYNAMIC_COLORS.put("COLOR_BACKGROUND", "Color.COLOR_BACKGROUND");
		DYNAMIC_COLORS.put("COLOR_BORDER", "Color.COLOR_BORDER");
		DYNAMIC_COLORS.put("COLOR_FOREGROUND", "Color.COLOR_FOREGROUND");
		DYNAMIC_COLORS.put("COLOR_HIGHLIGHTED_BACKGROUND", "Color.COLOR_HIGHLIGHTED_BACKGROUND");
		DYNAMIC_COLORS.put("COLOR_HIGHLIGHTED_BORDER", "Color.COLOR_HIGHLIGHTED_BORDER");
		DYNAMIC_COLORS.put("COLOR_HIGHLIGHTED_FOREGROUND", "Color.COLOR_HIGHLIGHTED_FOREGROUND");
		DYNAMIC_COLORS.put("Display.COLOR_BACKGROUND", "Color.COLOR_BACKGROUND");
		DYNAMIC_COLORS.put("Display.COLOR_BORDER", "Color.COLOR_BORDER");
		DYNAMIC_COLORS.put("Display.COLOR_FOREGROUND", "Color.COLOR_FOREGROUND");
		DYNAMIC_COLORS.put("Display.COLOR_HIGHLIGHTED_BACKGROUND", "Color.COLOR_HIGHLIGHTED_BACKGROUND");
		DYNAMIC_COLORS.put("Display.COLOR_HIGHLIGHTED_BORDER", "Color.COLOR_HIGHLIGHTED_BORDER");
		DYNAMIC_COLORS.put("Display.COLOR_HIGHLIGHTED_FOREGROUND", "Color.COLOR_HIGHLIGHTED_FOREGROUND");
		DYNAMIC_COLORS.put("Color.COLOR_BACKGROUND", "Color.COLOR_BACKGROUND");
		DYNAMIC_COLORS.put("Color.COLOR_BORDER", "Color.COLOR_BORDER");
		DYNAMIC_COLORS.put("Color.COLOR_FOREGROUND", "Color.COLOR_FOREGROUND");
		DYNAMIC_COLORS.put("Color.COLOR_HIGHLIGHTED_BACKGROUND", "Color.COLOR_HIGHLIGHTED_BACKGROUND");
		DYNAMIC_COLORS.put("Color.COLOR_HIGHLIGHTED_BORDER", "Color.COLOR_HIGHLIGHTED_BORDER");
		DYNAMIC_COLORS.put("Color.COLOR_HIGHLIGHTED_FOREGROUND", "Color.COLOR_HIGHLIGHTED_FOREGROUND");
		DYNAMIC_COLORS.put("COLOR_TRANSPARENT", "Color.TRANSPARENT");
		DYNAMIC_COLORS.put("BACKGROUND_COLOR", "Color.COLOR_BACKGROUND");
		DYNAMIC_COLORS.put("BORDER_COLOR", "Color.COLOR_BORDER");
		DYNAMIC_COLORS.put("FOREGROUND_COLOR", "Color.COLOR_FOREGROUND");
		DYNAMIC_COLORS.put("HIGHLIGHTED_BACKGROUND_COLOR", "Color.COLOR_HIGHLIGHTED_BACKGROUND");
		DYNAMIC_COLORS.put("HIGHLIGHTED_BORDER_COLOR", "Color.COLOR_HIGHLIGHTED_BORDER");
		DYNAMIC_COLORS.put("HIGHLIGHTED_FOREGROUND_COLOR", "Color.COLOR_HIGHLIGHTED_FOREGROUND");
		DYNAMIC_COLORS.put("TRANSPARENT_COLOR", "Color.TRANSPARENT");
		DYNAMIC_COLORS.put("transparent", "Color.TRANSPARENT");
		DYNAMIC_COLORS.put("Color.TRANSPARENT", "Color.TRANSPARENT");
	}
	
	private HashMap tempColors;
	
	/**
	 * Creates a new colors parser.
	 */
	public ColorConverter() {
		this.tempColors = new HashMap();
	}
	
	/**
	 * Removes all found color definition from the internal cache.
	 */
	public void clear() {
		this.tempColors.clear();
	}
	
	/**
	 * Parses the given color definition and returns the appropriate hex-definition.
	 * 
	 * @param definition the value of the color, e.g. "black", "#00ff00", "rgb( 340, 0, 0)"
	 * @return the hexadecimal color-value, e.g. "0x000000" or
	 * 		   "Item.TRANSPARENT" when the definition equals "transparent".
	 */
	public String parseColor( String definition ) {
		String dynamicValue = (String) DYNAMIC_COLORS.get(definition);
		if (dynamicValue == null) {
			dynamicValue = (String) DYNAMIC_COLORS.get(definition.toLowerCase());
		}
		if (dynamicValue != null) {
			return dynamicValue;
		}
		
		// the definition could be a color which has been defined earlier:
		String value = (String) this.tempColors.get( definition );
		if (value == null) {
			value = (String) this.tempColors.get( definition.toLowerCase() );
		}
		if (value != null) {
			return value;
		}
		
		// the definition could be a standard VGA color:
		value = (String) COLORS.get( definition );
		if (value == null) {
			value = (String) COLORS.get( definition.toLowerCase() );
		}
		if (value != null) {
			return value;
		}
		
		// the definition could be a normal hexadecimal value.
		// In CSS hex-values start with '#':
		if (definition.startsWith("#") || definition.startsWith("0x") ) {
			if (definition.charAt(0) == '#') {
				value = definition.substring( 1 );
			} else {
				value = definition.substring( 2 );
			}
			if (value.length() == 3 || value.length() == 4) {
				// an allowed shortcut in CSS is to use only one character
				// for each color, when they are equal otherwise.
				// blue is e.g. "#00F", translucent blue is "#a00f"
				StringBuffer buffer = new StringBuffer(12);
				buffer.append("0x");
				char[] chars = value.toCharArray();
				for (int i = 0; i < chars.length; i++) {
					char c = chars[i];
					buffer.append( c ).append( c );
				}
				value = buffer.toString();
				// check value:
				try {
					Long.decode(value);
				} catch (NumberFormatException e) {
					throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid hexadecimal value (" + e.getMessage() + ").");
				}
				return value;
			} // if value.length() == 3
			if (value.length() < 6 || value.length() > 8) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid hexadecimal value.");
			}
			value = "0x" + value;
			// check number:
			try {
				Long.decode(value);
			} catch (NumberFormatException e) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid hexadecimal value (" + e.getMessage() + ").");
			}
			return value;
		}
		// the color could be encoded as a decimal Red-Green-Blue value:
		if (definition.startsWith("rgb")) {
			value = definition.substring(3).trim();
			if ((!value.startsWith("(")) || (!value.endsWith(")")) ) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid RGB value. Allowed is [rgb(rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]." );
			}
			value = value.substring( 1, value.length() - 1 );
			String[] numbers = StringUtil.splitAndTrim( value, ',');
			if (numbers.length != 3) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid RGB value. Allowed is [rgb(rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]." );
			}
			return parseColors( numbers, definition );
		}
		if (definition.startsWith("argb")) {
			value = definition.substring(4).trim();
			if ((!value.startsWith("(")) || (!value.endsWith(")")) ) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid ARGB value. Allowed is [argb(aaa,rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]." );
			}
			value = value.substring( 1, value.length() - 1 );
			String[] numbers = StringUtil.splitAndTrim( value, ',');
			if (numbers.length != 4) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid ARGB value. Allowed is [argb(aaa,rrr,ggg,bbb)], e.g. [argb(128, 128, 255, 0)]." );
			}
			return parseColors( numbers, definition );
		}
		// this is an invalid color declaration:
		throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid value." );
	}
	
	/**
	 * Parses the given numbers and stores them into a hexadecimal string.
	 * 
	 * @param numbers String array with the numbers or percentage values
	 * @param definition the complete definition
	 * @return the parsed rgb or argb value as hex string
	 */
	private String parseColors(String[] numbers, String definition ) {
		StringBuffer buffer = new StringBuffer( numbers.length * 2 + 2 );
		buffer.append("0x");
		for (int i = 0; i < numbers.length; i++) {
			String numberStr = numbers[i];
			boolean isPercentage = numberStr.charAt( numberStr.length() - 1) == '%';
			int number = -1;			
			try {
				if (isPercentage) {
					String doubleStr = numberStr.substring( 0, numberStr.length() - 1);
					double percentageValue = Double.parseDouble( doubleStr );
					number = (int) ((255D * percentageValue) / 100D);
				} else {
					number = Integer.parseInt( numberStr );
				}
			} catch (NumberFormatException e) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid RGB value. Allowed is [rgb(rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]. The value [" + numberStr + "] cannot be parsed: " + e.getMessage() );
			}
			if (number > 0xFF || number < 0 ) {
				throw new BuildException("Invalid color definition in CSS: [" + definition + "] is not a valid RGB value. Allowed is [rgb(rrr,ggg,bbb)], e.g. [rgb(128, 255, 0)]. The value [" + numberStr + "] is invalid." );
			}
			String hexNumber = Integer.toHexString(number) ;
			if (hexNumber.length() < 2) {
				buffer.append( '0' );
			}
			buffer.append( hexNumber);
		}
		return buffer.toString();
	}

	/**
	 * Sets the temporary colors.
	 * 
	 * @param newColors all colors in a map.
	 * @throws BuildException when one of the given colors is invalid 
	 */
	public void oldSetTemporaryColors( Map newColors ) {
		this.tempColors = new HashMap();
		Set keys = newColors.entrySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			String colorName = (String) entry.getKey();
			HashMap map = (HashMap) entry.getValue();
			String color = (String) map.get(colorName);
			this.tempColors.put( colorName, parseColor( color ));
		}
	}
	
	/**
	 * Sets the temporary colors.
	 * 
	 * @param newColors all colors in a map.
	 * @throws BuildException when one of the given colors is invalid 
	 */
	 public void setTemporaryColors( Map newColors )
	 {
		this.tempColors = new HashMap();
		HashSet set = new HashSet( 5 );
		String[] keys = (String[]) newColors.keySet().toArray( new String[newColors.size() ] );
		// add all color definitions in lowercase as well:
		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i];
			HashMap map = (HashMap) newColors.get(key);
			String lowercase = key.toLowerCase();
			map.put(lowercase, map.get(key));
			newColors.put( lowercase, map);
		}
		// now parse colors:
		for (int i = 0; i < keys.length; i++)
		{
			String color = keys[i];
			if ( ! this.tempColors.containsKey( color ) )
			{
				//System.out.println( "color: " + color );
				set.clear();
				while( color.matches( "[a-zA-Z_][a-zA-Z0-9_-]*[a-zA-Z0-9_]" ) )
				{
					//System.out.println("resolving color " + color);
					if ( this.tempColors.containsKey( color ) ) {
						break;
					} else {
						if( set.contains( color ) ) {
							throw new BuildException( "Invalid color definition in CSS: [" +
							color + "] is cyclically." );
						}
						set.add( color );
						HashMap map = (HashMap) newColors.get( color );
						if (map == null) {
							map  = (HashMap) newColors.get( color.toLowerCase() );
						}
						String colorValue = map == null ? null : (String) map.get(color);
						if (colorValue == null && map != null) {
							colorValue = (String) map.get(color.toLowerCase());
						}
						if ( map == null || colorValue == null ) {
							throw new BuildException( "Invalid color definition in CSS: [" +
									color + "] is not a valid value." );
						}
						color = colorValue;
					 	if ( COLORS.get( color ) != null ) {
					 		//System.out.println("ColorConvert: found color " + color);
					 		break;
					 	}
					 }
				}
				if ( set.isEmpty() )
				{
					throw new BuildException("Invalid color definition in CSS: [" +
							color + "] is not a valid name." );
				} else {
					color = parseColor( color );
					for (Iterator iter1 = set.iterator(); iter1.hasNext();)
					{
						String keycolor = (String)iter1.next();
						String keycolorLowercase = keycolor.toLowerCase();
						//System.out.println( keycolor + "=" + color );
						this.tempColors.put( keycolor, color );
						this.tempColors.put( keycolorLowercase, color );
					}
				}
			}
		}
	}

	/**
	 * Determines whether the given color has a defined alpha channel.
	 * 
	 * @param color the color as a hexadecimal value
	 * @return true when the color has an alpha channel defined.
	 */
	public boolean isAlphaColor( String color ) {
		return color.startsWith( "0x") 
				&& color.length() > "0xRRGGBB".length() 
				&& !color.startsWith("0xff");
	}

	/**
	 * Determines whether the given color is a dynamic one.
	 * 
	 * @param value the value, e.g. red or COLOR_BACKGROUND
	 * @return true when the given value represents a dynamic color like COLOR_BACKGROUND, COLOR_FOREGROUND, etc
	 */
	public boolean isDynamic(String value) {
		return DYNAMIC_COLORS.get(value) != null;
	}
	
	/**
	 * Generates a new de.enough.polish.ui.Color constructor for the given color value.
	 * @param value the color value like red or COLOR_FOREGROUND
	 * @return source code that generates a new Color object
	 */
	public String generateColorConstructor(String value) {
		boolean isDynamic = isDynamic( value );
		return  "new Color( " + parseColor(value) + ", " + isDynamic + ")";
	}

	
}
