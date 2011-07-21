/*
 * Created on 26-Jan-2006 at 13:37:04.
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
package de.enough.polish.ide.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;

import de.enough.polish.devices.PolishComponent;
import de.enough.polish.swing.CheckBoxList;

class PolishComponentCheckBoxList 
extends CheckBoxList
implements ActionListener
{
    
	private static final long serialVersionUID = -7079257964561401013L;
	
	private JCheckBox[] checkBoxes;
	private JCheckBox selectAllEntries;
	private PolishComponent[] entries;
	private PolishComponentSelectionListener selectionListener;

	private int startIndexForComponents;

	public PolishComponentCheckBoxList( PolishComponent[] entries ) {
		this(entries, true );
	}
	
	public PolishComponentCheckBoxList( PolishComponent[] entries, boolean addAllCheckBox ) {
		super( getEntriesNumber( entries, addAllCheckBox) );
		this.entries = entries;
		this.checkBoxes = new JCheckBox[ getEntriesNumber( entries, addAllCheckBox) ];
		int start = 0;
		if (addAllCheckBox) {
			this.selectAllEntries = new JCheckBox( "All", true );
			this.selectAllEntries.addActionListener( this );
			this.checkBoxes[0] = this.selectAllEntries;
			start = 1;
		}
		this.startIndexForComponents = start;
		Color backgroundColor = Color.WHITE; //getBackground();
		for (int i = 0; i < entries.length; i++) {
			PolishComponent entry = entries[i];				
			PolishComponentCheckBox box = new PolishComponentCheckBox( entry  );
			box.setBackground( backgroundColor );
			this.checkBoxes[i+start] = box;
		}
		addCheckBoxes(this.checkBoxes);
		super.addActionListener( this );
	}

	private static int getEntriesNumber(PolishComponent[] entries, boolean addAllCheckBox) {
		if (addAllCheckBox) {
			return entries.length + 1;
		} else {
			return entries.length;
		}
	}

	public void actionPerformed(ActionEvent e) {
		//System.out.println( e );
		JCheckBox source = (JCheckBox) e.getSource();
		if (this.selectAllEntries != null) {
			if ( source == this.selectAllEntries) {
				if (source.isSelected()) {
					for (int i = 1; i < this.checkBoxes.length; i++) {
						this.checkBoxes[i].setSelected(false);
					}
				} else {
					source.setSelected( true );
				}
			} else if (source.isSelected()){
				this.selectAllEntries.setSelected(false);
			} else {
				// check if the last configuration has been de-selected:
				boolean atLeastOneElementIsSelected = false;
				for (int i = this.startIndexForComponents; i < this.checkBoxes.length; i++) {
					atLeastOneElementIsSelected |= this.checkBoxes[i].isSelected();
				}
				if (!atLeastOneElementIsSelected) {
					this.selectAllEntries.setSelected(true);
				}
			}
		}
		if (this.selectionListener != null) {
			this.selectionListener.notifySelection( new PolishComponentSelectionEvent( this, getSelectedComponents(), source ) );
		}
	}
	
	public void setSelectionListener( PolishComponentSelectionListener listener ) {
		this.selectionListener = listener;
	}

	/**
	 * Retrieves the selected polish components.
	 * 
	 * @return null when the "ALL" checkbox is selected or an array of components that are selected
	 */
	public PolishComponent[] getSelectedComponents() {
		if (this.selectAllEntries != null && this.selectAllEntries.isSelected()) {
			return null;
		}
		ArrayList list = new ArrayList();
		for (int i = this.startIndexForComponents; i < this.checkBoxes.length; i++) {
			if (this.checkBoxes[i].isSelected()) {
				list.add( this.entries[ i-this.startIndexForComponents ]);
			}
		}
		if (list.size() == 0) {
			return null;
		}
		return (PolishComponent[]) list.toArray( new PolishComponent[ list.size() ]);
	}

}