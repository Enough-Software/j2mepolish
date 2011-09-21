//#condition polish.usePolishGui
/*
 * Created on 01-Mar-2004 at 09:45:32.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.ArrayList;

//#if polish.blackberry
//# import de.enough.polish.blackberry.ui.BaseScreen;
//#endif

/**
 * <p>Contains a number of items.</p>
 * <p>Main purpose is to manage all items of a Form or similar canvases.</p>
 * <p>Containers support following additional CSS attributes:
 * </p>
 * <ul>
 * 		<li><b>columns</b>: The number of columns. If defined a table will be drawn.</li>
 * 		<li><b>columns-width</b>: The width of the columns. "equals" for an equal width
 * 				of each column, "normal" for a column width which depends on
 * 			    the items. One can also specify the used widths directly with
 * 				a comma separated list of integers, e.g.
 * 				<pre>
 * 					columns: 2;
 * 					columns-width: 15,5;
 * 				</pre>
 * 				</li>
 * 		<li><b>scroll-mode</b>: Either "smooth" (=default) or "normal".</li>
 * 		<li><b>and many more...</b>: compare the visual guide to J2ME Polish</li>
 * </ul>
 * <p>Copyright Enough Software 2004 - 2007 - 2009</p>

 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Container extends Item {
	//#if polish.css.columns || polish.useTable
		//#define tmp.useTable
	//#endif
	
	/** constant for normal scrolling (0) */
	public static final int SCROLL_DEFAULT = 0;
	/** constant for smooth scrolling (1) */
	public static final int SCROLL_SMOOTH = 1;
	
	protected ArrayList itemsList;
	//protected Item[] items;
	protected boolean autoFocusEnabled;
	protected int autoFocusIndex;
	protected Style itemStyle;
	protected Item focusedItem;
	/** the index of the currently focused item - please use only for reading, not for setting, unless you know what you are doing */
	public int focusedIndex = -1;
	protected boolean enableScrolling;
	//#if polish.Container.allowCycling != false
		/** specifies whether this container is allowed to cycle to the beginning when the last item has been reached */
		public boolean allowCycling = true;
	//#else
		//#	public boolean allowCycling = false;
	//#endif
	protected int yOffset;
	protected int targetYOffset;
	private int focusedTopMargin;
	//#if polish.css.view-type || polish.css.columns
		//#define tmp.supportViewType 
		protected ContainerView containerView;
	//#endif
	//#ifdef polish.css.scroll-mode
		protected boolean scrollSmooth = true;
	//#endif
	//#if polish.css.expand-items
		protected boolean isExpandItems;
	//#endif
	//#ifdef polish.hasPointerEvents
		/** vertical pointer position when it was pressed the last time */ 
		protected int lastPointerPressY;
		/** scrolloffset when this container was pressed the last time */
		protected int lastPointerPressYOffset;
		/** time in ms when this container was pressed the last time */
		protected long lastPointerPressTime;
	//#endif
	//#if polish.css.focused-style-first
		protected Style focusedStyleFirst;
	//#endif
	//#if polish.css.focused-style-last
		protected Style focusedStyleLast;
	//#endif
	private boolean isScrollRequired;
	/** The height available for scrolling, ignore when set to -1 */
	protected int scrollHeight = -1;
	private Item[] containerItems;
	private boolean showCommandsHasBeenCalled;
	private Item scrollItem;
	protected Style plainStyle;
	private static final String KEY_ORIGINAL_STYLE = "os"; 
	//#if polish.css.focus-all
		private boolean isFocusAllChildren;
	//#endif
	//#if polish.css.focus-all-style
		protected Style focusAllStyle;
	//#endif
	//#if polish.css.press-all
		private boolean isPressAllChildren;
	//#endif
	private boolean isIgnoreMargins;
	//private int availableContentWidth;
	//#if polish.css.show-delay
		private int showDelay;
		private int showDelayIndex;
		private long showNotifyTime;
	//#endif
	//#if polish.css.child-style
		private Style childStyle;
	//#endif
	boolean appearanceModeSet;
	private int scrollDirection;
	private int scrollSpeed;
	private int scrollDamping;
	//#ifdef tmp.supportFocusItemsInVisibleContentArea
		private boolean needsCheckItemInVisibleContent = false;
	//#endif
	private long lastAnimationTime;
	private boolean isScrolling;
	//#if polish.css.bounce && !(polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false)
		//#define tmp.checkBouncing
		private boolean allowBouncing = true;
	//#endif
	private FocusListener focusListener;
	
	/**
	 * Creates a new empty container.
	 */
	public Container() {
		this( null, false, null, -1 );
	}
	
	/**
	 * Creates a new empty container with the specified style.
	 * 
	 * @param style the style for this container
	 */
	public Container(Style style) {
		this( null, false, style, -1 );
	}
	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 */
	public Container( boolean focusFirstElement ) {
		this( null, focusFirstElement, null, -1 );
	}
	
	/**
	 * Creates a new empty container.
	 * 
	 * @param focusFirstElement true when the first focussable element should be focused automatically.
	 * @param style the style for this container
	 */
	public Container(boolean focusFirstElement, Style style) {
		this( null, focusFirstElement, style, -1  );
	}

	/**
	 * Creates a new empty container.
	 * 
	 * @param label the label of this container
	 * @param focusFirstElement true when the first focusable element should be focused automatically.
	 * @param style the style for this container
	 * @param height the vertical space available for this container, set to -1 when scrolling should not be activated
	 * @see #setScrollHeight( int ) 
	 */
	public Container(String label, boolean focusFirstElement, Style style, int height ) {
		super( label, LAYOUT_DEFAULT, INTERACTIVE, style );
		this.itemsList = new ArrayList();
		this.autoFocusEnabled = focusFirstElement;
		this.layout |= Item.LAYOUT_NEWLINE_BEFORE;
		setScrollHeight( height );
	}
	
	/**
	 * Sets the height available for scrolling of this item.
	 * 
	 * @param height available height for this item including label, padding, margin and border, -1 when scrolling should not be done.
	 */
	public void setScrollHeight( int height ) {
		//#debug
		System.out.println("Setting scroll height to " + height + " for " + this);
		boolean scrollAutomatic = (this.scrollHeight != -1) && (height != -1) && (height != this.scrollHeight) && isInitialized();
		this.scrollHeight = height;
		this.enableScrolling = (height != -1);
		Item item = this.scrollItem != null ? this.scrollItem : (this.isFocused ? this.focusedItem : null);
		if (scrollAutomatic && item != null) {
			//#debug
			System.out.println("setScrollHeight(): scrolling to item=" + item + " with y=" + item.relativeY + ", height=" + height);
			scroll( 0, item, true);
			synchronized(this.itemsList) {
				this.isScrollRequired = false;
			}
		}
	}
	
	/**
	 * Returns the available height for scrolling either from this container or from it's parent container.
	 * Note that the height available for this container might differ from the returned value.
	 * 
	 * @return the available vertical space or -1 when it is not known.
	 * @see #getContentScrollHeight()
	 */
	public int getScrollHeight() {
		if (this.scrollHeight == -1 && this.parent instanceof Container) {
			return ((Container)this.parent).getScrollHeight();
		} else {
			return this.scrollHeight;
		}
	}
	
//	/**
//	 * Returns the available height for scrolling either from this container or from it's parent container.
//	 * 
//	 * @return the available vertical space for this container or -1 when it is not known.
//	 * @see #getContentScrollHeight()
//	 */
//	public int getRelativeScrollHeight() {
//		if (this.scrollHeight == -1 && this.parent instanceof Container) {
//			return ((Container)this.parent).getRelativeScrollHeight() - this.relativeY - this.targetYOffset;
//		} else {
//			return this.scrollHeight - this.targetYOffset;
//		}
//	}

	
	/**
	 * Retrieves the available height available for the content of this container
	 *  
	 * @return the available vertical space minus paddings/margins etc or -1 when it is not known.
	 * @see #getScrollHeight()
	 */
	int getContentScrollHeight() {
		return getScrollHeight() - (this.contentY + getBorderWidthTop() + getBorderWidthBottom() + this.paddingBottom + this.marginBottom ); 
	}
	
	/**
	 * Adds an StringItem with the given text to this container.
	 * 
	 * @param text the text
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add(String text)
	{
		add(new StringItem(null,text));
	}
	
	/**
	 * Adds an StringItem with the given text to this container.
	 * 
	 * @param text the text
	 * @param textAddStyle the style for the text
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add(String text,Style textAddStyle)
	{
		add(new StringItem(null,text),textAddStyle);
	}

	/**
	 * Adds an item to this container.
	 * 
	 * @param item the item which should be added.
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add( Item item ) {
		//#debug
		System.out.println("adding " + item + " to " + this);
		synchronized (this.itemsList) {
			item.relativeY =  0;
			item.internalX = Item.NO_POSITION_SET;
			item.parent = this;
			this.itemsList.add( item );
			if (isInitialized()) {
				requestInit();
			}
		}
		if (this.isShown) {
			item.showNotify();
		}
		//#if polish.css.focus-all
			if (this.isFocused && this.isFocusAllChildren && !item.isFocused) {
				Style itemFocusedStyle = item.getFocusedStyle();
				if (itemFocusedStyle != this.style && itemFocusedStyle != StyleSheet.focusedStyle) {
					if (item.style != null) {
						item.setAttribute(KEY_ORIGINAL_STYLE, item.style);
					}
					item.focus(itemFocusedStyle, 0);
				}
				
			}
		//#endif
		if (this.isShown) {
			repaint();
		}
		notifyValueChanged(item);
	}
	

	/**
	 * Adds an item to this container.
	 * 
	 * @param item the item which should be added.
	 * @param itemAddStyle the style for the item
	 * @throws IllegalArgumentException when the given item is null
	 */
	public void add( Item item, Style itemAddStyle ) {
		add( item );
		if (itemAddStyle != null) {
			item.setStyle( itemAddStyle );
		}
		//#if polish.css.child-style
			else if (item.style == null && this.childStyle != null) {
				item.setStyle( this.childStyle );
			}
		//#endif
	}

	/**
	 * Inserts the given item at the defined position.
	 * Any following elements are shifted one position to the back.
	 * 
	 * @param index the position at which the element should be inserted, 
	 * 					 use 0 when the element should be inserted in the front of this list.
	 * @param item the item which should be inserted
	 * @throws IllegalArgumentException when the given item is null
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public void add( int index, Item item ) {
		synchronized (this.itemsList) {
			item.relativeY = 0;
			item.internalX = NO_POSITION_SET;
			item.parent = this;
			this.itemsList.add( index, item );
			if (index <= this.focusedIndex) {
				this.focusedIndex++;
				//#if tmp.supportViewType
					if (this.containerView != null) {
						this.containerView.focusedIndex = this.focusedIndex;
					}
				//#endif
			}
			if (this.isShown) {
				requestInit();
			}
			// set following items to relativeY=0, so that they will be scrolled correctly:
			for (int i= index + 1; i < this.itemsList.size(); i++ ) {
				Item followingItem = get(i);
				followingItem.relativeY = 0;
			}
			if (this.isShown) {
				item.showNotify();
			}
			
			//#if polish.css.focus-all
				if (this.isFocused && this.isFocusAllChildren) {
					Style itemFocusedStyle = item.getFocusedStyle();
					if (itemFocusedStyle != this.style && itemFocusedStyle != StyleSheet.focusedStyle) {
						if (item.style != null) {
							item.setAttribute(KEY_ORIGINAL_STYLE, item.style);
						}
						item.focus(itemFocusedStyle, 0);
					}
				}
			//#endif
		}
		if (this.isShown) {
			repaint();
		}
	}
	
	//#if polish.LibraryBuild
	/**
	 * Adds an item
	 * @param item the item to be added
	 */
	public void add( javax.microedition.lcdui.Item item ) {
		// ignore
	}
	/**
	 * Inserts an item
	 * @param index the index
	 * @param item the item
	 */
	public void add( int index, javax.microedition.lcdui.Item item ) {
		// ignore
	}
	/**
	 * Replaces an item
	 * @param index the index
	 * @param item the item to be added
	 */
	public void set( int index, javax.microedition.lcdui.Item item ) {
		// ignore
	}
	//#endif
	
	/**
	 * Replaces the item at the specified position in this list with the given item. 
	 * 
	 * @param index the position of the element, the first element has the index 0.
	 * @param item the item which should be set
	 * @return the replaced item
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item set( int index, Item item ) {
		return set( index, item, null );
	}
	/**
	 * Replaces the item at the specified position in this list with the given item. 
	 * 
	 * @param index the position of the element, the first element has the index 0.
	 * @param item the item which should be set
	 * @param itemStyle the new style for the item
	 * @return the replaced item
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item set( int index, Item item, Style itemStyle ) {
		//#debug
		System.out.println("Container: setting item " + index + " " + item.toString() );
		Item last = get( index );
		if (last == item && (itemStyle == null || itemStyle == item.style)) {
			// ignore, the same item is set over again:
			//#debug
			System.out.println("set: ignoring re-setting of the same item");
			return last;
		}
		item.parent = this;
		boolean focusNewItem = (index == this.focusedIndex) && (last.isFocused);
		this.itemsList.set(index, item);
		if (itemStyle != null) {
			item.setStyle(itemStyle);
		}
		if (index == this.focusedIndex) {
			if ( item.appearanceMode != PLAIN ) {
				if (focusNewItem) {
					this.focusedItem = null;
					focusChild( index, item, 0, true );
				} else {
					this.focusedItem = item;
				}
			} else {
				focusChild( -1 );
			}
		}
		if (this.enableScrolling && (this.focusedIndex == -1 || index <= this.focusedIndex )) {
			int offset = getScrollYOffset() + last.itemHeight;
			if (offset > 0) {
				offset = 0;
			}
			setScrollYOffset(offset, true);
		}
		//#if polish.css.focus-all
			if (this.isFocused && this.isFocusAllChildren && !item.isFocused) {
				Style itemFocusedStyle = item.getFocusedStyle();
				if (itemFocusedStyle != this.style && itemFocusedStyle != StyleSheet.focusedStyle) {
					if (item.style != null) {
						item.setAttribute(KEY_ORIGINAL_STYLE, item.style);
					}
					item.focus(itemFocusedStyle, 0);
				}
				
			}
		//#endif
		//requestInit();
		// set following items to relativeY=0, so that they will be scrolled correctly:
		for (int i= index + 1; i < this.itemsList.size(); i++ ) {
			Item followingItem = get(i);
			followingItem.relativeY = 0;
		}
		requestInit();
		repaint();
		if (this.isShown) {
			item.showNotify();
		}
		notifyValueChanged(item);
		return last;
	}
	
	/**
	 * Returns the item at the specified position of this container.
	 *  
	 * @param index the position of the desired item.
	 * @return the item stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item get( int index ) {
		return (Item) this.itemsList.get( index );
	}
	
	/**
	 * Removes the item at the specified position of this container.
	 *  
	 * @param index the position of the desired item.
	 * @return the item stored at the given position
	 * @throws IndexOutOfBoundsException when the index < 0 || index >= size()
	 */
	public Item remove( int index ) {
		Item removedItem = null;
		//#if polish.blackberry
			// when the currently focused item is removed and this contains a native blackberry field,
			// this can cause deadlocks with Container.initContent().
			synchronized (de.enough.polish.blackberry.midlet.MIDlet.getEventLock()) {
		//#endif
		synchronized (this.itemsList) {
			removedItem = (Item) this.itemsList.remove(index);
			if (removedItem == this.scrollItem) {
				this.scrollItem = null;
			}
			//#debug
			System.out.println("Container: removing item " + index + " " + removedItem.toString()  );
			// adjust y-positions of following items:
			//this.items = null;
			Object[] myItems = this.itemsList.getInternalArray();
			int removedItemHeight = removedItem.itemHeight + this.paddingVertical;
			//#if tmp.supportViewType
				if (this.containerView == null) {
			//#endif
					for (int i = index; i < myItems.length; i++) {
						Item item = (Item) myItems[i];
						if (item == null) {
							break;
						}
						item.relativeY -= removedItemHeight;
					}
			//#if tmp.supportViewType
				}
			//#endif
			// check if the currenlty focused item has been removed:
			if (index == this.focusedIndex) {
				this.focusedItem = null;
				removedItem.defocus(this.itemStyle);
				//#if tmp.supportViewType
					if (this.containerView != null) {
						this.containerView.focusedIndex = -1;
						this.containerView.focusedItem = null;
					}
				//#endif
				// remove any item commands:
				Screen scr = getScreen();
				if (scr != null) {
					scr.removeItemCommands(removedItem);
				}
				// focus the first possible item:
				if (index >= this.itemsList.size()) {
					index = this.itemsList.size() - 1;
				}
				if (index != -1) { 
					Item item = (Item) myItems[ index ];
					if (item.appearanceMode != PLAIN) {
						focusChild( index, item, Canvas.DOWN, true );
					} else {
						focusClosestItem(index);
					}
				} else {
					this.focusedIndex = -1;
					this.autoFocusEnabled = true;
					this.autoFocusIndex = 0;
				}
			} else if (index < this.focusedIndex) {
				//#if tmp.supportViewType
					if (this.containerView != null) {
						this.containerView.focusedIndex--;
					} else {
				//#endif
						int offset = getScrollYOffset() + removedItemHeight;
						//System.out.println("new container offset: from " + this.yOffset + " to " + (offset > 0 ? 0 : offset));
						setScrollYOffset( offset > 0 ? 0 : offset, false );
				//#if tmp.supportViewType
					}
				//#endif
				this.focusedIndex--;
			}
			setInitialized(false);
			if (this.parent != null) {
				this.parent.setInitialized(false);
			}
			if (this.isShown) {
				removedItem.hideNotify();
			}
		}
		//#if polish.blackberry
			}
		//#endif
		repaint();
		notifyValueChanged(removedItem);
		return removedItem;
	}
	
	/**
	 * Focuses the next focussable item starting at the specified index + 1. 
	 * @param index the index of the item that should be used as a starting point for the search of a new possible focussable item
	 * @return true when the focus could be set, when false is returned autofocus will be enabled instead
	 */
	public boolean focusClosestItemAbove( int index) {
		//#debug
		System.out.println("focusClosestItemAbove(" + index + ")");
		Item[] myItems = getItems();
		Item newFocusedItem = null;
		int newFocusedIndex = -1;
		for (int i = index -1; i >= 0; i--) {
			Item item = myItems[i];
			if (item.appearanceMode != PLAIN) {
				newFocusedIndex = i;
				newFocusedItem = item;
				break;
			}
		}
		if (newFocusedItem == null) {
			for (int i = index + 1; i < myItems.length; i++) {
				Item item = myItems[i];
				if (item.appearanceMode != PLAIN) {
					newFocusedIndex = i;
					newFocusedItem = item;
					break;
				}
			}			
		}
		if (newFocusedItem != null) {
			int direction = Canvas.DOWN;
			if (newFocusedIndex < index) {
				direction = Canvas.UP;
			}
			focusChild( newFocusedIndex, newFocusedItem, direction, true );
		} else {
			this.autoFocusEnabled = true;
			this.focusedItem = null;
			this.focusedIndex = -1;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
		}
		return (newFocusedItem != null);
	}

	/**
	 * Focuses the next focussable item starting at the specified index +/- 1. 
	 * @param index the index of the item that should be used as a starting point for the search of a new possible focussable item
	 * @return true when the focus could be set, when false is returned autofocus will be enabled instead
	 */
	public boolean focusClosestItem( int index) {
		//#debug
		System.out.println("focusClosestItem(" + index + ")");
		int i = 1;
		Item newFocusedItem = null;
		Item item;
		boolean continueFocus = true;
		Object[] myItems = this.itemsList.getInternalArray();
		int size = this.itemsList.size();
		while (continueFocus) {
			continueFocus = false;
			int testIndex = index + i;
			if (testIndex < size) {
				item = (Item) myItems[ testIndex ];
				if (item == null) {
					break;
				}
				if (item.appearanceMode != Item.PLAIN) {
					newFocusedItem = item;
					i = testIndex;
					break;
				}
				continueFocus = true;
			}
			testIndex = index - i;
			if (testIndex >= 0) {
				item = (Item) myItems[ testIndex ];
				if (item.appearanceMode != Item.PLAIN) {
					i = testIndex;
					newFocusedItem = item;
					break;
				}
				continueFocus = true;
			}
			i++;
		}
		if (newFocusedItem != null) {
			int direction = Canvas.DOWN;
			if (i < index) {
				direction = Canvas.UP;
			}
			focusChild( i, newFocusedItem, direction, true );
		} else {
			this.autoFocusEnabled = true;
			this.focusedItem = null;
			this.focusedIndex = -1;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
		}
		return (newFocusedItem != null);
	}
	
	/**
	 * Removes the given item.
	 * 
	 * @param item the item which should be removed.
	 * @return true when the item was found in this list.
	 * @throws IllegalArgumentException when the given item is null
	 */
	public boolean remove( Item item ) {
		int index = this.itemsList.indexOf(item);
		if (index != -1) {
			remove( index );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes all items from this container.
	 */
	public void clear() {
		//System.out.println("CLEARING CONTAINER " + this);
		synchronized (this.itemsList) {
			//#if tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
			//System.out.println("clearing container - focusedItem=" + this.focusedItem + ", isFocused="  + this.isFocused + ", focusedIndex=" + this.focusedIndex + ",  size=" + this.size() + ", itemStyle=" + this.itemStyle );
			this.scrollItem = null;
			if (this.isShown) {
				Object[] myItems = this.itemsList.getInternalArray();
				for (int i = 0; i < myItems.length; i++) {
					Item item = (Item) myItems[i];
					if (item == null) {
						break;
					}
					item.hideNotify();
				}
			}
			this.itemsList.clear();
			this.containerItems = new Item[0];
			//this.items = new Item[0];
			if (this.focusedIndex != -1) {
				this.autoFocusEnabled = this.isFocused;
				//#if polish.Container.clearResetsFocus != false
					this.autoFocusIndex = 0;
				//#else
					this.autoFocusIndex = this.focusedIndex;
				//#endif			
				this.focusedIndex = -1;
				if (this.focusedItem != null) {
					if (this.itemStyle != null) {
						//System.out.println("Container.clear(): defocusing current item " + this.focusedItem);
						this.focusedItem.defocus(this.itemStyle);
					} 
					if (this.focusedItem.commands != null) {
						Screen scr = getScreen();
						if (scr != null) {
							scr.removeItemCommands(this.focusedItem);
						}
					}
				}
				this.focusedItem = null;
			}
			this.yOffset = 0;
			this.targetYOffset = 0;
			if (this.internalX != NO_POSITION_SET) {
				this.internalX = NO_POSITION_SET;
				this.internalY = 0;
			}
				// adjust scrolling:
				if ( this.isFocused && this.parent instanceof Container ) {
					Container parentContainer = (Container) this.parent;
					int scrollOffset = - parentContainer.getScrollYOffset();
					if (scrollOffset > this.relativeY) {
						int diff = scrollOffset - this.relativeY;
						parentContainer.setScrollYOffset( diff - scrollOffset,  false );
					}
				}
			//}
			this.contentHeight = 0;
			this.contentWidth = 0;
			this.itemHeight = this.marginTop + this.paddingTop + this.paddingBottom + this.marginBottom;
			this.itemWidth = this.marginLeft + this.paddingLeft + this.paddingRight + this.marginRight;
			if (isInitialized()) {
				setInitialized(false);
				//this.yBottom = this.yTop = 0;
				repaint();
			}
		}
	}
	
	/**
	 * Retrieves the number of items stored in this container.
	 * 
	 * @return The number of items stored in this container.
	 */
	public int size() {
		return this.itemsList.size();
	}
	
	/**
	 * Retrieves all items which this container holds.
	 * The items might not have been intialised.
	 * 
	 * @return an array of all items, can be empty but not null.
	 */
	public Item[] getItems() {
		if (!isInitialized() || this.containerItems == null) {
			this.containerItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		}
		return this.containerItems;
	}
	
	/**
	 * Sets the focus listener.
	 * @param listener the new listener, use null to remove an existing listener.
	 * @see #getFocusListener()
	 */
	public void setFocusListener( FocusListener listener ) {
		this.focusListener = listener;
	}
	
	/**
	 * Retrieves the focus listener
	 * @return the listener, may be null
	 * @see #setFocusListener(FocusListener)
	 */
	public FocusListener getFocusListener() {
		return this.focusListener;
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item. The first item has the index 0, 
	 * 		when -1 is given, the focus will be removed altogether 
	 * @return true when the specified item could be focused.
	 * 		   It needs to have an appearanceMode which is not Item.PLAIN to
	 *         be focusable.
	 */
	public boolean focusChild(int index) {
		if (index == -1) {
			this.focusedIndex = -1;
			Item item = this.focusedItem; 
			if (item != null && this.itemStyle != null && item.isFocused) {
				item.defocus( this.itemStyle );
			}
			this.focusedItem = null;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focusedIndex = -1;
					this.containerView.focusedItem = null;
				}
			//#endif
			//#if polish.blackberry
				if (this.isFocused) {
					if(getScreen() != null) {
						getScreen().notifyFocusSet(this);
					} else {
						Display.getInstance().notifyFocusSet(this);
					}
				}
			//#endif
			if (this.focusListener != null) {
				this.focusListener.onFocusChanged(this, null, -1);
			}
			return true;
		}
		if (!this.isFocused) {
			this.autoFocusEnabled = true;
		}
		Item item = get(index );
		if (item.appearanceMode != Item.PLAIN) {
			int direction = 0;
			if (this.isFocused) {
				if (this.focusedIndex == -1) {
					// nothing
				} else if (this.focusedIndex < index ) {
					direction = Canvas.DOWN;
				} else if (this.focusedIndex > index) {
					direction = Canvas.UP;
				}
			
			}
			focusChild( index, item, direction, true);			
			return true;
		}
		return false;
	}
	
	
	
	
	//#if polish.hasPointerEvents
		//#ifdef tmp.supportFocusItemsInVisibleContentArea
			/**
			 * Checks if an item in the visible range is
			 *  
			 * @param item the Item to check
			 * @return true if item is in visible content area else false 
			 */
			private boolean isItemInVisibleContentArea(Item item){
				if(item == null) {
					return false;
				}
				int relY = (item.getAbsoluteY() + this.getCurrentScrollYOffset() - this.getScrollYOffset());
				if(relY <= this.getAvailableContentHeight() && relY >= 0){
					return true;
				}
				return false;
			}
			
			/**
			 * Find the position of first item in visible area
			 * 
			 * @param isInteractive true to check only interactive items else false
			 * @return the position of the first item in visible area or -1 if no item was found
			 */
			private int getFirstItemInVisibleContentArea(boolean isInteractive){
				Item[] items = this.getItems();
				for(int i = 0; i < items.length; i++){			
					Item item = items[i];
					if((!isInteractive || item.isInteractive()) && isItemInVisibleContentArea(item)) {
						return i;
					}
				}
				return -1;
			}
			
			/**
			 * Find the position of last item in visible area
			 * 
			 * @param isInteractive true to check only interactive items else false
			 * @return the position of the last item in visible area or -1 if no item was found
			 */
			private int getLastItemInVisibleContentArea(boolean isInteractive){
				Item[] items = this.getItems();
				for(int i = items.length-1; i > 0 ; i--){
					Item item = items[i];
					if((!isInteractive || item.isInteractive()) && isItemInVisibleContentArea(item)) {
						return i;
					}
				}
				return -1;
			}
		//#endif
	//#endif
	
	
	/**
	 * Sets the focus to the given item.
	 * 
	 * @param index the position
	 * @param item the item which should be focused
	 * @param direction the direction, either Canvas.DOWN, Canvas.RIGHT, Canvas.UP, Canvas.LEFT or 0.
	 * @param force true when the child should be focused again even though is has been focused before
	 */
	public void focusChild( int index, Item item, int direction, boolean force ) {
		//#debug
		System.out.println("Container (" + this + "): Focusing child item " + index + " (" + item + "), isInitialized=" + this.isInitialized + ", autoFocusEnabled=" + this.autoFocusEnabled );
		//System.out.println("focus: yOffset=" + this.yOffset + ", targetYOffset=" + this.targetYOffset + ", enableScrolling=" + this.enableScrolling + ", isInitialized=" + this.isInitialized );
		
		if (!isInitialized() && this.autoFocusEnabled) {
			// setting the index for automatically focusing the appropriate item
			// during the initialization:
			//#debug
			System.out.println("Container: Setting autofocus-index to " + index );
			this.autoFocusIndex = index;
		} 
		if (this.isFocused) {
			this.autoFocusEnabled = false;
		}
		
		if (index == this.focusedIndex && item.isFocused && item == this.focusedItem) {
			//#debug
			System.out.println("Container: ignoring focusing of item " + index );
			//#ifdef polish.css.view-type
				if (this.containerView != null && this.containerView.focusedIndex != index) {
					this.containerView.focusedItem = item;
					this.containerView.focusedIndex = index;
				}
			//#endif
			// ignore the focusing of the same element:
			return;
		}
		
		//#if polish.blackberry
			if(getScreen() != null) {
				getScreen().notifyFocusSet(item);
			} else {
				Display.getInstance().notifyFocusSet(item);
			}
		//#endif

		// indicating if either the former focusedItem or the new focusedItem has changed it's size or it's layout by losing/gaining the focus, 
		// of course this can only work if this container is already initialized:
		boolean isReinitializationRequired = false;
		// first defocus the last focused item:
		Item previouslyFocusedItem = this.focusedItem;
		if (previouslyFocusedItem != null) {
			int wBefore = previouslyFocusedItem.itemWidth;
			int hBefore = previouslyFocusedItem.itemHeight;
			int layoutBefore = previouslyFocusedItem.layout;
			if (this.itemStyle != null) {
				previouslyFocusedItem.defocus(this.itemStyle);
			} else {
				//#debug error
				System.out.println("Container: Unable to defocus item - no previous style found.");
				previouslyFocusedItem.defocus( StyleSheet.defaultStyle );
			}
			if (isInitialized()) {
				//fix 2008-11-11: width given to an item can be different from availableContentWidth on ContainerViews:
				//int wAfter = previouslyFocusedItem.getItemWidth( this.availableContentWidth, this.availableContentWidth, this.availableHeight );
				//fix 2008-12-10: on some ContainerViews it can happen, that not all items have been initialized before:
				//int wAfter = item.getItemWidth( item.availableWidth, item.availableWidth, item.availableHeight );
				int wAfter = getChildWidth(item);
				int hAfter = previouslyFocusedItem.itemHeight;
				int layoutAfter = previouslyFocusedItem.layout;
				if (wAfter != wBefore || hAfter != hBefore || layoutAfter != layoutBefore ) {
					//#debug
					System.out.println("dimension changed from " + wBefore + "x" + hBefore + " to " + wAfter + "x" + hAfter + " for previous " + previouslyFocusedItem);
					isReinitializationRequired = true;
					//#if tmp.supportViewType
						if (this.containerView != null) {
							previouslyFocusedItem.setInitialized(false); // could be that a container view poses restrictions on the possible size, i.e. within a table
						}
					//#endif
				}
			}
		}
		int wBefore = item.itemWidth;
		int hBefore = item.itemHeight;
		int layoutBefore = item.layout;
		Style newStyle = getFocusedStyle( index, item);
		boolean isDownwards = (direction == Canvas.DOWN) || (direction == Canvas.RIGHT) || (direction == 0 &&  index > this.focusedIndex);
		int previousIndex = this.focusedIndex; // need to determine whether the user has scrolled from the bottom to the top
		this.focusedIndex = index;
		this.focusedItem = item;
		int scrollOffsetBeforeScroll = getScrollYOffset();
		//#if tmp.supportViewType
			if ( this.containerView != null ) {
				this.itemStyle =  this.containerView.focusItem( index, item, direction, newStyle );
			} else {
		//#endif
				this.itemStyle = item.focus( newStyle, direction );
		//#if tmp.supportViewType
			} 
		//#endif
		//#ifdef polish.debug.error
			if (this.itemStyle == null) {
				//#debug error 
				System.out.println("Container: Unable to retrieve style of item " + item.getClass().getName() );
			}
		//#endif
		//System.out.println("focus - still initialzed=" + this.isInitialized + " for " + this);
		if  (isInitialized()) {
			// this container has been initialised already,
			// so the dimensions are known.
			//System.out.println("focus: contentWidth=" + this.contentWidth + ", of container " + this);
			//int wAfter = item.getItemWidth( this.availableContentWidth, this.availableContentWidth, this.availableHeight );
			// fix 2008-11-11: availableContentWidth can be different from the width granted to items in a ContainerView: 
			int wAfter = getChildWidth( item );
			int hAfter = item.itemHeight;
			int layoutAfter = item.layout;
			if (wAfter != wBefore || hAfter != hBefore || layoutAfter != layoutBefore ) {
				//#debug
				System.out.println("dimension changed from " + wBefore + "x" + hBefore + " to " + wAfter + "x" + hAfter + " for next " + item);
				isReinitializationRequired = true;
				//#if tmp.supportViewType
					if (this.containerView != null) {
						item.setInitialized(false); // could be that a container view poses restrictions on the possible size, i.e. within a table
					}
				//#endif
			}
			updateInternalPosition(item);
			if (getScrollHeight() != -1) {	
				// Now adjust the scrolling:			
				Item nextItem;
				if ( isDownwards && index < this.itemsList.size() - 1 ) {
					nextItem = get( index + 1 );
					//#debug
					System.out.println("Focusing downwards, nextItem.relativY = [" + nextItem.relativeY + "], focusedItem.relativeY=[" + item.relativeY + "], this.yOffset=" + this.yOffset + ", this.targetYOffset=" + this.targetYOffset);
				} else if ( !isDownwards && index > 0 ) {
					nextItem = get( index - 1 );
					//#debug
					System.out.println("Focusing upwards, nextItem.yTopPos = " + nextItem.relativeY + ", focusedItem.relativeY=" + item.relativeY );
				} else {
					//#debug
					System.out.println("Focusing last or first item.");
					nextItem = item;
				}
				if (getScrollYOffset() == scrollOffsetBeforeScroll) {
					if ( this.enableScrolling && ((isDownwards && (index < previousIndex) || (previousIndex == -1))) ) {
						// either the first item or the first selectable item has been focused, so scroll to the very top:
						//#if tmp.supportViewType
						if (this.containerView == null || !this.containerView.isVirtualContainer()) 
						//#endif
						{
							setScrollYOffset(0, true);
						}
					} else {
						int itemYTop = isDownwards ? item.relativeY : nextItem.relativeY;
						int itemYBottom = isDownwards ? nextItem.relativeY + nextItem.itemHeight : item.relativeY + item.itemHeight;
						int height = itemYBottom - itemYTop;
	                    //System.out.println("scrolling for item " + item + ", nextItem=" + nextItem + " in " + this + " with relativeY=" + this.relativeY + ", itemYTop=" + itemYTop);
						scroll( direction, this.relativeX, itemYTop, item.internalWidth, height, force );
					}
				}
			}
		} else if (getScrollHeight() != -1) { // if (this.enableScrolling) {
			//#debug
			System.out.println("focus: postpone scrolling to initContent() for " + this + ", item " + item);
			this.isScrollRequired = true;
		}
		if (isInitialized()) {
			setInitialized(!isReinitializationRequired);
		} else if (this.contentWidth != 0) {
			updateInternalPosition(item);
		}
		
		//#if polish.Container.notifyFocusChange
			notifyStateChanged();
		//#endif
		if (this.focusListener != null) {
			this.focusListener.onFocusChanged(this, this.focusedItem, this.focusedIndex);
		}
	}

	/**
	 * Queries the width of an child item of this container.
	 * This allows subclasses to control the possible re-initialization that is happening here.
	 * Also ContainerViews can override the re-initialization in their respective getChildWidth() method.
	 * @param item the child item
	 * @return the width of the child item
	 * @see #getChildHeight(Item)
	 * @see ContainerView#getChildWidth(Item)
	 */
	protected int getChildWidth(Item item) {
		//#if tmp.supportViewType
			ContainerView contView = this.containerView;
			if (contView != null) {
				return contView.getChildWidth(item);
			}
		//#endif
		int w;
		if (item.availableWidth > 0) {
			w = item.getItemWidth( item.availableWidth, item.availableWidth, item.availableHeight );
		} else {
			w = item.getItemWidth( this.availContentWidth, this.availContentWidth, this.availContentHeight );
		}
		return w;
	}
	
	/**
	 * Queries the height of an child item of this container.
	 * This allows subclasses to control the possible re-initialization that is happening here.
	 * Also ContainerViews can override the re-initialization in their respective getChildHeight() method.
	 * @param item the child item
	 * @return the height of the child item
	 * @see #getChildHeight(Item)
	 * @see ContainerView#getChildHeight(Item)
	 */
	protected int getChildHeight(Item item) {
		//#if tmp.supportViewType
			ContainerView contView = this.containerView;
			if (contView != null) {
				return contView.getChildHeight(item);
			}
		//#endif
		int h;
		if (item.availableWidth > 0) {
			h = item.getItemHeight( item.availableWidth, item.availableWidth, item.availableHeight );
		} else {
			h = item.getItemHeight( this.availContentWidth, this.availContentWidth, this.availContentHeight );
		}
		return h;
	}


	/**
	 * Retrieves the best matching focus style for the given item
	 * @param index the index of the item
	 * @param item the item
	 * @return the matching focus style
	 */
	protected Style getFocusedStyle(int index, Item item)
	{
		Style newStyle = item.getFocusedStyle();
		//#if polish.css.focused-style-first
			if (index == 0 && this.focusedStyleFirst != null) {
				newStyle = this.focusedStyleFirst;
			}
		//#endif
		//#if polish.css.focused-style-last
			if (this.focusedStyleLast != null  && index == this.itemsList.size() - 1) {
				newStyle = this.focusedStyleLast;
			}
		//#endif
		return newStyle;
	}

	/**
	 * Scrolls this container so that the (internal) area of the given item is best seen.
	 * This is used when a GUI even has been consumed by the currently focused item.
	 * The call is fowarded to scroll( direction, x, y, w, h ).
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param item the item for which the scrolling should be adjusted
	 * @return true when the container was scrolled
	 */
	public boolean scroll(int direction, Item item, boolean force) {
		//#debug
		System.out.println("scroll: scrolling for item " + item  + ", item.internalX=" + item.internalX +", relativeInternalY=" + ( item.relativeY + item.contentY + item.internalY ) + ", relativeY=" + item.relativeY + ", contentY=" + item.contentY + ", internalY=" + item.internalY);
		if ( (item.internalX != NO_POSITION_SET) 
                && ( (item.itemHeight > getScrollHeight()) || ( (item.internalY + item.internalHeight) > item.contentHeight ) )
        ) {
			// use internal position of item for scrolling:
			//System.out.println("using internal area for scrolling");
			int relativeInternalX = item.relativeX + item.contentX + item.internalX;
			int relativeInternalY = item.relativeY + item.contentY + item.internalY;
			return scroll(  direction, relativeInternalX, relativeInternalY, item.internalWidth, item.internalHeight, force );
		} else {
			if (!isInitialized() && item.relativeY == 0) {
				// defer scrolling to init at a later stage:
				//System.out.println( this + ": setting scrollItem to " + item);
				synchronized(this.itemsList) {
					this.scrollItem = item;
				}
				return true;
			} else {				
				// use item dimensions for scrolling:
				//System.out.println("use item area for scrolling");
				return scroll(  direction, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight, force );
			}
		}
	}
	
	/**
	 * Adjusts the yOffset or the targetYOffset so that the given relative values are inside of the visible area.
	 * The call is forwarded to a parent container when scrolling is not enabled for this item.
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param x the horizontal position of the area relative to this content's left edge, is ignored in the current version
	 * @param y the vertical position of the area relative to this content's top edge
	 * @param width the width of the area
	 * @param height the height of the area
	 * @return true when the scroll request changed the internal scroll offsets
	 */
	protected boolean scroll( int direction, int x, int y, int width, int height ) {
		return scroll( direction, x, y, width, height, false );
	}
	
	/**
	 * Adjusts the yOffset or the targetYOffset so that the given relative values are inside of the visible area.
	 * The call is forwarded to a parent container when scrolling is not enabled for this item.
	 * 
	 * @param direction the direction, is used for adjusting the scrolling when the internal area is to large. Either 0 or Canvas.UP, Canvas.DOWN, Canvas.LEFT or Canvas.RIGHT
	 * @param x the horizontal position of the area relative to this content's left edge, is ignored in the current version
	 * @param y the vertical position of the area relative to this content's top edge
	 * @param width the width of the area
	 * @param height the height of the area
	 * @param force true when the area should be shown regardless where the the current scrolloffset is located
	 * @return true when the scroll request changed the internal scroll offsets
	 */
	protected boolean scroll( int direction, int x, int y, int width, int height, boolean force ) {
		//#debug
		System.out.println("scroll: direction=" + direction + ", y=" + y + ", availableHeight=" + this.scrollHeight +  ", height=" +  height + ", focusedIndex=" + this.focusedIndex + ", yOffset=" + this.yOffset + ", targetYOffset=" + this.targetYOffset +", numberOfItems=" + this.itemsList.size() + ", in " + this + ", downwards=" + (direction == Canvas.DOWN || direction == Canvas.RIGHT ||  direction == 0));
		if (!this.enableScrolling) {
			if (this.parent instanceof Container) {
				x += this.contentX + this.relativeX;
				y += this.contentY + this.relativeY;
				//#debug
				System.out.println("Forwarding scroll request to parent now with y=" + y);
				return ((Container)this.parent).scroll(direction, x, y, width, height, force );
			}
			return false;
		}
		if ( height == 0) {
			return false;
		}
		// assume scrolling down when the direction is not known:
		boolean isDownwards = (direction == Canvas.DOWN || direction == Canvas.RIGHT ||  direction == 0);
		boolean isUpwards = (direction == Canvas.UP );
		
		int currentYOffset = this.targetYOffset; // yOffset starts at 0 and grows to -contentHeight + lastItem.itemHeight
		//#if polish.css.scroll-mode
			if (!this.scrollSmooth) {
				currentYOffset = this.yOffset;
			}
		//#endif
		int originalYOffset = currentYOffset;

		int verticalSpace = this.scrollHeight - (this.contentY + this.marginBottom + this.paddingBottom + getBorderWidthBottom()); // the available height for this container
		
		int yTopAdjust = 0;
		Screen scr = this.screen;
		boolean isCenterOrBottomLayout = (this.layout & LAYOUT_VCENTER) == LAYOUT_VCENTER || (this.layout & LAYOUT_BOTTOM) == LAYOUT_BOTTOM;
		if (isCenterOrBottomLayout && (scr != null && this == scr.container && this.relativeY > scr.contentY)) {
			// this is an adjustment for calculating the correct scroll offset for containers with a vertical-center or bottom layout:
			yTopAdjust = this.relativeY - scr.contentY;
		}
		if ( y + height + currentYOffset + yTopAdjust > verticalSpace ) {
			// the area is too low, so scroll down (= increase the negative yOffset):
			//#debug
			System.out.println("scroll: item too low: verticalSpace=" + verticalSpace + "  y=" + y + ", height=" + height + ", yOffset=" + currentYOffset + ", yTopAdjust=" + yTopAdjust + ", relativeY=" + this.relativeY + ", screen.contentY=" +  scr.contentY + ", scr=" + scr);
			//currentYOffset += verticalSpace - (y + height + currentYOffset + yTopAdjust);
			int newYOffset = verticalSpace - (y + height + yTopAdjust);
			// check if the top of the area is still visible when scrolling downwards:
			if ( !isUpwards && y + newYOffset < 0) {
				newYOffset = -y;
			}
			if (isDownwards) {
				// check if we scroll down more than one page:
				int difference = 	Math.max(Math.abs(currentYOffset), Math.abs(newYOffset)) - 
				 					Math.min(Math.abs(currentYOffset), Math.abs(newYOffset));
				if (difference > verticalSpace && !force ) {
					newYOffset = currentYOffset - verticalSpace;
				}
			}
			currentYOffset = newYOffset;
		} else if ( y + currentYOffset < 0 ) {
			//#debug
			System.out.println("scroll: item too high: , y=" + y + ", current=" + currentYOffset + ", target=" + (-y) );
			
			int newYOffset = -y;
			// check if the bottom of the area is still visible when scrolling upwards:
			if (isUpwards && newYOffset + y + height > verticalSpace) { //  && height < verticalSpace) {
				//2008-12-10: scrolling upwards resulted in too large jumps when we have big items, so
				// adjust the offset in any case, not only when height is smaller than the vertical space (height < verticalSpace):
				newYOffset = -(y + height) + verticalSpace;
			}
			
			int difference = Math.max(Math.abs(currentYOffset), Math.abs(newYOffset)) - 
							 Math.min(Math.abs(currentYOffset), Math.abs(newYOffset)); 
			
			if (difference > verticalSpace && !force ) {
				newYOffset = currentYOffset + verticalSpace;
			}
			currentYOffset = newYOffset;
		} else {
			//#debug
			System.out.println("scroll: do nothing");
			return false;
		}
		if (currentYOffset != originalYOffset) {
			setScrollYOffset(currentYOffset, true);
			return true;
		} else {
			//#debug
			System.out.println("scroll: no change");
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setAppearanceMode(int)
	 */
	public void setAppearanceMode(int appearanceMode)
	{
		super.setAppearanceMode(appearanceMode);
		// this is used in initContent() to circumvent the 
		// reversal of the previously set appearance mode
		synchronized(this.itemsList) {
			this.appearanceModeSet = true;
		}
	}

	protected void initLayout(Style style, int availWidth) {
		//#ifdef polish.css.view-type
		if (this.containerView != null) {
			this.containerView.initPadding(style, availWidth);
		} else
		//#endif
		{
			initPadding(style, availWidth);
		}
		
		//#ifdef polish.css.view-type
		if (this.containerView != null) {
			this.containerView.initMargin(style, availWidth);
		} else
		//#endif
		{
			initMargin(style, availWidth);
		}
	}
	
	/**
	 * Retrieves the synchronization lock for this container.
	 * As a lock either the internal ArrayList for items is used or the paint lock of the screen when this container is associated with a screen.
	 * The lock can be used manipulate a Container that is currently displayed
	 * @return the synchronization lock
	 * @see Screen#getPaintLock()
	 * @see #add(Item)
	 */
	public Object getSynchronizationLock() {
		Object lock = this.itemsList;
		Screen scr = getScreen();
		if (scr != null) {
			lock = scr.getPaintLock();
		}
		return lock;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem( int, int )
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		//#debug
		System.out.println("Container: intialising content for " + this + ": autofocus=" + this.autoFocusEnabled + ", autoFocusIndex=" + this.autoFocusIndex + ", isFocused=" + this.isFocused + ", firstLineWidth=" + firstLineWidth + ", availWidth=" + availWidth + ", availHeight=" + availHeight + ", size=" + this.itemsList.size() );
		//this.availableContentWidth = firstLineWidth;
		//#if polish.css.focused-style
			if (this.focusedStyle != null) {
				this.focusedTopMargin = this.focusedStyle.getMarginTop(availWidth) + this.focusedStyle.getPaddingTop(availWidth);
				if (this.focusedStyle.border != null) {
					this.focusedTopMargin += this.focusedStyle.border.borderWidthTop;
				}
				if (this.focusedStyle.background != null) {
					this.focusedTopMargin += this.focusedStyle.background.borderWidth;
				}
			}
		//#endif
		synchronized (this.itemsList) {
			int myContentWidth = 0;
			int myContentHeight = 0;
			Item[] myItems;
			if (this.containerItems == null || this.containerItems.length != this.itemsList.size()) {
				myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
				this.containerItems = myItems;
			} else {
				myItems = (Item[]) this.itemsList.toArray(this.containerItems);
			}
			//#if (polish.css.child-style-first || polish.css.child-style-last) && polish.css.child-style
				if (this.style != null && this.childStyle != null) {
					Style firstStyle = null;
					//#if polish.css.child-style-first					
						firstStyle = (Style) this.style.getObjectProperty("child-style-first");
					//#endif
					Style lastStyle = null;
					//#if polish.css.child-style-last					
						lastStyle = (Style) this.style.getObjectProperty("child-style-last");
					//#endif
					if (firstStyle != null || lastStyle != null) {
						int lastIndex = myItems.length - 1;
						for (int i = 0; i < myItems.length; i++) {
							Item item = myItems[i];
							if (item.style == null) {
								item.setStyle( this.childStyle );
							}
							if (i != 0 && item.style == firstStyle) {
								item.setStyle( this.childStyle );
							}
							if (i != lastIndex && item.style == lastStyle) {
								item.setStyle( this.childStyle );
							}
							if (i == 0 && firstStyle != null && item.style != firstStyle) {
								item.setStyle( firstStyle );
							}
							if (i == lastIndex && lastStyle != null && item.style != lastStyle) {
								item.setStyle( lastStyle );
							}
						}
					}
				}
			//#endif
			if (this.autoFocusEnabled && this.autoFocusIndex >= myItems.length ) {
				this.autoFocusIndex = 0;
			}
			//#if polish.Container.allowCycling != false
				if (this.focusedItem instanceof Container && ((Container)this.focusedItem).allowCycling && getNumberOfInteractiveItems() > 1) {
					((Container)this.focusedItem).allowCycling = false;
				}
				Item ancestor = this.parent;
				while (this.allowCycling && ancestor != null) {
					if ( (ancestor instanceof Container)  && ((Container)ancestor).getNumberOfInteractiveItems()>1 ) {
						this.allowCycling = false;
						break;
					}
					ancestor = ancestor.parent;
				}
			//#endif
			//#if tmp.supportViewType
				if (this.containerView != null) {
					// additional initialization is necessary when a view is used for this container:
					boolean requireScrolling = this.isScrollRequired && this.isFocused;
	//				System.out.println("ABOUT TO CALL INIT CONTENT - focusedIndex of Container=" + this.focusedIndex);
					this.containerView.parentItem = this;
					this.containerView.parentContainer = this;
					this.containerView.init( this, firstLineWidth, availWidth, availHeight);
					if (this.defaultCommand != null || (this.commands != null && this.commands.size() > 0)) {
						this.appearanceMode = INTERACTIVE;
					} else if(!this.appearanceModeSet){
						this.appearanceMode = this.containerView.appearanceMode;
					}
					if (this.isFocused && this.autoFocusEnabled) {
						// #debug
						System.out.println("Container/View: autofocusing element starting at " + this.autoFocusIndex);
						if (this.autoFocusIndex >= 0 && this.appearanceMode != Item.PLAIN) {
							for (int i = this.autoFocusIndex; i < myItems.length; i++) {
								Item item = myItems[i];
								if (item.appearanceMode != Item.PLAIN) {
									// make sure that the item has applied it's own style first (not needed since it has been initialized by the container view already):
									//item.getItemHeight( firstLineWidth, lineWidth );
									// now focus the item:
									this.autoFocusEnabled = false;
									requireScrolling = (this.autoFocusIndex != 0);
//									int heightBeforeFocus = item.itemHeight;
									focusChild( i, item, 0, true);
									// outcommented on 2008-07-09 because this results in a wrong
									// available width for items with subsequent wrong getAbsoluteX() coordinates
//									int availableWidth = item.itemWidth;
//									if (availableWidth < this.minimumWidth) {
//										availableWidth = this.minimumWidth;
//									}
//									if (item.getItemHeight( availableWidth, availableWidth ) > heightBeforeFocus) {
//										item.isInitialized = false;
//										this.containerView.initContent( this, firstLineWidth, lineWidth);	
//									}
									this.isScrollRequired = this.isScrollRequired && requireScrolling; // override setting in focus()
									//this.containerView.focusedIndex = i; is done within focus(i, item, 0) already
									//this.containerView.focusedItem = item;
									//System.out.println("autofocus: found item " + i );
									break;
								}							
							}
						// when deactivating the auto focus the container won't initialize correctly after it has
						// been cleared and items are added subsequently one after another (e.g. like within the Browser).
	//					} else {
	//						this.autoFocusEnabled = false;
						}
					}
					this.contentWidth = this.containerView.contentWidth;
					this.contentHeight = this.containerView.contentHeight;

					if (requireScrolling && this.focusedItem != null) {
						//#debug
						System.out.println("initContent(): scrolling autofocused or scroll-required item for view, focused=" + this.focusedItem);
						Item item = this.focusedItem;
						scroll( 0, item.relativeX, item.relativeY, item.itemWidth, item.itemHeight, true );
					}
					else if (this.scrollItem != null) {
						//System.out.println("initContent(): scrolling scrollItem=" + this.scrollItem);
						boolean  scrolled = scroll( 0, this.scrollItem, true );
						if (scrolled) {
							this.scrollItem = null;
						}
					} 
					if (this.focusedItem != null) {
						updateInternalPosition(this.focusedItem);
					}
					return;
				}
			//#endif
		
			boolean isLayoutShrink = (this.layout & LAYOUT_SHRINK) == LAYOUT_SHRINK;
			boolean hasFocusableItem = false;
			int numberOfVerticalExpandItems = 0;
			Item lastVerticalExpandItem = null;
			int lastVerticalExpandItemIndex = 0;
			boolean hasCenterOrRightItems = false;
			boolean hasVerticalExpandItems = false;
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				if (item.isLayoutVerticalExpand()) {
					hasVerticalExpandItems = true;
					break;
				}
			}
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				if (hasVerticalExpandItems && item.isLayoutVerticalExpand()) {
					// re-initialize items when we have vertical-expand items, so that relativeY and itemHeight is correctly calculated 
					// with each run:
					item.setInitialized(false);
				}
				//System.out.println("initalising " + item.getClass().getName() + ":" + i);
				int width = item.getItemWidth( availWidth, availWidth, availHeight );
				int height = item.itemHeight; // no need to call getItemHeight() since the item is now initialised...
				// now the item should have a style, so it can be safely focused
				// without loosing the style information:
				//String toString = item.toString();
				//System.out.println("init of item " + i + ": height=" + height + " of item " + toString.substring( 19, Math.min(120, toString.length() )  ));
				//if (item.isInvisible && height != 0) {
				//	System.out.println("*** item.height != 0 even though it is INVISIBLE - isInitialized=" + item.isInitialized );
				//}
				if (item.appearanceMode != PLAIN) {
					hasFocusableItem = true;
				}
				if (this.isFocused && this.autoFocusEnabled  && (i >= this.autoFocusIndex ) && (item.appearanceMode != Item.PLAIN)) {
					this.autoFocusEnabled = false;
					//System.out.println("Container.initContent: auto-focusing " + i + ": " + item );
					focusChild( i, item, 0, true );
					this.isScrollRequired = (this.isScrollRequired || hasFocusableItem) && (this.autoFocusIndex != 0); // override setting in focus()
					height = item.getItemHeight(availWidth, availWidth, availHeight);
					if (!isLayoutShrink) {
						width = item.itemWidth;  // no need to call getItemWidth() since the item is now initialised...
					} else {
						width = 0;
					}
					if (this.enableScrolling && this.autoFocusIndex != 0) {
						//#debug
						System.out.println("initContent(): scrolling autofocused item, autofocus-index=" + this.autoFocusIndex + ", i=" + i  );
						scroll( 0, 0, myContentHeight, width, height, true );
					}
				} else if (i == this.focusedIndex) {
					if (isLayoutShrink) {
						width = 0;
					}
					if (this.isScrollRequired) {
						//#debug
						System.out.println("initContent(): scroll is required - scrolling to y=" + myContentHeight + ", height=" + height);
						scroll( 0, 0, myContentHeight, width, height, true );
						this.isScrollRequired = false;
	//				} else if (item.internalX != NO_POSITION_SET ) {
	//					// ensure that lines of textfields etc are within the visible area:
	//					scroll(0, item );
					}
				} 
				if (item.isLayoutVerticalExpand()) {
					numberOfVerticalExpandItems++;
					lastVerticalExpandItem = item;
					lastVerticalExpandItemIndex = i;
				} 
				if (width > myContentWidth) {
					myContentWidth = width; 
				}
				item.relativeY = myContentHeight;
				if (item.isLayoutCenter || item.isLayoutRight) {
					hasCenterOrRightItems = true;
					if (this.parent == null) {
						myContentWidth = availWidth;
					}
				} else {
					item.relativeX = 0;
				}				

				myContentHeight += height != 0 ? height + this.paddingVertical : 0;
				//System.out.println("item.yTopPos=" + item.yTopPos);
			} // cycling through all items
			
			if (numberOfVerticalExpandItems > 0 && myContentHeight < availHeight) {
				int diff = availHeight - myContentHeight;
				if (numberOfVerticalExpandItems == 1) {
					// this is a simple case:
					lastVerticalExpandItem.setItemHeight( lastVerticalExpandItem.itemHeight + diff );
					for (int i = lastVerticalExpandItemIndex+1; i < myItems.length; i++)
					{
						Item item = myItems[i];
						item.relativeY += diff;
					}
				} else {
					// okay, there are several items that would like to be expanded vertically:
//					System.out.println("having " + numberOfVerticalExpandItems + ", diff: " + diff + "=>" + (diff / numberOfVerticalExpandItems) + "=>" + ((diff / numberOfVerticalExpandItems) * numberOfVerticalExpandItems));
					diff = diff / numberOfVerticalExpandItems;
					int relYAdjust = 0;
					for (int i = 0; i < myItems.length; i++)
					{
						Item item = myItems[i];
//						System.out.println("changing relativeY from " + item.relativeY + " to " + (item.relativeY + relYAdjust) + ", relYAdjust=" + relYAdjust + " for " + item );
						item.relativeY +=  relYAdjust;
						if (item.isLayoutVerticalExpand()) {
//							System.out.println("changing itemHeight from " + item.itemHeight + " to " + (item.itemHeight + diff) + " for " + item);
							item.setItemHeight(item.itemHeight + diff );
							relYAdjust += diff;
						}
					}
				}
				myContentHeight = availHeight;
			}
			if (this.minimumWidth != null && this.minimumWidth.getValue(firstLineWidth) > myContentWidth) {
				myContentWidth = this.minimumWidth.getValue(firstLineWidth) - (getBorderWidthLeft() + getBorderWidthRight() + this.marginLeft + this.paddingLeft + this.marginRight + this.paddingRight);
			}
			//#if polish.css.expand-items
				if (this.isExpandItems) {
					for (int i = 0; i < myItems.length; i++)
					{
						Item item = myItems[i];
						if (!item.isLayoutExpand && item.itemWidth < myContentWidth) {
							item.setItemWidth( myContentWidth );
						}
					}
				}
			//#endif
			if (!hasFocusableItem) {
				if (this.defaultCommand != null || (this.commands != null && this.commands.size() > 0)) {
					this.appearanceMode = INTERACTIVE;
				} else if(!this.appearanceModeSet){
					this.appearanceMode = PLAIN;
				}
			} else {
				this.appearanceMode = INTERACTIVE;
				Item item = this.focusedItem;
				if (item == null) {
					this.internalX = NO_POSITION_SET;
				} else {
					updateInternalPosition(item);
					if (isLayoutShrink) {
						//System.out.println("container has shrinking layout and contains focused item " + item);
						boolean doExpand = item.isLayoutExpand;
						int width;
						if (doExpand) {
							item.setInitialized(false);
							item.isLayoutExpand = false;
							width = item.getItemWidth( availWidth, availWidth, availHeight );
							item.setInitialized(false);
							item.isLayoutExpand = true;
							if (width > myContentWidth) {
								myContentWidth = width;
							}
						}
						if ( this.minimumWidth != null && myContentWidth < this.minimumWidth.getValue(availWidth) ) {
							myContentWidth = this.minimumWidth.getValue(availWidth);
						}
						if (doExpand) {
							item.init(myContentWidth, myContentWidth, availHeight);
						}
						//myContentHeight += item.getItemHeight( lineWidth, lineWidth );
					}
				}
			}
			if (hasCenterOrRightItems) {
				int width;
				for (int i = 0; i < myItems.length; i++) {
					Item item = myItems[i];
					width = item.itemWidth;
					if (item.isLayoutCenter) {
						item.relativeX = (myContentWidth - width) / 2;
					} else if (item.isLayoutRight) {
						item.relativeX = (myContentWidth - width);
					}
				}
			}
			if (this.scrollItem != null) {
				boolean scrolled = scroll( 0, this.scrollItem, true );
				//System.out.println( this + ": scrolled scrollItem " + this.scrollItem + ": " + scrolled);
				if (scrolled) {
					this.scrollItem = null;
				}
			}
			this.contentHeight = myContentHeight;
			this.contentWidth = myContentWidth;
			//#debug
			System.out.println("initContent(): Container " + this + " has a content-width of " + this.contentWidth + ", parent=" + this.parent);
		}
	}
	
	/**
	 * Updates the internal position of this container according to the specified item's one
	 * @param item the (assumed focused) item
	 */
	protected void updateInternalPosition(Item item) {
		//#debug
		System.out.println("updating internal position of " + this + " for child " + item);
		if (item == null) {
			return;
		}
		int prevX = this.internalX;
		int prevY = this.internalY;
		int prevWidth = this.internalWidth;
		int prevHeight = this.internalHeight;
		if (item.internalX != NO_POSITION_SET) { // && (item.itemHeight > getScrollHeight()  || (item.contentY + item.internalY + item.internalHeight > item.itemHeight) ) ) {
			// adjust internal settings for root container:
			this.internalX = item.relativeX + item.contentX + item.internalX;
			if (this.enableScrolling) {
				this.internalY = getScrollYOffset() + item.relativeY + item.contentY + item.internalY;
			} else {
				this.internalY = item.relativeY + item.contentY + item.internalY;
			}
			this.internalWidth = item.internalWidth;
			this.internalHeight = item.internalHeight;
			//#debug
			System.out.println(this + ": Adjusted internal area by internal area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );
		} else {
			this.internalX = item.relativeX;
			if (this.enableScrolling) {
				this.internalY = getScrollYOffset() + item.relativeY;
			} else {
				this.internalY = item.relativeY;
			}
			this.internalWidth = item.itemWidth;
			this.internalHeight = item.itemHeight;
			//#debug
			System.out.println(this + ": Adjusted internal area by full area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );						
		}
		if (this.isFocused 
				&& this.parent instanceof Container
				&& (prevY != this.internalY || prevX != this.internalX || prevWidth != this.itemWidth || prevHeight != this.internalHeight)
		) {
			((Container)this.parent).updateInternalPosition( this );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setContentWidth(int)
	 */
	protected void setContentWidth(int width)
	{
		if (width < this.contentWidth) {
			initContent( width, width, this.availContentHeight);
		} else {
			super.setContentWidth(width);
			
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.setContentWidth( width );
				}
			//#endif
			
			if (this.focusedItem != null && (this.layout & LAYOUT_SHRINK) == LAYOUT_SHRINK) {
				this.focusedItem.init(width, width, this.contentHeight);
			}
		}
	}
	
	//#ifdef tmp.supportViewType
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setContentHeight(int)
	 */
	protected void setContentHeight(int height) {
		super.setContentHeight(height);
		if(this.containerView != null) {
			this.containerView.setContentHeight( height );
		}
	}
	//#endif
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setItemWidth(int)
	 */
	public void setItemWidth(int width) {
		int prevContentX = this.contentX;
		int myContentWidth = this.contentWidth + width - this.itemWidth;
		super.setItemWidth(width);
		//#ifdef tmp.supportViewType
		if (this.containerView == null) {
		//#endif
			boolean hasCenterOrRightAlignedItems = false;
			Item[] myItems = this.containerItems; 
			if (myItems != null) {
				for (int i = 0; i < myItems.length; i++) {
					Item item = myItems[i];
					width = item.itemWidth;
					if (item.isLayoutCenter) {
						item.relativeX = (myContentWidth - width) / 2;
						hasCenterOrRightAlignedItems = true;
					} else if (item.isLayoutRight) {
						item.relativeX = (myContentWidth - width);
						hasCenterOrRightAlignedItems = true;
					}
				}
				
			}
			if (hasCenterOrRightAlignedItems) {
				this.contentWidth = myContentWidth;
				this.contentX = prevContentX;
			}
		//#ifdef tmp.supportViewType
		}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		//System.out.println("paintContent, size=" + this.itemsList.size() + ", isInitialized=" + this.isInitialized);
//		System.out.println("paintContent with implicit width " + (rightBorder - leftBorder) + ", itemWidth=" + this.itemWidth + " of " + this ) ;
		// paints all items,
		// the layout will be done according to this containers'
		// layout or according to the items layout, when specified.
		// adjust vertical start for scrolling:
		//#if polish.debug.debug
			if (this.enableScrolling) {
//				g.setColor( 0xFFFF00 );
//				g.drawLine( leftBorder, y, rightBorder, y + getContentScrollHeight() );
//				g.drawLine( rightBorder, y, leftBorder, y  + + getContentScrollHeight() );
//				g.drawString( "" + this.availableHeight, x, y, Graphics.TOP | Graphics.LEFT );
				//#debug 
				System.out.println("Container: drawing " + this + " with yOffset=" + this.yOffset );
			}
		//#endif
		boolean setClipping = ( this.enableScrolling && (this.yOffset != 0 || this.itemHeight > this.scrollHeight) ); //( this.yOffset != 0 && (this.marginTop != 0 || this.paddingTop != 0) );
		int clipX = 0;
		int clipY = 0;
		int clipWidth = 0;
		int clipHeight = 0;
		if (setClipping) {
			clipX = g.getClipX();
			clipY = g.getClipY();
			clipWidth = g.getClipWidth();
			clipHeight = g.getClipHeight();
			Screen scr = this.screen;
			if (scr != null && scr.container == this &&  this.relativeY > scr.contentY ) {
				int diff = this.relativeY - scr.contentY;
				g.clipRect(clipX, y - diff, clipWidth, clipHeight - (y - clipY) + diff );				
			} else {
				//g.clipRect(clipX, y, clipWidth, clipHeight - (y - clipY) );
				// in this way we also clip the padding area at the bottom of the container (padding-bottom):
				g.clipRect(clipX, y, clipWidth, this.scrollHeight - this.paddingTop - this.paddingBottom );
			}
		}
		//x = leftBorder;
		y += this.yOffset;
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				//#debug
				System.out.println("forwarding paint call to " + this.containerView );
				this.containerView.paintContent( this, x, y, leftBorder, rightBorder, g);
				if (setClipping) {
					g.setClip(clipX, clipY, clipWidth, clipHeight);
				}
			} else {
		//#endif
			Item[] myItems = this.containerItems;
			int startY = g.getClipY();
			int endY = startY + g.getClipHeight();
			Item focItem = this.focusedItem;
			int focIndex = this.focusedIndex;
			int itemX;
			//int originalY = y;
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				// currently the NEWLINE_AFTER and NEWLINE_BEFORE layouts will be ignored,
				// since after every item a line break will be done. Use view-type: midp2; to place several items into a single row.
				int itemY = y + item.relativeY;
				if (i != focIndex &&  itemY + item.itemHeight >= startY && itemY < endY ){
					//item.paint(x, y, leftBorder, rightBorder, g);
					itemX = x + item.relativeX;
					item.paint(itemX, itemY, itemX, itemX + item.itemWidth, g);
				}
//				if (item.itemHeight != 0) {
//					y += item.itemHeight + this.paddingVertical;
//				}
			}
			boolean paintFocusedItemOutside = false;
			if (focItem != null) {
				paintFocusedItemOutside = setClipping && (focItem.internalX != NO_POSITION_SET);
				if (!paintFocusedItemOutside) {
					itemX = x + focItem.relativeX;
					focItem.paint(itemX, y + focItem.relativeY, itemX, itemX + focItem.itemWidth, g);
				}
			}
	
			if (setClipping) {
				g.setClip(clipX, clipY, clipWidth, clipHeight);
			}
			
			// paint the currently focused item outside of the clipping area when it has an internal area. This is 
			// for example useful for popup items that extend the actual container area.
			if (paintFocusedItemOutside) {
				//System.out.println("Painting focusedItem " + this.focusedItem + " with width=" + this.focusedItem.itemWidth + " and with increased colwidth of " + (focusedRightBorder - focusedX)  );
				itemX = x + focItem.relativeX;
				focItem.paint(itemX, y + focItem.relativeY, itemX, itemX + focItem.itemWidth, g);
			}
		//#ifdef tmp.supportViewType
			}
		//#endif
//		if (this.internalX != NO_POSITION_SET) {
//			g.setColor(0xff00);
//			g.drawRect( x + this.internalX, y + this.internalY, this.internalWidth, this.internalHeight );
//		}
	}
	
	//#if tmp.supportViewType
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#paintBackgroundAndBorder(int, int, int, int, javax.microedition.lcdui.Graphics)
		 */
		protected void paintBackgroundAndBorder(int x, int y, int width, int height, Graphics g) {
			if (this.containerView == null) {
				super.paintBackgroundAndBorder(x, y, width, height, g);
			} else {
				// this is only necessary since ContainerViews are integrated differently from
				// normal ItemViews - we should consider abonding this approach!
				//#if polish.css.bgborder
					if (this.bgBorder != null) {
						int bgX = x - this.bgBorder.borderWidthLeft;
						int bgW = width + this.bgBorder.borderWidthLeft + this.bgBorder.borderWidthRight;
						int bgY = y - this.bgBorder.borderWidthTop;
						int bgH = height + this.bgBorder.borderWidthTop + this.bgBorder.borderWidthBottom;
						this.containerView.paintBorder( this.bgBorder, bgX, bgY, bgW, bgH, g );
					}
				//#endif
				if ( this.background != null ) {
					int bWidthL = getBorderWidthLeft();
					int bWidthR = getBorderWidthRight();
					int bWidthT = getBorderWidthTop();
					int bWidthB = getBorderWidthBottom();
					if ( this.border != null ) {
						x += bWidthL;
						y += bWidthT;
						width -= bWidthL + bWidthR;
						height -= bWidthT + bWidthB;
					}
					this.containerView.paintBackground( this.background, x, y, width, height, g );
					if (this.border != null) {
						x -= bWidthL;
						y -= bWidthT;
						width += bWidthL + bWidthR;
						height += bWidthT + bWidthB;
					}
				}
				if ( this.border != null ) {
					this.containerView.paintBorder( this.border, x, y, width, height, g );
				}
			}
		}
	//#endif

	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getCssSelector()
	 */
	protected String createCssSelector() {
		return "container";
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println("handleKeyPressed( " + keyCode + ", " + gameAction + " ) for " + this + ", focusedItem=" + this.focusedItem);
		if (this.itemsList.size() == 0 && this.focusedItem == null) {
			return super.handleKeyPressed(keyCode, gameAction);
		}
		
		Item item = this.focusedItem;
		
		//looking for the next focusable Item if the focusedItem is not in 
		//the visible content area	
		//#ifdef tmp.supportFocusItemsInVisibleContentArea
			//#if polish.hasPointerEvents	
				
				if(this.needsCheckItemInVisibleContent && item != null && !isItemInVisibleContentArea(item) 
						&& (gameAction == Canvas.DOWN || gameAction == Canvas.UP || gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT)){
					int next = -1;
					int offset = 0;
					//System.out.println("tmp.supportFocusItemsInVisibleContentArea is set");		
					if(gameAction == Canvas.DOWN ){
						next = getFirstItemInVisibleContentArea(true);
						offset = getScrollYOffset()-(this.getAvailableContentHeight());
					}else if(gameAction == Canvas.UP ){
						next = getLastItemInVisibleContentArea(true);
						offset = getScrollYOffset()+(this.getAvailableContentHeight());
					}
					if(next != -1){
						focusChild( next, this.get(next), gameAction,  false );
						item = get(next);	
					}
					else{
						if(gameAction == Canvas.DOWN || gameAction == Canvas.UP ){
							boolean smooth = true;
							//#ifdef polish.css.scroll-mode
								smooth = this.scrollSmooth;
							//#endif			
							setScrollYOffset(offset, smooth);
						}
						return true;
					}
				}else{
					this.needsCheckItemInVisibleContent = false;
				}
			//#endif
		//#endif
		
		
		if (item != null) {
			if (!item.isInitialized()) {
				if (item.availableWidth != 0) {
					item.init( item.availableWidth, item.availableWidth, item.availableHeight );
				} else {
					item.init( this.contentWidth, this.contentWidth, this.contentHeight );
				}
			} else if (this.enableScrolling && item.internalX != NO_POSITION_SET) {
				int startY = getScrollYOffset() + item.relativeY + item.contentY + item.internalY;
				if ( (
					(startY < 0  && gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2) 
					||  (startY + item.internalHeight > this.scrollHeight  && gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)
					)
					&& (scroll(gameAction, item, false))
				){
					//System.out.println("scrolling instead of forwwarding key to child " + item + ", item.internalY=" + item.internalY + ", item.internalHeight=" + item.internalHeight + ", item.focused=" + (item instanceof Container ? item.relativeY + ((Container)item).focusedItem.relativeY : -1) );
					return true;
				}
			}
			int scrollOffset = getScrollYOffset();
			if ( item.handleKeyPressed(keyCode, gameAction) ) {
				//if (item.internalX != NO_POSITION_SET) {
					if (this.enableScrolling) {
						if (getScrollYOffset() == scrollOffset) {
							//#debug
							System.out.println("scrolling focused item that has handled key pressed, item=" + item + ", item.internalY=" + item.internalY);
							scroll(gameAction, item, false);
						}
					} else  {
						updateInternalPosition(item);
					}
				//}
				//#debug
				System.out.println("Container(" + this + "): handleKeyPressed consumed by item " + item.getClass().getName() + "/" + item );
				
				return true;
			}
		}
		
		return handleNavigate(keyCode, gameAction) || super.handleKeyPressed(keyCode, gameAction);
	}

	/**
	 * Handles a keyPressed or keyRepeated event for navigating in the container.
	 *  
	 * @param keyCode the code of the keypress/keyrepeat event
	 * @param gameAction the associated game action 
	 * @return true when the key was handled
	 */
	protected boolean handleNavigate(int keyCode, int gameAction) {
		// now allow a navigation within the container:
		boolean processed = false;
		int offset = getRelativeScrollYOffset();
		int availableScrollHeight = getScrollHeight();
		Item focItem = this.focusedItem;
		int y = 0;
		int h = 0;
		if (focItem != null && availableScrollHeight != -1) {
			if (focItem.internalX == NO_POSITION_SET || (focItem.relativeY + focItem.contentY + focItem.internalY + focItem.internalHeight < availableScrollHeight)) {
				y = focItem.relativeY;
				h = focItem.itemHeight;
				//System.out.println("normal item has focus: y=" + y + ", h=" + h + ", item=" + focItem);
			} else {
				y = focItem.relativeY + focItem.contentY + focItem.internalY;
				h = focItem.internalHeight;
				//System.out.println("internal item has focus: y=" + y + ", h=" + h + ", item=" + focItem);
			}
			//System.out.println("offset=" + offset + ", scrollHeight=" + availableScrollHeight + ", offset + y + h=" + (offset + y + h) + ", focusedItem=" + focItem);
		}
		if (
			//#if polish.blackberry && !polish.hasTrackballEvents
				(gameAction == Canvas.RIGHT  && keyCode != Canvas.KEY_NUM6) ||
			//#endif
			   (gameAction == Canvas.DOWN   && keyCode != Canvas.KEY_NUM8)) 
		{
			if (focItem != null 
					&& (availableScrollHeight != -1 && offset + y + h > availableScrollHeight) 
			) {
				//System.out.println("offset=" + offset + ", foc.relativeY=" + this.focusedItem.relativeY + ", foc.height=" + this.focusedItem.itemHeight + ", available=" + this.availableHeight);
				// keep the focus do scroll downwards:
				//#debug
				System.out.println("Container(" + this + "): scrolling down: keeping focus, focusedIndex=" + this.focusedIndex + ", y=" + y + ", h=" + h + ", offset=" + offset );
			} else {
				//#ifdef tmp.supportViewType
					if (this.containerView != null) {
						 processed = this.containerView.handleKeyPressed(keyCode, gameAction);
					} else {
				//#endif
						processed = shiftFocus( true, 0 );
				//#ifdef tmp.supportViewType
					}
				//#endif
			}
			//#debug
			System.out.println("Container(" + this + "): forward shift by one item succeded: " + processed + ", focusedIndex=" + this.focusedIndex + ", enableScrolling=" + this.enableScrolling);
			if ((!processed)  
					&& ( 
						(availableScrollHeight != -1 && offset + y + h > availableScrollHeight)
						|| (this.enableScrolling && offset + this.itemHeight > availableScrollHeight)
						)
			) {
				int containerHeight = Math.max( this.contentHeight, this.backgroundHeight );
				int availScrollHeight = getContentScrollHeight();
				int scrollOffset = getScrollYOffset();
				
				// scroll downwards:
				int difference =
				//#if polish.Container.ScrollDelta:defined
					//#=  ${polish.Container.ScrollDelta};
				//#else
					((containerHeight + scrollOffset) - availScrollHeight);
				
					if(difference > (availScrollHeight / 2))
					{
						difference = availScrollHeight / 2;
					}
				//#endif
					
				if(difference == 0)
				{
					return false;
				}
					
				offset = scrollOffset - difference;
				if (offset > 0) {
					offset = 0;
				}
				setScrollYOffset( offset, true );
				processed = true;
				//#debug
				System.out.println("Down/Right: Decreasing (target)YOffset to " + offset);	
			}
		} else if ( 
				//#if polish.blackberry && !polish.hasTrackballEvents
					(gameAction == Canvas.LEFT  && keyCode != Canvas.KEY_NUM4) ||
				//#endif
				    (gameAction == Canvas.UP    && keyCode != Canvas.KEY_NUM2) ) 
		{
			if (focItem != null 
					&& availableScrollHeight != -1 
					&& offset + focItem.relativeY < 0 ) // this.focusedItem.yTopPos < this.yTop ) 
			{
				// keep the focus do scroll upwards:
				//#debug
				System.out.println("Container(" + this + "): scrolling up: keeping focus, relativeScrollOffset=" + offset + ", scrollHeight=" + availableScrollHeight +  ", focusedIndex=" + this.focusedIndex + ", focusedItem.relativeY=" + this.focusedItem.relativeY + ", this.availableHeight=" + this.scrollHeight + ", targetYOffset=" + this.targetYOffset);
			} else {
				//#ifdef tmp.supportViewType
					if (this.containerView != null) {
						 processed = this.containerView.handleKeyPressed(keyCode, gameAction);
					} else {
				//#endif
						processed = shiftFocus( false, 0 );
				//#ifdef tmp.supportViewType
					}
				//#endif
			}
			//#debug
			System.out.println("Container(" + this + "): upward shift by one item succeded: " + processed + ", focusedIndex=" + this.focusedIndex );
			if ((!processed) 
					&& ( (this.enableScrolling && offset < 0)
					   || (availableScrollHeight != -1 &&  focItem != null && offset + focItem.relativeY < 0) )
			) {
				// scroll upwards:
				int difference =
				//#if polish.Container.ScrollDelta:defined
					//#= ${polish.Container.ScrollDelta};
				//#else
					getScreen() != null ? getScreen().contentHeight / 2 :  30;
				//#endif
				offset = getScrollYOffset() + difference;
				if (offset > 0) {
					offset = 0;
				}
				setScrollYOffset(offset, true);
				//#debug
				System.out.println("Up/Left: Increasing (target)YOffset to " + offset);	
				processed = true;
			}
		}
		//#ifdef tmp.supportViewType
			else if (this.containerView != null) 
			{
				processed = this.containerView.handleKeyPressed(keyCode, gameAction);
			}
		//#endif
		return processed;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		//#debug
		System.out.println("handleKeyReleased( " + keyCode + ", " + gameAction + " ) for " + this);
		if (this.itemsList.size() == 0 && this.focusedItem == null) {
			return super.handleKeyReleased(keyCode, gameAction);
		}
		Item item = this.focusedItem;
		if (item != null) {
			int scrollOffset = getScrollYOffset();
			if ( item.handleKeyReleased( keyCode, gameAction ) ) {
				if (item.isShown) { // could be that the item or its screen has been removed in the meantime...
					if (this.enableScrolling) {
						if (getScrollYOffset() == scrollOffset) {
							//#debug
							System.out.println("scrolling focused item that has handled key released, item=" + item + ", item.internalY=" + item.internalY);
							scroll(gameAction, item, false);
						}
					} else  {
						updateInternalPosition(item);
					}
				}
//				2009-06-10:
//				if (this.enableScrolling && item.internalX != NO_POSITION_SET) {
//					scroll(gameAction, item);
//				}
				
//				if (this.enableScrolling) {
//					if (getScrollYOffset() == scrollOffset) {
//						// #debug
//						System.out.println("scrolling focused item that has handled key pressed, item=" + item + ", item.internalY=" + item.internalY);
//						scroll(gameAction, item);
//					}
//				} else  {
//					if (item.itemHeight > getScrollHeight()  &&  item.internalX != NO_POSITION_SET) {
//						// adjust internal settings for root container:
//						this.internalX = item.relativeX + item.contentX + item.internalX;
//						this.internalY = item.relativeY + item.contentY + item.internalY;
//						this.internalWidth = item.internalWidth;
//						this.internalHeight = item.internalHeight;
//						// #debug
//						System.out.println(this + ": Adjusted internal area by internal area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );						
//					} else {
//						this.internalX = item.relativeX;
//						this.internalY = item.relativeY;
//						this.internalWidth = item.itemWidth;
//						this.internalHeight = item.itemHeight;
//						// #debug
//						System.out.println(this + ": Adjusted internal area by full area of " + item + " to x=" + this.internalX + ", y=" + this.internalY + ", w=" + this.internalWidth + ", h=" + this.internalHeight );						
//					}
//				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyReleased consumed by item " + item.getClass().getName() + "/" + item );				
				return true;
			}	
		}
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				 if ( this.containerView.handleKeyReleased(keyCode, gameAction) ) {
					 return true;
				 }
			}
		//#endif
		return super.handleKeyReleased(keyCode, gameAction);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyRepeated(int, int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction) {
		if (this.itemsList.size() == 0 && this.focusedItem == null) {
			return false;
		}
		if (this.focusedItem != null) {
			Item item = this.focusedItem;
			if ( item.handleKeyRepeated( keyCode, gameAction ) ) {
				if (this.enableScrolling && item.internalX != NO_POSITION_SET) {
					scroll(gameAction, item, false);
				}
				//#debug
				System.out.println("Container(" + this + "): handleKeyRepeated consumed by item " + item.getClass().getName() + "/" + item );				
				return true;
			}	
		}
		return handleNavigate(keyCode, gameAction);
		// note: in previous versions a keyRepeat event was just re-asigned to a keyPressed event. However, this resulted
		// in non-logical behavior when an item wants to ignore keyRepeat events and only press "real" keyPressed events.
		// So now events are ignored by containers when they are ignored by their currently focused item...
		//return super.handleKeyRepeated(keyCode, gameAction);
	}

	//#if polish.Container.useTouchFocusHandling
	/**
	 * Focuses the first visible item in the given vertical minimum and maximum offsets.
	 * 
	 * @param container 
	 * 		the container
	 * @param verticalMin 
	 * 		the vertical minimum offset
	 * @param verticalMax 
	 * 		the vertical maximum offset
	 * @return 
	 * 		the newly focused item
	 */
	Item focusVisible(Container container, int verticalMin, int verticalMax) {
		Item[] items = container.getItems();
		Item focusedItem = null;
		for (int index = 0; index < items.length; index++) {
			Item item = items[index];
			
			int itemTop= item.getAbsoluteY();
			int itemBottom = itemTop + item.itemHeight;
			
			int itemAppearanceMode = item.getAppearanceMode(); 
			// if item is interactive ...
			if(itemAppearanceMode == Item.INTERACTIVE || itemAppearanceMode == Item.HYPERLINK || itemAppearanceMode == Item.BUTTON) {
				// ... and is a container and not fully visible ...
				if(item instanceof Container && !isItemVisible(verticalMin, verticalMax, itemTop, itemBottom, true)) {
					// ... but partially visible ...
					if(isItemVisible(verticalMin, verticalMax, itemTop, itemBottom, false)) {
						focusedItem = focusVisible((Container)item, verticalMin, verticalMax);
						
						// if a child item was focused ...
						if(focusedItem != null) {
							focusIndex(index);
							return item;
						}
					}
				} else if(isItemVisible(verticalMin, verticalMax, itemTop, itemBottom, true)) {
					return focusIndex(index);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns true if the given item top and bottom offset is inside the given vertical minimum and maximum offset.
	 * 
	 * @param verticalMin 
	 * 		the vertical minimum offset
	 * @param verticalMax 
	 * 		the vertical maximum offset
	 * @param itemTop 
	 * 		the item top offset
	 * @param itemBottom 
	 * 		the item bottom offset
	 * @param full 
	 * 		true if the item must fit completly into the given vertical offsets otherwise false
	 * @return true 
	 * 		if the item fits into the given vertical offsets otherwise false
	 */
	protected boolean isItemVisible(int verticalMin, int verticalMax, int itemTop, int itemBottom, boolean full) {
		if(full) {
			return itemTop >= verticalMin && itemBottom <= verticalMax;
		} else {
			return !(itemBottom <= verticalMin || itemTop >= verticalMax);
		}
	}
	
	/**
	 * Focuses the child at the given index while preserving the scroll offset.
	 *  
	 * @param index 
	 * 		the index 
	 * @return the focused item
	 */
	Item focusIndex(int index) {
		int scrollOffset = getScrollYOffset();
		setInitialized(false);
		focusChild(index);
		setInitialized(true);
		setScrollYOffset(scrollOffset);
		return getFocusedChild();
	}
	//#endif

	/**
	 * Shifts the focus to the next or the previous item.
	 * 
	 * @param forwardFocus true when the next item should be focused, false when
	 * 		  the previous item should be focused.
	 * @param steps how many steps forward or backward the search for the next focusable item should be started,
	 *        0 for the current item, negative values go backwards.
	 * @return true when the focus could be moved to either the next or the previous item.
	 */
	private boolean shiftFocus(boolean forwardFocus, int steps ) {
		Item[] items = getItems();
		if ( items == null || items.length <= 1) {
			//#debug
			System.out.println("shiftFocus fails: this.items==null or items.length <= 0");
			return false;
		}
		//System.out.println("|");
		Item focItem = this.focusedItem;
		
		//#if polish.Container.useTouchFocusHandling
		if(this.focusedIndex == -1) {
			int verticalMin = getAbsoluteY();
			int verticalMax = verticalMin + getScrollHeight();
			Item newFocusedItem = focusVisible(this, verticalMin, verticalMax);
			if(newFocusedItem != null) {
				return true;
			}
		}
		//#endif
		
		//#if polish.css.colspan
			int i = this.focusedIndex;
			if (steps != 0) {
				//System.out.println("ShiftFocus: steps=" + steps + ", forward=" + forwardFocus);
				int doneSteps = 0;
				steps = Math.abs( steps ) + 1;
				Item item = items[i];
				while( doneSteps <= steps) {
					doneSteps += item.colSpan;
					if (doneSteps >= steps) {
						//System.out.println("bailing out at too many steps: focusedIndex=" + this.focusedIndex + ", startIndex=" + i + ", steps=" + steps + ", doneSteps=" + doneSteps);
						break;
					}
					if (forwardFocus) {
						i++;
						if (i == items.length - 1 ) {
							i = items.length - 2;
							break;
						} else if (i == items.length) {
							i = items.length - 1;
							break;
						}
					} else {
						i--; 
						if (i < 0) {
							i = 1;
							break;
						}
					}
					item = items[i];
					//System.out.println("focusedIndex=" + this.focusedIndex + ", startIndex=" + i + ", steps=" + steps + ", doneSteps=" + doneSteps);
				}
				if (doneSteps >= steps && item.colSpan != 1) {
					if (forwardFocus) {
						i--;
						if (i < 0) {
							i = items.length - 1;
						}
						//System.out.println("forward: Adjusting startIndex to " + i );
					} else {
						i = (i + 1) % items.length;
						//System.out.println("backward: Adjusting startIndex to " + i );
					}
				}
			}
		//#else			
			//# int i = this.focusedIndex + steps;
			if (i > items.length) {
				i = items.length - 2;
			}
			if (i < 0) {
				i = 1;
			}
		//#endif
		Item item = null;
		boolean allowCycle = this.allowCycling;
		if (allowCycle) {
				if (forwardFocus) {
					// when you scroll to the bottom and
					// there is still space, do
					// scroll first before cycling to the
					// first item:
					allowCycle = (getScrollYOffset() + this.itemHeight <= getScrollHeight() + 1);
					//System.out.println("allowCycle-calculation ( forward non-smoothScroll): yOffset=" + this.yOffset + ", itemHeight=" + this.itemHeight + " (together="+ (this.yOffset + this.itemHeight));
				} else {
					// when you scroll to the top and
					// there is still space, do
					// scroll first before cycling to the
					// last item:
					allowCycle = (getScrollYOffset() == 0);
				}						
		}
		//#debug
		System.out.println("shiftFocus of " + this + ": allowCycle(local)=" + allowCycle + ", allowCycle(global)=" + this.allowCycling + ", isFoward=" + forwardFocus + ", enableScrolling=" + this.enableScrolling + ", targetYOffset=" + this.targetYOffset + ", yOffset=" + this.yOffset + ", focusedIndex=" + this.focusedIndex + ", start=" + i );
		while (true) {
			if (forwardFocus) {
				i++;
				if (i >= items.length) {
					if (allowCycle) {
						if (!fireContinueCycle(CycleListener.DIRECTION_BOTTOM_TO_TOP)) {
							return false;
						}
						allowCycle = false;
						i = 0;
						//#debug
						System.out.println("allowCycle: Restarting at the beginning");
					} else {
						break;
					}
				}
			} else {
				i--;
				if (i < 0) {
					if (allowCycle) {
						if (!fireContinueCycle(CycleListener.DIRECTION_TOP_TO_BOTTOM)) {
							return false;
						}
						allowCycle = false;
						i = items.length - 1;
						//#debug
						System.out.println("allowCycle: Restarting at the end");
					} else {
						break;
					}
				}
			}
			item = items[i];
			if (item.appearanceMode != Item.PLAIN) {
				break;
			}
		}
		if (item == null || item.appearanceMode == Item.PLAIN || item == focItem) {
			//#debug
			System.out.println("got original focused item: " + (item == focItem) + ", item==null:" + (item == null) + ", mode==PLAIN:" + (item == null ? false:(item.appearanceMode == PLAIN)) );
			
			return false;
		}
		int direction = Canvas.UP;
		if (forwardFocus) {
			direction = Canvas.DOWN;
		}
		focusChild(i, item, direction, false );
		return true;
	}

	/**
	 * Retrieves the index of the item which is currently focused.
	 * 
	 * @return the index of the focused item, -1 when none is focused.
	 */
	public int getFocusedIndex() {
		return this.focusedIndex;
	}
	
	/**
	 * Retrieves the currently focused item.
	 * 
	 * @return the currently focused item, null when there is no focusable item in this container.
	 */
	public Item getFocusedItem() {
		return this.focusedItem;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		//#if polish.debug.debug
		if (this.parent == null) {
			//#debug
			System.out.println("Container.setStyle without boolean parameter for container " + toString() );
		}
		//#endif
		setStyleWithBackground(style, false);
	}
	
	/**
	 * Sets the style of this container.
	 * 
	 * @param style the style
	 * @param ignoreBackground when true is given, the background and border-settings
	 * 		  will be ignored.
	 */
	public void setStyleWithBackground( Style style, boolean ignoreBackground) {
		super.setStyle(style);
		if (ignoreBackground) {
			this.background = null;
			this.border = null;
			this.marginTop = 0;
			this.marginBottom = 0;
			this.marginLeft = 0;
			this.marginRight = 0;
		}
		this.isIgnoreMargins = ignoreBackground;
		//#if polish.css.focused-style-first
			Style firstFocusStyleObj = (Style) style.getObjectProperty("focused-style-first");
			if (firstFocusStyleObj != null) {
				this.focusedStyleFirst = firstFocusStyleObj;
			}
		//#endif
		//#if polish.css.focused-style-last
			Style lastFocusStyleObj = (Style) style.getObjectProperty("focused-style-last");
			if (lastFocusStyleObj != null) {
				this.focusedStyleLast = lastFocusStyleObj;
			}
		//#endif
		//#if polish.css.focus-all-style
			Style focusAllStyleObj = (Style) style.getObjectProperty("focus-all-style");
			if (focusAllStyleObj != null) {
				this.focusAllStyle = focusAllStyleObj;
			}
		//#endif
			
		//#ifdef polish.css.view-type
//			ContainerView viewType =  (ContainerView) style.getObjectProperty("view-type");
//			if (this instanceof ChoiceGroup) {
//				System.out.println("SET.STYLE / CHOICEGROUP: found view-type (1): " + (viewType != null) + " for " + this);
//			}
			if (this.view != null && this.view instanceof ContainerView) {
				ContainerView viewType = (ContainerView) this.view; // (ContainerView) style.getObjectProperty("view-type");
				this.containerView = viewType;
				this.view = null; // set to null so that this container can control the view completely. This is necessary for scrolling, for example.
				viewType.parentContainer = this;
				viewType.focusFirstElement = this.autoFocusEnabled;
				viewType.allowCycling = this.allowCycling;
				if (this.focusedItem != null) {
					viewType.focusItem(this.focusedIndex, this.focusedItem, 0 );
				}
			} else if (!this.preserveViewType && style.getObjectProperty("view-type") == null && !this.setView) {
				this.containerView = null;
			}
		//#endif
		//#ifdef polish.css.columns
			if (this.containerView == null) {
				Integer columns = style.getIntProperty("columns");
				if (columns != null) {
					if (columns.intValue() > 1) {
						//System.out.println("Container: Using default container view for displaying table");
						this.containerView = new ContainerView();  
						this.containerView.parentContainer = this;
						this.containerView.focusFirstElement = this.autoFocusEnabled;
						this.containerView.allowCycling = this.allowCycling;
					}
				}
			}
		//#endif

		//#if polish.css.scroll-mode
			Integer scrollModeInt = style.getIntProperty("scroll-mode");
			if ( scrollModeInt != null ) {
				this.scrollSmooth = (scrollModeInt.intValue() == SCROLL_SMOOTH);
			}
		//#endif
			
		//#if tmp.checkBouncing
			Boolean allowBounceBool = style.getBooleanProperty("bounce");
			if (allowBounceBool != null) {
				this.allowBouncing = allowBounceBool.booleanValue();
			}
		//#endif
			
		//#if polish.css.expand-items
			synchronized(this.itemsList) {
				Boolean expandItemsBool = style.getBooleanProperty("expand-items");
				if (expandItemsBool != null) {
					this.isExpandItems = expandItemsBool.booleanValue();
				}
			}
		//#endif
			
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.setStyle(style);
			}
		//#endif
		
		//#if polish.css.focus-all
			Boolean focusAllBool = style.getBooleanProperty("focus-all");
			if (focusAllBool != null) {
				this.isFocusAllChildren = focusAllBool.booleanValue();
			}
		//#endif

		//#if polish.css.press-all
			Boolean pressAllBool = style.getBooleanProperty("press-all");
			if (pressAllBool != null) {
				this.isPressAllChildren = pressAllBool.booleanValue();
			}
		//#endif

		//#if polish.css.change-styles
			String changeStyles = style.getProperty("change-styles");
			if (changeStyles != null) {
				int splitPos = changeStyles.indexOf('>');
				if (splitPos != -1) {
					String oldStyle = changeStyles.substring(0, splitPos ).trim();
					String newStyle = changeStyles.substring(splitPos+1).trim();
					try {
						changeChildStyles(oldStyle, newStyle);
					} catch (Exception e) {
						//#debug error
						System.out.println("Unable to apply change-styles \"" + changeStyles + "\"" + e );
					}
				}
			}
		//#endif
		//#ifdef polish.css.show-delay
			Integer showDelayInt = style.getIntProperty("show-delay");
			if (showDelayInt != null) {
				this.showDelay = showDelayInt.intValue();
			}
		//#endif
		//#if polish.css.child-style
			Style childStyleObj = (Style) style.getObjectProperty("child-style");
			if (childStyleObj != null) {
				this.childStyle = childStyleObj;
			}
		//#endif
	}
	
	
	
	//#ifdef tmp.supportViewType
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style, boolean)
		 */
		public void setStyle(Style style, boolean resetStyle)
		{
			super.setStyle(style, resetStyle);
			if (this.containerView != null) {
				this.containerView.setStyle(style, resetStyle);
			}
		}
	//#endif
		
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#resetStyle(boolean)
	 */
	public void resetStyle(boolean recursive) {
		super.resetStyle(recursive);
		if (recursive) {
			Object[] items = this.itemsList.getInternalArray();
			for (int i = 0; i < items.length; i++) {
				Item item = (Item) items[i];
				if (item == null) {
					break;
				}
				item.resetStyle(recursive);
			}
		}
	}

	/**
	 * Changes the style of all children that are currently using the specified oldChildStyle with the given newChildStyle.
	 * 
	 * @param oldChildStyleName the name of the style of child items that should be exchanged
	 * @param newChildStyleName the name of the new style for child items that were using the specified oldChildStyle before
	 * @throws IllegalArgumentException if no corresponding newChildStyle could be found
	 * @see StyleSheet#getStyle(String)
	 */
	public void changeChildStyles( String oldChildStyleName, String newChildStyleName) {
		Style newChildStyle = StyleSheet.getStyle(newChildStyleName);
		if (newChildStyle ==  null) {
			throw new IllegalArgumentException("for " + newChildStyleName );
		}
		Style oldChildStyle = StyleSheet.getStyle(oldChildStyleName);
		changeChildStyles(oldChildStyle, newChildStyle);
	}
	
	/**
	 * Changes the style of all children that are currently using the specified oldChildStyle with the given newChildStyle.
	 * 
	 * @param oldChildStyle the style of child items that should be exchanged
	 * @param newChildStyle the new style for child items that were using the specified oldChildStyle before
	 * @throws IllegalArgumentException if newChildStyle is null
	 */
	public void changeChildStyles( Style oldChildStyle, Style newChildStyle) {
		if (newChildStyle == null) {
			throw new IllegalArgumentException();
		}
		Object[] children = this.itemsList.getInternalArray();
		for (int i = 0; i < children.length; i++)
		{
			Item child = (Item) children[i];
			if (child == null) {
				break;
			}
			if (child.style == oldChildStyle) {
				child.setStyle( newChildStyle );
			}
		}
	}

	/**
	 * Parses the given URL and includes the index of the item, when there is an "%INDEX%" within the given url.
	 * @param url the resource URL which might include the substring "%INDEX%"
	 * @param item the item to which the URL belongs to. The item must be 
	 * 		  included in this container.
	 * @return the URL in which the %INDEX% is substituted by the index of the
	 * 		   item in this container. The url "icon%INDEX%.png" is resolved
	 * 		   to "icon1.png" when the item is the second item in this container.
	 * @throws NullPointerException when the given url or item is null
	 */
	public String parseIndexUrl(String url, Item item) {
		int pos = url.indexOf("%INDEX%");
		if (pos != -1) {
			int index = this.itemsList.indexOf( item );
			//TODO rob check if valid, when url ends with %INDEX%
			url = url.substring(0, pos) + index + url.substring( pos + 7 );
		}
		return url;
	}
	/**
	 * Retrieves the position of the specified item.
	 * 
	 * @param item the item
	 * @return the position of the item, or -1 when it is not defined
	 */
	public int getPosition( Item item ) {
		return this.itemsList.indexOf( item );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style focusStyle, int direction ) {
		//#debug
		System.out.println("focusing container " + this + " from " + (this.style != null ? this.style.name : "<no style>") + " to " + getFocusedStyle().name);
		if (this.isFocused) {
			return this.style;
		}
		this.plainStyle = null;
		if ( this.itemsList.size() == 0) {
			return super.focus(focusStyle, direction );
		} else {
			focusStyle = getFocusedStyle();
			//#if polish.css.focus-all
				if (this.isFocusAllChildren) {
					Object[] myItems = this.itemsList.getInternalArray();
					for (int i = 0; i < myItems.length; i++)
					{
						Item item = (Item) myItems[i];
						if (item == null) {
							break;
						}
						Style itemFocusedStyle = item.getFocusedStyle();
						if (itemFocusedStyle != focusStyle && itemFocusedStyle != StyleSheet.focusedStyle) {
							if (!item.isFocused) {
								if (item.style != null) {
									item.setAttribute(KEY_ORIGINAL_STYLE, item.style);
								}
								item.focus(itemFocusedStyle, direction);
							}
						}
					}
				}
			//#endif
			//#if polish.css.focus-all-style
				if (this.focusAllStyle != null) {
					Object[] myItems = this.itemsList.getInternalArray();
					for (int i = 0; i < myItems.length; i++)
					{
						Item item = (Item) myItems[i];
						if (item == null) {
							break;
						}
						if (item.style != null) {
							item.setAttribute(KEY_ORIGINAL_STYLE, item.style);
						}
						item.setStyle(this.focusAllStyle);
					}
				}
			//#endif

			Style result = this.style;
			if ((focusStyle != null && focusStyle != StyleSheet.focusedStyle && (this.parent == null || (this.parent.getFocusedStyle() != focusStyle)))  
				//#if polish.css.include-label
				|| (this.includeLabel && focusStyle != null) 
				//#endif
			) {
				result = super.focus( focusStyle, direction );
				this.plainStyle = result;
			}
			
			if (!this.isStyleInitialised && result != null) {
				//#debug
				System.out.println("setting original style for container " + this + " with style " + result.name);
				setStyle( result );
			}

			
			//#if tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.focus(focusStyle, direction);
					//this.isInitialised = false; not required
				}
			//#endif
			this.isFocused = true;
			int newFocusIndex = this.focusedIndex;
			
			//#if tmp.supportViewType
				if ( this.containerView == null || this.containerView.allowsAutoTraversal ) {
			//#endif
					Item[] myItems = getItems();
					if (this.autoFocusEnabled &&  this.autoFocusIndex < myItems.length && (myItems[this.autoFocusIndex].appearanceMode != PLAIN)) {
						//#debug
						System.out.println("focus(Style, direction): autofocusing " + this + ", focusedIndex=" + this.focusedIndex + ", autofocus=" + this.autoFocusIndex);
						newFocusIndex = this.autoFocusIndex;
						this.autoFocusEnabled = false;
					} else {
						// focus the first interactive item...
						if (direction == Canvas.UP || direction == Canvas.LEFT ) {
							//System.out.println("Container: direction UP with " + myItems.length + " items");
							for (int i = myItems.length; --i >= 0; ) {
								Item item = myItems[i];
								if (item.appearanceMode != PLAIN) {
									newFocusIndex = i;
									break;
								}
							}
						} else {
							//System.out.println("Container: direction DOWN");
							for (int i = 0; i < myItems.length; i++) {
								Item item = myItems[i];
								if (item.appearanceMode != PLAIN) {
									newFocusIndex = i;
									break;
								}
							}
						}
					}
				this.focusedIndex = newFocusIndex;
				if (newFocusIndex == -1) {
					//System.out.println("DID NOT FIND SUITEABLE ITEM - current style=" + this.style.name);
					// this container has only non-focusable items!
					if (this.plainStyle != null) {
						// this will result in plainStyle being returned in the super.focus() call:
						this.style = this.plainStyle;
					}
					
					return super.focus( focusStyle, direction );
				}
			//#if tmp.supportViewType
				} else if (this.focusedIndex == -1) {
					Object[] myItems = this.itemsList.getInternalArray();
					//System.out.println("Container: direction DOWN through view type " + this.view);
					for (int i = 0; i < myItems.length; i++) {
						Item item = (Item) myItems[i];
						if (item == null) {
							break;
						}
						if (item.appearanceMode != PLAIN) {
							newFocusIndex = i;
							break;
						}
					}
					this.focusedIndex = newFocusIndex;
					if (newFocusIndex == -1) {
						//System.out.println("DID NOT FIND SUITEABLE ITEM (2)");
						// this container has only non-focusable items!
						if (this.plainStyle != null) {
							this.style = this.plainStyle;
						}
						return super.focus( focusStyle, direction );
					}
				}
			//#endif
			Item item = get( this.focusedIndex );
//			Style previousStyle = item.style;
//			if (previousStyle == null) {
//				previousStyle = StyleSheet.defaultStyle;
//			}
			this.showCommandsHasBeenCalled = false;
			//#if polish.css.focus-all
				if (item.isFocused) {
					Style orStyle = (Style) item.getAttribute(KEY_ORIGINAL_STYLE);
					if (orStyle != null) {
						//#debug
						System.out.println("re-setting to plain style " + orStyle.name + " for item " + item);
						item.style = orStyle;
					}
				}
			//#endif
				
			focusChild( this.focusedIndex, item, direction, true );
			
			// item command handling is now done within showCommands and handleCommand
			if (!this.showCommandsHasBeenCalled && this.commands != null) {
				showCommands();
			}
//			if (item.commands == null && this.commands != null) {
//				Screen scr = getScreen();
//				if (scr != null) {
//					scr.setItemCommands(this);
//				}
//			}
			// change the label-style of this container:
			//#ifdef polish.css.label-style
				if (this.label != null && focusStyle != null) {
					Style labStyle = (Style) focusStyle.getObjectProperty("label-style");
					if (labStyle != null) {
						this.labelStyle = this.label.style;
						this.label.setStyle( labStyle );
					}
				}
			//#endif
			return result;
		}
		
		
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	public void defocus(Style originalStyle) {
		//#debug
		System.out.println("defocus container " + this + " with style " + (originalStyle != null ? originalStyle.name : "<no style>"));
		//#if polish.css.focus-all
			if (this.isFocusAllChildren) { 
				Object[] myItems = this.itemsList.getInternalArray();
				for (int i = 0; i < myItems.length; i++)
				{
					Item item = (Item) myItems[i];
					if (item == null) {
						break;
					}
					Style itemPlainStyle = (Style) item.removeAttribute( KEY_ORIGINAL_STYLE );
					if (itemPlainStyle != null) {
						item.defocus(itemPlainStyle);
					}
				}
			}
		//#endif
		Style originalItemStyle = this.itemStyle;
		//#if polish.css.focus-all-style
			if (this.focusAllStyle != null) {
				Object[] myItems = this.itemsList.getInternalArray();
				for (int i = 0; i < myItems.length; i++)
				{
					Item item = (Item) myItems[i];
					if (item == null) {
						break;
					}
					Style itemPlainStyle = (Style) item.removeAttribute( KEY_ORIGINAL_STYLE );
					if (itemPlainStyle != null) {
						if (item == this.focusedItem) {
							originalItemStyle = itemPlainStyle;
						} else {
							
							item.setStyle(itemPlainStyle);
						}
					}
				}
			}
		//#endif
		if ( this.itemsList.size() == 0 || this.focusedIndex == -1 ) {
			super.defocus( originalStyle );
		} else {
			if (this.plainStyle != null) {
				super.defocus( this.plainStyle );
				if (originalStyle == null) {
					originalStyle = this.plainStyle;
				}
				this.plainStyle = null;
			} else if (this.isPressed) {
				notifyItemPressedEnd();
			}
			this.isFocused = false;
			//#ifdef tmp.supportViewType
				if (this.containerView != null) {
					this.containerView.defocus( originalStyle );
					setInitialized(false);
				}
			//#endif
			Item item = this.focusedItem;
			if (item != null) {
					//#if polish.css.focus-all
						if (item.isFocused) {
					//#endif
							item.defocus( originalItemStyle );
					//#if polish.css.focus-all
						}
					//#endif
					this.isFocused = false;
					// now remove any commands which are associated with this item:
					if (item.commands == null && this.commands != null) {
						Screen scr = getScreen();
						if (scr != null) {
							scr.removeItemCommands(this);
						}
					}
			}
			// change the label-style of this container:
			//#ifdef polish.css.label-style
				Style tmpLabelStyle = null;
				if ( originalStyle != null) {
					tmpLabelStyle = (Style) originalStyle.getObjectProperty("label-style");
				}
				if (tmpLabelStyle == null) {
					tmpLabelStyle = StyleSheet.labelStyle;
				}
				if (this.label != null && tmpLabelStyle != null && this.label.style != tmpLabelStyle) {
					this.label.setStyle( tmpLabelStyle );
				}
			//#endif
		}
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#showCommands()
	 */
	public void showCommands() {
		this.showCommandsHasBeenCalled = true;
		super.showCommands();
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleCommand(javax.microedition.lcdui.Command)
	 */
	protected boolean handleCommand(Command cmd) {
		boolean handled = super.handleCommand(cmd);
		if (!handled && this.focusedItem != null) {
			return this.focusedItem.handleCommand(cmd);
		}
		return handled;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		boolean addFullRepaintRegion = false;
		// scroll the container:
		int target = this.targetYOffset;
		int current = this.yOffset;
		int diff = 0;
		if (target != current) {
			if (this.scrollHeight != -1 && Math.abs(target - current) > this.scrollHeight) {
				// maximally scroll one page:
				if (current < target) {
					current = target - this.scrollHeight;
				} else {
					current = target + this.scrollHeight;
				}
			}
			int speed = (target - current) / 3;
			
			speed += target > current ? 1 : -1;
			current += speed;
			if ( ( speed > 0 && current > target) || (speed < 0 && current < target ) ) {
				current = target;
			}
			diff = Math.abs( current - this.yOffset);
			this.yOffset = current;
//			if (this.focusedItem != null && this.focusedItem.backgroundYOffset != 0) {
//				this.focusedItem.backgroundYOffset = (this.targetYOffset - this.yOffset);
//			}
			// # debug
			//System.out.println("animate(): adjusting yOffset to " + this.yOffset );
			addFullRepaintRegion = true;
		}
		int speed = this.scrollSpeed;
		if (speed != 0) {
			speed = (speed * (100 - this.scrollDamping)) / 100;
			if (speed <= 0) {
				speed = 0;
			}
			this.scrollSpeed = speed;
			long timeDelta = currentTime - this.lastAnimationTime;
			if (timeDelta > 1000) {
				timeDelta = AnimationThread.ANIMATION_INTERVAL;
			}
			speed = (int) ((speed * timeDelta) / 1000);
			if (speed == 0) {
				this.scrollSpeed = 0;
			}
			int offset = this.yOffset;
			if (this.scrollDirection == Canvas.UP) {
				offset += speed;
				target = offset;
				if (offset > 0) {
					this.scrollSpeed = 0;
					target = 0;
					//#if tmp.checkBouncing
						if (!this.allowBouncing) {
							offset = 0;
						}
					//#elif polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false
						offset = 0;
					//#endif
				}
			} else {
				offset -= speed;
				target = offset;
				int maxItemHeight = getItemAreaHeight();
				Screen scr = this.screen;
//				Style myStyle = this.style;
//				if (myStyle != null) {
//					maxItemHeight -= myStyle.getPaddingTop(this.availableHeight) + myStyle.getPaddingBottom(this.availableHeight) + myStyle.getMarginTop(this.availableHeight) + myStyle.getMarginBottom(this.availableHeight);
//				}
				if (scr != null 
						&& this == scr.container 
						&& this.relativeY > scr.contentY 
				) {
					// this is an adjustment for calculating the correct scroll offset for containers with a vertical-center or bottom layout:
					maxItemHeight += this.relativeY - scr.contentY;
				}
				if (offset + maxItemHeight < this.scrollHeight) { 
					this.scrollSpeed = 0;
					target = this.scrollHeight - maxItemHeight;
					//#if tmp.checkBouncing
						if (!this.allowBouncing) {
							offset = target;
						}
					//#elif polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false
						offset = target;
					//#endif
				}
			}
			this.yOffset = offset;
			this.targetYOffset = target;
			addFullRepaintRegion = true;
		}
		
		// add repaint region:
		if (addFullRepaintRegion) {
			int x, y, width, height;
			Screen scr = getScreen();
			height = getItemAreaHeight();
			if (this.parent == null && (this.scrollHeight > height || this.enableScrolling)) { // parent==null is required for example when a commands container is scrolled.
				x = scr.contentX;
				y = scr.contentY;
				height = scr.contentHeight;
				width = scr.contentWidth + scr.getScrollBarWidth();
			} else {
				x = getAbsoluteX();
				y = getAbsoluteY();
				width = this.itemWidth;
				//#if polish.useScrollBar || polish.classes.ScrollBar:defined
					width += scr.getScrollBarWidth();
				//#endif
			}
			repaintRegion.addRegion( x, y, width, height + diff + 1 );
		}
		
		
		this.lastAnimationTime = currentTime;
		
		Item focItem = this.focusedItem;
		if (focItem != null) {
			focItem.animate(currentTime, repaintRegion);
		}
		
		//#ifdef tmp.supportViewType
			ContainerView contView = this.containerView;
			if ( contView != null ) {
				contView.animate(currentTime, repaintRegion);
			}
		//#endif
		//#if polish.css.show-delay
			if (this.showDelay != 0 && this.showDelayIndex != 0) {
				int index = Math.min( (int)((currentTime - this.showNotifyTime) / this.showDelay), this.itemsList.size());
				if (index > this.showDelayIndex) {
					for (int i=this.showDelayIndex; i<index; i++) {
						try {
							//System.out.println("calling show notify on item " + i + " at " + (currentTime - this.showNotifyTime) + ", show-delay=" + this.showDelay);
							Item item = get(i);
							item.showNotify();
						} catch (Exception e) {
							//#debug error
							System.out.println("Unable to notify");
						}
					}
					if (index == this.itemsList.size()) {
						this.showDelayIndex = 0;
					} else {
						this.showDelayIndex = index;
					}
				}
			}
		//#endif
		//#if polish.css.focus-all
			if (this.isFocusAllChildren && this.isFocused) {
				Item[] items = this.getItems();
				for (int i = 0; i < items.length; i++) {
					Item item = items[i];
					item.animate(currentTime, repaintRegion);
				}
			}
		//#endif
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#addRepaintArea(de.enough.polish.ui.ClippingRegion)
	 */
	public void addRepaintArea(ClippingRegion repaintRegion) {
		if (this.enableScrolling) {
			Screen scr = getScreen();
			int x = scr.contentX;
			int y = scr.contentY;
			int height = scr.contentHeight;
			int width = scr.contentWidth + scr.getScrollBarWidth();
			repaintRegion.addRegion(x, y, width, height);
		} else {
			super.addRepaintArea(repaintRegion);
		}
	}

	/**
	 * Called by the system to notify the item that it is now at least
	 * partially visible, when it previously had been completely invisible.
	 * The item may receive <code>paint()</code> calls after
	 * <code>showNotify()</code> has been called.
	 * 
	 * <p>The container implementation calls showNotify() on the embedded items.</p>
	 */
	protected void showNotify()
	{
		super.showNotify();
		if (this.style != null && !this.isStyleInitialised) {
			setStyle( this.style );
		}
		//#ifdef polish.useDynamicStyles
			else if (this.style == null) {
				initStyle();
			}
		//#else
			else if (this.style == null && !this.isStyleInitialised) {
				//#debug
				System.out.println("Setting default style for container " + this  );
				setStyle( StyleSheet.defaultStyle );
			}
		//#endif
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.showNotify();
			}
		//#endif
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			if (item.style != null && !item.isStyleInitialised) {
				item.setStyle( item.style );
			}
			//#ifdef polish.useDynamicStyles
				else if (item.style == null) {
					initStyle();
				}
			//#else
				else if (item.style == null && !item.isStyleInitialised) {
					//#debug
					System.out.println("Setting default style for item " + item );
					item.setStyle( StyleSheet.defaultStyle );
				}
			//#endif
			//#if polish.css.show-delay
				if (this.showDelay == 0 || i == 0) { 
			//#endif
					item.showNotify();
			//#if polish.css.show-delay
				}
			//#endif
		}
		//#if polish.css.show-delay
			this.showDelayIndex = (myItems.length > 1 ? 1 : 0);
			this.showNotifyTime = System.currentTimeMillis();
		//#endif
	}

	/**
	 * Called by the system to notify the item that it is now completely
	 * invisible, when it previously had been at least partially visible.  No
	 * further <code>paint()</code> calls will be made on this item
	 * until after a <code>showNotify()</code> has been called again.
	 * 
	 * <p>The container implementation calls hideNotify() on the embedded items.</p>
	 */
	protected void hideNotify()
	{
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.hideNotify();
			}
		//#endif
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			item.hideNotify();
		}
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int relX, int relY) {
		//#debug
		System.out.println("Container.handlePointerPressed(" + relX + ", " + relY + ") for " + this );
		//System.out.println("Container.handlePointerPressed( x=" + x + ", y=" + y + "): adjustedY=" + (y - (this.yOffset  + this.marginTop + this.paddingTop )) );
		// an item within this container was selected:
		this.lastPointerPressY = relY;
		this.lastPointerPressYOffset = getScrollYOffset();
		this.lastPointerPressTime = System.currentTimeMillis();
		int origRelX = relX;
		int origRelY = relY;
		relY -= this.yOffset;
		relY -= this.contentY;
		//#ifdef polish.css.before
			relX -= getBeforeWidthWithPadding();
		//#endif
		relX -= this.contentX;
		//#ifdef tmp.supportViewType
			int viewXOffset = 0;
			ContainerView contView = this.containerView;
			if (contView != null) {
				viewXOffset = contView.getScrollXOffset(); 
				relX -= viewXOffset;
			}
		//#endif
		//System.out.println("Container.handlePointerPressed: adjusted to (" + relX + ", " + relY + ") for " + this );
		boolean eventHandled = false;
		Item item = this.focusedItem;
		if (item != null) {
			// the focused item can extend the parent container, e.g. subcommands, 
			// so give it a change to process the event itself:
			int itemLayout = item.layout;
			boolean processed = item.handlePointerPressed(relX - item.relativeX, relY - item.relativeY );
			if (processed) {
				//#debug
				System.out.println("pointerPressed at " + relX + "," + relY + " consumed by focusedItem " + item);
				// layout could have been changed:
				if (item.layout != itemLayout && isInitialized()) {
					if (item.availableWidth != 0) {
						item.init( item.availableWidth, item.availableWidth, item.availableHeight );
					} else {
						item.init( this.contentWidth, this.contentWidth, this.contentHeight );
					}
					if (item.isLayoutLeft()) {
						item.relativeX = 0;
					} else if (item.isLayoutCenter()) {
						item.relativeX = (this.contentWidth - item.itemWidth)/2;
					} else {
						item.relativeX = this.contentWidth - item.itemWidth;
					}
				}
				notifyItemPressedStart();
				return true;
			} else if (item.isPressed) {
				eventHandled = notifyItemPressedStart();
			}
		}
		//#ifdef tmp.supportViewType
			if (contView != null) {
				relX += viewXOffset;
				if ( contView.handlePointerPressed(relX + this.contentX, relY + this.contentY) ) {
					//System.out.println("ContainerView " + contView + " consumed pointer press event");
					notifyItemPressedStart();
					return true;
				}
				relX -= viewXOffset;
			}
			if (!isInItemArea(origRelX, origRelY - this.yOffset) || (item != null && item.isInItemArea(relX - item.relativeX, relY - item.relativeY )) ) {
				//System.out.println("Container.handlePointerPressed(): out of range, relativeX=" + this.relativeX + ", relativeY="  + this.relativeY + ", contentHeight=" + this.contentHeight );
				return ((this.defaultCommand != null) && super.handlePointerPressed(origRelX, origRelY)) || eventHandled;
			}
		//#else
			if (!isInItemArea(origRelX, origRelY) || (item != null && item.isInItemArea(relX - item.relativeX, relY - item.relativeY )) ) {
				//System.out.println("Container.handlePointerPressed(): out of range, relativeX=" + this.relativeX + ", relativeY="  + this.relativeY + ", contentHeight=" + this.contentHeight );
				return super.handlePointerPressed(origRelX, origRelY) || eventHandled;
			}
		//#endif
		Screen scr = this.screen;
		if ( ((origRelY < 0) && (scr == null || origRelY + this.relativeY - scr.contentY < 0)) 
				|| (this.enableScrolling && origRelY > this.scrollHeight) 
		){
			return ((this.defaultCommand != null) && super.handlePointerPressed(origRelX, origRelY)) || eventHandled;
		}
		Item nextItem = getChildAt( origRelX, origRelY );
		if (nextItem != null && nextItem != item) {
			int index = this.itemsList.indexOf(nextItem);
			//#debug
			System.out.println("Container.handlePointerPressed(" + relX + "," + relY + "): found item " + index + "=" + item + " at relative " + relX + "," + relY + ", itemHeight=" + item.itemHeight);
			// only focus the item when it has not been focused already:
			int offset = getScrollYOffset();
			focusChild(index, nextItem, 0, true);
			setScrollYOffset( offset, false ); // don't move the UI while handling the press event:
			// let the item also handle the pointer-pressing event:
			nextItem.handlePointerPressed( relX - nextItem.relativeX , relY - nextItem.relativeY );
			if (!this.isFocused) {
				this.autoFocusEnabled = true;
				this.autoFocusIndex = index;
			}
			notifyItemPressedStart();
			return true;			

		}
//		Item[] myItems = getItems();
//		int itemRelX, itemRelY;
//		for (int i = 0; i < myItems.length; i++) {
//			item = myItems[i];
//			itemRelX = relX - item.relativeX;
//			itemRelY = relY - item.relativeY;
//			//System.out.println( item + ".relativeX=" + item.relativeX + ", .relativeY=" + item.relativeY + ", pointer event relatively at " + itemRelX + ", " + itemRelY);
//			if ( i == this.focusedIndex || (item.appearanceMode == Item.PLAIN) || !item.isInItemArea(itemRelX, itemRelY)) {
//				// this item is not in the range or not suitable:
//				continue;
//			}
//			// the pressed item has been found:
//			//#debug
//			System.out.println("Container.handlePointerPressed(" + relX + "," + relY + "): found item " + i + "=" + item + " at relative " + itemRelX + "," + itemRelY + ", itemHeight=" + item.itemHeight);
//			// only focus the item when it has not been focused already:
//			int offset = getScrollYOffset();
//			focusChild(i, item, 0, true);
//			setScrollYOffset( offset, false ); // don't move the UI while handling the press event:
//			// let the item also handle the pointer-pressing event:
//			item.handlePointerPressed( itemRelX , itemRelY );
//			if (!this.isFocused) {
//				this.autoFocusEnabled = true;
//				this.autoFocusIndex = i;
//			}
//			return true;			
//		}
		return ((this.defaultCommand != null) && super.handlePointerPressed(origRelX, origRelY)) || eventHandled;
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/**
	 * Allows subclasses to check if a pointer release event is used for scrolling the container.
	 * This method can only be called when polish.hasPointerEvents is true.
	 * 
	 * @param relX the x position of the pointer pressing relative to this item's left position
	 * @param relY the y position of the pointer pressing relative to this item's top position
	 */
	protected boolean handlePointerScrollReleased(int relX, int relY) {
		if (Display.getInstance().hasPointerMotionEvents()) {
			return false;
		}
		int yDiff = relY - this.lastPointerPressY;
		int bottomY = Math.max( this.itemHeight, this.internalY + this.internalHeight );
		if (this.focusedItem != null && this.focusedItem.relativeY + this.focusedItem.backgroundHeight > bottomY) {
			bottomY = this.focusedItem.relativeY + this.focusedItem.backgroundHeight;
		}
		if ( this.enableScrolling 
				&& (this.itemHeight > this.scrollHeight || this.yOffset != 0)
				&& ((yDiff < -5 && this.yOffset + bottomY > this.scrollHeight) // scrolling downwards
					|| (yDiff > 5 && this.yOffset != 0) ) // scrolling upwards
			) 
		{
			int offset = this.yOffset + yDiff;
			if (offset > 0) {
				offset = 0;
			}
			//System.out.println("adjusting scrolloffset to " + offset);
			setScrollYOffset(offset, true);
			return true;
		}
		return false;
	}
	//#endif
	
	//#if polish.hasPointerEvents
	/**
	 * Handles the behavior of the virtual keyboard when the item is focused.
	 * By defaukt, the virtual keyboard is hidden. Components which need to have the virtual keyboard
	 * shown when they are focused can override this method.
	 */
	public void handleOnFocusSoftKeyboardDisplayBehavior() {
		Item focItem = this.focusedItem;
		if (focItem != null) {
			focItem.handleOnFocusSoftKeyboardDisplayBehavior();
		} else {
			super.handleOnFocusSoftKeyboardDisplayBehavior();	
		}
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int relX, int relY) {
		//#debug
		System.out.println("Container.handlePointerReleased(" + relX + ", " + relY + ") for " + this  );
		
		// handle keyboard behaviour
		if(this.isJustFocused) {
			this.isJustFocused = false;
			handleOnFocusSoftKeyboardDisplayBehavior();
		}
		
		//#ifdef tmp.supportFocusItemsInVisibleContentArea
			//#if polish.hasPointerEvents		
				this.needsCheckItemInVisibleContent=true;
			//#endif
		//#endif
		//#ifdef polish.css.before
			relX -= getBeforeWidthWithPadding();
		//#endif
		
		Item item = this.focusedItem;
		if (this.enableScrolling) {
			this.isScrolling = false;
			int scrollDiff = Math.abs(getScrollYOffset() - this.lastPointerPressYOffset);
			if ( scrollDiff > Display.getScreenHeight()/10  ||  handlePointerScrollReleased(relX, relY) ) {
				// we have scrolling in the meantime
				boolean processed = false;
				if (item != null && item.isPressed) {
					processed = item.handlePointerReleased(relX - item.relativeX, relY - item.relativeY );
					setInitialized(false);
				}
				if (!processed) {
					while (item instanceof Container) {
						if (item.isPressed) {
							item.notifyItemPressedEnd();
						}
						item = ((Container)item).focusedItem;
					}
					// we have scrolling in the meantime
					if (item != null && item.isPressed) {
						item.notifyItemPressedEnd();
						setInitialized(false);
					}
				}
				// check if we should continue the scrolling:
				long dragTime = System.currentTimeMillis() - this.lastPointerPressTime;
				if (dragTime < 1000 && dragTime > 1) {
					int direction = Canvas.DOWN;
					if (this.yOffset > this.lastPointerPressYOffset) {
						direction = Canvas.UP;
					}
					startScroll( direction,  (int) ((scrollDiff * 1000 ) / dragTime), 20 );
				} else if (this.yOffset > 0) {
					setScrollYOffset(0, true);
				} else if (this.yOffset + this.contentHeight < this.availContentHeight) {
					int maxItemHeight = getItemAreaHeight();
					Screen scr = this.screen;
					if (scr != null 
							&& this == scr.container 
							&& this.relativeY > scr.contentY 
					) {
						// this is an adjustment for calculating the correct scroll offset for containers with a vertical-center or bottom layout:
						maxItemHeight += this.relativeY - scr.contentY;
					}
					if (this.yOffset + maxItemHeight < this.scrollHeight) { 
						int target = this.scrollHeight - maxItemHeight;
						setScrollYOffset( target, true );
					}

				}
				if (this.isPressed) {
					notifyItemPressedEnd();
				}
				return true;
			}
		}
		// foward event to currently focused item:
		int origRelX = relX
		//#ifdef polish.css.before
			+ getBeforeWidthWithPadding()
		 //#endif
		 ;
		int origRelY = relY;
		relY -= this.yOffset;
		relY -= this.contentY;
		relX -= this.contentX;
		//#ifdef tmp.supportViewType
			int viewXOffset = 0;
			ContainerView contView = this.containerView;
			if (contView != null) {
				if (contView.handlePointerReleased(relX + this.contentX, relY + this.contentY)) {
					//System.out.println("ContainerView consumed pointer release event " + contView);
					if (this.isPressed) {
						notifyItemPressedEnd();
					}
					return true;
				}
				viewXOffset = contView.getScrollXOffset(); 
				relX -= viewXOffset;
			}
		//#endif
		//System.out.println("Container.handlePointerReleased: adjusted to (" + relX + ", " + relY + ") for " + this );
		if (item != null) {
			// the focused item can extend the parent container, e.g. subcommands, 
			// so give it a change to process the event itself:
			int itemLayout = item.layout;
			boolean processed = item.handlePointerReleased(relX - item.relativeX, relY - item.relativeY );
			if (processed) {
				//#debug
				System.out.println("pointerReleased at " + relX + "," + relY + " consumed by focusedItem " + item);
				if (this.isPressed) {
					notifyItemPressedEnd();
				}
				// layout could have been changed:
				if (item.layout != itemLayout && isInitialized()) {
					if (item.availableWidth != 0) {
						item.init( item.availableWidth, item.availableWidth, item.availableHeight );
					} else {
						item.init( this.contentWidth, this.contentWidth, this.contentHeight );
					}
					if (item.isLayoutLeft()) {
						item.relativeX = 0;
					} else if (item.isLayoutCenter()) {
						item.relativeX = (this.contentWidth - item.itemWidth)/2;
					} else {
						item.relativeX = this.contentWidth - item.itemWidth;
					}
				}
				if (this.isPressed) {
					notifyItemPressedEnd();
				}
				return true;
			} else if ( item.isInItemArea(relX - item.relativeX, relY - item.relativeY )) {
				//#debug
				System.out.println("pointerReleased not handled by focused item but within that item's area. Item=" + item + ", container=" + this);
				return (this.defaultCommand != null) && super.handlePointerReleased(origRelX, origRelY);
			}
		}
		if (!isInItemArea(origRelX, origRelY)) {
			return (this.defaultCommand != null) && super.handlePointerReleased(origRelX, origRelY);
		}
		Item nextItem = getChildAt(origRelX, origRelY);
		if (nextItem != null && nextItem != item) {
			item = nextItem;
			int itemRelX = relX - item.relativeX;
			int itemRelY = relY - item.relativeY;
			item.handlePointerReleased( itemRelX , itemRelY );
			if (this.isPressed) {
				notifyItemPressedEnd();
			}
			return true;			
		}
//		Item[] myItems = getItems();
//		int itemRelX, itemRelY;
//		for (int i = 0; i < myItems.length; i++) {
//			item = myItems[i];
//			itemRelX = relX - item.relativeX;
//			itemRelY = relY - item.relativeY;
//			//System.out.println( item + ".relativeX=" + item.relativeX + ", .relativeY=" + item.relativeY + ", pointer event relatively at " + itemRelX + ", " + itemRelY);
//			if ( i == this.focusedIndex || (item.appearanceMode == Item.PLAIN) || !item.isInItemArea(itemRelX, itemRelY)) {
//				// this item is not in the range or not suitable:
//				continue;
//			}
//			// the pressed item has been found:
//			//#debug
//			System.out.println("Container.handlePointerReleased(" + relX + "," + relY + "): found item " + i + "=" + item + " at relative " + itemRelX + "," + itemRelY + ", itemHeight=" + item.itemHeight);
//			// only focus the item when it has not been focused already:
//			//focus(i, item, 0);
//			// let the item also handle the pointer-pressing event:
//			item.handlePointerReleased( itemRelX , itemRelY );
//			return true;			
//		}
		return (this.defaultCommand != null) && super.handlePointerReleased(origRelX, origRelY);
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerDragged(int, int)
	 */
	protected boolean handlePointerDragged(int relX, int relY) {
		return false;
	}
	//#endif

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerDragged(int, int)
	 */
	protected boolean handlePointerDragged(int relX, int relY, ClippingRegion repaintRegion) {
		//#debug
		System.out.println("handlePointerDraggged " + relX + ", " + relY + " for " + this + ", enableScrolling=" + this.enableScrolling + ", focusedItem=" + this.focusedItem);
		//#ifdef polish.css.before
			relX -= getBeforeWidthWithPadding();
		//#endif
		
		Item item = this.focusedItem;
		if (item != null && item.handlePointerDragged( relX - this.contentX - item.relativeX, relY - this.yOffset - this.contentY - item.relativeY, repaintRegion)) {
			return true;
		}
		
		//#if polish.Container.useTouchFocusHandling
		if(item != null) {
	   		 focusChild(-1);
	   		 //#if polish.blackberry
	   		 //# ((BaseScreen)(Object)Display.getInstance()).notifyFocusSet(null);
	   		 //#endif
	   		 UiAccess.init(item, item.getAvailableWidth(), item.getAvailableWidth(), item.getAvailableHeight());
   	  	}
		//#endif
		
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				if ( this.containerView.handlePointerDragged(relX,relY, repaintRegion) ) {
					return true;
				}
			}
		//#endif
		if (this.enableScrolling ) {
			int maxItemHeight = getItemAreaHeight();
			Screen scr = this.screen;
			if (scr != null 
					&& this == scr.container 
					&& this.relativeY > scr.contentY 
			) {
				// this is an adjustment for calculating the correct scroll offset for containers with a vertical-center or bottom layout:
				maxItemHeight += this.relativeY - scr.contentY;
			}
			if (maxItemHeight > this.scrollHeight || this.yOffset != 0) {
				int lastOffset = getScrollYOffset();
				int nextOffset = this.lastPointerPressYOffset + (relY - this.lastPointerPressY);
				//#if tmp.checkBouncing
					if (!this.allowBouncing) {
				//#endif
					//#if tmp.checkBouncing || (polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false)
						if (nextOffset > 0) {
							nextOffset = 0;
						} else {
							if (nextOffset + maxItemHeight < this.scrollHeight) { 
								nextOffset = this.scrollHeight - maxItemHeight;
							}
						}
					//#endif
				//#if tmp.checkBouncing
					} else {
				//#endif
					//#if tmp.checkBouncing || !(polish.Container.ScrollBounce:defined && polish.Container.ScrollBounce == false)
						if (nextOffset > this.scrollHeight/3) {
							nextOffset = this.scrollHeight/3;
						} else {
							maxItemHeight += this.scrollHeight/3;
							if (nextOffset + maxItemHeight < this.scrollHeight) { 
								nextOffset = this.scrollHeight - maxItemHeight;
							}
						}
					//#endif
				//#if tmp.checkBouncing
					}
				//#endif
				this.isScrolling = (nextOffset != lastOffset);
				if (this.isScrolling) {
					setScrollYOffset( nextOffset, false );
					addRepaintArea(repaintRegion);
					return true;
				}
			}
		}
		return super.handlePointerDragged(relX, relY, repaintRegion);
	}
	//#endif
	
	//#if polish.hasTouchEvents
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerTouchDown(int, int)
	 */
	public boolean handlePointerTouchDown(int x, int y) {
		if (this.enableScrolling) {
			this.lastPointerPressY = y;
			this.lastPointerPressYOffset = getScrollYOffset();
			this.lastPointerPressTime = System.currentTimeMillis();
		}
		Item item = this.focusedItem;
		if (item != null) {
			if (item.handlePointerTouchDown(x - item.relativeX, y - item.relativeY)) {
				return true;
			}
		}
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				if ( this.containerView.handlePointerTouchDown(x,y) ) {
					return true;
				}
			}
		//#endif
		return super.handlePointerTouchDown(x, y);
	}
	//#endif
	
	//#if polish.hasTouchEvents
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerTouchUp(int, int)
	 */
	public boolean handlePointerTouchUp(int x, int y) {
		Item item = this.focusedItem;
		if (item != null) {
			if (item.handlePointerTouchUp(x - item.relativeX, y - item.relativeY)) {
				return true;
			}
		}
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				if ( this.containerView.handlePointerTouchUp(x,y) ) {
					return true;
				}
			}
		//#endif
		if (this.enableScrolling) {
			int scrollDiff = Math.abs(getScrollYOffset() - this.lastPointerPressYOffset);
			if (scrollDiff > Display.getScreenHeight()/10) {
				long dragTime = System.currentTimeMillis() - this.lastPointerPressTime;
				if (dragTime < 1000 && dragTime > 1) {
					int direction = Canvas.DOWN;
					if (this.yOffset > this.lastPointerPressYOffset) {
						direction = Canvas.UP;
					}
					startScroll( direction,  (int) ((scrollDiff * 1000 ) / dragTime), 20 );
				} else if (this.yOffset > 0) {
					setScrollYOffset( 0, true );
				}
			}
		}
		return super.handlePointerTouchUp(x, y);
	}
	//#endif

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getItemAreaHeight()
	 */
	public int getItemAreaHeight()
	{
		int max =  super.getItemAreaHeight();
		Item item = this.focusedItem;
		if (item != null) {
			max = Math.max( max, this.contentY + item.relativeY + item.getItemAreaHeight() );
		}
		return max;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getItemAt(int, int)
	 */
	public Item getItemAt(int relX, int relY) {
		relY -= this.yOffset;
		//#ifdef polish.css.before
			relX -= getBeforeWidthWithPadding();
		//#endif
		relX -= this.contentX;
		relY -= this.contentY;
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				relX -= this.containerView.getScrollXOffset();
			}
		//#endif
		Item item = this.focusedItem;
		if (item != null) {
			int itemRelX = relX - item.relativeX;
			int itemRelY = relY - item.relativeY;
//			if (this.label != null) {
//				System.out.println("itemRelY=" + itemRelY + " of item " + item + ", parent=" + this );
//			}
			if (item.isInItemArea(itemRelX, itemRelY)) {
				return item.getItemAt(itemRelX, itemRelY);
			}
		}
		Item[] myItems = getItems();
		int itemRelX, itemRelY;
		for (int i = 0; i < myItems.length; i++) {
			item = myItems[i];
			itemRelX = relX - item.relativeX;
			itemRelY = relY - item.relativeY;
			if ( i == this.focusedIndex || !item.isInItemArea(itemRelX, itemRelY)) {
				// this item is not in the range or not suitable:
				continue;
			}
			// the pressed item has been found:
			return item.getItemAt(itemRelX, itemRelY);			
		}
		relY += this.yOffset;
		relX += this.contentX;
		relY += this.contentY;
		return super.getItemAt(relX, relY);
	}
	
	/**
	 * Retrieves the child of this container at the corresponding position.
	 * 
	 * @param relX the relative horizontal position
	 * @param relY the relatiev vertical position
	 * @return the item at that position, if any
	 */
	public Item getChildAt(int relX, int relY) {
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				return this.containerView.getChildAt( relX, relY );
			}
		//#endif
		return getChildAtImpl( relX, relY );
	}
	
	/**
	 * Actual implementation for finding a child, can be used by ContainerViews.
	 * @param relX the relative horizontal position
	 * @param relY the relative vertical position
	 * @return the child item at the specified position
	 */
	protected Item getChildAtImpl(int relX, int relY) {
		relY -= this.yOffset;
		//#ifdef polish.css.before
			relX -= getBeforeWidthWithPadding();
		//#endif
		relY -= this.contentY;
		relX -= this.contentX;
		//#ifdef tmp.supportViewType
			int viewXOffset = 0;
			ContainerView contView = this.containerView;
			if (contView != null) {
				viewXOffset = contView.getScrollXOffset(); 
				relX -= viewXOffset;
			}
		//#endif
		Item item = this.focusedItem;
		if (item != null && item.isInItemArea(relX - item.relativeX, relY - item.relativeY)) {
			return item;
		}
		Item[] myItems = getItems();
		int itemRelX, itemRelY;
		for (int i = 0; i < myItems.length; i++) {
			item = myItems[i];
			itemRelX = relX - item.relativeX;
			itemRelY = relY - item.relativeY;
			//System.out.println( item + ".relativeX=" + item.relativeX + ", .relativeY=" + item.relativeY + ", pointer event relatively at " + itemRelX + ", " + itemRelY);
			if ( i == this.focusedIndex || (item.appearanceMode == Item.PLAIN) || !item.isInItemArea(itemRelX, itemRelY)) {
				// this item is not in the range or not suitable:
				continue;
			}
			return item;
		}
		return null;
	}
	
	/**
	 * Moves the focus away from the specified item.
	 * 
	 * @param item the item that currently has the focus
	 */
	public void requestDefocus( Item item ) {
		if (item == this.focusedItem) {
			boolean success = shiftFocus(true, 1);
			if (!success) {
				defocus(this.itemStyle);
			}
		}
	}

	/**
	 * Requests the initialization of this container and all of its children items.
	 * This was previously used for dimension changes which is now picked up automatically and not required anymore.
	 */
	public void requestFullInit() {
		for (int i = 0; i < this.itemsList.size(); i++) {
			Item item = (Item) this.itemsList.get(i);
			item.setInitialized(false);
			if (item instanceof Container) {
				((Container)item).requestFullInit();
			}
		}
		requestInit();
	}

	/**
	 * Retrieves the vertical scrolling offset of this item.
	 *  
	 * @return either the currently used offset or the targeted offset in case the targeted one is different. This is either a negative integer or 0.
	 * @see #getCurrentScrollYOffset()
	 */
	public int getScrollYOffset() {
		if (!this.enableScrolling && this.parent instanceof Container) {
			return ((Container)this.parent).getScrollYOffset();
		}
		int offset = this.targetYOffset;
		//#ifdef polish.css.scroll-mode
			if (!this.scrollSmooth) {
				offset = this.yOffset;
			}
		//#endif
		return offset;
	}
	
	/**
	 * Retrieves the current vertical scrolling offset of this item, depending on the scroll mode this can change with every paint iteration.
	 *  
	 * @return the currently used offset in pixels, either a negative integer or 0.
	 * @see #getScrollYOffset()
	 */
	public int getCurrentScrollYOffset() {
		if (!this.enableScrolling && this.parent instanceof Container) {
			return ((Container)this.parent).getCurrentScrollYOffset();
		}
		return this.yOffset;
	}
	
	
	/**
	 * Retrieves the vertical scrolling offset of this item relative to the top most container.
	 *  
	 * @return either the currently used offset or the targeted offset in case the targeted one is different.
	 */
	public int getRelativeScrollYOffset() {
		if (!this.enableScrolling && this.parent instanceof Container) {
			return ((Container)this.parent).getRelativeScrollYOffset() + this.relativeY;
		}
		int offset = this.targetYOffset;
		//#ifdef polish.css.scroll-mode
			if (!this.scrollSmooth) {
				offset = this.yOffset;
			}
		//#endif
		return offset;
	}
	
	/**
	 * Sets the vertical scrolling offset of this item.
	 *  
	 * @param offset either the new offset
	 */
	public void setScrollYOffset( int offset) {
		setScrollYOffset( offset, false );
	}

	/**
	 * Sets the vertical scrolling offset of this item.
	 *  
	 * @param offset either the new offset
	 * @param smooth scroll to this new offset smooth if allowed
	 * @see #getScrollYOffset()
	 */
	public void setScrollYOffset( int offset, boolean smooth) {
		//#debug
		System.out.println("Setting scrollYOffset to " + offset + " for " + this);
		//try { throw new RuntimeException("for yOffset " + offset + " in " + this); } catch (Exception e) { e.printStackTrace(); }
		if (!this.enableScrolling && this.parent instanceof Container) {
			((Container)this.parent).setScrollYOffset(offset, smooth);
			return;
		}
		if (!smooth  
		//#ifdef polish.css.scroll-mode
			|| !this.scrollSmooth
		//#endif
		) {
			this.yOffset = offset;			
		}
		this.targetYOffset = offset;
		this.scrollSpeed = 0;
	}
	
	/**
	 * Determines whether this container or one of its parent containers is currently being scrolled
	 * @return true when this container or one of its parent containers is currently being scrolled
	 */
	public boolean isScrolling() {
		if (this.enableScrolling) {
			return (this.isScrolling) || (this.targetYOffset != this.yOffset)  ||  (this.scrollSpeed != 0); 
		} else if (this.parent instanceof Container) {
            return ((Container)this.parent).isScrolling();
        } else {
            return false;
        }
	}
	
	/**
	 * Starts to scroll in the specified direction
	 * @param direction either Canvas.UP or Canvas.DOWN
	 * @param speed the speed in pixels per second
	 * @param damping the damping in percent; 0 means no damping at all; 100 means the scrolling will be stopped immediately
	 */
	public void startScroll( int direction,int speed, int damping) {
		//#debug
		System.out.println("startScrolling " + (direction == Canvas.UP ? "up" : "down") + " with speed=" + speed + ", damping=" + damping + " for " + this);
		if (!this.enableScrolling && this.parent instanceof Container) {
			((Container)this.parent).startScroll(direction, speed, damping);
			return;
		}
		this.scrollDirection = direction;
		this.scrollDamping = damping;
		this.scrollSpeed = speed;
	}

	/**
	 * Retrieves the index of the specified item.
	 * 
	 * @param item the item
	 * @return the index of the item; -1 when the item is not part of this container
	 */
	public int indexOf(Item item) {
		Object[] myItems = this.itemsList.getInternalArray();
		for (int i = 0; i < myItems.length; i++) {
			Object object = myItems[i];
			if (object == null) {
				break;
			}
			if (object == item) {
				return i;
			}
		}
		return -1;
	}
	
	/** 
	 * Checks if this container includes the specified item
	 * @param item the item
	 * @return true when this container contains the item
	 */
	public boolean contains( Item item ) {
		return this.itemsList.contains(item);
	}
	
	//#if (polish.debug.error || polish.keepToString) && polish.debug.container.includeChildren
	/**
	 * Generates a String representation of this item.
	 * This method is only implemented when the logging framework is active or the preprocessing variable 
	 * "polish.keepToString" is set to true.
	 * @return a String representation of this item.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( super.toString() ).append( ": { ");
		Item[] myItems = getItems();
		for (int i = 0; i < myItems.length; i++) {
			Item item = myItems[i];
			//#if polish.supportInvisibleItems || polish.css.visible
				if (item.isInvisible) {
					buffer.append( i ).append(":invis./plain:" + ( item.appearanceMode == PLAIN ) + "=[").append( item.toString() ).append("]");
				} else {
					buffer.append( i ).append("=").append( item.toString() );
				}
			//#else
				buffer.append( i ).append("=").append( item.toString() );
			//#endif
			if (i != myItems.length - 1 ) {
				buffer.append(", ");
			}
		}
		buffer.append( " }");
		return buffer.toString();
	}
	//#endif

	/**
	 * Sets a list of items for this container.
	 * Use this direct access only when you know what you are doing.
	 * 
	 * @param itemsList the list of items to set
	 */
	public void setItemsList(ArrayList itemsList) {
		//System.out.println("Container.setItemsList");
		clear();
		if (this.isFocused) {
			//System.out.println("enabling auto focus for index=" + this.focusedIndex);
			this.autoFocusEnabled = true;
			this.autoFocusIndex = this.focusedIndex;
		}
		this.focusedIndex = -1;
		this.focusedItem = null;
		if (this.enableScrolling) {
			setScrollYOffset(0, false);
		}
		this.itemsList = itemsList;
		this.containerItems = null;
		Object[] myItems = this.itemsList.getInternalArray();
		for (int i = 0; i < myItems.length; i++) {
			Item item = (Item) myItems[i];
			if (item == null) {
				break;
			}
			item.parent = this;
			if (this.isShown) {
				item.showNotify();
			}
		}
		requestInit();
	}
	

	/**
	 * Calculates the number of interactive items included in this container.
	 * @return the number between 0 and size()
	 */
	public int getNumberOfInteractiveItems()
	{
		int number = 0;
		Object[] items = this.itemsList.getInternalArray();
		for (int i = 0; i < items.length; i++)
		{
			Item item = (Item) items[i];
			if (item == null) {
				break;
			}
			if (item.appearanceMode != PLAIN) {
				number++;
			}
		}
		return number;
	}

	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this background.
	 */
	public void releaseResources() {
		super.releaseResources();
		Item[] items = getItems();
		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			item.releaseResources();
		}
		//#ifdef tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.releaseResources();
			}
		//#endif
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#destroy()
	 */
	public void destroy() {
		Item[] items = getItems();
		
		clear();
		
		super.destroy();
		
		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			item.destroy();
		}
		
		//#ifdef tmp.supportViewType
		if (this.containerView != null) {
			this.containerView.destroy();
			this.containerView = null;
		}
		//#endif
	}

	/**
	 * Retrieves the internal array with all managed items embedded in this container, some entries might be null.
	 * Use this method only if you know what you are doing and only for reading.
	 * @return the internal array of the managed items
	 */
	public Object[] getInternalArray()
	{
		return this.itemsList.getInternalArray();
	}

//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Item#getAbsoluteY()
//	 */
//	public int getAbsoluteY()
//	{
//		return super.getAbsoluteY() + this.yOffset;
//	}
//	
//	//#ifdef tmp.supportViewType
//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Item#getAbsoluteX()
//	 */
//	public int getAbsoluteX() {
//		int xAdjust = 0;
//		if (this.containerView != null) {
//			xAdjust = this.containerView.getScrollXOffset();
//		}
//		return super.getAbsoluteX() + xAdjust;
//	}
//	//#endif
//	
//	
//	
//	/* (non-Javadoc)
//	 * @see de.enough.polish.ui.Item#getAbsoluteX()
//	 */
//	public boolean isInItemArea(int relX, int relY, Item child) {
//		relY -= this.yOffset;
//		//#ifdef tmp.supportViewType
//			if (this.containerView != null) {
//				relX -= this.containerView.getScrollXOffset();
//			}
//		//#endif
//		return super.isInItemArea(relX, relY, child);
//	}
//	
	

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#isInItemArea(int, int)
	 */
	public boolean isInItemArea(int relX, int relY) {
		Item focItem = this.focusedItem;
		if (focItem != null && focItem.isInItemArea(relX - focItem.relativeX, relY - focItem.relativeY)) {
			return true;
		}
		return super.isInItemArea(relX, relY);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#fireEvent(java.lang.String, java.lang.Object)
	 */
	public void fireEvent(String eventName, Object eventData)
	{
		super.fireEvent(eventName, eventData);
		Object[] items = this.itemsList.getInternalArray();
		for (int i = 0; i < items.length; i++)
		{
			Item item = (Item) items[i];
			if (item == null) {
				break;
			}
			item.fireEvent(eventName, eventData);
		}
	}


	//#ifdef polish.css.view-type
	/**
	 * Sets the view type for this item.
	 * Please note that this is only supported when view-type CSS attributes are used within
	 * your application.
	 * @param view the new view, use null to remove the current view
	 */
	public void setView( ItemView view ) {
		if (!(view instanceof ContainerView)) {
			super.setView( view );
			return;
		}
		if (!this.isStyleInitialised && this.style != null) {
			setStyle( this.style );
		}
		if (view == null) {
			this.containerView = null;
			this.view = null;
		} else {
			ContainerView viewType = (ContainerView) view;
			viewType.parentContainer = this;
			viewType.focusFirstElement = this.autoFocusEnabled;
			viewType.allowCycling = this.allowCycling;
			this.containerView = viewType;
			if (this.style != null) {
				view.setStyle( this.style );
			}
		}
		
		this.setView = true;
	}
	//#endif
	
	//#ifdef polish.css.view-type	
	/**
	 * Retrieves the view type for this item.
	 * Please note that this is only supported when view-type CSS attributes are used within
	 * your application.
	 * 
	 * @return the current view, may be null
	 */
	public ItemView getView() {
		if (this.containerView != null) {
			return this.containerView;
		}
		return this.view;
	}
	//#endif
	
	//#ifdef polish.css.view-type	
	/**
	 * Retrieves the view type for this item or instantiates a new one.
	 * Please note that this is only supported when view-type CSS attributes are used within
	 * your application.
	 * 
	 * @param viewType the view registered in the style
	 * @param viewStyle the style
	 * @return the view, may be null
	 */
	protected ItemView getView( ItemView viewType, Style viewStyle) {
		if (viewType instanceof ContainerView) {
			if (this.containerView == null || this.containerView.getClass() != viewType.getClass()) {
				try {
					// formerly we have used the style's instance when that instance was still free.
					// However, that approach lead to GC problems, as the style is not garbage collected.
					viewType = (ItemView) viewType.getClass().newInstance();
					viewType.parentItem = this;
					if (this.isShown) {
						if (this.containerView != null) {
							this.containerView.hideNotify();
						}
						viewType.showNotify();
					}
					return viewType;
				} catch (Exception e) {
					//#debug error
					System.out.println("Container: Unable to init view-type " + e );
				}
			}
			return this.containerView;
		}
		return super.getView( viewType, viewStyle );
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initMargin(de.enough.polish.ui.Style, int)
	 */
	protected void initMargin(Style style, int availWidth) {
		if (this.isIgnoreMargins) {
			this.marginLeft = 0;
			this.marginRight = 0;
			this.marginTop = 0;
			this.marginBottom = 0;
		} else {
			this.marginLeft = style.getMarginLeft( availWidth );
			this.marginRight = style.getMarginRight( availWidth );
			this.marginTop = style.getMarginTop( availWidth );
			this.marginBottom = style.getMarginBottom(availWidth);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#onScreenSizeChanged(int, int)
	 */
	public void onScreenSizeChanged(int screenWidth, int screenHeight) {
		Style lastStyle = this.style;
		super.onScreenSizeChanged(screenWidth, screenHeight);
		//#if tmp.supportViewType
			if (this.containerView != null) {
				this.containerView.onScreenSizeChanged(screenWidth, screenHeight);
			}
		//#endif
		//#if polish.css.landscape-style || polish.css.portrait-style
			if (this.plainStyle != null && this.style != lastStyle) {
				Style newStyle = null;
				if (screenWidth > screenHeight) {
					if (this.landscapeStyle != null && this.style != this.landscapeStyle) {
						newStyle = this.landscapeStyle;
					}
				} else if (this.portraitStyle != null && this.style != this.portraitStyle){
					newStyle = this.portraitStyle;
				}
				this.plainStyle = newStyle;
			}
		//#endif
		Object[] items = this.itemsList.getInternalArray();
		for (int i = 0; i < items.length; i++) {
			Item item = (Item) items[i];
			if (item == null) {
				break;
			}
			item.onScreenSizeChanged(screenWidth, screenHeight);
		}
	}

	/**
	 * Recursively returns the focused child item of this Container or of the currently focused child Container. 
	 * @return the focused child item or this Container when there is no focused child.
	 */
	public Item getFocusedChild() {
		Item item = getFocusedItem();
		if (item == null) {
			return this;
		}
		if (item instanceof Container) {
			return ((Container)item).getFocusedChild();
		}
		return item;
	}

	//#if polish.css.press-all
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#notifyItemPressedStart()
	 */
	public boolean notifyItemPressedStart() {
		boolean handled = super.notifyItemPressedStart();
		if (this.isPressAllChildren) {
			Object[] children = this.itemsList.getInternalArray();
			for (int i = 0; i < children.length; i++) {
				Item child = (Item) children[i];
				if (child == null) {
					break;
				}
				handled |= child.notifyItemPressedStart();
			}			
		}
		return handled;
	}
	//#endif


	//#if polish.css.press-all
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#notifyItemPressedEnd()
	 */
	public void notifyItemPressedEnd() {
		super.notifyItemPressedEnd();
		if (this.isPressAllChildren) {
			Object[] children = this.itemsList.getInternalArray();
			for (int i = 0; i < children.length; i++) {
				Item child = (Item) children[i];
				if (child == null) {
					break;
				}
				child.notifyItemPressedEnd();
			}
		}
	}
	//#endif

	/**
	 * Resets the pointer press y offset which is used for starting scrolling processes.
	 * This is only applicable for touch enabled handsets.
	 */
	public void resetLastPointerPressYOffset() {
		//#if polish.hasPointerEvents
			this.lastPointerPressYOffset = this.targetYOffset;
		//#endif
	}
	



//#ifdef polish.Container.additionalMethods:defined
	//#include ${polish.Container.additionalMethods}
//#endif

}
