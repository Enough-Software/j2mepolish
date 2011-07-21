//#condition polish.usePolishGui
/*
 * Created on 08-Apr-2005 at 11:17:51.
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;

/**
 * <p>Shows  the available items of an ChoiceGroup or a horizontal list.</p>
 * <p>Apply this view by specifying "view-type: horizontal-choice;" in your polish.css file.</p>
 *
 * <p>Copyright (c) Enough Software 2006 - 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HorizontalChoiceView extends ContainerView {
	
	private static final int DISTRIBUTE_EQUALS = 1;

	private final static int POSITION_BOTH_SIDES = 0; 
	private final static int POSITION_RIGHT = 1; 
	private final static int POSITION_LEFT = 2; 
	private final static int POSITION_NONE = 3;
	private int arrowColor;
	//#ifdef polish.css.horizontalview-left-arrow
		private Image leftArrow;
		private int leftYOffset;
	//#endif
	//#ifdef polish.css.horizontalview-right-arrow
		private Image rightArrow;
		private int rightYOffset;
	//#endif
	//#ifdef polish.css.horizontalview-arrows-image
		private Image arrowsImage;
		private int yArrowsAdjust;
	//#endif 
	//#ifdef polish.css.horizontalview-arrow-position
		private int arrowPosition;
		//#ifdef polish.css.horizontalview-arrow-padding
			private int arrowPadding;
		//#endif
	//#endif
	//#ifdef polish.css.horizontalview-roundtrip
		private boolean allowRoundTrip;
	//#endif
	//#ifdef polish.css.horizontalview-expand-background
		private Background expandBackground;
		private boolean isExpandBackground = true;
	//#endif
	//#if polish.css.horizontalview-distribution
		private boolean isDistributeEquals;
	//#endif

	private int arrowWidth = 10;
	private int currentItemIndex;
	private int leftArrowStartX;
	private int leftArrowEndX;
	private int rightArrowStartX;
	private int rightArrowEndX;
	//private boolean isInitialized;
	private int pointerReleasedIndex = -1;
	private int contentStart;

	/**
	 * Creates a new view
	 */
	public HorizontalChoiceView() {
		super();
		this.allowsAutoTraversal = false;
		this.allowsDirectSelectionByPointerEvent = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentItm, int firstLineWidth, int availWidth, int availHeight) 
	{
		//#debug
		System.out.println("Initalizing HorizontalChoiceView with a lineWidth of " + availWidth );
		Container parent = (Container) parentItm;
		ChoiceGroup choiceGroup = (ChoiceGroup) parent;
		int selectedItemIndex;
		int focIndex = parent.getFocusedIndex();
		boolean isMultiple = choiceGroup.getType() == ChoiceGroup.MULTIPLE;
		if (!isMultiple) {
			selectedItemIndex = choiceGroup.getSelectedIndex();
			if (selectedItemIndex == -1) {
				selectedItemIndex = 0;
			}
//			if ( selectedItemIndex < parent.size() ) {
//				parent.focusChild (selectedItemIndex, parent.get( selectedItemIndex ), 0, true);
//			}
		} else {
			selectedItemIndex = focIndex;
		}
		//parent.focusedIndex = selectedItemIndex;
		int height = 0;
		//#if polish.css.horizontalview-left-arrow || polish.css.horizontalview-right-arrow
			int width = 0;
			//#ifdef polish.css.horizontalview-left-arrow
				if (this.leftArrow != null) {
					width = this.leftArrow.getWidth();
					height = this.leftArrow.getHeight();
				}
			//#endif
			//#ifdef polish.css.horizontalview-right-arrow
				if (this.rightArrow != null) {
					if ( this.rightArrow.getWidth() > width) {
						width = this.rightArrow.getWidth();
						if (this.leftArrow.getHeight() > height) {
							height = this.leftArrow.getHeight();
						}
					}
				}
			//#endif
			//#if polish.css.horizontalview-left-arrow && polish.css.horizontalview-right-arrow
				if (this.rightArrow != null && this.leftArrow != null) {
					this.arrowWidth = width;
				} else {
			//#endif
					if (width > this.arrowWidth) {
						this.arrowWidth = width;
					}
			//#if polish.css.horizontalview-left-arrow && polish.css.horizontalview-right-arrow
				}
			//#endif
		//#endif
		int completeArrowWidth;
		//#if polish.css.horizontalview-arrows-image
			if (this.arrowsImage != null) {
				height = this.arrowsImage.getHeight();
				completeArrowWidth = this.arrowsImage.getWidth();
				this.arrowWidth = completeArrowWidth / 2;
			} else {
		//#endif
				//#if polish.css.horizontalview-arrow-padding
					completeArrowWidth = ( this.arrowWidth * 2 ) + this.paddingHorizontal + this.arrowPadding;
				//#else
					completeArrowWidth = ( this.arrowWidth + this.paddingHorizontal ) << 1;
				//#endif
		//#if polish.css.horizontalview-arrows-image
			}
		//#endif
		int contentStartX = 0;
		//#ifdef polish.css.horizontalview-arrow-position
			if (this.arrowPosition == POSITION_BOTH_SIDES) {
		//#endif
				this.leftArrowStartX = 0;
				this.leftArrowEndX = this.arrowWidth;
				this.rightArrowStartX = availWidth - this.arrowWidth;
				this.rightArrowEndX = availWidth;
				contentStartX = this.leftArrowEndX;
		//#ifdef polish.css.horizontalview-arrow-position
			} else if (this.arrowPosition == POSITION_RIGHT ){
				this.leftArrowStartX = availWidth - completeArrowWidth + this.paddingHorizontal;
				this.leftArrowEndX = this.leftArrowStartX + this.arrowWidth;
				this.rightArrowStartX = availWidth - this.arrowWidth;
				this.rightArrowEndX = availWidth;
			} else if (this.arrowPosition == POSITION_LEFT ) {
				this.leftArrowStartX = 0;
				this.leftArrowEndX = this.arrowWidth;
				this.rightArrowStartX = this.arrowWidth + this.paddingHorizontal;
				this.rightArrowEndX = this.rightArrowStartX + this.arrowWidth;
				contentStartX = this.leftArrowEndX;
			} else {
				completeArrowWidth = 0;
			}
		//#endif
		this.contentStart = contentStartX;
		availWidth -= completeArrowWidth;
		int completeWidth = 0;
		int availItemWidth = availWidth;
		int availItemWidthWithPaddingShift8 = 0;
		Item[] items = parent.getItems();
		//#if polish.css.horizontalview-distribution
			if (this.isDistributeEquals) {
				int left = availItemWidth - ((items.length - 1)*this.paddingHorizontal);
				availItemWidth = left / items.length;
				availItemWidthWithPaddingShift8 = (availWidth << 8) / items.length;
			}
		//#endif
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			//TODO allow drawing of boxes as well
			//if (!isMultiple) {
				((ChoiceItem) item).drawBox = false;
			//}
			int itemHeight = item.getItemHeight(availItemWidth, availItemWidth, availHeight);
			int itemWidth = item.itemWidth;
			if (itemHeight > height ) {
				height = itemHeight;
			}
			boolean isLast = (i == items.length - 1);
			if ( isLast && item.isLayoutRight() && (completeWidth  + item.itemWidth < availWidth) ) {
				completeWidth = availWidth - item.itemWidth;
			}
			item.relativeX = completeWidth + contentStartX;
			item.relativeY = 0;
			int startX = completeWidth;
			completeWidth += itemWidth + this.paddingHorizontal;
			//#if polish.css.horizontalview-distribution
				if (this.isDistributeEquals) {
					completeWidth = (availItemWidthWithPaddingShift8 * (i+1)) >> 8;
				}
			//#endif
			if ( i == focIndex) {
				if ( startX + getScrollXOffset() < 0 ) {
					setScrollXOffset( -startX ); 
				} else if ( completeWidth + getScrollXOffset() > availWidth ) {
					setScrollXOffset( availWidth - completeWidth );
				}
			}
		}
		// now adjust vertical offsets:
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			int itemHeight = item.itemHeight;
			if (height > itemHeight) {
				int lo = item.getLayout();
				if ((lo & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER ) {
					item.relativeY += (height - item.itemHeight) / 2;
				} else if ((lo & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM ) {
					item.relativeY += (height - item.itemHeight);
				}

			}
		}
		if (completeWidth < availWidth) { // && parent.isLayoutExpand()) { always adjust width, so that arrows are painted correctly [or later adjust arrow positioning]
			completeWidth = availWidth;
		} 
		this.contentWidth = completeWidth + completeArrowWidth;
		this.contentHeight = height;
		
		if (items.length > 0) {
			this.appearanceMode = Item.INTERACTIVE;
		} else {
			this.appearanceMode = Item.PLAIN;
		}
		if (selectedItemIndex < items.length && selectedItemIndex != -1) {
			if (this.focusedItem == null) {
				this.focusedItem = items[ selectedItemIndex ];
				this.focusedIndex = selectedItemIndex;
			}
			this.currentItemIndex = selectedItemIndex;
		}
		//if ( selectedItem.isFocused ) {
			//System.out.println("Exclusive Single Line View: contentHeight=" + this.contentHeight);
		//}
		//this.isInitialized = true;
		
		//#if polish.css.horizontalview-arrows-image
			if (this.arrowsImage != null) {
				int offset = (this.contentHeight - this.arrowsImage.getHeight()) / 2; // always center vertically
				this.yArrowsAdjust = offset;
			}
		//#endif
		//#if polish.css.horizontalview-left-arrow			
			if (this.leftArrow != null) {
				this.leftYOffset = (this.contentHeight - this.leftArrow.getHeight()) / 2; // always center vertically
			}
		//#endif
		//#if polish.css.horizontalview-right-arrow
			if (this.rightArrow != null) {
				this.rightYOffset = (this.contentHeight - this.rightArrow.getHeight()) / 2; // always center vertically
			}
		//#endif

//		System.out.println("leftX=" + this.leftArrowStartX);
//		System.out.println("rightX=" + this.rightArrowStartX);
//		System.out.println("arrowColor=" + Integer.toHexString(this.arrowColor));
	}
	
	

	protected void setStyle(Style style) {
		//#ifdef polish.css.horizontalview-expand-background
			Boolean expandBackgroundBool = style.getBooleanProperty("horizontalview-expand-background");
			if (expandBackgroundBool != null) {
				this.isExpandBackground = expandBackgroundBool.booleanValue(); 
			}
			if (!this.isExpandBackground) {
				this.expandBackground = style.background;				
			} else {
				this.expandBackground = null;
			}
		//#endif
		super.setStyle(style);
		//#ifdef polish.css.horizontalview-arrows-image
			String arrowImageUrl = style.getProperty("horizontalview-arrows-image");
			if (arrowImageUrl != null) {
				try {
					this.arrowsImage = StyleSheet.getImage( arrowImageUrl, this, true );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load left arrow image [" + arrowImageUrl + "]" + e );
				}
			}
		//#endif
		//#ifdef polish.css.horizontalview-left-arrow
			String leftArrowUrl = style.getProperty("horizontalview-left-arrow");
			if (leftArrowUrl != null) {
				try {
					this.leftArrow = StyleSheet.getImage( leftArrowUrl, this, true );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load left arrow image [" + leftArrowUrl + "]" + e );
				}
			}
		//#endif
		//#ifdef polish.css.horizontalview-right-arrow
			String rightArrowUrl = style.getProperty("horizontalview-right-arrow");
			if (rightArrowUrl != null) {
				try {
					this.rightArrow = StyleSheet.getImage( rightArrowUrl, this, true );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load right arrow image [" + rightArrowUrl + "]" + e );
				}
			}
		//#endif
		//#ifdef polish.css.horizontalview-arrow-color
			Integer colorInt = style.getIntProperty("horizontalview-arrow-color");
			if ( colorInt != null ) {
				this.arrowColor = colorInt.intValue();
			}
		//#endif
		//#ifdef polish.css.horizontalview-arrow-position
			Integer positionInt = style.getIntProperty("horizontalview-arrow-position");
			if ( positionInt != null ) {
				this.arrowPosition = positionInt.intValue();
			}
			//#ifdef polish.css.horizontalview-arrow-padding
				Integer arrowPaddingInt = style.getIntProperty("horizontalview-arrow-padding");
				if (arrowPaddingInt != null) {
					this.arrowPadding = arrowPaddingInt.intValue();
//				} else {
//					this.arrowPadding = style.paddingHorizontal;
				}
			//#endif
		//#endif
		//#ifdef polish.css.horizontalview-roundtrip
			Boolean allowRoundTripBool = style.getBooleanProperty("horizontalview-roundtrip");
			if (allowRoundTripBool != null) {
				this.allowRoundTrip = allowRoundTripBool.booleanValue();
			}
		//#endif
		//#if polish.css.horizontalview-distribution
			Integer distribution = style.getIntProperty("horizontalview-distribution");
			if (distribution != null) {
				this.isDistributeEquals = (distribution.intValue() == DISTRIBUTE_EQUALS);
			}
		//#endif	
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		//#debug
		System.out.println("HorizontalView.start: x=" + x + ", y=" + y + ", leftBorder=" + leftBorder + ", rightBorder=" + rightBorder );
		
		int modifiedX = x;

		//#ifdef polish.css.horizontalview-arrow-position
			if (this.arrowPosition == POSITION_BOTH_SIDES ) {
		//#endif
				modifiedX += this.arrowWidth + this.paddingHorizontal;
				leftBorder += this.arrowWidth + this.paddingHorizontal;
				rightBorder -= this.arrowWidth + this.paddingHorizontal;
		//#ifdef polish.css.horizontalview-arrow-position
			} else if (this.arrowPosition == POSITION_LEFT ) {
				modifiedX += (this.arrowWidth + this.paddingHorizontal) << 1;
				leftBorder += (this.arrowWidth + this.paddingHorizontal) << 1;
			} else if (this.arrowPosition == POSITION_RIGHT){
				rightBorder -= (this.arrowWidth + this.paddingHorizontal) << 1;				
			}
		//#endif
				
		//#ifdef polish.css.horizontalview-expand-background
			if (!this.isExpandBackground && this.expandBackground != null) {
				this.expandBackground.paint(modifiedX, y, rightBorder-leftBorder, this.contentHeight, g);
			}
		//#endif


		
		//#debug
		System.out.println("HorizontalChoiceView.item: x=" + modifiedX + ", y=" + y + ", leftBorder=" + leftBorder + ", rightBorder=" + rightBorder + ", availableWidth=" + (rightBorder - leftBorder) + ", itemWidth=" + (this.focusedItem != null ? this.focusedItem.itemWidth : -1) );
//		g.setColor(0xff0000);
//		g.drawLine( rightBorder, y, rightBorder, y + this.contentHeight);
//		g.drawLine( leftBorder, y, leftBorder, y + this.contentHeight);
		boolean setClip = this.contentWidth > rightBorder - leftBorder;
		if (setClip) {
			g.clipRect(modifiedX, clipY, rightBorder - modifiedX, clipHeight );
		}
		super.paintContent(container, myItems, x, y, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
//		int itemX = modifiedX + this.xOffset;
//		int focusedX = 0;
//		int cHeight = this.contentHeight;
//		int vOffset = 0;
//		for (int i = 0; i < myItems.length; i++) {
//			Item item = myItems[i];
//			if ( item == this.focusedItem ) {
//				focusedX = itemX;				
//			} else {
//				//TODO allow explicit setting of top, vcenter and bottom for items (=layout)
//				vOffset = (cHeight - item.itemHeight) / 2;
//				item.paint(itemX, y + vOffset, itemX, itemX + item.itemWidth, g);
//			}
//			itemX += item.itemWidth + this.paddingHorizontal;
//		}
//		if (this.focusedItem != null) {
//			vOffset = (cHeight - this.focusedItem.itemHeight) / 2;
//			this.focusedItem.paint(focusedX, y + vOffset, leftBorder, rightBorder, g);			
//		}
		if (setClip) {
			g.setClip( clipX, clipY, clipWidth, clipHeight );
		}

		//#ifdef polish.css.horizontalview-arrow-position
			if (this.arrowPosition != POSITION_NONE ) {
		//#endif

		g.setColor( this.arrowColor );
		//draw left arrow:
		//#ifdef polish.css.horizontalview-roundtrip
			if (this.allowRoundTrip || this.currentItemIndex > 0) {
		//#else
			//# if (this.currentItemIndex > 0) {
		//#endif
			// draw left arrow
			int startX = x + this.leftArrowStartX;
			Image image = null;
			int vOffset = 0;
			//#if polish.css.horizontalview-arrows-image
				image = this.arrowsImage;
				vOffset = this.yArrowsAdjust;
			//#endif			
			//#ifdef polish.css.horizontalview-left-arrow
				if (image == null) {
					image = this.leftArrow;
					vOffset = this.leftYOffset;
				}
			//#endif
			if (image != null) {
				//System.out.println("Drawing left IMAGE arrow at " + startX );
				g.drawImage( image, startX, y + vOffset, Graphics.LEFT | Graphics.TOP );
			} else {
				//#if polish.midp2
					//System.out.println("Drawing left triangle arrow at " + startX );
					g.fillTriangle( 
							startX, y + this.contentHeight/2, 
							startX + this.arrowWidth, y,
							startX + this.arrowWidth, y + this.contentHeight );
				//#else
					int y1 = y + this.contentHeight / 2;
					int x2 = startX + this.arrowWidth;
					int y3 = y + this.contentHeight;
					g.drawLine( startX, y1, x2, y );
					g.drawLine( startX, y1, x2, y3 );
					g.drawLine( x2, y, x2, y3 );
				//#endif
			}
		}
		
		// draw right arrow:
		//#if polish.css.horizontalview-arrows-image
			if (this.arrowsImage != null) {
				return;
			}
		//#endif
		if (
		//#ifdef polish.css.horizontalview-roundtrip
			this.allowRoundTrip ||  
		//#endif
			(this.currentItemIndex < this.parentContainer.size() - 1) ) 
		{
			// draw right arrow
			int startX = x + this.rightArrowStartX;	
			//#ifdef polish.css.horizontalview-right-arrow
				if (this.rightArrow != null) {
					g.drawImage( this.rightArrow, startX, y + this.rightYOffset, Graphics.LEFT | Graphics.TOP );
				} else {
			//#endif
				//#if polish.midp2
					g.fillTriangle( 
							startX + this.arrowWidth, y + this.contentHeight/2, 
							startX, y,
							startX, y + this.contentHeight );
				//#else
					int y1 = y + this.contentHeight / 2;
					int x2 = startX + this.arrowWidth;
					int y3 = y + this.contentHeight;
					g.drawLine( x2, y1, startX, y );
					g.drawLine( x2, y1, startX, y3 );
					g.drawLine( startX, y, startX, y3 );
				//#endif
			//#ifdef polish.css.horizontalview-right-arrow
				}
			//#endif
		}
			
		//#ifdef polish.css.horizontalview-arrow-position
			} // if (this.arrowPosition != POSITION_NONE ) {
		//#endif

	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#getNextItem(int, int)
	 */
	protected Item getNextItem(int keyCode, int gameAction) {
		//#debug
		System.out.println("ExclusiveSingleLineView: getNextItem()");
		ChoiceGroup choiceGroup = (ChoiceGroup) this.parentContainer;
		Item[] items = this.parentContainer.getItems();
		ChoiceItem currentItem = (ChoiceItem) this.focusedItem;
		int current = this.currentItemIndex;
		if (currentItem == null) {
			//#debug warn
			System.out.println("HorizontalChoiceView: getNextItem(): no current item defined, it seems the initContent() has been skipped.");
			current = choiceGroup.getSelectedIndex();
			if (choiceGroup.getType() == ChoiceGroup.MULTIPLE) {
				current = choiceGroup.getFocusedIndex();
				if (current == -1) {
					current = 0;
				}
			}
			this.currentItemIndex = current;
			currentItem = (ChoiceItem) items[ current ];
			this.focusedItem = currentItem;
		}
		
		ChoiceItem nextItem = null;
	
		//#ifdef polish.css.horizontalview-roundtrip
			if ( gameAction == Canvas.LEFT && (this.allowRoundTrip || current > 0 )) {
		//#else
			//# if ( gameAction == Canvas.LEFT && this.currentItemIndex > 0 ) {
		//#endif
			currentItem.select(false);
			current--;
			//#ifdef polish.css.horizontalview-roundtrip
				if (current < 0) {
					current = items.length - 1;
				}
			//#endif
			nextItem = (ChoiceItem) items[ current ];
			//nextItem.adjustProperties( lastItem );
			//this.currentItem.select( true );
			choiceGroup.setSelectedIndex( current, true );
			if (getScreen() instanceof Form) {
				choiceGroup.notifyStateChanged();
			}
		//#ifdef polish.css.horizontalview-roundtrip
			} else if ( gameAction == Canvas.RIGHT && (this.allowRoundTrip || current < items.length - 1  )) {
		//#else
			} else if ( gameAction == Canvas.RIGHT && current < items.length - 1 ) {
		//#endif
			currentItem.select(false);
			current++;
			//#ifdef polish.css.horizontalview-roundtrip
				if (current >= items.length) {
					current = 0;
				}
			//#endif
			nextItem = (ChoiceItem) items[ current ];
			//nextItemItem.adjustProperties( lastItem );
			choiceGroup.setSelectedIndex( current, true );
			if (getScreen() instanceof Form) {
				choiceGroup.notifyStateChanged();
			}
			//this.currentItem.select( true );			
		}
		this.currentItemIndex = current;
		// in all other cases there is no next item:
		return nextItem;
	}
	

	

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerPressed(int, int)
	 */
	public boolean handlePointerPressed(int x, int y) {
		//#debug
		System.out.println("handlePointerPressed at " + x + ", " + y);
		if (!this.parentContainer.isInContentArea(x, y)) {
			this.isPointerPressedHandled = false;
		} else {
			this.pointerReleasedIndex = -1;
			int index = this.currentItemIndex;
			int size = this.parentContainer.size();
			boolean isMultiple = ((ChoiceGroup)this.parentContainer).getType() == ChoiceGroup.MULTIPLE;
			if (x >= this.leftArrowStartX  && x <= this.leftArrowEndX ) {
				if (isMultiple) {
					this.pointerReleasedIndex = 0;
					return true;
				}
				index--;
				if (index < 0) {
					index = size - 1;
				}
			} else if (x >= this.rightArrowStartX  && x <= this.rightArrowEndX ) {
				if (isMultiple) {
					this.pointerReleasedIndex = 0;
					return true;
				}
				index++;
				if (index >= size ) {
					index = 0;
				}
			} else {
				x -= getScrollXOffset();
				for (int i = 0; i < size; i++) {
					Item item = this.parentContainer.get(i);
					//System.out.println("item=" + item + ", relativeX=" + item.relativeX  + ", x=" + x);
					if (x >= item.relativeX && x <= item.relativeX + item.itemWidth) {
						index = i;
						break;
					}
				}
				x += getScrollXOffset();
			}
			if (index != this.currentItemIndex || !isMultiple) {
				this.pointerReleasedIndex = index;
				Item item = this.parentContainer.get(index);
				notifyItemPressedStart(item);
				super.handlePointerPressed(x, y);
				return true;
			} else {
				this.pointerReleasedIndex = -1;
			}
		}
		return super.handlePointerPressed(x, y);
	}
	//#endif
	
	
	private Item getItemAt( int x ) {
		x -= getScrollXOffset() - this.contentStart;
		int size = this.parentContainer.size();
		for (int i = 0; i < size; i++) {
			Item item = this.parentContainer.get(i);
			if (x >= item.relativeX && x <= item.relativeX + item.itemWidth) {
				return item;
			}
		}
		return null;
	}

	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#handlePointerReleased(int, int)
	 */
	public boolean handlePointerReleased(int x, int y)
	{
		//#debug
		System.out.println("handlePointerReleased: x=" + x + ", y=" + y + ", pointerReleasedIndex=" + this.pointerReleasedIndex + ", instance=" + this);
		int index = this.pointerReleasedIndex;
		if (index == -1) {
			return super.handlePointerReleased(x, y);
		}
		if (!(this.parentContainer.isInContentArea(x, y))) {
			this.pointerReleasedIndex = -1;
			return false;
		}
		super.handlePointerReleased(x, y);	
		boolean isMultiple = ((ChoiceGroup)this.parentContainer).getType() == ChoiceGroup.MULTIPLE;
		if (x >= this.rightArrowStartX  && x <= this.rightArrowEndX ) {
			if (isMultiple) {
				int target = getScrollTargetXOffset();
				if (target + this.contentWidth > this.availableWidth) {
					Item mostRightVisibleItem = getItemAt( this.availableWidth );
					int offset;
					if (mostRightVisibleItem != null) {
						offset = this.contentStart - mostRightVisibleItem.relativeX; // - this.contentStart + this.paddingHorizontal);
//						System.out.println("diff=" + diff + ", this.innerContentWidth/2=" + (this.innerContentWidth/2));
						int diff = target - offset;
						if (diff < (this.availableWidth/2)) {
							offset = target - this.availableWidth/2;
						}
					} else {
						offset = target - this.availableWidth/2;
					}
					if (offset + this.contentWidth < this.availableWidth) {
						offset = this.availableWidth - this.contentWidth;
					}
					//System.out.println("setting offset from " + this.targetXOffset + " to " + offset);
					setScrollXOffset( offset, true );
					return true;
				} else {
					// handle anyhow, we don't want anything funny happening in the original choicegroup:
					return true;
				}
			}
		} else if (x <= this.leftArrowEndX && x >= this.leftArrowStartX) {
			if (isMultiple) {
				int target = getScrollTargetXOffset();
				if (target < 0) {
					Item mostLeftVisibleItem = getItemAt( 0 );
					int offset;
					if (mostLeftVisibleItem != null) {
						offset = this.contentStart + this.availableWidth - mostLeftVisibleItem.itemWidth - mostLeftVisibleItem.relativeX; // - this.contentStart + this.paddingHorizontal);
//						System.out.println("diff=" + diff + ", this.innerContentWidth/2=" + (this.innerContentWidth/2));
						int diff = offset - target;
						if (diff < (this.availableWidth/2)) {
							offset = target + this.availableWidth/2;
						}
					} else {
						offset = target + this.availableWidth/2;
					}
					if (offset > 0) {
						offset = 0;
					}
					//System.out.println("setting offset from " + this.targetXOffset + " to " + offset);
					setScrollXOffset(offset, true);
					return true;
				} else {
					// handle anyhow, we don't anything funny happening in the original choicegroup:
					return true;
				}
			}
		}
		this.pointerReleasedIndex = -1;
		ChoiceGroup choiceGroup = (ChoiceGroup) this.parentContainer;
		//boolean isMultiple = choiceGroup.getType() == ChoiceGroup.MULTIPLE;
		if (!isMultiple) {
			ChoiceItem choiceItem = (ChoiceItem)this.focusedItem;
			choiceItem.select( false ); 
		}
		ChoiceItem item = (ChoiceItem) this.parentContainer.get( index );
		this.focusedItem = item;
		notifyItemPressedEnd(item);
		this.parentContainer.focusChild (index, item, 0, true);
		if (isMultiple) {
			item.toggleSelect();			
		} else {
			choiceGroup.setSelectedIndex( index, !item.isSelected );
		}
		this.parentContainer.notifyStateChanged();
		return true;
	}
	//#endif


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		//#ifdef polish.css.horizontalview-expand-background
			if (this.expandBackground != null) {
				this.parentContainer.background = this.expandBackground;
				this.expandBackground = null;
			}
		//#endif
//		if (this.parentBackground != null ) {
//			this.parentContainer.background = this.parentBackground;
//			this.parentBackground = null;
//		}
		super.defocus(originalStyle);
		//System.out.println("EXCLUSIVE:   DEFOCUS!");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focus(de.enough.polish.ui.Style, int)
	 */
	public void focus(Style focusstyle, int direction) {
		//#ifdef polish.css.horizontalview-expand-background
			if (!this.isExpandBackground) {
				Background bg = this.parentContainer.background;
				if (bg != null) {
					this.expandBackground = bg; 
					this.parentContainer.background = null;
				}
			}
		//#endif
		//System.out.println("EXCLUSIVE:   FOCUS, parentBackround != null: " + (this.parentBackground != null));
		super.focus(focusstyle, direction);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#isValid(de.enough.polish.ui.Item, de.enough.polish.ui.Style)
	 */
	protected boolean isValid(Item parent, Style style) {
		return (parent instanceof ChoiceGroup);
	}

}
