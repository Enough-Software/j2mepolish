/*
 * Created on 24-Nov-2003 at 14:48:58
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
package de.enough.webprocessor.util;

/**
 * <p>Manages a String-array.</p>
 * <p>
 * Example:
 * <code>
 * String[] lines = readFile();
 * StringList list = new StringList( lines );
 * while (list.next()) {
 * 	String line = list.getCurrent();
 * 	// process line...
 * }
 * </code>
 * </p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        14-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class StringList {
	private String[] lines;
	private int startIndex = 0;
	private int currentIndex = -1;
	private int numberOfInsertedLines;
	
	/**
	 * Creates a new StringList.
	 * 
	 * @param lines the String array which should be managed by this list.
	 */
	public StringList( String[] lines ) {
		this.lines = lines;
	}
	
	/**
	 * Retrieves the current index of this list.
	 * 
	 * @return the current index, the first index has the value 0.
	 */
	public int getCurrentIndex() {
		return this.currentIndex;
	}
	
	/**
	 * Retrieves the current line.
	 * next() has to be called at least one time before getCurrent() can be called.
	 * 
	 * @return the current line.
	 * @see #next()
	 * @throws ArrayIndexOutOfBoundsException when next() has never been called before.
	 */
	public String getCurrent() {
		if (this.currentIndex == -1) {
			throw new ArrayIndexOutOfBoundsException("StringList: next() has to be called at least once before getCurrent() can be called.");
		}
		return this.lines[ this.currentIndex ];
	}
	
	/**
	 * Updates the current line.
	 *  
	 * @param value the new value of the current line.
	 * @see #getCurrent()
	 */
	public void setCurrent( String value ) {
		this.lines[ this.currentIndex ] = value;
	}
	
	/**
	 * Inserts the given string after the current line.
	 * 
	 * @param value the string which should be inserted.
	 */
	public void insert( String value ) {
		insert( new String[]{ value  } );
	}
	
	/**
	 * Inserts the given string-array after the current line.
	 * 
	 * @param values the string array which should be inserted.
	 */
	public void insert( String[] values ) {
		//System.out.println("inserting " + values.length + " lines at " + this.currentIndex);
		this.numberOfInsertedLines += values.length;
		String[] newLines = new String[ this.lines.length + values.length ];
		// copy the lines up to the current position:
		System.arraycopy( this.lines, 0, newLines, 0, this.currentIndex+1 );
		// insert the new lines:
		System.arraycopy( values, 0, newLines, this.currentIndex+1, values.length );
		// append the rest of after the current position:
		System.arraycopy( this.lines, this.currentIndex+1, newLines, this.currentIndex + 1 + values.length, this.lines.length - (this.currentIndex+1) );
		// set the new lines:
		this.lines = newLines;
	}
	
	/**
	 * Retrieves the number of lines which have been inserted.
	 * 
	 * @return the number of lines which have been inserted so far.
	 */
	public int getNumberOfInsertedLines() {
		return this.numberOfInsertedLines;
	}

	/**
	 * Sets the value of the specified array-index.
	 *  
	 * @param index the array-index of the line 
	 * @param value the value of the line
	 * @throws ArrayIndexOutOfBoundsException when the 0 > index >= lines.length 
	 */
	public void set( int index, String value ) {
		this.lines[index] = value;
	}
	
	/**
	 * Shifts the current index one step further.
	 * 
	 * @return true when there is a next line.
	 */
	public boolean next() {
		if (this.currentIndex < this.lines.length - 1) {
			this.currentIndex++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determines if there is a line after the current one.
	 * @return true when there is a line after the current one.
	 */
	public boolean hasNext() {
		if (this.currentIndex < this.lines.length - 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes the current line from this list.
	 * 
	 * @return the current line
	 */
	public String removeCurrent() {
		if (this.currentIndex == -1) {
			throw new ArrayIndexOutOfBoundsException("StringList: next() has to be called at least once before getCurrent() can be called.");
		}
		String line = this.lines[ this.currentIndex ];
		for (int i = this.currentIndex; i < this.lines.length-1; i++) {
			this.lines[i] = this.lines[i+1];
		}
		String[] newLines = new String[ this.lines.length -1 ];
		System.arraycopy( this.lines, 0, newLines, 0, newLines.length );
		this.lines = newLines;
		//System.out.println("removing line (" + line + ")");
		if (this.currentIndex >= newLines.length) {
			this.currentIndex = newLines.length -1;
		}
		return line;
	}

	/**
	 * Returns to the previous line.
	 * 
	 * @return true when there is a previous line
	 */
	public boolean prev() {
		if (this.currentIndex < 0) {
			return false;
		} else {
			this.currentIndex--;
			return true;
		}
	}
	
	/**
	 * Sets the new index from where lines can be read.
	 * The current index will also be set on one less than the new start index.
	 * So after calling next() the getCurrent()-method will return lines[startIndex] 
	 * 
	 * @param startIndex the new start index, 0 is the first line.
	 */
	public void setStartIndex( int startIndex ) {
		this.startIndex = startIndex;
		this.currentIndex = startIndex - 1;
	}
	
	/**
	 * Resets the current index to the start index of this list.
	 * The getCurrent()-method will return lines[startIndex] after calling next(). 
	 */
	public void reset() {
		this.currentIndex = this.startIndex - 1;
	}
	
	
	/**
	 * Retrieves the internal array of this StringList.
	 * 
	 * @return the internal array of this StringList.
	 */
	public String[] getArray(){
		return this.lines;
	}

	/**
	 * Sets the current index.
	 * 
	 * @param index The new index.
	 * @see #getCurrentIndex()
	 */
	public void setCurrentIndex(int index) {
		this.currentIndex = index;
	}

	/**
	 * Retrieves the number of lines in this list. 
	 * 
	 * @return the length of this list
	 */
	public int length() {
		return this.lines.length;
	}	

}