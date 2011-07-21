//#condition polish.usePolishGui
/*
 * Created on 24-Jan-2005 at 01:37:32.
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
 * <p>Paints a background in which the top uses rounded edges, but the bottom uses straight ones. CSS-type is "round-tab"</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        24-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RoundTabBackground extends Background {

	private int color;
	private int arcWidth;
	private int arcHeight;

	/**
	 * Creates a new round tab background.
	 * 
	 * @param color the color of the background
	 * @param arcWidth the horizontal diameter of the arc at the top corners
	 * @param arcHeight the vertical diameter of the arc at the top corners
	 */
	public RoundTabBackground( int color, int arcWidth, int arcHeight) {
		super();
		this.color = color;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		g.fillRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
		y += height / 2;
		if (((height & 1) == 1)) {
			height = height / 2 + 1;
		} else {
			height /=  2;
		}
		g.fillRect( x, y, width, height );
	}
	

	//#if polish.css.animations
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
		 */
		public void setStyle(Style style)
		{
			//#if polish.css.background-round-tab-color
				Color col = style.getColorProperty("background-round-tab-color");
				if (col != null) {
					this.color = col.getColor();
				}
			//#endif
			//#if polish.css.background-round-tab-arc
				Integer arcInt = style.getIntProperty("background-round-tab-arc");
				if (arcInt != null) {
					this.arcWidth = arcInt.intValue();
					this.arcHeight = arcInt.intValue();
				}
			//#endif
			//#if polish.css.background-round-tab-arc-width
				Integer arcWidthInt = style.getIntProperty("background-round-tab-arc-width");
				if (arcWidthInt != null) {
					this.arcWidth = arcWidthInt.intValue();
				}
			//#endif
			//#if polish.css.background-round-tab-arc-height
				Integer arcHeightInt = style.getIntProperty("background-round-tab-arc-height");
				if (arcHeightInt != null) {
					this.arcHeight = arcHeightInt.intValue();
				}
			//#endif
		}
	//#endif

}
