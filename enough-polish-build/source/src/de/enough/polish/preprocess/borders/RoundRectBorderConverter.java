/*
 * Created on 10-Mar-2004 at 15:59:40.
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

import de.enough.polish.preprocess.css.BorderConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;

import de.enough.polish.BuildException;

import java.util.Map;

/**
 * <p>Creates a border with round edges.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class RoundRectBorderConverter extends BorderConverter {
	
	/**
	 * Creates a new instance
	 */
	public RoundRectBorderConverter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BorderConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(Map border, Style style, StyleSheet styleSheet) throws BuildException 
	{
		String arc = (String) border.get("arc");
		if (arc != null) {
			parseInt( "arc", arc );
		} else {
			arc = "10";
		}
		String arcHeight = (String) border.get("arc-height");
		if (arcHeight != null) {
			parseInt( "arc-height", arc );
		} else {
			arcHeight = arc;
		}
		String arcWidth = (String) border.get("arc-width");
		if (arcWidth != null) {
			parseInt( "arc-width", arc );
		} else {
			arcWidth = arc;
		}
		return "new " + BORDERS_PACKAGE + "RoundRectBorder( " 
					+ this.color + "," + this.width + ", " 
					+ arcWidth + ", " + arcHeight + ")";
	}
}
