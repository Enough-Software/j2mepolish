/*
 * Created on 18-Jan-2003 at 20:58:08.
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
package de.enough.polish.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Helps to parse properties in the form of ${ property.name }.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        18-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class PropertyUtil {

	static final Pattern PROPERTY_PATTERN = 
		Pattern.compile("\\$\\{\\s*(\\w|\\.|-|\\)|\\(|\\s)+\\s*\\}"); // == \$\{\s*(\w|\.|-|\(|\)|\s)+\s*\}
	
	/**
	 * Inserts the property-values in a string with property-definitions.
	 * 
	 * @param input the string in which property definition might be included, e.g. "file=${source}/MyFile.java"
	 * @param properties the map in which all properties are defined
	 * @return the input with all properties replaced by their values.
	 * 			When a property is not defined
	 *             the full property-name is inserted instead (e.g. "${ property-name }").  
	 */
	public static String writeProperties( String input, Map properties ) {
		return writeProperties( input, properties, false );
	}

	/**
	 * Inserts the property-values in a string with property-definitions.
	 * 
	 * @param input the string in which property definition might be included, e.g. "file=${source}/MyFile.java"
	 * @param properties the map in which all properties are defined
	 * @param needsToBeDefined true when an IllegalArgumentException should be thrown when
	 *              no value for a property was found.
	 * @return the input with all properties replaced by their values.
	 * 			When a property is not defined (and needsToBeDefined is false),
	 *             the full property-name is inserted instead (e.g. "${ property-name }").  
	 * @throws IllegalArgumentException when a property-value was not found and needsToBeDefined is true.
	 */
	public static String writeProperties( String input, Map properties, boolean needsToBeDefined ) {
		Matcher matcher = PROPERTY_PATTERN.matcher( input );
		boolean propertyFound = matcher.find();
		if (!propertyFound) {
			return input;
		}
		StringBuffer buffer = new StringBuffer();
		int startPos = 0;
		while (propertyFound) {
			// append string til start of the pattern:
			buffer.append( input.substring( startPos, matcher.start() ) );
			startPos = matcher.end();
			// append property:
			String group = matcher.group(); // == ${ property.name }
											// or == ${ function( property.name ) }
											// or == ${ function( fix.value ) }
			String property = group.substring( 2, group.length() -1 ).trim(); // == property.name
			String value;
			// the property-name can also include a convert-function, e.g. bytes( polish.HeapSize )
			int functionStart = property.indexOf('(');
			if (functionStart != -1) {
				int functionEnd = property.indexOf(')', functionStart);
				if (functionEnd == -1) {
					throw new IllegalArgumentException("The function [" + property + "] needs a closing paranthesis in input [" + input + "].");
				}
				String function = property.substring(0, functionStart).trim();
				property = property.substring( functionStart + 1, functionEnd ).trim();
				String originalValue = (String) properties.get( property );
				if (originalValue == null) {
					// when functions are used, fix values can be used, too: 
					originalValue = property;
				}
				Object intermediateValue = ConvertUtil.convert( originalValue, function, properties);
				value = ConvertUtil.toString(intermediateValue);
			} else {
				value = (String) properties.get( property );
			}
			if (value == null) {
				if (needsToBeDefined) {
					throw new IllegalArgumentException("property " + group + " is not defined.");
				} else {
					value = group;
				}
			} else {
				if ( value.indexOf("${") != -1) {
					Matcher valueMatcher = PROPERTY_PATTERN.matcher( value );
					while ( valueMatcher.find() ) {
						String internalGroup = valueMatcher.group();
						String internalProperty = internalGroup.substring( 2, internalGroup.length() -1 ).trim(); // == property.name
						String internalValue = (String) properties.get( internalProperty );
						if (internalValue != null) {
							value = StringUtil.replace( value, internalGroup, internalValue);
						} else if ( needsToBeDefined ) {
							throw new IllegalArgumentException("property " + internalGroup + " is not defined.");
						}
					}
					
				}
			}
			buffer.append( value );
			// look for another property:
			propertyFound = matcher.find();
		}
		// append tail:
		buffer.append( input.substring( startPos ) );
		return buffer.toString();
	}
}
