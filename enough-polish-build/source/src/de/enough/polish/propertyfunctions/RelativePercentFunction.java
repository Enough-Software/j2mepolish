/*
 * Created on Jan 13, 2009 at 2:48:52 PM.
 * 
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

/**
 * <p>Generates a percent value out of given values, e.g. ${percent( ${imagewidth(turtle.png)}, ${polish.ScreenWidth} )} gives the width of title.png relative to the width of the screen in percent, e.g. "40%"</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RelativePercentFunction extends PropertyFunction
{

	/**
	 * Creates a new function
	 */
	public RelativePercentFunction()
	{
		// creates a new function
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments, Environment env)
	{
		if (arguments == null || arguments.length != 1) {
			throw new IllegalArgumentException("The percent function needs one argument, e.g. ${percent( ${imagewidth(turtle.png)}, ${polish.ScreenWidth} )}");
		}
		
		int value = Integer.parseInt( input );
		int absolute = Integer.parseInt( arguments[0] );
		return Integer.toString((value * 100) / absolute) + "%";
	}

}
