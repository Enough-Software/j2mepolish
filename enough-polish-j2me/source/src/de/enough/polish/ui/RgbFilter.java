//#condition polish.usePolishGui
/*
 * Created on Jul 8, 2008 at 4:58:50 PM.
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

import de.enough.polish.io.Serializable;
import de.enough.polish.util.RgbImage;

/**
 * <p>Provides an RGB filter that transforms RGB data in a specific way.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class RgbFilter implements Serializable
{
	/**
	 * Creates a new filter
	 */
	public RgbFilter() {
		// no standard initialization
	}
	
	/**
	 * Processes the given RGB input
	 * @param input the RgbImage input
	 * @return the RgbImage output
	 */
	public abstract RgbImage process( RgbImage input );

	/**
	 * Determines whether this filter is active.
	 * An opacity filter is for example not active, when the opacity value is 255
	 * @return true when this RGB filter is active.
	 */
	public abstract boolean isActive();
	
	/**
	 * Configures this filter
	 * @param style the style
	 * @param resetStyle true when default values should be assumed, may be ignored by subclasses
	 */
	public void setStyle( Style style, boolean resetStyle ) {
		// configures this filter in a subclass
	}
	
	/**
	 * Releases all memory intensive resources
	 */
	public void releaseResources() {
		// let subclasses handle this
	}

}
