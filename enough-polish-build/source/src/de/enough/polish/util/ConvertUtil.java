/*
 * Created on 05-Sep-2004 at 21:19:40.
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

import java.util.HashMap;
import java.util.Map;


/**
 * <p>Converts units like MB, kb, etc.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        05-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class ConvertUtil {
	
	private static final long BYTES = 1;
	private static final long KILO_BYTES = 1024;
	private static final long MEGA_BYTES = 1024 * KILO_BYTES;
	private static final long GIGA_BYTES = 1024 * MEGA_BYTES;
	private static final Long BYTES_KEY = new Long( BYTES );
	private static final Long KILO_BYTES_KEY = new Long( KILO_BYTES );
	private static final Long MEGA_BYTES_KEY = new Long( MEGA_BYTES );
	private static final Long GIGA_BYTES_KEY = new Long( GIGA_BYTES );
	private static final String UPPERCASE = "uppercase";
	private static final String LOWERCASE = "lowercase";
	private static final String CLASSNAME = "classname";

	private static final HashMap FUNCTIONS  = new HashMap();
	static {
		FUNCTIONS.put("bytes", BYTES_KEY);
		FUNCTIONS.put("b", BYTES_KEY);
		FUNCTIONS.put("kilobytes", KILO_BYTES_KEY);
		FUNCTIONS.put("kb", KILO_BYTES_KEY);
		FUNCTIONS.put("megabytes", MEGA_BYTES_KEY);
		FUNCTIONS.put("mb", MEGA_BYTES_KEY);
		FUNCTIONS.put("gigabytes", GIGA_BYTES_KEY);
		FUNCTIONS.put("gb", GIGA_BYTES_KEY);
		FUNCTIONS.put(UPPERCASE, UPPERCASE );
		FUNCTIONS.put(LOWERCASE, LOWERCASE );
		FUNCTIONS.put(CLASSNAME, CLASSNAME );
	}

	/**
	 * Converts the given object according to the provided function.
	 * 
	 * @param value the value, e.g. "200 kb"
	 * @param targetFunction the function-name, e.g. "bytes"
	 * @param environment the environment with any defined variables
	 * @return the converted value, e.g. new Long( 204800 )
	 */
	public static Object convert( String value, String targetFunction, Map environment ) 
	{
		Object function = FUNCTIONS.get( targetFunction ); 
		if (function == null) {
			throw new IllegalArgumentException("The target-function [" + targetFunction + "] is not supported.");
		}
		if (function == BYTES_KEY) {
			return new Long( convertToBytes( value ) );
		} else if (function == KILO_BYTES_KEY) {
			return new Double( convertToKiloBytes( value ) );
		} else if (function == MEGA_BYTES_KEY) {
			return new Double( convertToMegaBytes( value ) );
		} else if (function == GIGA_BYTES_KEY) {
			return new Double( convertToGigaBytes( value ) );
		} else if (function == UPPERCASE) {
			return value.toUpperCase();
		} else if (function == LOWERCASE) {
			return value.toLowerCase();
		} else if (function == CLASSNAME ) {
			return convertClassName( value, environment );
		}
		throw new IllegalArgumentException("The target-function [" + targetFunction + "] is not supported.");
	}

	/**
	 * Retrieves the classname either with full package declaration or without depending on whether the "polish.useDefaultPackage" variable is defined
	 *  
	 * @param value the fully qualified classname, e.g. de.enough.polish.ui.Screen
	 * @param environment all defined variables
	 * @return the classname, e.g. "Screen" when the default package should be used, otherwise the fully qualified name
	 */
	public static Object convertClassName(String value, Map environment) {
		if (environment.get("polish.useDefaultPackage") != null) {
			int lastDotIndex = value.lastIndexOf('.');
			if (lastDotIndex != -1) {
				return value.substring( lastDotIndex + 1 );
			}
		}
		return value;
	}

	/**
	 * Converts the given memory value.
	 * 
	 * @param value the value, e.g. "200 kb"
	 * @return the value in bytes as a long value, when the value is "dynamic",
	 *         -1 will be returned.
	 */
	public static long convertToBytes(String value) {
		if (value == null || value.startsWith("polish.")) {
			return -1;
		}
		value = value.trim().toLowerCase();		
		if ("dynamic".equals(value)) {
			return -1;
		}
		int splitPos = value.indexOf(' ');
		if (splitPos == -1) {
			splitPos = value.indexOf('\t');
		}
		double valueNumber;
		String valueString = null;
		String valueUnit;
		if (splitPos != -1) {
			valueString = value.substring(0, splitPos).trim();
			valueUnit = value.substring( splitPos + 1 ).trim();
		} else {
			// check number char by char:
			char[] valueChars = value.toCharArray();
			StringBuffer buffer = new StringBuffer( value.length() );
			int pos = 0;
			for (pos = 0; pos < valueChars.length; pos++) {
				char c = valueChars[pos];
				if ( Character.isDigit( c ) || (c == '.' ) ) {
					buffer.append( c );
				} else {
					break;
				}
			}
			valueString = buffer.toString();
			valueUnit = value.substring( pos ).trim();
		}
		if (valueString.length() == 0) {
			throw new IllegalArgumentException("Unable to parse the memory-value [" + value + "]: no numbers found!");
		}
		if (valueString.indexOf('.') == -1) {
			valueNumber = Integer.parseInt( valueString );
		} else {
			valueNumber = Double.parseDouble( valueString );
		}
		Long unitMultiply = (Long) FUNCTIONS.get(valueUnit);
		if (unitMultiply == null) {
			throw new IllegalArgumentException("Invalid memory-value [" + value +"] / unit [" + valueUnit + "] found: please specify a valid unit (kb, mb etc).");
		}
		return (long) (valueNumber * unitMultiply.longValue());		
	}

	/**
	 * Converts the given memory value.
	 * 
	 * @param value the value, e.g. "200 kb"
	 * @return the value in kilobytes as a double value
	 */
	public static double convertToKiloBytes(String value) {
		double bytes = convertToBytes( value );
		if (bytes == -1) {
			return -1D;
		}
		return bytes / KILO_BYTES;
	}

	/**
	 * Converts the given memory value.
	 * 
	 * @param value the value, e.g. "200 kb"
	 * @return the value in mega bytes as a double value
	 */
	public static double convertToMegaBytes(String value) {
		double bytes = convertToBytes( value );
		if (bytes == -1) {
			return -1D;
		}
		return bytes / MEGA_BYTES;
	}

	/**
	 * Converts the given memory value.
	 * 
	 * @param value the value, e.g. "200 kb"
	 * @return the value in giga bytes as a double value
	 */
	public static double convertToGigaBytes(String value) {
		double bytes = convertToBytes( value );
		if (bytes == -1) {
			return -1D;
		}
		return bytes / GIGA_BYTES;
	}
	
	/**
	 * Converts the given object to a String.
	 * In contrast to Double.toString() etc a trailing ".0" will be removed
	 * from the String-representation of the value.
	 * 
	 * @param value the value
	 * @return the string representation of that value
	 */
	public static String toString( Object value ) {
		String valueStr = value.toString();
		if (valueStr.endsWith(".0")) {
			return valueStr.substring(0, valueStr.length() - 2);
		} else {
			return valueStr;
		}
	}
	
	public static long convertToMilliseconds( String value ) {
		long multiplier = 1;
		if (value.endsWith("ms")) {
			value = value.substring(0, value.length() - 2).trim();
		} else if (value.endsWith("min")) {
			value  = value.substring(0, value.length() - 3).trim();
			multiplier = 60 * 1000;
		} else if (value.endsWith("mins")) {
			value  = value.substring(0, value.length() - 4).trim();
			multiplier = 60 * 1000;
		} else if (value.endsWith("m")) {
			value  = value.substring(0, value.length() - 1).trim();
			multiplier = 60 * 1000;
		} else if (value.endsWith("sec")) {
			value  = value.substring(0, value.length() - 3).trim();
			multiplier = 1000;
		} else if (value.endsWith("h")) {
			value  = value.substring(0, value.length() - 1).trim();
			multiplier = 60 * 60 * 1000;
		} else if (value.endsWith("hour")) {
			value  = value.substring(0, value.length() - 4).trim();
			multiplier = 60 * 60 * 1000;
		} else if (value.endsWith("hours")) {
			value  = value.substring(0, value.length() - 5).trim();
			multiplier = 60 * 60 * 1000;
		} else if (value.endsWith("s")) {
			value  = value.substring(0, value.length() - 1).trim();
			multiplier = 1000;
		}
		return Long.parseLong( value ) * multiplier;
	}
	
}
