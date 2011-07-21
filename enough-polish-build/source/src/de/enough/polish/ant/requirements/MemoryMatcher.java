/*
 * Created on 11-Feb-2004 at 20:32:08.
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
package de.enough.polish.ant.requirements;

import de.enough.polish.BuildException;

import java.util.HashMap;

/**
 * <p>Compares memory value like "120+ kb" and "5mb".</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        11-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class MemoryMatcher 
implements Matcher 
{
	private static final HashMap UNITS = new HashMap();
	static {
		UNITS.put( "bytes", new Long(1));
		UNITS.put( "b", new Long(1));
		UNITS.put( "kb", new Long(1024));
		UNITS.put( "mb", new Long(1024 * 1024));
		UNITS.put( "gb", new Long(1024 * 1024 * 1024));
	}
	
	private long bytes;
	private boolean equalsOrGreater;
	
	/**
	 * Creates a new memory matcher.
	 * 
	 * @param value the needed memory, e.g. "120+ k"
	 */
	public MemoryMatcher( String value ) {
		this.bytes = getBytes( value );
		this.equalsOrGreater = value.indexOf('+') != -1;
	}
	
	/**
	 * Gets the number of bytes of the given value.
	 *  
	 * @param value the memory, e.g. "1 kb"
	 * @return the number of bytes of the given memory, e.g. 1024 for "1 kb"
	 */
	public static final long getBytes( String value ) {
		value = value.trim().toLowerCase();		
		int splitPos = value.indexOf('+');
		if (splitPos == -1) {
			splitPos = value.indexOf(' ');
		}
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
		if (valueString.indexOf('.') == -1) {
			valueNumber = Integer.parseInt( valueString );
		} else {
			valueNumber = Double.parseDouble( valueString );
		}
		Long unitMultiply = (Long) UNITS.get(valueUnit);
		if (unitMultiply == null) {
			throw new BuildException("Invalid memory-value [" + value +"] / unit [" + valueUnit + "] found: please specify a valid unit (kb, mb etc).");
		}
		return (long) (valueNumber * unitMultiply.longValue());
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Matcher#matches(java.lang.String)
	 */
	public boolean matches(String deviceValue) {
		if ("dynamic".equals( deviceValue )) {
			return true;
		}
		long deviceBytes = getBytes( deviceValue );
		if (this.equalsOrGreater) {
			return deviceBytes >= this.bytes;
		} else {
			return deviceBytes == this.bytes;
		}
	}

}
