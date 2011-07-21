//#condition polish.usePolishGui
/*
 * Created on 15-Aug-2007 at 00:41:51.
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

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;


/**
 * <p>Shows  the available items of a Container in a horizontal list.</p>
 * <p>Apply this view by specifying "view-type: horizontal;" in your polish.css file.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HorizontalContainerView extends ContainerView {
	
	private static final int DISTRIBUTE_EQUALS = 1;
	
	private boolean allowRoundTrip;
	private boolean isExpandRightLayout;
	//#if polish.css.horizontalview-align-heights
		private boolean isAlignHeights;
	//#endif
	//#if polish.css.horizontalview-distribution
		private boolean isDistributeEquals;
	//#endif
	//#if polish.css.show-text-in-title
		private boolean isShowTextInTitle;
		private String[] labels;
	//#endif
	private boolean isClippingRequired;

	private Image arrowRight;
	private Image arrowLeft;
	private int arrowLeftYAdjust;
	private int arrowRightYAdjust;

	/**
	 * Creates a new view
	 */
	public HorizontalContainerView() {
		super();
		this.allowsAutoTraversal = false;
		this.isHorizontal = true;
		this.isVertical = false;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentItm, int firstLineWidth,
			int availWidth, int availHeight) 
	{
		Container parent = (Container) parentItm;
		//#debug
		System.out.println("Initalizing HorizontalContainerView with focusedIndex=" + parent.getFocusedIndex() + " for parent " + parent);
		
		this.availableWidth = availWidth;
		
		int arrowWidth = 0;
		if (this.arrowLeft != null && this.arrowRight != null) {
			arrowWidth = this.arrowLeft.getWidth() + this.arrowRight.getWidth() + (this.paddingHorizontal * 2);
			availWidth -= arrowWidth;
			firstLineWidth -= arrowWidth;
		}
		
		int selectedItemIndex = parent.getFocusedIndex();
		int maxHeight = 0;
		int completeWidth = 0;
		if (arrowWidth > 0) {
			completeWidth = this.arrowLeft.getWidth() + this.paddingHorizontal;
		}
		Item[] items = parent.getItems();
		//#if polish.css.show-text-in-title
			if (this.isShowTextInTitle && (this.labels == null || this.labels.length != items.length)) {
				this.labels = new String[ items.length ];
			}
		//#endif

		int availItemWidth = availWidth;
		int availItemWidthWithPaddingShift8 = 0;
		//#if polish.css.horizontalview-distribution
			if (this.isDistributeEquals) {
				int left = availItemWidth - ((items.length - 1)*this.paddingHorizontal);
				availItemWidth = left / items.length;
				availItemWidthWithPaddingShift8 = (availWidth << 8) / items.length;
			}
		//#endif
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			//#if polish.css.show-text-in-title
				if (this.isShowTextInTitle) {
					String text = item.getLabel();
					if (text != null) {
						this.labels[i] = text;
						item.setLabel( null );
					} else if ( item instanceof IconItem) {
						IconItem iconItem = (IconItem) item;
						text = iconItem.getText();
						if (text != null) {						
							this.labels[i] = text;
							iconItem.setTextVisible(false);
						}
					}
				}
			//#endif
			int itemHeight = item.getItemHeight(availItemWidth, availItemWidth, availHeight);
			int itemWidth = item.itemWidth;
			if (itemHeight > maxHeight ) {
				maxHeight = itemHeight;
			}
			boolean isLast = (i == items.length - 1);
			if ( isLast && item.isLayoutRight() && (completeWidth  + item.itemWidth < availWidth) ) {
				completeWidth = availWidth - item.itemWidth;
			}
			int startX = completeWidth;
			item.relativeX = completeWidth;
			item.relativeY = 0;
			completeWidth += itemWidth + (isLast ? 0 :  this.paddingHorizontal);
			//#if polish.css.horizontalview-distribution
				if (this.isDistributeEquals) {
					completeWidth = (availItemWidthWithPaddingShift8 * (i+1)) >> 8;
					if (itemWidth < availItemWidth) {
						if (item.isLayoutCenter()) {
							item.relativeX += (availItemWidth - itemWidth) / 2;
						} else if (item.isLayoutRight() && !isLast) {
							item.relativeX += (availItemWidth - itemWidth);
						}
					}
				}
			//#endif
			if ( i == selectedItemIndex) {
				if ( startX + getScrollXOffset() < 0 ) {
					setScrollXOffset( -startX, true ); 
				} else if ( completeWidth + getScrollTargetXOffset() > availWidth ) {
					setScrollXOffset( availWidth - completeWidth, true );
				}
				//System.out.println("initContent: xOffset=" + xOffset);
				this.focusedItem = item;
			}
			if (item.appearanceMode != Item.PLAIN) {
				this.appearanceMode = Item.INTERACTIVE;
			}
		}
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			//#if polish.css.horizontalview-align-heights
				if (this.isAlignHeights && !item.isLayoutVerticalShrink()) {
					item.setItemHeight( maxHeight );
				} else
			//#endif
			if (item.isLayoutVerticalCenter()) {
				item.relativeY += (maxHeight - item.itemHeight) >> 1;
			} else if (item.isLayoutBottom()) {
				item.relativeY += (maxHeight - item.itemHeight);
			}
			if (i == items.length - 1 && item.isLayoutRight() && completeWidth < availWidth) {
				item.relativeX = availWidth - item.itemWidth;
				completeWidth = availWidth;
			}
		}
		this.contentHeight = maxHeight;
		if (arrowWidth > 0) {
			if (parent.isLayoutVerticalCenter()) {
				this.arrowLeftYAdjust = (maxHeight - this.arrowLeft.getHeight()) / 2;
				this.arrowRightYAdjust = (maxHeight - this.arrowRight.getHeight()) / 2;
			} else if (parent.isLayoutBottom()) {
				this.arrowLeftYAdjust = (maxHeight - this.arrowLeft.getHeight());
				this.arrowRightYAdjust = (maxHeight - this.arrowRight.getHeight());				
			}
		}
		if (completeWidth > availWidth) {
			this.isClippingRequired = true;
		} else {
			this.isClippingRequired = false;
		}
		if (arrowWidth > 0) {
			completeWidth += this.arrowRight.getWidth() + this.paddingHorizontal;
		}
		this.contentWidth = completeWidth;
		
    	if ( parent.isLayoutRight() && parent.isLayoutExpand() )
    	{
    		this.isExpandRightLayout = true;
    	} else {
    		this.isExpandRightLayout = false;
    	}
    	//System.out.println("init of horizontal: " + this.contentWidth + "x" + this.contentHeight);
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focusItem(int, de.enough.polish.ui.Item, int, de.enough.polish.ui.Style)
	 */
	public Style focusItem(int focIndex, Item item, int direction, Style focStyle) {
		//#if polish.css.show-text-in-title
		if (this.isShowTextInTitle) {
			Screen scr = getScreen();
			if (scr != null) {
				scr.setTitle( this.labels[ focIndex ] );
			}
		}
		//#endif
		if(item != null) {
			if (this.isClippingRequired) {
				int leftStart = 0;
		    	Image right = this.arrowRight;
		    	Image left = this.arrowLeft;
		    	boolean paintArrows = (right != null) && (left != null);
		    	int availWidth = this.availableWidth;
		    	if (paintArrows) {
		    		leftStart = left.getWidth() + this.paddingHorizontal;
		    		availWidth -= right.getWidth() + this.paddingHorizontal;
		    	}
				if (getScrollTargetXOffset() + item.relativeX < leftStart) {
					setScrollXOffset( leftStart - item.relativeX, true );
				} else if (getScrollTargetXOffset() + item.relativeX + item.itemWidth > availWidth) {
					setScrollXOffset( availWidth - item.relativeX - item.itemWidth, true );
				}
			}			
			//#if polish.css.horizontalview-align-heights
				Item lastFocusedItem = this.focusedItem;
				if (this.isAlignHeights && lastFocusedItem != null && lastFocusedItem.getContentHeight() < item.getContentHeight()) {
					this.parentContainer.setInitialized(false);
				}
			//#endif
			return super.focusItem(focIndex, item, direction, focStyle);
		}
		
		return null;
	}


	protected void setStyle(Style style) {
		super.setStyle(style);

		//#ifdef polish.css.horizontalview-roundtrip
			Boolean allowRoundTripBool = style.getBooleanProperty("horizontalview-roundtrip");
			if (allowRoundTripBool != null) {
				this.allowRoundTrip = allowRoundTripBool.booleanValue();
			}
		//#endif
		//#if polish.css.horizontalview-align-heights
			Boolean alignHeightsBools = style.getBooleanProperty("horizontalview-align-heights");
			if (alignHeightsBools != null) {
				this.isAlignHeights = alignHeightsBools.booleanValue();
			}
		//#endif
		//#if polish.css.show-text-in-title
			Boolean showTextInTitleBool = style.getBooleanProperty("show-text-in-title");
			if (showTextInTitleBool != null) {
				this.isShowTextInTitle = showTextInTitleBool.booleanValue();
			}
		//#endif
		//#if polish.css.horizontalview-distribution
			Integer distribution = style.getIntProperty("horizontalview-distribution");
			if (distribution != null) {
				this.isDistributeEquals = (distribution.intValue() == DISTRIBUTE_EQUALS);
			}
		//#endif
		//#if polish.css.horizontalview-arrow-left
			String urlLeft = style.getProperty("horizontalview-arrow-left");
			if (urlLeft != null) {
				setArrowLeft( urlLeft );
			}
		//#endif
		//#if polish.css.horizontalview-arrow-right
			String urlRight = style.getProperty("horizontalview-arrow-right");
			if (urlRight != null) {
				setArrowRight( urlRight );
			}
		//#endif
	}

	/**
	 * Sets the image URL that indicates that there are more resource right of the currently selected item.
	 * @param urlRight the image URL, use null to remove arrow image
	 * @return true when the image could be loaded successfully
	 */
	public boolean setArrowRight(String urlRight) {
		if (urlRight == null) {
			setArrowRight((Image)null);
		}
		try {
			setArrowRight( StyleSheet.getImage(urlRight, null, false) );
			return true;
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to load image " + urlRight + e);
			return false;
		}
	}

	/**
	 * Sets the image that indicates that there are more resource right of the currently selected item.
	 * @param image the image, use null to remove arrow image
	 */
	public void setArrowRight(Image image) {
		this.arrowRight = image;
	}
	/**
	 * Sets the image URL that indicates that there are more resource left of the currently selected item.
	 * @param urlLeft the image URL, use null to remove arrow image
	 * @return true when the image could be loaded successfully
	 */
	public boolean setArrowLeft(String urlLeft) {
		if (urlLeft == null) {
			setArrowLeft((Image)null);
		}
		try {
			setArrowLeft( StyleSheet.getImage(urlLeft, null, false) );
			return true;
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to load image " + urlLeft + e);
			return false;
		}
	}

	/**
	 * Sets the image that indicates that there are more resource left of the currently selected item.
	 * @param image the image, use null to remove arrow image
	 */
	public void setArrowLeft(Image image) {
		this.arrowLeft = image;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		//#debug
		System.out.println("paint " + this + " at " + x + ", " + y + ", with xOffset " + getScrollXOffset() + ", clipping req=" + this.isClippingRequired + ", expandRightLayout=" + this.isExpandRightLayout);
    	if (this.isExpandRightLayout) {
    		x = rightBorder - this.contentWidth;
    	}
    	Image right = this.arrowRight;
    	Image left = this.arrowLeft;
    	boolean paintArrows = (right != null) && (left != null);
    	if (paintArrows) {
    		x += left.getWidth() + this.paddingHorizontal;
    		rightBorder -= right.getWidth() + this.paddingHorizontal;
    	}
    	if (this.isClippingRequired) {
    		g.clipRect( x, y, rightBorder - x, this.contentHeight + 1 );
    	}
    	if (paintArrows) {
    		x -= left.getWidth() + this.paddingHorizontal;
    	}
		super.paintContent(container, myItems, x, y, leftBorder, rightBorder, clipX,
				clipY, clipWidth, clipHeight, g);
		
		if (this.isClippingRequired) {
			g.setClip(clipX, clipY, clipWidth, clipHeight);
		}
    	if (paintArrows) {
    		if (container.isLayoutExpand()) {
    			rightBorder += this.paddingHorizontal;
	    		g.drawImage(left, leftBorder, y + this.arrowLeftYAdjust, Graphics.TOP | Graphics.LEFT );
	    		g.drawImage(right, rightBorder, y + this.arrowRightYAdjust, Graphics.TOP | Graphics.LEFT );
    			
    		} else {
	    		g.drawImage(left, x, y + this.arrowLeftYAdjust, Graphics.TOP | Graphics.LEFT );
	    		g.drawImage(right, x + this.contentWidth, y + this.arrowRightYAdjust, Graphics.TOP | Graphics.RIGHT );
    		}
    	}

	}


	
	
}
