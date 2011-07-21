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
import de.enough.polish.ui.Display;
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
public class FishEyeContainerView extends ContainerView {

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
		protected int scaleFactor = 50;
		//#if polish.css.fisheyeview-scale-end
			//#define tmp.scaleAll
			protected int scaleFactorEnd;
		//#endif
		protected int startTranslucency = 200;
		protected int endTranslucency = 120;
		protected int[] targetTranslucencies;
		protected int[] currentTranslucencies;
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
	//#if polish.hasPointerEvents
		private int touchPressX;
		private int touchCurrentIndex;
		private static boolean isPointerDraggedEnabled;
	//#endif
	//#if polish.css.fisheyeview-place-label-at-top
		private boolean isPlaceLabelAtTop;
	//#endif
		
	//#if polish.css.fisheyeview-text-style
		private Style focusedLabelStyle;
		private Style focusedLabelFocusedStyle;
	//#endif
	private final Object lock = new Object();
	
	/**
	 * Creates a new fish eye view
	 */
	public FishEyeContainerView() {
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
				System.out.println("FishEyeContainerView is animated before initContent has been called");
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
						int factor = this.scaleFactor;
					//#endif
					//#if tmp.scaleAll
						factor = factor + ((this.scaleFactorEnd - factor ) * distance) / (length >> 1);
					//#endif
					//#if polish.midp2
						if (i != this.focusedIndex) {
							halfItemWidth = (halfItemWidth * factor) / 100;
						}
					//#endif
					int current = item.relativeX + halfItemWidth;
					//System.out.println("animate: itemWidth of " + i + " with distance " + distance + " =" + halfItemWidth);
					//System.out.println(i + ": current=" + current + ", target=" + target);
					if (current != target) {
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
						int currentAlpha = this.currentTranslucencies[i];
						int targetAlpha = this.targetTranslucencies[i];
						boolean adjustAlpha = (currentAlpha != targetAlpha);
						if (adjustAlpha) {
							currentAlpha = calculateCurrent( currentAlpha, targetAlpha);
							this.currentTranslucencies[i] = currentAlpha;
						}
						boolean isScaled = false;
						if (factor != 100) {
							current = this.shownRgbDataWidths[ i ];
							if (i == this.focusedIndex) {
								target = this.originalRgbDataWidths[ i ];
							} else {
								target = (this.originalRgbDataWidths[ i ] * factor) / 100;
							}
							if (current != target && ( distance < (length >> 2) || i == this.focusedIndex || (Math.abs(current - target)*100/target > 5) ) ) {
								animated = true;
								isScaled = true;
								int[] data = this.originalRgbData[i];
								int originalWidth = this.originalRgbDataWidths[i];
								int originalHeight = data.length / originalWidth;
								int newWidth = calculateCurrent( current, target );
								int newHeight = (newWidth * originalHeight) / originalWidth;
								//int alpha = calculateAlpha( getDistance( i, this.focusedIndex, length ), length );
								//this.shownRgbData[i] = ImageUtil.scale(alpha, data, newWidth, newHeight, originalWidth, originalHeight );
								//#if polish.FishEye.scaleHq
									ImageUtil.scaleDownHq(this.shownRgbData[i],data, originalWidth, newWidth, 0, currentAlpha, false);
								//#else
									ImageUtil.scale(currentAlpha, data, newWidth, newHeight, originalWidth, originalHeight, this.shownRgbData[i] );
								//#endif
								this.shownRgbDataWidths[i] = newWidth;
								this.shownRgbDataHeights[i] = newHeight;
								//item.itemWidth = newWidth;
								//item.itemHeight = newHeight;
								//item.relativeX += (originalWidth - newWidth) >> 1;
								//System.out.println("animate: new item width of " + i + " = " + newWidth + ", difference=" + (originalWidth - newWidth));
							}
						} 
						if (adjustAlpha && !isScaled) {
							// adjust only the translucency:
							animated = true;
							int[] rgbData = this.shownRgbData[i];
							if (rgbData != null) {
								ImageUtil.setTransparencyOnlyForOpaque( currentAlpha, rgbData, true );
							}
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
			if (animated) {
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
	 * @see de.enough.polish.ui.ItemView#isValid(de.enough.polish.ui.Item, de.enough.polish.ui.Style)
	 */
	protected boolean isValid(Item parent, Style style) {
		return (parent instanceof Container) && ((Container)parent).size() > 1;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight) {
		
		//#if polish.hasPointerEvents
			if (!isPointerDraggedEnabled) {
				isPointerDraggedEnabled = Display.getInstance().hasPointerMotionEvents();
			}
		//#endif
		this.isVertical = false;
		this.isHorizontal = true;
		Container parent = (Container) parentContainerItem;		
		//#debug
		System.out.println("FishEye: intialising content for " + this + " with vertical-padding " + this.paddingVertical + ", availWidth=" + availWidth );

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
			// only another item has been focused, so nothing needs to be adjusted.
			return;
		}

		//#if polish.css.show-text-in-title
			if (this.isRemoveText && this.focusedLabel == null && !this.isShowTextInTitle) {
				this.focusedLabel = new StringItem(null, null);
				//#if polish.css.fisheyeview-text-style
					if (this.focusedLabelStyle != null) {
						this.focusedLabel.setStyle(this.focusedLabelStyle);
					}
				//#endif
			}
		//#else
			if (this.isRemoveText && this.focusedLabel == null) {
				this.focusedLabel = new StringItem(null, null);
				//#if polish.css.fisheyeview-text-style
					if (this.focusedLabelStyle != null) {
						this.focusedLabel.setStyle(this.focusedLabelStyle);
					}
				//#endif
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
				int[] data = item.getRgbData( true, 255 );
				this.originalRgbData[i] = data;
				this.originalRgbDataWidths[i] = width;
				if (this.scaleFactor == 100) {
					this.shownRgbData[i] = data;
					this.shownRgbDataWidths[i] = width;
					this.shownRgbDataHeights[i] = height;
				} else {
					int newWidth = (width * this.scaleFactor) / 100;
					int newHeight = (height * this.scaleFactor) / 100;
					//this.shownRgbData[i] = ImageUtil.scale(data, newWidth, newHeight, width, height );
					int alpha = this.endTranslucency; // calculateAlpha( getDistance( i, this.focusedIndex, length ), length );
					this.shownRgbData[i]=new int[data.length];
					//#if polish.FishEye.scaleHq
						ImageUtil.scaleDownHq(this.shownRgbData[i], data,width, newWidth, 0, alpha, false);
					//#else
						ImageUtil.scale(alpha, data, newWidth, newHeight, width, height, this.shownRgbData[i] );
					//#endif
					
					this.shownRgbDataWidths[i] = newWidth;
					this.shownRgbDataHeights[i] = newHeight;
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
		
		initItemArrangement(availWidth, availHeight, myItems, length, maxWidth, maxHeight);
		
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
	}



	/**
	 * Arranges the items in this view.
	 * 
	 * @param lineWidth the available line width
	 * @param availHeight the available height in pixels
	 * @param myItems all items
	 * @param length the number of items
	 * @param maxWidth the maximum width of one item
	 * @param maxHeight the maximum height of one item
	 */
	protected void initItemArrangement(int lineWidth, int availHeight, Item[] myItems, int length, int maxWidth, int maxHeight) {
		//#debug
		System.out.println("initItemArrangement: lineWidth=" + lineWidth + ", availHeight=" + availHeight + ", maxWidth="  + maxWidth + ", maxHeight=" + maxHeight + ", length=" + length);
		if (length == 0) {
			return;
		}
		this.referenceXCenterPositions = new int[length];
		this.referenceXCenterPositions[this.focusedIndex] = lineWidth >> 1;
		this.referenceFocusedIndex = this.focusedIndex;
		if (maxWidth==0) {
			maxWidth = lineWidth;
		}
		if (maxHeight == 0) {
			maxHeight = availHeight;
		}
		int completeWidth;
		//#if polish.midp2
			completeWidth = maxWidth + ((maxWidth*this.scaleFactor)/100) * (length - 1) + ( length -1 ) * this.paddingHorizontal;
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
			factor = this.scaleFactor;
		//#endif
		//#if tmp.scaleAll
			factor = factor + ((this.scaleFactorEnd - factor ) * distance) / (length >> 1);
		//#endif
		return factor;
		
	}
	
	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		super.defocus(originalStyle);
		AnimationThread.addAnimationItem( this.parentItem );
		//#if polish.css.fisheyeview-text-style
			if (this.focusedLabelFocusedStyle != null && this.focusedLabelStyle != null) {
				this.focusedLabel.setStyle( this.focusedLabelStyle );
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focus(de.enough.polish.ui.Style, int)
	 */
	public void focus(Style focusstyle, int direction) {
		super.focus(focusstyle, direction);
		AnimationThread.removeAnimationItem( this.parentItem );
		//#if polish.css.fisheyeview-text-style
			if (this.focusedLabelFocusedStyle != null) {
				this.focusedLabel.setStyle( this.focusedLabelFocusedStyle );
			}
		//#endif
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
			//#if polish.midp2
				int[] targetAlphas;
				int[] currentAlphas;
			//#endif
			if (this.targetXCenterPositions == null || this.targetXCenterPositions.length != myItems.length) {
				targetXPositions = new int[ myItems.length ];
				if (this.referenceYCenterPositions != null) {
					targetYPositions = new int[ myItems.length ];
				}
				//#if polish.midp2
					targetAlphas = new int[ myItems.length ];
					currentAlphas = new int[ myItems.length ];
				//#endif
			} else {
				targetXPositions = this.targetXCenterPositions;
				if (this.referenceYCenterPositions != null) {
					targetYPositions = this.targetYCenterPositions;
				}
				//#if polish.midp2
					targetAlphas = this.targetTranslucencies;
					currentAlphas = this.currentTranslucencies;
				//#endif
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
				//#if polish.midp2
					targetAlphas[i] = calculateAlpha( getDistance(i, focIndex, myItems.length), myItems.length );
					//System.out.println("targetAlpha[" + i + "]=" + targetAlphas[i]);
					currentAlphas[i] = this.endTranslucency;
				//#endif
			}
			this.targetXCenterPositions = targetXPositions;
			if (targetYPositions != null) {
				this.targetYCenterPositions = targetYPositions;
			}
			//#if polish.midp2
				this.targetTranslucencies = targetAlphas;
				this.currentTranslucencies = currentAlphas;
			//#endif
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
			} else {
				StringItem focLabel = this.focusedLabel;
				if (focLabel != null) {
					int previousHeight = focLabel.itemHeight;
					focLabel.setText( this.labels[ focIndex ] );
					if (focLabel.getStyle() != item.getStyle() 
							//#if polish.css.fisheyeview-text-style
								&& (this.focusedLabelStyle == null)
							//#endif
					) {
						focLabel.setStyle( item.getStyle() );
						removeItemBackground( focLabel );
						removeItemBorder( focLabel );
					}
					if (focLabel.getAvailableHeight() != 0) {
						int currentHeight = focLabel.getItemHeight(focLabel.getAvailableWidth(), focLabel.getAvailableWidth(), focLabel.getAvailableHeight() );
						if (currentHeight != previousHeight) {
							this.contentHeight += (currentHeight - previousHeight);
							this.parentContainer.setInitialized(false);
						}
					}
				}
			}
		}
		return itemStyle;
	}

	
	//#if polish.midp2

	/**
	 * @param distance
	 * @param length
	 * @return the target alpha value for the item
	 */
	private int calculateAlpha(int distance, int length) {
		if (distance == 0) {
			return 255;
		}
		int alpha = this.startTranslucency - ((this.startTranslucency - this.endTranslucency) * distance) / (length >> 1);
		//System.out.println("alpha for processed=" + distance + ", length=" + length + "=" + alpha);
		return alpha;
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int lineWidth = rightBorder - leftBorder;
		int itemLabelDiff = 0;
		if (this.isRemoveText && this.focusedLabel != null) {
			itemLabelDiff = this.focusedLabel.itemHeight - this.focusedLabel.getContentHeight();
			//#if polish.css.fisheyeview-place-label-at-top
				if (this.isPlaceLabelAtTop) {
					int labelX = x + ((rightBorder - leftBorder) >> 1) - (this.focusedLabel.getItemWidth( lineWidth, lineWidth, this.availableHeight ) >> 1);
					this.focusedLabel.paint( labelX, y, labelX, labelX + this.focusedLabel.itemWidth, g);
					y += this.focusedLabel.itemHeight;
				}
			//#endif
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
			int backgroundY = y + this.referenceYCenterPositions[this.referenceFocusedIndex];
			if (this.focusedBackground != null) {
				this.focusedBackground.paint( backgroundX, backgroundY, backgroundWidth, backgroundHeight, g);
			}
			if (this.focusedBorder != null) {
				this.focusedBorder.paint( backgroundX, backgroundY, backgroundWidth, backgroundHeight, g);
			}
		}
		int itemX;
		int itemY;
		
		int length  = myItems.length;
		if (length == 0) {
			return;
		}
		//#if polish.css.fisheyeview-max-visible
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
			if (this.isRemoveText 
					&& this.focusedLabel != null
					//#if polish.css.fisheyeview-place-label-at-top
					&& !this.isPlaceLabelAtTop
					//#endif
			) {
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
	



	//#if polish.midp2
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintItem(de.enough.polish.ui.Item, int, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintItem(Item item, int index, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		//#if polish.midp2
			int width = this.shownRgbDataWidths[ index ];
			int height = this.shownRgbDataHeights[ index ];
			if (index == this.focusedIndex && (width == this.originalRgbDataWidths[index]) && (this.currentTranslucencies[index]==this.targetTranslucencies[index]) ) {
				super.paintItem(item, index, x, y, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
				return;
			}
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
			//#if polish.css.fisheyeview-text-style
				Style textStyle = (Style) style.getObjectProperty("fisheyeview-text-style");
				if (textStyle != null) {
					this.focusedLabelStyle = textStyle;
					if (this.focusedLabel != null) {
						this.focusedLabel.setStyle(textStyle);
						this.focusedLabelFocusedStyle = (Style) textStyle.getObjectProperty("focused-style");
					}
				}
			//#endif
		//#endif
		//#if polish.css.fisheyeview-place-label-at-top
			Boolean placeLabelAtTopBool = style.getBooleanProperty("fisheyeview-place-label-at-top");
			if (placeLabelAtTopBool != null) {
				this.isPlaceLabelAtTop = placeLabelAtTopBool.booleanValue();
			}
		//#endif
		//#if polish.css.fisheyeview-scale && polish.midp2
			Integer scaleInt = style.getIntProperty( "fisheyeview-scale" );
			if (scaleInt != null) {
				this.scaleFactor = scaleInt.intValue();
			}
		//#endif
		//#if polish.css.fisheyeview-scale-start && polish.midp2
			Integer scaleStartInt = style.getIntProperty("fisheyeview-scale-start");
			if (scaleStartInt != null) {
				this.scaleFactor = scaleStartInt.intValue();
			}
		//#endif
		//#if polish.css.fisheyeview-scale-end && polish.midp2
			Integer scaleEndInt = style.getIntProperty("fisheyeview-scale-end");
			if (scaleEndInt != null) {
				this.scaleFactorEnd = scaleEndInt.intValue();
			} else if (this.scaleFactorEnd == 0) {
				this.scaleFactorEnd = this.scaleFactor;
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
		//#if polish.css.fisheyeview-transparency && polish.midp2
			Boolean transparencyBool = style.getBooleanProperty("fisheyeview-transparency");
			if (transparencyBool != null && !transparencyBool.booleanValue()) {
				this.startTranslucency = 255;
				this.endTranslucency = 255;
			}
		//#endif
		//#if polish.css.fisheyeview-max-visible
			Integer maxVisibleItemsInt = style.getIntProperty("fisheyeview-max-visible");
			if (maxVisibleItemsInt != null) {
				this.maxVisibleItems = maxVisibleItemsInt.intValue();
			}
		//#endif
			
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#releaseResources()
	 */
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

	//#if polish.hasPointerEvents
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerDragged(int, int)
	 */
	public boolean handlePointerDragged( int x, int y ) {
		if (y < 0 || y > this.contentHeight || x < 0 || x > this.contentWidth) {
			return false;
		}
		int diff = x - this.touchPressX;
		int minDiff = this.contentWidth / this.parentContainer.size();
		if (minDiff > 30) {
			minDiff >>>= 1;
		}
		if (Math.abs(diff) < minDiff) {
			return false;
		}
		int current = this.touchCurrentIndex;
		if (diff < 0) {
			current++;
			if (current >=  this.parentContainer.size()) {
				current = 0;
			}
		} else {
			current--;
			if (current < 0) {
				current = this.parentContainer.size() - 1;
			}
		}
		this.touchCurrentIndex = current;
		this.touchPressX = x;
		this.parentContainer.focusChild( current );
		return true;
	}
	//#endif

	//#if polish.hasPointerEvents
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerPressed(int, int)
	 */
	public boolean handlePointerPressed( int x, int y ) {
		if (isPointerDraggedEnabled) {
			if (y < 0 || y > this.contentHeight || x < 0 || x > this.contentWidth) {
				return false;
			}
			this.touchPressX = x;
			this.touchCurrentIndex = this.focusedIndex;
			return true;
		} else {
			return super.handlePointerPressed(x, y);
		}
	}
	//#endif

	//#if polish.hasTouchEvents
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerTouchDown(int, int)
	 */
	public boolean handlePointerTouchDown( int x, int y) {
		if (y < 0 || y > this.contentHeight || x < 0 || x > this.contentWidth) {
			return false;
		}
		return handlePointerPressed( x, y );
	}
	//#endif

	
	
}
