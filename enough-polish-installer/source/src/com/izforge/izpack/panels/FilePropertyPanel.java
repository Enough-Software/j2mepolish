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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
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
 * <p>Allows the user to enter a single property that relates to a path or file.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 11, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FilePropertyPanel extends JPanel implements ActionListener {
	
	private final boolean isDirectory;
	private final boolean needsToExist;

	private boolean isMacOsX;
	private Frame parentFrame;
	private JTextField inputTextField;
	private JButton openButton;
	private JComboBox inputComboBox;
	private String propertyName;
	private final String label;
	private JLabel labelComponent;

	public FilePropertyPanel( Frame parent, String label, String value, String openButtonLabel, boolean isDirectory, boolean needsToExist ) {
		this( parent, label, new String[]{ value }, openButtonLabel, isDirectory, needsToExist );
	}
	public FilePropertyPanel( Frame parent, String label, String[] values, String openButtonLabel, boolean isDirectory, boolean needsToExist ) {
		this( parent, null,  label, values, openButtonLabel, isDirectory, needsToExist );
	}
	public FilePropertyPanel( Frame parent, String description, String label, String[] values, String openButtonLabel, boolean isDirectory, boolean needsToExist ) {
		this( parent, description,  label, values, openButtonLabel, isDirectory, needsToExist, new GridBagLayout() );
	}
	public FilePropertyPanel( Frame parent, String description, String label, String[] values, String openButtonLabel, boolean isDirectory, boolean needsToExist, GridBagLayout layout ) {
		super( layout );
		this.parentFrame = parent;
		this.label = label;
		this.isDirectory = isDirectory;
		this.needsToExist = needsToExist;
		this.isMacOsX = (System.getProperty("mrj.version") != null);

	    GridBagConstraints constraints = new GridBagConstraints();
	    constraints.insets.top = 2; constraints.insets.left=1; constraints.insets.bottom=0; constraints.insets.right=1;
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.anchor = GridBagConstraints.LINE_START;

		
		// init view:
	    constraints.gridwidth = 10;
	    constraints.gridx = 0;
	    constraints.gridy = 0;
		if ( description == null ) {
			description = " ";
		}
		JLabel descriptionLabel = new JLabel( description);
		Font font = descriptionLabel.getFont();
		font = font.deriveFont( Font.ITALIC );
		descriptionLabel.setFont(font);
		add( descriptionLabel, constraints );

	    //constraints.weightx = 1.0D;
	    constraints.insets.top = 0;
	    constraints.insets.bottom = 1;
	    constraints.gridwidth = 3;
	    constraints.gridx = 0;
	    constraints.gridy = 1;
	    this.labelComponent = new JLabel( label ); 
		add( this.labelComponent, constraints );
		
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.gridwidth = 5;
	    constraints.gridx = 3;
	    constraints.gridy = 1;
	    constraints.weightx = 1.0D;
		if (values == null || values.length <= 1) {
			String value = null;
			if (values != null && values.length == 1) {
				value = values[0];
			}
			this.inputTextField = new JTextField( value );
			add( this.inputTextField, constraints );
		} else {
			this.inputComboBox = new JComboBox(values);
			this.inputComboBox.setEditable(true);
			this.inputComboBox.setSelectedIndex( 0 );
			add( this.inputComboBox, constraints );
		}
		
	    constraints.weightx = 0.0D;
	    constraints.fill = GridBagConstraints.NONE;
	    constraints.gridwidth = 2;
	    constraints.gridx = 8;
	    constraints.gridy = 1;
		this.openButton = new JButton( openButtonLabel );
		this.openButton.addActionListener( this );
		add( this.openButton, constraints );
		
	
	}
	
	public int getLabelWidth() {
		return this.labelComponent.getPreferredSize().width;
	}
	
	public void setLabelWidth( int width ) {
		Dimension rect = this.labelComponent.getPreferredSize();
		//int diff  =  width - rect.width;
		rect.width = width;
		this.labelComponent.setPreferredSize(rect);
//		if (this.inputTextField != null) {
//			this.inputTextField.getBounds(rect);
//			rect.x += diff;
//			rect.width -= diff;
//			this.inputTextField.setBounds(rect);
//		} else if (this.inputComboBox != null) {
//			this.inputComboBox.getBounds(rect);
//			rect.x += diff;
//			rect.width -= diff;
//			this.inputComboBox.setBounds(rect);			
//		}
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
//	        	File file = null;
//	        	if (fileName != null) {
//	        		file = new File( fileName );
//	        	}
//	        	if ( this.isDirectory && isDirectory(file)) {
//	        		file = file.getParentFile();
//	        	}
//        		path = file.getAbsolutePath();
//        	} else {
        		// use swing on other operating systems:
	            JFileChooser fc = new JFileChooser();
	            fc.setCurrentDirectory( getValueFile() );
	            fc.setMultiSelectionEnabled(false);
	            if (this.isDirectory ) { //&& !this.isMacOsX) { 
	            	// on OS X the .app applications do not count as folders:
	            	if (this.isMacOsX) {
	            		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	            	} else {
	            		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);	            		
	            	}
	            }
	            fc.addChoosableFileFilter(fc.getAcceptAllFileFilter());
	
	            // Shows it
	            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
	            {
	            	return;
	            }
	            File file = fc.getSelectedFile();
	            if (this.isDirectory && !isDirectory(file)) {
	            	file = file.getParentFile();
	            }
                path = fc.getSelectedFile().getAbsolutePath();
//        	}
        	if (this.needsToExist) {
        		File existingFile = new File( path );
        		if (!existingFile.exists()) {
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

	private boolean isDirectory(File file) {
		if (file == null) {
			return false;
		}
		return file.isDirectory() || (this.isMacOsX && file.getName().endsWith(".app"));
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
