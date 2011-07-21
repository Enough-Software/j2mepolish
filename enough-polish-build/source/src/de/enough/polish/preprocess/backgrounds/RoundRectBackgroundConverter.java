/*
 * Created on 09-Mar-2004 at 21:48:27.
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
package de.enough.polish.preprocess.backgrounds;

import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.preprocess.css.BackgroundConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;

/**
 * <p>Creates a RoundRect- or BorderedRoundRectBackground.</p>
 *
 * <p>Copyright Enough Software 2004 - 2008</p>

 * <pre>
 * history
 *        09-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class RoundRectBackgroundConverter extends BackgroundConverter {
	
	/**
	 * Instantiates a new creator
	 */
	public RoundRectBackgroundConverter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(Map background, Style style, StyleSheet styleSheet) throws BuildException {
		String arc = (String) background.get("arc");
		if (arc != null) {
			arc = Integer.toString( parseInt( "arc", arc ) );
		} else {
			arc = "8";
		}
		String arcHeight = (String) background.get("arc-height");
		if (arcHeight != null) {
			arcHeight = Integer.toString( parseInt( "arc-height", arcHeight ) );
		} else {
			arcHeight = arc;
		}
		String arcWidth = (String) background.get("arc-width");
		if (arcWidth != null) {
			arcWidth = Integer.toString( parseInt( "arc-width", arcWidth ) );
		} else {
			arcWidth = arc;
		}
		boolean hasAlphaColor = isAlphaColor(this.color);
		if (this.hasBorder && !hasAlphaColor) {
			return "new " + BACKGROUNDS_PACKAGE + "BorderedRoundRectBackground( " 
					+ this.color + "," + arcWidth + ", " + arcHeight + ", " 
					+ this.borderColor + ", " + this.borderWidth + ")";
		} else if (this.hasBorder && hasAlphaColor) {
			return "new " + BACKGROUNDS_PACKAGE + "TranslucentRoundRectBackground( " 
			+ this.color + "," + arcWidth + ", " + arcHeight + ", " 
			+ this.borderColor + ", " + this.borderWidth + ")";
		} else if (hasAlphaColor) {
			return "new " + BACKGROUNDS_PACKAGE + "TranslucentRoundRectBackground( " 
					+ this.color + "," + arcWidth + ", " + arcHeight + ")";
		} else {
			return "new " + BACKGROUNDS_PACKAGE + "RoundRectBackground( " 
					+ this.color + "," + arcWidth + ", " + arcHeight + ")";
		}
	}

}
