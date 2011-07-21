//#condition polish.usePolishGui
/*
 * Created on June 21, 2007 at 10:48:13 AM.
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

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Arranges the items in a single row and scales items down.</p>
 * <p>Activate this view by specifying <code>view-type: fisheye</code> in the ChoiceGroup's, Container's or List's style.</p>
 * <p>Further attributes are:</p>
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
 * 		view-type: fisheye;
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
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        June 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CoverFlowContainerView extends ContainerView {

	protected int scaleFactorWidth = 60;
	protected int scaleFactorOuterHeight = 90;
	protected int scaleFactorInnerHeight = 60;

	private int minSpeed = 2;
	private int maxSpeed = -1;
	protected int[] targetXCenterPositions;
	protected int[] referenceXCenterPositions;
	protected int[] targetYCenterPositions;
	protected int[] referenceYCenterPositions;
	
	protected boolean isRemoveText = true;
	protected boolean includeAllItems = true;
	protected String[] labels;
	protected transient StringItem focusedLabel;
	//#if polish.midp2
		protected transient int[][] originalRgbData;
		protected int[] originalRgbDataWidths;
		protected transient int[][] shownRgbData;
		protected int[] shownRgbDataWidths;
		protected int[] shownRgbDataHeights;
	//#endif
	protected int referenceFocusedIndex;
	protected transient Background focusedBackground;
	protected Border focusedBorder;
	protected Style focusedStyle;
	protected int focusedDirection;
	protected int focusedWidth;
	protected int maxItemHeight;
	protected boolean isShowTextInTitle;
	//#if polish.css.fisheyeview-max-visible
		protected int maxVisibleItems;
	//#endif
		
	private final Object lock = new Object();
	
	/**
	 * Creates a new fish eye view
	 */
	public CoverFlowContainerView() {
		this.allowsAutoTraversal = false;
		this.allowsDirectSelectionByPointerEvent = false;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		synchronized (this.lock) {
			//#if polish.midp2
			if (this.shownRgbDataWidths == null) {
				//#debug warn
				System.out.println("CoverFlowContainerView is animated before initContent has been called");
				return;
			}
			//#endif
			boolean animated = false;
			if (this.targetXCenterPositions != null) {
				Item[] myItems = this.parentContainer.getItems();
				int length = myItems.length;
				//#if polish.css.fisheyeview-max-visible
					int maxDistance = length;
					if (this.maxVisibleItems != 0) {
						maxDistance = this.maxVisibleItems >> 1;
					}
				//#endif
				for (int i = 0; i <length; i++) {
					int target = this.targetXCenterPositions[i];
					Item item = myItems[i];
					int halfItemWidth = (item.itemWidth >> 1);
					int distance = getDistance( i, this.focusedIndex, length );
					if (distance != 0) {
						distance--;
					}
					//#if polish.midp2
						int factor = this.scaleFactorWidth;
					//#endif
					//#if polish.midp2
						if (i != this.focusedIndex) {
							halfItemWidth = (halfItemWidth * factor) / 100;
						}
					//#endif
					int current = item.relativeX + halfItemWidth;
					boolean scaleInAnyCase = false;
					//System.out.println("animate: itemWidth of " + i + " with distance " + distance + " =" + halfItemWidth);
					//System.out.println(i + ": current=" + current + ", target=" + target);
					if (current != target) {
						if ( Math.abs( current - target) > item.itemWidth ) {
							scaleInAnyCase = true;
						}
						animated = true;
						//System.out.println(i + ": animate:  with distance " + distance + ", halfItemWidth=" + halfItemWidth + ", current=" + current + ", target=" + target + ", focusedIndex=" + this.focusedIndex);
						item.relativeX = calculateCurrent( current, target ) - halfItemWidth;
						//System.out.println( i + ": relativeX=" + item.relativeX);
					}
					if (this.targetYCenterPositions != null) {
						int halfItemHeight = (item.itemHeight >> 1);
						//#if polish.midp2
							if (i != this.focusedIndex) {
								halfItemHeight = (halfItemHeight * factor) / 100;
							}
						//#endif
						current = item.relativeY + halfItemHeight;
						target = this.targetYCenterPositions[i];
						if (current != target) {
							animated = true;
							item.relativeY = calculateCurrent( current, target ) - halfItemHeight;						
						}
					}
					//#if polish.css.fisheyeview-max-visible
						if (distance >= maxDistance ) {
							continue;
						}
					//#endif
					//#if polish.midp2
						boolean isLeft = current < target;
						current = this.shownRgbDataWidths[ i ];
						if (i == this.focusedIndex) {
							target = this.originalRgbDataWidths[ i ];
						} else {
							target = (this.originalRgbDataWidths[ i ] * factor) / 100;
						}
						if (current != target || scaleInAnyCase) {
							animated = true;
							int[] data = this.originalRgbData[i];
							int originalWidth = this.originalRgbDataWidths[i];
							int newWidth = calculateCurrent( current, target );
							int height = item.itemHeight;
							int newHeightInner;
							int newHeightOuter;
							if (i == this.focusedIndex) {
								newHeightInner = (height * (this.scaleFactorInnerHeight + ((current * (100 - this.scaleFactorInnerHeight))/target) )) / (100);
								newHeightOuter = (height * (this.scaleFactorOuterHeight + ((current * (100 - this.scaleFactorOuterHeight))/target) )) / (100);
							} else {
								newHeightInner = (height * (100 - ((current * (100 - this.scaleFactorInnerHeight))/target) )) / (100);
								newHeightOuter = (height * (100 - ((current * (100 - this.scaleFactorOuterHeight))/target) )) / (100);
							}
							if (newHeightInner > height) {
								newHeightInner = height;
							}
							if (newHeightOuter > height) {
								newHeightOuter = height;
							}
							//this.shownRgbDataWidths[i] = newWidth;
							
							if ((i == this.focusedIndex && isLeft) || isLeftOfFocus(i, this.focusedIndex, length) ){ 
								ImageUtil.perspectiveShear(data, this.shownRgbData[i], originalWidth, newWidth, newHeightOuter, newHeightInner, 255, ImageUtil.EDGEDETECTION_MAP_FAST_AND_SIMPLE);
							} else {
								ImageUtil.perspectiveShear(data, this.shownRgbData[i], originalWidth, newWidth, newHeightInner, newHeightOuter, 255, ImageUtil.EDGEDETECTION_MAP_FAST_AND_SIMPLE);
							}
							//System.out.println("newWidth=" + newWidth + ", originalWidth=" + originalWidth);
						}
					//#endif
				}
			}
			if (this.isRemoveText && this.focusedLabel != null) {
				animated |= this.focusedLabel.animate();
			}
			if (this.focusedBackground != null) {
				animated |= this.focusedBackground.animate();
			}
			if (animated && repaintRegion != null) {
				repaintRegion.addRegion( this.parentContainer.getAbsoluteX() - 10, 
						this.parentContainer.getAbsoluteY() - 10, 
						this.parentContainer.itemWidth + 20,
						this.parentContainer.itemHeight + 20
				);
			}
		}
	}


	
	
	/**
	 * Retrieves the distance between the given index and the focused element witin the list.
	 *  
	 * @param i the index
	 * @param focused the index of the focused element
	 * @param length the length of the list
	 * @return the distance between the index and the focused lement
	 */
	protected static int getDistance(int i, int focused, int length) {
		/*
		 * r = Math.max( f, i )
		 * l = Math.min( f, i )
		 * distance = Math.min( length - r + l, r -l )
		 */
		if (i == focused) {
			return 0;
		}
		int right, left;
		if (focused > i) {
			right = focused;
			left = i;
		} else {
			right = i;
			left = focused;
		}
		return Math.min( length - right + left, right - left);
	}

	/**
	 * Used within the animate() method to move a position or alpha value towards a target position/alpha.
	 * @param current the current value
	 * @param target the target value
	 * @return the current value closer to the target
	 */
	protected int calculateCurrent(int current, int target) {
		int speed = Math.max( this.minSpeed, Math.abs( current - target ) / 3 );
		if (this.maxSpeed != -1 && speed > this.maxSpeed) {
			speed = this.maxSpeed;
		}
		if (current < target ) {
			current += speed;
			if (current > target) {
				current = target;
			}
		} else {
			current -= speed;
			if (current < target) {
				current = target;
			}
		}
		return current;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight) {
		synchronized (this.lock) {
		this.isVertical = false;
		this.isHorizontal = true;
		Container parent = (Container) parentContainerItem;		
		//#debug
		System.out.println("CoverFlow: intialising content for " + this + " with vertical-padding " + this.paddingVertical );

		this.parentContainer = parent;
		Item[] myItems = parent.getItems();
		int length = myItems.length;
		if (this.focusedIndex == -1 && length != 0) {
			//this.parentContainer.focus(0);
			if (parent.focusedIndex != -1) {
				this.focusedIndex = parent.focusedIndex;
			} else {
				this.focusedIndex = 0;
			}
			//System.out.println("AUTO-FOCUSSING ITEM " + this.focusedIndex );
			this.focusedItem = myItems[this.focusedIndex];
			this.focusedStyle = this.focusedItem.getFocusedStyle();
		}
		if (this.referenceXCenterPositions != null && this.referenceXCenterPositions.length == length) {
			return;
		}

		//#if polish.css.show-text-in-title
			if (this.isRemoveText && this.focusedLabel == null && !this.isShowTextInTitle) {
				this.focusedLabel = new StringItem(null, null);
			}
		//#else
			if (this.isRemoveText && this.focusedLabel == null) {
				this.focusedLabel = new StringItem(null, null);
			}
		//#endif

		if (this.isRemoveText && (this.labels == null || this.labels.length != length)) {
			this.labels = new String[ length ];
		}

		int maxWidth = 0;
		int maxHeight = 0;
		boolean hasFocusableItem = false;
		//#if polish.midp2
			this.originalRgbData = new int[ length ][];
			this.originalRgbDataWidths = new int[ length ];
			this.shownRgbData = new int[ length ][];
			this.shownRgbDataWidths = new int[ length ];
			this.shownRgbDataHeights= new int[ length ];
		//#endif
		for (int i = 0; i < length; i++) {
			Item item = myItems[i];
			if (this.isRemoveText) {
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
						//iconItem.setText(null);
					}
				}
			}
			int width = item.getItemWidth( firstLineWidth, availWidth, availHeight );
			int height = item.getItemHeight( firstLineWidth, availWidth, availHeight );
			//#if polish.midp2
				int[] data = UiAccess.getRgbData(item);
				this.originalRgbData[i] = data;
				this.originalRgbDataWidths[i] = width;
				if (this.scaleFactorWidth == 100) {
					this.shownRgbData[i] = data;
					this.shownRgbDataWidths[i] = width;
					this.shownRgbDataHeights[i] = height;
				} else {
					int newWidth = (width * this.scaleFactorWidth) / 100;
					int newHeightInner = (height * this.scaleFactorInnerHeight) / 100;
					int newHeightOuter = (height * this.scaleFactorOuterHeight) / 100;
					this.shownRgbData[i]=new int[data.length];
					if (isLeftOfFocus(i, this.focusedIndex, length)) { 
						ImageUtil.perspectiveShear(data, this.shownRgbData[i], width, newWidth, newHeightOuter, newHeightInner, 255, ImageUtil.EDGEDETECTION_MAP_FAST_AND_SIMPLE);
					} else {
						ImageUtil.perspectiveShear(data, this.shownRgbData[i], width, newWidth, newHeightInner, newHeightOuter, 255, ImageUtil.EDGEDETECTION_MAP_FAST_AND_SIMPLE);
					}
//					System.out.println("newWidth=" + newWidth);
					this.shownRgbDataWidths[i] = width;
					this.shownRgbDataHeights[i] = height;
				}
			//#endif
			if (item.appearanceMode != Item.PLAIN) {
				hasFocusableItem = true;
			}
			if (width > maxWidth) {
				maxWidth = width; 
			}
			if (height > maxHeight) {
				maxHeight = height;
			}
		}
		this.maxItemHeight = maxHeight;
		if (hasFocusableItem) {
			this.appearanceMode = Item.INTERACTIVE;
		} else {
			this.appearanceMode = Item.PLAIN;
		}
		
		initItemArrangement(availWidth, myItems, length, maxWidth, maxHeight);
		
		// use reference positions to set the position for the items:
		for (int i = 0; i < length; i++) {
			Item item = myItems[i];
			int distance = getDistance( i, this.focusedIndex, length );
			if (distance != 0) {
				distance--;
			}
			int halfItemWidth = (item.getItemWidth(availWidth, availWidth, availHeight) >> 1);
			int halfItemHeight = (item.getItemHeight(availWidth, availWidth, availHeight) >> 1);
			//#if polish.midp2
				if (i != this.focusedIndex) {
					int factor = getScaleFactor(distance, length);
					halfItemWidth =  (halfItemWidth  * factor) / 100;
					halfItemHeight = (halfItemHeight * factor) / 100;
				}
			//#endif
			//System.out.println(i + ": initContent:  with distance " + distance + ", halfItemWidth=" + halfItemWidth + ", focusedIndex=" + this.focusedIndex);
			item.relativeX = this.referenceXCenterPositions[i] - halfItemWidth;
			if (this.referenceYCenterPositions != null) {
				item.relativeY = this.referenceYCenterPositions[i] - halfItemHeight; 
			}
			//System.out.println( i + ": relativeX=" + item.relativeX);

//			System.out.println("item.relativeX for " + i + "=" + item.relativeX);
		}
		if (this.focusedStyle != null) {
			focusItem( this.focusedIndex, this.focusedItem, this.focusedDirection, this.focusedStyle );
			this.focusedItem.relativeX = this.referenceXCenterPositions[this.focusedIndex] - (this.focusedItem.getItemWidth( availWidth, availWidth, availHeight) >> 1);
			if (this.referenceYCenterPositions != null) {
				this.focusedItem.relativeY = this.referenceYCenterPositions[this.focusedIndex] - (this.focusedItem.getItemHeight(availWidth, availWidth, availHeight) >> 1);
			}
//			System.out.println("focused.relativeX=" + this.focusedItem.relativeX);
			this.focusedStyle = null;
		}
		
		this.contentWidth = availWidth; //TODO: this can change when no expanded layout is used
		this.contentHeight = this.focusedLabel == null ? maxHeight : maxHeight + this.focusedLabel.getItemHeight(availWidth, availWidth, availHeight); // maxItemHeight + this.paddingVertical + this.focusedLabel.getItemHeight(lineWidth, lineWidth);
		
		if (!this.isFocused) {
			AnimationThread.addAnimationItem( parent );
		}
		animate(System.currentTimeMillis(), null);
		}
	}



	/**
	 * @param i
	 * @param focIndex
	 * @param length 
	 * @return
	 */
	private boolean isLeftOfFocus(int i, int focIndex, int length)
	{
		int leftBorder = focIndex - (length >> 1);
		if (leftBorder >= 0) {
			return i < focIndex && i >= leftBorder;
		} else {
			return i < focIndex || i >= (length + leftBorder);
		}
	}


	/**
	 * Arranges the items in this view.
	 * 
	 * @param lineWidth the available line width
	 * @param myItems all items
	 * @param length the number of items
	 * @param maxWidth the maximum width of one item
	 * @param maxHeight the maximum height of one item
	 */
	protected void initItemArrangement(int lineWidth, Item[] myItems, int length, int maxWidth, int maxHeight) {
		this.referenceXCenterPositions = new int[length];
		this.referenceXCenterPositions[this.focusedIndex] = lineWidth >> 1;
		this.referenceFocusedIndex = this.focusedIndex;

		int completeWidth;
		//#if polish.midp2
			completeWidth = maxWidth + ((maxWidth*this.scaleFactorWidth)/100) * (length - 1) + ( length -1 ) * this.paddingHorizontal;
		//#else
			completeWidth = maxWidth * length + ( length - 1 ) * this.paddingHorizontal;
		//#endif
		
		if (this.focusedStyle != null && this.focusedItem != null) {
			UiAccess.focus(this.focusedItem, this.focusedDirection, this.focusedStyle );
			this.focusedWidth = this.focusedItem.getItemWidth( lineWidth, lineWidth, maxHeight );
			this.focusedItem.relativeX = (lineWidth - this.focusedWidth) >> 1;
		} else if (this.focusedWidth == 0) {
			this.focusedWidth = maxWidth;
		}
		int availWidth;
		if ( (completeWidth > lineWidth && this.includeAllItems) || (completeWidth < lineWidth && isLayoutExpand() ) ) {
			availWidth = ((lineWidth - this.focusedWidth) >> 1) - this.paddingHorizontal;
		} else {
			availWidth = ((completeWidth - this.focusedWidth) >> 1) - this.paddingHorizontal; 
		}
//		System.out.println("available=" + availableWidth + ", lineWidth=" + lineWidth + ", completeWidth=" + completeWidth + ", maxItemWidth=" + maxWidth + ", paddingHorizontal=" + this.paddingHorizontal);
		int availableWidthPerItem = (availWidth << 8) / (length -1);
		// process items on the left side:
		int index = this.focusedIndex - 1;
		int processed = 0;
		int halfLength = (length - 1) >> 1;
		int startX = availWidth;
//		System.out.println("left: startX=" + startX + ", center=" + (lineWidth/2) );
		while (processed < halfLength ) {
			if (index < 0) {
				index = length - 1;
			}
			this.referenceXCenterPositions[index] = startX - ((processed * availableWidthPerItem) >>> 8) - (processed * this.paddingHorizontal); //  - (maxItemWidth >> 1);
//			System.out.println( index + "=" + this.referenceXCenterPositions[index]);
			index--;
			processed++;
		}
		// process items on the right side:
		index = this.focusedIndex + 1;
		processed = 0;
		halfLength = length >> 1;
		startX =  lineWidth -  availWidth - (this.paddingHorizontal >> 1); //(lineWidth >> 1) +  ((lineWidth >> 1) - startX);
//		System.out.println("right: startX=" + startX + ", center=" + (lineWidth/2) );
		while (processed < halfLength) {
			if (index >= length) {
				index = 0;
			}
			this.referenceXCenterPositions[index] = startX + ((processed * availableWidthPerItem) >>> 8) + (processed * this.paddingHorizontal); //+ (maxWidth >> 1);
//			System.out.println( index + "=" + this.referenceXCenterPositions[index]);
			index++;
			processed++;
		}
		

	}
	
	/**
	 * Obtains the scaling factor for the given distance from the center item.
	 * 
	 * @param distance the distance to the central/focused item - an item next to the focused item has the distance 0.
	 * @param length the number of items of the parent container
	 * @return the scaling factor in percent - 100 means there is nothing to scale.
	 */
	protected int getScaleFactor( int distance, int length ) {
		int factor = 100;
		//#if polish.midp2
			factor = this.scaleFactorWidth;
		//#endif
		return factor;
		
	}
	
	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		super.defocus(originalStyle);
		AnimationThread.addAnimationItem( this.parentItem );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focus(de.enough.polish.ui.Style, int)
	 */
	public void focus(Style focusstyle, int direction) {
		super.focus(focusstyle, direction);
		AnimationThread.removeAnimationItem( this.parentItem );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focusItem(int, de.enough.polish.ui.Item, int, de.enough.polish.ui.Style)
	 */
	public Style focusItem(int focIndex, Item item, int direction, Style focStyle) {
		if (this.referenceXCenterPositions == null || this.referenceXCenterPositions.length != this.parentContainer.size() ) {
			this.focusedStyle = focStyle;
			this.focusedDirection = direction;
			this.focusedIndex = focIndex;
			this.focusedItem = item;
			return item.getStyle();
		} else {
			int difference = this.referenceFocusedIndex - focIndex;
			Item[] myItems = this.parentContainer.getItems();
			int[] targetXPositions;
			int[] targetYPositions = null;
			if (this.targetXCenterPositions == null || this.targetXCenterPositions.length != myItems.length) {
				targetXPositions = new int[ myItems.length ];
				if (this.referenceYCenterPositions != null) {
					targetYPositions = new int[ myItems.length ];
				}
			} else {
				targetXPositions = this.targetXCenterPositions;
				if (this.referenceYCenterPositions != null) {
					targetYPositions = this.targetYCenterPositions;
				}
			}
			if (this.referenceXCenterPositions.length != targetXPositions.length) {
				return item.getStyle();
			}
			for (int i = 0; i < myItems.length; i++) {
				int nextIndex = i + difference;
				if (nextIndex < 0) {
					nextIndex = myItems.length + nextIndex;
				} else if (nextIndex >= myItems.length ) {
					nextIndex -= myItems.length;
				}
				targetXPositions[i] = this.referenceXCenterPositions[ nextIndex ];
				if (targetYPositions != null) {
					targetYPositions[i] = this.referenceYCenterPositions[ nextIndex ];
				}
			}
			this.targetXCenterPositions = targetXPositions;
			if (targetYPositions != null) {
				this.targetYCenterPositions = targetYPositions;
			}
		}
		Style itemStyle;
		if (!item.isFocused) {
			itemStyle = super.focusItem(focIndex, item, direction, focStyle);
		} else {
			itemStyle = item.getStyle();
		}
		
		this.focusedBackground = removeItemBackground( item );
		this.focusedBorder = removeItemBorder( item );
		if (this.isRemoveText) {
			if (this.isShowTextInTitle) {
				Screen scr = getScreen();
				if (scr != null) {
					scr.setTitle( this.labels[ focIndex ] );
				}
			} else if (this.focusedLabel != null) {
				this.focusedLabel.setText( this.labels[ focIndex ] );
				if (this.focusedLabel.getStyle() != item.getStyle() ) {
					this.focusedLabel.setStyle( item.getStyle() );
					removeItemBackground( this.focusedLabel );
					removeItemBorder( this.focusedLabel );
				}
			}
		}
		return itemStyle;
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		synchronized (this.lock) {
		int lineWidth = rightBorder - leftBorder;
		int itemLabelDiff = 0;
		if (this.isRemoveText && this.focusedLabel != null) {
			itemLabelDiff = this.focusedLabel.itemHeight - this.focusedLabel.getContentHeight();
		}
		if (this.focusedItem != null && (this.focusedBackground != null || this.focusedBorder != null)) {
			Item item = this.focusedItem;
			int backgroundWidth;
			int backgroundHeight;
			if ( this.isRemoveText && this.focusedLabel != null) {
				backgroundWidth = Math.max( item.itemWidth, this.focusedLabel.getItemWidth( lineWidth, lineWidth, this.availableHeight ) );
				backgroundHeight = item.itemHeight + this.focusedLabel.itemHeight + itemLabelDiff;
			} else {
				backgroundWidth = item.itemWidth;
				backgroundHeight = item.itemHeight;
			}
//			if (this.focusedBackground != null) {
//				//TODO add background.borderWidth << 1 + border.borderWidth << 1 ??
//			}
			int backgroundX = x + ((rightBorder - leftBorder) >> 1) - (backgroundWidth >> 1 ); 
			if (this.focusedBackground != null) {
				this.focusedBackground.paint( backgroundX, y, backgroundWidth, backgroundHeight, g);
			}
			if (this.focusedBorder != null) {
				this.focusedBorder.paint( backgroundX, y, backgroundWidth, backgroundHeight, g);
			}
		}
		int itemX;
		int itemY;
		
		//#if polish.css.fisheyeview-max-visible
			int length  = myItems.length;
			int maxDistance = length;
			if (this.maxVisibleItems != 0) {
				maxDistance = this.maxVisibleItems >> 1;
			}
		//#endif
		int processed = ((myItems.length - 1) >> 1);
		int index = this.focusedIndex - processed;
		if (index < 0) {
			index += myItems.length;
		}
		// draw left items:
		while (processed > 0) {
			//#if polish.css.fisheyeview-max-visible
				int distance = getDistance( index, this.focusedIndex, length);
				if (distance != 0) {
					distance--;
				}
				if (distance < maxDistance ) {
			//#endif
					Item item = myItems[index];
					//System.out.println("left: " + index + " at " + item.relativeX );
					itemX = x + item.relativeX;
					itemY = y + item.relativeY;
					paintItem(item, index, itemX, itemY, itemX, itemX + item.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
			//#if polish.css.fisheyeview-max-visible
				}
			//#endif
			processed--;
			index++;
			if (index == myItems.length) {
				index = 0;
			}
		}
		// draw right items:
		processed = (myItems.length >> 1);
		index = (this.focusedIndex + (myItems.length >> 1))  % myItems.length;
		while (processed > 0) {
			//#if polish.css.fisheyeview-max-visible
				int distance = getDistance( index, this.focusedIndex, length);
				if (distance != 0) {
					distance--;
				}
				if (distance < maxDistance ) {
			//#endif
					Item item = myItems[index];			
					//System.out.println("right: " + index + " at " + item.relativeX );
					itemX = x + item.relativeX;
					itemY = y + item.relativeY;
					paintItem(item, index, itemX, itemY, itemX, itemX + item.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
			//#if polish.css.fisheyeview-max-visible
				}
			//#endif
			processed--;
			index--;
			if (index == -1) {
				index = myItems.length - 1;
			}
		}
		// now paint focused item:
		Item item = this.focusedItem;
		if (item != null) {
			itemX = x + item.relativeX;
			itemY = y + item.relativeY;
			//itemY = y + item.getItemHeight( lineWidth, lineWidth );
			paintItem(item, this.focusedIndex, itemX, itemY, itemX, itemX + item.itemWidth, clipX, clipY, clipWidth, clipHeight, g);

			// now paint label:
			if (this.isRemoveText && this.focusedLabel != null) {
				//System.out.println("painting focused label with style " + this.focusedLabel.getStyle() );
				int labelX = x + ((rightBorder - leftBorder) >> 1) - (this.focusedLabel.getItemWidth( lineWidth, lineWidth, this.availableHeight ) >> 1);
				int labelY = y + this.contentHeight - this.focusedLabel.itemHeight;  // item.itemHeight + itemLabelDiff;
				this.focusedLabel.paint( labelX, labelY, labelX, labelX + this.focusedLabel.itemWidth, g);
			}
		}	
//		g.setColor( 0xff0000 );
//		g.drawRect( x, y, this.contentWidth, this.contentHeight );
//		for (int i=0; i<myItems.length; i++) {
//			g.setColor( 0xff0000 );
//			int referenceX = this.referenceXCenterPositions[i];
//			g.drawLine( x + referenceX, y, x+referenceX, y+this.contentHeight);
//			g.setColor( 0x00ff00 );
//			referenceX = this.targetXCenterPositions[i];
//			g.drawLine( x + referenceX, y, x+referenceX, y+this.contentHeight);
//
//		}
		}
	}
	



	//#if polish.midp2
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintItem(de.enough.polish.ui.Item, int, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintItem(Item item, int index, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		//#if polish.midp2
			int width = this.shownRgbDataWidths[ index ];
			if (index == this.focusedIndex && (width == this.originalRgbDataWidths[index]) ) {
				super.paintItem(item, index, x, y, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
				return;
			}
			int height = this.shownRgbDataHeights[ index ];
			int[] data = this.shownRgbData[ index ];
			
			int itemLayout = item.getLayout();
			if ( (itemLayout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER) {
				y += (this.maxItemHeight - height) >> 1;
			} else if ( (itemLayout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM) {
				y += (this.maxItemHeight - height);
			} 
			DrawUtil.drawRgb( data, x, y, width, height, true, g );
//			g.setColor( 0xffff00);
//			g.drawRect( x, y, item.itemWidth, item.itemHeight );
//			g.drawLine( x, y, x + item.itemWidth, y + item.itemHeight );
		//#else
			super.paintItem(item, index, x, y, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
		//#endif
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.fisheyeview-remove-text
			Boolean removeTextBool = style.getBooleanProperty("fisheyeview-remove-text");
			if (removeTextBool != null) {
				this.isRemoveText = removeTextBool.booleanValue();
			}
		//#endif
			
		//#if polish.css.fisheyeview-scale && polish.midp2
			Integer scaleInt = style.getIntProperty( "fisheyeview-scale" );
			if (scaleInt != null) {
				this.scaleFactorWidth = scaleInt.intValue();
			}
		//#endif
		//#if polish.css.show-text-in-title
			Boolean showTextInTitleBool = style.getBooleanProperty("show-text-in-title");
			if (showTextInTitleBool != null) {
				this.isShowTextInTitle = showTextInTitleBool.booleanValue();
				if (this.isShowTextInTitle) {
					this.isRemoveText = true;
				}
			}
		//#endif
		//#if polish.css.fisheyeview-max-visible
			Integer maxVisibleItemsInt = style.getIntProperty("fisheyeview-max-visible");
			if (maxVisibleItemsInt != null) {
				this.maxVisibleItems = maxVisibleItemsInt.intValue();
			}
		//#endif
			
	}


	/* see ItemView.releaseResources() */
	public void releaseResources() {
		super.releaseResources();
		synchronized (this.lock) {
			//#if polish.midp2
				this.originalRgbData = null;
				this.originalRgbDataWidths = null;
				this.shownRgbData = null;
				this.shownRgbDataWidths = null;
				this.shownRgbDataHeights= null;
				this.referenceXCenterPositions = null;
				this.referenceYCenterPositions = null;
			//#endif
		}
	}

	
	
}
