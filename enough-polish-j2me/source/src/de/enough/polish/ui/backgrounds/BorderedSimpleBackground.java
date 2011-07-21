//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:17:53.
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
package de.enough.polish.ui.backgrounds;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a filled rectangle with a border around it as a background.</p>
 * <p>This background-type can save some memory and processing time,
 *       when used instead of the SimpleBackground and a SimpleBorder together.
 * </p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class BorderedSimpleBackground extends Background {
	
	private int color;
	private Color colorObj; 
	private int borderColor;
	private Color borderColorObj;
	private boolean isInitialized;

	/**
	 * Creates a new simple background with a border.
	 * 
	 * @param color the color of the background
	 * @param borderColor the color of the border
	 * @param borderWidth the width of the border
	 */
	public BorderedSimpleBackground( int color, int borderColor, int borderWidth) {
		this.color = color;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		this.isInitialized = true;
	}

	/**
	 * Creates a new simple background with a border.
	 * 
	 * @param color the color of the background
	 * @param borderColor the color of the border
	 * @param borderWidth the width of the border
	 */
	public BorderedSimpleBackground( Color color, Color borderColor, int borderWidth) {
		this.colorObj = color;
		this.borderColorObj = borderColor;
		this.borderWidth = borderWidth;
		this.isInitialized = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (!this.isInitialized) {
			this.color = this.colorObj.getColor();
			this.borderColor = this.borderColorObj.getColor();
			this.isInitialized = true;
			this.colorObj = null;
			this.borderColorObj = null;
		}
		g.setColor( this.color );
		g.fillRect( x, y, width, height );
		width--;
		height--;
		g.setColor( this.borderColor );
		g.drawRect( x, y, width, height );
		int border = this.borderWidth - 1;
		while ( border > 0) {
			g.drawRect( x+border, y+border, width - (border<<1), height - (border<<1) );
			border--;
		}
	}

	//#if polish.css.animations
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.background-simple-bordered-color
			Color bgColor = style.getColorProperty("background-simple-bordered-color");
			if (bgColor != null) {
				this.color = bgColor.getColor();
			}
		//#endif
		//#if polish.css.background-simple-bordered-border-color
			Color brdColor = style.getColorProperty("background-simple-bordered-border-color");
			if (brdColor != null) {
				this.borderColor = brdColor.getColor();
			}
		//#endif
		//#if polish.css.background-simple-bordered-border-width
			Integer brdWidth = style.getIntProperty("background-simple-bordered-border-width");
			if (brdWidth != null) {
				this.borderWidth = brdWidth.intValue();
			}
		//#endif
	}
	//#endif
}
