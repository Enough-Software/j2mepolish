/*
 * Created on 02-Nov-2004 at 13:34:55.
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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.enough.polish.dataeditor.DataEntry;
import de.enough.polish.util.StringUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CountEditorDialog 
extends JDialog
implements ActionListener
{
	private static final long serialVersionUID = 7291283200939987740L;

	private final static char[] OPERATION_CHARS = new char[]{ '*', '/', '+', '-' };
	
	private final JTextField editor;
	private final JButton okButton;
	private final JButton cancelButton;
	private final JButton clearButton;
	private final JComboBox operationSelector;
	private char currentOperator;
	private String countText;

	/**
	 * @param owner
	 * @param title
	 * @param possibleOperants
	 * @param countText
	 * @throws java.awt.HeadlessException
	 */
	public CountEditorDialog(Frame owner, String title, DataEntry[] possibleOperants, String countText )
	throws HeadlessException 
	{
		super(owner, title, true);
		this.editor = new JTextField( countText );
		this.countText = countText;
		this.okButton = new JButton( "OK" );
		this.okButton.setMnemonic( 'O' );
		this.okButton.addActionListener( this );
		this.cancelButton = new JButton( "Cancel" );
		this.okButton.setMnemonic( 'C' );
		this.cancelButton.addActionListener( this );
		this.clearButton = new JButton( "Clear" );
		this.clearButton.addActionListener( this );
		this.operationSelector = new JComboBox( new String[]{ "Multiply", "Divide", "Add", "Subtract" } );
		
		// check what operation should be used:
		int index = 0;
		if (countText.indexOf('/') != -1) {
			index = 1;
		} else if (countText.indexOf('+') != -1) {
			index = 2;
		} else if (countText.indexOf('-') != -1) {
			index = 3;
		}
		this.operationSelector.setSelectedIndex(index);
		this.currentOperator = OPERATION_CHARS[index];
		this.operationSelector.addActionListener( this );
		
		JPanel buttonPanel = new JPanel( new GridLayout( 1, 3 ));
		buttonPanel.add( this.okButton );
		buttonPanel.add( this.cancelButton );
		buttonPanel.add( this.clearButton );
		
		JPanel operationPanel = new JPanel( new GridLayout( 1, 2 ));
		operationPanel.add( new JLabel("Operation:"));
		operationPanel.add( this.operationSelector );
		
		JPanel operantPanel = new JPanel( new GridLayout( possibleOperants.length, 1 ));
		for (int i = 0; i < possibleOperants.length; i++) {
			JButton button = new JButton( possibleOperants[i].getName() );
			button.addActionListener( this );
			operantPanel.add( button );
		}
		
		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
		contentPane.add( this.editor, BorderLayout.CENTER );
		contentPane.add( buttonPanel, BorderLayout.SOUTH );
		contentPane.add( operationPanel, BorderLayout.NORTH );
		contentPane.add( operantPanel, BorderLayout.WEST );
		int height = possibleOperants.length * 50;
		if (height < 200 ) {
			height = 200;
		}
		setSize( 500, height );
		//pack();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == this.okButton ) {
			this.countText = this.editor.getText();
			setVisible( false );
		} else if (source == this.cancelButton ) {
			setVisible( false );
		} else if (source == this.clearButton ) {
			this.editor.setText("1");
		} else if (source == this.operationSelector ) {
			char newOperator = OPERATION_CHARS[ this.operationSelector.getSelectedIndex() ];
			String text = this.editor.getText();
			text = StringUtil.replace( text, this.currentOperator, newOperator );
			this.editor.setText( text );
			this.currentOperator = newOperator;
		} else {
			// this is an operant's button:
			JButton button = (JButton) source;
			String operant = button.getText();
			String text = this.editor.getText();
			boolean isNumeric = StringUtil.isNumeric( text );
			if (isNumeric || text.length() == 0) {
				this.editor.setText( operant );
			} else {
				this.editor.setText( text + " " + this.currentOperator + " " + operant );
			}
		}
	}
	
	public String getText() {
		return this.countText;
	}

	

}
