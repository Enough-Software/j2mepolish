//#condition polish.usePolishGui
/*
 * Created on Jun 22, 2008 at 3:38:59 PM.
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
 * <p>A common interface for items and screens.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface UiElement
{
	/**
	 * Sets the style of this user interface element.
	 * Typically control is forwarded to setStyle( Style, true ).
	 * 
	 * @param style the new style for this element.
	 * @throws NullPointerException when style is null
	 */
	void setStyle( Style style );
	
	/**
	 * Sets the style with animatable CSS attributes of this user interface element.
	 * 
	 * @param style the new style for this element.
	 * @param resetStyle true when style settings should be resetted. This is not the case
	 * 			when styles are animated, for example.
	 * @throws NullPointerException when style is null
	 */
	void setStyle( Style style, boolean resetStyle );
	
	
	/**
	 * Retrieves the currently used style
	 * @return the style of this UI element
	 */
	Style getStyle();
	
	/**
	 * Adds a repaint request for this user interface component's space.
	 * @param repaintArea the clipping rectangle to which the repaint area should be added
	 */
	void addRepaintArea( ClippingRegion repaintArea );
	
	/**
	 * Adds a region relative to this item's content x/y start position.
	 * @param repaintRegion the clipping region
	 * @param x horizontal start relative to this item's content position
	 * @param y vertical start relative to this item's content position
	 * @param width width
	 * @param height height
	 */
	void addRelativeToContentRegion(ClippingRegion repaintRegion, int x, int y, int width, int height);
	

}
