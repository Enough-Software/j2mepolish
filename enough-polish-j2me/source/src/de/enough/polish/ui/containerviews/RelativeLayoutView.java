//#condition polish.usePolishGui
/*
 * @(#)MIDP2LayoutView.java
 * Created on 6/02/2005
 * Copyright 2005 by Majitek International Pte. Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek International Pte Ltd.
 * Use is subject to license terms.
 * 
 * 
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
 * 
 */

package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ArrayList;
/**
 * <p>Layouts and positions items relatively to it's parent items.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RelativeLayoutView extends ContainerView {
        static private final int LAYOUT_HORIZONTAL = Item.LAYOUT_LEFT
                        | Item.LAYOUT_CENTER | Item.LAYOUT_RIGHT;

        static private final int LAYOUT_VERTICAL = Item.LAYOUT_TOP
                        | Item.LAYOUT_VCENTER | Item.LAYOUT_BOTTOM;

        private ArrayList allRows;
        private ArrayList currentRow;
        private int rowWidth;
        private int rowHeight;
        private int horizontalOffset = -1;

		private int currentContentHeight;

        /**
         * Constructs an instance of <code>RelativeLayoutView</code>.
         */
        public RelativeLayoutView() {
        	// just creating a default instance
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container,int, int)
         */
        protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) 
        {
        	Container parContainer = (Container) parent;
        	this.parentContainer = parContainer;
            Item[] myItems = parContainer.getItems();
            	//#if polish.Container.allowCycling != false
                	this.allowCycling = parContainer.allowCycling;
                //#endif
                this.contentHeight = this.contentWidth = this.rowWidth = this.rowHeight = 0;
                this.currentRow = new ArrayList();
                this.allRows = new ArrayList();

                boolean hasFocusableItem = false;
                this.currentContentHeight = 0;
                for (int i = 0; i < myItems.length; i++) {
                    Item item = myItems[i];
                    if (item.appearanceMode != Item.PLAIN) {
                            hasFocusableItem = true;
                    }
                    appendItemToRow(i, item, firstLineWidth, availWidth, availHeight);
                }
                // Make the remaining items a final line
                rowBreak(availWidth, availHeight,  0 );
                if (!hasFocusableItem) {
                	this.appearanceMode = Item.PLAIN;
                } else {
                    this.appearanceMode = Item.INTERACTIVE;
                }
        }

        void appendItemToRow(int index, Item item, int firstLineWidth, int availWidth, int availHeight) {
	        	int itemLayout = item.getLayout();
	        	boolean isExpand = (itemLayout & Item.LAYOUT_EXPAND) == Item.LAYOUT_EXPAND;
	        	if (isExpand) {
	        		item.setLayout( itemLayout ^ Item.LAYOUT_EXPAND );
	        	}
                if (this.focusFirstElement && (item.appearanceMode != Item.PLAIN)) {
                        focusItem(index, item);
                        this.focusFirstElement = false;
                }
                int width = item.getItemWidth(firstLineWidth, availWidth, availHeight);
                int height = item.getItemHeight(firstLineWidth, availWidth, availHeight);
                if (isExpand) {
                	item.setLayout(itemLayout);
                }
                

                if ((Item.LAYOUT_NEWLINE_BEFORE == (itemLayout & Item.LAYOUT_NEWLINE_BEFORE) || isExpand )
                                || (this.rowWidth + this.paddingHorizontal + width > availWidth)) 
                {
                        // Break if the NEWLINE_BEFORE is specified or not enough
                        // room in the current row
                        rowBreak(availWidth, availHeight, itemLayout);
                }

                this.rowWidth += width;
                if (this.currentRow.size() == 0) {
                    this.rowHeight = height;
                } else {
                	if (this.rowHeight < height) {
                		this.rowHeight = height;
                    }
                    this.rowWidth += this.paddingHorizontal;
                }

//                RowItem rowItem = new RowItem();
//                rowItem.width = width;
//                rowItem.height = height;
//                rowItem.item = item;
                this.currentRow.add(item);

                if (Item.LAYOUT_NEWLINE_AFTER == (itemLayout & Item.LAYOUT_NEWLINE_AFTER)) {
                        rowBreak(availWidth, availHeight, itemLayout);
                }
        }

        private void rowBreak(int lineWidth, int availHeight, int itemLayout) {
                if (this.currentRow.size() == 0) {
                        return; // Current row is empty
                }
                /**
                 * Horizontal Layout Starts Here!
                 */
                // Take away all the horizontal paddings
                int remainingWidth = lineWidth
                                - ((this.currentRow.size() - 1) * this.paddingHorizontal);
//                RowItem[] requiredExpanded = null;
                //Item[] requiredExpanded = null;
                int requiredExpandedIndex = 0;
                int top = this.currentContentHeight;
                int bottom = top + this.rowHeight;
                this.currentContentHeight += this.rowHeight + this.paddingVertical;
                int currentWidth = 0;
                for (int i = 0; i < this.currentRow.size(); i++) {
//                    RowItem rowItem = (RowItem) this.currentRow.get(i);
                    Item rowItem = (Item) this.currentRow.get(i);
                    rowItem.relativeY = top;
                    rowItem.relativeX = currentWidth;
                    if (Item.LAYOUT_EXPAND == (itemLayout & Item.LAYOUT_EXPAND)) {
                    	rowItem.getItemWidth( lineWidth - currentWidth, lineWidth - currentWidth, availHeight );
//                        if (requiredExpanded == null) {
////                            requiredExpanded = new RowItem[this.currentRow.size() - i];
//                        	requiredExpanded = new Item[this.currentRow.size() - i];
//                        }
//                        requiredExpanded[requiredExpandedIndex++] = rowItem;
                    	
                    }
                    currentWidth += rowItem.itemWidth;
                    remainingWidth -= rowItem.itemWidth;
                }
                // Distribute the remaining width to the items that require expanding
//                if (requiredExpanded != null) {
//                    int expansion = remainingWidth / requiredExpandedIndex;
//                    remainingWidth = remainingWidth % requiredExpandedIndex;
//                    for (int i = 0; i < requiredExpandedIndex; i++) {
////                        RowItem rowItem = requiredExpanded[i];
//                    	Item rowItem = requiredExpanded[i];
//                        rowItem.width += expansion;
//                        if (i == 0) {
//                            // The first item get all the rounding
//                            rowItem.width += remainingWidth;
//                        }
//                    }
//                }

                // Horizontal Positioning determined by the first item in a row
//                RowItem rowItem = (RowItem) this.currentRow.get(0);
                Item rowItem = (Item) this.currentRow.get(0);
                int rowHorizontalLayout = (itemLayout & LAYOUT_HORIZONTAL);
//                if (requiredExpanded != null) {
//                	rowHorizontalLayout = Item.LAYOUT_LEFT;
//                }

                int x = 0;
                switch (rowHorizontalLayout) {
                    case Item.LAYOUT_CENTER :
                        x = (remainingWidth >> 1);
                        break;

                    case Item.LAYOUT_RIGHT :
                        x = remainingWidth;
                        break;
                }

                for (int i = 0; i < this.currentRow.size(); i++) {
//	                rowItem = (RowItem) this.currentRow.get(i);
//	                rowItem.x = x;
//	                x += rowItem.width + this.paddingHorizontal; // Next Item

	                /**
	                 * Vertical Layout starts here
	                 */
//	                int layout = rowItem.item.getLayout();
//	                rowItem.y = this.contentHeight;
//	                if (Item.LAYOUT_VEXPAND == (layout & Item.LAYOUT_VEXPAND)) {
//                        // Vertical expansion is required, ignore all other
//                        rowItem.height = this.rowHeight;
//	                } else {
//                        layout = (layout & LAYOUT_VERTICAL);
//                        switch (layout) {
//	                        case Item.LAYOUT_VCENTER :
//	                            rowItem.y += ((this.rowHeight - rowItem.height) >> 1);
//	                            break;
//                            case Item.LAYOUT_BOTTOM :
//                                rowItem.y += this.rowHeight - rowItem.height;
//                                break;
//                        }
//	                }
                	rowItem = (Item) this.currentRow.get(i);
                	rowItem.relativeX = x;
                	x += rowItem.itemWidth + this.paddingHorizontal; // Next Item

                	if (Item.LAYOUT_VEXPAND == (itemLayout & Item.LAYOUT_VEXPAND)) {
		                rowItem.relativeY = this.contentHeight;
		                if (Item.LAYOUT_VEXPAND == (itemLayout & Item.LAYOUT_VEXPAND)) {
	                        // Vertical expansion is required, ignore all other
	                        //rowItem.height = this.rowHeight;
		                } else {
		                	itemLayout = (itemLayout & LAYOUT_VERTICAL);
	                        switch (itemLayout) {
		                        case Item.LAYOUT_VCENTER :
		                            rowItem.relativeY += ((this.rowHeight - rowItem.itemHeight) >> 1);
		                            break;
	                            case Item.LAYOUT_BOTTOM :
	                                rowItem.relativeY += this.rowHeight - rowItem.itemHeight;
	                                break;
	                        }
		                }
                	}
                }

                if (this.allRows.size() == 0) {
	                // Adding first row
	                this.contentHeight += this.rowHeight;
                } else {
                    this.contentHeight += this.paddingVertical + this.rowHeight;
                }
                if ( this.rowWidth > this.contentWidth ) {
	                this.contentWidth = this.rowWidth;
                }

                // Get ready for next row
                this.allRows.add(this.currentRow);
                this.rowHeight = this.rowWidth = 0;
                this.currentRow = new ArrayList();
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.enough.polish.ui.ContainerView#paintContent(int, int, int, int,
         *      javax.microedition.lcdui.Graphics)
         */
        protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder,
                        Graphics g) 
        {
			int clipX = g.getClipX();
			int clipY = g.getClipY();
			int clipWidth = g.getClipWidth();
			int clipHeight = g.getClipHeight();
                for (int i = 0; i < this.allRows.size(); i++) {
                    ArrayList row = (ArrayList) this.allRows.get(i);
                    for (int j = 0; j < row.size(); j++) {
                        Item rowItem = (Item) row.get(j);
                        int xItem = x + rowItem.relativeX;
                        paintItem(rowItem, i, xItem,  y + rowItem.relativeY, 
                        		Math.max(leftBorder, xItem), Math.min(rightBorder, xItem + rowItem.itemWidth),
                        		clipX, clipY, clipWidth, clipHeight, g);
                    }
                }
        }
        /*
         * (non-Javadoc)
         * 
         * @see de.enough.polish.ui.ContainerView#getNextItem(int, int)
         */
        protected Item getNextItem(int keyCode, int gameAction) {
                if (this.allRows.size() == 0)
                        return null;
                Item[] items = this.parentContainer.getItems();
                if (this.focusedIndex >= items.length) {
                        for(int i=0; i < items.length; i++) {
                                if(items[i].appearanceMode != Item.PLAIN) {
                                        focusItem(i, items[i], gameAction );
                                        return items[i];
                                }
                        }
                }

                // Find out where the current focused item is.
                Item focusedItem = items[this.focusedIndex];
                int rowIndex = 0;
                //int colIndex = 0;
                int xOffset = 0;
                for (int i = 0; i < this.allRows.size(); i++) {
                        ArrayList row = (ArrayList) this.allRows.get(i);
                        for (int j = 0; j < row.size(); j++) {
                            Item rowItem = (Item) row.get(j);
                            if (rowItem == focusedItem) {
                                    rowIndex = i;
                                    //colIndex = j;
                                    xOffset = rowItem.relativeX + (rowItem.itemWidth >> 1);
                                    i = 10000;
                                    break;
                            }
                    }
//                        for (int j = 0; j < row.size(); j++) {
//                                RowItem rowItem = (RowItem) row.get(j);
//                                if (rowItem.item == focusedItem) {
//                                        rowIndex = i;
//                                        //colIndex = j;
//                                        xOffset = rowItem.x + (rowItem.width >> 1);
//                                        i = 10000;
//                                        break;
//                                }
//                        }
                }

                Item item = null;
                if (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) {
                        // Going Up
                        if (this.horizontalOffset == -1) {
                        	this.horizontalOffset = xOffset;
                        }
                        while (rowIndex > 0) {
                                rowIndex--;
                                item = getItemByHorizontalOffset((ArrayList) this.allRows
                                                .get(rowIndex), this.horizontalOffset);
                                if(item != null) break;
                        }
                        if(item == null){
                                // Can't go up any more
                        }

                } else if (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8) {
                        // Going Down
                        if (this.horizontalOffset == -1) {
                        	this.horizontalOffset = xOffset;
                        }
                        
                        while(rowIndex < (this.allRows.size() - 1)) {
                                rowIndex++;
                                item = getItemByHorizontalOffset((ArrayList) this.allRows
                                                .get(rowIndex), this.horizontalOffset);
                                if(item != null) break;
                        } 
                        
                        if(item == null){
                                // Can't go Down any more
                        }

                } else if (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6) {
                        // Going Right
                	this.horizontalOffset = -1; // Reset vertical movement position
                        item = getNextFocusableItem(items, true, 1, false);

                } else if (gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4) {
                        // Going Left
                		this.horizontalOffset = -1; // Reset vertical movement position
                        item = getNextFocusableItem(items, false, 1, false);
                }
                
                // Finally set the focus if it has been found
                if (item != null) {
                        for (int i = 0; i < items.length; i++) {
                                if (items[i] == item) {
                                        focusItem(i, item, gameAction);
                                        break;
                                }
                        }
                }
                else {
                        if (this.focusedIndex >= items.length) {
                                for(int i=0; i < items.length; i++) {
                                        Item focItem = items[i];
										if(focItem.appearanceMode != Item.PLAIN) {
                                                focusItem(i, focItem, gameAction );
                                                return focItem;
                                        }
                                }
                        }
                }
                return item;
        }

        private Item getItemByHorizontalOffset(ArrayList row, int xOffset) {
            Item item = null;
            Item rowItem = null;
            int distance = 60000;
            int itemOffset = 0;
            int itemDistance = 0;
            for (int i = 0; i < row.size(); i++) {
                    rowItem = (Item) row.get(i);
                    if (rowItem.appearanceMode != Item.PLAIN) {
                            itemOffset = rowItem.relativeX + (rowItem.itemWidth >> 1);
                            itemDistance = xOffset - itemOffset;
                            if(itemDistance < 0) {
                            	itemDistance = -itemDistance;
                            }

                            if (itemDistance < distance) {
                                    distance = itemDistance;
                                    item = rowItem;
                            }
                    }
            }
            return item;

//                Item item = null;
//                RowItem rowItem = null;
//                int distance = 60000;
//                int itemOffset = 0;
//                int itemDistance = 0;
//                for (int i = 0; i < row.size(); i++) {
//                        rowItem = (RowItem) row.get(i);
//                        if (rowItem.item.appearanceMode != Item.PLAIN) {
//                                itemOffset = rowItem.x + (rowItem.width >> 1);
//                                itemDistance = xOffset - itemOffset;
//                                if(itemDistance < 0) itemDistance = -itemDistance;
//
//                                if (itemDistance < distance) {
//                                        distance = itemDistance;
//                                        item = rowItem.item;
//                                }
//                        }
//                }
//                return item;
        }
}