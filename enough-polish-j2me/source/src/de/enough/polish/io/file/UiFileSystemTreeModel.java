//#condition polish.api.fileconnection && polish.usePolishGui
/*
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

package de.enough.polish.io.file;

import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;

/**
 * Provides the file system of a device as a TreeModel.
 * 
 * @author Robert Virkus
 * @see de.enough.polish.ui.TreeModel
 * @see de.enough.polish.ui.TreeItem
 * @see FileSystemTreeModel
 */
public class UiFileSystemTreeModel extends FileSystemTreeModel {
	
	protected ItemCreator itemCreator;

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		if (node instanceof Item) {
			FileSystemNode fsNode = (FileSystemNode) ((Item)node).getAttribute(this);
			return super.isLeaf(fsNode);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TreeModel#addChildren(java.lang.Object, de.enough.polish.util.ArrayList)
	 */
	public void addChildren(Object parent, ArrayList list) {
		if (parent instanceof Item) {
			parent = ((Item)parent).getAttribute(this);
		}
		super.addChildren(parent, list);
		for (int i=0; i<list.size(); i++) {
			FileSystemNode node = (FileSystemNode) list.get(i);
			Item item;
			if (this.itemCreator != null) {
				item = this.itemCreator.createItem(node);				
			} else {
				Style nodeStyle = getStyle(node);
				item = new IconItem( node.getPath(), null, nodeStyle );
			}
			item.setAttribute(this, node);
			addCommands( node, item );
			list.set(i, item);
		}
	}

	/**
	 * Adds commands to this item.
	 * The default implementation does not add any commands.
	 * 
	 * @param node the file system node
	 * @param item the corresponding item.
	 */
	protected void addCommands(FileSystemNode node, Item item) {
		// subclasses can override this to specify commands
	}

	/**
	 * Selects a style for the given node.
	 * This method is not called when an ItemCreator has been registered.
	 * The default implementation uses the <code>.directory</code> style for directories
	 * and <code>.file</code> for files.
	 * 
	 * @param node the file system node that includes the path (name and suffix)
	 * @return a style for that node, null when no style should be applied
	 * @see #setItemCreator(ItemCreator)
	 */
	protected Style getStyle(FileSystemNode node) {
		Style nodeStyle = null;
		if (node.isDirectory()) {
			//#if polish.css.style.directory
				//#style directory?
				//#= nodeStyle = ();
			//#endif
		} else {
			//#if polish.css.style.file
				//#style file?
				//#= nodeStyle = ();
			//#endif
		}
		return nodeStyle;
	}
	
	/**
	 * Registers an ItemCreator which creates items for FileSystemNodes.
	 * Note that you can implement your own complex style handling, register your specific commands and ItemCommandListener, etc within the
	 * ItemCreator.createItem(FileSystemNode) method.
	 * 
	 * @param itemCreator the ItemCreator that should be used, use null to de-register.
	 */
	public void setItemCreator( ItemCreator itemCreator ) {
		this.itemCreator = itemCreator;
	}

}
