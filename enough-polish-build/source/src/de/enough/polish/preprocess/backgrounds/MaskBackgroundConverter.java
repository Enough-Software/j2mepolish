//#condition polish.usePolishGui
/*
 * Created on Nov 21, 2007 at 12:12:00 PM.
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
package de.enough.polish.preprocess.backgrounds;

import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.BackgroundConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;
import de.enough.polish.preprocess.css.attributes.DimensionCssAttribute;

/**
 * <p>Converts a mask background</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Nov 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MaskBackgroundConverter extends BackgroundConverter
{

	/**
	 * 
	 */
	public MaskBackgroundConverter()
	{
		// no initialization needed
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.css.Style, de.enough.polish.preprocess.css.StyleSheet)
	 */
	protected String createNewStatement(Map background, Style style,
			StyleSheet styleSheet) throws BuildException
	{
		String maskReference = (String) background.get("mask");
		if (maskReference == null) {
			throw new BuildException( "Invalid CSS: a \"mask\" background is missing the \"mask\" CSS attribute which needs to refer to a background defined within the backgrounds section of your polish.css file.");
		}
		if (styleSheet.getBackgrounds().get(maskReference) == null) {
			throw new BuildException( "Invalid CSS: a \"mask\" background contains the invalid \"mask: " + maskReference +";\" CSS attribute. Please refer to a background defined within the backgrounds section of your polish.css file.");
		}
		maskReference += "Background";
		
		String maskColor = (String) background.get("mask-color");
		if (maskColor == null) {
			maskColor = "0x000000";
		} else {
			maskColor = parseColor(maskColor);
		}
		
		String backgroundReference  = (String) background.get("background");
		if (backgroundReference == null) {
			throw new BuildException( "Invalid CSS: a \"mask\" background is missing the \"background\" CSS attribute which needs to refer to a background defined within the backgrounds section of your polish.css file.");
		}
		if (styleSheet.getBackgrounds().get(backgroundReference) == null) {
			throw new BuildException( "Invalid CSS: a \"mask\" background contains the invalid \"background: " + backgroundReference +";\" CSS attribute. Please refer to a background defined within the backgrounds section of your polish.css file.");
		}
		backgroundReference += "Background";
		
		String opacity  = (String) background.get("opacity");
		if (opacity == null) {
			opacity = "255";
		} else {
			DimensionCssAttribute attribute = new DimensionCssAttribute();
			opacity = attribute.getValue(opacity, Environment.getInstance() ); //Integer.toString( parseInt("opacity", opacity) );
		}
		String result = "new " + BACKGROUNDS_PACKAGE + "MaskBackground( StyleSheet." 
		+ maskReference + ", " + maskColor + ", StyleSheet." + backgroundReference  + ", " + opacity + ")";
	
		return result;

	}

}
