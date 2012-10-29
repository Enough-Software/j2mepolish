//#condition polish.usePolishGui
/*
 * Created on 16-Nov-2005 at 12:22:20.
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

import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.WrappedText;

/**
 * <p>Uses bold for a text without changing the size.</p>
 * <p>Activate the shadow text effect by specifying <code>text-effect: bold;</code> in your polish.css file.
 * </p>
 *
 * <p>Copyright (c) Enough Software 2012</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BoldTextEffect extends TextEffect {
	
	private Font	font;


	/**
	 * Creates a shadow effect.
	 */
	public BoldTextEffect() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(de.enough.polish.ui.StringItem, java.lang.String, int, javax.microedition.lcdui.Font, int, int, int, java.lang.String, int, de.enough.polish.util.WrappedText)
	 */
	public void wrap(StringItem parent, String text, int textColor, Font font,
			int firstLineWidth, int lineWidth, int maxLines,
			String maxLinesAppendix, int maxLinesAppendixPosition,
			WrappedText wrappedText)
	{
		super.wrap(parent, text, textColor, font, firstLineWidth, lineWidth, maxLines,
				maxLinesAppendix, maxLinesAppendixPosition, wrappedText);
		this.font = Font.getFont( font.getFace(), font.getSize(), font.getStyle() | Font.STYLE_BOLD);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y, int orientation,
			Graphics g) 
	{
		g.drawString(text, x, y, orientation);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawStrings(de.enough.polish.util.WrappedText, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawStrings(WrappedText textLines, int textColor, int x, int y,
			int leftBorder, int rightBorder, int lineHeight, int maxWidth,
			int layout, Graphics g)
	{
		g.setFont(this.font);
		super.drawStrings(textLines, textColor, x, y, leftBorder, rightBorder,
				lineHeight, maxWidth, layout, g);
	}





}
