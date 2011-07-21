/*
 * Created on Apr 10, 2008 at 1:00:45 PM.
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

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TableDataTest extends TestCase
{

	
	public TableDataTest(String name)
	{
		super(name);
	}
	
	public void testSimpleCase() {
		int columns;
		int rows;
		TableData table;
		
		columns = 5;
		rows = 2;
		table = new TableData( columns, rows );
		for (int col = 0; col < columns; col++) 
		{
			for (int row = 0; row < rows; row++ ) {
				table.set(col, row, new Integer( (row * columns) + col ) );
			}
		}
		for (int col = 0; col < columns; col++) 
		{
			for (int row = 0; row < rows; row++ ) {
				Integer value = (Integer) table.get(col, row);
				assertEquals( (row * columns) + col, value.intValue() );
			}
		}
	}
	
	public void testAddColumnAddRow() {
		TableData table;
		int index;
		
		table = new TableData();
		index = table.addColumn();
		assertEquals( 0, index );
		index = table.addRow();
		assertEquals( 0, index );
		table.set( 0, 0, "hello");
		assertEquals( "hello", table.get(0, 0));
		
		table = new TableData();
		index = table.addRow();
		assertEquals( 0, index );
		index = table.addColumn();
		assertEquals( 0, index );
		table.set( 0, 0, "hello");
		assertEquals( "hello", table.get(0, 0));
		
		index = table.addColumn();
		assertEquals( 1, index );
		assertEquals( "hello", table.get(0, 0));
		assertEquals( null, table.get(1, 0));
		
		index = table.addRow();
		assertEquals( 1, index );
		assertEquals( "hello", table.get(0, 0));
		assertEquals( null, table.get(1, 0));
		assertEquals( null, table.get(0, 1));
		assertEquals( null, table.get(1, 1));
		
		table = new TableData( 2, 2 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );

		index = table.addColumn();
		assertEquals( 2, index );
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(1, 0));
		assertEquals( "0.1", table.get(0, 1));
		assertEquals( "1.1", table.get(1, 1));
		assertEquals( null, table.get( 2, 0 ));
		assertEquals( null, table.get( 2, 1 ));
		
		table = new TableData( 2, 2 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );

		index = table.addRow();
		assertEquals( 2, index );
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(1, 0));
		assertEquals( "0.1", table.get(0, 1));
		assertEquals( "1.1", table.get(1, 1));
		assertEquals( null, table.get( 0, 2 ));
		assertEquals( null, table.get( 1, 2 ));

	}
	
	public void testRemoveColum() {
		TableData table;

		table = new TableData( 2, 2 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );

		table.removeColumn(0);
		assertEquals( 1, table.getNumberOfColumns() );
		assertEquals( "1.0", table.get(0, 0));
		assertEquals( "1.1", table.get(0, 1));
		
		table = new TableData( 2, 2 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );

		table.removeColumn(1);
		assertEquals( 1, table.getNumberOfColumns() );
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "0.1", table.get(0, 1));


		table = new TableData( 3, 2 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );

		table.removeColumn(1);
		assertEquals( 2, table.getNumberOfColumns() );
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "0.1", table.get(0, 1));
		assertEquals( "2.0", table.get(1, 0));
		assertEquals( "2.1", table.get(1, 1));
	}
	
	public void testRemoveRow() {
		TableData table;

		table = new TableData( 2, 2 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );

		table.removeRow(0);
		assertEquals( 1, table.getNumberOfRows() );
		assertEquals( "0.1", table.get(0, 0));
		assertEquals( "1.1", table.get(1, 0));
		
		table = new TableData( 2, 2 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );

		table.removeRow(1);
		assertEquals( 1, table.getNumberOfRows() );
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(1, 0));


		table = new TableData( 3, 3 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );
		table.set( 0, 2, "0.2" );
		table.set( 1, 2, "1.2" );
		table.set( 2, 2, "2.2" );

		table.removeRow(1);
		assertEquals( 2, table.getNumberOfRows() );
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(1, 0));
		assertEquals( "2.0", table.get(2, 0));
		assertEquals( "0.2", table.get(0, 1));
		assertEquals( "1.2", table.get(1, 1));
		assertEquals( "2.2", table.get(2, 1));
	}

	
	public void testInsertColumn() {
		TableData table;
		int index;
		
		table = new TableData();
		table.insertColumn(0);
		assertEquals( 1, table.getNumberOfColumns() );
		index = table.addRow();
		assertEquals( 0, index );
		table.set( 0, 0, "hello");
		assertEquals( "hello", table.get(0, 0));
		
		table = new TableData( 3, 3 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );
		table.set( 0, 2, "0.2" );
		table.set( 1, 2, "1.2" );
		table.set( 2, 2, "2.2" );
		
		table.insertColumn(0);
		assertEquals( 4, table.getNumberOfColumns() );
		assertEquals( 3, table.getNumberOfRows() );
		assertEquals( null, table.get(0, 0));
		assertEquals( null, table.get(0, 1));
		assertEquals( null, table.get(0, 2));
		
		assertEquals( "0.0", table.get(1, 0));
		assertEquals( "1.0", table.get(2, 0));
		assertEquals( "2.0", table.get(3, 0));
		assertEquals( "0.1", table.get(1, 1));
		assertEquals( "1.1", table.get(2, 1));
		assertEquals( "2.1", table.get(3, 1));
		assertEquals( "0.2", table.get(1, 2));
		assertEquals( "1.2", table.get(2, 2));
		assertEquals( "2.2", table.get(3, 2));
		
		
		table = new TableData( 3, 3 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );
		table.set( 0, 2, "0.2" );
		table.set( 1, 2, "1.2" );
		table.set( 2, 2, "2.2" );
		
		table.insertColumn(1);
		assertEquals( 4, table.getNumberOfColumns() );
		assertEquals( 3, table.getNumberOfRows() );
		assertEquals( null, table.get(1, 0));
		assertEquals( null, table.get(1, 1));
		assertEquals( null, table.get(1, 2));
		
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(2, 0));
		assertEquals( "2.0", table.get(3, 0));
		assertEquals( "0.1", table.get(0, 1));
		assertEquals( "1.1", table.get(2, 1));
		assertEquals( "2.1", table.get(3, 1));
		assertEquals( "0.2", table.get(0, 2));
		assertEquals( "1.2", table.get(2, 2));
		assertEquals( "2.2", table.get(3, 2));

		table = new TableData( 3, 3 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );
		table.set( 0, 2, "0.2" );
		table.set( 1, 2, "1.2" );
		table.set( 2, 2, "2.2" );
		
		table.insertColumn(2);
		assertEquals( 4, table.getNumberOfColumns() );
		assertEquals( 3, table.getNumberOfRows() );
		assertEquals( null, table.get(2, 0));
		assertEquals( null, table.get(2, 1));
		assertEquals( null, table.get(2, 2));
		
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(1, 0));
		assertEquals( "2.0", table.get(3, 0));
		assertEquals( "0.1", table.get(0, 1));
		assertEquals( "1.1", table.get(1, 1));
		assertEquals( "2.1", table.get(3, 1));
		assertEquals( "0.2", table.get(0, 2));
		assertEquals( "1.2", table.get(1, 2));
		assertEquals( "2.2", table.get(3, 2));
		
		
		table = new TableData( 3, 3 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );
		table.set( 0, 2, "0.2" );
		table.set( 1, 2, "1.2" );
		table.set( 2, 2, "2.2" );
		
		table.insertColumn(3);
		assertEquals( 4, table.getNumberOfColumns() );
		assertEquals( 3, table.getNumberOfRows() );
		assertEquals( null, table.get(3, 0));
		assertEquals( null, table.get(3, 1));
		assertEquals( null, table.get(3, 2));
		
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(1, 0));
		assertEquals( "2.0", table.get(2, 0));
		assertEquals( "0.1", table.get(0, 1));
		assertEquals( "1.1", table.get(1, 1));
		assertEquals( "2.1", table.get(2, 1));
		assertEquals( "0.2", table.get(0, 2));
		assertEquals( "1.2", table.get(1, 2));
		assertEquals( "2.2", table.get(2, 2));
		
	
	}

	
	public void testInsertRow() {
		TableData table;
		int index;
		
		table = new TableData();
		table.insertRow(0);
		assertEquals( 1, table.getNumberOfRows() );
		index = table.addColumn();
		assertEquals( 0, index );
		table.set( 0, 0, "hello");
		assertEquals( "hello", table.get(0, 0));
		
		table = new TableData( 3, 3 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );
		table.set( 0, 2, "0.2" );
		table.set( 1, 2, "1.2" );
		table.set( 2, 2, "2.2" );
		
		table.insertRow(0);
		assertEquals( 3, table.getNumberOfColumns() );
		assertEquals( 4, table.getNumberOfRows() );
		assertEquals( null, table.get(0, 0));
		assertEquals( null, table.get(1, 0));
		assertEquals( null, table.get(2, 0));
		
		assertEquals( "0.0", table.get(0, 1));
		assertEquals( "1.0", table.get(1, 1));
		assertEquals( "2.0", table.get(2, 1));
		assertEquals( "0.1", table.get(0, 2));
		assertEquals( "1.1", table.get(1, 2));
		assertEquals( "2.1", table.get(2, 2));
		assertEquals( "0.2", table.get(0, 3));
		assertEquals( "1.2", table.get(1, 3));
		assertEquals( "2.2", table.get(2, 3));
		
		
		
		
		table = new TableData( 3, 3 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );
		table.set( 0, 2, "0.2" );
		table.set( 1, 2, "1.2" );
		table.set( 2, 2, "2.2" );
		
		table.insertRow(1);
		assertEquals( 3, table.getNumberOfColumns() );
		assertEquals( 4, table.getNumberOfRows() );
		assertEquals( null, table.get(0, 1));
		assertEquals( null, table.get(1, 1));
		assertEquals( null, table.get(2, 1));
		
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(1, 0));
		assertEquals( "2.0", table.get(2, 0));
		assertEquals( "0.1", table.get(0, 2));
		assertEquals( "1.1", table.get(1, 2));
		assertEquals( "2.1", table.get(2, 2));
		assertEquals( "0.2", table.get(0, 3));
		assertEquals( "1.2", table.get(1, 3));
		assertEquals( "2.2", table.get(2, 3));
		
		
		
		
		table = new TableData( 3, 3 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );
		table.set( 0, 2, "0.2" );
		table.set( 1, 2, "1.2" );
		table.set( 2, 2, "2.2" );
		
		table.insertRow(2);
		assertEquals( 3, table.getNumberOfColumns() );
		assertEquals( 4, table.getNumberOfRows() );
		assertEquals( null, table.get(0, 2));
		assertEquals( null, table.get(1, 2));
		assertEquals( null, table.get(2, 2));
		
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(1, 0));
		assertEquals( "2.0", table.get(2, 0));
		assertEquals( "0.1", table.get(0, 1));
		assertEquals( "1.1", table.get(1, 1));
		assertEquals( "2.1", table.get(2, 1));
		assertEquals( "0.2", table.get(0, 3));
		assertEquals( "1.2", table.get(1, 3));
		assertEquals( "2.2", table.get(2, 3));
		

		
		
		
		table = new TableData( 3, 3 );
		table.set( 0, 0, "0.0" );
		table.set( 1, 0, "1.0" );
		table.set( 2, 0, "2.0" );
		table.set( 0, 1, "0.1" );
		table.set( 1, 1, "1.1" );
		table.set( 2, 1, "2.1" );
		table.set( 0, 2, "0.2" );
		table.set( 1, 2, "1.2" );
		table.set( 2, 2, "2.2" );
		
		table.insertRow(3);
		assertEquals( 3, table.getNumberOfColumns() );
		assertEquals( 4, table.getNumberOfRows() );
		assertEquals( null, table.get(0, 3));
		assertEquals( null, table.get(1, 3));
		assertEquals( null, table.get(2, 3));
		
		assertEquals( "0.0", table.get(0, 0));
		assertEquals( "1.0", table.get(1, 0));
		assertEquals( "2.0", table.get(2, 0));
		assertEquals( "0.1", table.get(0, 1));
		assertEquals( "1.1", table.get(1, 1));
		assertEquals( "2.1", table.get(2, 1));
		assertEquals( "0.2", table.get(0, 2));
		assertEquals( "1.2", table.get(1, 2));
		assertEquals( "2.2", table.get(2, 2));
		
		
	
	}
}
