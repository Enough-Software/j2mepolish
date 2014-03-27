//#condition polish.usePolishGui

/*
 * Created on Sept 24, 2012 at 10:18:40 PM.
 * 
 * Copyright (c) 2012 Robert Virkus / Enough Software
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

import de.enough.polish.util.IntHashMap;
import de.enough.polish.util.TimePoint;

/**
 * A container that contains only uniform items, meaning all items have the same type and same height.
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class UniformContainer 
extends Container 
implements ItemConsumer
{
	
	private UniformItemSource itemSource;
	private int childRowHeight;
	private int childStartIndex;
	private final BackupItemStorage backupItemStorage;
	private boolean	isIgnoreYOffsetChange;

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 */
	public UniformContainer(UniformItemSource itemSource) 
	{
		this( itemSource, false, null);
	}

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 * @param style the style
	 */
	public UniformContainer(UniformItemSource itemSource, Style style) 
	{
		this( itemSource, false, style);
	}

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 * @param focusFirst true when the first item should be focused automatically
	 */
	public UniformContainer(UniformItemSource itemSource, boolean focusFirst ) 
	{
		this( itemSource, focusFirst, null );
	}

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 * @param focusFirst true when the first item should be focused automatically
	 * @param style the style
	 */
	public UniformContainer(UniformItemSource itemSource, boolean focusFirst, Style style ) 
	{
		super(focusFirst, style );
		if (itemSource == null) {
			throw new NullPointerException();
		}
		this.itemSource = itemSource;
		itemSource.setItemConsumer(this);
		this.backupItemStorage = new BackupItemStorage(10);
	}
	
	/**
	 * Sets a new item source
	 * @param itemSource the new item source
	 * @throws NullPointerException when itemSource is null
	 */
	public void setItemSource( UniformItemSource itemSource) 
	{
		if (itemSource == null) {
			throw new NullPointerException();
		}
		this.itemSource = itemSource;
		requestInit();
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) 
	{
		//#debug info
		System.out.println("uc init " + firstLineWidth + ", " + availWidth + ", " + availHeight);
		synchronized (getSynchronizationLock()) 
		{
			this.itemsList.clear();
			int count = this.itemSource.countItems();
			if (count == 0)
			{
				this.isIgnoreYOffsetChange = true;
				setScrollYOffset(0);
				this.isIgnoreYOffsetChange = false;
				Item item = this.itemSource.getEmptyItem();
				if (item == null)
				{					
					this.contentHeight = 0;
				}
				else
				{
					this.itemsList.add(item);
					this.contentWidth = item.getItemWidth(firstLineWidth, availWidth, availHeight);
					if (item.isLayoutVerticalExpand())
					{
						item.setItemHeight(availHeight);
						this.contentHeight = availHeight;
					}
					else if (item.isLayoutBottom())
					{
						this.contentHeight = availHeight;
						item.relativeY = availHeight - item.itemHeight;
					}
					else if (item.isLayoutVerticalCenter())
					{
						this.contentHeight = availHeight;
						item.relativeY = (availHeight - item.itemHeight) / 2;
					}
					else
					{
						this.contentHeight = item.itemHeight;
						item.relativeY = 0;
					}
				}
				return;
			}
			Item item = this.itemSource.createItem(0);
			item.parent = this;
			this.itemsList.add(item);
			int rowHeight = item.getItemHeight(firstLineWidth, availWidth, availHeight) + this.paddingVertical;
			this.childRowHeight = rowHeight;
			int startIndex = Math.max( 0, (-this.yOffset)/rowHeight - 5);
			if (startIndex != 0) 
			{
				this.itemSource.populateItem(startIndex, item);
			}
			
			int height = (count * rowHeight) - this.paddingVertical;
			this.contentHeight = height;
			this.contentWidth = availWidth; // always use fully available width
			
			int numberOfRealItems = Math.min( count, (availHeight / rowHeight) + 10);
			if (count > numberOfRealItems) 
			{
				startIndex = Math.min(count - numberOfRealItems, startIndex);
			}
			this.childStartIndex = startIndex;
			for (int itemIndex=startIndex + 1; itemIndex < startIndex + numberOfRealItems; itemIndex++) {
				item = this.itemSource.createItem(itemIndex);
				item.parent = this;
				rowHeight = item.getItemHeight(firstLineWidth, availWidth, availHeight);
				item.relativeY = itemIndex * rowHeight;
				this.itemsList.add(item);
			}
			if (this.autoFocusEnabled)
			{
				this.autoFocusEnabled = false;
				int index = this.autoFocusIndex;
				if (index < startIndex)
				{ 
					index = startIndex;
				}
				else if (index > startIndex + numberOfRealItems)
				{
					index = startIndex + numberOfRealItems;
				}
				focusChild(index);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#onScrollYOffsetChanged(int)
	 */
	protected void onScrollYOffsetChanged(int offset) 
	{
		if (this.isIgnoreYOffsetChange)
		{
			return;
		}
		int count = this.itemSource.countItems();
		int itemsListSize = this.itemsList.size();
		if (count <= itemsListSize)
		{
			return;
		}
		int startIndex = Math.max( 0, (-offset)/this.childRowHeight - 5);
		if (count > itemsListSize) 
		{
			startIndex = Math.min(count - itemsListSize, startIndex);
		}
		int delta = Math.abs(startIndex - this.childStartIndex);
		if (delta != 0) {
			synchronized (getSynchronizationLock())
			{
				itemsListSize = this.itemsList.size();
				if (count > itemsListSize) 
				{
					startIndex = Math.min(count - itemsListSize, startIndex);
				}
				delta = Math.abs(startIndex - this.childStartIndex);
				if (delta >= itemsListSize)
				{
					// all items need to be re=populated:
					Object[] items = this.itemsList.getInternalArray();
					for (int itemIndex=0; itemIndex<itemsListSize; itemIndex++)
					{
						int index = startIndex + itemIndex;
						//System.out.println(".. current index: " + itemIndex + " / " + index + ", itemsListSize=" + itemsListSize  + " / " + itemsList.size());
						Item item = this.backupItemStorage.extract(index);
						Item prevItem = (Item) items[itemIndex];
						if (item != null) 
						{
							item.showNotify();
							prevItem.hideNotify();
							//this.itemsList.set(itemIndex, item);
							items[itemIndex] = item; // this works as we operate on the original array
						}
						else
						{
							item = prevItem;
							if (item.isFocused) {
								focusChild(-1);
							}
						}
						item.setInitialized(false);
						this.itemSource.populateItem(index, item);
						item.relativeY = index * this.childRowHeight;
						int cw = this.availContentWidth;
						int ch = this.availContentHeight;
						item.getItemHeight(cw, cw, ch);
					}
				}
				else
				{ 
					// only some items need to be re-populated
					if (startIndex > this.childStartIndex)
					{
						// scrolling down:
						for (int itemIndex=0; itemIndex<delta; itemIndex++) 
						{
							int index = startIndex + itemsListSize - delta + itemIndex;
							Item item = this.backupItemStorage.extract(index);
							Item prevItem = (Item) this.itemsList.remove(0);
							if (prevItem.isFocused)
							{
								focusChild(-1);
							}
							if (item != null) 
							{
								item.showNotify();
								prevItem.hideNotify();
							}
							else
							{
								item = prevItem;
							}
							this.itemsList.add(item);
							item.setInitialized(false);
							this.itemSource.populateItem(index, item);
							item.relativeY = index * this.childRowHeight;
							int cw = this.availContentWidth;
							int ch = this.availContentHeight;
							item.getItemHeight(cw, cw, ch);
						}
					}
					else
					{
						// scrolling up:
						for (int itemIndex=0; itemIndex<delta; itemIndex++) 
						{
							Item item = (Item) this.itemsList.remove(itemsListSize-1);
							if (item.isFocused)
							{
								focusChild(-1);
							}
							this.itemsList.add(0, item);
							int index = this.childStartIndex - itemIndex - 1;
							item.setInitialized(false);
							this.itemSource.populateItem(index, item);
							item.relativeY = index * this.childRowHeight;
							int cw = this.availContentWidth;
							int ch = this.availContentHeight;
							item.getItemHeight(cw, cw, ch);
						}
					}
				}
				this.childStartIndex = startIndex;
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#shiftFocus(boolean, int)
	 */
	protected boolean shiftFocus(boolean forwardFocus, int steps) 
	{
		if (forwardFocus)
		{
			int itemStartIndex = this.focusedIndex - this.childStartIndex + 1;
			int itemsListSize = this.itemsList.size();
			if (itemStartIndex >= 0 && itemStartIndex < itemsListSize)
			{
				for (int itemIndex = itemStartIndex; itemIndex < itemsListSize; itemIndex++)
				{
					Item item = (Item) this.itemsList.get(itemIndex);
					if (item.isInteractive())
					{
						focusChild(itemStartIndex + this.childStartIndex, item, Canvas.DOWN, false);
						return true;
					}
				}
			}
		}
		else
		{
			int itemStartIndex = this.focusedIndex - this.childStartIndex - 1;
			int itemsListSize = this.itemsList.size();
			if (itemStartIndex >= 0 && itemStartIndex < itemsListSize)
			{
				for (int itemIndex = itemStartIndex; itemIndex >= 0; itemIndex--)
				{
					Item item = (Item) this.itemsList.get(itemIndex);
					if (item.isInteractive())
					{
						focusChild(itemStartIndex + this.childStartIndex, item, Canvas.UP, false);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#focusClosestItem(int)
	 */
	public boolean focusClosestItem(int index) {
		int itemStartIndex = index - this.childStartIndex;
		int itemsListSize = this.itemsList.size();
		if (itemStartIndex >= 0 && itemStartIndex < itemsListSize)
		{
			for (int itemIndex = itemStartIndex + 1; itemIndex < itemsListSize; itemIndex++)
			{
				Item item = (Item) this.itemsList.get(itemIndex);
				if (item.isInteractive())
				{
					//TODO ok, now look UP if it's possibly closer...
					focusChild(itemStartIndex + this.childStartIndex, item, Canvas.DOWN, false);
					return true;
				}
			}
		}
		// okay, there was no item found in the current batch:
		return false;
		// TODO finish focusClosesItem
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#focusClosestItemAbove(int)
	 */
	public boolean focusClosestItemAbove(int index) {
		// TODO Auto-generated method stub
		return super.focusClosestItemAbove(index);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#get(int)
	 */
	public Item get(int index) {
		int itemIndex = index - this.childStartIndex;
		int itemsListSize = this.itemsList.size();
		if (itemIndex >= 0 && itemIndex < itemsListSize) 
		{
			return (Item) this.itemsList.get(itemIndex);
		}
		Item item = (Item) this.backupItemStorage.get(index);
		if (item != null)
		{
			return item;
		}
		item = this.itemSource.createItem(index);
		item.parent = this;
		item.relativeY = index * this.childRowHeight;
		int cw = this.availContentWidth;
		int ch = this.availContentHeight;
		item.getItemHeight(cw, cw, ch);
		this.backupItemStorage.put(itemIndex, item); 
		return item;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#getPosition(de.enough.polish.ui.Item)
	 */
	public int getPosition(Item item) {
		int index = this.itemsList.indexOf(item);
		if (index != -1)
		{
			index += this.childStartIndex;
		}
		else
		{
			int key = this.backupItemStorage.getKeyForValue(item);
			if (key != Integer.MIN_VALUE)
			{
				index = key;
			}
			
		}
		return index;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemConsumer#onItemsChanged(de.enough.polish.ui.ItemChangedEvent)
	 */
	public void onItemsChanged(ItemChangedEvent event)
	{
		synchronized (getSynchronizationLock())
		{
			int change = event.getChange();
			int itemIndex = event.getItemIndex();
			Item affectedItem = event.getAffectedItem();
			//#debug info
			System.out.println("onItemsChanged: change=" + event + ", index=" + itemIndex + ", at " + TimePoint.now().toStringTime());
			if (change == ItemChangedEvent.CHANGE_COMPLETE_REFRESH 
					|| (itemIndex == -1 && affectedItem == null))
			{
				this.isIgnoreYOffsetChange = true;
				boolean refocus = (this.isFocused && this.focusedItem != null);
				if (refocus || this.focusedItem != null)
				{
					focusChild(-1);
				}
				setScrollYOffset(0);
				if (refocus)
				{
					focusChild(0);
				}
				this.isIgnoreYOffsetChange = false;
			} 
			else if ((itemIndex == this.focusedIndex) || (itemIndex == -1))
			{
				itemIndex = this.focusedIndex; // in case the index is not known (-1), the item _could_ be the currently focused item
				if (itemIndex != -1)
				{
					if (change == ItemChangedEvent.CHANGE_SET || change == ItemChangedEvent.CHANGE_ADD)
					{
						if ((itemIndex >= this.childStartIndex) && (itemIndex < this.childStartIndex + this.itemsList.size()))
						{
							if (affectedItem == null)
							{
								affectedItem = (Item) this.itemsList.get(itemIndex - this.childStartIndex);
							}
							this.itemSource.populateItem(itemIndex, affectedItem);
							this.isIgnoreYOffsetChange = true;
							int offset = getScrollYOffset();
							focusChild( -1 );
							this.itemsList.set(itemIndex - this.childStartIndex, affectedItem );
							focusChild(itemIndex);
							setScrollYOffset(offset, false);
							this.isIgnoreYOffsetChange = false;
						}
					}
					else if (change == ItemChangedEvent.CHANGE_REMOVE)
					{
						this.isIgnoreYOffsetChange = true;
						focusChild(-1);
						setScrollYOffset(0);
						if (this.itemSource.countItems() > 0)
						{
							if (affectedItem == null)
							{
								affectedItem = (Item) this.itemsList.get(0);
							}
							this.itemSource.populateItem(0, affectedItem);
							focusChild(0);
						}
						this.isIgnoreYOffsetChange = false;
					}
				}
			}
		}
		requestInit();
	}	
	
	private static class BackupItemStorage 
	extends IntHashMap 
	{
		
		private final int	maxSize;

		BackupItemStorage(int maxSize) 
		{
			this.maxSize = maxSize;
		}

		public Item extract(int index) {
			Item item = (Item) super.remove(index);
			return item;
		}
	}

}
