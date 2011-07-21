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

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Paints a translucent rectangle with round corners as a background.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Robert Virkus, robert@enough.de
 */
public class TranslucentRoundRectBackground 
extends Background 
{
	private int color;
	private int arcWidth;
	private int arcHeight;
	private final int borderColor; 
	private int[] buffer;
	private int lastWidth;
	private int lastHeight;
	
	/**
	 * Creates a new round rectangle background.
	 * 
	 * @param color the argb color of the background
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 */
	public TranslucentRoundRectBackground( int color, int arcWidth, int arcHeight) {
		this( color, arcWidth, arcHeight, 0, 0 );
	}


	/**
	 * Creates a new round rectangle background with a border.
	 * 
	 * @param color the argb color of the background
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 * @param borderColor the color of the border
	 * @param borderWidth the width of the border
	 */
	public TranslucentRoundRectBackground( int color,  int arcWidth, int arcHeight, int borderColor, int borderWidth) {
		this.color = color;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		//#if polish.blackberry
			net.rim.device.api.ui.Graphics bbGraphics = null;
			//# bbGraphics = g.g;
			int alpha = this.color >>> 24;
			bbGraphics.setGlobalAlpha( alpha );
			bbGraphics.setColor( this.color );
			bbGraphics.fillRoundRect(x, y, width, height, this.arcWidth, this.arcHeight );
			bbGraphics.setGlobalAlpha( 0xff ); // reset to fully opaque
		//#elif polish.midp2
			//#define tmp.useBuffer
			if (this.buffer == null || width != this.lastWidth || height != this.lastHeight ) {
				if (width < this.arcWidth || height < this.arcHeight) {
					return;
				}
				Image image = Image.createImage( width, height );
				Graphics imageG = image.getGraphics();
				imageG.setColor( 0 );
				imageG.fillRoundRect(0, 0, width, height, this.arcWidth, this.arcHeight );
				int[] imageData = new int[ width * height ];
				image.getRGB(imageData, 0, width, 0, 0, width, height );
				int targetColor = imageData[ width + width / 2];
				boolean isLastPixelFullyTransparent = true;
				int halfTransparentColor = ((this.color >>> 1) | (0x00ffffff)) & (this.color | 0xff000000);
				for (int i = 0; i < imageData.length; i++) {
					int col = imageData[i];
					if (col == targetColor) {
						if (isLastPixelFullyTransparent) {
							imageData[i] = halfTransparentColor;
							isLastPixelFullyTransparent = false;
						} else {
							imageData[i] = this.color;
						}
					} else {
						// the remaining white part has to be fully transparent:
						imageData[i] = 0x00000000;
						if (!isLastPixelFullyTransparent) {
							imageData[i-1] = halfTransparentColor;
							isLastPixelFullyTransparent = true;
						}
					}
				}
				this.buffer = imageData;
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
			g.setColor( this.color );
			g.fillRoundRect(x, y, width, height, this.arcWidth, this.arcHeight);
		//#endif
	}
	

	//#if polish.css.animations
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
		 */
		public void setStyle(Style style)
		{
			boolean resetBuffer = false;
			//#if polish.css.background-round-rect-translucent-color
				Color col = style.getColorProperty("background-round-rect-translucent-color");
				if (col != null) {
					this.color = col.getColor();
					resetBuffer = true;
				}
			//#endif
			//#if polish.css.background-round-rect-translucent-arc
				Integer arcInt = style.getIntProperty("background-round-rect-translucent-arc");
				if (arcInt != null) {
					this.arcWidth = arcInt.intValue();
					this.arcHeight = arcInt.intValue();
					resetBuffer = true;
				}
			//#endif
			//#if polish.css.background-round-rect-translucent-arc-width
				Integer arcWidthInt = style.getIntProperty("background-round-rect-translucent-arc-width");
				if (arcWidthInt != null) {
					this.arcWidth = arcWidthInt.intValue();
					resetBuffer = true;
				}
			//#endif
			//#if polish.css.background-round-rect-translucent-arc-height
				Integer arcHeightInt = style.getIntProperty("background-round-rect-translucent-arc-height");
				if (arcHeightInt != null) {
					this.arcHeight = arcHeightInt.intValue();
					resetBuffer = true;
				}
			//#endif
			//#if tmp.useBuffer
				if (resetBuffer) {
					this.buffer = null;
				}
			//#endif
		}
	//#endif	

}
