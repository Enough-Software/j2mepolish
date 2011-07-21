//#condition polish.usePolishGui
/*
 * Created on Dec 8, 2008 at 7:38:29 AM.
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
package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.StringItem;

/**
 * <p>Aligns elements according to the MIDP 2.0 layout directives of the items.</p>
 * <p>Usage:
 * </p>
 * <pre>
 * .myForm {
 * 		view-type: midp2;
 * }
 * </pre>
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Midp2ContainerView extends ContainerView
{
	
	Dimension contentX;

	/**
	 * Creates a new view type
	 */
	public Midp2ContainerView()
	{
		// use style settings for configuration
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight)
	{
		super.initContent(parentContainerItem, firstLineWidth, availWidth, availHeight);
		// now just adjust positions, so that the elements are layout according to their settings:
		Item[] items = this.parentContainer.getItems();
		initContent( items, firstLineWidth, availWidth, availHeight );
	}

	/**
	 * Initiates this view for the specified items
	 * @param items the items
	 * @param firstLineWidth available width for the first line
	 * @param availWidth available width for the view
	 * @param availHeight available height for the view
	 */
	public void initContent(Item[] items, int firstLineWidth, int availWidth, int availHeight) 
	{
		int x = 0;
		int y = 0;
		int currentRowHeight = 0;
		int currentRowStartIndex = 0;
		int maxRowWidth = 0;
		int availRowWidth = firstLineWidth;
		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			
			int lo = item.getLayout();
			if (((lo & Item.LAYOUT_NEWLINE_BEFORE) == Item.LAYOUT_NEWLINE_BEFORE) || (x + item.getContentWidth() > availRowWidth) ) 
			{
				if (currentRowHeight != 0) {
					addLineBreak( items, currentRowStartIndex, i - 1, x, currentRowHeight, availRowWidth );
					y += currentRowHeight + this.paddingVertical;
					currentRowHeight = 0;
					availRowWidth = availWidth;
				}
				if (x > maxRowWidth) {
					maxRowWidth = x;
				}
				x = 0;
				currentRowStartIndex = i;
			}
			item.relativeX = x;
			item.relativeY = y;
			if (item.itemWidth > availRowWidth) {
				// item has probably expand layout, as the content width fits within this row:
				item.getItemWidth( availRowWidth - x, availRowWidth - x, availHeight );
			}
			x += item.itemWidth;
			if (item.itemHeight > currentRowHeight) {
				currentRowHeight = item.itemHeight;
			}
			if (x >= availRowWidth || ((lo & Item.LAYOUT_NEWLINE_AFTER) == Item.LAYOUT_NEWLINE_AFTER) || (i == items.length -1)) {
				if (currentRowHeight != 0) {
					addLineBreak( items, currentRowStartIndex, i, x, currentRowHeight, availRowWidth);
					y += currentRowHeight + this.paddingVertical;
					currentRowHeight = 0;
					availRowWidth = availWidth;
				}
				if (x > maxRowWidth) {
					maxRowWidth = x;
				}
				x = 0;
				currentRowStartIndex = i + 1;
			}
		}
		if (x != 0) {
			//#debug
			System.out.println("Midp2ContainerView: currentRowHeight=" + currentRowHeight + ", x=" + x + ", maxRowWidth=" + maxRowWidth);
			if (currentRowHeight != 0) {
				addLineBreak( items, currentRowStartIndex, items.length-1, x, currentRowHeight, availRowWidth);
				y += currentRowHeight;
				availRowWidth = availWidth;
			}
			if (x > maxRowWidth) {
				maxRowWidth = x;
			}
		}
		this.contentHeight = y;
		this.contentWidth = maxRowWidth;		
	}

	/**
	 * Adds a linebreak to the current list of items.
	 */
	protected void addLineBreak(Item[] items, int currentRowStartIndex, int currentRowEndIndex, int currentRowWidth, int currentRowHeight, int availWidth)
	{
		int diff = 0;
		if (this.isLayoutCenter) {
			diff = (availWidth - currentRowWidth) / 2;
		} else if (this.isLayoutRight) {
			diff = (availWidth - currentRowWidth);
		}
		for (int i=currentRowStartIndex; i <= currentRowEndIndex; i++) {
			Item item = items[i];
			int lo = item.getLayout();
			if ((lo & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER ) {
				item.relativeY += (currentRowHeight - item.itemHeight) / 2;
			} else if ((lo & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM ) {
				item.relativeY += (currentRowHeight - item.itemHeight);
			}
			item.relativeX += diff;
			if (i == currentRowEndIndex) {
				if  ((lo & Item.LAYOUT_RIGHT) == Item.LAYOUT_RIGHT)
				{
					item.relativeX = availWidth - item.itemWidth;
				}
			}
		}
	}

	/**
	 * Paints the content of this MIDP2 view.
	 * 
	 * @param items the nested items
	 * @param x
	 * @param y
	 * @param leftBorder
	 * @param rightBorder
	 * @param g
	 */
	public void paintContent(Item[] items, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		super.paintContent( null, items, x, y, leftBorder, rightBorder, g.getClipX(), g.getClipY(), g.getClipWidth(), g.getClipHeight(), g);
		
	}

	/**
	 * Retrieves the content height
	 * @return the height of this view
	 */
	public int getContentHeight() {
		return this.contentHeight;
	}
	
	/**
	 * Retrieves the content width
	 * @return the width of this view
	 */
	public int getContentWidth() {
		return this.contentWidth;
	}

}
