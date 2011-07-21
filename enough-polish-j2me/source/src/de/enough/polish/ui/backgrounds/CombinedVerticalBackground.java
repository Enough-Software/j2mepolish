//#condition polish.usePolishGui
/*
 * Created on May 10, 2008 at 12:23:44 PM.
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
package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;

/**
 * <p>Places two backgrounds side by side in a vertical direction (top-bottom).</p>
 * <p>You can combine more by two backgrounds by using nested further combined/horizontal/vertical backgrounds.</p>

 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CombinedVerticalBackground extends Background
{

	private static final int SIDE_BOTTOM = 1;
	private final Background topBackground;
	private final Background bottomBackground;
	private final int splitPos;
	private final boolean isPercent;
	private final boolean isSplitRight;
	private final int margin;

	/**
	 * Creates a new horizontal background.
	 * 
	 * @param leftBackground the background painted left
	 * @param rightBackground  the background painted right
	 * @param splitPos the split position either in percent (0 - 100) or in pixels, negative values are interpreted as percent values
	 * @param splitSide the side of the splitPos
	 * @param margin the margin between the backgrounds - can be negative for overlapping 
	 * 
	 */
	public CombinedVerticalBackground( Background leftBackground, Background rightBackground, int splitPos, int splitSide, int margin )
	{
		this.topBackground = leftBackground;
		this.bottomBackground = rightBackground;
		this.splitPos = splitPos < 0 ? -splitPos : splitPos;
		this.isPercent = splitPos < 0;
		this.isSplitRight = splitSide == SIDE_BOTTOM;
		this.margin = margin;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		int split = this.splitPos;
		if (this.isPercent) {
			split = (height * split) / 100;
		}
		if (split == 0) {
			split = Math.min(width, height);
		}
		if (this.isSplitRight) {
			split = height - split;
		}
		int m = this.margin >> 1;
		this.topBackground.paint(x, y, width, split - m, g);
		this.bottomBackground.paint(x, y + split + m, width, height - (split + m), g);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#animate(de.enough.polish.ui.Screen, de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Screen screen, Item parent, long currentTime,
			ClippingRegion repaintRegion)
	{
		this.topBackground.animate(screen, parent, currentTime, repaintRegion);
		this.bottomBackground.animate(screen, parent, currentTime, repaintRegion);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#showNotify()
	 */
	public void showNotify()
	{
		this.topBackground.showNotify();
		this.bottomBackground.showNotify();
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#hideNotify()
	 */
	public void hideNotify()
	{
		this.topBackground.hideNotify();
		this.bottomBackground.hideNotify();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#releaseResources()
	 */
	public void releaseResources()
	{
		this.topBackground.releaseResources();
		this.bottomBackground.releaseResources();
	}


	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		this.topBackground.setStyle(style);
		this.bottomBackground.setStyle(style);
	}
	//#endif

}
