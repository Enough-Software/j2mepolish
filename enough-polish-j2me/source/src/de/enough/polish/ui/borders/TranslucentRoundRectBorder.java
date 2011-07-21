//#condition polish.usePolishGui
/*
 * Created on 20-May-2008 at 16:09:10.
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
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Border;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Paints a border with round corners using RGB data.</p>
 *
 * <p>Copyright Enough Software2008</p>
 * @author Robert Virkus, robert@enough.de
 */
public class TranslucentRoundRectBorder extends Border {

	protected int color;
	private final int arcWidth;
	private final int arcHeight;
	private int[] rgbDataLt;
	private int[] rgbDataRt;
	private int[] rgbDataLb;
	private int[] rgbDataRb;

	/**
	 * Creates a new round rectangle border.
	 * 
	 * @param argbColor the color of the background
	 * @param borderWidth the width of the border
	 * @param arcWidth the horizontal diameter of the arc at the four corners
	 * @param arcHeight the vertical diameter of the arc at the four corners
	 */
	public TranslucentRoundRectBorder( int argbColor, int borderWidth, int arcWidth, int arcHeight ) {
		super(borderWidth, borderWidth, borderWidth, borderWidth);
		this.color = argbColor;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		g.setColor( this.color );
		boolean isTransparent = (this.color & 0xff000000) != 0;
		//#if polish.midp2
		if (this.borderWidthLeft == 1 && !isTransparent) {
		//#endif
			width--;
			height--;
			g.drawRoundRect( x, y, width, height, this.arcWidth, this.arcHeight );
		//#if polish.midp2
		} else {
			int[] lt = this.rgbDataLt;
			int[] rt = this.rgbDataRt;
			int[] lb = this.rgbDataLb;
			int[] rb = this.rgbDataRb;
			int aw = this.arcWidth;
			int ah = this.arcHeight;
			int bw = this.borderWidthLeft;
			if (lt == null) {
				// setup RGB data:
				int bgColor = DrawUtil.getComplementaryColor(this.color);
				if (bgColor == this.color) {
					bgColor = 0;
				}
				//bgColor = StyleSheet.display.getColor(bgColor);
				int maxW = Math.max( bw, aw );
				int maxH = Math.max( bw, ah );
				Image img = Image.createImage( maxW, maxH );
				Graphics imgG = img.getGraphics();
				imgG.setColor(bgColor);
				imgG.fillRect( 0, 0, maxW, maxH );
				imgG.setColor( this.color );
				imgG.fillRoundRect( 0, 0, width, height, aw, ah );
				imgG.setColor( bgColor );
				imgG.fillRoundRect( bw, bw, width - (bw << 1), height - (bw << 1), aw, ah );
				lt = new int[ maxW * maxH  ];
				img.getRGB(lt, 0, maxW, 0, 0, maxW, maxH);
				bgColor = lt[0];
				int transparencyMask;
				if (isTransparent) {
					transparencyMask = ((this.color & 0xff000000) | (0x00ffffff & this.color));
				} else {
					transparencyMask = (this.color | 0xff000000);
				}
				boolean isLastPixelFullyTransparent = true;
				int halfTransparentColor = ((this.color >>> 1) | (0x00ffffff)) & (this.color | 0xff000000);
				for (int row = 0; row < maxH; row++) {
					isLastPixelFullyTransparent = true;
					for (int column = 0; column < maxW; column++) {
						int i = column + ( row * maxW );
						//boolean isBorderPixel = (column == 0 || column == maxW -1);
						
						int pixel = lt[i];
						if (pixel == bgColor) {
							lt[i] = 0x00000000;
							if (!isLastPixelFullyTransparent) {
								lt[i-1] = halfTransparentColor;
								isLastPixelFullyTransparent = true;
							}
						} else {
							// a real pixel is found
							if (isLastPixelFullyTransparent) {
								lt[i] = halfTransparentColor;
								isLastPixelFullyTransparent = false;
							} else if (isTransparent) {
								lt[i] = pixel & transparencyMask;
							}
						}
					}
				}
				rt = new int[ lt.length ];
				ImageUtil.rotateSimple(lt, rt, maxW, maxH, 90 );
				rb = new int[ lt.length ];
				ImageUtil.rotateSimple(lt, rb, maxW, maxH, 180 );
				lb = new int[ lt.length ];
				ImageUtil.rotateSimple(lt, lb, maxW, maxH, 270 );
				this.rgbDataRt = rt;
				this.rgbDataRb = rb;
				this.rgbDataLb = lb;
				this.rgbDataLt = lt;
			}
			int clipX = g.getClipX();
			int clipY = g.getClipY();
			int clipW = g.getClipWidth();
			int clipH = g.getClipHeight();
			// draw RGB data:
			DrawUtil.drawRgb(lt, x, y, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(rt, x + width - aw, y, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(lb, x, y + height - ah, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(rb, x + width - aw, y + height - ah, aw, ah, true, clipX, clipY, clipW, clipH, g );
			// draw lines:
			for (int i=0; i<bw; i++){
				if (isTransparent) {
					DrawUtil.drawLine( this.color, x + aw, y + i, x + width - aw, y + i, g ); // top
					DrawUtil.drawLine( this.color, x + aw, y + height - i, x + width - aw, y + height - i, g ); // bottom
					DrawUtil.drawLine( this.color, x + i, y + ah, x + i, y + height - ah, g ); // left
					DrawUtil.drawLine( this.color, x + width - i, y + ah, x + width - i, y + height - ah, g ); // right
				} else {
					g.drawLine( x + aw, y + i, x + width - aw, y + i ); // top
					g.drawLine( x + aw, y + height - i, x + width - aw, y + height - i ); // bottom
					g.drawLine( x + i, y + ah, x + i, y + height - ah ); // left
					g.drawLine( x + width - i, y + ah, x + width - i, y + height - ah ); // right					
				}
			}
		}
		//#endif
	}


}
