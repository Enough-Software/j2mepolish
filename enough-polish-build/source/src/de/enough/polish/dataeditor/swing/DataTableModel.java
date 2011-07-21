/*
 * Created on 19-Oct-2004 at 19:29:55.
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

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import de.enough.polish.dataeditor.DataEntry;
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
public class DataTableModel 
extends AbstractTableModel
{
	private static final long serialVersionUID = 1447486151953084724L;

	private final DataManager dataManager;
	private final SwingDataEditor parentFrame;

	/**
	 * @param dataManager
	 * @param parentFrame
	 * 
	 */
	public DataTableModel( DataManager dataManager, SwingDataEditor parentFrame ) {
		super();
		this.dataManager = dataManager;
		this.parentFrame = parentFrame;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 4;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.dataManager.getNumberOfEntries();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex) {
		switch ( columnIndex ) {
			case 0: return String.class;
			case 1: return String.class;
			case 2: return String.class;
			case 3: return String.class;
		}	
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		DataEntry entry = this.dataManager.getDataEntry( rowIndex );
		switch ( columnIndex ) {
			case 0: return entry.getName();
			case 1: return entry.getCountAsString();
			case 2: return entry.getType().getName();
			case 3: 
				if ( entry.getCount() == 0 ) { 
					return "-";
				}
				String[] data = entry.getDataAsString();
				if ( entry.getCount() == 1) {
					return data[0];
				}
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < data.length; i++) {
					buffer.append('[') 
						.append( data[i] )
						.append("] ");
				}
				return buffer.toString();
		}
		return "undefined column: " + columnIndex;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		DataEntry entry = this.dataManager.getDataEntry( rowIndex );

		String strValue = aValue.toString();
		//System.out.println("Setting value [" + strValue + "] for column [" + columnIndex + "].");
		try {
			switch ( columnIndex ) {
				case 0: 
					this.dataManager.setEntryName( strValue, entry );
					break;
				case 1: 
					this.dataManager.setCountAsString(strValue, entry);
					break;
				case 2:
					strValue = strValue.substring(0, strValue.lastIndexOf(' '));
					DataType type = this.dataManager.getDataType( strValue );
					this.dataManager.setEntryType(type, entry);
					break;
				case 3:
					if (entry.getCount() == 0) {
						showErrorMessage("Unable to set data-value: count is 0.");
						return;
					} else if (entry.getCount() == 1) {
						this.dataManager.setDataAsString(strValue, entry);
					} else {
						String[] values = new String[ entry.getCount() ];
						int startPos = strValue.indexOf( '[' );
						for (int i = 0; i < values.length; i++) {
							if (startPos == -1) {
								showErrorMessage( "Unable to set multiple data \"" + strValue + "\" - missing opening parenthesis.");
								return;
							}
							int endPos = strValue.indexOf(']', startPos );
							if (endPos == -1) {
								showErrorMessage( "Unable to set multiple data \"" + strValue + "\" - missing closing parenthesis.");
								return;
							}
							values[i] = strValue.substring( startPos + 1, endPos );
							//System.out.println("values[" + i +"] = " + values[i] );
							startPos = strValue.indexOf('[', endPos );
						}
						this.dataManager.setDataAsString(values, entry);
					}
			}
			fireTableDataChanged();
		} catch (Exception e) {
			e.printStackTrace();
			this.parentFrame.setStatusBarWarning( "Unable to set value: " + e.toString() );
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		switch ( columnIndex ) {
			case 0: return "Name";
			case 1: return "Count";
			case 2: return "Type";
			case 3: return "Data";
		}
		return "Unknown Column";
	}


	/**
	 * Updates the view
	 * @param view the view
	 */
	public void refresh( JTable view ) {
		if ( view.isEditing() ) {
			view.getCellEditor().cancelCellEditing();
		}
		fireTableDataChanged();
	}
	

	private void showErrorMessage( String message ) {
		JOptionPane.showMessageDialog( this.parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE );
	}
	

}
