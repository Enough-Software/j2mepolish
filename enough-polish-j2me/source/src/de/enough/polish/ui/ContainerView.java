//#condition polish.usePolishGui
/*
 * Created on Oct 27, 2004 at 7:03:40 PM.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.TextUtil;

/**
 * <p>Is responsible for visual representation and interpretation of user-input.</p>
 * <p>Copyright Enough Software 2004 - 2010</p>
 * @author Robert Virkus, robert@enough.de
 */
public class ContainerView 
extends ItemView
{
	//#if polish.css.columns || polish.useTable
		//#define tmp.useTable
		//#ifdef polish.css.columns-width.star
			private int starIndex;
		//#endif
			
	//#endif
	
	protected static final int NO_COLUMNS = 0;
	protected static final int EQUAL_WIDTH_COLUMNS = 1;
	protected static final int NORMAL_WIDTH_COLUMNS = 2;
	protected static final int STATIC_WIDTH_COLUMNS = 3;

	protected int xOffset;
	protected int targetXOffset;
	protected int yOffset;
	protected int focusedIndex = -1;
	/** this field is set automatically, so that subclasses can use it for referencing the parent-container */
	protected transient Container parentContainer;
	/** determines whether any animation of this view should be (re) started at the next possibility. this is set to "true" in each showNotify() method. */
	protected boolean restartAnimation;
	protected boolean focusFirstElement;
	protected int appearanceMode;
	protected transient Item focusedItem;
	
	// table support:
	protected int columnsSetting = NO_COLUMNS;
	protected int numberOfColumns;
	protected Dimension[] columnsWidths;
	protected int[] rowsHeights;
	protected int numberOfRows;
//	/** 
//	 * the number of items - this information is used for rebuilding the table only when it is necessary 
//	 * (number is changed or the columns-width is not static). 
//	 */
//	protected int numberOfItems;
//	/** All items ordered within a table. Some cells can be null when colspan or rowspan CSS attributes are used. */
//	protected Item[][] itemsTable;
	
	protected boolean allowCycling = true;
	
	//#if polish.css.view-type-left-x-offset
		protected int leftXOffset;
	//#endif
	//#if polish.css.view-type-right-x-offset
		protected int rightXOffset;
	//#endif
	//#if polish.css.view-type-top-y-offset
		protected int topYOffset;
	//#endif
	//#if polish.css.view-type-sequential-traversal
		protected boolean isSequentialTraversal;
	//#endif
	//#if polish.css.expand-items
		protected boolean isExpandItems;
	//#endif
	//#if polish.css.align-heights
		protected boolean isAlignHeights;
	//#endif
	/** indicates whether the parent Container is allowed to change the currently focused item 
	 *  when the user traverses around a form and enters the container from different sides 
	 */
	protected boolean allowsAutoTraversal = true;
	protected boolean isHorizontal = true;
	protected boolean isVertical = true;
	/** indicates whether elements in this container view can be selected directly by pointer events */
	protected boolean allowsDirectSelectionByPointerEvent = true;
	private int lastAvailableContentWidth;

	private int scrollDirection;
	private int scrollSpeed;
	private int scrollDamping;
	private long lastAnimationTime;
	//#if polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false
		//#define tmp.dontBounce
	//#endif
	//#if polish.hasPointerEvents
		private long lastPointerPressTime;
		private int lastPointerPressXOffset;
		protected boolean isPointerPressedHandled;
		private int pointerPressedX;
	//#endif


	/**
	 * Creates a new view
	 */
	protected ContainerView() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		// scroll the container:
		int target = this.targetXOffset;
		int current = this.xOffset;
		if (target != current) {
			int speed = (target - current) / 3;
			
			speed += target > current ? 1 : -1;
			current += speed;
			if ( ( speed > 0 && current > target) || (speed < 0 && current < target ) ) {
				current = target;
			}
			int diff = Math.abs( current - this.xOffset);
			this.xOffset = current;
//			if (this.focusedItem != null && this.focusedItem.backgroundYOffset != 0) {
//				this.focusedItem.backgroundYOffset = (this.targetYOffset - this.yOffset);
//			}
			// # debug
			//System.out.println("animate(): adjusting yOffset to " + this.yOffset );
			
			// add repaint region:
			int x, y, width, height;
			Screen scr = getScreen();
			x = this.parentContainer.getAbsoluteX();
			y = this.parentContainer.getAbsoluteY();
			width = this.parentContainer.itemWidth;
			//#if polish.useScrollBar || polish.classes.ScrollBar:defined
				width += scr.getScrollBarWidth();
			//#endif
			height = this.parentContainer.itemHeight;
			repaintRegion.addRegion( x, y, width, height + diff + 1 );
		}
		int speed = this.scrollSpeed;
		if (speed != 0) {
			speed = (speed * (100 - this.scrollDamping)) / 100;
			if (speed <= 0) {
				speed = 0;
			}
			this.scrollSpeed = speed;
			long timeDelta = currentTime - this.lastAnimationTime;
			if (timeDelta > 1000) {
				timeDelta = AnimationThread.ANIMATION_INTERVAL;
			}
			this.lastAnimationTime = currentTime;
			speed = (int) ((speed * timeDelta) / 1000);
			if (speed == 0) {
				this.scrollSpeed = 0;
			}
			int offset = this.xOffset;
			int width = this.contentWidth;
			if (this.scrollDirection == Canvas.LEFT) {
				offset += speed;
				target = offset;
				if (offset > 0) {
					this.scrollSpeed = 0;
					target = 0;
					//#if polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false
						offset = 0;
					//#endif
				}
			} else {
				offset -= speed;
				target = offset;
				if (offset + width < this.availableWidth) { 
					this.scrollSpeed = 0;
					target = this.availableWidth - width;
					//#if polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false
						offset = target;
					//#endif
				}
			}
			this.xOffset = offset;
			this.targetXOffset = target;
			// add repaint region:
			int x, y, height;
			Screen scr = getScreen();
			x = this.parentContainer.getAbsoluteX();
			y = this.parentContainer.getAbsoluteY();
			//#if polish.useScrollBar || polish.classes.ScrollBar:defined
				width += scr.getScrollBarWidth();
			//#endif
			height = this.parentContainer.itemHeight;
			repaintRegion.addRegion( x, y, width, height + 1 );
		}
	}
	
	/**
	 * Sets the horizontal scrolling offset of this item.
	 *  
	 * @param offset either the new offset
	 */
	public void setScrollXOffset( int offset) {
		setScrollXOffset( offset, false );
	}

	/**
	 * Sets the horizontal scrolling offset of this item.
	 *  
	 * @param offset either the new offset
	 * @param smooth scroll to this new offset smooth if allowed
	 * @see #getScrollXOffset()
	 */
	public void setScrollXOffset( int offset, boolean smooth) {
		//#debug
		System.out.println("Setting scrollXOffset from " + this.xOffset + "/" + this.targetXOffset + " to " + offset + ", smooth=" + smooth +  " for " + this);
		//try { throw new RuntimeException("for xOffset=" + offset); } catch (Exception e) { e.printStackTrace(); }
		if (!smooth  
		//#ifdef polish.css.scroll-mode
			|| !this.parentContainer.scrollSmooth
		//#endif
		) {
			this.xOffset = offset;			
		}
		this.targetXOffset = offset;
		this.scrollSpeed = 0;
	}

	/**
	 * Retrieves the current horizontal scroll offset
	 * @return the offset in pixel, when scrolled to the left side this offset is negative
	 */
	public int getScrollXOffset() {
		return this.xOffset;
	}

	/**
	 * Retrieves the target horizontal scroll offset
	 * @return the offset in pixel, when scrolling to the left side this offset is negative
	 */
	public int getScrollTargetXOffset() {
		return this.targetXOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#addFullRepaintRegion(de.enough.polish.ui.Item, de.enough.polish.ui.ClippingRegion)
	 */
	protected void addFullRepaintRegion( Item item, ClippingRegion repaintRegion ) {
		repaintRegion.addRegion( item.getAbsoluteX() - this.xOffset, 
				item.getAbsoluteY(), 
				item.itemWidth,
				item.itemHeight 
		);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initMargin(de.enough.polish.ui.Style, int)
	 */
	protected void initMargin(Style style, int availWidth) {
		this.parentContainer.initMargin(style, availWidth);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initPadding(de.enough.polish.ui.Style, int)
	 */
	protected void initPadding(Style style, int availWidth) {
		this.parentContainer.initPadding(style, availWidth);
	}

	/**
	 * Initializes this container view. 
	 * The implementation needs to calculate and set the contentWidth and 
	 * contentHeight fields. 
	 * The style of the focused item has already been set.
	 * When the contentWidth will be larger than the specified availWidth, the container view allows to scroll horizontally automatically using pointer events.
	 * 
	 * @param firstLineWidth the maximum width of the first line 
	 * @param availWidth the maximum width of any following lines
	 * @param parentContainerItem the Container which uses this view, use parent.getItems() for retrieving all items. 
	 *  
	 * @see #contentWidth
	 * @see #contentHeight
	 */
	protected void initContent( Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight ) {
		Container parent = (Container) parentContainerItem;		
		//#debug
		System.out.println( this + ": ContainerView: intialising content for " + this + " with vertical-padding " + this.paddingVertical + ", focusedIndex=" + this.focusedIndex + ", parent.focusedIndex=" + parent.getFocusedIndex() );
		this.paddingHorizontal = parentContainerItem.paddingHorizontal;
		this.paddingVertical = parentContainerItem.paddingVertical;
		this.focusedIndex = parent.getFocusedIndex();
		this.focusedItem = parent.getFocusedItem();
		boolean reinitInAnyCase = (availWidth != this.lastAvailableContentWidth);
		this.lastAvailableContentWidth = availWidth;
		//#if polish.Container.allowCycling != false
			this.allowCycling = parent.allowCycling;
			Item ancestor = parent.parent;
			while (this.allowCycling && ancestor != null) {
				if ( (ancestor instanceof Container)  && ((Container)ancestor).getNumberOfInteractiveItems()>1 ) {
					this.allowCycling = false;
					break;
				}
				ancestor = ancestor.parent;
			}
		//#endif
		//#if polish.css.view-type-left-x-offset
			availWidth -= this.leftXOffset;
		//#endif
		//#if polish.css.view-type-right-x-offset
			availWidth -= this.rightXOffset;
		//#endif

		
		this.parentContainer = parent;
		Item[] myItems = parent.getItems();
		int myItemsLength = myItems.length;
		//boolean hasCenterOrRightItems = false;
		boolean hasVerticalExpandItems = false;
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			if (item.isLayoutVerticalExpand()) {
				hasVerticalExpandItems = true;
				break;
			}
		}
		int numberOfVerticalExpandItems = 0;
		Item lastVerticalExpandItem = null;
		int lastVerticalExpandItemIndex = 0;


		//#ifdef tmp.useTable
			if (this.columnsSetting == NO_COLUMNS || myItemsLength <= 1 || this.numberOfColumns <= 1) {
		//#endif
			this.isHorizontal = false;
			this.isVertical = true;
			// look at the layout of the parentContainer, since the SHRINK layout can be set outside of the setStyle method as well:
			boolean isLayoutShrink = (this.parentContainer.layout & Item.LAYOUT_SHRINK) == Item.LAYOUT_SHRINK;
			int myContentWidth = 0;
			int myContentHeight = 0;
			boolean hasFocusableItem = false;
			
			for (int i = 0; i < myItemsLength; i++) {
				Item item = myItems[i];
				if (hasVerticalExpandItems) {
					// re-initialize items when we have vertical-expand items, so that relativeY and itemHeight is correctly calculated 
					// with each run:
					item.setInitialized(false);
				}
				//System.out.println("initalising " + item.getClass().getName() + ":" + i);
				int width = item.itemWidth;
				if (reinitInAnyCase || !item.isInitialized) {
					width = item.getItemWidth( firstLineWidth, availWidth, availHeight );
				}
				int height = item.itemHeight;
				if (item.isLayoutVerticalExpand()) {
					numberOfVerticalExpandItems++;
					lastVerticalExpandItem = item;
					lastVerticalExpandItemIndex = i;
				} 
				if (item.appearanceMode != Item.PLAIN) {
					hasFocusableItem = true;
				}
				if (isLayoutShrink && i == this.focusedIndex) {
					width = 0;
				}
				if (width > myContentWidth) {
					myContentWidth = width; 
				}
				item.relativeY = myContentHeight;
				if (item.isLayoutExpand) {
					item.relativeX = 0;
				} else if (item.isLayoutCenter) {
					item.relativeX = (availWidth - width) / 2;
				} else if (item.isLayoutRight) {
					item.relativeX = (availWidth - width);
				} else {
					item.relativeX = 0;
				}				
				myContentHeight += height + (height != 0 ? this.paddingVertical : 0);
			}
			if (hasFocusableItem) {
				this.appearanceMode = Item.INTERACTIVE;
				if (isLayoutShrink && this.focusedItem != null) {
					Item item = this.focusedItem;
					//System.out.println("container has shrinking layout and contains focused item " + item + ", minWidth=" + parent.minimumWidth);
					item.setInitialized(false);
					boolean doExpand = item.isLayoutExpand;
					int width;
					if (doExpand) {
						item.isLayoutExpand = false;
						width = item.getItemWidth( availWidth, availWidth, availHeight );
						item.setInitialized(false);
						item.isLayoutExpand = true;
					} else {
						width = item.itemWidth;
					}
					if (width > myContentWidth) {
						myContentWidth = width;
					}
					if ( parent.minimumWidth != null && myContentWidth < parent.minimumWidth.getValue(firstLineWidth) ) {
						myContentWidth = parent.minimumWidth.getValue(firstLineWidth);
					}
					if (doExpand) {
						item.init(myContentWidth, myContentWidth, availHeight);
					}
				}
			} else {
				this.appearanceMode = Item.PLAIN;
			}
			if (numberOfVerticalExpandItems > 0 && myContentHeight < availHeight) {
				int diff = availHeight - myContentHeight;
				if (numberOfVerticalExpandItems == 1) {
					// this is a simple case:
					lastVerticalExpandItem.setItemHeight( lastVerticalExpandItem.itemHeight + diff );
					for (int i = lastVerticalExpandItemIndex+1; i < myItems.length; i++)
					{
						Item item = myItems[i];
						item.relativeY += diff;
					}
				} else {
					// okay, there are several items that would like to be expanded vertically:
//					System.out.println("having " + numberOfVerticalExpandItems + ", diff: " + diff + "=>" + (diff / numberOfVerticalExpandItems) + "=>" + ((diff / numberOfVerticalExpandItems) * numberOfVerticalExpandItems));
					diff = diff / numberOfVerticalExpandItems;
					int relYAdjust = 0;
					for (int i = 0; i < myItems.length; i++)
					{
						Item item = myItems[i];
//						System.out.println("changing relativeY from " + item.relativeY + " to " + (item.relativeY + relYAdjust) + ", relYAdjust=" + relYAdjust + " for " + item );
						item.relativeY +=  relYAdjust;
						if (item.isLayoutVerticalExpand()) {
//							System.out.println("changing itemHeight from " + item.itemHeight + " to " + (item.itemHeight + diff) + " for " + item);
							item.setItemHeight(item.itemHeight + diff );
							relYAdjust += diff;
						}
					}
				}
				myContentHeight = availHeight;
			}
			
			//#if polish.css.view-type-top-y-offset
				this.contentHeight = myContentHeight + this.topYOffset;
			//#else
				this.contentHeight = myContentHeight;
			//#endif
			this.contentWidth = myContentWidth;
			//#if polish.css.expand-items
				if (this.isExpandItems) {
					if (parent.minimumWidth != null && parent.minimumWidth.getValue(firstLineWidth) > myContentWidth) {
						myContentWidth = parent.minimumWidth.getValue(firstLineWidth) - (parent.getBorderWidthLeft() + parent.getBorderWidthRight() + parent.marginLeft + parent.paddingLeft + parent.marginRight + parent.paddingRight);
					}
					for (int i = 0; i < myItemsLength; i++)
					{
						Item item = myItems[i];
						if (!item.isLayoutExpand && item.itemWidth < myContentWidth) {
							item.isLayoutExpand = true;
							item.init(myContentWidth, myContentWidth, availHeight);
							item.isLayoutExpand = false;
						}
					}
				}
			//#endif
		//#ifdef tmp.useTable
				return;
			}
		//#endif
		
			
		/*
		 * Init items in a table layout with columns and rows.
		 * This only allows simple layouts, complex layouts with colspan AND rowspan is handled by the TableItem.
		 */
			
		//#ifdef tmp.useTable
			this.isHorizontal = true;
			// columns are used
			boolean isNormalWidthColumns = (this.columnsSetting == NORMAL_WIDTH_COLUMNS);
			//#ifdef polish.css.columns-width.star
				if (this.columnsSetting == STATIC_WIDTH_COLUMNS) {
					if (this.starIndex != -1) {
						int combinedWidth = 0;
						for (int i = 0; i < this.numberOfColumns; i++) {
							combinedWidth += this.columnsWidths[i].getValue( availWidth );
						}
						this.columnsWidths[this.starIndex].setValue( availWidth - combinedWidth, false );
						this.starIndex = -1;
						//#debug
						System.out.println("initContent: width of star column=" + (availWidth - combinedWidth) );
					}
				} 
				//# else {
			//#else
				if (this.columnsSetting != STATIC_WIDTH_COLUMNS || this.columnsWidths == null) {
			//#endif
				int availableColumnWidth;
				if (isNormalWidthColumns) {
					// this.columnsSetting == NORMAL_WIDTH_COLUMNS
					// each column should use as much space as it can use
					// without busting the other columns
					// (the calculation will be finished below)
					availableColumnWidth = availWidth - ((this.numberOfColumns -1) * this.paddingHorizontal);
				} else {
					// each column should take an equal share
					availableColumnWidth = 
						(availWidth - ((this.numberOfColumns -1) * this.paddingHorizontal))
						/ this.numberOfColumns;
				}
				//System.out.println("available column width: " + availableColumnWidth );
				this.columnsWidths = new Dimension[ this.numberOfColumns ];
				for (int i = 0; i < this.numberOfColumns; i++) {
					this.columnsWidths[i] = new Dimension( availableColumnWidth, false );
				}
			}
				
			//#if polish.css.colspan
				ArrayList rowHeightsList = new ArrayList( (myItemsLength / this.numberOfColumns) + 1 );
			//#else
				this.numberOfRows = (myItemsLength / this.numberOfColumns);
				if (myItemsLength % this.numberOfColumns != 0) {
					this.numberOfRows += 1;
				}
				this.rowsHeights = new int[ this.numberOfRows ];
			//#endif
			int maxRowHeight = 0;
			int columnIndex = 0;
			int rowIndex = 0;
			int[] maxColumnWidths = null;
			if (isNormalWidthColumns) {
				maxColumnWidths = new int[ this.numberOfColumns ];
			}
			int maxWidth = 0; // important for "equal" columns-width
			int myContentHeight = 0;
			boolean hasFocusableItem = false;
			int columnX = 0; // the horizontal position of the current column relative to the content's left corner (starting a 0)
			//System.out.println("starting init of " + myItems.length + " container items.");
			int rowStartIndex = 0;
			for (int i=0; i< myItemsLength; i++) {
				Item item = myItems[i];
				Dimension colWidthDim = this.columnsWidths[columnIndex];
				if (colWidthDim == null) {
					colWidthDim = new Dimension(availWidth/this.numberOfColumns);
					this.columnsWidths[columnIndex] = colWidthDim;
				}
				int availColWidth = colWidthDim.getValue( availWidth );
				//#if polish.css.colspan
					int itemColSpan = item.colSpan;
					if (!item.isInitialized() && item.style != null) {
						Integer colSpanInt = item.style.getIntProperty("colspan");
						if ( colSpanInt != null ) {
							itemColSpan = colSpanInt.intValue();
							//System.out.println("colspan of item " + i + "/" + item + ", column " + columnIndex + ": " + itemColSpan);
						}
					}
					//System.out.println("ContainerView.init(): colspan of item " + i + "=" + itemColSpan);
					if (itemColSpan > 1) {
						// okay, this item stretched beyond one column,
						// so let's calculate the correct available cell width
						// and switch to the right column index:
						int maxColSpan = this.numberOfColumns - columnIndex;
						if (itemColSpan > maxColSpan) {
							//#debug error
							System.err.println("Warning: colspan " + itemColSpan + " is invalid at column " + columnIndex + " of a table with " + this.numberOfColumns + " columns, now using maximum possible value " + maxColSpan + ".");
							itemColSpan = maxColSpan;
						}
						
						// adjust the available width only when
						// each column has an equal size or when
						// the column widths are static,
						// otherwise the complete row width
						// is available anyhow:
						if (!isNormalWidthColumns) {
							if (itemColSpan == maxColSpan) {
								availColWidth = 0;
								for (int j = 0; j < columnIndex; j++) {
									availColWidth += this.paddingHorizontal + this.columnsWidths[j].getValue( availWidth );
								}
								availColWidth = availWidth - availColWidth;
							} else {
								for (int j = columnIndex + 1; j < columnIndex + itemColSpan; j++) {
									availColWidth += this.paddingHorizontal + this.columnsWidths[j].getValue( availWidth );
								}					
							}
							//System.out.println("ContainerView.init(): adjusted availableWidth for item " + i + ": " + availableWidth);
						}
					}
				//#endif
//				System.out.println( i + ": available with: " + availableWidth + ", lineWidth=" + lineWidth );
//				System.out.println("itemWidth=" + item.getItemWidth( availableWidth, availableWidth ));
				//#if polish.css.align-heights
					if (this.isAlignHeights) {
						// we need to re-initialize every item every time, because otherwise
						// item heights could only grow and never shrink:
						item.setInitialized(false);
					}
				//#endif
					
				int width = item.itemWidth;
				if (reinitInAnyCase || width > availColWidth || !item.isInitialized) {
					width = item.getItemWidth( availColWidth, availColWidth, availHeight );
				}
				int height = item.itemHeight; //getItemHeight( availColWidth, availColWidth, availHeight ) is not needed as it is initialized above if required;
				if (item.appearanceMode != Item.PLAIN) {
					hasFocusableItem = true;
				}
								
				if (height > maxRowHeight) {
					maxRowHeight = height;
				}
				//#if polish.css.colspan
					if (itemColSpan == 1) {
				//#endif
						if (isNormalWidthColumns && width > maxColumnWidths[columnIndex ]) {
							maxColumnWidths[ columnIndex ] = width;
						}
						if (width > maxWidth ) {
							maxWidth = width;
						}
				//#if polish.css.colspan
					}
				//#endif
				//#if polish.css.colspan
					if (item.colSpan != itemColSpan) {
						//#debug
						System.out.println("initializing new colspan of item " + i + "/" + item + ", column " + columnIndex + ": " + itemColSpan);
						item.colSpan = itemColSpan;
						item.setInitialized(false);
					}
					columnIndex += itemColSpan;
				//#else
					columnIndex++;
				//#endif
				item.relativeX = columnX; // when equal or normal column widths are used, this is below changed again, since the widths are just calculated right now.
				if (width < availColWidth) {
					if (item.isLayoutCenter) {
						item.relativeX += (availColWidth - width) / 2;
					} else if (item.isLayoutRight) {
						item.relativeX += (availColWidth - width);
					}
				}

				item.relativeY = myContentHeight;
				if ((columnIndex == this.numberOfColumns) || (i == myItemsLength - 1)) {
					if (item.isLayoutRight && isLayoutExpand()) {
						// position item to the far right side:
						item.relativeX = availWidth - width + 1;
					}
					//System.out.println("starting new row: rowIndex=" + rowIndex + "  numberOfRows: " + numberOfRows);
					columnIndex = 0;
					columnX = 0;
					//#if polish.css.colspan
						//System.out.println("ContainerView.init(): adding new row " + rowIndex + " with height " + maxRowHeight + ", contentHeight=" + myContentHeight + ", item " + i);
						rowHeightsList.add( new Integer( maxRowHeight ) );
					//#else
						this.rowsHeights[rowIndex] = maxRowHeight;						
					//#endif
					myContentHeight += maxRowHeight + (maxRowHeight != 0 ? this.paddingVertical : 0);
					//System.out.println("end of line(1): myContentHeight=" + myContentHeight +  " for " + this);
					rowIndex++;
					// adjust vertical positioning:
					if (maxRowHeight != 0) {
						for (int j=rowStartIndex; j<=i; j++) {
							item = myItems[j];
							if (item.itemHeight < maxRowHeight) {
								//#if polish.css.align-heights
									if (this.isAlignHeights && !item.isLayoutVerticalShrink()) {
										item.setItemHeight( maxRowHeight );
									} else
								//#endif
								if (item.isLayoutVerticalCenter()) {
									item.relativeY += (maxRowHeight - item.itemHeight) >> 1;
								} else if (item.isLayoutBottom()) {
									item.relativeY += (maxRowHeight - item.itemHeight);
								}
							}
						}
					}
					maxRowHeight = 0;
					rowStartIndex = i + 1;
				} else {
					int columnXCent = 0;
					for (int j = 0; j < columnIndex; j++) {
						columnXCent += this.columnsWidths[j].getValue( availWidth * 100 ) + this.paddingHorizontal;
					}
					columnX = columnXCent / 100;
					if (columnXCent % 100 >= 50) {
						columnX++;
					}
				}
			} // for each item
			if (hasFocusableItem) {
				this.appearanceMode = Item.INTERACTIVE;
			} else {
				this.appearanceMode = Item.PLAIN;
			}
			if (columnIndex != 0) {
				// last row is not completely filled.
				//#if polish.css.colspan
					rowHeightsList.add( new Integer( maxRowHeight ) );
				//#else
					this.rowsHeights[rowIndex] = maxRowHeight;
				//#endif
				myContentHeight += maxRowHeight;
				//System.out.println("end of last line(1): myContentHeight=" + myContentHeight + ", columnIndex=" + columnIndex +  " for " + this);
			}
			//#if polish.css.colspan
				this.numberOfRows = rowHeightsList.size();
				//System.out.println("ContainerView.init(): numberOfRows=" + this.numberOfRows + ", rowIndex=" + rowIndex);
				this.rowsHeights = new int[ this.numberOfRows ];
				
				for (int i = 0; i < this.numberOfRows; i++) {
					this.rowsHeights[i] = ((Integer) rowHeightsList.get(i)).intValue();
				}
			//#endif
			// now save the worked out dimensions:
			columnX = 0;
			if (isNormalWidthColumns) {
				// Each column should use up as much space as 
				// needed in the "normal" columns-width mode.
				// Each column which takes less than available 
				// the available-row-width / number-of-columns
				// can keep, but the others might need to be adjusted,
				// in case the complete width of the table is wider
				// than the allowed width.
				
				int availableRowWidth = availWidth - ((this.numberOfColumns -1) * this.paddingHorizontal);
				int availableColumnWidth = availableRowWidth / this.numberOfColumns;
				int usedUpWidth = 0;
				int leftColumns = this.numberOfColumns;
				int completeWidth = 0;
				for (int i = 0; i < maxColumnWidths.length; i++) {
					int maxColumnWidth = maxColumnWidths[i];
					if (maxColumnWidth <= availableColumnWidth) {
						usedUpWidth += maxColumnWidth;
						leftColumns--;
					}
					completeWidth += maxColumnWidth;
				}
				if (completeWidth <= availableRowWidth) {
					// workaround for cases in which there are only items with a colspan of the complete row,
					// or when a single column does not occupy any items with colspan=1.
					//#if polish.css.colspan
						int numberOfZeroWidthColumns = 0;
						for (int i = 0; i < maxColumnWidths.length; i++)
						{
							if (maxColumnWidths[i] == 0) {
								numberOfZeroWidthColumns++;
							}
						}
						if (numberOfZeroWidthColumns > 0) {
							int remainingWidth = availableRowWidth - completeWidth;
							for (int i = 0; i < maxColumnWidths.length; i++)
							{
								if (maxColumnWidths[i] == 0) {
									int colWidth;
									if (numberOfZeroWidthColumns == 1) {
										// last column receives the remaining space:
										colWidth = remainingWidth;
									} else {
										colWidth = remainingWidth / numberOfZeroWidthColumns;
									}
									maxColumnWidths[i] = colWidth;
									numberOfZeroWidthColumns--;
									remainingWidth -= colWidth;
								}
							}
						}
					//#endif
					// okay, the table is now fine just how it is
					for (int i=0; i<this.numberOfColumns; i++) {
						this.columnsWidths[i] = new Dimension( maxColumnWidths[i], false );
					}
				} else {
					//System.out.println("container-view: too wide");
					// okay, some columns need to be adjusted:
					// re-initialize the table:
					int leftAvailableColumnWidth = (availableRowWidth - usedUpWidth) / leftColumns;
					int[] newMaxColumnWidths = new int[ this.numberOfColumns ];
					myContentHeight = 0;
					columnIndex = 0;
					rowIndex = 0;
					maxRowHeight = 0;
					maxWidth = 0;
					rowStartIndex = 0;
					//System.out.println("starting init of " + myItems.length + " container items.");
					for (int i = 0; i < myItemsLength; i++) {
						Item item = myItems[i];
						int width = item.itemWidth;
						int height = item.itemHeight;
						int maxColumnWidth = maxColumnWidths[ columnIndex ];
						//#if polish.css.colspan
							if (item.colSpan == 1) {
						//#endif
								if ( maxColumnWidth <= availableColumnWidth) {
									newMaxColumnWidths[ columnIndex ] = maxColumnWidth;
								} else {
									// re-initialise this item,
									// if it is wider than the left-available-column-width
									if ( width > leftAvailableColumnWidth ) {
										width = item.getItemWidth( leftAvailableColumnWidth, leftAvailableColumnWidth, availHeight );
										height = item.itemHeight;
									}
									if (width > newMaxColumnWidths[ columnIndex ]) {
										newMaxColumnWidths[ columnIndex ] = width;
									}
								}
						//#if polish.css.colspan
							}
							columnIndex += item.colSpan;
						//#else
							columnIndex++;
						//#endif
						if (height > maxRowHeight) {
							maxRowHeight = height;
						}
						item.relativeY = myContentHeight;
						//System.out.println( i + ": yTopPos=" + item.yTopPos );
						if ((columnIndex == this.numberOfColumns) || (i == myItemsLength-1)) {
							//System.out.println("starting new row: rowIndex=" + rowIndex + "  numberOfRows: " + numberOfRows);
							columnIndex = 0;
							this.rowsHeights[rowIndex] = maxRowHeight;
							myContentHeight += maxRowHeight + (maxRowHeight != 0 ? this.paddingVertical : 0);
							//System.out.println("end of line(2): myContentHeight=" + myContentHeight +  " for " + this);
							// adjust vertical positioning:
							for (int j=rowStartIndex; j<=i; j++) {
								item = myItems[j];
								if (item.itemHeight < maxRowHeight) {
									//#if polish.css.align-heights
										if (this.isAlignHeights) {
											item.setItemHeight( maxRowHeight );
										} else
									//#endif
									if ((item.layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER) {
										item.relativeY += (maxRowHeight - item.itemHeight) >> 1;
									} else if ((item.layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM) {
										item.relativeY += (maxRowHeight - item.itemHeight);
									}
								}
							}
							rowStartIndex = i + 1;
							maxRowHeight = 0;
							rowIndex++;
						}
					} // for each item
					for (int i=0; i<this.numberOfColumns; i++) {
						this.columnsWidths[i] = new Dimension( newMaxColumnWidths[i], false );
					}
				}
			} else if (this.columnsSetting == EQUAL_WIDTH_COLUMNS) {
				// Use the maximum used column-width for each column,
				// unless this table should be expanded, in which
				// case the above set widths  will be used instead.
				if (!isLayoutExpand()) {
					for (int i = 0; i < this.columnsWidths.length; i++) {
						this.columnsWidths[i] = new Dimension( maxWidth, false );
					}
				}
			} // otherwise the column widths are defined statically.
			// set content height & width:
			int myContentWidth = 0;
			
			columnIndex = 0;
			for (int i = 0; i < myItemsLength; i++) {
				Item item = myItems[i];
				int cw = this.columnsWidths[columnIndex].getValue( availWidth );
				item.relativeX = myContentWidth;
				if (item.itemWidth < cw) {
					if (item.isLayoutCenter) {
						item.relativeX += (cw - item.itemWidth) / 2;
					} else if (item.isLayoutRight) {
						item.relativeX += (cw - item.itemWidth);
					}
				}
				myContentWidth += cw + this.paddingHorizontal;
				//#if polish.css.colspan
					if (item.colSpan > 1) {
					    for (int j = 2; j <= item.colSpan; j++) {
					    	myContentWidth += this.columnsWidths[++columnIndex].getValue( availWidth ) + this.paddingHorizontal;
					    }
					}	
				//#endif
				columnIndex++;
				if (columnIndex == this.numberOfColumns) {
					columnIndex = 0;
					myContentWidth = 0;
				}
			}
			myContentWidth = 0;
			for (int i = 0; i < this.columnsWidths.length; i++) {
				myContentWidth += this.columnsWidths[i].getValue( availWidth ) + this.paddingHorizontal;
			}
			myContentWidth -= this.paddingHorizontal;
			this.isVertical = this.numberOfRows > 1;
			this.contentWidth = myContentWidth;
			this.contentHeight = myContentHeight;
			
			//System.out.println("ContainerView.initContent(): content=" + this.contentWidth + "x" + this.contentHeight + ", focusedIndex=" + this.focusedIndex + " for " + this);

			
		//#endif
	}
	
	/**
	 * Returns the content width
	 * @return the content width
	 */
	public int getContentWidth() {
		return this.contentWidth;
	}
	
	/**
	 * Returns the content height
	 * @return the content height
	 */
	public int getContentHeight() {
		return this.contentHeight;
	}
		
	/**
	 * Determines whether this view should be expanded horizontally
	 * 
	 * @return true when this view should be expanded horizontally
	 * @see #layout
	 * @see Item#LAYOUT_EXPAND
	 */
	protected boolean isLayoutExpand() {
		return ((this.layout & Item.LAYOUT_EXPAND) == Item.LAYOUT_EXPAND);
	}

	/**
	 * Paints the content of this container view.
	 * This method adjusts the x and y offsets and forwards the call to paintContent(Container, Item[], int, int, int, int, int, int, int, int, Graphics)
	 * 
	 * @param parent the parent item
	 * @param x the left start position
	 * @param y the upper start position
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param g the Graphics on which this item should be painted.
	 * @see #paintContent(Container, Item[], int, int, int, int, int, int, int, int, Graphics)
	 */
	protected void paintContent( Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g ) {
		//System.out.println("ContainerView: painting content for " + this + " with vertical-padding " + this.paddingVertical  + ", screen=" + this.parentContainer.getScreen());
		
		//#if polish.css.view-type-top-y-offset
			y += this.topYOffset;
		//#endif
		//#if polish.css.view-type-left-x-offset
			x += this.leftXOffset;
			leftBorder += this.leftXOffset;
		//#endif
		//#if polish.css.view-type-right-x-offset
			rightBorder -= this.rightXOffset;
		//#endif
		//System.out.println("ContainerView.paint(): width=" + (rightBorder - leftBorder ) + ", firstLineWidth=" + (rightBorder - x) + ", contentWidth=" + this.contentWidth + ", parentContentWidth=" + this.parentContainer.contentWidth );
		
		Item[] myItems = this.parentContainer.getItems();
		
		paintContent( this.parentContainer, myItems, x, y, leftBorder, rightBorder, g.getClipX(), g.getClipY(), g.getClipWidth(), g.getClipHeight(), g);
		
	}
	
	/**
	 * Paints the content of this container view.
	 * This method calls 
	 * 
	 * @param container the parent container
	 * @param myItems the items that should be painted
	 * @param x the left start position
	 * @param y the upper start position
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param clipX absolute horizontal clipping start
	 * @param clipY absolute verical clipping start
	 * @param clipWidth clipping width
	 * @param clipHeight clipping height
	 * @param g the Graphics on which this item should be painted.
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		x += this.xOffset;
		for (int i = 0; i < myItems.length; i++) {
			if (i != this.focusedIndex) {
				Item item = myItems[i];
				//System.out.println("item " + i + " at " + item.relativeX + "/" + item.relativeY);
				int itemX = x + item.relativeX;
				int itemY = y + item.relativeY;
//				leftBorder = itemX;
//				rightBorder = itemX + item.itemWidth;
				paintItem(item, i, itemX, itemY, itemX, itemX + item.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
		
		// paint focused item last:
		Item focItem = this.focusedItem;
		if (focItem != null) {
			x += focItem.relativeX;
			paintItem(focItem, this.focusedIndex, x, y + focItem.relativeY, x, x + focItem.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
			//paintItem(focItem, this.focusedIndex, x, y + focItem.relativeY, leftBorder, leftBorder + focItem.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
		}
		
	}

	/**
	 * Paints this item at the specified position.
	 * Subclasses can override this method for taking advantage of the table support of the basic ContainerView class. 
	 * When the item is outside of the given clipping area, it will not be painted.
	 *  
	 * @param item the item that needs to be painted
	 * @param index the index of the item
	 * @param x the horizontal position of the item
	 * @param y the vertical position of the item
	 * @param leftBorder the left border
	 * @param rightBorder the right border
	 * @param clipX absolute horizontal clipping start
	 * @param clipY absolute verical clipping start
	 * @param clipWidth clipping width
	 * @param clipHeight clipping height
	 * @param g the graphics context
	 */
	protected void paintItem( Item item, int index,  int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g ) {
		//#debug
		System.out.println("ContainerView: painting item at (" +  x + ", " + y + ") " + item );
		if ( (index == this.focusedIndex)
			|| ( (y < clipY + clipHeight && (y + item.getItemAreaHeight() > clipY) )
			|| (item.internalX != Item.NO_POSITION_SET && y + item.internalY < clipY + clipHeight && y + item.internalY + item.internalHeight > clipY ) )
			&& (x < clipX + clipWidth && x + item.itemWidth > clipX) ) 
		{
//			if (rightBorder - leftBorder <= 1 && (item.itemWidth > 0 || !item.isInitialized)) {
//				System.out.println("painting item at left=" + leftBorder + ", right=" + rightBorder + ": " + item);
//			}
			item.paint(x, y, leftBorder, rightBorder, g);
		//} else {
		//	System.out.println("skipping " + item +": clipY=" + clipY + ", clipHeight=" + clipHeight + ", y=" + y + ", item.internalY=" + item.internalY + ", item.internalHeight=" + item.internalHeight + ", item.internalX=" + item.internalX);
		}
	} 

	/**
	 * Interprets the given user-input and retrieves the next item which should be focused.
	 * Please not that the focusItem()-method is not called as well. The
	 * view is responsible for updating its internal configuration here as well.
	 * 
	 * @param keyCode the code of the keyPressed-events
	 * @param gameAction the associated game-action to the given keyCode
	 * @return the next item which will be focused, null when there is
	 * 			no such element.
	 */
	protected Item getNextItem( int keyCode, int gameAction ) 
	{
//		System.out.println("getNextItem for "+ getScreen().getKeyName( keyCode ) + ", view=" + this);
		Item[] myItems = this.parentContainer.getItems();
		if ( 
				//#if polish.blackberry && !polish.hasTrackballEvents
					(gameAction == Canvas.RIGHT  && keyCode != Canvas.KEY_NUM6) ||
				//#else
					( this.isHorizontal && gameAction == Canvas.RIGHT  && keyCode != Canvas.KEY_NUM6) || 
				//#endif
				( this.isVertical   && gameAction == Canvas.DOWN   && keyCode != Canvas.KEY_NUM8)) 
		{
			//#if polish.css.view-type-sequential-traversal
				if (!this.isSequentialTraversal) {
			//#endif
					if (gameAction == Canvas.DOWN && this.columnsSetting != NO_COLUMNS) {
						return shiftFocus( true, this.numberOfColumns - 1, myItems, this.allowCycling, gameAction );
					}
			//#if polish.css.view-type-sequential-traversal
				}
			//#endif
			boolean allowCycle = this.allowCycling;
			if (this.isHorizontal && (gameAction == Canvas.RIGHT) && !this.isVertical) {
				allowCycle = true;
			}
			return shiftFocus( true, 0, myItems, allowCycle, gameAction );
			
		} else if ( 
				//#if polish.blackberry && !polish.hasTrackballEvents
					(gameAction == Canvas.LEFT  && keyCode != Canvas.KEY_NUM4) ||
				//#else
					(this.isHorizontal && gameAction == Canvas.LEFT  && keyCode != Canvas.KEY_NUM4) || 
				//#endif
				(this.isVertical && gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) ) 
		{
			//#if polish.css.view-type-sequential-traversal
				if (!this.isSequentialTraversal) {
			//#endif
					if (gameAction == Canvas.UP && this.columnsSetting != NO_COLUMNS) {
						return shiftFocus( false,  -(this.numberOfColumns -1 ), myItems, this.allowCycling, gameAction);
					}
			//#if polish.css.view-type-sequential-traversal
				}
			//#endif
			boolean allowCycle = this.allowCycling;
			if (this.isHorizontal && (gameAction == Canvas.LEFT) && !this.isVertical) {
				allowCycle = true;
			}
			return shiftFocus( false, 0, myItems, allowCycle, gameAction );
		}
		
//		System.out.println("getNextItem: returning null for " + getScreen().getKeyName( keyCode )	);
		
		return null;
		
	}
	
	/**
	 * Shifts the focus to the next or the previous item.
	 * 
	 * @param forwardFocus true when the next item should be focused, false when
	 * 		  the previous item should be focused.
	 * @param steps how many steps forward or backward the search for the next focusable item should be started,
	 *        0 for the current item, negative values go backwards.
	 * @param items the items of this view
	 * @return the item that has been focused or null, when no item has been focused.
	 */
	protected Item shiftFocus(boolean forwardFocus, int steps, Item[] items) {
		return shiftFocus( forwardFocus, steps, items, this.allowCycling, 0 );
	}
	
	/**
	 * Shifts the focus to the next or the previous item.
	 * 
	 * @param forwardFocus true when the next item should be focused, false when
	 * 		  the previous item should be focused.
	 * @param steps how many steps forward or backward the search for the next focusable item should be started,
	 *        0 for the current item, negative values go backwards.
	 * @param items the items of this view
	 * @param allowCycle true when cycling should be allowed (starting at the first item when the last has been reached and the other way round)
	 * @return the item that has been focused or null, when no item has been focused.
	 */
	protected Item shiftFocus(boolean forwardFocus, int steps, Item[] items, boolean allowCycle) {
		return shiftFocus( forwardFocus, steps, items, allowCycle, 0 );
	}
	/**
	 * Shifts the focus to the next or the previous item.
	 * 
	 * @param forwardFocus true when the next item should be focused, false when
	 * 		  the previous item should be focused.
	 * @param steps how many steps forward or backward the search for the next focusable item should be started,
	 *        0 for the current item, negative values go backwards.
	 * @param items the items of this view
	 * @param allowCycle true when cycling should be allowed (starting at the first item when the last has been reached and the other way round)
	 * @param direction the original direction that should be gone, e.g. Canvas.UP or Canvas.LEFT
	 * @return the item that has been focused or null, when no item has been focused.
	 */
	protected Item shiftFocus(boolean forwardFocus, int steps, Item[] items, boolean allowCycle, int direction) {
		//#debug
		System.out.println("ContainerView.shiftFocus( forward=" + forwardFocus + ", steps=" + steps + ", focusedIndex=" + this.focusedIndex + " [container:" + this.parentContainer.focusedIndex + "]), direction=" + direction );
//		System.out.println("parent.focusedIndex=" + this.parentContainer.getFocusedIndex() );
		//boolean allowCycle = this.allowCycling;
		if (!allowCycle && forwardFocus && steps != 0 && isInBottomRow(this.focusedIndex) ) {
			return null;
		}
		int i;
		//#if polish.css.colspan
			i = this.focusedIndex;
			if ( i != -1 && steps != 0) {
				//System.out.println("ShiftFocus: steps=" + steps + ", forward=" + forwardFocus);
				int doneSteps = 0;
				steps = Math.abs( steps ) + 1;
				Item item = items[i]; //(Item) this.parentContainer.itemsList.get(i); 
				while( doneSteps <= steps ) {
					doneSteps += item.colSpan;
					if (doneSteps >= steps) {
//						System.out.println("bailing out at too many steps: focusedIndex=" + this.focusedIndex + ", startIndex=" + i + ", steps=" + steps + ", doneSteps=" + doneSteps);
						break;
					}
					if (forwardFocus) {
						i++;
						if (i == items.length - 1 ) {
							if (!allowCycle) {
								return null;
							}
//								System.out.println("reached items.length -1, breaking at -2");
							i = items.length - 2;
							break;
						} else if (i >= items.length) {
							if (!allowCycle) {
								return null;
							}
//								System.out.println("reached items.length, breaking at -1");
							i = items.length - 1;
							break;
						}
					} else {
						i--; 
						if (i < 0) {
							i = 1;
							break;
						}
					}
					item = items[i];
//					System.out.println("focusedIndex=" + this.focusedIndex + ", startIndex=" + i + ", steps=" + steps + ", doneSteps=" + doneSteps);
				}
//				System.out.println("item is now " + i + ": " + item);
				if (doneSteps >= steps && item.colSpan != 1 && i != this.focusedIndex)  {
					if (forwardFocus) {
						i--;
						if (i < 0) {
							i = items.length + i;
						}
//						System.out.println("forward: Adjusting startIndex to " + i );
					} else {
						i = (i + 1) % items.length;
//						System.out.println("backward: Adjusting startIndex to " + i );
					}
				}
			}
		//#else
			i = this.focusedIndex + steps;
			if (steps != 0) {
				if (!forwardFocus) {
					if (i < 0) {
						if (!allowCycle) {
							return null;
						}
						i = items.length + i;
					}
//					System.out.println("forward: Adjusting startIndex to " + i );
				} else {
					i = i % items.length;
					if ( i >= items.length) {
						if (!allowCycle) {
							return null;
						}
						i -= items.length;
					}
					//System.out.println("backward: Adjusting startIndex to " + i );
				}
			}
		//#endif
		//#if polish.Container.allowCycling != false
			if (allowCycle && ((direction == Canvas.UP) || (direction == Canvas.DOWN)) ) {
				if (forwardFocus) {
					// when you scroll to the bottom and
					// there is still space, do
					// scroll first before cycling to the
					// first item:
					allowCycle = (this.parentContainer.getScrollYOffset() + this.parentContainer.itemHeight <= this.parentContainer.getScrollHeight() + 1);
					//System.out.println("allowCycle-calculation: yOffset=" + this.parentContainer.yOffset + ", itemHeight=" + this.itemHeight + " (together="+ (this.yOffset + this.itemHeight));
				} else {
					// when you scroll to the top and there is still space, do
					// scroll first before cycling to the last item:
					allowCycle = (this.parentContainer.getScrollYOffset() == 0);
//					System.out.println("allowing cycle switched to " + allowCycle + ", scrollOffset=" + this.parentContainer.getScrollYOffset() + ", parentContainer=" + this.parentContainer);
				}						

			}
		//#endif
		Item nextItem = null;
//		System.out.println("shifting focus - allowCycle=" + allowCycle + ", this.allowCycling=" + this.allowCycling + ", parent.allowCycling=" + this.parentContainer.allowCycling);
//		System.out.println("starting at i=" + i + ", focusedIndex=" + this.focusedIndex + ", steps=" + steps + ", allowCycle=" + allowCycle) ;
		while (true) {
			if (forwardFocus) {
				i++;
				if (i >= items.length) {
					//#if polish.Container.allowCycling != false
						if (allowCycle) {
							allowCycle = false;
							i = 0;
						} else {
							break;
						}
					//#else
						break;
					//#endif
				}
			} else {
				i--;
				if (i < 0) {
					//#if polish.Container.allowCycling != false
						if (allowCycle) {
							allowCycle = false;
							i = items.length - 1;
						} else {
							break;
						}
					//#else
						break;
					//#endif
				}
			}
			//System.out.println("now at i=" + i + ", focusedIndex=" + this.focusedIndex);
			nextItem = items[i];
			if (nextItem.appearanceMode != Item.PLAIN) {
				break;
			}
		}
		if (nextItem == null || nextItem.appearanceMode == Item.PLAIN || nextItem == this.focusedItem) {
			//#debug
			System.out.println("returning null: allowCycling=" + this.parentContainer.allowCycling + ", local-cycle=" + allowCycle);
			return null;
		}
//		int direction = Canvas.UP;
//		if (forwardFocus) {
//			direction = Canvas.DOWN;
//		}
		Screen screen = getScreen();
		Item focItem = this.focusedItem;
		if (forwardFocus && focItem != null && nextItem.relativeY < focItem.relativeY) {
			// scroll to the top when focussing an item above the current one:
			this.parentContainer.setScrollYOffset(0, true);
		}
		if (screen != null && focItem != null 
				&& screen.container == this.parentContainer
				&& forwardFocus && i > this.focusedIndex 
				&& (nextItem.relativeY - focItem.relativeY + (focItem.relativeY + this.parentContainer.getRelativeScrollYOffset()) > screen.contentHeight )
		) {
			// scroll before shifting focus
			//#debug
			System.out.println("scroll before shifting focus");
			return null;
		}
				
		focusItem(i, nextItem );
		return nextItem;
	}

	/**
	 * Detects if the specified item index is within the last row of this view.
	 * @param index the item's index
	 * @return true when the item is in the last row of this view. When no columns are used, this will be only true for the last item.
	 */
	protected boolean isInBottomRow(int index)
	{
		//#ifdef tmp.useTable
			if (this.columnsSetting == NO_COLUMNS || this.parentContainer.size() <= 1) {
		//#endif
				return (index == this.parentContainer.size() -1 ); 
		//#ifdef tmp.useTable
			} else {
				//#if polish.css.colspan
					int adjustedIndex = 0;
					
					for (int i=0; i < index; i++) {
						Item item = this.parentContainer.get(i);
						adjustedIndex += item.colSpan;
					}
					index = adjustedIndex;
				//#endif
				int row = index / this.numberOfColumns;
				//System.out.println("index=" + index + ", row=" + row + ", numberOfRows=" + this.numberOfRows);
				return (row == this.numberOfRows - 1);
				
			}
		//#endif
	}

	/**
	 * Focuses the item with the given index.
	 * The container will then set the style of the 
	 * retrieved item. The default implementation just
	 * sets the internal focusedIndex field along with focusedItem. 
	 * When this method is overwritten, please do call super.focusItem first
	 * or set the fields "focusedIndex" and "focusedItem" yourself.
	 * This method figures out the direction and calls focusItem( index, item, direction )
	 * 
	 * @param index the index of the item
	 * @param item the item which should be focused
	 * @see #focusItem(int, Item, int)
	 */
	protected void focusItem( int index, Item item  ) {
		int direction = 0;
		if (this.focusedIndex < index ) {
			direction = Canvas.DOWN;
		} else if (this.focusedIndex == index ) {
			direction = 0;
		} else {
			direction = Canvas.UP;
		}
		focusItem( index, item, direction );
	}
	
	/**
	 * Focuses the item with the given index.
	 * The container will then set the style of the 
	 * retrieved item. The default implementation just
	 * sets the internal focusedIndex field along with focusedItem. 
	 * When this method is overwritten, please do call super.focusItem first
	 * or set the fields "focusedIndex" and "focusedItem" yourself.
	 * 
	 * @param index the index of the item
	 * @param item the item which should be focused
	 * @param direction the direction, either Canvas.DOWN, Canvas.RIGHT, Canvas.UP, Canvas.LEFT or 0.
	 */
	protected void focusItem( int index, Item item, int direction  ) {
		this.parentContainer.focusChild(index, item, direction, true );
	}

//	/**
//	 * Sets the focus to this container view.
//	 * The default implementation sets the style and the field "isFocused" to true.
//	 * 
//	 * @param focusstyle the appropriate style.
//	 * @param direction the direction from the which the focus is gained, 
//	 *        either Canvas.UP, Canvas.DOWN, Canvas.LEFT, Canvas.RIGHT or 0.
//	 *        When 0 is given, the direction is unknown.1
//	 * 
//	 */
//	public void focus(Style focusstyle, int direction) {
//		this.isFocused = true;
//		setStyle( focusstyle );
//	}
//
//	
//	/**
//	 * Notifies this view that the parent container is not focused anymore.
//	 * Please call super.defocus() when overriding this method.
//	 * The default implementation calls setStyle( originalStyle )
//	 * and sets the field "isFocused" to false.
//	 * 
//	 * @param originalStyle the previous used style, may be null
//	 */
//	protected void defocus( Style originalStyle ) {
//		this.isFocused = false;
//		if (originalStyle != null) {
//			setStyle( originalStyle );
//		}
//	}
	
	/**
	 * Sets the style for this view.
	 * The style can include additional parameters for the view.
	 * Subclasses should call super.setStyle(style) first.
	 * 
	 * @param style the style
	 */
	protected void setStyle( Style style ) {
		//#debug
		System.out.println("Setting style for " + this );
		super.setStyle( style );
		//this.columnsSetting = NO_COLUMNS;
		//#ifdef polish.css.columns
			Integer columns = style.getIntProperty("columns");
			if (columns != null) {
				this.numberOfColumns = columns.intValue();
				this.columnsSetting = NORMAL_WIDTH_COLUMNS;
				//#ifdef polish.css.columns-width
					String width = style.getProperty("columns-width");
					if (width != null) {
						if ("equal".equals(width)) {
							this.columnsSetting = EQUAL_WIDTH_COLUMNS;
						} else if ("normal".equals(width)) {
							//this.columnsSetting = NORMAL_WIDTH_COLUMNS;
							// this is the default value set above...
						} else {
							// these are pixel settings.
							String[] widths = TextUtil.split( width, ',');
							if (widths.length != this.numberOfColumns) {
								// this is an invalid setting! 
								this.columnsSetting = NORMAL_WIDTH_COLUMNS;
								//#debug warn
								System.out.println("Container: Invalid [columns-width] setting: [" + width + "], the number of widths needs to be the same as with [columns] specified.");
							} else {
								this.columnsSetting = STATIC_WIDTH_COLUMNS;
								this.columnsWidths = new Dimension[ this.numberOfColumns ];
								//#ifdef polish.css.columns-width.star
									this.starIndex = -1;
								//#endif
								for (int i = 0; i < widths.length; i++) {
									String widthStr = widths[i];
									//#ifdef polish.css.columns-width.star
										if ("*".equals( widthStr )) {
											if (this.starIndex != -1) {												
												//#debug error
												System.out.println("Container: Invalid [columns-width] setting: [" + width + "], only one * can be used!");
												this.columnsWidths[i] = new Dimension( 30, true );
											} else {
												this.starIndex = i;
												this.columnsWidths[i] = new Dimension( 0, false );;
											}
										} else {
									//#endif
											this.columnsWidths[i] = new Dimension( widthStr );
									//#ifdef polish.css.columns-width.star
										}
									//#endif
								}
								this.columnsSetting = STATIC_WIDTH_COLUMNS;
							}					
						}
					}
				//#endif
				//TODO rob allow definition of the "fill-policy"
			}
		//#endif
		//#if polish.css.view-type-left-x-offset
			Integer leftXOffsetInt = style.getIntProperty("view-type-left-x-offset");
			if (leftXOffsetInt != null) {
				this.leftXOffset = leftXOffsetInt.intValue();
			}
		//#endif
		//#if polish.css.view-type-right-x-offset
			Integer rightXOffsetInt = style.getIntProperty("view-type-right-x-offset");
			if (rightXOffsetInt != null) {
				this.rightXOffset = rightXOffsetInt.intValue();
			}
		//#endif
		//#if polish.css.view-type-top-y-offset
			Integer topYOffsetInt = style.getIntProperty("view-type-top-y-offset");
			if (topYOffsetInt != null) {
				this.topYOffset = topYOffsetInt.intValue();
			}
		//#endif
		//#if polish.css.view-type-sequential-traversal
			Boolean sequentialTraversalBool = style.getBooleanProperty("view-type-sequential-traversal");
			if (sequentialTraversalBool != null) {
				this.isSequentialTraversal = sequentialTraversalBool.booleanValue();
			}
		//#endif
		//#if polish.css.view-type-auto-traversal
			Boolean autoTraversalBool = style.getBooleanProperty("view-type-auto-traversal");
			if (autoTraversalBool != null) {
				this.allowsAutoTraversal = autoTraversalBool.booleanValue();
			}
		//#endif
		//#if polish.css.expand-items
			Boolean expandItemsBool = style.getBooleanProperty("expand-items");
			if (expandItemsBool != null) {
				this.isExpandItems = expandItemsBool.booleanValue();
			}
		//#endif
		//#if polish.css.align-heights
			Boolean alignHeightsBool = style.getBooleanProperty("align-heights");
			if (alignHeightsBool != null) {
				this.isAlignHeights = alignHeightsBool.booleanValue();
			}
		//#endif
			
	}
	

	
	/**
	 * Retrieves the next focusable item.
	 * This helper method can be called by view-implementations.
	 * The index of the currently focused item can be retrieved with the focusedIndex-field.
	 * 
	 * @param items the available items
	 * @param forward true when a following item should be looked for,
	 *        false if a previous item should be looked for.
	 * @param steps the number of steps which should be used (e.g. 2 in a table with two columns)
	 * @param allowCircle true when either the first focusable or the last focusable element
	 *        should be returned when there is no focusable item in the given direction.
	 * @return either the next focusable item or null when there is no such element
	 * @see #focusItem(int, Item)
	 */
	protected Item getNextFocusableItem( final Item[] items, final boolean forward, int steps, boolean allowCircle ) {
		int i = this.focusedIndex;
		boolean isInLoop;
		while  ( true ) {
			if (forward) {
				i += steps;
				isInLoop = i < items.length;
				if (!isInLoop) {
					if (steps > 1) {
						i = items.length - 1;
						isInLoop = true;
					} else if (allowCircle) {
						steps = 1;
						allowCircle = false;
						i = 0;
						isInLoop = true;
					}
				}
			} else {
				i -= steps;
				isInLoop = i >= 0;
				if (!isInLoop) {
					if (steps > 1) {
						i = 0;
						isInLoop = true;
					} else if (allowCircle) {
						steps = 1;
						allowCircle = false;
						i = items.length - 1;
						isInLoop = true;
					}
				}
			}
			if (isInLoop) {
				Item item = items[i];
				if (item.appearanceMode != Item.PLAIN) {
					this.focusedIndex = i;
					return item;
				}
			} else {
				break;
			}
		}
		return null;
	}
	

	/**
	 * Notifies this view that it is about to be shown (again).
	 * The default implementation just sets the restartAnimation-field to true.
	 */
	public void showNotify() {
		this.restartAnimation = true;
		super.showNotify();
	}
	
	
	/**
	 * Retrieves the screen to which this view belongs to.
	 * This is necessary since the getScreen()-method of item has only protected
	 * access. The screen can be useful for setting the title for example. 
	 * 
	 * @return the screen in which this view is embedded.
	 */
	protected Screen getScreen() {
		return this.parentContainer.getScreen();
	}
	
	/**
	 * Handles the given keyPressed event when the currently focused item was not able to handle it.
	 * The default implementation just calls getNextItem() and focuses the returned item.
	 * 
	 * @param keyCode the key code
	 * @param gameAction the game action like Canvas.UP etc
	 * @return true when the key was handled.
	 */
	public boolean handleKeyPressed( int keyCode, int gameAction) {
		//#debug
		System.out.println("ContainerView.handleKeyPressed() of container " + this.parentContainer);
		Item item = getNextItem( keyCode, gameAction );
		if (item != null) {
			return true;
		}
		return false;
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerPressed(int, int)
	 */
	public boolean handlePointerPressed(int x, int y) {
		if (this.contentWidth > this.availableWidth) {
			initHorizontalScrolling( x, y );
		}
		return super.handlePointerPressed(x, y);
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	private void initHorizontalScrolling(int x, int y) {
		//System.out.println("initHorizontalScrolling, inContentArea=" + this.parentContainer.isInContentWithPaddingArea(x, y) + ", for " + this);
		this.pointerPressedX = x;
		this.lastPointerPressXOffset = getScrollXOffset();
		this.lastPointerPressTime = System.currentTimeMillis();
		this.isPointerPressedHandled = this.parentContainer.isInContentWithPaddingArea(x, y);
	}
	//#endif
	
	//#ifdef polish.hasTouchEvents
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerTouchDown(int, int)
	 */
	public boolean handlePointerTouchDown(int x, int y) {
		if (this.contentWidth > this.availableWidth) {
			initHorizontalScrolling( x, y );
		}
		return super.handlePointerTouchDown(x, y);
	}
	//#endif

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerDragged(int, int,ClippingRegion)
	 */
	public boolean handlePointerDragged(int x, int y, ClippingRegion repaintRegion) {
		int availWidth = this.availableWidth;
		if (this.isPointerPressedHandled && this.contentWidth > availWidth) {
			int offset = this.xOffset + (x - this.pointerPressedX);
			//#if tmp.dontBounce
				if (offset + this.contentWidth < availWidth) {
					offset = availWidth - this.contentWidth;
				} else if (offset > 0) {
					offset = 0;
				}
			//#else
				if (offset + this.contentWidth < availWidth - availWidth/3) {
					offset = availWidth - availWidth/3 - this.contentWidth;
				} else if (offset > availWidth/3) {
					offset = availWidth/3;
				}
			//#endif
			if (offset != this.xOffset) {
				setScrollXOffset( offset, false );
				this.pointerPressedX = x;
				this.parentContainer.addRepaintArea(repaintRegion);
				return true;
			}
		}
		return super.handlePointerDragged(x, y, repaintRegion);
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerReleased(int, int)
	 */
	public boolean handlePointerReleased(int x, int y) {
		this.isPointerPressedHandled = false; // this is for cases where the press event is not forwarded to the view
		if ((this.contentWidth > this.availableWidth) && startHorizontalScroll(x , y )) {
			return true;
		}
		return super.handlePointerReleased(x, y);
	}
	//#endif
	
	//#ifdef polish.hasTouchEvents
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerTouchUp(int, int)
	 */
	public boolean handlePointerTouchUp(int x, int y) {
		if ((this.contentWidth > this.availableWidth) && startHorizontalScroll(x , y )) {
			return true;
		}
		return super.handlePointerTouchUp(x, y);
	}
	//#endif
	
	//#if polish.hasPointerEvents
	private boolean startHorizontalScroll(int x, int y) {
		int offset = this.xOffset;
		int scrollDiff = Math.abs(offset - this.lastPointerPressXOffset);
		if ( scrollDiff > this.availableWidth/10 || offset > 0 || offset + this.contentWidth < this.availableWidth) {
			// we have scrolling in the meantime
			// check if we should continue the scrolling:
			long dragTime = System.currentTimeMillis() - this.lastPointerPressTime;
			if (dragTime < 1000 && dragTime > 1) {
				int direction = Canvas.RIGHT;
				if (offset > this.lastPointerPressXOffset) {
					direction = Canvas.LEFT;
				}
				startScroll( direction,  (int) ((scrollDiff * 1000 ) / dragTime), 20 );
			} else if (offset > 0) {
				setScrollXOffset(0, true);
			} else if (offset + this.contentWidth < this.availableWidth) {
				setScrollXOffset( this.availableWidth - this.contentWidth, true );
			}
			return true;
		}
		return false;
	}

	//#endif
	
	/**
	 * Adjusts the yOffset or the targetYOffset so that the given relative values are inside of the visible area.
	 * The call is ignored when scrolling is not enabled for this item.
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param x the horizontal position of the area relative to this content's left edge, is ignored in the current version
	 * @param y the vertical position of the area relative to this content's top edge
	 * @param width the width of the area
	 * @param height the height of the area
	 */
	protected void scroll( int direction, int x, int y, int width, int height, boolean force ){
		Container container = this.parentContainer;
		while (!container.enableScrolling) {
			Item item = container.parent;
			if (item instanceof Container) {
				x += container.relativeX;
				y += container.relativeY;
				container = (Container) item;
			} else {
				break;
			}
		} 
		if (container.enableScrolling) {
			container.scroll( direction, x, y, width, height, force );
		}
	}
	
	protected int getParentRelativeY(){
		return this.parentContainer.relativeY;
	}
	
	protected int getItemRelativeY( Item item ){
		return item.relativeY;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#isValid(de.enough.polish.ui.Item, de.enough.polish.ui.Style)
	 */
	protected boolean isValid(Item parent, Style style) {
		return (parent instanceof Container);
	}

	/**
	 * Focuses the given item and retrieves the previous style of that item.
	 * The default implementation sets the focusedIndex and focusedItem fields
	 * and returns the result of item.focus( focusStyle, direction ).
	 * This is a method that is usually called from within the parent Container (in contrast to the other focusItem() methods which forward the call to the parentContainer).
	 * 
	 * @param index the index of the item
	 * @param item the item which should be focused
	 * @param direction the direction, either Canvas.DOWN, Canvas.RIGHT, Canvas.UP, Canvas.LEFT or 0.
	 * @param focusedStyle the new style for the focused item
	 * @return the previous style of the focussed item
	 */
	public Style focusItem(int index, Item item, int direction, Style focusedStyle) {
//		System.out.println("focusItem: index=" + index);
		this.focusedIndex = index;
		this.focusedItem = item;
		return item.focus(focusedStyle, direction);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#releaseResources()
	 */
	public void releaseResources() 
	{
		super.releaseResources();
		
		if(this.parentContainer != null)
		{
			this.parentContainer.isInitialized = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#destroy()
	 */
	public void destroy() {
		releaseResources();

		//make sure parent item is dereferenced, else possible mem leak
		this.parentContainer = null;
	}

	/**
	 * Checks if this view is a virtual container.
	 * A virtual container does not contain all items physically, but only contains some items.
	 * 
	 * @return true when this is a virtual container
	 */
	protected boolean isVirtualContainer() {
		return false;
	}
	
	/**
	 * Starts to scroll in the specified direction
	 * @param direction either Canvas.UP or Canvas.DOWN
	 * @param speed the speed in pixels per second
	 * @param damping the damping in percent; 0 means no damping at all; 100 means the scrolling will be stopped immediately
	 */
	public void startScroll( int direction,int speed, int damping) {
		if (direction == Canvas.UP || direction == Canvas.DOWN) {
			this.parentContainer.startScroll(direction, speed, damping);
		} else {
			//#debug
			System.out.println("start horizontal scrolling " + (direction == Canvas.RIGHT ? "right" : "left") + " with speed=" + speed + ", damping=" + damping + " for " + this);
			this.lastAnimationTime = System.currentTimeMillis();
			this.scrollDirection = direction;
			this.scrollSpeed = speed;
			this.scrollDamping = damping;
		}
	}
	
	/**
	 * Sets the appearance mode for this view
	 * @param appearanceMode the appearance mode 
	 */
	public void setAppearanceMode(int appearanceMode) {
		this.appearanceMode = appearanceMode;
	}
	
	/**
	 * Returns the appearance mode for this view
	 * @return the appearance mode for this view
	 */
	public int getAppearanceMode() {
		return this.appearanceMode;
	}

	/**
	 * Retrieves the child of this container view at the corresponding position.
	 * The default implementation calls getChildAtImpl(relX, relY) on the parent container.
	 * 
	 * @param relX the relative horizontal position
	 * @param relY the relatiev vertical position
	 * @return the item at that position, if any
	 */
	public Item getChildAt(int relX, int relY) {
		return this.parentContainer.getChildAtImpl(relX, relY);
	}
	

	/**
	 * Queries the width of an child item of this container.
	 * This allows subclasses to control the possible re-initialization that is happening here.
	 * @param item the child item
	 * @return the width of the child item
	 * @see #getChildHeight(Item)
	 */
	protected int getChildWidth(Item item) {
		Container parent = this.parentContainer;
		int w;
		if (item.availableWidth > 0) {
			w = item.getItemWidth( item.availableWidth, item.availableWidth, item.availableHeight );
		} else {
			w = item.getItemWidth( parent.availContentWidth, parent.availContentWidth, parent.availContentHeight );
		}
		return w;
	}
	
	/**
	 * Queries the height of an child item of this container.
	 * This allows subclasses to control the possible re-initialization that is happening here.
	 * @param item the child item
	 * @return the height of the child item
	 * @see #getChildHeight(Item)
	 */
	protected int getChildHeight(Item item) {
		Container parent = this.parentContainer;
		int h;
		if (item.availableWidth > 0) {
			h = item.getItemHeight( item.availableWidth, item.availableWidth, item.availableHeight );
		} else {
			h = item.getItemHeight( parent.availContentWidth, parent.availContentWidth, parent.availContentHeight );
		}
		return h;
	}

}
