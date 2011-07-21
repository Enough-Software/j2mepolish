/*
 * Created on 26-Jan-2006 at 22:11:32.
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

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

import de.enough.polish.ide.swing.DeviceTreeView.DeviceTreeItemCellRenderer;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DeviceTreeItemCellEditor
//extends DefaultTreeCellEditor {
implements TreeCellEditor {

	private DeviceTreeCheckBox currentItem;

	/**
	 * @param renderer 
	 * @param view 
	 * 
	 */
	public DeviceTreeItemCellEditor(DeviceTreeView view, DeviceTreeItemCellRenderer renderer) {
		//super( view, renderer );
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
	 */
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) 
	{
		if (value instanceof DeviceTreeCheckBox) {
			this.currentItem =  (DeviceTreeCheckBox) value;
			System.out.println( "retrieving " + this.currentItem.getDeviceTreeItem() + " as editor" );
			return this.currentItem;
		}
		System.out.println("no editor for " + value );
		this.currentItem = null;
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	public void cancelCellEditing() {
		// TODO enough implement cancelCellEditing

	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		// TODO enough implement stopCellEditing
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		if (this.currentItem != null) {
			return Boolean.valueOf( this.currentItem.isSelected() );
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject event) {
		if (event.getSource() instanceof DeviceTreeCheckBox) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	public boolean shouldSelectCell(EventObject event) {
		if (event.getSource() instanceof DeviceTreeCheckBox) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void addCellEditorListener(CellEditorListener l) {
		// TODO enough implement addCellEditorListener

	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void removeCellEditorListener(CellEditorListener l) {
		// TODO enough implement removeCellEditorListener

	}

}
