/*
 * Created on 31-Oct-2004 at 20:29:10.
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
package de.enough.polish.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

/**
 * <p>Provides some often used methods for Swing applications.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        31-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class SwingUtil {
	
	private SwingUtil() {
		// do nothin
	}

	/**
	 * Opens a file with a FileChooser dialog.
	 * 
	 * @param extension the needed extension, e.g. ".java"
	 * @param open true when a file should be opened, if false is given, the save-Dialog is shown instead
	 * @param currentDirectory the current directory
	 * @param parent the frame which wants to show the FileChooser
	 * @return the chosen file or null, when cancel was selected
	 */
	public static File openFile(String extension, boolean open, File currentDirectory, JFrame parent ) {
		JFileChooser fileChooser = new JFileChooser( currentDirectory );
		if (extension != null) {
			fileChooser.setFileFilter( new CustomFileFilter( extension ) );
		}
		int result;
		if (open) {
			result = fileChooser.showOpenDialog( parent );
		} else {
			result = fileChooser.showSaveDialog( parent );
		}
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (!open && extension != null) {
				if (!selectedFile.getName().endsWith(extension)) {
					selectedFile = new File( selectedFile.getAbsolutePath() + extension );
				}
			}
			return selectedFile;
		} else {
			return null;
		}
	}
	
	/**
	 * Loads an image from within a jar.
	 * 
	 * @param fileName the image path. 
	 * @return an initialised image-icon or null, when no image could be found.
	 */
	public static Image loadIcon( String fileName ) {
        URL url = ClassLoader.getSystemResource(fileName);
        
        if (url == null) {    	
        	System.out.println("unable to locate [" + fileName + "].");
        	return null;
        }
        return Toolkit.getDefaultToolkit().createImage(url);
	}
	
	/**
	 * Loads an image from within a jar or from the file system.
	 * 
	 * @param fileName the image path. 
	 * @param packageClass a class within the package where the image can be found
	 * @return an initialised image-icon or null, when no image could be found.
	 */
	public static Image loadIcon( String fileName, Class packageClass ) {
		try {
	        ClassLoader classLoader = packageClass.getClassLoader(); 			
	        InputStream is = classLoader.getResourceAsStream(fileName);
			if (is == null) {
				File file = new File( fileName );
				if (file.exists()) {
					is = new FileInputStream( file );
				} else {
					return null;
				}
			}
			return ImageIO.read( is );
		} catch (IOException e) {
			System.err.println("Unable to load image [" + fileName + "]");
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * Provides an easy way to set the icon of a JFrame.
	 * When the image could not be loaded, nothing will be changed.
	 * 
	 * @param frame the frame which should have an icon
	 * @param fileName the path of the icon
	 */
	public static void setImageIcon( JFrame frame, String fileName ) {
		Image image = loadIcon( fileName );
		if (image != null) {
			frame.setIconImage(image);
		}
	}
	
	/**
	 * @param url
	 * @param button
	 * @param packageClass
	 */
	public static void setImageIcon(String url, JButton button, Class packageClass) {
		Image image = SwingUtil.loadIcon(url, packageClass );
		if (image != null) {
			button.setIcon( new ImageIcon( image )  );
		}

	}
	
	/**
	 * Sets a title for the given component by surrounding it with a CompoundBorder
	 * 
	 * @param component the component
	 * @param title the title of the component
	 */
	public static void setTitle( JComponent component, String title ) {
		component.setBorder(  BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder( title ),
                BorderFactory.createEmptyBorder(5,5,5,5)) );
	}


	static class CustomFileFilter extends FileFilter {
		private final String lowerCaseType;
		private final String upperCaseType;
		public CustomFileFilter( String type ) {
			this.lowerCaseType = type.toLowerCase();
			this.upperCaseType = type.toUpperCase();
		}
		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith( this.lowerCaseType ) || f.getName().endsWith(this.upperCaseType);
		}
		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		public String getDescription() {
			return this.lowerCaseType;
		}	
	}
}
