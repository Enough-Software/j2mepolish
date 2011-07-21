/*
 * Created on 22-Oct-2004 at 20:16:19.
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


import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.enough.polish.dataeditor.DataManager;
import de.enough.polish.dataeditor.DataType;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        22-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CreateTypeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 899856310835630210L;

	private final List subtypes;
	private final DataManager manager; 

	/**
	 * Creates a new model for inserting a new type.
	 * 
	 * @param manager the manager of datatypes
	 */
	public CreateTypeTableModel( DataManager manager ) {
		super();
		this.manager = manager;
		this.subtypes = new ArrayList();
		this.subtypes.add( DataType.BYTE );
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.subtypes.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return new Integer( rowIndex );
		} else {
			DataType type = (DataType) this.subtypes.get( rowIndex ); 
			return type.getName();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		String typeName = aValue.toString();
		int separatorPos = typeName.lastIndexOf('(');
		typeName = typeName.substring( 0, separatorPos -1 );
		DataType type = this.manager.getDataType(typeName);
		if (type != null ) {
			this.subtypes.set( rowIndex, type );
		}
	}
	public DataType[] getSubtypes() {
		return (DataType[]) this.subtypes.toArray( new DataType[ this.subtypes.size() ] );
	}
	
	public void addSubtype() {
		this.subtypes.add( DataType.BYTE );
		fireTableRowsInserted( this.subtypes.size() - 2, this.subtypes.size() - 1 );
	}
	
	public void deleteSubtype( int index ) {
		this.subtypes.remove( index );
		fireTableRowsDeleted( this.subtypes.size(), this.subtypes.size() );
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		if (column == 0 ) {
			return "Index";
		} else {
			return "Subtype";
		}
	}
}
