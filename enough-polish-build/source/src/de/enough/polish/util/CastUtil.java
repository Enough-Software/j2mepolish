/*
 * Created on 26-Feb-2004 at 10:02:20.
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

/**
 * <p>A helper class for casting values between different types.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        26-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class CastUtil {
	
	/**
	 * Determines whether the given object represents a boolean "true".
	 * 
	 * @param value The value
	 * @return True when the value is a Boolean representing true,
	 *         or when the String representation of the value
	 *         is either "true" or "yes".
	 *         False is returned in all other cases, e.g. when null is given.
	 */
	public static boolean getBoolean( Object value ) {
		if (value == null) {
			return false;
		}
		if (value instanceof Boolean) {
			return ((Boolean)value).booleanValue();
		}
		String valueStr = value.toString();
		return ("true".equalsIgnoreCase(valueStr))
			  || ("yes".equalsIgnoreCase(valueStr)) 
			  || ("on".equalsIgnoreCase(valueStr));
	}
	
	/**
	 * Retrieves the integer-value represented by the given object.
	 * 
	 * @param value the value which represents an integer
	 * @return the integer value
	 * @throws NullPointerException when the given value is null
	 * @throws NumberFormatException when the value does not represent an integer
	 */
	public static int getInt( Object value ) {
		if (value == null) {
			throw new NullPointerException("Unable to parse int-value [null].");
		}
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		return Integer.parseInt(value.toString());
	}
	
	/**
	 * Transforms the given byte value to an positive int between 0 and 255.
	 * 
	 * @param value the byte value
	 * @return the unsigned integer value
	 */
	public static int toUnsignedInt( byte value ) {
		int intValue = value & 0x7F;
		return value < 0 ? intValue + 128 : intValue;
	}
	
	/**
	 * Transforms the given integer value to an byte-value representing a number between 0 and 255.
	 * 
	 * @param value the integer value between 0 and 255
	 * @return the byte value representing that number
	 */
	public static byte toUnsignedByte( int value ) {
		if (value > 255 || value < 0) {
			throw new IllegalArgumentException("Unsigned byte value range is between 0 and 255 - the value [" + value + "] is not valid." );
		}
		return (byte) value;
	}

}
