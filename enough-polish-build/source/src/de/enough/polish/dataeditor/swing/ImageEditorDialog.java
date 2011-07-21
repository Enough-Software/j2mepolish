/*
 * Created on 13-Nov-2004 at 15:17:01.
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
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.enough.polish.util.SwingUtil;

/**
 * <p>Shows an image-viewer which allows to save the image externally and to load it from an external source.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        13-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ImageEditorDialog 
extends JDialog 
implements ActionListener 
{
	private static final long serialVersionUID = -4574658971912104600L;

	private BufferedImage image;
	private final JButton saveButton;
	private final JButton loadButton;
	private final JButton okButton;
	private final JButton cancelButton;
	private final JLabel imageViewer;
	private boolean isChanged;
	private File currentDirectory;
	private final JFrame parent;

	/**
	 * @param owner
	 * @param title
	 * @param image
	 * @param currentDirectory
	 * @throws java.awt.HeadlessException
	 */
	public ImageEditorDialog(JFrame owner, String title, BufferedImage image, File currentDirectory )
	throws HeadlessException 
	{
		super(owner, title, true);
		this.currentDirectory = currentDirectory;
		this.parent = owner;
		this.image = image;
		this.saveButton = new JButton( "Save..." );
		this.saveButton.addActionListener( this );
		this.loadButton = new JButton( "Load..." );
		this.loadButton.addActionListener( this );
		this.okButton = new JButton( "OK" );
		this.okButton.addActionListener( this );
		this.cancelButton = new JButton( "Cancel" );
		this.cancelButton.addActionListener( this );
		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
		this.imageViewer = new JLabel("");
		int width = 400;
		int height = 100;
		if (image != null) {
			this.imageViewer.setIcon( new ImageIcon( image ) );
			width = image.getWidth() + 100;
			height = image.getHeight() + 100;
		} else {
			this.imageViewer.setText("Please load an image first");
		}
		if (width < 400) {
			width = 400;
		}
		setSize( width, height );
		contentPane.add( new JScrollPane( this.imageViewer ), BorderLayout.CENTER );
		JPanel panel = new JPanel( new GridLayout( 1, 4 ));
		panel.add( this.saveButton );
		panel.add( this.loadButton );
		panel.add( this.okButton );
		panel.add( this.cancelButton );
		contentPane.add( panel, BorderLayout.SOUTH );
	}
	
	public BufferedImage getImage() {
		return this.image;
	}
	
	public boolean isChanged() {
		return this.isChanged;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == this.saveButton) {
			if (this.image == null) {
				JOptionPane.showMessageDialog(this.parent, "Unable to save non-existing image. Please load an image first." );
				return;
			}
			File file = SwingUtil.openFile(".png", false, this.currentDirectory, this.parent );
			if (file != null) {
				try {
					ImageIO.write( this.image, "png",  file );
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this.parent, "Unable to save image to " + file.getAbsolutePath() + ": " + e.toString() );
				}
				this.currentDirectory = file.getParentFile();
			}
		} else if ( source == this.loadButton ) {
			File file = SwingUtil.openFile(".png", true, this.currentDirectory, this.parent );
			if (file != null) {
				try {
					this.image = ImageIO.read( file );
					this.isChanged = true;
					this.imageViewer.setIcon( new ImageIcon( this.image ) );
					this.imageViewer.setText("");
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this.parent, "Unable to load image from " + file.getAbsolutePath() + ": " + e.toString() );
				}
				this.currentDirectory = file.getParentFile();
			}
		} else if (source == this.cancelButton) {
			this.isChanged = false;
			setVisible( false );
		} else if (source == this.okButton ) {
			setVisible( false );
		}
	}


}
