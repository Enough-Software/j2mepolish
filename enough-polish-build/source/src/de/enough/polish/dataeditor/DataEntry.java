/*
 * Created on 18-Oct-2004 at 23:15:02.
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
package de.enough.polish.dataeditor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom.Element;

/**
 * <p>Represents a data-field within a binary file.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        18-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataEntry {
	
	private String name;
	private DataType type;
	private int count;
	private CountTerm countTerm;
	private Object[] data;
	private final ArrayList dependentEntries;

	/**
	 * Creates a new entry in a binary file.
	 * 
	 * @param name the name of the entry
	 * @param type the data type
	 * 
	 */
	public DataEntry( String name, DataType type ) {
		super();
		this.name = name;
		this.dependentEntries = new ArrayList();
		setType( type );
		setCount( 1 );
	}

	/**
	 * @param entryElement
	 * @param manager
	 */
	public DataEntry(Element entryElement, DataManager manager) {
		this.name = entryElement.getAttributeValue("name");
		this.dependentEntries = new ArrayList();
		DataType dataType = manager.getDataType( entryElement.getAttributeValue("type"));
		setType( dataType );
		setCount( entryElement.getAttributeValue("count"), manager );
	}

	public String[] getDataAsString() {
		String[] result = new String[ this.count ];
		for (int i = 0; i < this.count; i++) {
			result[i] = this.type.toString( this.data[i] );
		}
		return result;
	}
	
	protected void setDataAsString( String dataStr ) {
		if (this.count != 1) {
			throw new IllegalStateException("Cannot set data as String for a DataEntry with a count different than one (" + this.count + ")" );
		}
		setDataAsString( new String[]{ dataStr } );
	}

	protected void setDataAsString( String[] dataStr ) {
		if (dataStr.length != this.count ) {
			throw new IllegalArgumentException("The to-be-set data has a different count [" + dataStr.length + "] than the allowed number [" + this.count + "].");
		}
		for (int i = 0; i < this.count; i++) {
			this.data[i] = this.type.parseDataString( dataStr[i] ); 
		}
		notifyDependentEntries();
	}

	protected void setCount( int count ) {
		if (count == this.count) {
			// ignore the setting of the same count:
			return;
		}
		this.count = count;
		Object[] newData = new Object[ count ];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = this.type.getDefaultValue();
		}
		if (this.data == null || this.data.length == 0) {
			this.data = newData;
			return;
		} else {
			int minLength = Math.min( newData.length, this.data.length );
			System.arraycopy( this.data, 0, newData, 0, minLength );
			this.data = newData;
			this.countTerm = null;
		}
	}
	
	protected void setCount( CountTerm term ) {
		if (this.countTerm != null) {
			DataEntry[] parents = this.countTerm.getOperants();
			for (int i = 0; i < parents.length; i++) {
				DataEntry entry = parents[i];
				entry.dependentEntries.remove( this );
			}
		}
		setCount( term.calculateCount() );
		this.countTerm = term;
		DataEntry[] parents = term.getOperants();
		for (int i = 0; i < parents.length; i++) {
			DataEntry entry = parents[i];
			entry.dependentEntries.add( this );
		}
	}

	/**
	 * @param value either a number or a count term
	 * @param manager
	 */
	protected void setCount(String value, DataManager manager ) {
		try {
			int intValue = Integer.parseInt( value );
			setCount( intValue );
		} catch (NumberFormatException e) {
			// okay, this is more likely a complex count term:
			CountTerm term = CountTerm.createTerm(value, manager );
			setCount( term );
		}
	}
	
	protected void updateCount() {
		if (this.countTerm != null) {
			CountTerm term = this.countTerm;
			setCount( this.countTerm.calculateCount() );
			this.countTerm = term;
		}
	}
		
	/**
	 * Loads the data for this entry.
	 * 
	 * @param in the input stream for reading data
	 * @throws IOException when the data could not be loaded
	 */
	protected void loadData( DataInputStream in ) 
	throws IOException 
	{
		for (int i = 0; i < this.count; i++) {
			this.data[i] = this.type.loadData( in );
		}
		notifyDependentEntries();
	}
	
	protected void setName( String name ) {
		this.name = name;
	}
	
	protected void setType( DataType type ) {
		this.type = type;
		this.data = new Object[ this.count ];
		for (int i = 0; i < this.count; i++) {
			this.data[i] = type.getDefaultValue();
		}
	}
	
	public DataType getType() {
		return this.type;
	}
	
	public int getCount() {
		if (this.countTerm != null ) {
			return this.countTerm.calculateCount();
		} else {
			return this.count;
		}
	}
	
	public int getColumns() {
		if (this.countTerm == null) {
			return 1;
		} else {
			return this.countTerm.getColumns();
		}
	}

	public int getRows() {
		if (this.countTerm == null) {
			return this.count;
		} else {
			return this.countTerm.getRows();
		}
	}

	public String getCountAsString() {
		if (this.countTerm != null) {
			return this.countTerm.toString();
		} else {
			return "" + this.count;
		}
	}
	
	public String getCountAsCodeString() {
		if (this.countTerm != null) {
			return this.countTerm.toCodeString();
		} else {
			return "" + this.count;
		}
	}
	
	public Object[] getData() {
		return this.data;
	}
	
	public int getDataAsInt() {
		return this.type.getIntRepresentation( this.data[0] );
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDataTypeName() {
		return this.type.getName();
	}
	
	private void notifyDependentEntries() {
		DataEntry[] entries = (DataEntry[]) this.dependentEntries.toArray( new DataEntry[ this.dependentEntries.size() ] );
		for (int i = 0; i < entries.length; i++) {
			DataEntry entry = entries[i];
			entry.updateCount();
		}
	}

	/**
	 * Retrieves the XML representation for this <code>DataEntry</code>. This is useful
	 * e.g. for saving it into a file.
	 * 
	 * @return the XML representation
	 */
	public String getXmlRepresentation() {
		StringBuffer  buffer = new StringBuffer();
		buffer.append( "\t<entry name=\"" )
			.append( this.name )
			.append( "\" type=\"" )
			.append( this.type.getName() )
			.append( "\" count=\"" );
		if (this.countTerm != null) {
			buffer.append( this.countTerm.toString() );
		} else {
			buffer.append( this.count );
		}
		buffer.append("\" />");
		return buffer.toString();
	}	
	
	public void addInstanceDeclaration( StringBuffer buffer ) {
		this.type.addInstanceDeclaration(getCountAsString(), this.name, buffer);
	}
	
	public void addCode( StringBuffer buffer ) {
		this.type.addCode(getCountAsCodeString(), this.name, buffer);
	}

	public void saveData(DataOutputStream out) 
	throws IOException
	{
		for (int i = 0; i < this.count; i++) {
			this.type.saveData( this.data[i], out );
		}
	}

	/**
	 * Sets the data directly.
	 * 
	 * @param data the new data
	 */
	public void setData(Object data) {
		if (this.count != 1) {
			throw new IllegalStateException("The entry " + this.name + " does not allow a single data-value to be set. Adjust the count of this entry to 1.");
		}
		this.data = new Object[]{ data };
	}
}
