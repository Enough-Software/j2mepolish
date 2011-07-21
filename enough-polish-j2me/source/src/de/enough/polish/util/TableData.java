/*
 * Created on Apr 10, 2008 at 12:17:17 PM.
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
package de.enough.polish.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

/**
 * <p>Allows to manage data in a tabular structure.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TableData implements Externalizable
{
	private static final int VERSION = 90;
	private int numberOfColumns;
	private int numberOfRows;
	private Object[][] data;
	
	
	/**
	 * Creates a new empty table
	 */
	public TableData() {
		this( 0, 0 );
	}
	
	/**
	 * Creates a table with the specified dimensions
	 * 
	 * @param columns the number of columns 
	 * @param rows  the number of rows
	 */
	public TableData(int columns, int rows)
	{
		setDimension(columns, rows);
	}

	/**
	 * Specifies a new dimension for this table
	 * 
	 * @param columns the number of columns 
	 * @param rows  the number of rows
	 */
	public void setDimension(int columns, int rows) {
		this.data = new Object[columns][rows];
		this.numberOfColumns = columns;
		this.numberOfRows = rows;		
	}

	/**
	 * Retrieves the number of columns
	 * @return the number of columns
	 */
	public int getNumberOfColumns() {
		return this.numberOfColumns;
	}

	/**
	 * Retrieves the number of rows
	 * @return the number of rows
	 */
	public int getNumberOfRows() {
		return this.numberOfRows;
	}
	
	/**
	 * Retrieves the size.
	 * @return numberOfRows() * numberOfColumns()
	 */
	public int size() {
		return this.numberOfRows * this.numberOfColumns;
	}

	
	/**
	 * Adds a new column to this table.
	 * @return the index of the created column, the first/most left one has the index 0.
	 */
	public int addColumn() {
		Object[][] previousData = this.data;
		int cols = this.numberOfColumns;
		setDimension(cols + 1, this.numberOfRows );
		Object[][] currentData = this.data;
		for (int col = 0; col < cols; col++ ) {
			currentData[col] = previousData[col];
		}
		this.numberOfColumns = cols + 1;
		return cols;
	}
	
	/**
	 * Inserts a column before the specified index.
	 * 
	 * @param index the index, use 0 for adding a column in the front, use getNumberOfColumns() for appending the column at the end
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void insertColumn( int index ) {
		if (index < 0 || index > this.numberOfColumns) {
			throw new ArrayIndexOutOfBoundsException();
		}
		if (index == this.numberOfColumns) {
			addColumn();
			return;
		}
		
		int cols = this.numberOfColumns;
		Object[][] newData = new Object[cols + 1][];
		Object[][] previousData = this.data;
		int source = 0;
		for (int col = 0; col < newData.length; col++)
		{
			if (col == index) {
				newData[col] = new Object[ this.numberOfRows];
				col++;
			}
			newData[col] = previousData[source];
			source++;
		}
		this.numberOfColumns = cols + 1;
		this.data = newData;
	}
	
	/**
	 * Removes the specified column
	 * 
	 * @param index the index. 0 is the first column
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void removeColumn( int index ) {
		if (index < 0 || index > this.numberOfColumns) {
			throw new ArrayIndexOutOfBoundsException();
		}
		int cols = this.numberOfColumns;
		Object[][] newData = new Object[cols -1][this.numberOfRows];
		Object[][] previousData = this.data;
		int target = 0;
		for (int col = 0; col < newData.length; col++)
		{
			if (target == index) {
				target++;
			}
			newData[col] = previousData[target];
			target++;
		}
		this.numberOfColumns = cols - 1;
		this.data = newData;
	}
	
	
	/**
	 * Adds a new row to this table.
	 * @return the index of the created row, the first/top one has the index 0.
	 */
	public int addRow() {
		Object[][] previousData = this.data;
		int rows = this.numberOfRows;
		for (int col = 0; col < previousData.length; col++)
		{
			Object[] previousRow = previousData[col];
			Object[] newRow = new Object[ rows + 1];
			System.arraycopy( previousRow, 0, newRow, 0, rows );
			previousData[col] = newRow;
		}
		this.numberOfRows = rows + 1;
		return rows;
	}
	
	/**
	 * Inserts a row before the specified index.
	 * 
	 * @param index the index, use 0 for adding a row in the top, use getNumberOfRows() for appending the column at the bottom
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void insertRow( int index ) {
		if (index < 0 || index > this.numberOfRows) {
			throw new ArrayIndexOutOfBoundsException();
		}
		if (index == this.numberOfRows) {
			addRow();
			return;
		}
		
		int cols = this.numberOfColumns;
		int rows = this.numberOfRows + 1;
		Object[][] newData = new Object[cols][rows];
		Object[][] currentData = this.data;
		int source = 0;
		for (int col = 0; col < cols; col++)
		{
			Object[] currentColumn = currentData[col];
			Object[] newColumn = newData[col];
			source = 0;
			for (int row = 0; row < rows; row++)
			{
				if (row == index) {
					row++;
				}
				newColumn[row] = currentColumn[source];
				source++;
			}
		}
		this.numberOfRows = rows;
		this.data = newData;
	}
	
	/**
	 * Removes the specified row
	 * 
	 * @param index the index. 0 is the first row
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void removeRow( int index ) {
		if (index < 0 || index > this.numberOfRows) {
			throw new ArrayIndexOutOfBoundsException();
		}
		int rows = this.numberOfRows - 1;
		Object[][] currentData = this.data;
		int target;
		for (int col = 0; col < this.numberOfColumns; col++)
		{
			Object[] previousColumn = currentData[col];
			Object[] newColumn = new Object[ rows ];
			target = 0;
			for (int row = 0; row < rows; row++)
			{
				if (target == index) {
					target++;
				}
				newColumn[row] = previousColumn[target];
				target++;
			}
			currentData[col] = newColumn;
		}
		this.numberOfRows = rows;
	}

	/**
	 * Sets the value of the given table position.
	 * @param column the horizontal position
	 * @param row the vertical position
	 * @param value the value, use null to delete a previous set value
	 * @throws ArrayIndexOutOfBoundsException when the column or row is invalid
	 * @see #get(int, int)
	 */
	public void set( int column, int row, Object value ) {
//		if (column < 0) {
//			throw new ArrayIndexOutOfBoundsException("For column " + column);
//		}
//		if (row < 0) {
//			throw new ArrayIndexOutOfBoundsException("For row " + row);
//		}
		this.data[column][row] = value;
	}
	

	/**
	 * Retrieves the value of the given table position.
	 * @param column the horizontal position
	 * @param row the vertical position
	 * @return the value stored at the given position
	 * @throws ArrayIndexOutOfBoundsException when the column or row is invalid
	 * @see #set(int, int, Object)
	 */
	public Object get( int column, int row ) {
		return this.data[column][row];
	}
	
	/**
	 * Retrieves the value of the given table position.
	 * @param index the position of the item
	 * @return the value stored at the given position
	 * @throws ArrayIndexOutOfBoundsException when the column or row is invalid
	 * @see #get(int, int)
	 */
	public Object get( int index ) {
		return get( index % this.numberOfColumns, index / this.numberOfColumns );
	}
	
	/**
	 * Retrieves the internal two dimensional array of this table.
	 * Handle with care!
	 * 
	 * @return the internal array
	 */
	public Object[][] getInternalData() {
		return this.data;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException
	{
		out.writeInt( VERSION );
		int columns = this.numberOfColumns;
		int rows = this.numberOfRows;
		out.writeInt( columns );
		out.writeInt( rows );
		Object[][] currentData = this.data;
		for (int col = 0; col < columns; col++)
		{
			for (int row = 0; row < rows; row++)
			{
				Serializer.serialize( currentData[col][row], out);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException
	{
		int version = in.readInt();
		//#if polish.debug
			if (version > VERSION) {
				//#debug warn
				System.out.println("unsupported table item version: " + version);
			}
		//#endif
		int columns = in.readInt();
		int rows = in.readInt();
		setDimension(columns, rows);
		Object[][] currentData = this.data;
		for (int col = 0; col < columns; col++)
		{
			for (int row = 0; row < rows; row++)
			{
				currentData[col][row] = Serializer.deserialize( in );
			}
		}
	}

	/**
	 * Retrieves the column index for the specified item or object
	 * @param obj the object
	 * @return the column index, -1 when the obj was not found
	 */
	public int getColumnIndex(Object obj) {
		int columns = this.numberOfColumns;
		int rows = this.numberOfRows;
		for (int col = 0; col < columns; col++)
		{
			for (int row = 0; row < rows; row++)
			{
				Object dt = this.data[col][row];
				if (dt == obj) {
					return col;
				}
			}
		}
		return -1;
	}
	

	/**
	 * Retrieves the column index for the specified item or object
	 * @param obj the object
	 * @return the row index, -1 when the obj was not found
	 */
	public int getRowIndex(Object obj) {
		int columns = this.numberOfColumns;
		int rows = this.numberOfRows;
		for (int col = 0; col < columns; col++)
		{
			for (int row = 0; row < rows; row++)
			{
				Object dt = this.data[col][row];
				if (dt == obj) {
					return row;
				}
			}
		}
		return -1;
	}


}
