//#condition polish.usePolishGui
/*
 * Created on Jun 14, 2006 at 6:07:17 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.ui;

import de.enough.polish.util.RgbImage;



/**
 * <p>Applies an effect to an RGB array.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Jun 14, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class RgbEffect {
	
	/**
	 * Renders the effect on the given RGB array.
	 * It is explicitely allowed that the effect is returning the given RGB array instead of creating a new one.
	 * 
	 * @param image RgbImage containing an integer array containing the (A)RGB data, on which the effect should be applied.
	 */
	public abstract void renderEffect( RgbImage image );
	
	/**
	 * Allows the subclasses to acquire necessary settings by querying the CSS style.
	 * When overriding, methods should call super.setStyle(style) first.
	 *  
	 * @param style the style
	 */
	public void setStyle( Style style ) {
		// let subclasses implement this when required
	}

}
