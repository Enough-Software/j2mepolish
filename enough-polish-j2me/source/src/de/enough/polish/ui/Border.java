//#condition polish.usePolishGui
/*
 * Created on 04-Jan-2004 at 19:37:35.
 *
 * Copyright (c) 2004-2009 Robert Virkus / Enough Software
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

import javax.microedition.lcdui.Graphics;

import de.enough.polish.io.Serializable;

/**
 * <p>Provides an abstract  border.</p>
 *
 * <p>copyright Enough Software 2004 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class Border implements Serializable 
{
	
	/** width of the border at the top */
	protected int borderWidthTop;
	/** width of the border at the bottom */
	protected int borderWidthBottom;
	/** width of the border at the left side */
	protected int borderWidthLeft;
	/** width of the border at the right side */
	protected int borderWidthRight;

	/**
	 * Creates a new border with the specified border widths.
	 * 
	 * @param left the border width on the left side
	 * @param right the border width on the right side 
	 * @param top the border width at the top
	 * @param bottom the border width at the bottom
	 */
	public Border(int left, int right, int top, int bottom) {
		this.borderWidthLeft = left;
		this.borderWidthRight = right;
		this.borderWidthTop = top;
		this.borderWidthBottom = bottom;
	}
	
	/**
	 * Paints this border.
	 * 
	 * @param x  the horizontal start point
	 * @param y  the vertical start point
	 * @param width  the width of the border
	 * @param height  the height of the border
	 * @param g  the Graphics on which the border should be painted.
	 */
	public abstract void paint( int x, int y, int width, int height, Graphics g );
	
	/**
	 * Animates this background.
	 * Subclasses can override this method to create animations.
	 * The default implementation calls the animate() method and adds the full content area to the repaint region.
	 * 
	 * @param screen the parent screen
	 * @param parent the parent item, can be null when the background belongs to a screen
	 * @param currentTime the current time in milliseconds
	 * @param repaintRegion the repaint area that needs to be updated when this item is animated
	 * @see Item#addRelativeToContentRegion(ClippingRegion, int, int, int, int)
	 */
	public void animate(Screen screen, Item parent, long currentTime, ClippingRegion repaintRegion) 
	{
		if (animate()) {
			if (parent != null) {
				parent.addRelativeToBackgroundRegion( 
						//#if polish.css.complete-border
							null, this, // provide references to this border so that the correct background dimensions are selected
						//#endif
						repaintRegion, 0, 0, parent.backgroundWidth, parent.backgroundHeight 
				);
			} else {
				repaintRegion.addRegion(0, 0, screen.getWidth(), screen.getScreenHeight() );
			}
		}
	}
	
	/**
	 * Animates this border.
	 * Subclasses can override this method to create animations.
	 * 
	 * @return true when this border has been animated and needs a repaint.
	 * @see #animate(Screen, Item, long, ClippingRegion)
	 */
	public boolean animate() {
		return false;
	}
	
	/**
	 * Informs the border that it is being hidden shortly.
	 * The default implementation is empty.
	 */
	public void hideNotify() {
		// do nothing
	}
	
	/**
	 * Informs the border that it is being shown shortly or that it is now applied to a new visible item.
	 * The default implementation is empty.
	 */
	public void showNotify() {
		// do nothing
	}



	/**
	 * Allows borders to be animated using CSS attribute animations.
	 * @param style the style containing typically only one element
	 */
	public void setStyle(Style style)
	{
		// do nothing
	}

	/**
	 * Retrieves the border width.
	 * 
	 * @return the border for the top side in pixels
	 */
	public int getBorderWidthTop() {
		return this.borderWidthTop;
	}

	/**
	 * Retrieves the border width.
	 * 
	 * @return the border for the bottom side in pixels
	 */
	public int getBorderWidthBottom() {
		return this.borderWidthBottom;
	}

	/**
	 * Retrieves the border width.
	 * 
	 * @return the border for the left side in pixels
	 */
	public int getBorderWidthLeft() {
		return this.borderWidthLeft;
	}

	/**
	 * Retrieves the border width.
	 * 
	 * @return the border for the right side in pixels
	 */
	public int getBorderWidthRight() {
		return this.borderWidthRight;
	}
	
	
	
}
