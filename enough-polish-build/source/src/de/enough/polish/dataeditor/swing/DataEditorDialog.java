/*
 * Created on 02-Nov-2004 at 01:07:50.
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import de.enough.polish.dataeditor.DataEntry;

/**
 * <p>Allows the editing of data with count greater than 1.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataEditorDialog 
extends JDialog
implements ActionListener
{
	private static final long serialVersionUID = 2383600165182958744L;

	private final JButton okButton;
	private final JButton cancelButton;
	private final JTextField[] textFields;
	private final String[] originalData;
	private boolean okPressed;



	/**
	 * @param owner
	 * @param title
	 * @param entry
	 * @throws java.awt.HeadlessException
	 */
	public DataEditorDialog(Frame owner, String title, DataEntry entry) 
	throws HeadlessException 
	{
		super(owner, title, true );
		
		this.okButton = new JButton( "OK");
		this.okButton.setMnemonic('O');
		this.okButton.addActionListener( this );
		this.cancelButton = new JButton( "Cancel");
		this.cancelButton.setMnemonic('C');
		this.cancelButton.addActionListener( this );
		
		JPanel buttonPanel = new JPanel( new GridLayout(1, 2));
		buttonPanel.add( this.okButton );
		buttonPanel.add( this.cancelButton );
				
		int cols = entry.getColumns() + 1;
		int rows = entry.getRows();
		int firstRow = 0;
		if (cols > 2) {
			rows += 1;
			firstRow = 1;
		}
		JPanel editorPanel = new JPanel( new GridLayout( rows, cols) );
		if (cols > 2) {
			editorPanel.add( new JLabel( "" ) );
			// add columns-header:
			for ( int col = 1; col < cols; col++ ) {
				editorPanel.add( new JLabel( " " + col ) );
			}
		}
		int index = 0;
		String[] data = entry.getDataAsString();
		this.textFields = new JTextField[ data.length ];
		this.originalData = data;
		for (int row = firstRow; row < rows; row++ ) {
			editorPanel.add( new JLabel( " " + (firstRow == 1 ? row : (row+1)) ) );
			for ( int col = 1; col < cols; col++ ) {
				JTextField field = new JTextField( data[index] ); 
				editorPanel.add( field );
				this.textFields[ index ] = field;
				index++;
			}
		}
		
		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
		contentPane.add( new JScrollPane( editorPanel ), BorderLayout.CENTER );
		contentPane.add( buttonPanel, BorderLayout.SOUTH );
		int width = cols * 40;
		if (width < 250) {
			width = 250;
		}
		int height = rows * 35;
		if (height < 100) {
			height = 100;
		}
		setSize( width, height);
		//pack();
	}



	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		this.okPressed = e.getSource() == this.okButton;
		setVisible( false );
	}
	
	public boolean okPressed() {
		return this.okPressed;
	}
	
	public String[] getData() {
		if (!this.okPressed) {
			return this.originalData;
		} else {
			String[] data = new String[ this.textFields.length ];
			for (int i = 0; i < data.length; i++) {
				data[i] = this.textFields[i].getText();
			}
			return data;
		}
	}

	

}
