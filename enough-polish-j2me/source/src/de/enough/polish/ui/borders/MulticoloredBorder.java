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

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a plain border in multiple colors.</p>
 *
 * <p>Copyright Enough Software 2012</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MulticoloredBorder extends Border {

	protected int colorLeft;
	protected int colorRight;
	protected int colorTop;
	protected int colorBottom;

	
	/**
	 * Creates a new multicolored border.
	 * 
	 * @param colorLeft the color of this border in RGB, e.g. 0xFFDD12
	 * @param colorRight the color of this border in RGB, e.g. 0xFFDD12
	 * @param colorTop the color of this border in RGB, e.g. 0xFFDD12
	 * @param colorBottom the color of this border in RGB, e.g. 0xFFDD12
	 * @param leftWidth the width of this border on the left side
	 * @param rightWidth the width of this border on the right side
	 * @param topWidth the width of this border at the top
	 * @param bottomWidth the width of this border at the bottom
	 */
	public MulticoloredBorder( int colorLeft, int colorRight, int colorTop, int colorBottom, int leftWidth, int rightWidth, int topWidth, int bottomWidth  ) {
		super(leftWidth, rightWidth, topWidth, bottomWidth);
		this.colorLeft = colorLeft;
		this.colorRight = colorRight;
		this.colorTop = colorTop;
		this.colorBottom = colorBottom;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		int rightX = x + width - 1;
		int bottomY = y + height - 1;
		// paint top border:
		g.setColor( this.colorTop );
		int border = this.borderWidthTop - 1;
		while ( border >= 0) {
			g.drawLine( x, y + border, rightX, y + border );
			border--;
		}
		// paint bottom border:
		g.setColor( this.colorBottom );
		border = this.borderWidthBottom - 1;
		while ( border >= 0) {
			g.drawLine( x, bottomY - border, rightX, bottomY - border );
			border--;
		}
		// paint left border:
		g.setColor( this.colorLeft );
		border = this.borderWidthLeft - 1;
		while ( border >= 0) {
			g.drawLine( x + border, y, x + border, bottomY );
			border--;
		}
		// paint right border:
		g.setColor( this.colorRight );
		border = this.borderWidthRight - 1;
		while ( border >= 0) {
			g.drawLine( rightX - border, y, rightX - border, bottomY );
			border--;
		}

	}
	

	//#if polish.css.animations
	/**
	 * Allows borders to be animated using CSS attribute animations.
	 * @param style the style containing typically only one element
	 */
	public void setStyle(Style style)
	{
		Color col;
		//#if polish.css.border-multicolored-color-left
			col = style.getColorProperty("border-multicolored-color-left");
			if (col != null) {
				this.colorLeft = col.getColor();
			}
		//#endif
		//#if polish.css.border-multicolored-color-right
			col = style.getColorProperty("border-multicolored-color-right");
			if (col != null) {
				this.colorRight = col.getColor();
			}
		//#endif
		//#if polish.css.border-multicolored-color-top
			col = style.getColorProperty("border-multicolored-color-top");
			if (col != null) {
				this.colorTop = col.getColor();
			}
		//#endif
		//#if polish.css.border-multicolored-color-bottom
			col = style.getColorProperty("border-multicolored-color-bottom");
			if (col != null) {
				this.colorBottom = col.getColor();
			}
		//#endif
		//#if polish.css.border-multicolored-width
			Integer widthInt = style.getIntProperty("border-multicolored-width");
			if (widthInt != null) {
				int w = widthInt.intValue();
				this.borderWidthBottom = w;
				this.borderWidthTop = w;
				this.borderWidthLeft = w;
				this.borderWidthRight = w;
			}
		//#endif
		//#if polish.css.border-multicolored-width-left
			Integer widthLeftInt = style.getIntProperty("border-simple-width-left");
			if (widthLeftInt != null) {
				this.borderWidthLeft = widthLeftInt.intValue();
			}
		//#endif
		//#if polish.css.border-multicolored-width-right
			Integer widthRightInt = style.getIntProperty("border-simple-width-right");
			if (widthRightInt != null) {
				this.borderWidthRight = widthRightInt.intValue();
			}
		//#endif
		//#if polish.css.border-multicolored-width-top
			Integer widthTopInt = style.getIntProperty("border-simple-width-top");
			if (widthTopInt != null) {
				this.borderWidthTop = widthTopInt.intValue();
			}
		//#endif
		//#if polish.css.border-multicolored-width-bottom
			Integer widthBottomInt = style.getIntProperty("border-simple-width-bottom");
			if (widthBottomInt != null) {
				this.borderWidthBottom = widthBottomInt.intValue();
			}
		//#endif
	}
	//#endif

}
