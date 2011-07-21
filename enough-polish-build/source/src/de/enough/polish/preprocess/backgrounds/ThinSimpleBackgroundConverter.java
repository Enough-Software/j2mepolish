/*
 * Created on Jul 29, 2006 at 6:48:11 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
 * <p>Converts a simple background with a maximum width</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jul 29, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ThinSimpleBackgroundConverter extends BackgroundConverter {

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.css.Style, de.enough.polish.preprocess.css.StyleSheet)
	 */
	protected String createNewStatement(Map background, Style style,
			StyleSheet styleSheet) 
	throws BuildException 
	{
		int maxWidth = 1;
		boolean isPercent = false;
		String maxWidthStr = (String) background.get("max-width");
		if (maxWidthStr != null) {
			isPercent = maxWidthStr.charAt( maxWidthStr.length() - 1 ) == '%';
			if (isPercent) {
				maxWidth = parseInt( "max-width", maxWidthStr.substring(0, maxWidthStr.length() - 1) );
			} else {
				maxWidth = parseInt( "max-width", maxWidthStr );
			}
		}
		return "new de.enough.polish.ui.backgrounds.ThinSimpleBackground( " + this.color + ", " + maxWidth + ", " + isPercent + ", " 
			   + this.borderWidth + ", " + this.borderColor + ")";
	}

}
