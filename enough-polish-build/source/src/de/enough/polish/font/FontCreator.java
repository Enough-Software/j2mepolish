/*
 * Created on 09-Nov-2004 at 21:31:25.
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
package de.enough.polish.font;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import de.enough.polish.dataeditor.swing.SwingDataEditor;
import de.enough.polish.swing.StatusBar;
import de.enough.polish.swing.SwingApplication;
import de.enough.polish.util.SwingUtil;

/**
 * <p>An utility for creating bitmap-font-files, which can be used by the J2ME Polish client classes.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FontCreator 
extends SwingApplication
implements ActionListener
{

	private static final long serialVersionUID = -8392341822931692366L;
	private JMenuItem menuSave;
	private JMenuItem menuQuit;
	private JMenuItem menuSaveAs;
	private JMenuItem menuOpenTrueTypeFont;
	//private JMenuItem menuOpenBitMapFont;
	private JMenuItem menuOpenImage;
	private final StatusBar statusBar;
	private JScrollPane scrollPane;
	private File currentDirectory = new File(".");
	private File bitMapFontFile;
	private File fontFile;
	private TrueTypeFontViewer trueTypeViewer;
	private JMenuItem menuSavePngImageAs;
	private JMenuItem menuOpenInDataEditor;

	/**
	 * 
	 */
	public FontCreator() {
		super("FontEditor", true );
		setJMenuBar( createMenuBar() );
		setSize( 900, 600 );
		Container contentPane = getContentPane();
		this.scrollPane = new JScrollPane( new JLabel("Please open any true type font or drag it here."));
		contentPane.add( this.scrollPane );
		this.statusBar = new StatusBar();
		contentPane.add( this.statusBar, BorderLayout.SOUTH );
		SwingUtil.setImageIcon(this, "icons/font.png");
		updateTitle();
		//pack();
	}
	
	/**
	 * 
	 */
	private void updateTitle() {
		String title =  "J2ME Polish: BitMapFont Creator";
		if (this.fontFile != null) {
			title += ": " + this.fontFile.getName();
			if (this.bitMapFontFile != null) {
				title += " = " + this.bitMapFontFile.getName();
			}
		}
		this.setTitle( title );
	}

	private JMenuBar createMenuBar() {
		// create menu-bar:
		JMenuBar menuBar = new JMenuBar();
		int shortcutKeyMask = getNativeShortcutKeyMask();
		menuBar.add( createFileMenu(shortcutKeyMask) );
		//menuBar.add( createEditMenu() );
		return menuBar;
	}
	
	private JMenu createFileMenu(int shortcutKeyMask) {
		// create file-menu:
		JMenu menu = new JMenu( "File" );
		menu.setMnemonic('f');
 
		JMenuItem item = new JMenuItem( "Save", 'S' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSave = item;

		item = new JMenuItem( "Save As...", 'A' );
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveAs = item;

		item = new JMenuItem( "Save PNG Image As...", 'P' );
		item.addActionListener( this );
		menu.add( item );
		this.menuSavePngImageAs = item;

		menu.addSeparator();

		item = new JMenuItem( "Open True-Type-Font", 't' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'O', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenTrueTypeFont = item;

		/*
		item = new JMenuItem( "Open Bit-Map-Font", 'b' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'B', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenBitMapFont = item;

		*/
		item = new JMenuItem( "Open PNG-Image", 'i' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'I', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenImage = item;
		
		item = new JMenuItem( "Open in Binary Editor", 'e' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'E', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenInDataEditor = item;

		menu.addSeparator();

		item = new JMenuItem( "Quit", 'q' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'Q', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuQuit = item;
		return menu;
	}
	
	/*
	private JMenu createEditMenu() {
		// create edit-menu:
		JMenu menu = new JMenu( "Edit" );
		menu.setMnemonic('e');
		JMenuItem item = new JMenuItem( "Add Entry", 'a' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'N', Event.CTRL_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuAddEntry = item;
		item = new JMenuItem( "Delete Entry", 't' );
		item.addActionListener( this );
		menu.add( item );
		this.menuDeleteEntry = item;
		item = new JMenuItem( "Move Entry Down", 'd' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'D', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuMoveDownEntry = item;
		item = new JMenuItem( "Move Entry Up", 'u' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'U', Event.CTRL_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuMoveUpEntry = item;
		menu.addSeparator();
		item = new JMenuItem( "Add Custom Type", 't' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'T', Event.CTRL_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuAddType = item;
		return menu;
	}
		*/

	public static void main(String[] args) {
		FontCreator creator = new FontCreator();
		creator.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		try {
			if ( source == this.menuQuit ) {
				quit();
			} else if ( source == this.menuOpenTrueTypeFont ) {
				openTrueTypeFont();
			} else if ( source == this.menuSave ) {
				save();
			} else if ( source == this.menuSaveAs ) {
				saveAs();
			} else if ( source == this.menuSavePngImageAs ) {
				savePngImageAs();
			}  else if ( source == this.menuOpenImage ) {
				openImage();
			} else if ( source == this.menuOpenInDataEditor ) {
				openInDataEditor();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.statusBar.warn( event.getActionCommand() + " failed: " + e.toString() );
		}	
	}
	
	
	
	/**
	 * @throws IOException
	 * 
	 */
	private void openInDataEditor() 
	throws IOException 
	{
		File definitionFile = new File( "bmf.definition");
		if (!definitionFile.exists()) {
			JOptionPane.showMessageDialog(this, "Unable to locate the \"bmf.definition\" file. This should be in the same directory as the fonteditor-start-script.");
			return;
		}
		save();
		if (this.bitMapFontFile == null) {
			JOptionPane.showMessageDialog(this, "You need to save the bitmap-font-file first.");
			return;
		}
		SwingDataEditor editor = new SwingDataEditor( definitionFile, this.bitMapFontFile, false );
		editor.setVisible( true );
		
	}

	/**
	 * Opens an image and sets this as data to the current viewer.
	 * @throws IOException
	 */
	private void openImage() throws IOException {
		if (this.trueTypeViewer == null ) {
			JOptionPane.showMessageDialog( this, "You need to open a True Type Font first.");
			return;
		}
		File file = openFile( ".png", true );
		if (file != null) {
			this.trueTypeViewer.setImage( file );
			this.statusBar.message( "Loaded " + file.getName() );
		}
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void savePngImageAs() throws IOException {
		if (this.trueTypeViewer != null) {
			File file = openFile( ".png", false );
			if (file != null) {
				this.trueTypeViewer.savePngFile(file);
				this.statusBar.message( "saved " + file.getName() );
			}
		}
	}

	/**
	 * Saves the current font.
	 * @throws IOException
	 */
	private void save() throws IOException {
		if (this.bitMapFontFile == null) {
			saveAs();
			return;
		}
		if (this.trueTypeViewer != null) {
			this.trueTypeViewer.saveBitMapFont( this.bitMapFontFile );
			updateTitle();
			this.statusBar.message( "saved " + this.bitMapFontFile.getName() + " (" + this.bitMapFontFile.length() + " bytes)" );
		}
	}

	/**
	 * Saves the current font under a new name.
	 * @throws IOException
	 */
	private void saveAs() throws IOException {
		File file = openFile( ".bmf", false );
		if (file != null) {
			this.bitMapFontFile = file;
			save();
		}
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#openDocument(java.io.File)
	 */
	public void openDocument(File file) {
		try {
			openTrueTypeFont( file );
		} catch (IOException e) {
			this.statusBar.warn("Unable to load font: " + e.toString() );
			e.printStackTrace();
		}
	}
	/**
	 * @throws IOException
	 * 
	 */
	private void openTrueTypeFont() throws IOException {
		File file = openFile(".ttf", true);
		if (file != null) {
			openTrueTypeFont( file );
		}
	}
	
	private void openTrueTypeFont( File file ) throws IOException {
		this.trueTypeViewer = new TrueTypeFontViewer( file, this.statusBar );	
		Container contentPane = getContentPane();
		contentPane.remove( this.scrollPane );
		this.scrollPane = new JScrollPane( this.trueTypeViewer );
		contentPane.add( this.scrollPane );
		this.statusBar.message("Loaded TTF font " + file.getName() );
		this.fontFile = file;
		this.bitMapFontFile = null;
		updateTitle();
	}

	
	/**
	 * @param extension
	 * @return the file that should be opened
	 */
	private File openFile(String extension, boolean open ) {
		File selectedFile = SwingUtil.openFile( extension, open, this.currentDirectory, this );
		if (selectedFile != null) {
			this.currentDirectory = selectedFile.getParentFile();
		} 
		return selectedFile;
	}
	
}
