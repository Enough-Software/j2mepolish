/*
 * Created on 02-Nov-2004 at 01:33:04.
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

import javax.swing.JButton;

import de.enough.polish.dataeditor.DataEntry;

/**
 * <p>A button which has a corresponding data entry.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataEntryButton extends JButton {

	private static final long serialVersionUID = -1523409336974295169L;

	private DataEntry entry;
	
	/**
	 * @param entry
	 */
	public DataEntryButton( DataEntry entry ) {
		super();
		setEntry( entry );
	}

	/**
	 * 
	 */
	public DataEntryButton() {
		super();
	}
	
	public void setEntry( DataEntry entry ) {
		this.entry = entry;
		/*
		String[] data = entry.getDataAsString();
		setText( data );
		*/
	}

	public void setText( String[] data ) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			buffer.append('[') 
				.append( data[i] )
				.append("] ");
		}
		setText( buffer.toString() );
	}
	
	public DataEntry getEntry() {
		return this.entry;
	}
	
	public String toString() {
		return getText();
	}

}
