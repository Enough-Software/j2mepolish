//#condition polish.usePolishGui
/*
 * Created on Dec 3, 2008 at 6:27:01 PM.
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

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.WrappedText;

/**
 * <p>Cycles through text so that only a single line or word is being shown.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CyclingTextEffect extends TextEffect
{

	private static final long DEFAULT_INTERVAL = 1 * 1000;
	private int currentRow;
	private long interval = DEFAULT_INTERVAL;
	private long lastSwitchTime;
	private Font textFont;
	private WrappedText wrappedText;



	/**
	 * Creates a new cycling text effect.
	 */
	public CyclingTextEffect()
	{
		// use set style to configure this effect
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate(de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Item parent, long currentTime,
			ClippingRegion repaintRegion)
	{
		
		super.animate(parent, currentTime, repaintRegion);
		boolean addRepaintRegion = false;
		if (this.lastSwitchTime == 0) {
			this.lastSwitchTime = currentTime;
			addRepaintRegion = true;
		} 
		else if (currentTime - this.lastSwitchTime > this.interval) {
			int index = this.currentRow + 1;
			if (index >= this.wrappedText.size()) {
				index = 0;
			}
			this.currentRow = index;
			this.lastSwitchTime = currentTime;
			addRepaintRegion = true;
		}
		if (addRepaintRegion) {
			parent.addRepaintArea(repaintRegion);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#showNotify()
	 */
	public void showNotify()
	{
		super.showNotify();
		this.lastSwitchTime = 0;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(de.enough.polish.ui.StringItem, java.lang.String, int, javax.microedition.lcdui.Font, int, int, int, java.lang.String, int, de.enough.polish.util.WrappedText)
	 */
	public void wrap(StringItem parent, String text, int textColor, Font font,
			int firstLineWidth, int lineWidth, int maxLines,
			String maxLinesAppendix, int maxLinesAppendixPosition,
			WrappedText wrappedTextResult) 
	{
		super.wrap(parent, text, textColor, font, firstLineWidth, lineWidth, maxLines,
				maxLinesAppendix, maxLinesAppendixPosition, wrappedTextResult);
		this.wrappedText = wrappedTextResult;
		this.textFont = font;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawStrings(de.enough.polish.util.WrappedText, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawStrings(WrappedText textLines, int textColor, int x, int y,
			int leftBorder, int rightBorder, int lineHeight, int maxWidth,
			int layout, Graphics g) 
	{
		if (this.wrappedText == null) {
			super.drawStrings(textLines, textColor, x, y, leftBorder, rightBorder,
				lineHeight, maxWidth, layout, g);
		}
		if ( ( layout & Item.LAYOUT_CENTER ) == Item.LAYOUT_CENTER ) {
			x = leftBorder + (rightBorder - leftBorder) / 2;
		} else if ( ( layout & Item.LAYOUT_RIGHT ) == Item.LAYOUT_RIGHT ) {
			x = rightBorder;
		}
		if ((layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM) {
			// this is either bottom or vcenter layout:
			if ((layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER) {
				y -= this.textFont.getBaselinePosition();
			} else {
				y -= this.textFont.getHeight();
			}
		}
		int anchor = this.style.getAnchorHorizontal();
		String line = this.wrappedText.getLine( this.currentRow );
		g.drawString( line, x, y, Graphics.TOP | anchor );

	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int anchor, Graphics g)
	{
		// can be ignored, as text is painted in in drawStrings
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		super.setStyle(style);
		//#if polish.css.text-cycling-interval
			Integer cyclingInterval = style.getIntProperty("text-cycling-interval");
			if (cyclingInterval != null) {
				this.interval = cyclingInterval.longValue();
			}
		//#endif
	}

	
}
