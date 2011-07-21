/*
 * Created on 19-Oct-2004 at 19:28:33.
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

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import de.enough.polish.dataeditor.DataManager;
import de.enough.polish.dataeditor.DataType;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataView extends JTable {

	private static final long serialVersionUID = 3655944205917161708L;

	/**
	 * @param parentFrame
	 * @param dataModel
	 * @param manager
	 */
	public DataView(JFrame parentFrame, DataTableModel dataModel, DataManager manager ) {
		super(dataModel);
		TableColumnModel colModel = getColumnModel();
		colModel.getColumn( 0 ).setPreferredWidth( 60 ); // name
		colModel.getColumn( 1 ).setPreferredWidth( 20 ); // count
		colModel.getColumn( 2 ).setPreferredWidth( 60 ); // type
		colModel.getColumn( 3 ).setPreferredWidth( 240 ); // data
		colModel.getColumn( 1 ).setCellEditor( new CountCellEditor( parentFrame, manager ));
		colModel.getColumn( 3 ).setCellEditor( new DataCellEditor( parentFrame, manager ));
		updateTypes( manager );
	}
	
	public void updateTypes( DataManager manager ) {
		TableColumnModel colModel = getColumnModel();
		// set combo-box for types:
		JComboBox comboBox = new JComboBox();
		DataType[] types = manager.getDataTypes();
		for (int i = 0; i < types.length; i++) {
			DataType type = types[i];
			comboBox.addItem( type.getName() + " (" + type.getNumberOfBytes() + ")" );
		}
		colModel.getColumn( 2 ).setCellEditor(new DefaultCellEditor(comboBox));	
	}

}
