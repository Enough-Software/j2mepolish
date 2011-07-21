//#condition polish.usePolishGui
/*
 * Created on May 17, 2008 at 2:04:13 AM.
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

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;

/**
 * <p>Arranges the items either in single rows or in a table layout.</p>
 * <p>Activate this view by specifying <code>view-type: remove-text</code> in the ChoiceGroup's, Container's or List's style.</p>
 * <p>Further attributes are:</p>
 * <ul>
 *  <li><b>view-remove-text</b>: removes the text of embedded items and only shows the currently selected one</li>
 *  <li><b>view-remove-text-position</b>: either top or bottom</li>
 *  <li><b>show-text-in-title</b>: uses the text of embedded items for the screen title instead of displaying it under the item</li>
 *  <!--
 *  <li><b></b>: </li>
 *  -->
 * </ul>
 * <p>Example:
 * <pre>
 * .myList {
 * 		view-type: remove-text;
 * 		view-remove-text: true;
 *      view-remove-text-position: top;
 *      background-color: green;
 *      padding: 5;
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software - 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RemoveTextContainerView extends ContainerView {

	private final static int POSITION_BOTTOM = 0;
	private final static int POSITION_TOP = 1;
	protected boolean isRemoveText = true;
	protected boolean isShowTextInTitle;
	protected String[] labels;
	protected transient StringItem removeTextItem;
	//#if polish.css.view-remove-text-position
		private int removeTextPosition;
	//#endif
	//#if polish.css.view-remove-text-style
		private Style removeTextStyle;
	//#endif

	
	/**
	 * Creates a new fish eye view
	 */
	public RemoveTextContainerView() {
		// use styles for finetuning behavior
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight) {
		this.isVertical = false;
		this.isHorizontal = true;
		Container parent = (Container) parentContainerItem;		
		//#debug
		System.out.println("RemoveText: intialising content for " + this + " with vertical-padding " + this.paddingVertical );

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
		}

		if (this.isRemoveText && this.removeTextItem == null 
			//#if polish.css.show-text-in-title
				&& !this.isShowTextInTitle
			//#endif
			)
		{
			this.removeTextItem = new StringItem(null, null);
			//#if polish.css.view-remove-text-style
				if (this.removeTextStyle  != null) {
					this.removeTextItem.setStyle( this.removeTextStyle );
				}
			//#endif
		}

		if (this.isRemoveText && (this.labels == null || this.labels.length != length)) {
			this.labels = new String[ length ];
		}

		String longestText = null;
		if (this.isRemoveText) {
			for (int i = 0; i < length; i++) {
				Item item = myItems[i];
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
				if (text == null) {
					text = this.labels[i];
				}
				if (text != null) { 
					if (longestText == null || longestText.length() < text.length()) {
						longestText = text;
					}
				}
			}
		}
		super.initContent( parentContainerItem, firstLineWidth, availWidth, availHeight );

		if (this.removeTextItem != null) {
			int height;
			if (this.isFocused && this.removeTextItem.getText() == null && this.focusedIndex != -1 && this.focusedItem != null) {
				this.removeTextItem.setText( this.labels[ this.focusedIndex ] );
				//#if polish.css.view-remove-text-style
					if (this.removeTextStyle == null) {
				//#endif
						if (this.removeTextItem.getStyle() != this.focusedItem.getStyle() ) {
							this.removeTextItem.setStyle( this.focusedItem.getStyle() );
							removeItemBackground( this.removeTextItem );
							removeItemBorder( this.removeTextItem );
						}						
				//#if polish.css.view-remove-text-style
					}
				//#endif
			}			
			if (this.removeTextItem.getText() == null) {
				this.removeTextItem.setText(longestText);
				height = this.removeTextItem.getItemHeight(availWidth, availWidth, availHeight);
				this.removeTextItem.setText(null);
			} else {
				height = this.removeTextItem.getItemHeight(availWidth, availWidth, availHeight);
			}
			//#if polish.css.view-remove-text-position
				if (this.removeTextPosition == POSITION_BOTTOM) {
			//#endif
					this.removeTextItem.relativeY = this.contentHeight + this.paddingVertical;
			//#if polish.css.view-remove-text-position
				} else {
					for (int i = 0; i < length; i++) {
						Item item = myItems[i];
						item.relativeY += height;
					}
				}
			//#endif
			this.contentHeight += height + this.paddingVertical;
			if (this.removeTextItem.itemWidth > this.contentWidth) {
				this.contentWidth = this.removeTextItem.itemWidth;
			}
		}
	}




	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#focusItem(int, de.enough.polish.ui.Item, int, de.enough.polish.ui.Style)
	 */
	public Style focusItem(int focIndex, Item item, int direction, Style focStyle) {
		if (this.labels == null) {
			return super.focusItem(focIndex, item, direction, focStyle);
		}
		if (this.isRemoveText) {
			if (this.isShowTextInTitle) {
				Screen scr = getScreen();
				if (scr != null) {
					scr.setTitle( this.labels[ focIndex ] );
				}
			} else if (this.removeTextItem != null) {
				this.removeTextItem.setText( this.labels[ focIndex ] );
				//#if polish.css.view-remove-text-style
					if (this.removeTextStyle == null) {
				//#endif
						if (this.removeTextItem.getStyle() != item.getStyle() ) {
							this.removeTextItem.setStyle( item.getStyle() );
							removeItemBackground( this.removeTextItem );
							removeItemBorder( this.removeTextItem );
						}						
				//#if polish.css.view-remove-text-style
					}
				//#endif
			}
		}
		return super.focusItem(focIndex, item, direction, focStyle);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		if (this.removeTextItem != null) {
			this.removeTextItem.paint( x + this.removeTextItem.relativeX, y + this.removeTextItem.relativeY, leftBorder, rightBorder, g );
		}
		super.paintContent(container, myItems, x, y, leftBorder, rightBorder, clipX, clipY, clipWidth, clipHeight, g);
	}
	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.view-remove-text
			Boolean removeTextBool = style.getBooleanProperty("view-remove-text");
			if (removeTextBool != null) {
				this.isRemoveText = removeTextBool.booleanValue();
			}
		//#endif
		//#if polish.css.view-remove-text-position
			Integer removeTextPositionObj = style.getIntProperty("view-remove-text-position");
			if (removeTextPositionObj != null) {
				this.removeTextPosition = removeTextPositionObj.intValue();
			}
		//#endif
		//#if polish.css.view-remove-text-style
			Style removeTextStyleObj = (Style) style.getObjectProperty("view-remove-text-style");
			if (removeTextStyleObj != null) {
				this.removeTextStyle = removeTextStyleObj;
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
	}

	

//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.ItemView#focus(de.enough.polish.ui.Style, int)
//	 */
//	public void focus(Style focusstyle, int direction)
//	{
//		super.focus(focusstyle, direction);
//		//#if polish.css.view-remove-text-style
//			if (this.removeTextItem != null && this.removeTextStyle != null) {
//				UiAccess.focus( this.removeTextItem, direction, null );
//			}
//		//#endif
//	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle)
	{
		super.defocus(originalStyle);
		if (this.removeTextItem != null) {
//			//#if polish.css.view-remove-text-style
//				if (this.removeTextStyle  != null) {
//					UiAccess.defocus(this.removeTextItem, this.removeTextStyle );
//				} else {
//			//#endif
					this.removeTextItem.setText(null);
//			//#if polish.css.view-remove-text-style
//				}
//			//#endif
		}
	}

	
	
}
