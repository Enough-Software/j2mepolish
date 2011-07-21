/*
 * Created on 02-Nov-2004 at 01:54:30.
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
import java.awt.image.BufferedImage;

import javax.swing.AbstractCellEditor;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import de.enough.polish.dataeditor.DataEntry;
import de.enough.polish.dataeditor.DataManager;
import de.enough.polish.dataeditor.DataType;

/**
 * <p>Invokes editors for specific datatypes in the DataView.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataCellEditor 
extends AbstractCellEditor
implements TableCellEditor, ActionListener 
{
	private static final long serialVersionUID = -6453045956191058472L;

	private final JFrame parentFrame;
	private final DataManager manager;
	private final JTextField textField;
	private final DataEntryButton button;
	private DataEditorDialog editor;
	private boolean useTextField;

	/**
	 * Creates a new data cell editor
	 * 
	 * @param parentFrame the parent
	 * @param manager the manager for the binary data
	 */
	public DataCellEditor( JFrame parentFrame, DataManager manager) {
		super();
		this.parentFrame = parentFrame;
		this.manager = manager;
		this.textField = new JTextField();
		this.button = new DataEntryButton();
		this.button.setText("Edit...");
		this.button.addActionListener( this );
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		if (this.useTextField) {
			return this.textField.getText();
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		DataEntry entry = this.manager.getDataEntry(row);
		if (entry.getCount() == 1 && entry.getType() != DataType.PNG_IMAGE) {
			this.textField.setText( value.toString() );
			this.textField.selectAll();
			this.useTextField = true;
			return this.textField;
		} else if (entry.getCount() == 0) {
			return null;
		} else {
			this.useTextField = false;
			this.button.setEntry( entry );
			return this.button;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof DataEntryButton) {
			DataEntry entry = this.button.getEntry();
			if (entry.getType() == DataType.PNG_IMAGE) {
				openPngImageEditor( entry );
				return;
			}
			this.editor = new DataEditorDialog( this.parentFrame, "Edit " + entry.getName(), entry );
			this.editor.setVisible(true);
			if (this.editor.okPressed()) {
				String[] data = this.editor.getData();
				this.manager.setDataAsString( data, entry );
			}
			fireEditingStopped();
		}
	}

	/**
	 * Shows a simple image-viewer which allows the external saving and loading of the image.
	 */
	private void openPngImageEditor( DataEntry entry ) {
		Object[] data = entry.getData();
		BufferedImage image = null;
		if (data != null && data.length == 1) {
			Object o = data[0];
			if ( o instanceof BufferedImage ) {
				image = (BufferedImage) o;
			}
		}
		ImageEditorDialog dialog = new ImageEditorDialog( this.parentFrame, "PNG-Image " + entry.getName(), image, this.manager.getCurrentDirectory() );
		dialog.setVisible( true );
		if ( dialog.isChanged() ) {
			this.manager.setData( dialog.getImage(), entry );
		}
		fireEditingStopped();
	}
		
	

}
