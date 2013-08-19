//#condition polish.usePolishGui
/*
 * Created on Nov 21, 2007 at 12:23:44 PM.
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
package de.enough.polish.ui.borders;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Border;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;

/**
 * <p>Provides a border consisting of two other borders.</p>
 * <p>You can combine more by two borders by using nested further combined borders.</p>
 *
 * <p>Copyright Enough Software 2012</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CombinedBorder extends Border
{

	private final Border foreground;
	private final Border background;

	/**
	 * Creates a new combined border.
	 * 
	 * @param foreground the border painted last
	 * @param background  the border painted in the border
	 * 
	 */
	public CombinedBorder( Border foreground, Border background )
	{
		super(  Math.max(foreground.getBorderWidthLeft(), background.getBorderWidthLeft()),
				Math.max(foreground.getBorderWidthRight(), background.getBorderWidthRight()),
				Math.max(foreground.getBorderWidthTop(), background.getBorderWidthTop()),
				Math.max(foreground.getBorderWidthBottom(), background.getBorderWidthBottom())
				);
		this.foreground = foreground;
		this.background = background;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		this.background.paint(x, y, width, height, g);
		this.foreground.paint(x, y, width, height, g);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#animate(de.enough.polish.ui.Screen, de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Screen screen, Item parent, long currentTime,
			ClippingRegion repaintRegion)
	{
		this.foreground.animate(screen, parent, currentTime, repaintRegion);
		this.background.animate(screen, parent, currentTime, repaintRegion);
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#showNotify()
	 */
	public void showNotify()
	{
		this.foreground.showNotify();
		this.background.showNotify();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#hideNotify()
	 */
	public void hideNotify()
	{
		this.foreground.hideNotify();
		this.background.hideNotify();
	}


	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		this.background.setStyle(style);
		this.foreground.setStyle(style);
	}
	//#endif
	
}
