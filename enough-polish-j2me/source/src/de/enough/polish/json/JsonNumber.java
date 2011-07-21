/*
 * Copyright (c) 2011 Robert Virkus / Enough Software
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
package de.enough.polish.json;


/**
 * This class implements a JSON number. Internally, JSON numbers are stored as Strings.
 * @author Ovidiu Iliescu
 *
 */
public class JsonNumber implements JsonItem {
	
	/**
	 * The String representation of the number
	 */
	protected String stringRepresentation;
	
	/**
	 * Creates a new JSON number
	 * @param stringRepresentation the string representation of the number
	 */
	public JsonNumber(String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}
	
	/**
	 * Creates a new JSON number
	 * @param number the number
	 */
	public JsonNumber(int number) {
		this.stringRepresentation = String.valueOf(number);
	}
	
	/**
	 * Creates a new JSON number
	 * @param number the number
	 */
	public JsonNumber(Integer number) {
		this.stringRepresentation = number.toString();
	}
	
	/**
	 * Creates a new JSON number
	 * @param number the number
	 */
	public JsonNumber(long number) {
		this.stringRepresentation = String.valueOf(number);
	}
	
	/**
	 * Creates a new JSON number
	 * @param number the number
	 */
	public JsonNumber(Long number) {
		this.stringRepresentation = number.toString();
	}
	
	//#if polish.hasFloatingPoint	
	/**
	 * Creates a new JSON number
	 * @param number the number
	 */
	public JsonNumber(float number) {
		this.stringRepresentation = String.valueOf(number);
	}
	//#endif
	
	//#if polish.hasFloatingPoint	
	/**
	 * Creates a new JSON number
	 * @param number the number
	 */
	public JsonNumber(Float number) {
		this.stringRepresentation = number.toString();
	}
	//#endif
	
	//#if polish.hasFloatingPoint	
	/**
	 * Creates a new JSON number
	 * @param number the number
	 */
	public JsonNumber(Double number) {
		this.stringRepresentation = number.toString();
	}
	//#endif
	
	/**
	 * Creates a new JSON number
	 * @param number the number
	 */
	public JsonNumber(byte number) {
		this.stringRepresentation = String.valueOf(number);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.stringRepresentation;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.json.JSONItem#serializeToStringBuffer(java.lang.StringBuffer)
	 */
	public void serializeToStringBuffer(StringBuffer stringBuffer) {
		stringBuffer.append(this.stringRepresentation);		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.json.JSONItem#serializeToString()
	 */
	public String serializeToString() {
		StringBuffer buffer = new StringBuffer();
		serializeToStringBuffer(buffer);
		return buffer.toString();
	}
	
	/**
	 * Convenience method for getting the long value of this number
	 * @return the long value
	 * @throws NullPointerException when the string representation is null
	 * @throws NumberFormatException when the number could not be parsed
	 */
	public long toLong() {
		return Long.parseLong(this.stringRepresentation);
	}

	/**
	 * Convenience method for getting the int value of this number
	 * @return the integer value
	 * @throws NullPointerException when the string representation is null
	 * @throws NumberFormatException when the number could not be parsed
	 */
	public int toInt() {
		return Integer.parseInt(this.stringRepresentation);
	}
	
	//#if polish.hasFloatingPoint
	/**
	 * Convenience method for getting the float value of this number
	 * @return the float value
	 * @throws NullPointerException when the string representation is null
	 * @throws NumberFormatException when the number could not be parsed
	 */
	public float toFloat() {
		return Float.parseFloat(this.stringRepresentation);
	}
	//#endif

	
	//#if polish.hasFloatingPoint
	/**
	 * Convenience method for getting the double value of this number
	 * @return the double value
	 * @throws NullPointerException when the string representation is null
	 * @throws NumberFormatException when the number could not be parsed
	 */
	public double toDouble() {
		return Double.parseDouble(this.stringRepresentation);
	}
	//#endif

}
