//#condition polish.usePolishGui
/*
 * Created on 27-March-2010 at 16:37:51.
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;


/**
 * <p>Shows  the available items of a Container in a horizontal list - the focused/current element is always shown at the very left side.</p>
 * <p>Apply this view by specifying "view-type: horizontal-left;" in your polish.css file.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LeftHorizontalContainerView extends HorizontalContainerView {
	

	private int[] xSlots;
	/**
	 * Creates a new view
	 */
	public LeftHorizontalContainerView() {
		super();
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentItm, int firstLineWidth, int availWidth, int availHeight) 
	{
		super.initContent(parentItm, firstLineWidth, availWidth, availHeight);
		Item[] myItems = this.parentContainer.getItems();
		this.xSlots = new int[ myItems.length ];
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			this.xSlots[i] = item.relativeX;
		}
		if (this.focusedItem != null) {
			// set scroll X offset:
			focusItem( this.focusedIndex, this.focusedItem, 0, this.focusedItem.getStyle() );
		}
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focusItem(int, de.enough.polish.ui.Item, int, de.enough.polish.ui.Style)
	 */
	public Style focusItem(int focIndex, Item item, int direction, Style focStyle) {
		int prevFocIndex = this.focusedIndex;
		Style result = super.focusItem( focIndex, item, direction, focStyle );
		
		if (this.xSlots != null) {
			Item[] myItems = this.parentContainer.getItems();
			int startX = this.contentWidth;
			for (int i = 0; i < myItems.length; i++) {
				Item myItem = myItems[i];
				if (i < focIndex) {
					myItem.relativeX = startX + this.xSlots[i];
				} else {
					myItem.relativeX = this.xSlots[i];
				}
			}				
			if (item != null) {
				if (focIndex < prevFocIndex && this.targetXOffset != this.xSlots[focIndex]) {
					setScrollXOffset( this.availableWidth + this.xOffset, false);
				}
				setScrollXOffset(-item.relativeX, true);
			}
		}
		return result;

	
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		//#debug
		System.out.println("paint " + this + " at " + x + ", " + y + ", with xOffset " + getScrollXOffset() + ",");
		
		super.paintContent(container, myItems, x, y, leftBorder, rightBorder, clipX,
				clipY, clipWidth, clipHeight, g);
		
		if (this.xOffset != this.targetXOffset) {
			//if (this.xOffset > this.targetXOffset) {
				// paint last item on left side:
	    		g.clipRect( x, y, rightBorder - x + 1, this.contentHeight + 1 );
				int index = this.focusedIndex - 1;
				if (index < 0) {
					index = myItems.length - 1;
					x -= this.contentWidth;
				}
				Item item = myItems[index];
				x += this.xOffset + this.xSlots[index];
				item.paint(x, y, x, x + item.itemWidth, g);
				g.setClip(clipX, clipY, clipWidth, clipHeight);
				//System.out.println("painting " + index + " at " + x + ", xOffset=" + this.xOffset + ", target=" + this.targetXOffset + ", xSlot=" + this.xSlots[index]);
			//} 
		}
		
//		//#if polish.css.horizontalview-focus-anchor && polish.css.horizontalview-roundtrip
//			if (this.allowRoundTrip && this.focusAnchor != 0) {
//				if (this.focusAnchor == Graphics.LEFT) {
//					int startX = x + this.contentWidth;
//					if ((this.focusedIndex > 0)  && (startX < this.availableWidth)) {
//						for (int i=0; i<this.focusedIndex; i++) {
//							Item item = myItems[i];
//							x = startX + item.relativeX + this.paddingHorizontal;
//							rightBorder = x + item.itemWidth;
//							paintItem(item, i, x, y + item.relativeY, x, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
//							if (x >= this.availableWidth) {
//								break;
//							}
//						}
//					}
//				}
//			}
//		//#endif
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#startScroll(int, int, int)
	 */
	public void startScroll( int direction,int speed, int damping) {
		if (direction == Canvas.UP || direction == Canvas.DOWN) {
			super.startScroll(direction, speed, damping);
		}

	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerDragged(int, int)
	 */
	public boolean handlePointerDragged(int x, int y) {
		return false;
	}
	//#endif
}
