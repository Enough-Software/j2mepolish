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
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;

/**
 * <p>Shows only the currently selected item of an exclusive ChoiceGroup or an exclusive List.</p>
 * <p>Apply this view by specifying "view-type: exclusive-single-line;" in your polish.css file.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        08-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExclusiveSingleLineView extends ContainerView {
	
	
	private final static int POSITION_BOTH_SIDES = 0; 
	private final static int POSITION_RIGHT = 1; 
	private final static int POSITION_LEFT = 2; 
	private int arrowColor;
	//#ifdef polish.css.exclusiveview-left-arrow
		private Image leftArrow;
		private int leftYOffset;
	//#endif
	//#ifdef polish.css.exclusiveview-right-arrow
		private Image rightArrow;
		private int rightYOffset;
	//#endif
	//#ifdef polish.css.exclusiveview-arrow-position
		private int arrowPosition;
		//#ifdef polish.css.exclusiveview-arrow-padding
			private int arrowPadding;
		//#endif
	//#endif
	private boolean allowRoundTrip;
	//#ifdef polish.css.exclusiveview-expand-background
		private Background expandBackground;
		private boolean isExpandBackground = true;
	//#endif	
	private int arrowWidth = 10;
	private int currentItemIndex;
	private transient ChoiceItem currentItem;
	private int leftArrowStartX;
	private int leftArrowEndX;
	private int rightArrowStartX;
	private int rightArrowEndX;
	//#if polish.css.exclusiveview-arrow-color-pressed || (polish.css.exclusiveview-left-arrow-pressed && polish.css.exclusiveview-right-arrow-pressed)
		//#define tmp.supportPress
		private boolean isLeftArrowPressed;
		private boolean isRightArrowPressed;
		private Color arrowColorPressed;
		//#if polish.css.exclusiveview-left-arrow-pressed
			private Image leftArrowPressed;
			private Image rightArrowPressed;
		//#endif
		//#if polish.blackberry
			private long bbKeyReleaseTime;
		private boolean bbReleaseArrow;
		//#endif
	//#endif

	/**
	 * Creates a new view
	 */
	public ExclusiveSingleLineView() {
		super();
		this.allowsAutoTraversal = false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentItm, int firstLineWidth, int availWidth, int availHeight) 
	{
		//#debug
		System.out.println("Initalizing ExclusiveSingleLineView: available=" + availWidth + "x" + availHeight);
		Container parent = (Container) parentItm;
		int selectedItemIndex = ((ChoiceGroup) parent).getSelectedIndex();
		if (selectedItemIndex == -1) {
			selectedItemIndex = 0;
		}
		parent.focusedIndex = selectedItemIndex;
		int height = 0;
		//#if polish.css.exclusiveview-left-arrow || polish.css.exclusiveview-right-arrow
			int width = 0;
			//#ifdef polish.css.exclusiveview-left-arrow
				if (this.leftArrow != null) {
					width = this.leftArrow.getWidth();
					height = this.leftArrow.getHeight();
				}
			//#endif
			//#ifdef polish.css.exclusiveview-right-arrow
				if (this.rightArrow != null) {
					if ( this.rightArrow.getWidth() > width) {
						width = this.rightArrow.getWidth();
						if (this.leftArrow.getHeight() > height) {
							height = this.leftArrow.getHeight();
						}
					}
				}
			//#endif
			//#if polish.css.exclusiveview-left-arrow && polish.css.exclusiveview-right-arrow
				if (this.rightArrow != null && this.leftArrow != null) {
					this.arrowWidth = width;
				} else {
			//#endif
					if (width > this.arrowWidth) {
						this.arrowWidth = width;
					}
			//#if polish.css.exclusiveview-left-arrow && polish.css.exclusiveview-right-arrow
				}
			//#endif
		//#endif
		//#if polish.css.exclusiveview-arrow-padding
			int completeArrowWidth = ( this.arrowWidth * 2 ) + this.paddingHorizontal + this.arrowPadding;
		//#else
			//# int completeArrowWidth = ( this.arrowWidth + this.paddingHorizontal ) << 1;
		//#endif
		//#ifdef polish.css.exclusiveview-arrow-position
			if (this.arrowPosition == POSITION_BOTH_SIDES) {
		//#endif
				this.leftArrowStartX = 0;
				this.leftArrowEndX = this.arrowWidth;
				this.rightArrowStartX = availWidth - this.arrowWidth;
				this.rightArrowEndX = availWidth;
		//#ifdef polish.css.exclusiveview-arrow-position
			} else if (this.arrowPosition == POSITION_RIGHT ){
				this.leftArrowStartX = availWidth - completeArrowWidth + this.paddingHorizontal;
				this.leftArrowEndX = this.leftArrowStartX + this.arrowWidth;
				this.rightArrowStartX = availWidth - this.arrowWidth;
				this.rightArrowEndX = availWidth;
			} else {
				this.leftArrowStartX = 0;
				this.leftArrowEndX = this.arrowWidth;
				this.rightArrowStartX = this.arrowWidth + this.paddingHorizontal;
				this.rightArrowEndX = this.rightArrowStartX + this.arrowWidth;
			}
		//#endif
		availWidth -= completeArrowWidth;
		int selectedItemHeight = 0;
		if (selectedItemIndex < parent.size() ) {
			ChoiceItem selectedItem = (ChoiceItem) parent.get( selectedItemIndex );
			selectedItem.drawBox = false;
			selectedItemHeight = selectedItem.getItemHeight(availWidth, availWidth, availHeight);
			this.contentWidth = selectedItem.getItemWidth( availWidth, availWidth, availHeight ) + completeArrowWidth;
			this.appearanceMode = Item.INTERACTIVE;
			this.currentItem = selectedItem;
			this.currentItemIndex = selectedItemIndex;
		} else {
			this.appearanceMode = Item.PLAIN;
			if (this.isLayoutExpand()) {
				this.contentWidth = availWidth + completeArrowWidth;
			} else {
				this.contentWidth = this.paddingHorizontal + completeArrowWidth;
			}
		}
		if (selectedItemHeight > height) {
			//#debug
			System.out.println("contentHeight = selectedItemHeight; selectedItemHeight > height: " + selectedItemHeight + ">" +  height);
			this.contentHeight = selectedItemHeight;
		} else {
			//#debug
			System.out.println("contentHeight = height; selectedItemHeight <= height: " + selectedItemHeight + "<=" +  height);
			this.contentHeight = height;
		}
		//if ( selectedItem.isFocused ) {
			//System.out.println("Exclusive Single Line View: contentHeight=" + this.contentHeight);
		//}
		//this.isInitialized = true;
		
		//#if polish.css.exclusiveview-left-arrow			
			if (this.leftArrow != null) {
				this.leftYOffset = (this.contentHeight - this.leftArrow.getHeight()) / 2; // always center vertically
			}
		//#endif
		//#if polish.css.exclusiveview-right-arrow
			if (this.rightArrow != null) {
				this.rightYOffset = (this.contentHeight - this.rightArrow.getHeight()) / 2; // always center vertically
			}
		//#endif
		//System.out.println("content: " + this.contentWidth + "x" + this.contentHeight + ", available=" + availWidth + "x" + availHeight);
		
//		System.out.println("leftX=" + this.leftArrowStartX);
//		System.out.println("rightX=" + this.rightArrowStartX);
//		System.out.println("arrowColor=" + Integer.toHexString(this.arrowColor));
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		//#ifdef polish.css.exclusiveview-expand-background
			Boolean expandBackgroundBool = style.getBooleanProperty("exclusiveview-expand-background");
			if (expandBackgroundBool != null) {
				this.isExpandBackground = expandBackgroundBool.booleanValue(); 
			}
			if (this.isExpandBackground) {
				this.expandBackground = style.background;				
			}
		//#endif
		super.setStyle(style);
		//#ifdef polish.css.exclusiveview-left-arrow
			String leftArrowUrl = style.getProperty("exclusiveview-left-arrow");
			if (leftArrowUrl != null) {
				try {
					this.leftArrow = StyleSheet.getImage( leftArrowUrl, this, true );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load left arrow image [" + leftArrowUrl + "]" + e );
				}
			}
			//#if tmp.supportPress && polish.css.exclusiveview-left-arrow-pressed
				leftArrowUrl = style.getProperty("exclusiveview-left-arrow-pressed");
				if (leftArrowUrl != null) {
					try {
						this.leftArrowPressed = StyleSheet.getImage( leftArrowUrl, null, true );
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load left arrow pressed image [" + leftArrowUrl + "]" + e );
					}
				}
			//#endif
		//#endif
		//#ifdef polish.css.exclusiveview-right-arrow
			String rightArrowUrl = style.getProperty("exclusiveview-right-arrow");
			if (rightArrowUrl != null) {
				try {
					this.rightArrow = StyleSheet.getImage( rightArrowUrl, this, true );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load right arrow image [" + rightArrowUrl + "]" + e );
				}
			}
			//#if tmp.supportPress && polish.css.exclusiveview-right-arrow-pressed
				rightArrowUrl = style.getProperty("exclusiveview-right-arrow-pressed");
				if (rightArrowUrl != null) {
					try {
						this.rightArrowPressed = StyleSheet.getImage( rightArrowUrl, null, true );
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load right arrow pressed image [" + rightArrowUrl + "]" + e );
					}
				}
			//#endif
		//#endif
		//#ifdef polish.css.exclusiveview-arrow-color
			Color colorInt = (Color) style.getObjectProperty("exclusiveview-arrow-color");
			if ( colorInt != null ) {
				this.arrowColor = colorInt.getColor();
			}
		//#endif
		//#if tmp.supportPress && polish.css.exclusiveview-arrow-color-pressed
			Color colorPressed = (Color) style.getObjectProperty("exclusiveview-arrow-color-pressed");
			if ( colorPressed != null ) {
				this.arrowColorPressed = colorPressed;
			}
		//#endif
		//#ifdef polish.css.exclusiveview-arrow-position
			Integer positionInt = style.getIntProperty("exclusiveview-arrow-position");
			if ( positionInt != null ) {
				this.arrowPosition = positionInt.intValue();
			}
			//#ifdef polish.css.exclusiveview-arrow-padding
				Integer arrowPaddingInt = style.getIntProperty("exclusiveview-arrow-padding");
				if (arrowPaddingInt != null) {
					this.arrowPadding = arrowPaddingInt.intValue();
//				} else {
//					this.arrowPadding = style.paddingHorizontal;
				}
			//#endif
		//#endif
		//#ifdef polish.css.exclusiveview-roundtrip
			Boolean allowRoundTripBool = style.getBooleanProperty("exclusiveview-roundtrip");
			if (allowRoundTripBool != null) {
				this.allowRoundTrip = allowRoundTripBool.booleanValue();
			}
		//#endif

	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder,
			Graphics g) 
	{
		//#debug
		System.out.println("ExclusiveView.start: x=" + x + ", y=" + y + ", leftBorder=" + leftBorder + ", rightBorder=" + rightBorder );
		//this.xStart = x;
		int modifiedX = x;
//		//#ifdef polish.css.exclusiveview-expand-background
//			if (!this.isExpandBackground && this.expandBackground != null) {
//				if (this.currentItem != null && this.currentItem.background == this.expandBackground) {
//					this.currentItem.background = null;
//				}
//				//this.background.paint(x, y, this.contentWidth, this.contentHeight, g);
//				this.expandBackground.paint(leftBorder, y, rightBorder-leftBorder, this.contentHeight, g);
//			}
//		//#endif

		//#ifdef polish.css.exclusiveview-arrow-position
			if (this.arrowPosition == POSITION_BOTH_SIDES ) {
		//#endif
				modifiedX += this.arrowWidth + this.paddingHorizontal;
				leftBorder += this.arrowWidth + this.paddingHorizontal;
				rightBorder -= this.arrowWidth + this.paddingHorizontal;
		//#ifdef polish.css.exclusiveview-arrow-position
			} else if (this.arrowPosition == POSITION_LEFT ) {
				modifiedX += (this.arrowWidth + this.paddingHorizontal) << 1;
				leftBorder += (this.arrowWidth + this.paddingHorizontal) << 1;
				rightBorder -= (this.arrowWidth + this.paddingHorizontal) << 1;
			}
		//#endif	

		//#ifdef polish.css.horizontalview-expand-background
			if (!this.isExpandBackground && this.expandBackground != null) {
				this.expandBackground.paint(modifiedX, y, rightBorder-leftBorder, this.contentHeight, g);
			}
		//#endif
			
		//#debug
		System.out.println("ExclusiveView.item: x=" + modifiedX + ", y=" + y + ", leftBorder=" + leftBorder + ", rightBorder=" + rightBorder + ", availableWidth=" + (rightBorder - leftBorder) + ", itemWidth=" + this.currentItem.itemWidth  );
		if (this.currentItem != null) {
			this.currentItem.paint(modifiedX, y, leftBorder, rightBorder, g);
		}

		g.setColor( this.arrowColor );
		//draw left arrow:
		//#ifdef polish.css.exclusiveview-roundtrip
			if (this.allowRoundTrip || this.currentItemIndex > 0) {
		//#else
			//# if (this.currentItemIndex > 0) {
		//#endif
			// draw left arrow
			int startX = x + this.leftArrowStartX;
			if (startX >= rightBorder) {
				//TODO this is  a hack for cases where the label is staying on the same line as this view:
				startX = rightBorder - (this.arrowWidth << 1);
			}
			
			//#ifdef polish.css.exclusiveview-left-arrow
				Image arrowImg = this.leftArrow;
				//#if tmp.supportPress && polish.css.exclusiveview-left-arrow-pressed
					if (this.isLeftArrowPressed && this.leftArrowPressed != null) {
						arrowImg = this.leftArrowPressed;
					}
				//#endif
				if (arrowImg != null) {
					//System.out.println("Drawing left IMAGE arrow at " + startX );
					g.drawImage( arrowImg, startX, y + this.leftYOffset, Graphics.LEFT | Graphics.TOP );
				} else {
			//#endif
				//#if tmp.supportPress && polish.css.exclusiveview-arrow-color-pressed
					if (this.isLeftArrowPressed && this.arrowColorPressed != null) {
						g.setColor( this.arrowColorPressed.getColor() );
					}
				//#endif
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
			//#ifdef polish.css.exclusiveview-left-arrow
				}
			//#endif
		}
		
		// draw right arrow:
		//#ifdef polish.css.exclusiveview-roundtrip
			if (this.allowRoundTrip ||  (this.currentItemIndex < this.parentContainer.size() - 1) ) {
		//#else
			//# if (this.currentItemIndex < this.parentContainer.size() - 1) {
		//#endif
			// draw right arrow
			int startX = x + this.rightArrowStartX;
			if (startX >= rightBorder) {
				//TODO this is  a hack for cases where the label is staying on the same line as this view:
				startX = rightBorder; // - this.arrowWidth;
			}
			//#ifdef polish.css.exclusiveview-right-arrow
				Image arrowImg = this.rightArrow;
				//#if tmp.supportPress && polish.css.exclusiveview-right-arrow-pressed
					if (this.isRightArrowPressed && this.rightArrowPressed != null) {
						arrowImg = this.rightArrowPressed;
					}
				//#endif
				if (arrowImg != null) {
					g.drawImage( arrowImg, startX, y + this.rightYOffset, Graphics.LEFT | Graphics.TOP );
				} else {
			//#endif
				//#if tmp.supportPress && polish.css.exclusiveview-arrow-color-pressed
					if (this.isRightArrowPressed && this.arrowColorPressed != null) {
						g.setColor( this.arrowColorPressed.getColor() );
					} else {
						g.setColor( this.arrowColor );
					}
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
			//#ifdef polish.css.exclusiveview-right-arrow
				}
			//#endif
		}
	}
	
	
	//#if polish.blackberry && tmp.supportPress
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		if ( this.bbReleaseArrow && (currentTime - this.bbKeyReleaseTime) > 200 ) {
			this.bbReleaseArrow = false;
			this.isLeftArrowPressed = false;
			this.isRightArrowPressed = false;
			addFullRepaintRegion(this.parentItem, repaintRegion);
		}

	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#getNextItem(int, int)
	 */
	protected Item getNextItem(int keyCode, int gameAction) {
		//#debug
		System.out.println("ExclusiveSingleLineView: getNextItem()");
		ChoiceGroup choiceGroup = (ChoiceGroup) this.parentContainer;
		Item[] items = this.parentContainer.getItems();
		if (this.currentItem == null) {
			//#debug warn
			System.out.println("ExclusiveSingleLineView: getNextItem(): no current item defined, it seems the initContent() has been skipped.");
			this.currentItemIndex = choiceGroup.getSelectedIndex();
			this.currentItem = (ChoiceItem) items[ this.currentItemIndex ];
		}
		Item lastItem = this.currentItem;
		//ChoiceItem currentItem = (ChoiceItem) items[ this.currentItemIndex ];
		if ( gameAction == Canvas.LEFT && 
				(
					//#ifdef polish.css.exclusiveview-roundtrip
						this.allowRoundTrip || 
					//#endif
						this.currentItemIndex > 0 
				)
		) {
			//#if tmp.supportPress
				this.isLeftArrowPressed = true;
			//#endif
			this.currentItem.select( false );
			this.currentItemIndex--;
			//#ifdef polish.css.exclusiveview-roundtrip
				if (this.currentItemIndex < 0) {
					this.currentItemIndex = items.length - 1;
				}
			//#endif
			this.currentItem = (ChoiceItem) items[ this.currentItemIndex ];
			this.currentItem.adjustProperties( lastItem );
			//this.currentItem.select( true );
			choiceGroup.setSelectedIndex( this.currentItemIndex, true );
			choiceGroup.notifyStateChanged();
			
			return this.currentItem;
		} else if ( gameAction == Canvas.RIGHT && 
				(
			//#ifdef polish.css.exclusiveview-roundtrip
						this.allowRoundTrip || 
			//#endif
						this.currentItemIndex < items.length - 1  
				)
		) {
			//#if tmp.supportPress
				this.isRightArrowPressed = true;
			//#endif
			this.currentItem.select( false );
			this.currentItemIndex++;
			//#ifdef polish.css.exclusiveview-roundtrip
				if (this.currentItemIndex >= items.length) {
					this.currentItemIndex = 0;
				}
			//#endif
			this.currentItem = (ChoiceItem) items[ this.currentItemIndex ];
			this.currentItem.adjustProperties( lastItem );
			choiceGroup.setSelectedIndex( this.currentItemIndex, true );
			choiceGroup.notifyStateChanged();
			//this.currentItem.select( true );
			
			return this.currentItem;
		}
		// in all other cases there is no next item:
		return null;
	}
	
	//#if tmp.supportPress
		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemView#handleKeyReleased(int, int)
		 */
		public boolean handleKeyReleased(int keyCode, int gameAction) {
			//#if polish.blackberry
				this.bbReleaseArrow = this.isLeftArrowPressed || this.isRightArrowPressed;
				this.bbKeyReleaseTime = System.currentTimeMillis();
				//# return super.handleKeyReleased(keyCode, gameAction);
			//#else
				this.isLeftArrowPressed = false;
				this.isRightArrowPressed = false;
				return true;
			//#endif
		}
	//#endif
	

	//#ifdef polish.hasPointerEvents
	/**
	 * Handles pointer pressed events.
	 * This is an optional feature that doesn't need to be implemented by subclasses.
	 * 
	 * @param x the x position of the event
	 * @param y the y position of the event
	 * @return true when the event has been handled. When false is returned the parent container
	 *         will forward the event to the affected item.
	 */
	public boolean handlePointerPressed(int x, int y) {
		if ( !this.parentContainer.isInItemArea(x, y) ) {
			return false;
		}
		//#if tmp.supportPress
	        x -= this.parentContainer.getContentX();
			int index = this.currentItemIndex;
			if ( (index > 0 || this.allowRoundTrip) && 
				 (	
					//#ifdef polish.css.exclusiveview-arrow-position
						 (
								 (this.arrowPosition == POSITION_BOTH_SIDES) &&
					//#endif
						 (x < this.contentWidth / 2)
					//#ifdef polish.css.exclusiveview-arrow-position
						 ) 
						 || 	(x >= this.leftArrowStartX  && x <= this.leftArrowEndX) 
					//#endif
				 ) 
			) {
				// go left:
				this.isLeftArrowPressed = true;
			} else 
				//#ifdef polish.css.exclusiveview-arrow-position
					if (! (x >= this.leftArrowStartX  && x <= this.leftArrowEndX && index > 0))
				//#endif
			{
				// go rigth:
				this.isRightArrowPressed = true;
			}
			//#if polish.blackberry
				this.bbReleaseArrow = false;
			//#endif
		//#endif
		
		return true;
	}
	//#endif
	
	
	//#ifdef polish.hasPointerEvents
	/**
	 * Handles pointer pressed events.
	 * This is an optional feature that doesn't need to be implemented by subclasses.
	 * 
	 * @param x the x position of the event
	 * @param y the y position of the event
	 * @return true when the event has been handled. When false is returned the parent container
	 *         will forward the event to the affected item.
	 */
	public boolean handlePointerReleased(int x, int y) {
		if ( !this.parentContainer.isInItemArea(x, y) ) {
			return false;
		}
		//#if tmp.supportPress
			this.isLeftArrowPressed = false;
			this.isRightArrowPressed = false;
		//#endif
        x -= this.parentContainer.getContentX();
		Item[] items = this.parentContainer.getItems();
		this.currentItem.select( false );
		int index = this.currentItemIndex;
		if ( (index > 0 || this.allowRoundTrip) && 
			 (	
				//#ifdef polish.css.exclusiveview-arrow-position
					 (
							 (this.arrowPosition == POSITION_BOTH_SIDES) &&
				//#endif
					 (x < this.contentWidth / 2)
				//#ifdef polish.css.exclusiveview-arrow-position
					 ) 
					 || 	(x >= this.leftArrowStartX  && x <= this.leftArrowEndX) 
				//#endif
			 ) 
		) {
			index--;
			if (index < 0) {
				index = items.length - 1;
			}
		} else 
			//#ifdef polish.css.exclusiveview-arrow-position
				if (! (x >= this.leftArrowStartX  && x <= this.leftArrowEndX && index > 0))
			//#endif
		{
			index = ( index + 1) % items.length;
		}
		this.currentItemIndex = index;
		this.currentItem = (ChoiceItem) items[ index ];
		//this.currentItem.select( true );
		((ChoiceGroup) this.parentContainer).setSelectedIndex( this.currentItemIndex, true );
		this.parentContainer.focusChild(this.currentItemIndex, this.currentItem, 0, true);
		this.parentContainer.notifyStateChanged();
		return true;
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		//#ifdef polish.css.exclusiveview-expand-background
			if (this.expandBackground != null ) {
				this.parentContainer.background = this.expandBackground;
				this.expandBackground = null;
			}
		//#endif	
		//#if tmp.supportPress
			this.isLeftArrowPressed = false;
			this.isRightArrowPressed = false;
		//#endif
		super.defocus(originalStyle);
	}

	//#ifdef polish.css.exclusiveview-expand-background
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.ContainerView#focus(de.enough.polish.ui.Style, int)
		 */
		public void focus(Style focusstyle, int direction) {
			if (!this.isExpandBackground) {
				Background bg = this.parentContainer.background;
				if (bg != null) {
					this.expandBackground = bg; 
					this.parentContainer.background = null;
				}
			}
			super.focus(focusstyle, direction);
		}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#isValid(de.enough.polish.ui.Item, de.enough.polish.ui.Style)
	 */
	protected boolean isValid(Item parent, Style style) {
		return (parent instanceof ChoiceGroup);
	}

	

}
