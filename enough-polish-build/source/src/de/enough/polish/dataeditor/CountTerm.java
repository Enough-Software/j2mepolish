/*
 * Created on 19-Oct-2004 at 12:01:16.
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

import de.enough.polish.util.StringUtil;

/**
 * <p>Represents a simple term for calculating the number of entries of a specific data entry.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CountTerm {
	
	public static final int OPERATION_ADD = 1; 
	public static final int OPERATION_SUBTRACT = 2; 
	public static final int OPERATION_MULTIPLY = 3; 
	public static final int OPERATION_DEVIDE = 4; 
	
	private final int operation;
	private final DataEntry[] operants;

	/**
	 * Creates a new term.
	 * 
	 * @param operation the operation, either OPERATION_ADD, OPERATION_SUBTRACT, OPERATION_MULTIPLY or OPERATION_DEVIDE 
	 * @param operants the entries which are used in this calculation
	 */
	public CountTerm( int operation, DataEntry[] operants ) {
		super();
		this.operation = operation;
		this.operants = operants;
		if (operants == null || operants.length == 0) {
			throw new IllegalArgumentException( "The operants of a count term needs to be defined, not null or empty.");
		}
	}
	
	public int calculateCount() {
		switch ( this.operation ) {
			case OPERATION_ADD:
				int result = 0;
				for (int i = 0; i < this.operants.length; i++ ) {
					result += this.operants[i].getDataAsInt();
				}
				return result;
			case OPERATION_SUBTRACT:
				result = this.operants[0].getCount();
				for (int i = 1; i < this.operants.length; i++ ) {
					result -= this.operants[i].getDataAsInt();
				}
				return result;
			case OPERATION_MULTIPLY:
				result = 1;
				for (int i = 0; i < this.operants.length; i++ ) {
					result *= this.operants[i].getDataAsInt();
				}
				return result;
			case OPERATION_DEVIDE:
				result = this.operants[0].getCount();
				for (int i = 1; i < this.operants.length; i++ ) {
					result /= this.operants[i].getDataAsInt();
				}
				return result;
			default:
				throw new IllegalStateException("The operation [" + this.operation + "] is not supported.");
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < this.operants.length; i++ ) {
			buffer.append( this.operants[i].getName() );
			if (i != this.operants.length -1 ) {	
				switch ( this.operation ) {
					case OPERATION_ADD:
						buffer.append(" + "); break;
					case OPERATION_SUBTRACT:
						buffer.append(" - "); break;
					case OPERATION_MULTIPLY:
						buffer.append(" * "); break;
					case OPERATION_DEVIDE:
						buffer.append(" / "); break;
					default:
						throw new IllegalStateException("The operation [" + this.operation + "] is not supported.");
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * @return the string representation
	 */
	public String toCodeString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < this.operants.length; i++ ) {
			buffer.append( "this.").append( this.operants[i].getName() );
			if (i != this.operants.length -1 ) {	
				switch ( this.operation ) {
					case OPERATION_ADD:
						buffer.append(" + "); break;
					case OPERATION_SUBTRACT:
						buffer.append(" - "); break;
					case OPERATION_MULTIPLY:
						buffer.append(" * "); break;
					case OPERATION_DEVIDE:
						buffer.append(" / "); break;
					default:
						throw new IllegalStateException("The operation [" + this.operation + "] is not supported.");
				}
			}
		}
		return buffer.toString();
	}
	
	/**
	 * @param term the term string
	 * @param manager the data manager
	 * @return the term object
	 */
	public static CountTerm createTerm(String term, DataManager manager) {
		char devider = '+';
		boolean operatorFound = true;
		int operation = OPERATION_ADD;
		if (term.indexOf('+') != -1) {
			devider = '+';
			operation = OPERATION_ADD;
		} else if (term.indexOf('-') != -1) {
			devider = '-';
			operation = OPERATION_SUBTRACT;
		} else if (term.indexOf('*') != -1) {
			devider = '*';
			operation = OPERATION_MULTIPLY;
		} else if (term.indexOf('/') != -1) {
			devider = '/';
			operation = OPERATION_DEVIDE;
		} else {
			operatorFound = false;
		}
		String[] entryNames;
		if (operatorFound) {
			entryNames = StringUtil.splitAndTrim( term, devider );
		} else {
			entryNames = new String[]{ term.trim() };
		}
		DataEntry[] dataEntries = new DataEntry[ entryNames.length ];
		for (int i = 0; i < dataEntries.length; i++) {
			String name = entryNames[ i ];
			DataEntry entry = manager.getDataEntry( name );
			if (entry == null) {
				throw new IllegalArgumentException("Unable to parse count term [" + term + "]: the entry [" + name + "] is not known.");
			}
			dataEntries[i] = entry;
		}
		return new CountTerm( operation, dataEntries );
	}

	/**
	 * @return the data
	 */
//	public DataEntry[] getOperands() {
	public DataEntry[] getOperants() {
		return this.operants;
	}

	/**
	 * @return the number of columns for this term
	 */
	public int getColumns() {
		if ((this.operation != OPERATION_MULTIPLY) || (this.operants.length != 2)) {
			return 1;
		}
		DataEntry columnsEntry = this.operants[0];
		return columnsEntry.getDataAsInt();
	}

	/**
	 * @return the number of rows for this term
	 */
	public int getRows() {
		if ((this.operation != OPERATION_MULTIPLY) || (this.operants.length != 2)) {
			return calculateCount();
		}
		DataEntry rowsEntry = this.operants[1];
		return rowsEntry.getDataAsInt();
	}

}
