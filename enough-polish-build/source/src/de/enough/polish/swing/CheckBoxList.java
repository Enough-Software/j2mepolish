/*
 * Created on 26-Jan-2006 at 12:49:06.
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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * <p>Used for a list of checkboxes.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CheckBoxList 
extends JPanel
implements ActionListener
{

	private static final long serialVersionUID = 3750438471447335136L;
	private JCheckBox[] checkboxes;
	private ActionListener[] actionListeners;

	/**
	 * Creates a new CheckBoxList
	 * 
	 * @param size the number of checkboxes to be added 
	 */
	public CheckBoxList( int size ) {
		super( new GridLayout( size, 1, 0, 2 ) );
	}
	
	/**
	 * Creates a new CheckBoxList
	 * 
	 * @param checkboxes the checkboxes to be added 
	 */
	public CheckBoxList( JCheckBox[] checkboxes ) {
		super( new GridLayout( checkboxes.length, 1, 0, 2 ) );
		addCheckBoxes(checkboxes);
	}

	public void addCheckBoxes( JCheckBox[] boxes ) {
		this.checkboxes = boxes;
		for (int i = 0; i < boxes.length; i++) {
			JCheckBox box = boxes[i];
			box.addActionListener( this );
			add( box );
		}
	}
	
	/**
	 * Adds an ActionListener to this list.
	 * ActionListener will get the original JCheckBox as the source of the event in actionPerformed( ActionEvent e).
	 * 
	 * @param listener the ActionListener
	 */
	public void addActionListener( ActionListener listener ) {
		if (this.actionListeners == null) {
			this.actionListeners = new ActionListener[]{ listener };
		} else {
			ActionListener[] listeners = new ActionListener[ this.actionListeners.length + 1 ];
			System.arraycopy(this.actionListeners, 0, listeners, 0, this.actionListeners.length );
			listeners[ this.actionListeners.length ] = listener;
			this.actionListeners = listeners;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (this.actionListeners != null) {
			for (int i = 0; i < this.actionListeners.length; i++) {
				ActionListener listener = this.actionListeners[i];
				listener.actionPerformed(e);
			}
		}
	}
	
	public JCheckBox[] getCheckBoxes() {
		return this.checkboxes;
	}
	
	public JCheckBox getCheckBox( int index ) {
		return this.checkboxes[ index ];
	}

}
