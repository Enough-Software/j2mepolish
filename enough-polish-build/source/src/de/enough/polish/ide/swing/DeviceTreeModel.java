/*
 * Created on 26-Jan-2006 at 15:01:06.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.ide.swing;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.enough.polish.devices.DeviceTree;
import de.enough.polish.devices.DeviceTreeItem;

/**
 * <p>Manages the data model of a device tree.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
class DeviceTreeModel implements TreeModel {

	private final DeviceTree deviceTree;
	private final String rootElement;

	/**
	 * @param deviceTree the base device tree
	 * 
	 */
	public DeviceTreeModel( DeviceTree deviceTree ) {
		super();
		this.deviceTree = deviceTree;
		this.rootElement = "devices";
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return this.rootElement;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		if (parent == this.rootElement) {
			return this.deviceTree.getNumberOfRootItems();
		} else {
			return this.deviceTree.getChildCount( ((DeviceTreeCheckBox)parent).getDeviceTreeItem() );
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		if (node == this.rootElement) {
			return false;
		}
		DeviceTreeItem item = ((DeviceTreeCheckBox) node).getDeviceTreeItem();
		return item.getChildCount() == 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {
		// TODO enough implement addTreeModelListener

	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		// TODO enough implement removeTreeModelListener

	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		DeviceTreeItem child;
		if (parent == this.rootElement) {
			child = this.deviceTree.getRootItems()[ index ];
		} else {
			DeviceTreeItem item = ((DeviceTreeCheckBox) parent).getDeviceTreeItem();
			child = item.getChildren()[ index ];
		}
		Object data = child.getData();
		if (data == null) {
			data = new DeviceTreeCheckBox( child, false );
			child.setData( data );
		}
		return data;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {
		// TODO enough implement getIndexOfChild
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO enough implement valueForPathChanged

	}

}