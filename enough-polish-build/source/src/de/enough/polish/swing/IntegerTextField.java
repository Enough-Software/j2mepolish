/*
 * Created on 06-Mar-2005 at 20:59:07.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.swing;

import java.text.NumberFormat;

import javax.swing.event.ChangeListener;


/**
 * <p>An active textfield that only accepts integer input.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        06-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class IntegerTextField extends ActiveTextField {


	private static final long serialVersionUID = 7006859670363152211L;

	/**
	 * @param listener
	 */
	public IntegerTextField( ChangeListener listener) {
		super(NumberFormat.getInstance(), listener);
	}

	/**
	 * @param listener
	 * @param columns
	 */
	public IntegerTextField( ChangeListener listener, int columns ) {
		super(NumberFormat.getInstance(), listener);
		setColumns(columns);
	}

	/**
	 * @return the integer value of this field. When a NumberFormatException is thrown, 0 will be returned
	 */
	public int getIntValue() {
		try {
			return Integer.parseInt( getText() );
		} catch (NumberFormatException e) {
			System.err.println("Warning: unable to parse integer [" + getText() + "]: " + e.toString()  );
			return 0;
		}
	}

	/**
	 * @param value
	 */
	public void setIntValue( int value ) {
		setText( "" + value );
	}

}
