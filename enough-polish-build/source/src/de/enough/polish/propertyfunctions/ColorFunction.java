/*
 * Created on 19-Dec-2008 at 9:59:04.
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
package de.enough.polish.propertyfunctions;

import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.ColorConverter;

/**
 * <p>Transforms color values like "#fff" into hexadecimal Java values, e.g. "0xffffff".</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ColorFunction extends PropertyFunction {

	
	/**
	 * Creates a new function
	 */
	public ColorFunction() {
		super();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments,	Environment env) 
	{
		ColorConverter colorConverter = (ColorConverter) env.get( ColorConverter.ENVIRONMENT_KEY );
		if (colorConverter == null) {
			colorConverter = new ColorConverter();
		}
		return colorConverter.parseColor(input);
	}

}
