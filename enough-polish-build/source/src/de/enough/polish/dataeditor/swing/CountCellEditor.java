/*
 * Created on 02-Nov-2004 at 14:06:07.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.dataeditor.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import de.enough.polish.dataeditor.DataEntry;
import de.enough.polish.dataeditor.DataManager;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CountCellEditor extends AbstractCellEditor implements
		TableCellEditor, ActionListener {

	private static final long serialVersionUID = -3429071280277525478L;

	private final JFrame parentFrame;
	private final DataManager manager;
	private final JButton button;
	private Object currentValue;
	private int currentRow;

	/**
	 * @param parentFrame
	 * @param manager
	 * 
	 */
	public CountCellEditor( JFrame parentFrame, DataManager manager ) {
		super();
		this.parentFrame = parentFrame;
		this.manager = manager;
		this.button = new JButton("Edit");
		this.button.addActionListener( this );
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) 
	{
		this.currentRow = row;
		this.currentValue = value;
		return this.button;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		DataEntry entry = this.manager.getDataEntry( this.currentRow );
		DataEntry[] possibleOperants = this.manager.getCountOperants( this.currentRow );
		CountEditorDialog editor = new CountEditorDialog( this.parentFrame, 
				"Edit count of " + entry.getName(), possibleOperants, entry.getCountAsString() );
		editor.setVisible(true);
		this.currentValue = editor.getText();
		fireEditingStopped();
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return this.currentValue;
	}

}
