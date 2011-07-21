//#condition polish.usePolishGui
/*
 * Created on 30-Mar-2009 at high noon.
 * 
 * Copyright (c) 2009 Andre Schmidt / Enough Software
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

import javax.microedition.lcdui.Graphics;

/**
 * <p>A menubar implementation to be drawn in the title. Use title-menu: true
 * in the css of a screen to activate this feature.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        30-Mar-2009 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class TitleMenuBar extends MenuBar{

	/**
	 * the title item
	 */
	Item titleItem;
	
	/**
	 * Creates a new TitleMenuBar
	 * @param screen the screen
	 */
	public TitleMenuBar(Screen screen) {
		this(screen,null);
	}
	
	/**
	 * Creates a new TitleMenuBar
	 * @param screen the screen
	 * @param style the style
	 */
	public TitleMenuBar(Screen screen, Style style) {
		super(screen,style);
		//#style title?
		this.titleItem = new StringItem(null,null);
	}

	/**
	 * Sets the title
	 * @param title the title
	 */
	public void setTitle(String title)
	{
		if (this.titleItem instanceof StringItem) {
			((StringItem)this.titleItem).setText(title);
		}
	}
	
	/**
	 * Sets the title
	 * @param title the title
	 */
	public void setTitle(String title, Style style)
	{
		if (this.titleItem instanceof StringItem) {
			((StringItem)this.titleItem).setText(title, style);
		}
	}
	
	/**
	 * Sets a title item
	 * @param item the new item for the title
	 */
	public void setTitle( Item item ) {
		setTitle( item, null );
	}
	
	/**
	 * Sets a title item
	 * @param item the new item for the title
	 */
	public void setTitle( Item item, Style style ) {
		this.titleItem = item;
		item.parent = this;
		if (style != null) {
			item.setStyle(style);
		}
		requestInit();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.MenuBar#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		
		//#if polish.css.title-style
			Style titleStyle = (Style)style.getObjectProperty("title-style");
			if(titleStyle != null)
			{
				this.titleItem.setStyle(titleStyle);
			}
			if (this.screen != null) {
				Style screenStyle = this.screen.style;
				titleStyle = (Style)screenStyle.getObjectProperty("title-style");
				if(titleStyle != null)
				{
					this.titleItem.setStyle(titleStyle);
				}
			}
		//#endif
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#init(int, int, int)
	 */
	protected void init(int firstLineWidth, int availWidth, int availHeight) {
		super.init(firstLineWidth, availWidth, availHeight);
		
		Container cmds = this.commandsContainer;
		if (cmds.isLayoutVerticalCenter()) {
			cmds.relativeY = this.contentHeight + (availHeight - (this.contentHeight + cmds.itemHeight))/2;
		} else if (cmds.isLayoutBottom()) {
			cmds.relativeY = this.contentHeight + (availHeight - (this.contentHeight + cmds.itemHeight));
		} else {
			cmds.relativeY = this.contentHeight;
		}
		if (cmds.isLayoutCenter()) {
			cmds.relativeX = (availWidth - cmds.itemWidth)/2;
		} else if (cmds.isLayoutRight) {
			cmds.relativeX = (availWidth - cmds.itemWidth);
		} else {
			cmds.relativeX = 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		super.initContent(firstLineWidth, availWidth, availHeight);
		
		Item title = this.titleItem;
		int maxHeight = Math.max( this.contentHeight, title.getItemHeight( availWidth, availWidth, availHeight) );
		CommandItem singleLeft = this.singleLeftCommandItem;
		CommandItem singleRight = this.singleRightCommandItem;
		if (maxHeight > title.itemHeight) {
			if (title.isLayoutVerticalCenter()) {
				title.relativeY = (maxHeight - title.itemHeight) / 2;
			} else if (title.isLayoutBottom()) {
				title.relativeY = maxHeight - title.itemHeight; 
			}
			singleLeft.relativeY = 0;
			singleRight.relativeY = 0;
		} else {
			title.relativeY = 0;
			if ( singleLeft.isLayoutVerticalCenter()) {
				singleLeft.relativeY = (maxHeight - singleLeft.itemHeight) >> 1;					
			} else if ( singleLeft.isLayoutBottom()) {
				singleLeft.relativeY = maxHeight - singleLeft.itemHeight;
			} else {
				singleLeft.relativeY = 0;
			}
			if ( singleRight.isLayoutVerticalCenter()) {
				singleRight.relativeY = (maxHeight - singleRight.itemHeight) >> 1;					
			} else if ( singleRight.isLayoutBottom()) {
				singleRight.relativeY = maxHeight - singleRight.itemHeight;
			} else {
				singleRight.relativeY = 0;
			}
		}
		this.contentHeight = maxHeight;
		if (title.isLayoutCenter()) {
			title.relativeX = (availWidth - title.itemWidth)/2;
		} else if (title.isLayoutRight()) {
			title.relativeX = (availWidth - title.itemWidth);
		} else {
			title.relativeX = 0;
		}

	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.MenuBar#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		super.paintContent(x, y, leftBorder, rightBorder, g);
		
		Item title = this.titleItem;
		int itemX = x + title.relativeX;
		int itemY = y + title.relativeY;
		title.paint( itemX, itemY, itemX, itemX + title.itemWidth, g );
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.MenuBar#animate(long,ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		this.titleItem.animate(currentTime, repaintRegion);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.MenuBar#isInMenubar(int)
	 */
	protected boolean isInMenubar(int relativeY)
	{
		return relativeY < this.itemHeight;
	}

	/* non-Javadoc
	 * @see MenuBar#handleKeyPressed(int,int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		Item title = this.titleItem;
		boolean handled = false;
		if (!this.isOpened && title.appearanceMode != PLAIN) {
			handled = title.handleKeyPressed(keyCode, gameAction);
		}
		return handled || super.handleKeyPressed(keyCode, gameAction);
	}

	/* non-Javadoc
	 * @see MenuBar#handleKeyPressed(int,int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		Item title = this.titleItem;
		boolean handled = false;
		if (!this.isOpened && title.appearanceMode != PLAIN) {
			handled = title.handleKeyReleased(keyCode, gameAction);
		}
		return handled || super.handleKeyReleased(keyCode, gameAction);
	}

	/* non-Javadoc
	 * @see MenuBar#handleKeyPressed(int,int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction) {
		Item title = this.titleItem;
		boolean handled = false;
		if (!this.isOpened && title.appearanceMode != PLAIN) {
			handled = title.handleKeyRepeated(keyCode, gameAction);
		}
		return handled || super.handleKeyRepeated(keyCode, gameAction);
	}

	//#ifdef polish.hasPointerEvents
	/* non-Javadoc
	 * @see MenuBar#handleKeyPressed(int,int, ClippingRegion)
	 */
	protected boolean handlePointerDragged(int relX, int relY, ClippingRegion repaintRegion) {
		Item title = this.titleItem;
		boolean handled = false;
		if (!this.isOpened && title.appearanceMode != PLAIN) {
			handled = title.handlePointerDragged(relX - title.relativeX, relY - title.relativeY, repaintRegion);
		}
		return handled || super.handlePointerDragged(relX, relY, repaintRegion);
	}
	//#endif

	//#ifdef polish.hasPointerEvents
	/* non-Javadoc
	 * @see MenuBar#handleKeyPressed(int,int)
	 */
	protected boolean handlePointerPressed(int relX, int relY) {
		Item title = this.titleItem;
		boolean handled = false;
		if (!this.isOpened && title.appearanceMode != PLAIN) {
			handled = title.handlePointerPressed(relX - title.relativeX, relY - title.relativeY);
		}
		return handled || super.handlePointerPressed(relX, relY);
	}
	//#endif

	//#ifdef polish.hasPointerEvents
	/* non-Javadoc
	 * @see MenuBar#handleKeyPressed(int,int)
	 */
	protected boolean handlePointerReleased(int relX, int relY) {
		Item title = this.titleItem;
		boolean handled = false;
		if (!this.isOpened && title.appearanceMode != PLAIN) {
			handled = title.handlePointerReleased(relX - title.relativeX, relY - title.relativeY);
		}
		return handled || super.handlePointerReleased(relX, relY);
	}
	//#endif
	
	
}
