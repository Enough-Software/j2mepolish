//#condition polish.usePolishGui
/*
 * Created on 04-Jan-2004 at 18:48:09.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
 * <p>Background is the base class for any backgrounds of widgets or forms.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <p>copyright Enough Software 2004 - 2009</p>
 */
public abstract class Background implements Serializable
{
	
	/**
	 * Defines the width of this Background.
	 * Usually this is 0, but some backgrounds might have a border included.
	 */
	public int borderWidth;

        /**
         * The Item which uses the background.
         */
        public transient Item parent ;

        /**
         * Set the parent item.
         * @param parent
         */
        public void setParentItem(Item parent)
        {
            this.parent = parent;
        }

	/**
	 * Creates a new Background.
	 * The width of this background is set to 0 here.
	 */
	public Background() {
		this.borderWidth = 0;
	}
	
	
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
			addRelativeToBackgroundRegion(repaintRegion, screen, parent, 0, 0, 0, 0 );
		}
	}

	/**
	 * Adds an repaint area relative to this background
	 * @param repaintRegion the clipping rectangle
	 * @param screen the screen of this background
	 * @param parent the item of this background
	 * @param left left adjustment of the repaint region, use negative values to expand area
	 * @param right right adjustment of the repaint region, use positive values to expand area
	 * @param top top adjustment of the repaint region, use negative values to expand area
	 * @param bottom bottom adjustment of the repaint region, use positive values to expand area
	 */
	protected void addRelativeToBackgroundRegion(ClippingRegion repaintRegion, Screen screen, Item parent, int left, int right, int top, int bottom ) {
		if (parent != null) {
			parent.addRelativeToBackgroundRegion(
					//#if polish.css.complete-background
						this, null, // provide references to this background so that the correct background dimensions are selected 
					//#endif
					repaintRegion, left, top, parent.backgroundWidth - left + right + 1, parent.backgroundHeight - top + bottom + 1 
			);
		} else {
			repaintRegion.addRegion(0, 0, screen.getWidth(), screen.getScreenHeight() );
		}		
	}
	
	/**
	 * Animates this background.
	 * Subclasses can override this method to create animations.
	 * 
	 * @return true when this background has been animated.
	 * @see #animate(Screen, Item, long, ClippingRegion)
	 */
	public boolean animate() {
		return false;
	}
	
	/**
	 * Paints this background.
	 * 
	 * @param x the horizontal start point
	 * @param y the vertical start point
	 * @param width the width of the background
	 * @param height the height of the background
	 * @param g the Graphics on which the background should be painted.
	 */
	public abstract void paint( int x, int y, int width, int height, Graphics g );
	
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this background.
	 * The default implementation does not do anything.
	 */
	public void releaseResources() {
		// do nothing
	}
	
	/**
	 * Informs the background that it is being hidden shortly.
	 * The default implementation is empty.
	 */
	public void hideNotify() {
		// do nothing
	}
	
	/**
	 * Informs the background that it is being shown shortly or that it is now applied to a new visible item.
	 * The default implementation is empty.
	 */
	public void showNotify() {
		// do nothing
	}
	


	/**
	 * Allows backgrounds to be animated using CSS attribute animations.
	 * @param style the style containing typically only one element
	 */
	public void setStyle(Style style)
	{
		// do nothing
		
	}

}
