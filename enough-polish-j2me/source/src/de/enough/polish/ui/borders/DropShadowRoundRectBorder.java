//#condition polish.usePolishGui

/*
 * Created on 19-Aug-2008 at 23:03:12.
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
 * <p>Paints a translucent shadow border with rounded corners.</p>
 *
 * <p>Copyright (c) Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DropShadowRoundRectBorder extends Border {
	/** paints the border at the bottom and the right sides of the corresponding item */
	public final static int BOTTOM_RIGHT = 0;
	/** paints the border at the top and the right sides of the corresponding item */
	public final static int TOP_RIGHT = 1;
	/** paints the border at the bottom and the left sides of the corresponding item */
	public final static int BOTTOM_LEFT = 2;
	/** paints the border at the top and the left sides of the corresponding item */
	public final static int TOP_LEFT = 3;
	/** paints the border at all sides of the corresponding item */
	public final static int ALL = 4;
	
	private final int[] shadowColors;
	private final int orientation;
	private int arcWidth;
	private int arcHeight;
	private int[] rgbDataLt;
	private int[] rgbDataRt;
	private int[] rgbDataLb;
	private int[] rgbDataRb;
	private int borderWidth;

	public DropShadowRoundRectBorder( int innerColor, int outerColor, int width, int orientation, int arcWidth, int arcHeight ) {
		super(0, 0, 0, 0);
		this.borderWidth = width;
		this.orientation = orientation;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.shadowColors = DrawUtil.getGradient(outerColor, innerColor, width);
	}

	public void paint(int x, int y, int width, int height, Graphics g) {
		//#if polish.midp2
		
		int[] lt = this.rgbDataLt;
		int[] rt = this.rgbDataRt;
		int[] lb = this.rgbDataLb;
		int[] rb = this.rgbDataRb;
		int aw = this.arcWidth;
		int ah = this.arcHeight;
		int bw = this.borderWidth;
		if (lt == null) {
			// setup RGB data:
			int maxW = Math.max( bw, aw );
			int maxH = Math.max( bw, ah );
			Image img = Image.createImage( maxW, maxH );
			Graphics imgG = img.getGraphics();
			imgG.setColor( 0 );
			imgG.fillRoundRect( 0, 0, width, height, aw, ah );
			lt = new int[ maxW * maxH  ];
			img.getRGB(lt, 0, maxW, 0, 0, maxW, maxH);
			
			int bgColor = lt[0];
			int innerColor = this.shadowColors[ bw - 1 ];
			int[] startRows = new int[ maxH ];
			for (int i = 0; i < startRows.length; i++)
			{
				startRows[i] = -1;
			}
			int startColumn = -1;
			for (int row = 0; row < maxH; row++) {
				for (int column = 0; column < maxW; column++) {
					int i = column + ( row * maxW );
					int pixel = lt[i];
					if (pixel == bgColor) {
						lt[i] = 0x00000000;
					} else {
						if (startColumn == -1) {
							startColumn = column;
						}
						int startRow = startRows[column];
						if (startRow == -1) {
							startRow = row;
							startRows[column] = startRow; 
						}
						int min = Math.min(column - startColumn, row - startRow);
						if (min < bw) {
							lt[i] = this.shadowColors[min];
						} else {
							lt[i] = innerColor;
						}
					}
				}
				startColumn = -1;
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
		switch (this.orientation) {
		case BOTTOM_RIGHT:
			DrawUtil.drawRgb(rt, x + width - (aw - bw), y + bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(lb, x + bw, y + height - (ah - bw), aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(rb, x + width - (aw - bw), y + height - (ah - bw), aw, ah, true, clipX, clipY, clipW, clipH, g );
			break;
		case TOP_RIGHT:
			DrawUtil.drawRgb(lt, x + bw, y - bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(rt, x + width - aw + bw, y - bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(rb, x + width - (aw - bw), y + height - ah - bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			break;
		case BOTTOM_LEFT:
			DrawUtil.drawRgb(lt, x - bw, y + bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(lb, x - bw, y + height + bw - ah, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(rb, x + width - aw - bw, y + height + bw - ah, aw, ah, true, clipX, clipY, clipW, clipH, g );
			break;
		case TOP_LEFT:
			DrawUtil.drawRgb(lt, x - bw, y - bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(rt, x + width - aw - bw, y - bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(lb, x - bw, y + height - ah - bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			break;
		case ALL:
			DrawUtil.drawRgb(lt, x - bw, y - bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(rt, x + width - (aw - bw), y - bw, aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(lb, x - bw, y + height - (ah - bw), aw, ah, true, clipX, clipY, clipW, clipH, g );
			DrawUtil.drawRgb(rb, x + width - (aw - bw), y + height - (ah - bw), aw, ah, true, clipX, clipY, clipW, clipH, g );
			break;
		}
		// draw lines:
		int left = x;
		int top = y;
		int right = x + width;
		int bottom = y + height;
		for (int i = 0; i < bw; i++ ) {
			int color = this.shadowColors[bw-(i+1)];
			
			switch (this.orientation) {
			case BOTTOM_RIGHT:
				// right:
				DrawUtil.drawLine(color, right + i, top + ah + bw, right + i, bottom + bw - ah, g);
				// bottom:
				DrawUtil.drawLine(color, left + bw + aw, bottom + i, right + bw - aw, bottom + i, g);
				break;
			case TOP_RIGHT:
				// right:
				DrawUtil.drawLine(color, right + i, top - bw + ah, right + i, bottom - bw - ah, g);
				// top:
				DrawUtil.drawLine(color, left + bw + aw, top - i - 1, right + bw - aw, top - i - 1, g);
				break;
			case BOTTOM_LEFT:
				// left:
				DrawUtil.drawLine(color, left - i - 1, top + bw + ah, left - i - 1, bottom - ah + bw, g);
				// bottom:
				DrawUtil.drawLine(color, left - bw + aw, bottom + i, right - bw - aw, bottom + i, g);
				break;
			case TOP_LEFT:
				// left:
				DrawUtil.drawLine(color, left - i - 1, top - bw + ah, left - i - 1, bottom - bw - ah, g);
				// top:
				DrawUtil.drawLine(color, left - bw + aw, top - i - 1, right - bw - aw, top - i - 1, g);
				break;
			case ALL:
				//#if polish.blackberry
					// on blackberry offsets react different than on other devices:
	                // left:
	                DrawUtil.drawLine(color, left - i - 1, top - bw + ah, left - i - 1, bottom + bw - ah - 1, g);
	                // right:
	                DrawUtil.drawLine(color, right + i, top - bw + ah, right + i, bottom + bw - ah - 1, g);
	                // bottom:
	                DrawUtil.drawLine(color, left - bw + aw, bottom + i, right + bw - aw - 1, bottom + i, g);
	                // top:
	                DrawUtil.drawLine(color, left - bw + aw, top - i - 1, right + bw - aw - 1, top - i - 1, g);
				//#else
					// left:
					DrawUtil.drawLine(color, left - i - 1, top - bw + ah, left - i - 1, bottom + bw - ah, g);
					// right:
					DrawUtil.drawLine(color, right + i, top - bw + ah, right + i, bottom + bw - ah, g);
					// bottom:
					DrawUtil.drawLine(color, left - bw + aw, bottom + i, right + bw - aw, bottom + i, g);
					// top:
					DrawUtil.drawLine(color, left - bw + aw, top - i - 1, right + bw - aw, top - i - 1, g);
				//#endif
				break;
			}
		}
		//#endif
	}

	/**
	 * @param column
	 * @param row
	 * @param maxW
	 * @param maxH
	 * @param lt
	 * @return
	 */
	private int getPixel(int column, int row, int maxW, int maxH, int[] lt, int bgColor)
	{
		// check the real column of this pixel:
		int start = row * maxW;
		for (int i=start; i > start - maxW; i--) {
			int pixel = lt[i];
			if (pixel != bgColor) {
				// found left border
				if ((column - i) < this.borderWidth) {
					return this.shadowColors[ column - i ];
				} else if (row < this.borderWidth){
					return this.shadowColors[ row ];
				} else if (maxH - row < this.borderWidth){
					return this.shadowColors[ maxH - row ];
				}
			}
		}
		return 0;
	}


}
