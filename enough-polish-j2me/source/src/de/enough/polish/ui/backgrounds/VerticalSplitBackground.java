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
public class VerticalSplitBackground extends Background
{

	private int topColor;
	private int bottomColor;
	private int splitPos;
	private final boolean isPercent;

	/**
	 * Creates a new background
	 * @param topColor the top color
	 * @param bottomColor the bottom color
	 * @param splitPos the split position either in percent (0 - 100) or in pixels, negative values are interpreted as percent values
	 */
	public VerticalSplitBackground( int topColor, int bottomColor, int splitPos ) 
	{
		this.topColor = topColor;
		this.bottomColor = bottomColor;
		this.splitPos = splitPos < 0 ? -splitPos : splitPos;
		this.isPercent = splitPos < 0;
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
		g.setColor( this.topColor );
		g.fillRect(x, y, width, split + 1);
		g.setColor( this.bottomColor );
		g.fillRect( x, y + split, width, height - split );
	}
	
	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-vertical-split-top-color
			Color lcol = style.getColorProperty("background-vertical-split-top-color");
			if (lcol != null) {
				this.topColor = lcol.getColor();
			}
		//#endif
			//#if polish.css.background-vertical-split-bottom-color
			Color rcol = style.getColorProperty("background-vertical-split-bottom-color");
			if (rcol != null) {
				this.bottomColor = rcol.getColor();
			}
		//#endif
			//#if polish.css.background-vertical-split-split-pos
			Integer splitPosInt = style.getIntProperty("background-vertical-split-split-pos");
			if (splitPosInt != null) {
				this.splitPos = splitPosInt.intValue();
			}
		//#endif
	}
	//#endif

}
