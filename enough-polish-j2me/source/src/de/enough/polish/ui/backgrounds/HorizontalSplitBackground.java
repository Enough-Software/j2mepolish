//#condition polish.usePolishGui
/*
 * Created on Apr 23, 2008 at 11:52:42 PM.
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
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;

/**
 * <p>Creates a rectangular background with two colors.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HorizontalSplitBackground extends Background
{
	private final static int SIDE_RIGHT = 1;
	private int leftColor;
	private int rightColor;
	private int splitPos;
	private final boolean isPercent;
	private final boolean isSplitRight;

	/**
	 * Creates a new background
	 * @param leftColor the top color
	 * @param rightColor the bottom color
	 * @param splitPos the split position either in percent (0 - 100) or in pixels, negative values are interpreted as percent values
	 * @param splitSide the side of the splitPos
	 */
	public HorizontalSplitBackground( int leftColor, int rightColor, int splitPos, int splitSide ) 
	{
		this.leftColor = leftColor;
		this.rightColor = rightColor;
		this.splitPos = splitPos < 0 ? -splitPos : splitPos;
		this.isPercent = splitPos < 0;
		this.isSplitRight = splitSide == SIDE_RIGHT;
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
		g.setColor( this.leftColor );
		g.fillRect(x, y, split + 1, height);
		g.setColor( this.rightColor );
		g.fillRect( x + split, y , width - split, height );
	}

	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-horizontal-split-left-color
			Color lcol = style.getColorProperty("background-horizontal-split-left-color");
			if (lcol != null) {
				this.leftColor = lcol.getColor();
			}
		//#endif
			//#if polish.css.background-horizontal-split-right-color
			Color rcol = style.getColorProperty("background-horizontal-split-right-color");
			if (rcol != null) {
				this.rightColor = rcol.getColor();
			}
		//#endif
			//#if polish.css.background-horizontal-split-split-pos
			Integer splitPosInt = style.getIntProperty("background-horizontal-split-split-pos");
			if (splitPosInt != null) {
				this.splitPos = splitPosInt.intValue();
			}
		//#endif
	}
	//#endif
	
	

}
