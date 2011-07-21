//#condition polish.usePolishGui
/*
 * Created on 06-Jan-2004 at 22:36:37.
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
package de.enough.polish.ui.borders;

import de.enough.polish.ui.Border;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.util.DrawUtil;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a plain border in one color.</p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>
 * @author Robert Virkus, robert@enough.de
 */
public class SimpleBorder extends Border {

	protected int color;
	protected final boolean isArgb;

	/**
	 * Creates a new simple border.
	 * 
	 * @param color the color of this border in RGB, e.g. 0xFFDD12
	 * @param borderWidth the width of this border
	 */
	public SimpleBorder( int color, int borderWidth ) {
		super( borderWidth, borderWidth, borderWidth, borderWidth );
		this.color = color;
		this.isArgb = ((color & 0xff000000) != 0) && ((color & 0xff000000) != 0xff);
	}
	
	/**
	 * Creates a new simple border.
	 * 
	 * @param color the color of this border in RGB, e.g. 0xFFDD12
	 * @param leftWidth the width of this border on the left side
	 * @param rightWidth the width of this border on the right side
	 * @param topWidth the width of this border at the top
	 * @param bottomWidth the width of this border at the bottom
	 */
	public SimpleBorder( int color, int leftWidth, int rightWidth, int topWidth, int bottomWidth  ) {
		super(leftWidth, rightWidth, topWidth, bottomWidth);
		this.color = color;
		this.isArgb = ((color & 0xff000000) != 0) && ((color & 0xff000000) != 0xff);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		width--;
		height--;
		int border = this.borderWidthLeft - 1;
		int col = this.color;
		if (this.isArgb) {
			DrawUtil.drawRect( col, x, y, width, height, g);
			while ( border > 0) {
				DrawUtil.drawRect( col, x+border, y+border, width - (border<<1), height - (border<<1), g);
				border--;
			}
		} else {
			g.setColor( col );
			g.drawRect( x, y, width, height);
			while ( border > 0) {
				g.drawRect( x+border, y+border, width - (border<<1), height - (border<<1));
				border--;
			}
		}
	}
	

	//#if polish.css.animations
	/**
	 * Allows borders to be animated using CSS attribute animations.
	 * @param style the style containing typically only one element
	 */
	public void setStyle(Style style)
	{
		//#if polish.css.border-simple-color
			Color col = style.getColorProperty("border-simple-color");
			if (col != null) {
				this.color = col.getColor();
			}
		//#endif
		//#if polish.css.border-simple-width
			Integer widthInt = style.getIntProperty("border-simple-width");
			if (widthInt != null) {
				int w = widthInt.intValue();
				this.borderWidthBottom = w;
				this.borderWidthTop = w;
				this.borderWidthLeft = w;
				this.borderWidthRight = w;
			}
		//#endif
		//#if polish.css.border-simple-width-left
			Integer widthLeftInt = style.getIntProperty("border-simple-width-left");
			if (widthLeftInt != null) {
				this.borderWidthLeft = widthLeftInt.intValue();
			}
		//#endif
		//#if polish.css.border-simple-width-right
			Integer widthRightInt = style.getIntProperty("border-simple-width-right");
			if (widthRightInt != null) {
				this.borderWidthRight = widthRightInt.intValue();
			}
		//#endif
		//#if polish.css.border-simple-width-top
			Integer widthTopInt = style.getIntProperty("border-simple-width-top");
			if (widthTopInt != null) {
				this.borderWidthTop = widthTopInt.intValue();
			}
		//#endif
		//#if polish.css.border-simple-width-bottom
			Integer widthBottomInt = style.getIntProperty("border-simple-width-bottom");
			if (widthBottomInt != null) {
				this.borderWidthBottom = widthBottomInt.intValue();
			}
		//#endif
	}
	//#endif

}
