//#condition false
/*
 * Created on 17-Jul-2004 at 15:37:20.
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
package de.enough.polish.preprocess.borders;

import java.util.Map;

import de.enough.polish.BuildException;

import de.enough.polish.preprocess.css.BorderConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;

/**
 * <p>Converts CSS code into a CircleBorder instantiation.</p>
 * <p>Following CSS-attributes are supported:</p>
 * <ul>
 * 	<li><b>type</b>: the type of the border, needs to be "circle".</li>
 * 	<li><b>color</b>: the color of the border, defaults to "black".</li>
 * 	<li><b>width</b>: the width of the border, defaults to "1" pixel.</li>
 * 	<li><b>stroke-style</b>: the stroke-style, either "dotted" or "solid". Defaults to "solid". </li>
 * </ul>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        17-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CircleBorderConverter extends BorderConverter {

	public CircleBorderConverter() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BorderConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(
			Map border, 
			Style style,
			StyleSheet styleSheet ) 
	throws BuildException 
	{
		String strokeStyle = (String) border.get("stroke-style");
		String strokeStyleCode;
		if (strokeStyle != null) {
			if ("dotted".equalsIgnoreCase(strokeStyle)) {
				strokeStyleCode = "javax.microedition.lcdui.Graphics.DOTTED";
			} else {
				strokeStyleCode = "javax.microedition.lcdui.Graphics.SOLID";
			}
		} else {
			strokeStyleCode = "javax.microedition.lcdui.Graphics.SOLID";
		}
		return "new " + BORDERS_PACKAGE  + "CircleBorder( " 
			+ this.color + ", " + this.width + ", " + strokeStyleCode + ")";
	}

}
