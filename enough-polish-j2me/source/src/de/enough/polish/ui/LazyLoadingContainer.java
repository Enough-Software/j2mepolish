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
import de.enough.polish.util.IntList;

/**
 * A container that loads items only when they are required.
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LazyLoadingContainer 
extends Container 
implements ItemConsumer
{
	private static final int	MIN_NUMBER_OF_ITEMS	= 10;
	
	private ItemSource itemSource;
	private int distributionPreference;
	private int childRowHeight;
	private int childStartIndex;
	private final BackupItemStorage backupItemStorage;
	private int	previousYOffset;
	private final IntList itemIndexList;

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 */
	public LazyLoadingContainer(ItemSource itemSource) 
	{
		this( itemSource, false, null);
	}

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 * @param style the style
	 */
	public LazyLoadingContainer(ItemSource itemSource, Style style) 
	{
		this( itemSource, false, style);
	}

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 * @param focusFirst true when the first item should be focused automatically
	 */
	public LazyLoadingContainer(ItemSource itemSource, boolean focusFirst ) 
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
	public LazyLoadingContainer(ItemSource itemSource, boolean focusFirst, Style style ) 
	{
		super(focusFirst, style );
		if (itemSource == null) {
			throw new NullPointerException();
		}
		this.itemIndexList = new IntList();
		this.itemSource = itemSource;
		itemSource.setItemConsumer(this);
		this.distributionPreference = itemSource.getDistributionPreference();
		this.backupItemStorage = new BackupItemStorage(10);
	}
	
	/**
	 * Sets a new item source
	 * @param itemSource the new item source
	 * @throws NullPointerException when itemSource is null
	 */
	public void setItemSource( ItemSource itemSource) 
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
		
		synchronized (this.itemsList) 
		{
			this.itemsList.clear();
			this.itemIndexList.clear();
			int count = this.itemSource.countItems();
			if (count == 0)
			{
				this.contentHeight = 0;
				return;
			}
			int comulatedHeight = 0;
			int numberOfAddedItems = 0;
			if (this.distributionPreference == ItemSource.DISTRIBUTION_PREFERENCE_BOTTOM)
			{
				int startIndex = count - 1;
				for (int itemIndex=startIndex; itemIndex >= 0; itemIndex--)
				{
					Item item = this.itemSource.createItem(itemIndex);
					this.itemsList.add(0, item);
					this.itemIndexList.add(0, itemIndex);
					int rowHeight = item.getItemHeight(firstLineWidth, availWidth, availHeight) + this.paddingVertical;
					comulatedHeight += rowHeight;
					numberOfAddedItems++;
					if ((comulatedHeight > (availHeight*2)) && (numberOfAddedItems >= MIN_NUMBER_OF_ITEMS)) // that's 2 screen heights filled.
					{
						break;
					}
				}
			}
			int estimatedHeight = count * comulatedHeight / numberOfAddedItems;
			this.childRowHeight = estimatedHeight / count;
			this.contentHeight = estimatedHeight;
			boolean smooth = (this.yOffset != 0);
			if (estimatedHeight > availHeight && (this.distributionPreference == ItemSource.DISTRIBUTION_PREFERENCE_BOTTOM))
			{
				this.previousYOffset = availHeight - estimatedHeight;
				setScrollYOffset(availHeight - estimatedHeight, smooth);
			}
			if (this.distributionPreference == ItemSource.DISTRIBUTION_PREFERENCE_BOTTOM)
			{
				int lastRelativeY = estimatedHeight;
				for (int index=this.itemsList.size()-1; index >= 0; index-- )
				{
					Item item = (Item) this.itemsList.get(index);
					item.relativeY = lastRelativeY - item.itemHeight;
					lastRelativeY -= item.itemHeight + this.paddingVertical;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#onScrollYOffsetChanged(int)
	 */
	protected void onScrollYOffsetChanged(int offset) 
	{
		synchronized (this.itemsList)
		{
			int prevOffset = this.previousYOffset;
			int diff = Math.abs(offset - prevOffset);
			if (diff < 10)
			{
				// ignore small changes:
				return;
			}
			Item firstItem = (Item) this.itemsList.get(0);
			int firstItemIndex = this.itemIndexList.get(0);
			int itemsListLastIndex = this.itemsList.size() - 1;
			Item lastItem = (Item) this.itemsList.get(itemsListLastIndex);
			//System.out.println("new offset=" + offset + ", was=" + prevOffset + ", offset + lastItem.relativeY=" + (offset + lastItem.relativeY) + ", availContentHeight=" + this.availContentHeight);
			if (offset > prevOffset)
			{
				// scrolled upwards
				while (offset + lastItem.relativeY > this.availContentHeight 
						&& firstItemIndex > 0)
				{
					// remove last item:
					this.itemsList.remove(itemsListLastIndex);
					this.itemIndexList.removeElementAt(itemsListLastIndex);
					firstItemIndex--;
					Item item = this.itemSource.createItem(firstItemIndex);
					int itemHeight = item.getItemHeight(this.availContentWidth, this.availContentWidth, this.availContentHeight);
					item.relativeY = firstItem.relativeY - this.paddingVertical - itemHeight;
					firstItem = item;
					this.itemsList.add(0, firstItem);
					this.itemIndexList.add(0, firstItemIndex);
					lastItem = (Item) this.itemsList.get(itemsListLastIndex);
					if (firstItemIndex == 0 && item.relativeY != 0) {
						int itemRelativeY = 0;
						for (int index=0; index < itemsListLastIndex; index++)
						{
							item = (Item) this.itemsList.get(index);
							item.relativeY = itemRelativeY;
							itemRelativeY += item.itemHeight + this.paddingVertical;
						}
						setScrollYOffset(0, false);
					}
				}
			}
			else if (offset < prevOffset)
			{
				int lastItemIndex = this.itemIndexList.get(itemsListLastIndex);
				int count = this.itemSource.countItems();
				// scrolled downwards:
				int minTopOffset = -(this.availContentHeight/2);
				while (offset + firstItem.relativeY + firstItem.itemHeight < minTopOffset
						&& lastItemIndex < count - 1)
				{
					this.itemsList.remove(0);
					this.itemIndexList.removeElementAt(0);
					lastItemIndex++;
					Item item = this.itemSource.createItem(lastItemIndex);
					item.getItemHeight(this.availContentWidth, this.availContentWidth, this.availContentHeight);
					item.relativeY = lastItem.relativeY + lastItem.itemHeight + this.paddingVertical;
					lastItem = item;
					this.itemsList.add(lastItem);
					this.itemIndexList.add(lastItemIndex);
					firstItem = (Item) this.itemsList.get(0);
					//System.out.println("added index=" + lastItemIndex);	
				}
			}
			this.childStartIndex = this.itemIndexList.get(0);
			this.previousYOffset = offset;
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
		item.getItemHeight(this.contentWidth, this.contentWidth, this.contentHeight);
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
