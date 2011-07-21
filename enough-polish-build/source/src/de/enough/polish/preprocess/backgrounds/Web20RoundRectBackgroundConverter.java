/*
 * Created on 09-ec-2006 at 16:44:27.
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
 * <p>Creates a Web20RoundRectBackground.</p>
 *
 * <p>Copyright Enough Software 2006</p>

 * <pre>
 * history
 *        09-Dec-2006 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Web20RoundRectBackgroundConverter extends BackgroundConverter {
	
	/**
	 * Instantiates a new creator
	 */
	public Web20RoundRectBackgroundConverter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(Map background, Style style, StyleSheet styleSheet) throws BuildException {
		String arc = (String) background.get("arc");
		if (arc != null) {
			parseInt( "arc", arc );
		} else {
			arc = "10";
		}
		String arcHeight = (String) background.get("arc-height");
		if (arcHeight != null) {
			parseInt( "arc-height", arc );
		} else {
			arcHeight = arc;
		}
		String arcWidth = (String) background.get("arc-width");
		if (arcWidth != null) {
			parseInt( "arc-width", arc );
		} else {
			arcWidth = arc;
		}
		String circleColorStr = (String) background.get("circle-color");
		if ( circleColorStr != null ) {
			circleColorStr = parseColor( circleColorStr );
		} else {
			circleColorStr = "0x999999";
		}
		String circleAnchorStr = (String) background.get("circle-anchor");
		if (circleAnchorStr != null) {
			circleAnchorStr = parseAnchor("circle-anchor", circleAnchorStr );
		} else {
			circleAnchorStr = "Graphics.TOP";
		}
		String paddingStr = (String) background.get("padding");
		if (paddingStr != null) {
			parseInt( "padding", paddingStr );
		} else {
			paddingStr = "1";
		}
		String paddingLeftStr  = (String) background.get("padding-left");
		if (paddingLeftStr != null) {
			parseInt( "padding-left", paddingLeftStr );
		} else {
			paddingLeftStr = paddingStr;
		}
		String paddingRightStr  = (String) background.get("padding-right");
		if (paddingRightStr != null) {
			parseInt( "padding-right", paddingRightStr );
		} else {
			paddingRightStr = paddingStr;
		}
		String paddingTopStr  = (String) background.get("padding-top");
		if (paddingTopStr != null) {
			parseInt( "padding-top", paddingTopStr );
		} else {
			paddingTopStr = paddingStr;
		}
		String paddingBottomStr  = (String) background.get("padding-bottom");
		if (paddingBottomStr != null) {
			parseInt( "padding-bottom", paddingBottomStr );
		} else {
			paddingBottomStr = paddingStr;
		}
		return "new " + BACKGROUNDS_PACKAGE + "Web20RoundRectBackground( " 
				+ this.color + "," + arcWidth + ", " + arcHeight + ", " 
				+ circleColorStr + ", " + circleAnchorStr + ", " + paddingLeftStr + ", " + paddingRightStr + ", " + paddingTopStr + ", " + paddingBottomStr +  ")";
	}

}
