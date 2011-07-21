/*
 * Created on Jan 11, 2007 at 1:34:23 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package com.izforge.izpack.panels;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 11, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FilePropertyPanelBorderLayout extends JPanel implements ActionListener {
	
	private final boolean isDirectory;
	private final boolean needsToExist;

	private boolean isMacOsX;
	private Frame parentFrame;
	private JTextField inputTextField;
	private JButton openButton;
	private JComboBox inputComboBox;
	private String propertyName;
	private final String label;

	public FilePropertyPanelBorderLayout( Frame parent, String label, String value, String openButtonLabel, boolean isDirectory, boolean needsToExist ) {
		this( parent, label, new String[]{ value }, openButtonLabel, isDirectory, needsToExist );
	}
	public FilePropertyPanelBorderLayout( Frame parent, String label, String[] values, String openButtonLabel, boolean isDirectory, boolean needsToExist ) {
		this( parent, null,  label, values, openButtonLabel, isDirectory, needsToExist );
	}
	public FilePropertyPanelBorderLayout( Frame parent, String description, String label, String[] values, String openButtonLabel, boolean isDirectory, boolean needsToExist ) {
		super( new BorderLayout() );
		this.parentFrame = parent;
		this.label = label;
		this.isDirectory = isDirectory;
		this.needsToExist = needsToExist;
		this.isMacOsX = (System.getProperty("mrj.version") != null);

		
		// init view:
		JPanel myPanel = new JPanel( new BorderLayout() );;
		myPanel.add( new JLabel( label ), BorderLayout.WEST );
		if (values == null || values.length <= 1) {
			String value = null;
			if (values != null && values.length == 1) {
				value = values[0];
			}
			this.inputTextField = new JTextField( value );
			myPanel.add( this.inputTextField, BorderLayout.CENTER );
		} else {
			this.inputComboBox = new JComboBox(values);
			this.inputComboBox.setEditable(true);
			this.inputComboBox.setSelectedIndex( 0 );
			myPanel.add( this.inputComboBox, BorderLayout.CENTER );
		}
		this.openButton = new JButton( openButtonLabel );
		this.openButton.addActionListener( this );
		myPanel.add( this.openButton, BorderLayout.EAST );
		
		if ( description == null) {
			description = " ";
		}
		add( new JLabel( description), BorderLayout.NORTH );
		add( myPanel, BorderLayout.CENTER );
	}
	
	public File getValueFile() {
		String path;
		if (this.inputTextField != null) {
			path = this.inputTextField.getText();
		} else {
			path = (String) this.inputComboBox.getSelectedItem();
		}
		if (path == null) {
			return null;
		} else {
			return new File( path );
		}
	}
	
	public String getValueString() {
		String path;
		if (this.inputTextField != null) {
			path = this.inputTextField.getText();
		} else {
			path = (String) this.inputComboBox.getSelectedItem();
		}
		return path;
	}
	
	/**
     * Actions-handling method.
     * 
     * @param e The event.
     */
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source == this.openButton )
        {
        	try {
        	 // The user wants to browse its filesystem
        	String path;
            // Prepares the file chooser
//        	if (this.isMacOsX) {
//        		// use AWT on OS X:
//	        	FileDialog dialog = new FileDialog( this.parentFrame, this.label );
//	        	dialog.setFile( getValueString() );
//	        	if (this.needsToExist) {
//	        		dialog.setMode( FileDialog.LOAD );
//	        	} else {
//	        		dialog.setMode( FileDialog.SAVE );
//	        	}
//	        	dialog.setVisible(true);
//	        	String fileName = dialog.getFile();
//	        	if (fileName == null) {
//	        		fileName = dialog.getDirectory();
//	        	}
//	        	File file = new File( fileName );
//	        	if ( this.isDirectory && !file.isDirectory()) {
//	        		file = file.getParentFile();
//	        	}
//        		path = file.getAbsolutePath();
//        	} else {
        		// use swing on other operating systems:
	            JFileChooser fc = new JFileChooser();
	            fc.setCurrentDirectory( getValueFile() );
	            fc.setMultiSelectionEnabled(false);
	            if (this.isDirectory && !this.isMacOsX) { // on OS X the .app applications do not count as folders
	            	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            }
	            fc.addChoosableFileFilter(fc.getAcceptAllFileFilter());
	
	            // Shows it
	            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
	            {
	            	return;
	            }
                path = fc.getSelectedFile().getAbsolutePath();
//        	}
        	if (this.needsToExist) {
        		File file = new File( path );
        		if (!file.exists()) {
        			//showWarning("")
        			return;
        		}
        	}
        	setValue( path );
        	} catch (Exception ex) {
        		ex.printStackTrace();
        	}
        }
    }

	public void setValue(String value) {
		if (this.inputTextField != null) {
			this.inputTextField.setText( value );
		} else {
			this.inputComboBox.setSelectedItem( value );
		}
	}
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return this.propertyName;
	}
	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

}
