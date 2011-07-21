/*
 * Created on 25-Apr-2005 at 14:59:04.
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
package de.enough.polish.propertyfunctions;

import java.util.HashMap;

import de.enough.polish.Environment;

/**
 * <p>Transforms memory values like "100 kb" into bytes.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        25-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BytesFunction extends PropertyFunction {

	protected static final long BYTES = 1;
	protected static final long KILO_BYTES = 1024;
	protected static final long MEGA_BYTES = 1024 * KILO_BYTES;
	protected static final long GIGA_BYTES = 1024 * MEGA_BYTES;
	protected static final Long BYTES_KEY = new Long( BYTES );
	protected static final Long KILO_BYTES_KEY = new Long( KILO_BYTES );
	protected static final Long MEGA_BYTES_KEY = new Long( MEGA_BYTES );
	protected static final Long GIGA_BYTES_KEY = new Long( GIGA_BYTES );
	
	protected static final HashMap CONVERSION_TABLE  = new HashMap();
	static {
		CONVERSION_TABLE.put("bytes", BYTES_KEY);
		CONVERSION_TABLE.put("b", BYTES_KEY);
		CONVERSION_TABLE.put("kilobytes", KILO_BYTES_KEY);
		CONVERSION_TABLE.put("kb", KILO_BYTES_KEY);
		CONVERSION_TABLE.put("Kb", KILO_BYTES_KEY);
		CONVERSION_TABLE.put("KB", KILO_BYTES_KEY);
		CONVERSION_TABLE.put("megabytes", MEGA_BYTES_KEY);
		CONVERSION_TABLE.put("mb", MEGA_BYTES_KEY);
		CONVERSION_TABLE.put("Mb", MEGA_BYTES_KEY);
		CONVERSION_TABLE.put("MB", MEGA_BYTES_KEY);
		CONVERSION_TABLE.put("gigabytes", GIGA_BYTES_KEY);
		CONVERSION_TABLE.put("gb", GIGA_BYTES_KEY);
		CONVERSION_TABLE.put("Gb", GIGA_BYTES_KEY);
		CONVERSION_TABLE.put("GB", GIGA_BYTES_KEY);
	}

	/**
	 * Creates a new function
	 */
	public BytesFunction() {
		super();
	}
	
	/**
	 * Converts the given memory value to bytes.
	 * 
	 * @param input the input, e.g. "100 kb" 
	 * @return the number of bytes, -1 when the input could not be parsed
	 */
	public static long getBytes( String input ) {
		if (input == null || input.startsWith("polish.")) {
			return -1;
		}
		input = input.trim().toLowerCase();		
		if ("dynamic".equals(input)) {
			return -1;
		}
		int splitPos = input.indexOf(' ');
		if (splitPos == -1) {
			splitPos = input.indexOf('\t');
		}
		double valueNumber;
		String valueString = null;
		String valueUnit;
		if (splitPos != -1) {
			valueString = input.substring(0, splitPos).trim();
			valueUnit = input.substring( splitPos + 1 ).trim();
		} else {
			// check number char by char:
			char[] valueChars = input.toCharArray();
			StringBuffer buffer = new StringBuffer( input.length() );
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
			valueUnit = input.substring( pos ).trim();
		}
		if (valueString.length() == 0) {
			throw new IllegalArgumentException("Unable to parse the memory-value [" + input + "]: no numbers found!");
		}
		if (valueString.indexOf('.') == -1) {
			valueNumber = Integer.parseInt( valueString );
		} else {
			valueNumber = Double.parseDouble( valueString );
		}
		Long unitMultiply = (Long) CONVERSION_TABLE.get(valueUnit);
		if (unitMultiply == null) {
			throw new IllegalArgumentException("Invalid memory-value [" + input +"] / unit [" + valueUnit + "] found: please specify a valid unit (kb, mb etc).");
		}
		return (long) (valueNumber * unitMultiply.longValue());		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments,	Environment env) 
	{
		return Long.toString( getBytes( input ) );
	}

}
