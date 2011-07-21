//#condition polish.usePolishGui
/*
 * Created on 23-Jan-2005 at 18:46:50.
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.ArrayList;

/**
 * <p>Separates a form into several tabs.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        23-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Craig Newton for dynamic adding and removing of tabs
 */
public class TabbedForm extends Form {
	
	//#if polish.classes.TabBar:defined
		//#= private final ${polish.classes.TabBar} tabBar;
	//#else
		private final TabBar tabBar;
	//#endif
	private final ArrayList tabContainers;
	private int activeTabIndex;
	private TabbedFormListener tabbedFormListener;
	//#if polish.TabbedForm.allowTabSelection
		private Style tabBarStyle;
	//#endif

	/**
	 * Creates a new tabbed form without a style.
	 * 
	 * @param title the title of the form.
	 * @param tabNames the names of the tabs
	 * @param tabImages the images of the tabs, can be null
	 */
	public TabbedForm(String title, String[] tabNames, Image[] tabImages ) {
		this( title, tabNames, tabImages, null );
	}

	/**
	 * Creates a new tabbed form.
	 *  
	 * @param title the title of the form.
	 * @param tabNames the names of the tabs
	 * @param tabImages the images of the tabs, can be null
	 * @param style the style of this tabbed form.
	 * @throws NullPointerException if tabNames is null
	 */
	public TabbedForm(String title, String[] tabNames, Image[] tabImages, Style style) {
		super(title, style );
		//#if polish.classes.TabBar:defined
			//#style tabbar?
			//#= this.tabBar = new ${polish.classes.TabBar} ( tabNames, tabImages );
		//#else
			//#style tabbar?
			this.tabBar = new TabBar( tabNames, tabImages );
		//#endif
		int length;
		if (tabNames != null) {
			length = tabNames.length;
		} else {
			length = tabImages.length;
		}
		this.tabContainers = new ArrayList( length );
		this.tabContainers.add( this.container );
		for (int i = 1; i < length; i++) {
			Container tabContainer = new Container( null, false, null, this.screenHeight );
			if (style != null) {
				tabContainer.setStyleWithBackground( style, true );
			}
			tabContainer.screen = this;
			this.tabContainers.add( tabContainer );
		}
		setSubTitle( this.tabBar );
	}
	

	//#if polish.LibraryBuild
	/**
	 * Adds the item  to this form.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param item the item which should be added.
	 */
	public void append( int tabIndex, javax.microedition.lcdui.Item item ) {
		throw new RuntimeException("Unable to use standard items in a tabbed form - please append only J2ME Polish items.");
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Changes the item of a tab.
	 * 
	 * @param tabIndex the index of the tab,
	 *        the first tab has the index 0.
	 * @param itemIndex the index of the item in the tab
	 * @param item the item which should be added.
	 */
	public void set( int tabIndex, int itemIndex, javax.microedition.lcdui.Item item ) {
		throw new RuntimeException("Unable to use standard items in a tabbed form - please append only J2ME Polish items.");
	}
	//#endif

	
	//#if polish.LibraryBuild
	/**
	 * Deletes the item from this form.
	 * 
	 * @param tabIndex the index of the tab from which the item should be removed,
	 *        the first tab has the index 0.
	 * @param item the item which should be removed.
	 */
	public void delete( int tabIndex, javax.microedition.lcdui.Item item ) {
		throw new RuntimeException("Unable to use standard items in a tabbed form - please append only J2ME Polish items.");
	}
	//#endif
		
	/**
	 * Adds the  item  to the first tab of this form.
	 * 
	 * @param item the item which should be added.
	 * @param itemStyle the style for that item
   * @return the assigned index of the Item within the specified tab
	 */
	public int append( Item item, Style itemStyle ) {
		return append( 0, item, itemStyle );
	}
	
	/**
	 * Adds the  item  to this form.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param item the item which should be added.
	 * @return the assigned index of the Item within the specified tab
	 */
	public int append( int tabIndex, Item item ) {
		return append( tabIndex, item, null );
	}

	/**
	 * Adds the  item  to this form.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param item the item which should be added.
   * @param itemStyle the style for that item
	 * @return the assigned index of the Item within the specified tab
	 */
	public int append( int tabIndex, Item item, Style itemStyle ) {
		//#if polish.Container.allowCycling != false
			if (item instanceof Container) {
				((Container)item).allowCycling = false;
			}
		//#endif
		if (itemStyle != null) {
			item.setStyle( itemStyle );
		}
 		Container tabContainer = (Container) this.tabContainers.get( tabIndex );
		tabContainer.add(item);
		return tabContainer.size() - 1;
	}
	
	//#if polish.LibraryBuild
	/**
	 * Inserts an item into this form just prior to the item specified on the
	 * specified tab.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param itemNum the index where insertion is to occur
	 * @param item the item to be inserted
	 */
	public void insert(int tabIndex, int itemNum, javax.microedition.lcdui.Item item)
	{
		// ignore
	}
	//#endif

	/**
	 * Inserts an item into this form just prior to the item specified on the
	 * specified tab.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param itemNum the index where insertion is to occur
	 * @param item the item to be inserted
	 */
	public void insert(int tabIndex, int itemNum, Item item)
	{
		insert(tabIndex, itemNum, item, null);
	}

	//#if polish.LibraryBuild
	/**
	 * Inserts an item into this form just prior to the item specified on the
	 * specified tab.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param itemNum the index where insertion is to occur
	 * @param item the item to be inserted
	 * @param itemStyle the style of the item
	 */
	public void insert(int tabIndex, int itemNum, javax.microedition.lcdui.Item item, Style itemStyle)
	{
	}
	//#endif

	/**
	 * Inserts an item into this form just prior to the item specified on the
	 * specified tab.
	 * 
	 * @param tabIndex the index of the tab to which the item should be added,
	 *        the first tab has the index 0.
	 * @param itemNum the index where insertion is to occur
	 * @param item the item to be inserted
	 * @param itemStyle the style of the item
	 */
	public void insert(int tabIndex, int itemNum, Item item, Style itemStyle)
	{
		//#if polish.Container.allowCycling != false
			if (item instanceof Container) {
				((Container)item).allowCycling = false;
			}
		//#endif
		if (itemStyle != null) {
			item.setStyle(itemStyle);
		}
 		Container tabContainer = (Container) this.tabContainers.get( tabIndex );
		if (itemNum == tabContainer.size()) {
			tabContainer.add(item);
		} else {
			tabContainer.add(itemNum, item);
		}
	}


	/**
	 * Changes the item of the first tab.
	 * 
	 * @param itemIndex the index of the item in the tab
	 * @param item the item which should be added.
	 */
	public void set( int itemIndex, Item item ) {
		set( 0, itemIndex, item );
	}

	/**
	 * Changes the item of a tab.
	 * 
	 * @param tabIndex the index of the tab,
	 *        the first tab has the index 0.
	 * @param itemIndex the index of the item in the tab
	 * @param item the item which should be added.
	 */
	public void set( int tabIndex, int itemIndex, Item item ) {
		//#if polish.Container.allowCycling != false
			if (item instanceof Container) {
				((Container)item).allowCycling = false;
			}
		//#endif
		Container tabContainer = (Container) this.tabContainers.get( tabIndex );
		tabContainer.set(itemIndex, item);
	}
	
	/**
	 * Gets the item at given position within the specified tab.  
	 * The contents of the
	 * <code>TabbedForm</code> are left unchanged.
	 * The <code>itemNum</code> parameter must be
	 * within the range <code>[0..size()-1]</code>, inclusive.
	 * 
	 * @param tabIndex the index of the tab,
	 *        the first tab has the index 0.
	 * @param itemNum the index of item
	 * @return the item at the given position
	 * @throws IndexOutOfBoundsException - if itemNum is invalid
	 */
	//#if polish.LibraryBuild
		public javax.microedition.lcdui.Item get(int tabIndex, int itemNum)
		{
			return null;
		}
	//#else
		//# public Item get(int tabIndex, int itemNum)
		//# {
		//#	Container tabContainer = (Container) this.tabContainers.get( tabIndex );
		//#	return tabContainer.get( itemNum );
		//# }
	//#endif

	
	/**
	 * Deletes the item from this form.
	 * 
	 * @param tabIndex the index of the tab from which the item should be removed,
	 *        the first tab has the index 0.
	 * @param item the item which should be removed.
	 */
	public void delete( int tabIndex, Item item ) {
		Container tabContainer = (Container) this.tabContainers.get( tabIndex );
		tabContainer.remove( item );
		if (this.isShown() ) {
			requestRepaint();
		}
	}
	
	/**
	 * Deletes the item from this form.
	 * 
	 * @param tabIndex the index of the tab from which the item should be removed,
	 *        the first tab has the index 0.
	 * @param itemIndex the index of the item which should be removed.
	 */
	public void delete( int tabIndex, int itemIndex ) {
		Container tabContainer = (Container) this.tabContainers.get( tabIndex );
		tabContainer.remove( itemIndex );
		if (this.isShown() ) {
			requestRepaint();
		}
	}

	/**
	 * Deletes the all items from the specified tab.
	 * 
	 * @param tabIndex the index of the tab from which all items should be removed,
	 *        the first tab has the index 0.
	 */
	public void deleteAll( int tabIndex ) {
		Container tabContainer = (Container) this.tabContainers.get( tabIndex );
		tabContainer.clear();
	}
	
	/**
	 * Retrieves the number of elements within the specified tab.
	 * 
	 * @param tabIndex the tab, the first tab has the index 0
	 * @return the number of elements within that tab
	 */
	public int size( int tabIndex ) {
		Container tabContainer = (Container) this.tabContainers.get( tabIndex );
		return tabContainer.size();
	}

	/**
	 * Retrieves the number of tabs in this <code>TabbedForm</code>.
	 * 
	 * @return the number of tabs
	 */
	public int getTabCount() {
		return this.tabContainers.size();
	}

	/**
	 * Focuses the specified tab.
	 * 
	 * @param tabIndex the index of the tab, the first tab has the index 0.
	 */
	public void setActiveTab( int tabIndex ) {
		setActiveTab(tabIndex, true);
	}

	/**
	 * Focuses the specified tab.
	 * 
	 * @param tabIndex the index of the tab, the first tab has the index 0.
	 * @param focusTabBar true when the tabbar should be focused - this only has an effect when polish.TabbedForm.allowTabSelection is set to true
	 */
	public void setActiveTab( int tabIndex, boolean focusTabBar ) {
		if (!notifyTabbedChangeRequested( this.activeTabIndex, tabIndex )) {
			return;
		}
		//#debug
		System.out.println("Activating tab [" + tabIndex + "].");
		boolean isShown = isShown();
		if (isShown && this.container.isInitialized) {
			//System.out.println("defocus of container " + this.container);
			this.container.hideNotify();
			if (this.container.isInteractive()) {
				this.container.defocus( this.container.style );
			}
			//#if polish.TabbedForm.releaseResourcesOnTabChange
				this.container.releaseResources();
			//#endif
		}
		int oldTabIndex = this.activeTabIndex;
		this.activeTabIndex = tabIndex;
		this.tabBar.setActiveTab(tabIndex);
		Container tabContainer = (Container) this.tabContainers.get( tabIndex );
		this.container = tabContainer;
		if (this.contentHeight != 0) {
			tabContainer.setScrollHeight( this.contentHeight );
		}
		if (!tabContainer.isInitialized && this.contentWidth != 0) {
			tabContainer.init( this.contentWidth, this.contentWidth, this.contentHeight );
		}
		if (
		//#if polish.TabbedForm.allowTabSelection
			!focusTabBar && 
		//#endif
			tabContainer.isInteractive()) 
		{
				//#debug
				System.out.println("Focusing tab [" + tabIndex + "].");
				tabContainer.focus( tabContainer.style, 0 );
		        //#if polish.blackberry
					notifyFocusSet(tabContainer);
			    //#endif
			}
		tabContainer.background = null;
		tabContainer.border = null;
		initContent( tabContainer );
		if (isShown) {
			tabContainer.showNotify();
			requestRepaint();
		}
		notifyTabbedChangeCompleted(oldTabIndex, this.activeTabIndex);
		//#if polish.TabbedForm.allowTabSelection
			if (focusTabBar && !this.tabBar.isFocused) {
				if (tabContainer.isInteractive()) {
					tabContainer.defocus( tabContainer.style );
					//TODO check again when allowing tab-specific styles
					tabContainer.background = null;
					tabContainer.border = null;
				}
				this.tabBar.focus( this.tabBar.getFocusedStyle(), 0);
			}
		//#endif
	}
	
	/**
	 * Sets the image for the specified tab.
	 * 
	 * @param tabIndex the index of the tab 
	 * @param image the image
	 */
	public void setTabImage( int tabIndex, Image image ) {
		this.tabBar.setImage( tabIndex, image );
	}
	
	/**
	 * Sets the text for the specified tab.
	 * 
	 * @param tabIndex the index of the tab 
	 * @param text the text
	 */
	public void setText(int tabIndex, String text ) {
		this.tabBar.setText( tabIndex, text );	
	}


	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#createCssSelector()
	 */
	protected String createCssSelector() {
		return "tabbedform";
	}
	//#endif
	
	
	
	
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#if polish.TabbedForm.allowTabSelection
			if (this.tabBar.isFocused) {
				int nextTabIndex = this.activeTabIndex;
				if ( (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6)
					&&	(this.tabBar.isRoundtrip() || (this.activeTabIndex < (this.tabContainers.size() - 1)))
				){
					nextTabIndex = this.activeTabIndex + 1;
					if (nextTabIndex >= this.tabContainers.size()) {
						nextTabIndex = 0;
					}
				} 
				else if ((gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4) 
						&&	(this.tabBar.isRoundtrip() || (this.activeTabIndex > 0))
				){
					nextTabIndex = this.activeTabIndex - 1;
					if (nextTabIndex < 0) {
						nextTabIndex = this.tabContainers.size() - 1;
					}		
				}
				else if (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8 && this.container.isInteractive()) {
					this.tabBar.defocus(this.tabBarStyle);
					this.container.focus(this.container.style, Canvas.DOWN);
					return true;
				}
				//#if polish.css.tabbar-roundtrip
					else if (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2 && this.container.isInteractive()) {
						this.tabBar.defocus(this.tabBar.style);
						this.container.focus(this.container.style, Canvas.UP);
						return true;
					}
				//#endif
	
				if (this.activeTabIndex != nextTabIndex) {
					setActiveTab(nextTabIndex, true);
					return true;
				}
	
				// Don't continue processing events when tabbar is focused.
				return false;
			}
		//#endif

		int indexBeforeEvent = this.container.getFocusedIndex(); 
		boolean handled = super.handleKeyPressed(keyCode, gameAction);
		int indexAfterEvent = this.container.getFocusedIndex();
		if (handled && (indexAfterEvent < indexBeforeEvent || (gameAction == Canvas.UP && indexAfterEvent > indexBeforeEvent)) && this.container.isInteractive()) {
			// the container cycled, if left or right has been pressed, undo this change:
			if ((gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6) 
					|| (gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4)
					//#if polish.TabbedForm.allowTabSelection
					|| (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)
					|| (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2 && indexAfterEvent > indexBeforeEvent)
					//#endif
					)
			{
				handled = false;
				this.container.focusChild(indexBeforeEvent);
			}
		}
		//#if !polish.TabbedForm.allowTabSelection
			if (!handled) {
				int nextTabIndex = this.activeTabIndex;
				if ( (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6)
						&&	(this.tabBar.isRoundtrip() || (this.activeTabIndex < (this.tabContainers.size() - 1)))

				){
					nextTabIndex = this.activeTabIndex + 1;
					if (nextTabIndex >= this.tabContainers.size()) {
						nextTabIndex = 0;
					}
				} else if ((gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4) 
						&&	(this.tabBar.isRoundtrip() || (this.activeTabIndex > 0))
				){
					nextTabIndex = this.activeTabIndex - 1;
					if (nextTabIndex < 0) {
						nextTabIndex = this.tabContainers.size() - 1;
					}		
				}
				if (this.activeTabIndex != nextTabIndex) {
					setActiveTab(nextTabIndex, true);
					return true;
				}

			}
		//#else
			// Focus the tabbar when needed.
			if (!handled) {
				if (gameAction == Canvas.UP || gameAction == Canvas.DOWN) {
					if (this.container.isInteractive()) {
						this.container.defocus(this.container.style);
					}
					if (!this.tabBar.isFocused) {
						this.tabBar.focus(null, gameAction);
						return true;
					}
				}
				//#if polish.blackberry and !polish.hasTrackballEvents
					else if (gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT) {
						if (this.container.isInteractive()) {
							this.container.defocus(this.container.style);
						}
						if (!this.tabBar.isFocused) {
							this.tabBar.focus(null, gameAction);
							return true;
						}
					}
				//#endif
			}
		//#endif

		return handled;
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y)
	{
		if (this.tabBar.isInItemArea(x - this.tabBar.relativeX, y - this.tabBar.relativeY)) {
			return this.tabBar.handlePointerPressed(x - this.tabBar.relativeX, y - this.tabBar.relativeY);
		}

		return super.handlePointerPressed(x, y);
	}
	//#endif

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int x, int y)
	{
		if (this.tabBar.isInItemArea(x - this.tabBar.relativeX, y - this.tabBar.relativeY)) {
			return this.tabBar.handlePointerReleased(x, y);
		}

		return super.handlePointerReleased(x, y);
	}
	//#endif

	//#if polish.TabbedForm.allowTabSelection
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#initContent(de.enough.polish.ui.Container)
	 */
	protected void initContent(Container cont) {
		super.initContent(cont);
		if (!this.tabBar.isFocused) {
			this.tabBarStyle = this.tabBar.style;
			if (!this.container.isInteractive()) {
				// focus tabbar by default:
				this.tabBar.focus( null, 0);
			}
		}
	}
	//#endif

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#focus(de.enough.polish.ui.Item)
	 */
	public void focus(Item item) {
		if (item == this.tabBar) {
			item.focus( item.getFocusedStyle(), 0);
			// defocus current container:
			this.container.defocus( this.container.style );
			return;
		}
		for (int i = 0; i < this.tabContainers.size(); i++) {
			Container tabContainer = (Container) this.tabContainers.get( i );
			if ( tabContainer.itemsList.contains(item)) {
				if (i != this.activeTabIndex) {
					setActiveTab( i );
				}
				super.focus(item);
				return;
			}
		}
	}
	
	
	/**
	 * Retrieves the index of the currently active tab.
	 * 
	 * @return the index of the currently active tab, 0 is the first tab. 
	 * @deprecated
	 * @see #getActiveTab()
	 */
	public int getSelectedTab() {
		return this.activeTabIndex;
	}
	
	/**
	 * Retrieves the index of the currently active tab.
	 * 
	 * @return the index of the currently active tab, 0 is the first tab. 
	 */
	public int getActiveTab() {
		return this.activeTabIndex;
	}


	/**
	 * Notifies the <code>TabbedFormListener</code> that a tab change was requested. Then
	 * <code>TabbedFormListener</code> can now allow or disallow the tab change.
	 * 
	 * @param oldTabIndex the index of the old tab
	 * @param newTabIndex the index of the new tab
	 * @return <code>true</code> if a tab change is okay, <code>false</code> otherwise
	 */
	public boolean notifyTabbedChangeRequested(int oldTabIndex, int newTabIndex) {
		if (this.tabbedFormListener != null) {
			return this.tabbedFormListener.notifyTabChangeRequested(oldTabIndex, newTabIndex);
		}

		return true;
	}

	/**
	 * Notifies the <code>TabbedFormListener</code> that a tab change is completed.
	 * 
	 * @param oldTabIndex the index of the old tab
	 * @param newTabIndex the index of the new tab
	 */
	public void notifyTabbedChangeCompleted(int oldTabIndex, int newTabIndex) {
		if (this.tabbedFormListener != null) {
			this.tabbedFormListener.notifyTabChangeCompleted(oldTabIndex, newTabIndex);
		}
	}

	/**
	 * Sets the <code>TabbedFormListener</code> to be notified when tab changes happen.
	 * 
	 * @param listener the listener that is notified whenever the user selects another tab,
	 */
	public void setTabbedFormListener( TabbedFormListener listener ) {
		this.tabbedFormListener = listener;
	}

	public Item getCurrentItem() {
		if (this.tabBar.isFocused) {
			return this.tabBar;
		}
		return super.getCurrentItem();
	}

	//#if polish.midp2 && !polish.Bugs.needsNokiaUiForSystemAlerts 
	public void sizeChanged(int width, int height) {
		boolean doInit;
		
		//#if (polish.useMenuFullScreen && tmp.fullScreen) || polish.needsManualMenu
			doInit = width != this.screenWidth || height != this.fullScreenHeight;
		//#else
			doInit = width != this.screenWidth || height != this.originalScreenHeight;
		//#endif

		if (doInit) {
			this.tabBar.onScreenSizeChanged(width, height);
		}

		super.sizeChanged(width, height);
	}
	//#endif

	//#if polish.css.tabbar-style
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Screen#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style)
	{
		super.setStyle(style);
		//#if polish.css.tabbar-style
			Style tabbarStyle = (Style) style.getObjectProperty("tabbar-style");
			if (tabbarStyle != null) {
				this.tabBar.setStyle(tabbarStyle);
				//#if polish.TabbedForm.allowTabSelection
					this.tabBarStyle = tabbarStyle;
				//#endif
			}
		//#endif		
	}
	//#endif
	

	/**
	* Adds a new tab with a container to the TabbedForm.
	* 
	* @param tabName The name of the new tab
	* @param tabImage The optional image
	* @return The index of the new tab
	*/
	public int addNewTab(String tabName, Image tabImage) {
	   return addNewTab(tabName, tabImage, null);
	}

	/**
	* Adds a new tab with a container to the TabbedForm.
	* This method also assigns a style or uses the default
	* if style is set to null.
	* 
	* @param tabName The name of the new tab
	* @param tabImage The optional image
	* @param tabStyle The initial style of the tab
	* @return The index of the new tab
	*/
	public int addNewTab(String tabName, Image tabImage, Style tabStyle) {
	   this.tabBar.addNewTab(tabName, tabImage);
	   Container tabContainer = new Container(null, false, null, this.contentHeight);
	   if (tabStyle != null) {
	       tabContainer.setStyle(tabStyle, true);
	   }
	   tabContainer.screen = this;
	   this.tabContainers.add(tabContainer);

	   return tabContainers.size() - 1;
	}
	

	/**
	* Adds a new tab with a container to the TabbedForm.
	* 
	* @param index the index at which the tab should be added. 0 adds the tab at the beginning.
	* @param tabName The name of the new tab
	* @param tabImage The optional image
	*/
	public void addNewTab(int index, String tabName, Image tabImage) {
	   addNewTab(index, tabName, tabImage, null);
	}

	/**
	* Adds a new tab with a container to the TabbedForm.
	* This method also assigns a style or uses the default
	* if style is set to null.
	* 
	* @param index the index at which the tab should be added
	* @param tabName The name of the new tab
	* @param tabImage The optional image
	* @param tabStyle The initial style of the tab
	*/
	public void addNewTab(int index, String tabName, Image tabImage, Style tabStyle) {
	   this.tabBar.addNewTab(index, tabName, tabImage);
	   Container tabContainer = new Container(null, false, null, this.contentHeight);
	   if (tabStyle != null) {
	       tabContainer.setStyle(tabStyle, true);
	   }
	   tabContainer.screen = this;
	   this.tabContainers.add( index, tabContainer);
	}
	
	
	/**
	* Removes a tab and its container from the TabbedForm.
	* 
	* @param index The tab at the index to remove
	*/
	public void removeTab(int index) {        
	   this.tabContainers.remove(index);        
	   this.tabBar.removeTab(index);
	}
	
	/**
	 * Retrieves the item that renders the tab
	 * 
	 * @param tabIndex the tab position
	 * @return the tab item
	 */
	public Item getTabItem( int tabIndex ) {
		return this.tabBar.getTabItem(tabIndex);
	}

	/**
	 * Sets the item that renders the specified tab
	 * 
	 * @param tabIndex the tab position
	 * @param item the tab item
	 */
	public void setTabItem( int tabIndex, Item item ) {
		this.tabBar.setTabItem(tabIndex, item);
	}
	
	//#if polish.LibraryBuild
	/**
	 * Sets the item that renders the specified tab
	 * 
	 * @param tabIndex the tab position
	 * @param item the tab item
	 */
	public void setTabItem( int tabIndex, javax.microedition.lcdui.Item item ) {
		// ignore
	}
	//#endif
}
