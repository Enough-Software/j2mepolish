//#condition polish.usePolishGui

/*
 * Created on May 21, 2013 at 10:29:20 PM.
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

/**
 * A sourced container that only loads some items of the original item source.
 * Only when the user scrolls, further items are loaded and added.
 * Currently this requires an ItemSource with a DISTRIBUTION_PREFERENCE_BOTTOM preference.
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 *
 */
public class SourcedLazyContainer extends SourcedContainer {

	private ItemSource realItemSource;
	private WrappedItemSource wrappedItemSource;
	private boolean isDistributionPreferenceBottom;
	private int initialNumberOfItems;
	private boolean isIgnoreScrollOffsetChange;
	private int previousScrollYOffset;
	private int currentPointerDragY;

	public SourcedLazyContainer(ItemSource itemSource, int initialNumberOfItems) {
		this(itemSource, false, initialNumberOfItems, null);
	}

	public SourcedLazyContainer(ItemSource itemSource, int initialNumberOfItems, Style style) {
		this(itemSource, false, initialNumberOfItems, style);
	}

	public SourcedLazyContainer(ItemSource itemSource, boolean focusFirst, int initialNumberOfItems) {
		this(itemSource, focusFirst, initialNumberOfItems, null);
	}

	public SourcedLazyContainer(ItemSource itemSource, boolean focusFirst, int initialNumberOfItems,
			Style style) 
	{
		super( new WrappedItemSource(itemSource, initialNumberOfItems), focusFirst, style);
		this.initialNumberOfItems = initialNumberOfItems;
		this.realItemSource = itemSource;
		this.wrappedItemSource = (WrappedItemSource) this.itemSource;
		this.isDistributionPreferenceBottom = (itemSource.getDistributionPreference() == ItemSource.DISTRIBUTION_PREFERENCE_BOTTOM);
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.SourcedContainer#setItemSource(de.enough.polish.ui.ItemSource)
	 */
	public void setItemSource(ItemSource itemSource) {
		try
		{
			this.isIgnoreScrollOffsetChange = true;
			this.previousScrollYOffset = 0;
			if (!(itemSource instanceof WrappedItemSource))
			{
				this.realItemSource = itemSource;
				this.wrappedItemSource = new WrappedItemSource(itemSource, initialNumberOfItems);
				itemSource = this.wrappedItemSource;
			}
			super.setItemSource(itemSource);
		}
		finally
		{
			this.isIgnoreScrollOffsetChange = false;
		}
	}

	
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Container#onScrollYOffsetChanged(int)
	 */
	protected boolean handlePointerDragged(int relX, int relY,
			ClippingRegion repaintRegion) 
	{
		this.currentPointerDragY = relY;
		return super.handlePointerDragged(relX, relY, repaintRegion);
	}

	protected void onScrollYOffsetChanged(int offset) 
	{
		if (!this.isIgnoreScrollOffsetChange && this.isDistributionPreferenceBottom && this.isInitialized)
		{
			int triggerOffset = 20;
			if (!isBouncingAllowed())
			{
				triggerOffset = 0;
			}
			if (offset >= triggerOffset && offset > this.previousScrollYOffset && size() > 0)
			{
				try {
					this.isIgnoreScrollOffsetChange = true;
					int realCount = this.realItemSource.countItems();
					int shownCount = this.wrappedItemSource.currentNumberOfItems;
					this.previousScrollYOffset = offset;
					if (realCount > shownCount)
					{
						setInitialized(false);
						int number = Math.min( this.initialNumberOfItems, realCount - shownCount);
						int startIndex = realCount - shownCount - 1;
						int endIndex = startIndex - number + 1;
						//System.out.println("adding " + number + " items from " + startIndex + " to " + endIndex);
						this.wrappedItemSource.currentNumberOfItems += number;
						Item previousFirstItem = get(0);
						int previousRelativeY = previousFirstItem.relativeY;
						for (int itemIndex = startIndex; itemIndex >= endIndex; itemIndex--)
						{
							Item item = this.realItemSource.createItem(itemIndex);
							add(0, item);
						}
						init(this.availableWidth, this.availableWidth, this.availableHeight);
						int currentRelativeY = previousFirstItem.relativeY;
						int newOffset = previousRelativeY - currentRelativeY + offset;
						//System.out.println("changing offset from " + offset + " to " + newOffset + " for " + this);
						setScrollYOffset( newOffset, false);
						this.lastPointerPressY = this.currentPointerDragY;
						this.lastPointerPressYOffset = newOffset;
						this.previousScrollYOffset = newOffset;
					}
				} 
				finally
				{
					this.isIgnoreScrollOffsetChange = false;
				}
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.SourcedContainer#onItemsChanged(de.enough.polish.ui.ItemChangedEvent)
	 */
	public void onItemsChanged(ItemChangedEvent event) {
		//System.out.println("onItemsChanged: " + event);
		int change = event.getChange();
		if (change == ItemChangedEvent.CHANGE_COMPLETE_REFRESH)
		{
			this.wrappedItemSource.refresh( this.initialNumberOfItems );
		}
		else
		{
			int index = event.getItemIndex();
			if (index != -1)
			{
				if (change == ItemChangedEvent.CHANGE_ADD)
				{
					this.wrappedItemSource.currentNumberOfItems++;
				}
				int realCount = this.realItemSource.countItems();
				int shownCount = this.wrappedItemSource.currentNumberOfItems;
				int newIndex = index - (realCount - shownCount);
				//System.out.println("onItemsChanged: adjusting index from " + index + " to " + newIndex);
				if (newIndex < 0)
				{
					// this event affects an item that is currently not shown:
					return;
				}
				event.setItemIndex( newIndex );
			}
		}
		super.onItemsChanged(event);
	}






	private static class WrappedItemSource implements ItemSource
	{
		private ItemSource source;
		private int currentNumberOfItems;

		public WrappedItemSource(ItemSource source, int initialNumberOfItems)
		{
			this.source = source;
			refresh(initialNumberOfItems);
		}

		public void refresh(int initialNumberOfItems) {
			this.currentNumberOfItems = Math.min(source.countItems(), initialNumberOfItems);			
		}

		public int countItems() {
			int count = this.source.countItems();
			if (count > this.currentNumberOfItems)
			{
				count = this.currentNumberOfItems;
			}
			return count;
		}

		public Item createItem(int index) {
			if (this.source.getDistributionPreference() == DISTRIBUTION_PREFERENCE_BOTTOM)
			{
				int count = this.source.countItems();
				if (count > this.currentNumberOfItems)
				{
					//System.out.println("real=" + count + ", currentNumber=" + currentNumberOfItems + ", previousIndex=" + index + ", translatedIndex="  + (index + count - currentNumberOfItems));
					index += count - this.currentNumberOfItems;
				}
			}
			return this.source.createItem(index);
		}

		public void setItemConsumer(ItemConsumer consumer) {
			this.source.setItemConsumer(consumer);
		}

		public int getDistributionPreference() {
			return this.source.getDistributionPreference();
		}

		public Item getEmptyItem() {
			return this.source.getEmptyItem();
		}
	}
}
