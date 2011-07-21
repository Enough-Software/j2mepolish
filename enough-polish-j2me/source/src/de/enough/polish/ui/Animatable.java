/*
 * Created on Oct 1, 2008 at 4:10:39 PM.
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

/**
 * <p>An interface implemented by animatable elements.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface Animatable
{

	/**
	 * Animates this element.
	 * Subclasses can override this method to create animations.
	 * The default implementation animates the background and the item view if present.
	 * 
	 * @param currentTime the current time in milliseconds
	 * @param repaintRegion the repaint area that needs to be updated when this item is animated
	 * @see Item#addRelativeToContentRegion(ClippingRegion, int, int, int, int)
	 */
	void animate( long currentTime, ClippingRegion repaintRegion);
}
