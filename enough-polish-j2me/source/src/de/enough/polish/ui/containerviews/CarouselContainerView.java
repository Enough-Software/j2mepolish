//#condition polish.usePolishGui

/*
 * Created on Oct 11, 2007 at 12:12:47 PM.
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

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Arranges the items in an animated carousel.</p>
 * <p>Activate this view by specifying <code>view-type: carousel</code> in the ChoiceGroup's, Container's or List's style.</p>
 * <p>Further attributes are:</p>
 * <ul>
 *  <!-- (none at the moment)
 *  <li><b></b>: </li>
 *  -->
 * </ul>
 * <p>Since this view extends the fisheye view, you can also use all fisheyeview attributes, e.g.:</p>
 * <ul>
 *  <li><b>fisheyeview-remove-text</b>: removes the text of embedded items and only shows the currently selected one</li>
 *  <li><b>fisheyeview-scale</b> or <b>fisheyeview-scale-start</b>: the percentage value to which the items should be scaled down, e.g. <code>fisheyeview-scale: 60%;</code></li>
 *  <li><b>fisheyeview-scale-end</b>: the percentage value to which the items located at the edges should be scaled down, e.g. <code>fisheyeview-scale-end: 30%;</code></li>
 *  <li><b>fisheyeview-alpha-start</b>: the translucency between 0 and 255 for items directly next to the central item of the view. Higher values indicate more opaqness, lower ones make it more translucent, e.g. <code>fisheyeview-alpha-end: 80;</code></li>
 *  <li><b>fisheyeview-alpha-end</b>: the translucency between 0 and 255 at the edge of the view. Higher values indicate more opaqness, lower ones make it more translucent, e.g. <code>fisheyeview-alpha-end: 80;</code></li>
 *  <li><b>show-text-in-title</b>: uses the text of embedded items for the screen title instead of displaying it under the item</li>
 *  <!--
 *  <li><b></b>: </li>
 *  -->
 * </ul>
 * <p>Example:
 * <pre>
 * .myList {
 * 		view-type: carousel;
 * 		fisheyeview-remove-text: true;
 * 		fisheyeview-scale-start: 70%;
 * 		fisheyeview-scale-end: 40%;
 * 		fisheyeview-alpha-start: 200;
 * 		fisheyeview-alpha-end: 100;
 *      background-color: green;
 *      padding: 5;
 * }
 * </pre>
 * </p>
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Oct 11, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see FishEyeContainerView
 */
public class CarouselContainerView extends FishEyeContainerView {
	
	private boolean isFocusedAtBottom = true;
	private Dimension maximumHeight;

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.containerviews.FishEyeContainerView#initItemArrangement(int, de.enough.polish.ui.Item[], int, int, int)
	 */
	protected void initItemArrangement(int lineWidth, int lineHeight, Item[] myItems, int length, int maxWidth, int maxHeight) {
		if (this.maximumHeight != null && lineHeight > this.maximumHeight.getValue(maxHeight)) {
			lineHeight = this.maximumHeight.getValue(maxHeight);
		}
		
		if (this.isRemoveText && !this.isShowTextInTitle) {
			if (this.focusedLabel == null && this.focusedStyle != null) {
				this.focusedLabel = new StringItem(null, "T");
				this.focusedLabel.setStyle( this.focusedStyle );
			}
			if (this.focusedItem != null) {
				if (this.focusedLabel.getText() == null) {
					this.focusedLabel.setText("T");
				}	
				lineHeight -= this.focusedLabel.getItemHeight(lineWidth, lineWidth, maxHeight);
			}
		}
		this.referenceXCenterPositions = new int[length];
		this.referenceYCenterPositions = new int[length];
		this.referenceXCenterPositions[this.focusedIndex] = lineWidth >> 1;
		this.referenceFocusedIndex = this.focusedIndex;

		
		
		if (this.focusedStyle != null && this.focusedItem != null) {
			UiAccess.focus(this.focusedItem, this.focusedDirection, this.focusedStyle );
			this.focusedWidth = this.focusedItem.getItemWidth( lineWidth, lineWidth, maxHeight );
			this.focusedItem.relativeX = (lineWidth - this.focusedWidth) >> 1;
		} else if (this.focusedWidth == 0) {
			this.focusedWidth = maxWidth;
		}
		if (this.focusedItem != null) {
			if (this.isFocusedAtBottom) {
				this.referenceYCenterPositions[this.focusedIndex] = lineHeight - (this.focusedItem.getItemHeight(lineWidth, lineWidth, maxHeight) >> 1);
			} else {
				this.referenceYCenterPositions[this.focusedIndex] = (this.focusedItem.getItemHeight(lineWidth, lineWidth, maxHeight) >> 1);
			}
		}

		
		int availWidth = lineWidth; // available width for one side
		int availHeight = lineHeight; // - maxHeight; 
		
//		System.out.println("available=" + availableWidth + ", lineWidth=" + lineWidth + ", completeWidth=" + completeWidth + ", maxItemWidth=" + maxWidth + ", paddingHorizontal=" + this.paddingHorizontal);
		int halfLength = (length - 1) >> 1;
		int availableWidthPerItem  = (availWidth  << 8) / (halfLength + 1);
		int availableHeightPerItem = (availHeight << 8) / (length -1);
		// process items on the left side:
		int index = this.focusedIndex - 1;
		int processed = 0;
		int x = (lineWidth - maxWidth)  >> 1;
		int y = lineHeight - (maxHeight >> 1);
		boolean isDirectionSwitched = false;
		while (processed < halfLength ) {
			if (index < 0) {
				index = length - 1;
			}
			x -= ((availableWidthPerItem >> 8) * getScaleFactor(processed, length)) / 100;
			if ( (x <= (maxWidth>>1) ) || (!isDirectionSwitched && (processed + 1 >= (halfLength >> 1)))  ) {
				x = maxWidth >> 1;
				availableWidthPerItem = -availableWidthPerItem;
				isDirectionSwitched = true;
			}
			
			this.referenceXCenterPositions[index] = x;
			y -= (availableHeightPerItem >> 8);
			this.referenceYCenterPositions[index] = y;
//			System.out.println("index=" + index + ", x=" + x + ", y=" + y + ", delta(x)=" + (((availableWidthPerItem >> 8) * getScaleFactor(processed, length)) / 100));
			index--;
			processed++;
		}
		// process items on the right side:
		index = this.focusedIndex + 1;
		processed = 0;
		halfLength = length >> 1;
		x = (lineWidth + maxWidth)  >> 1;
		y = lineHeight - (maxHeight >> 1);
		if (availableWidthPerItem > 0) {
			availableWidthPerItem = -availableWidthPerItem;
		}
		isDirectionSwitched = false;
		while (processed < halfLength) {
			if (index >= length) {
				index = 0;
			}
			x -= ((availableWidthPerItem >> 8) * getScaleFactor(processed, length)) / 100;
			
			if ( (x >= lineWidth - (maxWidth>>1) ) || (!isDirectionSwitched && (processed + 1 >= (halfLength >> 1)))  ) {
				x = lineWidth - (maxWidth>>1);
				availableWidthPerItem = -availableWidthPerItem;
				isDirectionSwitched = true;
			}
			this.referenceXCenterPositions[index] = x;
			y -= (availableHeightPerItem >> 8);
			this.referenceYCenterPositions[index] = y;
//			System.out.println("index=" + index + ", x=" + x + ", y=" + y + ", delta(x)=" + (((availableWidthPerItem >> 8) * getScaleFactor(processed, length)) / 100));
			index++;
			processed++;
		}
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.containerviews.FishEyeContainerView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight) {
		super.initContent(parentContainerItem, firstLineWidth, availWidth, availHeight);
		int lineHeight = availHeight;
		if (this.maximumHeight != null && lineHeight > this.maximumHeight.getValue(availHeight)) {
			lineHeight = this.maximumHeight.getValue(availHeight);
		}
		this.contentHeight = lineHeight;
		this.contentWidth = availWidth;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.containerviews.FishEyeContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#ifdef polish.css.max-height
			this.maximumHeight = (Dimension)style.getObjectProperty( "max-height");
		//#endif

	}

	


//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.containerviews.FishEyeContainerView#paintItem(de.enough.polish.ui.Item, int, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
//	 */
//	protected void paintItem(Item item, int index, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
//		// quick'n'dirty workaround for wrong vertical positioning 
//		super.paintItem(item, index, x, y - 10, leftBorder, rightBorder, clipX, clipY,
//				clipWidth, clipHeight, g);
//	}
	
	

	
	
	

}
