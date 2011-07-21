//#condition  polish.midp || polish.usePolishGui
/*
 * Created on Sep 8, 2010 at 10:25:17 PM.
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

/**
 * <p>Contains wrapped text and additional meta information about it.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class WrappedText implements Externalizable {
	private static final int VERSION = 100;
	private String[] lines;
	private final IdentityArrayList linesList;
	private int[] lineWidths;
	private final IntList lineWidthsList;
	private int maxLineWidth;
	
	/**
	 * Creates a new wrapped text object
	 */
	public WrappedText() {
		this.linesList = new IdentityArrayList();
		this.lineWidthsList = new IntList();
	}
	
	/**
	 * Creates a new wrapped text object by copying values from the given one
	 * @param original the wrapped text that should be copied
	 */
	public WrappedText(WrappedText original) {
		int size = original.size();
		this.linesList = new IdentityArrayList(size);
		this.lineWidthsList = new IntList(size);
		addAll( original );
	}

	
	/**
	 * Adds all content from the given wrapped text
	 * @param original the wrapped text that should be copied into this one
	 */
	public void addAll(WrappedText original) {
		Object[] originalLines = original.getLinesInternalArray();
		int[] originalWidths = original.getLineWidthsInternalArray();
		int size = original.size();
		for (int i=0; i<size; i++) {
			this.linesList.add(originalLines[i]);
			this.lineWidthsList.add(originalWidths[i]);
		}
		if (original.maxLineWidth > this.maxLineWidth) {
			this.maxLineWidth = original.maxLineWidth;
		}
	}

	/**
	 * Adds a wrapped line
	 * @param line the line
	 * @param width the width of the line
	 */
	public void addLine( String line, int width ) {
		this.lines = null;
		this.lineWidths = null;
		this.linesList.add(line);
		this.lineWidthsList.add(width);
		if (width > this.maxLineWidth) {
			this.maxLineWidth = width;
		}
	}
	
	/**
	 * Sets a line without changing it's width
	 * @param index index of the line
	 * @param line the line
	 */
	public void setLine( int index, String line ) {
		this.lines = null;
		this.lineWidths = null;
		this.linesList.set(index, line);
	}
	
	/**
	 * Sets a line
	 * @param index index of the line
	 * @param line the line
	 * @param width the width of the line
	 */
	public void setLine( int index, String line, int width ) {
		this.lines = null;
		this.lineWidths = null;
		this.linesList.set(index, line);
		int prevWidth = this.lineWidthsList.set(index, width);
		if (width > this.maxLineWidth) {
			this.maxLineWidth = width;
		} else if (prevWidth == this.maxLineWidth && prevWidth > width) {
			int[] widths = this.lineWidthsList.getInternalArray();
			int size = this.lineWidthsList.size();
			int max = 0;
			for (int i = 0; i < size; i++) {
				width = widths[i];
				if (width > max) {
					max = width;
				}
			}
			this.maxLineWidth = width;
		}
	}


	
	/**
	 * Clears this list.
	 */
	public void clear() {
		this.lines = null;
		this.lineWidths = null;
		this.linesList.clear();
		this.lineWidthsList.clear();
		this.maxLineWidth = 0;
	}
	
	/**
	 * Retrieves the lines of this wrapped text
	 * @return an array of the lines, can be empty but not null
	 */
	public String[] getLines() {
		if (this.lines == null) {
			this.lines = (String[]) this.linesList.toArray(new String[ this.linesList.size() ]);
		}
		return this.lines;
	}
	
	/**
	 * Retrieves the lines of this wrapped text
	 * @return an array of the lines which may include null values
	 * @see #size()
	 */
	public Object[] getLinesInternalArray() {
		return this.linesList.getInternalArray();
	}
	
	/**
	 * Retrieves the widths of the lines of this wrapped text
	 * @return an array of the line-widths, can be empty but not null
	 */
	public int[] getLineWidths() {
		if (this.lineWidths == null) {
			this.lineWidths = this.lineWidthsList.toArray();
		}
		return this.lineWidths;
	}
	
	/**
	 * Retrieves the widths of lines of this wrapped text.
	 * @return an array of the lines which may include unused values
	 * @see #size()
	 */
	public int[] getLineWidthsInternalArray() {
		return this.lineWidthsList.getInternalArray();
	}
	
	/**
	 * Retrieves the number of text lines in this wrapped text
	 * @return the number of lines
	 */
	public int size() {
		return this.linesList.size();
	}
	
	/**
	 * Retrieves the line at the specified index
	 * @param index the index, [0..size()[
	 * @return the line at the specified index
	 */
	public String getLine( int index ) {
		return (String) this.linesList.get(index);
	}
	
	/**
	 * Retrieves the line width at the specified index
	 * @param index the index, [0..size()[
	 * @return the line width at the specified index
	 */
	public int getLineWidth( int index ) {
		return this.lineWidthsList.get(index);
	}
	
	/**
	 * Retrieves the maximum line width
	 * @return the width of widest line
	 */
	public int getMaxLineWidth() {
		return this.maxLineWidth;
	}
	
	/**
	 * Sets a the width of the widest line within this wrapped text.
	 * @param width the width
	 */
	public void setMaxLineWidth( int width ) {
		this.maxLineWidth = width;
	}

	/**
	 * Removes the specified line
	 * @param index the index of the line
	 */
	public void removeLine(int index) {
		this.lines = null;
		this.lineWidths = null;
		this.linesList.remove(index);
		int width = this.lineWidthsList.removeElementAt(index);
		if (width == this.maxLineWidth) {
			int[] widths = this.lineWidthsList.getInternalArray();
			int size = this.lineWidthsList.size();
			int max = 0;
			for (int i = 0; i < size; i++) {
				width = widths[i];
				if (width > max) {
					max = width;
				}
			}
			this.maxLineWidth = width;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(VERSION);
		out.writeInt( this.maxLineWidth );
		int size = size();
		out.writeInt(size);
		for (int i=0; i<size; i++) {
			out.writeInt( this.lineWidthsList.get(i) );
			out.writeUTF( (String)this.linesList.get(i) );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version > VERSION) {
			throw new IOException("for version " + version );
		}
		int max = in.readInt();
		int size = in.readInt();
		for (int i=0; i<size; i++) {
			int width = in.readInt();
			String line = in.readUTF();
			addLine( line, width );
		}
		this.maxLineWidth = max;		
	}

}
