//#condition polish.usePolishGui
/*
 * Created on 23-Jan-2005 at 19:04:14.
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
package de.enough.polish.ui;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.ArrayList;


/**
 * <p>Manages and paints the tabs of a tabbed form (or another Screen).</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        23-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TabBar extends Item {

	private final ArrayList tabs;
	private Style activeStyle;
	private Style activeFocusedStyle;
	private Style inactiveStyle;
	private Style activeStyleUsed;

	private int activeTabIndex;
	//#ifdef polish.hasPointerEvents
	protected boolean handlePointerReleaseEvent;
	//#endif
	private int xOffset;
	private int scrollArrowHeight = 10;
	private int scrollArrowPadding = 2;
	private int scrollArrowColor = 0xffffff;
	//#ifdef polish.css.tabbar-left-arrow
	private Image leftArrow;
	//#endif
	//#ifdef polish.css.tabbar-right-arrow
	private Image rightArrow;
	//#endif
	private int arrowYOffset;
	private int arrowXOffset;
	private boolean allowRoundtrip;
	private int nextTabIndex;


	/**
	 * Creates a new tab bar.
	 * 
	 * @param tabNames the names of the tabs
	 * @param tabImages the images of the tabs, can be null
	 */
	public TabBar(String[] tabNames, Image[] tabImages) {
		this( tabNames, tabImages, null );
	}

	/**
	 * Creates a new tab bar.
	 * 
	 * @param tabNames the names of the tabs
	 * @param tabImages the images of the tabs, can be null
	 * @param style the style of the bar
	 */
	public TabBar(String[] tabNames, Image[] tabImages, Style style) {
		super( null, 0, Item.INTERACTIVE, style);
		if (tabImages == null) {
			tabImages = new Image[ tabNames.length ];
		} else if (tabNames == null) {
			tabNames = new String[ tabImages.length ];
		}
		// getting styles:
		//#if !polish.LibraryBuild
			//#style activetab, tab, default
			//# this.activeStyle = ();
			//#style activefocusedtab, activetab, tab, default
			//# this.activeFocusedStyle = ();
			//#style inactivetab, tab, default
			//# this.inactiveStyle = ();
		//#endif

		this.tabs = new ArrayList( tabNames.length );
		for (int i = 0; i < tabImages.length; i++) {
			String name = tabNames[i];
			Image image = tabImages[i];
			IconItem tab = new IconItem( name, image, this.inactiveStyle );
			tab.parent = this;
			this.tabs.add( tab );
		}

		this.activeStyleUsed = this.activeStyle;
		((Item)this.tabs.get(0)).style = this.activeStyleUsed;
	}

	/**
	 * Changes the active/selected tab.
	 * 
	 * @param index the index of the active tab, the first tab has the index 0.
	 */
	public void setActiveTab( int index ) {
		// deactivating the old tab:
		((Item)this.tabs.get( this.activeTabIndex )).setStyle(this.inactiveStyle);
		// activating the new tab:
		this.activeTabIndex = index;
		((Item)this.tabs.get( index )).setStyle(this.activeStyleUsed);
		setInitialized(false);
		this.nextTabIndex = index;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style newStyle, int direction)
	{
		this.activeStyleUsed = this.activeFocusedStyle;
		((Item)this.tabs.get( this.activeTabIndex )).setStyle(this.activeFocusedStyle);
		return super.focus(newStyle, direction);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle)
	{
		this.activeStyleUsed = this.activeStyle;
		((Item)this.tabs.get( this.activeTabIndex )).setStyle(this.activeStyle);
		super.defocus(originalStyle);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		int arrowWidth = this.scrollArrowHeight + 2 * this.scrollArrowPadding;
		//#debug
		System.out.println("init of TabBar lineWidth=" + availWidth + ", arrowWidth=" + arrowWidth + ", number of tabs=" + this.tabs.size() );
		int maxHeight = arrowWidth;
		int completeWidth = 0;
		int rightBorder = availWidth - arrowWidth;
		int startX = 0;
		if (
			//#if polish.css.tabbar-roundtrip
				this.allowRoundtrip ||
			//#endif
			(this.activeTabIndex > 0 )
		){
			startX = arrowWidth;
		};
		if (
		//#if polish.css.tabbar-roundtrip
			!this.allowRoundtrip && 
		//#endif
			(this.activeTabIndex == 0 || this.activeTabIndex == this.tabs.size() -1)) 
		{
			// only one scroll indicator needs to be painted
			if (this.activeTabIndex != 0 
			) {
				rightBorder = availWidth;
			}
			availWidth -= maxHeight;
			completeWidth = maxHeight;
		} else {
			availWidth -= 2 * maxHeight;
			completeWidth = 2 * maxHeight;
		}
//		availWidth -= 2 * maxHeight;
//		completeWidth = maxHeight;

		int activeTabXPos = 0;
		int activeTabWidth = 0;
		int minHeight = Integer.MAX_VALUE;
		for (int i = 0; i < this.tabs.size(); i++) {
			Item tab = (Item) this.tabs.get(i);
			int tabHeight = tab.getItemHeight(firstLineWidth, availWidth, availHeight);
			if (tabHeight > maxHeight ) { 
				maxHeight = tabHeight;
			}
			if (tabHeight < minHeight) {
				minHeight = tabHeight;
			}
			if (i == this.activeTabIndex) {
				activeTabXPos = completeWidth;
				activeTabWidth = tab.itemWidth;
			}
			// I can use the itemWidth field, since I have called getItemHeight(..) above,
			// which initialises the tab.
			tab.relativeX = startX;
			tab.relativeY = 0;
			startX += tab.itemWidth;
			completeWidth += tab.itemWidth;
		}
		if (maxHeight > minHeight) {
			for (int i = 0; i < this.tabs.size(); i++) {
				Item tab = (Item) this.tabs.get(i);
				tab.relativeY = maxHeight - tab.itemHeight;
			}
		}
		this.contentHeight = maxHeight;
		this.contentWidth = completeWidth;
		if (this.activeTabIndex == 0) {
			this.xOffset = 0;
		} else if ( this.xOffset + activeTabXPos < arrowWidth ) {
			// tab is too much left:
			this.xOffset = arrowWidth - activeTabXPos;
			//System.out.println("this.xOffset + activeTabXPos < scrollerWidth ");
		} else if ( this.xOffset + activeTabXPos + activeTabWidth > rightBorder ) {
			// tab is too much right:
			//#if polish.css.tabbar-roundtrip
				if (this.allowRoundtrip) {
					this.xOffset = (rightBorder - activeTabWidth - activeTabXPos);
				} else {
			//#endif
					this.xOffset = (rightBorder - activeTabWidth) - (activeTabXPos - arrowWidth);
			//#if polish.css.tabbar-roundtrip
				}
			//#endif
			//System.out.println("this.xOffset + activeTabXPos + activeTabWidth > rightBorder");
			//System.out.println("xOffset=" + this.xOffset + ", activeTabXPos=" + activeTabXPos + ", activeTabWidth=" + activeTabWidth + ", rightBorder=" + rightBorder);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) 
	{
		// draw scrolling indicators:
		g.setColor( this.scrollArrowColor );
		int cHeight = this.contentHeight;
		y += (cHeight - this.scrollArrowHeight) / 2;
		int startX = x;
		if ( 
			//#if polish.css.tabbar-roundtrip
				this.allowRoundtrip ||
			//#endif
			(this.activeTabIndex > 0)
		) {
			// draw left scrolling indicator:
			int arrowX = x + this.scrollArrowPadding;
			//#ifdef polish.css.tabbar-left-arrow
				if (this.leftArrow != null) {
					g.drawImage(this.leftArrow, arrowX + this.arrowXOffset, y + this.arrowYOffset, Graphics.LEFT |  Graphics.TOP );
				} else {
			//#endif
				int halfWidth = this.scrollArrowHeight >> 1;
				//#ifdef polish.midp2
					g.fillTriangle(arrowX, y + halfWidth-1, arrowX + this.scrollArrowHeight, y, arrowX + this.scrollArrowHeight, y + this.scrollArrowHeight );
				//#else
					g.drawLine( arrowX, y + halfWidth-1, arrowX + this.scrollArrowHeight, y );
					g.drawLine( arrowX + this.scrollArrowHeight, y, arrowX + this.scrollArrowHeight, y + this.scrollArrowHeight);
					g.drawLine( arrowX, y + halfWidth-1, arrowX + this.scrollArrowHeight, y  + this.scrollArrowHeight );
				//#endif
			//#ifdef polish.css.tabbar-left-arrow
				}
			//#endif
			startX = arrowX + this.scrollArrowHeight + this.scrollArrowPadding;
		}
//		if (
//				//#if polish.css.tabbar-roundtrip
//					this.allowRoundtrip ||
//				//#endif
//				(this.activeTabIndex > 0) ||
//				(this.contentWidth <= this.availableWidth)
//		) {
//			x += this.scrollArrowHeight + this.scrollArrowPadding;
//		}
		if ((this.activeTabIndex < this.tabs.size() - 1)
			//#if polish.css.tabbar-roundtrip
				|| this.allowRoundtrip
			//#endif
		) {
			// draw right scrolling indicator:
			rightBorder -=  (this.scrollArrowHeight + this.scrollArrowPadding);
			//#ifdef polish.css.tabbar-right-arrow
				if (this.rightArrow != null) {
					g.drawImage(this.rightArrow, rightBorder + this.arrowXOffset, y + this.arrowYOffset, Graphics.LEFT |  Graphics.TOP );
				} else {
			//#endif
					int halfWidth = this.scrollArrowHeight / 2;
					//#ifdef polish.midp2
						g.fillTriangle(rightBorder, y, rightBorder, y  + this.scrollArrowHeight, rightBorder + this.scrollArrowHeight, y + halfWidth - 1 );
					//#else
						g.drawLine( rightBorder, y, rightBorder, y  + this.scrollArrowHeight );
						g.drawLine( rightBorder, y, rightBorder + this.scrollArrowHeight, y + halfWidth - 1);
						g.drawLine( rightBorder, y  + this.scrollArrowHeight, rightBorder + this.scrollArrowHeight, y + halfWidth - 1);
					//#endif
			//#ifdef polish.css.tabbar-right-arrow
				}
			//#endif
			rightBorder -=  this.scrollArrowPadding;
		}

		// draw the tabs:
		y -= (cHeight - this.scrollArrowHeight) / 2;
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		g.setClip( startX, y, rightBorder - startX, clipHeight);
		x += this.xOffset;
		for (int i = 0; i < this.tabs.size(); i++) {
			Item tab = (Item) this.tabs.get(i);
			tab.paint( x + tab.relativeX, y + tab.relativeY, leftBorder, rightBorder, g );
//			int tabHeight = tab.itemHeight;
//			tab.paint( x, y + (cHeight - tabHeight), leftBorder, rightBorder, g );
//			x += tab.itemWidth;
		}
		g.setClip( clipX, clipY, clipWidth, clipHeight);
	}

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "tabbar";
	}
	//#endif


	public void setStyle(Style style) {
		super.setStyle(style);
		//#ifdef polish.css.tabbar-scrolling-indicator-color
		Integer scrollColorInt = style.getIntProperty("tabbar-scrolling-indicator-color");
		if (scrollColorInt != null) {
			this.scrollArrowColor = scrollColorInt.intValue();
		}
		//#endif
		//#ifdef polish.css.tabbar-left-arrow
		String leftArrowUrl = style.getProperty("tabbar-left-arrow");
		if (leftArrowUrl != null) {
			try {
				this.leftArrow = StyleSheet.getImage(leftArrowUrl, this, false);
				this.scrollArrowHeight = this.leftArrow.getHeight();
			} catch (IOException e) {
				//#debug error
				System.out.println("Unable to load tabbar-left-arrow " + leftArrowUrl);
			}
		}
		//#endif
		//#ifdef polish.css.tabbar-right-arrow
		String rightArrowUrl = style.getProperty("tabbar-right-arrow");
		if (rightArrowUrl != null) {
			try {
				this.rightArrow = StyleSheet.getImage(rightArrowUrl, this, false);
				this.scrollArrowHeight = this.rightArrow.getHeight();
			} catch (IOException e) {
				//#debug error
				System.out.println("Unable to load tabbar-right-arrow " + rightArrowUrl);
			}
		}
		//#endif
		//#ifdef polish.css.tabbar-arrow-y-offset
		Integer arrowYOffsetInt = style.getIntProperty("tabbar-arrow-y-offset");
		if (arrowYOffsetInt != null) {
			this.arrowYOffset = arrowYOffsetInt.intValue();
		}
		//#endif
		//#ifdef polish.css.tabbar-arrow-x-offset
		Integer arrowXOffsetInt = style.getIntProperty("tabbar-arrow-x-offset");
		if (arrowXOffsetInt != null) {
			this.arrowXOffset = arrowXOffsetInt.intValue();
		}
		//#endif
		//#if polish.css.tabbar-roundtrip
		Boolean allowRoundtripBool = style.getBooleanProperty("tabbar-roundtrip");
		if (allowRoundtripBool != null) {
			this.allowRoundtrip = allowRoundtripBool.booleanValue();
		}
		//#endif
		//#if polish.css.tabbar-activetab-style
		Style actStyle = (Style) style.getObjectProperty("tabbar-activetab-style");
		if (actStyle != null) {
			this.activeStyle = actStyle; 
			this.activeStyleUsed = actStyle;
		}
		//#endif
		//#if polish.css.tabbar-inactivetab-style
		Style inactStyle = (Style) style.getObjectProperty("tabbar-inactivetab-style");
		if (inactStyle != null) {
			this.inactiveStyle = inactStyle; 
		}
		//#endif
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		int index = this.activeTabIndex;
		//#if polish.css.tabbar-roundtrip
		if (this.allowRoundtrip) {
			if (gameAction == Canvas.RIGHT) {
				index++;
				if (index >= this.tabs.size()) {
					index = 0;
				}
			} else if (gameAction == Canvas.LEFT) {
				index--;
				if (index < 0) {
					index = this.tabs.size() - 1;
				}
			}
		}
		//#else
		if (gameAction == Canvas.RIGHT && index < this.tabs.size() -1 ) {
			index++;
		} else if (gameAction == Canvas.LEFT && index > 0) {
			index--;
		}
		//#endif

		if (index != this.activeTabIndex) {
			setActiveTab(index);
			notifyStateChanged();
			return true;
		}
		return super.handleKeyPressed(keyCode, gameAction);
	}

	//#ifdef polish.hasPointerEvents
	protected boolean handlePointerPressed(int x, int y) {
		this.handlePointerReleaseEvent = false;
		if (!isInItemArea(x, y)) {
			return false;
		}
		//System.out.println( "pointer-pressed: " + x + ", " + y);
		int scrollerWidth = this.scrollArrowHeight + 2 * this.scrollArrowPadding;
		int newActiveTabIndex = this.activeTabIndex;
		if ( (this.activeTabIndex > 0 || this.allowRoundtrip) && x <= scrollerWidth ) {
			//System.out.println("left: x <= " + scrollerWidth );
			int index = this.activeTabIndex - 1;
			if (index < 0) {
				index = this.tabs.size() - 1;
			}
			newActiveTabIndex = index;
		} else if ( (this.activeTabIndex < this.tabs.size() -1 || this.allowRoundtrip) && x >= this.itemWidth - scrollerWidth) {
			//System.out.println("right: x >= " + (this.xRightPos - scrollerWidth) );
			newActiveTabIndex = (this.activeTabIndex + 1) % this.tabs.size();
		} else {
//			if (this.activeTabIndex > 0 || this.allowRoundtrip) {
//				x -= scrollerWidth;
//			}
			for (int i = 0; i < this.tabs.size(); i++) {
				Item tab = (Item) this.tabs.get(i);
				//ystem.out.println( "x=" + x + ", tab.relativeX=" + tab.relativeX + ", xOffset=" + this.xOffset +  ", relX+itemW=" + (tab.relativeX + this.xOffset + tab.itemWidth) + ", tab=" + tab);
				int tabX = tab.relativeX + this.xOffset;
				if (tabX <= x && x <= tabX + tab.itemWidth) {
					newActiveTabIndex = i;
					break;
				}
			}
		}
		int activeTab = this.activeTabIndex;
		if (newActiveTabIndex != activeTab) {
			((Item)this.tabs.get(newActiveTabIndex)).notifyItemPressedStart();
			this.nextTabIndex = newActiveTabIndex;
			this.handlePointerReleaseEvent = true;
		}

		return this.handlePointerReleaseEvent;
	}
	//#endif


	//#ifdef polish.hasPointerEvents
	protected boolean handlePointerReleased(int x, int y) {
		if (this.handlePointerReleaseEvent) {
			this.handlePointerReleaseEvent = false;
			((Item)this.tabs.get(this.nextTabIndex)).notifyItemPressedEnd();
			if (this.screen instanceof TabbedForm) {
				((TabbedForm)this.screen).setActiveTab(this.nextTabIndex);
			} else {
				setActiveTab(this.nextTabIndex);
				notifyStateChanged();
			}
			return true;
		}
		return false;
	}
	//#endif

	/**
	 * Sets the image for the specified tab.
	 * 
	 * @param tabIndex the index of the tab 
	 * @param image the image
	 */
	public void setImage(int tabIndex, Image image) {
		Object tab = this.tabs.get(tabIndex);
		if (tab instanceof IconItem) {
			((IconItem)tab).setImage(image);
		} else if (tab instanceof ImageItem) {
			((ImageItem)tab).setImage(image);
		}
	}

	/**
	 * Sets the text for the specified tab.
	 * 
	 * @param tabIndex the index of the tab 
	 * @param text the text
	 */
	public void setText(int tabIndex, String text ) {
		Object tab = this.tabs.get(tabIndex);
		if (tab instanceof StringItem) {
			((StringItem)tab).setText( text );
		} else if (tab instanceof ImageItem) {
			((ImageItem)tab).setAltText(text);
		}
	}

	/**
	 * Retrieves the index of the currently selected tab.
	 * 
	 * @return the index of the currently selected tab, 0 is the index of the first tab.
	 */
	public int getNextTab() {
		return this.nextTabIndex;
	}

	/**
	 * Creates a new tab on the tab bar.
	 *
	 * @param tabName the name of the new tab
	 * @param tabImage the image of the new tab, can be null
	 */
	public void addNewTab(String tabName, Image tabImage) {
		addNewTab( tabName, tabImage, this.inactiveStyle );
	}

	/**
	 * Creates a new tab on the tab bar.
	 *
	 * @param tabName the name of the new tab
	 * @param tabImage the image of the new tab, can be null
	 * @param tabStyle the style of the tab
	 */
	public void addNewTab(String tabName, Image tabImage, Style tabStyle) {
		IconItem tab = new IconItem(tabName, tabImage);
		addNewTab( tab, tabStyle);
		this.tabs.add(tab);
	}

	/**
	 * Creates a new tab on the tab bar.
	 *
	 * @param tab the new tab item
	 */
	public void addNewTab(Item tab) {
		addNewTab( tab, this.inactiveStyle );
	}

	/**
	 * Creates a new tab on the tab bar.
	 *
	 * @param tab the new tab item
	 * @param tabStyle the style of the tab
	 */
	public void addNewTab(Item tab, Style tabStyle) {
		this.tabs.add(tab);
		if (tabStyle != null) {
			tab.setStyle( tabStyle );
		}
		tab.parent = this;
		setInitialized(false);
	}

	/**
	 * Creates a new tab on the tab bar.
	 * 
	 * @param index the index at which the tab should be added
	 * @param tabName the name of the new tab
	 * @param tabImage the image of the new tab, can be null
	 */
	public void addNewTab(int index, String tabName, Image tabImage) {
		addNewTab( tabName, tabImage, this.inactiveStyle );
	}

	/**
	 * Creates a new tab on the tab bar.
	 *
	 * @param index the index at which the tab should be added
	 * @param tabName the name of the new tab
	 * @param tabImage the image of the new tab, can be null
	 * @param tabStyle the style of the tab
	 */
	public void addNewTab(int index, String tabName, Image tabImage, Style tabStyle) {
		IconItem tab = new IconItem(tabName, tabImage );
		addNewTab( index, tab, tabStyle );
	}

	/**
	 * Creates a new tab on the tab bar.
	 * 
	 * @param index the index at which the tab should be added
	 * @param tab the new tab item
	 */
	public void addNewTab(int index, Item tab) {
		addNewTab( index, tab, this.inactiveStyle );
	}

	/**
	 * Creates a new tab on the tab bar.
	 * 
	 * @param index the index at which the tab should be added
	 * @param tab the new tab item
	 * @param tabStyle the style of the tab
	 */
	public void addNewTab(int index, Item tab, Style tabStyle) {
		this.tabs.add(index, tab);
		if (tabStyle != null) {
			tab.setStyle( tabStyle );
		}
		tab.parent = this;
		setInitialized(false);
	}

	/**
	 * Removes a tab from the tab bar.
	 *
	 * @param index the index of the tab to remove
	 */
	public void removeTab(int index) {
		this.tabs.remove(index);
	}

	/**
	 * Retrieves a tab item
	 * @param tabIndex the index of the tab item
	 * @return the tab item
	 */
	public Item getTabItem(int tabIndex)
	{
		return (Item) this.tabs.get(tabIndex);
	}

	/**
	 * Sets a tab item
	 * @param tabIndex the index of the tab
	 * @param item the item
	 */
	public void setTabItem(int tabIndex, Item item)
	{
		this.tabs.set(tabIndex, item);
	}

	/**
	 * Specifies if cycling is allowed
	 * @return true when cycling is allowed for this TabBar
	 */
	public boolean isRoundtrip() {
		return this.allowRoundtrip;
	}

	/**
	 * Specifies if cycling is allowed
	 * @param allow true when cycling should be allowed for this TabBar
	 */
	public void setRoundtrip(boolean allow) {
		this.allowRoundtrip = allow;
	}


}
