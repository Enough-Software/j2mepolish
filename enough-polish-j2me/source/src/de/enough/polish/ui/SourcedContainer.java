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

import de.enough.polish.util.TimePoint;


/**
 * A container that loads items from an ItemSource.
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SourcedContainer 
extends Container 
implements ItemConsumer
{
	
	private ItemSource itemSource;
	private int distributionPreference;
	private boolean	isEmpty;

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 */
	public SourcedContainer(ItemSource itemSource) 
	{
		this( itemSource, false, null);
	}

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 * @param style the style
	 */
	public SourcedContainer(ItemSource itemSource, Style style) 
	{
		this( itemSource, false, style);
	}

	/**
	 * Creates a new container
	 * 
	 * @param itemSource the item source
	 * @param focusFirst true when the first item should be focused automatically
	 */
	public SourcedContainer(ItemSource itemSource, boolean focusFirst ) 
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
	public SourcedContainer(ItemSource itemSource, boolean focusFirst, Style style ) 
	{
		super(focusFirst, style );
		if (itemSource == null) {
			throw new NullPointerException();
		}
		setItemSource(itemSource);
	}
	
	/**
	 * Sets a new item source
	 * @param itemSource the new item source
	 * @throws NullPointerException when itemSource is null
	 */
	public void setItemSource(ItemSource itemSource) 
	{
		if (itemSource == null) {
			throw new NullPointerException();
		}
		synchronized (getSynchronizationLock())
		{
			//#debug
			System.out.println("before setItemSource: " + this.itemsList.size());
			try 
			{
				this.itemSource = itemSource;
				itemSource.setItemConsumer(this);
				this.distributionPreference = itemSource.getDistributionPreference();
				int count = this.itemSource.countItems();
				focusChild(-1);
				this.itemsList.clear();
				if (this.isInitialized)
				{
					this.contentHeight = 0;
					this.itemHeight = this.paddingTop + this.paddingBottom + getBorderWidthTop() + getBorderWidthBottom() + (this.label != null ? this.label.itemHeight : 0);
					this.backgroundHeight = this.itemHeight;
					setScrollYOffset(0, false);
				}
				for (int itemIndex = 0; itemIndex < count; itemIndex++)
				{
					Item item = itemSource.createItem(itemIndex);
					add(item);
				}
				if (count == 0)
				{
					Item item = itemSource.getEmptyItem();
					if (item != null)
					{
						this.isEmpty = true;
						add(item);
					}
				}
				else
				{
					this.isEmpty = false;
				}
			} 
			catch (Exception e)
			{
				//#debug error
				System.out.println("setItemSource" + e);
			}
		}
		//#debug
		System.out.println("after setItemSource: " + this.itemsList.size());
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.ItemConsumer#onItemsChanged(de.enough.polish.ui.ItemChangedEvent)
	 */
	public void onItemsChanged(ItemChangedEvent event)
	{
		//#debug info
		System.out.println("itemsChanged, initialized=" + this.isInitialized + ", event=" + event + ", size=" + size() + " for " + this); //" at " + TimePoint.now().toStringTime());
		int change = event.getChange();
		int itemIndex = event.getItemIndex();
		if (change == ItemChangedEvent.CHANGE_COMPLETE_REFRESH || itemIndex == -1)
		{
			setItemSource(this.itemSource);
			return;
		}
		if (change == ItemChangedEvent.CHANGE_ADD || change == ItemChangedEvent.CHANGE_SET)
		{
			Item nextItem = event.getAffectedItem();
			if (nextItem == null)
			{
				nextItem = this.itemSource.createItem(itemIndex);
			}
			if (change == ItemChangedEvent.CHANGE_ADD)
			{
				if (this.isEmpty)
				{
					this.isEmpty = false;
					clear();
				}
				if (itemIndex >= this.itemsList.size()-1)
				{
					if (!this.isInitialized)
					{
						add( nextItem );
					}
					else // this container is initialized already, just scroll down to the new item:
					{
						setInitialized(false);
						add( nextItem );
						int height = nextItem.getItemHeight(this.availContentWidth, this.availContentWidth, this.availContentHeight);
						nextItem.relativeX = 0;
						if (nextItem.isLayoutRight())
						{
							nextItem.relativeX = this.availContentWidth - nextItem.itemWidth;
						}
						else if (nextItem.isLayoutCenter())
						{
							nextItem.relativeX = (this.availContentWidth - nextItem.itemWidth) / 2;
						}
						nextItem.relativeY = this.contentHeight + this.paddingVertical;
						Item p = this;
						while (p != null)
						{
							p.contentHeight += height;
							p.itemHeight += height;
							p = p.parent;
						}
						setInitialized(true);
						if (this.distributionPreference == ItemSource.DISTRIBUTION_PREFERENCE_BOTTOM)
						{
							scrollToBottom();
						}
					}
				}
				else
				{
					add( itemIndex, nextItem );
				}
			} 
			else // if (change == ItemChangedEvent.CHANGE_SET)
			{
				if (itemIndex >= this.itemsList.size())
				{
					setItemSource(this.itemSource);
				}
				else if (!this.isInitialized)
				{
					set( itemIndex, nextItem );
				}
				else
				{
					Item previousItem = get(itemIndex);
					int previousHeight = previousItem.itemHeight;
					setInitialized(false);
					set( itemIndex, nextItem );
					int height = nextItem.getItemHeight(this.availContentWidth, this.availContentWidth, this.availContentHeight);
					nextItem.relativeX = 0;
					if (nextItem.isLayoutRight())
					{
						nextItem.relativeX = this.availContentWidth - nextItem.itemWidth;
					}
					else if (nextItem.isLayoutCenter())
					{
						nextItem.relativeX = (this.availContentWidth - nextItem.itemWidth) / 2;
					}
					nextItem.relativeY = previousItem.relativeY;
					setInitialized(true);
					if (height != previousHeight)
					{
						requestInit();
					}
				}
			}
		}
		else if (change == ItemChangedEvent.CHANGE_REMOVE)
		{
			int size = this.itemsList.size();
			if (!this.isInitialized || (itemIndex < size - 1))
			{
				remove(itemIndex);
			}
			else if (itemIndex < size) // last element is removed
			{
				setInitialized(false);
				Item prevItem = remove(itemIndex);
				//System.out.println("removing with height=" + prevItem.itemHeight + ": " + prevItem);
				int height = prevItem.itemHeight + this.paddingVertical;
				Item p = this;
				while (p != null)
				{
					p.contentHeight -= height;
					p.itemHeight -= height;
					p.backgroundHeight -= height;
					p = p.parent;
				}
				setInitialized(true);
				if (this.distributionPreference == ItemSource.DISTRIBUTION_PREFERENCE_BOTTOM)
				{
					scrollToBottom();
				}
			}
			else
			{
				setItemSource(this.itemSource);
			}
		}
		//#debug
		System.out.println("after: size=" + size() + ", source=" + this.itemSource.countItems());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Container#setScrollHeight(int)
	 */
	public void setScrollHeight( int height ) {
		super.setScrollHeight(height);
		if (height != -1 && this.distributionPreference == ItemSource.DISTRIBUTION_PREFERENCE_BOTTOM)
		{
			scrollToBottom();
		}
	}
	
//	/*
//	 * (non-Javadoc)
//	 * @see de.enough.polish.ui.Item#init(int, int, int)
//	 */
//	protected void init(int firstLineWidth, int availWidth, int availHeight)
//	{
//		super.init(firstLineWidth, availWidth, availHeight);
//		if (this.isFirstInitialization)
//		{
//			this.isFirstInitialization = false;
////			if ( this.distributionPreference == ItemSource.DISTRIBUTION_PREFERENCE_BOTTOM && this.itemsList.size() > 0)
////			{
////				this.scrollItem = (Item) this.itemsList.get(this.itemsList.size()-1);
//				//scrollToBottom();
////				Container cont = this;
////				int yOff = -this.contentHeight;
////				while (cont != null)
////				{
////					if (cont.enableScrolling)
////					{
////						cont.yOffset = yOff;
////					}
////					if (cont.parent instanceof Container)
////					{
////						yOffset -= cont.relativeY;
////						cont = (Container) cont.parent;
////					}
////					else
////					{
////						break;
////					}
////				}
//			}
//		}
//	}
}
