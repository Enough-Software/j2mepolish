//#condition polish.usePolishGui
/*
 * Created on 13-April-2007 at 19:49:13.
 *
 * Copyright (c) 2004-2007 Robert Virkus / Enough Software
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
import javax.microedition.lcdui.Image;

import de.enough.polish.util.DrawUtil;


/**
 * <p>Paints a translucent rectangle with round corners as a background.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Robert Virkus, robert@enough.de
 */
public class RoundRectDoubleGradientVerticalBackground 
extends DoubleGradientVerticalBackground 
{
	private final int arcWidth;
	private final int arcHeight;
	private final int borderColor; 
	private int[] buffer;
	private int lastWidth;
	private int lastHeight;
	


	/**
	 * Creates a new round rectangle background with a border.
	 * 
	 * @param firstTopColor the color at the top of the first gradient
	 * @param firstBottomColor the color at the bottom of the first gradient
	 * @param secondTopColor the color at the top of the second gradient
	 * @param secondBottomColor the color at the bottom of the second gradient
	 * @param stroke the line stroke style
	 * @param start the line counted from the top at which the gradient starts, either in pixels or in percent
	 * @param end the line counted from the top at which the gradient ends, either in pixels or in percent
	 * @param isPercent true when the start and end settings should be counted in percent
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 * @param borderColor the color of the border
	 * @param borderWidth the width of the border
	 */
	public RoundRectDoubleGradientVerticalBackground( int firstTopColor, int firstBottomColor, int secondTopColor, int secondBottomColor, int stroke, int start, int end, boolean isPercent,  int arcWidth, int arcHeight, int borderColor, int borderWidth) 
	{
		super( firstBottomColor, firstBottomColor, secondTopColor, secondBottomColor, stroke, start, end, isPercent );
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		//#if polish.midp2
			if (this.buffer == null || width != this.lastWidth || height != this.lastHeight ) {
				if (width < this.arcWidth || height < this.arcHeight || height < 2) {
					return;
				}
				Image image = Image.createImage( width, height );
				Graphics imageG = image.getGraphics();
				super.paint(0, 0, width, height, imageG);
				if (this.borderWidth > 0) {
					imageG.setColor( this.borderColor );
					int b = this.borderWidth;
					while (b > 0) {
						imageG.drawRoundRect(b, b, width - (b<<1), height - (b<<1), this.arcWidth, this.arcHeight );
						b--;
					}
				}
				int[] gradientImageData = new int[ width* height ];
				image.getRGB(gradientImageData, 0, width, 0, 0, width, height );
				imageG.setColor( 0 );
				imageG.fillRoundRect(0, 0, width, height, this.arcWidth, this.arcHeight );
				int[] roundImageData = new int[ width * height ];
				image.getRGB(roundImageData, 0, width, 0, 0, width, height );
				int targetColor = roundImageData[ width + (width >> 1)];
				boolean isLastPixelFullyTransparent = true;
				int halfTransparentColor = 0; //((this.color >>> 1) | (0x00ffffff)) & (this.color | 0xff000000);
				for (int i = 0; i < roundImageData.length; i++) {
					int col = roundImageData[i];
					if (col == targetColor) {
						if (isLastPixelFullyTransparent) {
							gradientImageData[i] = halfTransparentColor;
							isLastPixelFullyTransparent = false;
						}
					} else {
						// the remaining white/colorful part has to be fully transparent:
						gradientImageData[i] = 0x00000000;
						if (!isLastPixelFullyTransparent) {
							gradientImageData[i-1] = halfTransparentColor;
							isLastPixelFullyTransparent = true;
						}
					}
				}
				this.buffer = gradientImageData;
				this.lastWidth = width;
				this.lastHeight = height;
			}
			//#ifdef polish.Bugs.drawRgbOrigin
				x += g.getTranslateX();
				y += g.getTranslateY();
			//#endif
			DrawUtil.drawRgb( this.buffer, x, y, width, height, true, g );
			// draw border:
			int border = this.borderWidth;
			if (border > 0) {
				g.setColor( this.borderColor );
				g.drawRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
				while ( border >= 0) {
					g.drawRoundRect( x+border, y+border, width - 2*border, height - 2*border, this.arcWidth, this.arcHeight );
					border--;
				}
			}
		//#else
			super.paint(x, y, width, height, g);
		//#endif
	}

}
