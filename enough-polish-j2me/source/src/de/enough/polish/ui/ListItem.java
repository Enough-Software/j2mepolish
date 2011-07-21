//#condition polish.usePolishGui
/*
 * Created on 16-Feb-2005 at 09:45:41.
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


/**
 * <p>Provides a list of items that can be used within a Form.</p>
 * <p>The list item behaves like a normal J2ME Polish container, so 
 *    you can specify view-types, columns, colspans, etc.
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        16-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ListItem 
//#if polish.LibraryBuild
	extends FakeContainerCustomItem
//#else
	//# extends Container
//#endif
 
{

	/**
	 * Creates a new list item.
	 * 
	 * @param label the label of this item
	 */
	public ListItem(String label ) {
		this( label, null );
	}

	/**
	 * Creates a new list item.
	 * 
	 * @param label the label of this item
	 * @param style the style
	 */
	public ListItem(String label, Style style) {
		super(false, style );
		setLabel( label );
	}
	
	//#if polish.LibraryBuild
	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 */
	public void append( javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 */
	public void append( javax.microedition.lcdui.Item item, Style style ) {
		// ignore, only for the users
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Inserts the specified item into this list.
	 * 
	 * @param position the position into which the item should be inserted
	 * @param item the item that should be added
	 */
	public void insert( int position, javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Removes the specified item from this list.
	 * 
	 * @param item the item that should be removed
	 * @return true when the item was contained in this list.
	 */
	public boolean remove( javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
		return false;
	}
	//#endif
	
	/**
	 * Removes the specified item from this list.
	 * 
	 * @param index the index of the item that should be removed
	 * @return the item that has been at the specified index
	 */
	//#if polish.LibraryBuild
	public javax.microedition.lcdui.Item removeItem( int index ) {
		return null;
	//#else
		//# public Item removeItem( int index ) {
		//# return remove( index );
	//#endif
	}

	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 */
	public void append( Item item ) {
		super.add( item );
	}

	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 * @param itmStyle the item style
	 */
	public void append( Item item, Style itmStyle ) {
		super.add( item,  itmStyle );
	}

	/**
	 * Inserts the specified item into this list.
	 * 
	 * @param position the position into which the item should be inserted
	 * @param item the item that should be added
	 */
	public void insert( int position, Item item ) {
		add( position, item );
	}

	/**
	 * Inserts the specified item into this list and provides it with a style.
	 * 
	 * @param position the position into which the item should be inserted
	 * @param item the item that should be added
	 * @param itemStyle the style
	 */
	public void insert( int position, Item item, Style itemStyle ) {
		if (itemStyle != null) {
			item.setStyle( itemStyle );
		}
		add( position, item );
	}


	
	/**
	 * Clears this list.
	 */
	public void removeAll() {
		clear();
	}

}
