/*
 * Created on 16-Dec-2005 at 11:48:28.
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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        16-Dec-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SelectListEntryDialog 
extends JDialog
implements ActionListener
{
	
	private static final long serialVersionUID = -7131054914416722655L;
	private boolean cancel;
	private JList list;
	private JButton okButton;
	private JButton cancelButton;
	

	/**
	 * Creates a new dialog in which one of the entries has to be selected.
	 * 
	 * @param owner the owner of the dialog
	 * @param title the title
	 * @param description the description
	 * @param entries the entries of the list
	 * @throws HeadlessException
	 */
	public SelectListEntryDialog(Dialog owner, String title, String description, Object[] entries)
			throws HeadlessException {
		super(owner, title, true);
		init( description, entries);
		setLocationRelativeTo( owner );
	}

	/**
	 * Creates a new dialog in which one of the entries has to be selected.
	 * 
	 * @param owner the owner of the dialog
	 * @param title the title
	 * @param description the description
	 * @param entries the entries of the list
	 * @throws HeadlessException
	 */
	public SelectListEntryDialog(Frame owner, String title, String description, Object[] entries)
	throws HeadlessException 
	{
		super(owner, title, true);
		init( description, entries);
		setLocationRelativeTo( owner );
	}
	
	private void init( String description, Object[] entries ) {
		JPanel panel = new JPanel( new BorderLayout() );
		if (description != null) {
			JLabel label = new JLabel( description );
			panel.add( label, BorderLayout.NORTH );
		}
		this.list = new JList( entries );
		this.list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		panel.add( new JScrollPane( this.list ), BorderLayout.CENTER );
		
		this.okButton = new JButton("ok");
		this.okButton.setMnemonic('o');
		this.okButton.addActionListener( this );
		this.cancelButton = new JButton("cancel");
		this.cancelButton.setMnemonic('c');
		this.cancelButton.addActionListener( this );
		
		JPanel buttonPanel = new JPanel( new GridLayout( 1, 2, 5, 2));
		buttonPanel.add( this.okButton );
		buttonPanel.add( this.cancelButton );
		panel.add( buttonPanel, BorderLayout.SOUTH );
		
		getContentPane().add( panel );
		pack();
	}
	
	public boolean cancel() {
		return this.cancel;
	}
	
	public Object getSelectedEntry() {
		return this.list.getSelectedValue();
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if ( source == this.cancelButton ) {
			this.cancel = true;
			setVisible( false );
		} else {
			if (this.list.getSelectedValue() == null) {
				JOptionPane.showMessageDialog( this, "You need to select an entry.", "Invalid Selection", JOptionPane.ERROR_MESSAGE );
				return;
			}
			this.cancel = false;
			setVisible( false );
		}
	}

	

}
