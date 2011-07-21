/*
 * Created on 22-Aug-2005 at 17:19:44.
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
package de.enough.polish.preprocess.borders;

import java.util.Map;

import de.enough.polish.BuildException;

import de.enough.polish.preprocess.css.BorderConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;

/**
 * <p>Creates a drop shadow border.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-Aug-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DropShadowBorderConverter extends BorderConverter {

	/**
	 * 
	 */
	public DropShadowBorderConverter() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BorderConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(Map border, Style style,
			StyleSheet styleSheet) 
	throws BuildException 
	{
		String offset = (String) border.get("offset");
		if (offset == null) {
			offset = "1"; // default ofset
		} else {
			parseInt( "offset", offset );
		}
		String innerColorStr = (String) border.get("inner-color");
		if (innerColorStr == null ) {
			innerColorStr = this.color;
		}
		innerColorStr = parseColor( innerColorStr );
		if ( !isAlphaColor(innerColorStr) ) {
			int innerColor = Integer.parseInt(innerColorStr.substring(2), 16) | 0xFF000000;
			innerColorStr = "0x" + Integer.toHexString( innerColor );
		}
		String outerColorStr = (String) border.get("outer-color");
		if (outerColorStr == null ) {
			outerColorStr = this.color;
		}
		outerColorStr = parseColor( outerColorStr );
		if ( !isAlphaColor(outerColorStr) ) {
			int outerColor = Integer.parseInt(outerColorStr.substring(2), 16) | 0x10000000;
			outerColorStr = "0x" + Integer.toHexString( outerColor );
		}
		String orientation = BORDERS_PACKAGE + "DropShadowBorder.BOTTOM_RIGHT";
		String orientationStr = (String) border.get("orientation");
		if ("top-right".equals(orientationStr) || "right-top".equals(orientationStr)) {
			orientation = BORDERS_PACKAGE + "DropShadowBorder.TOP_RIGHT";
		} else if ("top-left".equals(orientationStr) || "left-top".equals(orientationStr)) {
			orientation = BORDERS_PACKAGE + "DropShadowBorder.TOP_LEFT";
		} else if ("bottom-left".equals(orientationStr) || "left-bottom".equals(orientationStr)) {
			orientation = BORDERS_PACKAGE + "DropShadowBorder.BOTTOM_LEFT";
		} else if ("all".equals(orientationStr) || "all-sides".equals(orientationStr)) {
			orientation = BORDERS_PACKAGE + "DropShadowBorder.ALL";
		}
		return "new " + BORDERS_PACKAGE + "DropShadowBorder( " 
				+ innerColorStr + ", " 
				+ outerColorStr + ", "  
				+ this.width  + ", " 
				+ offset   + ", " 
				+ orientation + ")";
	}

}
