/*
 * Created on Jun 26, 2008 at 3:13:04 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

/**
 * <p>Wraps a numerical value that may have a unit such as 'px' (pixel), '%' (percent), 'em' (font height). Absolute values such as 'cm', 'mm', 'pt' and 'pc' cannot be supported without knowing the 'pixels per inch' or similar setting (which are not present on J2ME handsets).</p>
 *
 * <p>Copyright Enough Software 2008 - 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Andre Schmidt
 */
public class Dimension implements Externalizable
{
	
	private static final int VERSION = 100;
	
	private final static int UNIT_UNDEFINED = Integer.MIN_VALUE;
	
	/** A pixel value is absolute */
	public final static int UNIT_PIXEL = 0;
	/** A percentage value is taken relative to some other '100%' value */
	public final static int UNIT_PERCENT = 1;
	/** A value relative to the font's height. */
	public final static int UNIT_EM = 1;
	/** One point equals one dot in a display that has 72dpi */
	public final static int UNIT_POINT = 2;

	private int unit;
	private int value;
	private boolean isPercent;
	private int factor;
	private String valueAsString;
	
	
	/**
	 * Creates a new absolute (pixel) dimension.
	 * 
	 * @param value the integer value
	 * 
	 */
	public Dimension( int value )
	{
		this( value, false );
	}

	/**
	 * Creates a new percent or absolute integer value.
	 * 
	 * @param value the integer value
	 * @param isPercent true when the integer value is a percentage value
	 * 
	 */
	public Dimension( int value, boolean isPercent )
	{
		this.value = value;
		this.isPercent = isPercent;
		this.factor = 1;
	}
	
	/**
	 * Creates a new percent value with fake floating point support.
	 * 
	 * @param value the floating point percent value that has been multiplied with the factor 
	 * @param factor the factor
	 * 
	 */
	public Dimension( int value, int factor )
	{
		this.value = value;
		this.factor = factor;
		this.isPercent = true;
	}
	
	/**
	 * Creates a new value with fake floating point support.
	 * 
	 * @param value the floating point percent value that has been multiplied with the factor 
	 * @param factor the factor
	 * @param unit the unit
	 * 
	 */
	public Dimension( int value, int factor, int unit )
	{
		this.value = value;
		this.factor = factor;
		this.isPercent = (unit == UNIT_PIXEL);
		this.unit = unit;
	}

	
	/**
	 * Creates a new dimension that is parsed later
	 * 
	 * @param value the value that is parsed at a later stage
	 */
	public Dimension( String value ) {
		this.valueAsString = value;
	}
	
	/**
	 * Retrieves the actual value
	 * @param range the value that corresponds to 100%
	 * @return the integer value
	 */
	public int getValue( int range ) {
		if (this.valueAsString != null) {
			resolve();
		}
		
		if (this.isPercent) {
			return (range * this.value) / (this.factor * 100);
		}
		return this.value;
	}
	
	/**
	 * Returns the unit of the dimension
	 * @return the unit
	 */
	public int getUnit() {
		// if the unit is undefined ...
		if(this.unit == UNIT_UNDEFINED) {
			// call resolve
			resolve();
		}
		
		return this.unit;
	}
	
	/**
	 * Sets a new value, keeping the percentage setting the same.
	 * @param value the new value
	 */
	public void setValue( int value ) {
		this.value = value;
	}

	/**
	 * Sets a new value.
	 * @param value the new value
	 * @param isPercent true when the integer value is a percentage value
	 */
	public void setValue( int value, boolean isPercent ) {
		setValue( value, 1, isPercent ? UNIT_PERCENT : UNIT_PIXEL );
	}
	
	/**
	 * Sets a new percentage value with fake floating point support.
	 * 
	 * @param value the floating point percent value that has been multiplied with the factor 
	 * @param factor the factor
	 */
	public void setValue( int value, int factor ) {
		setValue( value, factor, UNIT_PERCENT );
	}

	
	/**
	 * Sets a new value with fake floating point support.
	 * 
	 * @param value the floating point percent value that has been multiplied with the factor 
	 * @param factor the factor
	 * @param unit the unit
	 */
	public void setValue( int value, int factor, int unit ) {
		this.value = value;
		this.factor = factor;
		this.unit = unit;
		this.isPercent = (unit == UNIT_PERCENT);
	}

	
	/**
	 * Sets a new (possibly complex) value.
	 * 
	 * @param value the new value
	 */
	public void setValue( String value ) {
		this.valueAsString = value;
		this.unit = UNIT_UNDEFINED;
	}


	/**
	 * @return true when this is a percentage value
	 */
	public boolean isPercent()
	{
		if (this.valueAsString != null) {
			resolve();
		}
		return this.isPercent;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException
	{
		int version = in.readInt();
		this.value = in.readInt();
		this.factor = in.readInt();
		this.isPercent = in.readBoolean();
		if (version != VERSION) {
			//#debug warn
			System.out.println("Unsupported dimension version " + version);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException
	{
		if (this.valueAsString != null) {
			resolve();
		}
		out.writeInt( VERSION );
		out.writeInt( this.value );
		out.writeInt( this.factor );
		out.writeBoolean( this.isPercent );
		
	}

	private void resolve() {
		String v = this.valueAsString;
		int l = v.length();
		
		if (v.charAt(l-1) == '%') {
			int f = 1;
			boolean dotFound = false;
			StringBuffer buffer = new StringBuffer(l);
			for (int i=0; i<l-1; i++) {
				char c = v.charAt(i);
				if (c == '.') {
					if (dotFound) {
						//#debug warn
						System.out.println("Encountered invalid dimension: " + v );
						break;
					}
					dotFound = true;
					continue; // do not multiply the factor at the dot position itself
				} else if (Character.isDigit(c)){
					buffer.append(c);
				} else {
					if (c == ' ') {
						break;
					}
					//#debug warn
					System.out.println("Encountered invalid dimension: " + v );
					break;
				}
				if (dotFound) {
					f *= 10;
				}
			}
			this.isPercent = true;
			this.unit = UNIT_PERCENT;
			this.factor = f;
			this.value = Integer.parseInt( buffer.toString() );
			//System.out.println("resolved " + this.valueAsString + " to " + this.value + ", with factor=" + this.factor	 );
			this.valueAsString = null;
		} else if (v.endsWith("pt")) {
			v = v.substring(0, l-2).trim();
			if(parseNumberValue(v)) {
				this.unit = UNIT_POINT;
			}
		} else {
			if (v.endsWith("px")) {
				v = v.substring(0, l-2).trim();
			}
			if(parseNumberValue(v)) {
				this.unit = UNIT_PIXEL;
			}
		}  
		
	}
	
	private boolean parseNumberValue(String v) {
		try {
			this.value = Integer.parseInt(v);
		} catch (NumberFormatException e) {
			//#debug warn
			System.out.println("Encountered invalid dimension: " + this.valueAsString );
			int length = v.length(); 
			StringBuffer buffer = new StringBuffer(length);
			for (int i=0; i<length-1; i++) {
				char c = v.charAt(i);
				if (Character.isDigit(c)){
					buffer.append(c);
				} else {
					break;
				}
			}
			this.value = Integer.parseInt(buffer.toString());
			return false;
		}
		this.isPercent = false;
		this.factor = 1;
		this.valueAsString = null;
		return true;
	}

}
