/*
 * Created on 22-Oct-2004 at 20:10:45.
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

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;

import de.enough.polish.dataeditor.DataManager;
import de.enough.polish.dataeditor.DataType;

/**
 * <p>Provides a dialog for entering a new type-definition.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        22-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CreateTypeDialog 
extends JDialog
implements ActionListener
{
	private static final long serialVersionUID = 1366169570854644586L;

	private final CreateTypeTableModel tableModel;
	private final JTable table;
	private final JTextField nameTextField;
	private final JButton addTypeButton = new JButton( "Add Subtype");
	private final JButton deleteTypeButton = new JButton( "Remove Subtype" );
	private final JButton okButton = new JButton( "OK" );
	private final JButton cancelButton = new JButton( "Cancel" );
	private boolean okPressed;

	/**
	 * Creates a new dialog
	 * @param owner the owner of this dialog, usually SwingDataEditor
	 * @param title the title
	 * @param manager the manager of already defined types and entries
	 * @throws java.awt.HeadlessException
	 */
	public CreateTypeDialog(Frame owner, String title, DataManager manager ) throws HeadlessException {
		super(owner, title, true );
		// create a table which lists all the subtypes:
		this.tableModel =  new CreateTypeTableModel( manager );
		this.table = new JTable( this.tableModel );
		TableColumnModel colModel = this.table.getColumnModel();
		// set combo-box for types:
		JComboBox comboBox = new JComboBox();
		DataType[] knownTypes = manager.getDataTypes();
		for (int i = 0; i < knownTypes.length; i++) {
			DataType type = knownTypes[i];
			comboBox.addItem( type.getName() + " (" + type.getNumberOfBytes() + ")" );
		}
		colModel.getColumn( 1 ).setCellEditor(new DefaultCellEditor(comboBox));
		colModel.getColumn( 0 ).setPreferredWidth( 20 ); // index
		colModel.getColumn( 1 ).setPreferredWidth( 80 ); // type
	
		JPanel namePanel = new JPanel( new GridLayout(1,2) );
		JLabel nameLabel = new JLabel( "Type-Name:");
		namePanel.add( nameLabel );
		this.nameTextField = new JTextField( "NewTypeName" );
		namePanel.add( this.nameTextField  );
		this.addTypeButton.addActionListener( this );
		this.deleteTypeButton.addActionListener( this );
		JPanel addDeletePanel = new JPanel( new GridLayout(1,2) );
		addDeletePanel.add( this.addTypeButton );
		addDeletePanel.add( this.deleteTypeButton );
		this.okButton.addActionListener( this );
		this.okButton.setMnemonic('O');
		this.cancelButton.addActionListener( this );
		this.cancelButton.setMnemonic('C');
		JPanel okCancelPanel = new JPanel( new GridLayout(1, 2) );
		okCancelPanel.add( this.okButton );
		okCancelPanel.add( this.cancelButton );
		// add UI elements:
		Container contentPane = this.getContentPane();
		// set layout manager:
		GridBagLayout layout = new GridBagLayout();
		contentPane.setLayout( layout );
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 100D;
		constraints.weighty = 100D;
		constraints.fill = GridBagConstraints.BOTH;
		layout.setConstraints(namePanel, constraints);
		contentPane.add( namePanel );
		constraints.gridy = 1;
		layout.setConstraints(addDeletePanel, constraints);
		contentPane.add( addDeletePanel );
		constraints.gridy = 2;
		constraints.gridheight = 5;
		JScrollPane scrollPane = new JScrollPane(this.table );
		layout.setConstraints(scrollPane, constraints);
		contentPane.add( scrollPane );
		constraints.gridy = 7;
		constraints.gridheight = 1;
		layout.setConstraints(okCancelPanel, constraints);
		contentPane.add( okCancelPanel );
		
		setSize( 500, 550 );
		//pack();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == this.addTypeButton ) {
			this.tableModel.addSubtype();
		} else if (source == this.deleteTypeButton ) {
			int index = this.table.getSelectedRow();
			if (index == -1 ) {
				showWarningMessage("Please select a row before deleting a subtype.");
			} else {
				this.tableModel.deleteSubtype( index );
			}
		} else if ( source == this.okButton ) {
			this.okPressed = true;
			this.setVisible( false );
		} else {
			this.setVisible( false );
		}
	}

	private void showWarningMessage( String message ) {
		JOptionPane.showMessageDialog( this, message, "Warning", JOptionPane.WARNING_MESSAGE );
	}

	/**
	 * @return the data type
	 */
	public DataType getDataType() {
		DataType[] subtypes = this.tableModel.getSubtypes();
		if (!this.okPressed || subtypes.length < 2) {
			return null;
		}
		String name = this.nameTextField.getText();
		return new DataType( name, subtypes );
	}

}
