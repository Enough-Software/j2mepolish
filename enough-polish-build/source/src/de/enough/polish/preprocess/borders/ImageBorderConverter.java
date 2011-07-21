/*
 * Created on 10-May-2007 at 15:55:12.
 *
 * Copyright (c) 2007 Michael Koch / Enough Software
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

/*
 * <p>Creates am image border.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        9-May-2005 - mkoch creation
 * </pre>
 * @author Michael Koch, michael.koch@enough.de
 */
public class ImageBorderConverter
	extends BorderConverter
{
	/**
	 * Creates a new custom border creator
	 */
	public ImageBorderConverter() {
		// Make default constructor public.
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.BorderConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.Style, de.enough.polish.preprocess.StyleSheet)
	 */
	protected String createNewStatement(Map border, Style style, StyleSheet styleSheet)
		throws BuildException
	{
		String borderWidthStr = (String) border.get("border-width");

		if (borderWidthStr == null) {
			throw new BuildException("no width attribute found");
		}

		String image = (String) border.get("image");

		if (image == null) {
			throw new BuildException("no image attribute found");
		}

		int borderWidth = parseInt("border-width", borderWidthStr);
		image = getUrl(image);

		return "new " + BORDERS_PACKAGE + "ImageBorder(" + borderWidth +
				", \"" + image + "\")";
	}
}
