/*
 * Created on Apr 15, 2007 at 10:12:36 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess.css.attributes;

/**
 * <p>A numerical attribute given in percent, negative and values over 100% are also allowed. The percent sign is optional.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PercentCssAttribute extends IntegerCssAttribute {
	
	/**
	 * Creates a new instance.
	 */
	public PercentCssAttribute() {
		super();
	}

//	/* (non-Javadoc)
//	 * @see de.enough.polish.preprocess.css.attributes.IntegerCssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
//	 */
//	public String getValue(String value, Environment environment ) {
//		if (value.endsWith("%")) {
//			try {
//				int intValue = Integer.parseInt( value.substring( 0, value.length() - 1) );
//				if (this.isBaseAttribute) {
//					return "" + intValue;
//				} else {
//					return "new Integer( " + intValue + " )";
//				}
//			} catch (NumberFormatException e) {
//				throw new BuildException("Invalid CSS: The attribute [" + this.name + "] needs an percent value. The value [" + value + "] cannot be accepted.");			
//			}
//		}
//		if (this.allowedValues == null) {
//			try {
//				int intValue;
//				try {
//					intValue = Integer.parseInt( value );
//				} catch (NumberFormatException e) {
//					String processedValue = environment.getProperty( "calculate(" + value + ")", true);
//					intValue = Integer.parseInt( processedValue );
//				}
//				if (this.isBaseAttribute) {
//					return "" + intValue;
//				} else {
//					return "new Integer(" + intValue + ")";
//				}
//			} catch (NumberFormatException e) {
//				throw new BuildException("Invalid CSS: The attribute [" + this.name + "] needs an integer value. The value [" + value + "] cannot be accepted.");
//			}
//		} else {
//			// there are fixed allowed values defined:
//			for (int i = 0; i < this.allowedValues.length; i++) {
//				if (!this.isCaseSensitive) {
//					value = value.toLowerCase();
//				}
//				if (value.equals( this.allowedValues[i])) {
//					if (this.isBaseAttribute) {
//						return "" + i;
//					} else {
//						return "new Integer(" + i + ")";
//					}
//				}
//			}
//			String message = "Invalid CSS: the attribute [" + this.name + "] needs to be one "
//						+ "of the following values: [";
//			for (int i = 0; i < this.allowedValues.length; i++) {
//				message += this.allowedValues[i];
//				if (i < this.allowedValues.length - 1) {
//					message += "], [";
//				}
//			}		
//			message += "]. The value [" + value + "] is not supported.";
//			throw new BuildException( message );
//		}
//	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.attributes.IntegerCssAttribute#parseInt(java.lang.String)
	 */
	protected int parseInt(String value)
	{
		if (value.endsWith("%")) {
			value = value.substring(0, value.length() - 1).trim();
		}
		return super.parseInt(value);
	}

}
