/*
 * Created on 30-Oct-2004 at 18:30:13.
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
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <p>Asks the user for the package-name as well as the class-name for the to-be-generated Java code.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        30-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CreateCodeDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1161197272516841399L;

	private final JTextField packageNameField;
	private final JTextField classNameField;
	private final JButton okButton;
	private boolean okPressed;

	/**
	 * @param owner
	 * @param title
	 * @param packageName
	 * @param className
	 * @throws java.awt.HeadlessException
	 */
	public CreateCodeDialog(Frame owner, String title, String packageName, String className ) throws HeadlessException {
		super(owner, title, true);
		
		this.packageNameField = new JTextField( packageName );
		this.classNameField = new JTextField( className );
		this.okButton = new JButton("Generate Code");
		this.okButton.setMnemonic('G');
		this.okButton.addActionListener( this );
		
		JPanel packageNamePanel = new JPanel( new GridLayout(1, 2) );
		JLabel label = new JLabel("Package-Name:");
		packageNamePanel.add( label );
		packageNamePanel.add( this.packageNameField );
		
		JPanel classNamePanel = new JPanel( new GridLayout(1, 2) );
		label = new JLabel("Class-Name:");
		classNamePanel.add( label  );
		classNamePanel.add( this.classNameField );
		
		Container contentPane = getContentPane();
		contentPane.setLayout( new GridLayout( 3, 1 ));
		contentPane.add( packageNamePanel );
		contentPane.add( classNamePanel );
		contentPane.add( this.okButton );
		
		setSize( 300, 200 );
		//pack();
	}
	
	public String getClassName() {
		this.okPressed = false;
		return this.classNameField.getText();
	}
	
	public String getPackageName() {
		return this.packageNameField.getText();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		this.okPressed = e.getSource() == this.okButton;
		setVisible(false);
	}
	
	public boolean okPressed() {
		return this.okPressed;
	}

}
