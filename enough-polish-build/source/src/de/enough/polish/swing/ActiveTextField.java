/*
 * Created on 06-Mar-2005 at 20:48:04.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Format;

import javax.swing.JFormattedTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>A textfield that actively notifies the registered change listener about any changes.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        06-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ActiveTextField 
extends JFormattedTextField
implements CaretListener, ActionListener //, FocusListener
{
	private static final long serialVersionUID = 3877268458402335970L;

	private final ChangeListener changeListener;
	private String originalValue;

	/**
	 * @param listener
	 * 
	 */
	public ActiveTextField(ChangeListener listener) {
		super();
		this.changeListener = listener;
		registerListeners();
	}

	/**
	 * @param value
	 * @param listener
	 */
	public ActiveTextField(Object value, ChangeListener listener ) {
		super(value);
		if (value != null) {
			this.originalValue = value.toString();
		}
		this.changeListener = listener;
		registerListeners();
	}

	/**
	 * @param format
	 * @param listener
	 */
	public ActiveTextField(Format format, ChangeListener listener) {
		super(format);
		this.changeListener = listener;
		registerListeners();
	}

	/**
	 * @param formatter
	 * @param listener
	 */
	public ActiveTextField(AbstractFormatter formatter, ChangeListener listener) {
		super(formatter);
		this.changeListener = listener;
		registerListeners();
	}

	/**
	 * 
	 */
	private void registerListeners() {
		addCaretListener( this );
		//addFocusListener( this );
		addActionListener( this );
		
	}
	
	/**
	 * Sets the provided action listener.
	 * ActiveTextField provides its own action listener that is removed
	 * when this method is used.
	 * 
	 * @param listener the action listener
	 */
	public void setActionListener( ActionListener listener ) {
		removeActionListener( this );
		addActionListener( listener );
	}
	

	public void setValue(Object value) {
		super.setValue(value);
		if (value != null) {
			this.originalValue = value.toString();
		} else {
			this.originalValue = null;
		}
	}
	
	public void setText(String text) {
		this.originalValue = text;
		super.setText(text);
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
	 */
	public void caretUpdate(CaretEvent e) {
		//System.out.println("CaretUpdateEvent: " + e.toString() );
		fireChangeEvent();
	}


	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	public void focusGained(FocusEvent e) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 *
	public void focusLost(FocusEvent e) {
		System.out.println("FocusLostEvent: " + e.toString() );
		fireChangeEvent();
	}
	 */

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		//System.out.println("ActionPerformedEvent: " + e.toString() );
		fireChangeEvent();
	}

	private void fireChangeEvent() {
		String newValue = getText();
		if ( newValue == null
				|| newValue.length() == 0
				|| newValue.equals( this.originalValue ) ) 
		{
			//System.out.println("Active: NO fireChange: original=" + this.originalValue + ",  new=" + newValue );
			return;
		} else {
			//System.out.println("\nActive: fireChange: original=" + this.originalValue + ",  new=" + newValue  + "\n");
			this.originalValue = newValue;
			this.changeListener.stateChanged( new ChangeEvent( this ) );
		}
	}
	

}
