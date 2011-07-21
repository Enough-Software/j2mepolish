//#condition polish.usePolishGui
/*
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

package de.enough.polish.ui;

import javax.microedition.lcdui.Graphics;

/**
 * Displays the time along with the actual text title on a screen.
 * @author Robert Virkus
 */
public class TimeTitleItem extends StringItem {

	private ClockItem clockItem;
	private int textYAdjust;

	/**
	 * @param label
	 * @param text
	 */
	public TimeTitleItem(String label, String text) {
		this(label, text, Item.PLAIN, null);
	}

	/**
	 * @param label
	 * @param text
	 * @param style
	 */
	public TimeTitleItem(String label, String text, Style style) {
		this(label, text, Item.PLAIN, style);
	}

	/**
	 * @param label
	 * @param text
	 * @param appearanceMode
	 */
	public TimeTitleItem(String label, String text, int appearanceMode) {
		this(label, text, appearanceMode, null);
	}

	/**
	 * @param label
	 * @param text
	 * @param appearanceMode
	 * @param style
	 */
	public TimeTitleItem(String label, String text, int appearanceMode, Style style) {
		super(label, text, appearanceMode, style);
		//#style titleClock?
		this.clockItem = new ClockItem(null);
		this.clockItem.parent = this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		this.clockItem.animate(currentTime, repaintRegion);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth,int availHeight) {
		super.initContent(firstLineWidth, availWidth, availHeight);
		ClockItem clock = this.clockItem;
		int w = clock.getItemWidth(firstLineWidth, availWidth, availHeight);
		int h = clock.itemHeight;
		if (w > this.contentWidth) {
			this.contentWidth = w;
			clock.relativeX = 0;
		} else {
			if (clock.isLayoutRight()) {
				clock.relativeX = this.contentWidth - w;
			} else if (clock.isLayoutCenter()) {
				clock.relativeX = (this.contentWidth - w)/2;
			} else {
				clock.relativeX = 0;
			}
		}
		if (h > this.contentHeight) {
			if (isLayoutVerticalCenter()) {
				this.textYAdjust = (h - this.contentHeight)/2;
			} else if (isLayoutBottom()) {
				this.textYAdjust = (h - this.contentHeight);
			}
			this.contentHeight = h;
			clock.relativeY = 0;
		} else {
			this.textYAdjust = 0;
			if (clock.isLayoutVerticalCenter()) {
				clock.relativeY = (this.contentHeight - h)/2;
			} else if (clock.isLayoutBottom()) {
				clock.relativeY = (this.contentHeight - h);
			} else {
				clock.relativeY = 0;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder,Graphics g) {
		super.paintContent(x, y + this.textYAdjust, leftBorder, rightBorder, g);
		ClockItem clock = this.clockItem;
		clock.paint(x + clock.relativeX, y + clock.relativeY, leftBorder, rightBorder, g);
	}
	
	//TODO allow styling of clock item with clock-style CSS attribute or something 

}
