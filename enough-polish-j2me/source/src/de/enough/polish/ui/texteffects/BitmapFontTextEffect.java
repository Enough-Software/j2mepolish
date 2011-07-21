//#condition polish.usePolishGui
/*
 * Created on 11-Nov-2008 at 19:04:20.
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
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.BitMapFont;
import de.enough.polish.util.BitMapFontViewer;
import de.enough.polish.util.WrappedText;

/**
 * <p>Renders texts with a given bitmap font.</p>
 * <p>Activate the bitmap font text effect by specifying <code>text-effect: bitmap;</code> in your polish.css file.
 * <!--
 *    You can finetune the effect with following attributes:
 *    -->
 * </p>
 * <!--
 * <ul>
 * 	 <li><b>font-bitmap</b>: the URL of the font.</li>
 * </ul>
 *    -->
 *
 * <p>Copyright (c) 2009 Enough Software</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BitmapFontTextEffect extends TextEffect {

	protected transient BitMapFont font;
	protected transient BitMapFontViewer viewer;
//	private String[] lastText;

	/**
	 * Creates a text with smileys
	 */
	public BitmapFontTextEffect() {
		this.isTextSensitive = true;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#getFontHeight()
	 */
	public int getFontHeight()
	{
		if (this.font == null) {
			return super.getFontHeight();
		}
		return this.font.getFontHeight();
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#stringWidth(java.lang.String)
	 */
	public int stringWidth(String str)
	{
		if (this.font == null) {
			return super.stringWidth(str);
		}
		return this.font.stringWidth(str);
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#charWidth(char)
	 */
	public int charWidth(char c)
	{
		if (this.font == null) {
			return super.charWidth(c);
		}
		return this.font.charWidth(c);
	}

	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(de.enough.polish.ui.StringItem, java.lang.String, int, javax.microedition.lcdui.Font, int, int, int, java.lang.String, int, de.enough.polish.util.WrappedText)
	 */
	public void wrap(StringItem parent, String text, int textColor, Font meFont,
			int firstLineWidth, int lineWidth, int maxLines,
			String maxLinesAppendix, int maxLinesAppendixPosition,
			WrappedText wrappedText) 
	{
		if (this.font == null) {
			super.wrap(parent, text, textColor, meFont, firstLineWidth, lineWidth, maxLines,
					maxLinesAppendix, maxLinesAppendixPosition, wrappedText);
			return;
		}
		this.viewer = this.font.getViewer(text,textColor);
		if (this.viewer == null) {
			super.wrap(parent, text, textColor, meFont, firstLineWidth, lineWidth, maxLines,
					maxLinesAppendix, maxLinesAppendixPosition, wrappedText);
			return;
		}
		int pv = 1;
		int anchor = Graphics.LEFT;
		if (this.style != null) {
			pv = this.style.getPaddingVertical( lineWidth );
			anchor = this.style.getAnchorHorizontal();
		}
		this.viewer.layout(firstLineWidth, lineWidth, pv,  anchor, maxLines, maxLinesAppendix, this.font );
		this.viewer.wrap( text, wrappedText );
//		this.lastText = wrappedText;
	}





	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		super.setStyle(style);
		//#if polish.css.font-bitmap
			String url = style.getProperty("font-bitmap");
			if (url != null) {
				this.font = BitMapFont.getInstance(url);
			}
		//#endif
	}
	
	/**
	 * Set the bitmap font used in this text effect
	 * @param font the font
	 */
	public void setFont(BitMapFont font)
	{
		this.font = font;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawStrings(de.enough.polish.util.WrappedText, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawStrings(WrappedText textLines, int textColor, int x, int y,
			int leftBorder, int rightBorder, int lineHeight, int maxWidth,
			int layout, Graphics g) 
	{
//		if (textLines != this.lastText && this.font != null) {
//			StringBuffer buffer = new StringBuffer();
//			for (int i = 0; i < textLines.length; i++)
//			{
//				buffer.append( textLines[i] );
//				if (i != textLines.length - 1) {
//					buffer.append('\n');
//				}
//			}
//			
//			this.viewer = this.font.getViewer(buffer.toString(),textColor);
//			this.lastText = textLines;
//		}
		if (this.viewer == null) {
			super.drawStrings(textLines, textColor, x, y, leftBorder, rightBorder,
				lineHeight, maxWidth, layout, g);
		} else {
			if ( ( layout & Item.LAYOUT_CENTER ) == Item.LAYOUT_CENTER ) {
				x = leftBorder + (rightBorder - leftBorder) / 2;
			} else if ( ( layout & Item.LAYOUT_RIGHT ) == Item.LAYOUT_RIGHT ) {
				x = rightBorder;
			}
			if ((layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM) {
				// this is either bottom or vcenter layout:
				int fontHeight = this.font.getFontHeight();
				if ((layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER) {
					y -= fontHeight / 2;
				} else {
					y -= fontHeight;
				}
			}
			this.viewer.paint(x, y, g);
		}
		
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g)
	{
		// just in case no font is defined:
		g.drawString( text, x, y, orientation );
		
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawChar(char, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawChar(char c, int x, int y, int anchor, Graphics g)
	{
		if (this.font == null) {
			super.drawChar(c, x, y, anchor, g);
		}
		
		this.font.drawChar(c, x, y, anchor, g );
	}


	
	

}
