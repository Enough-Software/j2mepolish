/*
 * Created on 30-Oct-2004 at 18:41:00.
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
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.enough.polish.util.FileUtil;
import de.enough.polish.util.SwingUtil;

/**
 * <p>Shows the generated code and makes it available for the clipboard etc.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        30-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CodeEditor 
extends JFrame 
implements ActionListener, ClipboardOwner 
{
	private static final long serialVersionUID = 2486441360577961200L;

	private final JEditorPane editor;
	private final JButton copyToClipboardButton;
	private final JButton saveButton;
	private File currentDirectory = new File(".");

	/**
	 * @param title
	 * @param code
	 * @param icon
	 * @throws java.awt.HeadlessException
	 */
	public CodeEditor(String title, String code, Image icon) throws HeadlessException {
		super(title);
		this.editor = new JEditorPane( "text/plain", code );
		this.copyToClipboardButton = new JButton("Copy to Clipboard");
		this.copyToClipboardButton.addActionListener( this );
		this.copyToClipboardButton.setMnemonic('C');
		this.saveButton = new JButton("Save");
		this.saveButton.setMnemonic('S');
		this.saveButton.addActionListener( this );
		JScrollPane scrollPane = new JScrollPane( this.editor );
		Container contentPane = this.getContentPane();
		contentPane.setLayout( new BorderLayout() );
		contentPane.add( scrollPane, BorderLayout.CENTER );
		if (icon != null) {
			setIconImage(icon);
		}
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout( new GridLayout( 1, 2));
		buttonPanel.add( this.copyToClipboardButton );
		buttonPanel.add( this.saveButton );
		
		contentPane.add( buttonPanel, BorderLayout.SOUTH );
		
		setSize( 700, 800 );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if ( source == this.copyToClipboardButton ) {
			StringSelection selection = new StringSelection( this.editor.getText() );
			Clipboard clipboard = getToolkit().getSystemClipboard();
			clipboard.setContents(selection, this );
		} else if ( source == this.saveButton ) {
			File targetFile = SwingUtil.openFile( ".java", false, this.currentDirectory, this );
			if (targetFile != null) {
				if ( targetFile.getName().indexOf('.') == -1) {
					targetFile = new File( targetFile.getAbsolutePath() + ".java" );
				}
				this.currentDirectory = targetFile.getParentFile();
				String text = this.editor.getText();
				try {
					FileUtil.writeTextFile(targetFile, new String[]{ text } );
				} catch (IOException e) {
					System.err.println("Unable to save java-file: " + targetFile.getAbsolutePath() + ": " + e.toString() );
					e.printStackTrace();
				}
			} 			
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard, java.awt.datatransfer.Transferable)
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// ignore
	}

}
