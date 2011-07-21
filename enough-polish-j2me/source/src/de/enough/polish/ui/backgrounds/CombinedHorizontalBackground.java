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
 * <p>Places two further backgrounds side by side.</p>
 * <p>You can combine more by two backgrounds by using nested further combined/horizontal/vertical backgrounds.</p>

 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CombinedHorizontalBackground extends Background
{

	private static final int SIDE_RIGHT = 1;
	private final Background leftBackground;
	private final Background rightBackground;
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
	public CombinedHorizontalBackground( Background leftBackground, Background rightBackground, int splitPos, int splitSide, int margin )
	{
		this.leftBackground = leftBackground;
		this.rightBackground = rightBackground;
		this.splitPos = splitPos < 0 ? -splitPos : splitPos;
		this.isPercent = splitPos < 0;
		this.isSplitRight = splitSide == SIDE_RIGHT;
		this.margin = margin;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		int split = this.splitPos;
		if (this.isPercent) {
			split = (width * split) / 100;
		}
		if (split == 0) {
			split = Math.min(width, height);
		}
		if (this.isSplitRight) {
			split = width - split;
		}
		int m = this.margin >> 1;
		this.leftBackground.paint(x, y, split - m, height, g);
		this.rightBackground.paint(x + split + m, y, width - (split + m), height, g);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#animate(de.enough.polish.ui.Screen, de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Screen screen, Item parent, long currentTime,
			ClippingRegion repaintRegion)
	{
		this.leftBackground.animate(screen, parent, currentTime, repaintRegion);
		this.rightBackground.animate(screen, parent, currentTime, repaintRegion);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#showNotify()
	 */
	public void showNotify()
	{
		this.leftBackground.showNotify();
		this.rightBackground.showNotify();
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#hideNotify()
	 */
	public void hideNotify()
	{
		this.leftBackground.hideNotify();
		this.rightBackground.hideNotify();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#releaseResources()
	 */
	public void releaseResources()
	{
		this.leftBackground.releaseResources();
		this.rightBackground.releaseResources();
	}


	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		this.leftBackground.setStyle(style);
		this.rightBackground.setStyle(style);
	}
	//#endif

}
