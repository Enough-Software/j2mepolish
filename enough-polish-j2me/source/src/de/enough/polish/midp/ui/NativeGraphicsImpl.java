//#condition polish.usePolishGui && polish.midp && !(polish.android || polish.blackberry)
/*
 * Created on Mar 8, 2010 at 9:43:58 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.midp.ui;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Font;
import de.enough.polish.ui.Image;
import de.enough.polish.ui.NativeGraphics;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Provides access to the native LCDUI Graphics object.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class NativeGraphicsImpl implements NativeGraphics {
	
	private Graphics g;

	/**
	 * 
	 */
	public NativeGraphicsImpl(Graphics g) {
		this.g = g;
	}

	public void drawImage(Image img, int x, int y, int anchor) 
	{
		//#if polish.midp2
			this.g.drawImage(UiAccess.cast(img), x, y, anchor);
		//#endif
	}

	public void drawRegion(Image src, int xSrc, int ySrc, int width,
			int height, int transform, int xDest, int yDest, int anchor) 
	{
		//#if polish.midp2
			this.g.drawRegion(UiAccess.cast(src), xSrc, ySrc, width, height, transform, xDest, yDest, anchor);
		//#endif
	}

	public void setFont(Font font) {
		this.g.setFont( font.getNativeFont() );
	}

	public void clipRect(int x, int y, int width, int height) {
		// TODO Besitzer implement clipRect
		
	}

	public void copyArea(int xSrc, int ySrc, int width, int height, int xDest,
			int yDest, int anchor) {
		// TODO Besitzer implement copyArea
		
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Besitzer implement drawArc
		
	}

	public void drawChar(char character, int x, int y, int anchor) {
		// TODO Besitzer implement drawChar
		
	}

	public void drawChars(char[] data, int offset, int length, int x, int y,
			int anchor) {
		// TODO Besitzer implement drawChars
		
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		// TODO Besitzer implement drawLine
		
	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x,
			int y, int width, int height, boolean processAlpha) {
		// TODO Besitzer implement drawRGB
		
	}

	public void drawRect(int x, int y, int width, int height) {
		// TODO Besitzer implement drawRect
		
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Besitzer implement drawRoundRect
		
	}

	public void drawString(String str, int x, int y, int anchor) {
		// TODO Besitzer implement drawString
		
	}

	public void drawSubstring(String str, int offset, int len, int x, int y,
			int anchor) {
		// TODO Besitzer implement drawSubstring
		
	}

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Besitzer implement fillArc
		
	}

	public void fillRect(int x, int y, int width, int height) {
		// TODO Besitzer implement fillRect
		
	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Besitzer implement fillRoundRect
		
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		// TODO Besitzer implement fillTriangle
		
	}

	public int getBlueComponent() {
		// TODO Besitzer implement getBlueComponent
		return 0;
	}

	public int getClipHeight() {
		// TODO Besitzer implement getClipHeight
		return 0;
	}

	public int getClipWidth() {
		// TODO Besitzer implement getClipWidth
		return 0;
	}

	public int getClipX() {
		// TODO Besitzer implement getClipX
		return 0;
	}

	public int getClipY() {
		// TODO Besitzer implement getClipY
		return 0;
	}

	public int getColor() {
		// TODO Besitzer implement getColor
		return 0;
	}

	public int getDisplayColor(int color) {
		// TODO Besitzer implement getDisplayColor
		return 0;
	}

	public Font getFont() {
		// TODO Besitzer implement getFont
		return null;
	}

	public int getGrayScale() {
		// TODO Besitzer implement getGrayScale
		return 0;
	}

	public int getGreenComponent() {
		// TODO Besitzer implement getGreenComponent
		return 0;
	}

	public int getRedComponent() {
		// TODO Besitzer implement getRedComponent
		return 0;
	}

	public int getStrokeStyle() {
		// TODO Besitzer implement getStrokeStyle
		return 0;
	}

	public int getTranslateX() {
		// TODO Besitzer implement getTranslateX
		return 0;
	}

	public int getTranslateY() {
		// TODO Besitzer implement getTranslateY
		return 0;
	}

	public void setClip(int x, int y, int width, int height) {
		// TODO Besitzer implement setClip
		
	}

	public void setColor(int red, int green, int blue) {
		// TODO Besitzer implement setColor
		
	}

	public void setColor(int rgb) {
		// TODO Besitzer implement setColor
		
	}

	public void setGrayScale(int value) {
		// TODO Besitzer implement setGrayScale
		
	}

	public void setStrokeStyle(int style) {
		// TODO Besitzer implement setStrokeStyle
		
	}

	public void translate(int x, int y) {
		// TODO Besitzer implement translate
		
	}
	
	

}
