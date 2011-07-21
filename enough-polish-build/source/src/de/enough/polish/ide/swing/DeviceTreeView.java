/*
 * Created on 26-Jan-2006 at 14:57:08.
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.enough.polish.devices.DeviceTree;
import de.enough.polish.devices.DeviceTreeItem;

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
public class DeviceTreeView 
extends JTree
implements TreeExpansionListener
{
	private static final long serialVersionUID = 2343392013648834988L;

	/**
	 * Is used to visualize a device tree.
	 * 
	 * @param deviceTree the device tree
	 */
	public DeviceTreeView( DeviceTree deviceTree ) {
		super( new DeviceTreeModel( deviceTree ) );
		DeviceTreeItemCellRenderer renderer = new DeviceTreeItemCellRenderer();
		setCellRenderer( renderer );
		//setCellEditor( new DeviceTreeItemCellEditor(this, renderer) );
		getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		setShowsRootHandles( false );
		addMouseListener( new DeviceTreeSelectionListener() );
		int rows = getRowCount();
		ArrayList pathsToExpandList = new ArrayList();
		for (int i = 0; i < rows; i++) {
			TreePath path = getPathForRow( i );
			Object element = path.getLastPathComponent();
			if (element instanceof DeviceTreeCheckBox) {
				DeviceTreeItem item = ((DeviceTreeCheckBox)element).getDeviceTreeItem();
				if (item.isExpanded()) {
					pathsToExpandList.add( path );
				}
			}
		}
		for (Iterator iter = pathsToExpandList.iterator(); iter.hasNext();) {
			TreePath path = (TreePath) iter.next();
			expandPath( path );
		}
		addTreeExpansionListener( this );
	}

	static class DeviceTreeItemCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = -6531995911250649576L;

		public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean cellHasFocus) 
		{
			Component component = super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                cellHasFocus);
			if ( value instanceof Component) {
				component = (Component) value;
//				if (cellHasFocus) {
//					component.setBackground(tree.getSelectionBackground());
//					component.setForeground(tree.getSelectionForeground());
//				} else {
					component.setBackground(tree.getBackground());
					component.setForeground(tree.getForeground());
//				}
			}
			return component;
		}
	}
	
	class DeviceTreeSelectionListener extends MouseAdapter  {
		
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int row = getRowForLocation(x, y);
			TreePath path = getPathForRow(row);
			if (path != null) {
				Object node = path.getLastPathComponent();
				if (node instanceof DeviceTreeCheckBox ) {
					DeviceTreeCheckBox box = (DeviceTreeCheckBox) node;
					boolean select = !box.isSelected();
					box.getDeviceTreeItem().setIsSelected( select );
					if (select) {
						expandPath( path );
					} else {
						collapsePath( path );
					}
					repaint();
					fireValueChanged( new TreeSelectionEvent(node, path, true, null, null ) );
				}
			}
		}

		public void valueChanged(TreeSelectionEvent e) {
			System.out.println("value changed");
			//TreePath path = e.getNewLeadSelectionPath();
			
			Object source = getLastSelectedPathComponent(); //path.getLastPathComponent();
			if ( source instanceof DeviceTreeCheckBox ) {
				DeviceTreeCheckBox box = (DeviceTreeCheckBox) source;
				box.setSelected( !box.isSelected() );
			} else {
				System.out.println(e);
			}
		}
		
	}

	/**
	 * Uses the DeviceTree to rebuild it's state.
	 */
	public void rebuild() {
		super.revalidate();
		
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		Object element = event.getPath().getLastPathComponent();
		if (element instanceof DeviceTreeCheckBox) {
			((DeviceTreeCheckBox) element).getDeviceTreeItem().setExpanded( false );
		}
	}

	public void treeExpanded(TreeExpansionEvent event) {
		Object element = event.getPath().getLastPathComponent();
		if (element instanceof DeviceTreeCheckBox) {
			((DeviceTreeCheckBox) element).getDeviceTreeItem().setExpanded( true );
		}
	}

}
