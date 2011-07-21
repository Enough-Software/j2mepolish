//#condition polish.usePolishGui
/*
 * Created on Nov 20, 2009 at 12:25:57 PM.
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

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.FocusListener;
import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Point;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Displays embedded all items in different tabs.</p>
 * <p>usage:</p>
 * <pre>
 * .myList {
 *    view-type: tabbed;
 *    tabbedview-icon: url( tab.png );
 *    tabbedview-icon-hover: url( tabHover.png );
 *    tabbedview-tabbar-position: bottom;
 *    tabbedview-tabbar-style: .myListTabs; 
 *    tabbedview-tab-style: .myListTab; 
 * }
 * 
 * .myListTabs {
 * 	  view-type: horizontal;
 * 	  layout: center;
 * }
 * 
 * .myListTab {
 * }
 * .myListTab:hover {
 * 	 background-color: red;
 * }
 * </pre>
 * 
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TabbedContainerView 
extends ContainerView
implements FocusListener
{

	/**
	 * Positions the tabs above the items.
	 */
	public static final int POSITION_TOP = 0;
	/**
	 * Positions the tabs below the items.
	 */
	public static final int POSITION_BOTTOM = 1;
	
	private String iconUrl;
	private String iconHoverUrl;
	private int tabbarPosition = POSITION_TOP;
	private Style tabbarStyle;
	private transient Container tabbarContainer;
	private Style tabStyle;
	private boolean isTabbarInitRequired;
	private boolean isRoundtrip = true;

	/**
	 * Creates a new tabbed view-type
	 */
	public TabbedContainerView() {
		super();
		this.allowsAutoTraversal = false;
		this.isHorizontal = true;
		this.isVertical = false;
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Item, int, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth,
			int availWidth, int availHeight) 
	{
		Container parent = (Container) parentContainerItem;
		
		// init items:
		Item[] children = parent.getItems();
		int maxWidth = 0;
		int maxHeight = 0;
		for (int i = 0; i < children.length; i++) {
			Item item = children[i];
			if (item.getItemWidth(firstLineWidth, availWidth, availHeight) > maxWidth) {
				maxWidth = item.itemWidth;
			}
			if (item.itemHeight > maxHeight) {
				maxHeight = item.itemHeight;
			}
		}
		// init tabs:
		Container tabbar = this.tabbarContainer;
		if (tabbar == null) {
			tabbar = new Container(false, this.tabbarStyle);
			tabbar.setParent(parent);
			tabbar.setFocusListener(this);
			this.tabbarContainer = tabbar;
		}
		if (tabbar.size() != children.length) {
			tabbar.clear();
			Image img = null;
			if (this.iconUrl != null) {
				try {
					img = StyleSheet.getImage(this.iconUrl, this, false );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load tabbed-icon " + this.iconUrl + e);
				}
			}
			Image hoverImg = null;
			if (this.iconHoverUrl != null) {
				try {
					hoverImg = StyleSheet.getImage(this.iconHoverUrl, this, false );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load tabbed-icon-hover " + this.iconHoverUrl + e);
				}
			}
			
			for (int i = 0; i < children.length; i++) {
				IconItem tab = new IconItem(null, img, this.tabStyle);
				tab.setHoverImage(hoverImg);
				tabbar.add(tab);
			}
			UiAccess.init(tabbar, firstLineWidth, availWidth, availHeight - (maxHeight + this.paddingVertical));
		} else if (this.isTabbarInitRequired) {
			UiAccess.init(tabbar, firstLineWidth, availWidth, availHeight - (maxHeight + this.paddingVertical));
			this.isTabbarInitRequired = false;
		}
		// layout tabbar:
		boolean displayTabs = (children.length > 1); // only display tabs when there is more than 1 item
		int y = 0;
		if (this.tabbarPosition == POSITION_BOTTOM) {
			tabbar.relativeY = maxHeight + this.paddingVertical;
		} else if (displayTabs) { 
			tabbar.relativeY = 0;
			y = tabbar.itemHeight + this.paddingVertical;  
		}
		//System.out.println("maxWidth=" + maxWidth + ", availWidth=" + availWidth + ", tabbar.itemWidth=" + tabbar.itemWidth);
		if (displayTabs && (maxWidth < tabbar.itemWidth)) {
			maxWidth = tabbar.itemWidth;
		}
		if (tabbar.isLayoutCenter()) {
			tabbar.relativeX = (maxWidth - tabbar.itemWidth) / 2;
		} else if (tabbar.isLayoutRight()) {
			tabbar.relativeX = (maxWidth - tabbar.itemWidth);
		} else {
			tabbar.relativeX = 0;
		}
		
		// layout children:
		for (int i = 0; i < children.length; i++) {
			Item item = children[i];
			item.relativeY = y;
			if (item.itemHeight < maxHeight) {
				if (item.isLayoutVerticalCenter()) {
					item.relativeY += (maxHeight - item.itemHeight) / 2;
				} else if (item.isLayoutBottom()) {
					item.relativeY += (maxHeight - item.itemHeight);
				}
			}
			item.relativeX = 0;
			if (item.itemWidth < maxWidth) {
				if (item.isLayoutCenter()) {
					item.relativeX = (maxWidth - item.itemWidth) / 2;
				} else if (item.isLayoutRight()) {
					item.relativeX = (maxWidth - item.itemWidth);
				}
			}
		}
		
		// focus tab:
		int index = this.focusedIndex;
		if (index == -1) {
			index = 0;
		}
		if (index < tabbar.size()) {
			Container rootContainer = getScreen().getRootContainer();
			int offset = rootContainer.getScrollYOffset();
			tabbar.focusChild(index);
			rootContainer.setScrollYOffset(offset, false);
		}
		
		// set size:
		this.contentWidth = maxWidth;
		if (displayTabs) {
			this.contentHeight = maxHeight + this.paddingVertical + tabbar.itemHeight;
		} else {
			this.contentHeight = maxHeight;
		}
		this.appearanceMode = Item.INTERACTIVE;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, 
			Item[] myItems, 
			int x, int y, int leftBorder, int rightBorder, 
			int clipX, int clipY, int clipWidth, int clipHeight, 
			Graphics g) 
	{
		int index = this.focusedIndex;
		if (index == -1) {
			index = 0;
			if (myItems.length == 0) {
				return;
			}
		}
		if (myItems.length > 1) {
			Container tb = this.tabbarContainer;
			tb.paint(x + tb.relativeX, y + tb.relativeY, x + tb.relativeX, x + tb.relativeX + tb.itemWidth, g);
		}
		Item item = myItems[index];
		paintItem(item, index, x + item.relativeX, y + item.relativeY, x, x + item.relativeX + item.itemWidth, clipX, clipY, clipWidth, clipHeight, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.tabbedview-icon
			String url = style.getProperty("tabbedview-icon");
			if (url != null) {
				this.iconUrl = url;
			}
		//#endif
		//#if polish.css.tabbedview-icon-hover
			String urlHover = style.getProperty("tabbedview-icon-hover");
			if (urlHover != null) {
				this.iconHoverUrl = urlHover;
			}
		//#endif
		//#if polish.css.tabbedview-roundtrip
			Boolean roundtripBool = style.getBooleanProperty("tabbedview-roundtrip");
			if (roundtripBool != null) {
				this.isRoundtrip  = roundtripBool.booleanValue();
			}
		//#endif
		//#if polish.css.tabbedview-tabbar-position
			Integer tabPositionInt = style.getIntProperty("tabbedview-tabbar-position");
			if (tabPositionInt != null) {
				this.tabbarPosition  = tabPositionInt.intValue();
			}
		//#endif
		//#if polish.css.tabbedview-tabbar-style
			Style tabbarStyleObj = (Style) style.getObjectProperty("tabbedview-tabbar-style");
			if (tabbarStyleObj != null) {
				this.tabbarStyle = tabbarStyleObj;
				if (this.tabbarContainer != null) {
					this.tabbarContainer.setStyle(tabbarStyleObj);
				}
			}
		//#endif
		//#if polish.css.tabbedview-tab-style
			Style tabStyleObj = (Style) style.getObjectProperty("tabbedview-tab-style");
			if (tabStyleObj != null) {
				this.tabStyle = tabStyleObj;
				if (this.tabbarContainer != null) {
					int index = this.tabbarContainer.getFocusedIndex();
					this.tabbarContainer.focusChild(-1);
					for (int i=0; i<this.tabbarContainer.size(); i++ ) {
						Item tab = this.tabbarContainer.get(index);
						if (tab != null) {
							tab.setStyle(tabStyleObj);
						}
					}
					this.tabbarContainer.focusChild(index);
				}
			}
		//#endif
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#handlePointerPressed(int, int)
	 */
	public boolean handlePointerPressed(int x, int y) {
		Point p = adjustToContentArea(x, y);
		Container tabbar = this.tabbarContainer;
		boolean handled = UiAccess.handlePointerPressed( tabbar, p.x - tabbar.relativeX, p.y - tabbar.relativeY );
		return handled || super.handlePointerPressed(x, y);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#handlePointerReleased(int, int)
	 */
	public boolean handlePointerReleased(int x, int y) {
		Point p = adjustToContentArea(x, y);
		Container tabbar = this.tabbarContainer;
		boolean handled = UiAccess.handlePointerReleased( tabbar, p.x - tabbar.relativeX, p.y - tabbar.relativeY );
		return handled || super.handlePointerReleased(x, y);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#handlePointerDragged(int, int, de.enough.polish.ui.ClippingRegion)
	 */
	public boolean handlePointerDragged(int x, int y, ClippingRegion repaintRegion) {
		Point p = adjustToContentArea(x, y);
		Container tabbar = this.tabbarContainer;
		boolean handled = UiAccess.handlePointerDragged( tabbar, p.x - tabbar.relativeX, p.y - tabbar.relativeY, repaintRegion );
		return handled || super.handlePointerDragged(x, y, repaintRegion);
	}


	//#ifdef polish.hasTouchEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#handlePointerTouchDown(int, int)
	 */
	public boolean handlePointerTouchDown(int x, int y) {
		Point p = adjustToContentArea(x, y);
		Container tabbar = this.tabbarContainer;
		boolean handled = tabbar.handlePointerTouchDown( p.x - tabbar.relativeX, p.y - tabbar.relativeY );
		return handled || super.handlePointerTouchDown(x, y);
	}
	//#endif

	//#ifdef polish.hasTouchEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#handlePointerTouchUp(int, int)
	 */
	public boolean handlePointerTouchUp(int x, int y) {
		Point p = adjustToContentArea(x, y);
		Container tabbar = this.tabbarContainer;
		boolean handled = tabbar.handlePointerTouchUp( p.x - tabbar.relativeX, p.y - tabbar.relativeY );
		return handled || super.handlePointerTouchUp(x, y);
	}
	//#endif
	
	

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#getNextItem(int, int)
	 */
	protected Item getNextItem( int keyCode, int gameAction ) 
	{
		//#if polish.css.tabbedview-roundtrip
		if (!this.isRoundtrip) {
			if (gameAction == Canvas.LEFT && this.focusedIndex <= 0) {
				return null;
			} else if (gameAction == Canvas.RIGHT && this.focusedIndex == this.parentContainer.size() - 1) {
				return null;
			}
		}
		//#endif
		Item next = super.getNextItem(keyCode, gameAction);
		if (next != null) {
			this.tabbarContainer.focusChild(this.focusedIndex);
		}
		return next;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		this.tabbarContainer.animate(currentTime, repaintRegion);
		super.animate(currentTime, repaintRegion);
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.FocusListener#onFocusChanged(de.enough.polish.ui.Container, de.enough.polish.ui.Item, int)
	 */
	public void onFocusChanged(Container parent, Item focItem, int focIndex) 
	{
		if (focIndex != -1 && focIndex != this.focusedIndex) {
			this.parentContainer.focusChild(focIndex, focItem, 0, false);
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#onScreenSizeChanged(int, int)
	 */
	protected void onScreenSizeChanged(int screenWidth, int screenHeight) {
		super.onScreenSizeChanged(screenWidth, screenHeight);
		this.isTabbarInitRequired = true;
	}
	

}
