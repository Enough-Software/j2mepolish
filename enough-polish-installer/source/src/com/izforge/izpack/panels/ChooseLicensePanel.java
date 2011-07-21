/*
 * Created on May 31, 2004
 * 
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package com.izforge.izpack.panels;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.util.MultiLineLabel;

/**
 * @author robertvirkus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChooseLicensePanel extends IzPanel 
implements  ChangeListener {
	
	private final static int GPL = 1;
	private final static int EVALUATION = 2;
	private final static int COMMERCIAL = 3;
	private final static int KEY_LENGTH = 12;
	
	ButtonGroup radioBox;
	JRadioButton gplButton;
	JRadioButton evaluationButton;
	JRadioButton commercialButton;
	private JTextArea licenseInformationLabel;
	private int selectedLicense;

	/**
	 * @param parent
	 * @param idata
	 */
	public ChooseLicensePanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
	
		JPanel subPanel = new JPanel();
		
		// The layout
	    GridBagLayout layout = new GridBagLayout();
	    subPanel.setLayout(layout);
	    GridBagConstraints constraints = new GridBagConstraints();
	    constraints.insets = new Insets(0, 0, 0, 0);
	    constraints.fill = GridBagConstraints.VERTICAL;
	    constraints.anchor = GridBagConstraints.SOUTHWEST;
	    
	    JLabel title = new JLabel("License Selection");
	    title.setFont( title.getFont().deriveFont( title.getFont().getSize() * 2F ));
	    constraints.gridwidth = 3;
	    constraints.gridx = 0;
	    constraints.gridy = 0;
	    layout.setConstraints( title, constraints );
	    subPanel.add( title );
	    
	    
	    JLabel chooseType = new JLabel("Please choose the license for using J2ME Polish:");
	    constraints.gridwidth = 3;
	    constraints.gridx = 0;
	    constraints.gridy = 1;
	    layout.setConstraints( chooseType, constraints );
	    subPanel.add( chooseType );
	    
	    chooseType = new JLabel("Type:");
	    constraints.gridwidth = 1;
	    constraints.gridx = 0;
	    constraints.gridy = 2;
	    layout.setConstraints( chooseType, constraints );
	    subPanel.add( chooseType );
	    
	    
	    this.radioBox = new ButtonGroup();
	    JRadioButton radioButton = new JRadioButton("GPL");
	    this.radioBox.add( radioButton );
	    constraints.gridy = 2;
	    constraints.gridx = 1;
	    constraints.gridwidth = 2;
	    layout.setConstraints( radioButton, constraints );
	    subPanel.add( radioButton );
	    radioButton.addChangeListener( this );
	    this.gplButton = radioButton;
	    
	    radioButton = new JRadioButton("Evaluation");
	    this.radioBox.add( radioButton );
	    constraints.gridy = 3;
	    layout.setConstraints( radioButton, constraints );
	    subPanel.add( radioButton );
	    radioButton.addChangeListener( this );
	    this.evaluationButton = radioButton;
	    
	    radioButton = new JRadioButton("Commercial / None-GPL");
	    this.radioBox.add( radioButton );
	    constraints.gridy = 4;
	    layout.setConstraints( radioButton, constraints );
	    subPanel.add( radioButton );
	    radioButton.addChangeListener( this );
	    this.commercialButton = radioButton;
	    
	    this.licenseInformationLabel = new JTextArea( "Please select a license for more information... " );
	    this.licenseInformationLabel.setEditable( false );
	    this.licenseInformationLabel.setLineWrap( true );
	    this.licenseInformationLabel.setBackground( title.getBackground() );

	    constraints.gridy = 5;
	    constraints.gridx = 0;
	    constraints.gridwidth = 3;
	    constraints.gridheight = 2;
	    constraints.fill=GridBagConstraints.BOTH;
	    constraints.weightx = 1.0D;
	    constraints.weightx = 1.0D;
	    layout.setConstraints( this.licenseInformationLabel, constraints );
	    subPanel.add( this.licenseInformationLabel );
	   
	    
	    setLayout( new BorderLayout() );
	    add( subPanel, BorderLayout.NORTH );
	}



	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();
		if (source == this.commercialButton ) {
			if (this.commercialButton.isSelected()) {
				this.selectedLicense = COMMERCIAL;
				this.licenseInformationLabel.setText("When you obtain a commercial license you can use all J2ME Polish components in your commercial application(s) without releasing the source code of your application(s). Please follow the instructions on www.j2mepolish.org for ordering your commercial license.");
				this.parent.unlockNextButton();
			}
		} else if ( source == this.gplButton) {
			if (this.gplButton.isSelected()) {
				this.selectedLicense = GPL;
				this.licenseInformationLabel.setText("When you are using the GNU General Public License (GPL) you can use all features of J2ME Polish when you release the source code of your application(s) under the GPL license as well. You can also use the GPL license for your commercial applications as long as you are only using the build framework of J2ME Polish and not the user interface, the RMI, serializatin, peristence and so forth frameworks.");
				this.parent.unlockNextButton();
			}
		} else if ( source == this.evaluationButton) {
			if (this.evaluationButton.isSelected()) {
				this.selectedLicense = EVALUATION;
				this.licenseInformationLabel.setText("Select evaluation when you are not sure yet what license you want to use for J2ME Polish. You can evaluate J2ME Polish for as long as you wish, unless you are using generated applications in a commercial context already.");
				this.parent.unlockNextButton();
			}
		}
	}

	  /**
	   *  Indicates wether the panel has been validated or not.
	   *
	   * @return    true if the user has agreed.
	   */
	  public boolean isValidated()
	  {
	  	String licenseText = "GPL";
		// now set the license variable:
		this.idata.setVariable( "J2ME_POLISH_LICENSE", licenseText );
		return true;
	  }

	  /**  Called when the panel becomes active.  */
	  public void panelActivate()
	  {
		  super.panelActivate();
//		String text = this.licenseField.getText();
//		if ( (text != null && text.length() == KEY_LENGTH ) 
//				|| ((this.selectedLicense != -1)) && (this.selectedLicense != COMMERCIAL )) {
//			this.parent.unlockNextButton();
//		} else {
//			this.parent.lockNextButton();
//		}
	  }

}
