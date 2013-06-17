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
	private int maxNumberOfItems;

	public SourcedLazyContainer(ItemSource itemSource, int initialNumberOfItems, int maxNumberOfItems) {
		this(itemSource, false, initialNumberOfItems, maxNumberOfItems, null);
	}

	public SourcedLazyContainer(ItemSource itemSource, int initialNumberOfItems, int maxNumberOfItems, Style style) {
		this(itemSource, false, initialNumberOfItems, maxNumberOfItems, style);
	}

	public SourcedLazyContainer(ItemSource itemSource, boolean focusFirst, int initialNumberOfItems, int maxNumberOfItems) {
		this(itemSource, focusFirst, initialNumberOfItems, maxNumberOfItems, null);
	}

	public SourcedLazyContainer(ItemSource itemSource, boolean focusFirst, int initialNumberOfItems, 
			int maxNumberOfItems, Style style) 
	{
		super( new WrappedItemSource(itemSource, initialNumberOfItems, maxNumberOfItems), focusFirst, style);
		if (maxNumberOfItems < 2 * initialNumberOfItems)
		{
			throw new IllegalArgumentException("maxNumberOfItems needs to be at least twice initialNumberOfItems");
		}
		this.initialNumberOfItems = initialNumberOfItems;
		this.maxNumberOfItems = maxNumberOfItems;
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
				this.wrappedItemSource = new WrappedItemSource(itemSource, this.initialNumberOfItems, this.maxNumberOfItems);
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
			synchronized (getSynchronizationLock())
			{
				if (offset >= triggerOffset && offset > this.previousScrollYOffset && size() > 0)
				{
					// the user scrolls upwards to/over the first item, so now is a good time
					// to ask for more items:
					try {
						this.isIgnoreScrollOffsetChange = true;
						IndexRange indexRange = this.wrappedItemSource.indexRange;
						this.previousScrollYOffset = offset;
						if (indexRange.canMoveUp())
						{
							setInitialized(false);
							int startIndex = indexRange.getIndexStart() - 1;
							int endIndex = Math.max(startIndex - this.initialNumberOfItems + 1, 0);
							//System.out.println("adding " + number + " items from " + startIndex + " to " + endIndex);
							//this.wrappedItemSource.currentNumberOfItems += number;
							Item previousFirstItem = get(0);
							int previousRelativeY = previousFirstItem.relativeY;
							for (int itemIndex = startIndex; itemIndex >= endIndex; itemIndex--)
							{
								Item item = this.realItemSource.createItem(itemIndex);
								add(0, item);
								if (indexRange.moveRangeUpRequiresDeleteAtEnd())
								{
									remove(size() - 1);
								}
							}
							init(this.availableWidth, this.availableWidth, this.availableHeight);
							int currentRelativeY = previousFirstItem.relativeY;
							int newOffset = previousRelativeY - currentRelativeY + offset;
							//System.out.println("changing offset from " + offset + " to " + newOffset + " for " + this);
							setScrollYOffset( newOffset, false);
							this.lastPointerPressY = this.currentPointerDragY;
							this.lastPointerPressYOffset = newOffset;
							this.previousScrollYOffset = newOffset;
							//System.out.println("up: size=" + size() + ", startIndex=" + indexRange.getIndexStart() + ", endIndex=" + indexRange.getIndexEnd() );
							//System.out.println("prev=" + previousRelativeY + ", curr=" + currentRelativeY + ", offset=" + offset + ", newOffset=" + newOffset);
						}
					} 
					finally
					{
						this.isIgnoreScrollOffsetChange = false;
					}
				} // if (offset >= triggerOffset && offset > this.previousScrollYOffset && size() > 0)
				else if ((this.maxNumberOfItems > 1) && (size() >= this.maxNumberOfItems) && (offset <= this.scrollHeight - getItemAreaHeight()))
				{
					// the user scrolls down to items that have been cleared previously:
					try 
					{
						this.isIgnoreScrollOffsetChange = true;
						IndexRange indexRange = this.wrappedItemSource.indexRange;
						this.previousScrollYOffset = offset;
						if (indexRange.canMoveDown())
						{
							setInitialized(false);
							Item previousLastItem = get(size() - 1);
							int previousRelativeY = previousLastItem.relativeY;
	
							int itemIndex = indexRange.getIndexEnd() + 1;
							int maxIndex = itemIndex + this.initialNumberOfItems;
							while (indexRange.canMoveDown() && itemIndex < maxIndex)
							{
								Item item = this.realItemSource.createItem(itemIndex);
								add(item);
								if (indexRange.moveRangeDownRequiresDeleteAtStart())
								{
									remove(0);
								}
								itemIndex++;
							}
							//System.out.println("down: size=" + size() + ", startIndex=" + indexRange.getIndexStart() + ", endIndex=" + indexRange.getIndexEnd() );
							init(this.availableWidth, this.availableWidth, this.availableHeight);
							int currentRelativeY = previousLastItem.relativeY;
							int newOffset = previousRelativeY - currentRelativeY + offset;
							//System.out.println("prev=" + previousRelativeY + ", curr=" + currentRelativeY + ", offset=" + offset + ", newOffset=" + newOffset);
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
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.SourcedContainer#onItemsChanged(de.enough.polish.ui.ItemChangedEvent)
	public void onItemsChanged(ItemChangedEvent event) {
		//System.out.println("onItemsChanged: " + event);
		int change = event.getChange();
		if (change == ItemChangedEvent.CHANGE_COMPLETE_REFRESH)
		{
			this.wrappedItemSource.refresh();
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
		 */







	private static class WrappedItemSource implements ItemSource, ItemConsumer
	{
		private ItemSource source;
		private ItemConsumer consumer;
		public final IndexRange indexRange;
		

		public WrappedItemSource(ItemSource source, int initialNumberOfItems, int maxNumberOfItems)
		{
			this.source = source;
			this.indexRange = new IndexRange(initialNumberOfItems, maxNumberOfItems, source);
			this.indexRange.refresh(this.source);
			source.setItemConsumer(this);
		}

		public void refresh() 
		{
			this.indexRange.refresh(this.source);
		}

		public int countItems() {
			return this.indexRange.getRange();
		}

		public Item createItem(int index) {
			index = this.indexRange.translateIndexToOriginal(index);
			return this.source.createItem(index);
		}

		public void setItemConsumer(ItemConsumer consumer) {
			this.consumer = consumer;
		}

		public int getDistributionPreference() {
			return this.source.getDistributionPreference();
		}

		public Item getEmptyItem() {
			return this.source.getEmptyItem();
		}

		public void onItemsChanged(ItemChangedEvent event) {
			//System.out.println("Wrapped: " + event);
			int change = event.getChange();
			if (change == ItemChangedEvent.CHANGE_COMPLETE_REFRESH)
			{
				refresh();
			}
			else
			{
				int index = event.getItemIndex();
				if (index != -1)
				{
					if (change == ItemChangedEvent.CHANGE_ADD)
					{
						//System.out.println("add: range.indexEnd=" + indexRange.indexEnd + ", index=" + index);
						int indexEnd = this.indexRange.getIndexEnd();
						if (index == indexEnd + 1)
						{
							//TODO: this behavior is only for LAYOUT_BOTTOM ItemSources correct
							boolean needToDelete = this.indexRange.moveRangeDownRequiresDeleteAtStart();
							//System.out.println("need to delete: " + needToDelete);
							if (needToDelete && this.consumer != null)
							{
								// need to remove first item in order to make space:
								Item affectedItem = event.getAffectedItem();
								event.setAffectedItem(null);
								event.setChange(ItemChangedEvent.CHANGE_REMOVE);
								event.setItemIndex(0);
								this.consumer.onItemsChanged(event);
								event.setChange(ItemChangedEvent.CHANGE_ADD);
								event.setAffectedItem(affectedItem);
							}
						}
						else //TODO allow item to be added to the top or within the current range
						{
							// this event does not affect the currently shown range:
							return;
						}
					} 
					else if (!this.indexRange.isInRange(index))
					{
						// this event does not affect the currently shown range:
						return;
					}
					int translatedIndex = this.indexRange.translateIndexFromOriginal(index);
					event.setItemIndex(translatedIndex);
				}
			}
			if (this.consumer != null)
			{
				this.consumer.onItemsChanged(event);
			}
		}
	}
	
	private static class IndexRange
	{
		private int	indexStart;
		private int indexEnd;
		private final int maxNumberOfItems;
		private final int initialNumberOfItems;
		private ItemSource source;
		
		public IndexRange(int initialNumberOfItems, int maxNumberOfItems, ItemSource source)
		{
			this.initialNumberOfItems = initialNumberOfItems;
			this.maxNumberOfItems = maxNumberOfItems;
			refresh(source);
		}
		
		public boolean canMoveDown() {
			return this.indexEnd < this.source.countItems() - 1;
		}
		
		public boolean canMoveUp()
		{
			return this.indexStart  > 0;
		}

		public boolean isInRange(int index) {
			return (index >= this.indexStart) && (index <= this.indexEnd);
		}

		public int translateIndexToOriginal(int index) {
			return index + this.indexStart;
		}

		public int translateIndexFromOriginal(int index) {
			return index - this.indexStart;
		}

		public boolean moveRangeDownRequiresDeleteAtStart()
		{
			boolean needToDelete = false;
			this.indexEnd++;
			if (this.indexEnd - this.indexStart >= this.maxNumberOfItems && this.maxNumberOfItems > 0)
			{
				this.indexStart++;
				needToDelete = true;
			}
			return needToDelete;
		}
		
		public boolean moveRangeUpRequiresDeleteAtEnd()
		{
			boolean needToDelete = false;
			if (this.indexStart == 0)
			{
				return false;
			}
			this.indexStart--;
			if (this.indexEnd - this.indexStart >= this.maxNumberOfItems && this.maxNumberOfItems > 0)
			{
				needToDelete = true;
				this.indexEnd--;
			}
			return needToDelete;
		}			
			
		public int getIndexStart()
		{
			return this.indexStart;
		}
		
		public int getIndexEnd()
		{
			return this.indexEnd;
		}
		
		public int getRange()
		{
			return this.indexEnd - this.indexStart + 1;
		}
		
		public void refresh(ItemSource source)
		{
			this.source = source;
			int count = source.countItems();
			int numberOfItems = count;
			if (numberOfItems > this.initialNumberOfItems)
			{
				numberOfItems = this.initialNumberOfItems;
			}
			if (source.getDistributionPreference() == ItemSource.DISTRIBUTION_PREFERENCE_BOTTOM)
			{
				this.indexEnd = count - 1;
				this.indexStart = count - numberOfItems;
			}
			else
			{
				this.indexStart = 0;
				this.indexEnd = numberOfItems - 1;
			}
		}
	}
}
