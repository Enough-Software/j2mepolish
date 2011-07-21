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
import de.enough.polish.preprocess.css.BackgroundConverter;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.preprocess.css.StyleSheet;

/**
 * <p>Converts a combined background</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Nov 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CombinedBackgroundConverter extends BackgroundConverter
{
	

	private final String attribute1;
	private final String attribute2;
	private final String name;
	private final String className;


	/**
	 * Creates a new combined bg converter 
	 */
	public CombinedBackgroundConverter()
	{
		this( "CombinedBackground", "combined", "foreground", "background" );
	}
	
	/**
	 * Creates a new combined bg converter 
	 * @param className the name of the background class
	 * @param name the name of the bg, e.g. combined
	 * @param attribute1 the name of the first background attribute, e.g. foreground
	 * @param attribute2 the name of the first background attribute, e.g. background
	 */
	public CombinedBackgroundConverter( String className, String name, String attribute1, String attribute2 )
	{
		this.className = className;
		this.name = name;
		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.BackgroundConverter#createNewStatement(java.util.HashMap, de.enough.polish.preprocess.css.Style, de.enough.polish.preprocess.css.StyleSheet)
	 */
	protected String createNewStatement(Map background, Style style,
			StyleSheet styleSheet) throws BuildException
	{
		String foregroundReference = (String) background.get(this.attribute1);
		if (foregroundReference == null) {
			throw new BuildException( "Invalid CSS: a \"" + this.name + "\" background is missing the \"" + this.attribute1 + "\" CSS attribute which needs to refer to a background defined within the backgrounds section of your polish.css file.");
		}
		if (styleSheet.getBackgrounds().get(foregroundReference) == null) {
			throw new BuildException( "Invalid CSS: a \"" + this.name + "\" background contains the invalid \"" + this.attribute1 +": " + foregroundReference +";\" CSS attribute. Please refer to a background defined within the backgrounds section of your polish.css file.");
		}
		foregroundReference += "Background";
		String backgroundReference  = (String) background.get(this.attribute2);
		if (backgroundReference == null) {
			throw new BuildException( "Invalid CSS: a \"" + this.name + "\" background is missing the \"" + this.attribute2 + "\" CSS attribute which needs to refer to a background defined within the backgrounds section of your polish.css file.");
		}
		if (styleSheet.getBackgrounds().get(backgroundReference) == null) {
			throw new BuildException( "Invalid CSS: a \"" + this.name + "\" background contains the invalid \"" + this.attribute2 + ": " + backgroundReference +";\" CSS attribute. Please refer to a background defined within the backgrounds section of your polish.css file.");
		}
		backgroundReference += "Background";
		String result = "new " + BACKGROUNDS_PACKAGE + this.className + "(" 
		+ "StyleSheet." + foregroundReference + ", StyleSheet." + backgroundReference + ")";
	
		return result;

	}

}
