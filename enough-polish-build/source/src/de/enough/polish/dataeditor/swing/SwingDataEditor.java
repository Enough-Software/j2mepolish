/*
 * Created on 19-Oct-2004 at 17:17:11.
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
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import de.enough.polish.dataeditor.DataEditorUI;
import de.enough.polish.dataeditor.DataEntry;
import de.enough.polish.dataeditor.DataManager;
import de.enough.polish.dataeditor.DataType;
import de.enough.polish.swing.StatusBar;
import de.enough.polish.swing.SwingApplication;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.SwingUtil;

/**
 * <p>Provides a swing-based GUI for the binary data editor.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SwingDataEditor 
extends SwingApplication
implements DataEditorUI, ActionListener
{
	private static final long serialVersionUID = 6447380899408950583L;

	private JMenuItem menuNewDefinition;	
	private JMenuItem menuOpenDefinition;
	private JMenuItem menuOpenData;
	private JMenuItem menuSaveDefinition;
	private JMenuItem menuSaveDefinitionAs;
	private JMenuItem menuSaveData;
	private JMenuItem menuSaveDataAs;
	private JMenuItem menuSaveAll;
	private JMenuItem menuQuit;
	private JMenuItem menuAddEntry;
	private JMenuItem menuDeleteEntry;
	private JMenuItem menuMoveUpEntry;
	private JMenuItem menuMoveDownEntry;
	private JMenuItem menuAddType;
	private final StatusBar statusBar;
	private final JTextField descriptionField;
	private final JTextField extensionField;
	private File definitionFile;
	private File dataFile;
	private final DataManager dataManager;
	private final DataTableModel dataTableModel;
	private final DataView dataView;
	private File currentDirectory = new File(".");
	private JMenuItem menuGenerateCode;
	
	private CreateCodeDialog createCodeDialog;
	private Image icon;
	private String packageName;
	
	/**
	 * Creates a new empty data editor with a swing GUI. 
	 */
	public SwingDataEditor( ) {
		this( null, null, true );
	}

	/**
	 * Creates a new empty data editor with a swing GUI.
	 *  
	 * @param definition the definition file. Is ignored if null.
	 * @param data the data file. Is ignored if null.
	 * @param doSystemExit true when the editor should call System.exit() whewn quitting.
	 */
	public SwingDataEditor( File definition, File data, boolean doSystemExit  ) {
		super("BinaryEditor", doSystemExit);
		setJMenuBar( createMenuBar() );
		this.dataManager = new DataManager();
		DataEntry entry = new DataEntry( "noname", DataType.BYTE );
		this.dataManager.addDataEntry( entry );
		setSize( 900, 600 );
		//pack();
		this.descriptionField = new JTextField();
		this.descriptionField.addActionListener( this );
		this.extensionField = new JTextField();
		this.extensionField.addActionListener( this );
		
		// add data-view:
		this.dataTableModel = new DataTableModel( this.dataManager, this );
		this.dataView = new DataView( this, this.dataTableModel, this.dataManager );
		this.dataView.setPreferredScrollableViewportSize(new Dimension(900, 550));
		registerDropTarget( this.dataView );
		JScrollPane scrollPane = createScrollPane(this.dataView);
		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
		JPanel descriptionPanel = new JPanel( new BorderLayout() );
		descriptionPanel.add( new JLabel("description: "), BorderLayout.WEST );
		descriptionPanel.add( this.descriptionField, BorderLayout.CENTER );
		JPanel extensionPanel = new JPanel( new BorderLayout() );
		extensionPanel.add(   new JLabel("extension:   "), BorderLayout.WEST );
		extensionPanel.add( this.extensionField, BorderLayout.CENTER );
		JPanel definitionPanel = new JPanel( new GridLayout( 2, 1));
		definitionPanel.add( descriptionPanel );
		definitionPanel.add( extensionPanel );
		contentPane.add( definitionPanel, BorderLayout.NORTH );
		contentPane.add( scrollPane, BorderLayout.CENTER );
		this.statusBar = new StatusBar();
		contentPane.add( this.statusBar, BorderLayout.SOUTH );
		updateTitle();
		
		setIcon( "icons/binaryeditor.png" );
		
		if ( definition == null ) {
			loadSettings();			
		} else {
			openDefinition( definition );
			if (data != null) {
				openData( data );
			}
		}
		this.dataManager.registerUI( this );
	}
	
	private JMenuBar createMenuBar() {
		// create menu-bar:
		JMenuBar menuBar = new JMenuBar();
		int shortcutKeyMask = getNativeShortcutKeyMask();
		menuBar.add( createFileMenu(shortcutKeyMask) );
		menuBar.add( createEditMenu(shortcutKeyMask) );
		menuBar.add( createCodeMenu(shortcutKeyMask) );
		return menuBar;
	}

	/**
	 * @return
	 */
	private JMenu createFileMenu(int shortcutKeyMask) {
		// create file-menu:
		JMenu menu = new JMenu( "File" );
		menu.setMnemonic('f');
		JMenuItem item = new JMenuItem( "New", 'N' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'N', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuNewDefinition = item;
		item = new JMenuItem( "Save All", 'A' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveAll = item;
		menu.addSeparator();
		item = new JMenuItem( "Save Definition", 's' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', Event.ALT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveDefinition = item;
		item = new JMenuItem( "Save Definition As...", 'a' );
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveDefinitionAs = item;
		item = new JMenuItem( "Open Definition", 'o' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'O', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenDefinition = item;
		menu.addSeparator();
		item = new JMenuItem( "Save Data", 'd' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'S', Event.ALT_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveData = item;
		item = new JMenuItem( "Save Data As...", 't' );
		item.addActionListener( this );
		menu.add( item );
		this.menuSaveDataAs = item;
		item = new JMenuItem( "Open Data", 'o' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'O', Event.ALT_MASK + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuOpenData = item;
		menu.addSeparator();
		item = new JMenuItem( "Quit", 'q' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'Q', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuQuit = item;
		return menu;
	}
	
	private JMenu createEditMenu(int shortcutKeyMask) {
		// create edit-menu:
		JMenu menu = new JMenu( "Edit" );
		menu.setMnemonic('e');
		JMenuItem item = new JMenuItem( "Add Entry", 'a' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'N', shortcutKeyMask + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuAddEntry = item;
		item = new JMenuItem( "Delete Entry", 't' );
		item.addActionListener( this );
		menu.add( item );
		this.menuDeleteEntry = item;
		item = new JMenuItem( "Move Entry Down", 'd' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'D', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuMoveDownEntry = item;
		item = new JMenuItem( "Move Entry Up", 'u' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'U', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuMoveUpEntry = item;
		menu.addSeparator();
		item = new JMenuItem( "Add Custom Type", 't' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'T', shortcutKeyMask + Event.SHIFT_MASK ));
		item.addActionListener( this );
		menu.add( item );
		this.menuAddType = item;
		return menu;
	}
	
	/**
	 * @param shortcutKeyMask
	 * @return
	 */
	private JMenu createCodeMenu(int shortcutKeyMask) {
		// create edit-menu:
		JMenu menu = new JMenu( "Code" );
		menu.setMnemonic('c');
		JMenuItem item = new JMenuItem( "Generate Code", 'g' );
		item.setAccelerator( KeyStroke.getKeyStroke( 'G', shortcutKeyMask ));
		item.addActionListener( this );
		menu.add( item );
		this.menuGenerateCode = item;
		return menu;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		try {
			if ( source == this.menuQuit ) {
				quit();
			} else if (source == this.menuNewDefinition ) {
				newDefinition();
			} else if (source == this.menuSaveAll ) {
				saveAll();
			} else if (source == this.menuOpenDefinition ) {
				openDefinition();
			} else if (source == this.menuSaveDefinition ) {
				saveDefinition();
			} else if (source == this.menuSaveDefinitionAs ) {
				saveDefinitionAs();
			} else if (source == this.menuOpenData) {
				openData();
			} else if (source == this.menuSaveData ) {
				saveData();
			} else if (source == this.menuSaveDataAs ) {
				saveDataAs();
			} else if (source == this.menuAddEntry ) {
				addDataEntry();
			} else if (source == this.menuDeleteEntry ) {
				deleteDataEntry();
			} else if (source == this.menuMoveDownEntry ) {
				moveDownDataEntry();
			} else if (source == this.menuMoveUpEntry ) {
				moveUpDataEntry();
			} else if (source == this.menuAddType ) {
				addDataType();
			} else if (source == this.menuGenerateCode ) {
				generateCode();
			} else if (source == this.descriptionField ) {
				this.dataManager.setDescription( this.descriptionField.getText() );
			} else if (source == this.extensionField ) {
				this.dataManager.setExtension( this.extensionField.getText() );
				this.extensionField.setText( this.dataManager.getExtension() );
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.statusBar.warn( event.getActionCommand() + " failed: " + e.toString() );
		}
	}
	
	/**
	 * 
	 */
	private void generateCode() {
		if (this.createCodeDialog == null) {
			String className = "DataClass";
			if (this.definitionFile != null) {
				String fileName = this.definitionFile.getName();
				fileName = fileName.substring(0,1).toUpperCase() + fileName.substring( 1 );
				int index = fileName.lastIndexOf('.');
				if (index != -1) {
					className = fileName.substring( 0, index );
				} else {
					className = fileName;
				}
			}
			String pack = "com.company.data";
			if (this.packageName != null) {
				pack = this.packageName;
			}
			this.createCodeDialog = new CreateCodeDialog( this, "Generate Code", pack, className );
		}
		this.createCodeDialog.setVisible( true );
		if (this.createCodeDialog.okPressed()) {
			String className = this.createCodeDialog.getClassName();
			this.packageName = this.createCodeDialog.getPackageName();
			String code = this.dataManager.generateJavaCode( this.packageName, className );
			CodeEditor codeEditor = new CodeEditor( className + ".java", code, this.icon );
			codeEditor.setVisible(true);
		}
	}

	/**
	 * 
	 */
	private void moveUpDataEntry() {
		int row = this.dataView.getSelectedRow();
		if (row != -1) {
			if (this.dataManager.pushUpDataEntry(row)) {
				this.dataTableModel.refresh( this.dataView );
				this.dataView.setRowSelectionInterval(row - 1, row - 1 );
			}
		}
	}

	/**
	 * 
	 */
	private void moveDownDataEntry() {
		int row = this.dataView.getSelectedRow();
		if (row != -1) {
			if (this.dataManager.pushDownDataEntry(row)) {
				this.dataTableModel.refresh( this.dataView );
				this.dataView.setRowSelectionInterval(row + 1, row + 1 );
			}
		}
	}

	/**
	 * 
	 */
	private void deleteDataEntry() {
		int row = this.dataView.getSelectedRow();
		if (row != -1) {
			this.dataManager.removeDataEntry(row);
			this.dataTableModel.refresh( this.dataView );
			if (this.dataManager.getNumberOfEntries() > 0) {
				this.dataView.setRowSelectionInterval(row -1, row -1);
			}
		}
	}

	/**
	 * 
	 */
	private void addDataType() {
		CreateTypeDialog dialog = new CreateTypeDialog( this, "Add a custom type", this.dataManager );
		dialog.setVisible(true);
		DataType newType = dialog.getDataType();
		if (newType != null ) {
			this.dataManager.addDataType(newType);
			this.dataView.updateTypes(this.dataManager);
			this.dataTableModel.refresh( this.dataView );
		}
	}

	/**
	 * 
	 */
	private void saveAll() {
		if (this.definitionFile != null) {
			saveDefinition();
			saveData();
		} else { 
			saveDefinitionAs();
			saveDataAs();
		}
		
	}

	/**
	 * 
	 */
	private void newDefinition() {
		this.dataManager.clear();
		this.descriptionField.setText( null );
		this.extensionField.setText( null );
		DataEntry entry = new DataEntry( "noname", DataType.BYTE );
		this.dataManager.addDataEntry( entry );
		this.dataTableModel.refresh( this.dataView );
		this.definitionFile = null;
		this.dataFile = null;
		updateTitle();
	}

	/**
	 * 
	 */
	private void addDataEntry() {
		DataEntry entry = new DataEntry( "noname", DataType.BYTE );
		this.dataManager.addDataEntry( entry );
		if (this.dataView.isEditing()) {
			this.dataView.getCellEditor().stopCellEditing();
		}
		this.dataTableModel.refresh( this.dataView );
		this.dataView.setRowSelectionInterval( this.dataManager.getNumberOfEntries() -1, this.dataManager.getNumberOfEntries() -1);
	}

	/**
	 * 
	 */
	private void saveDefinition() {
		if (this.definitionFile == null) {
			saveDefinitionAs();
			return;
		} 
		try {
			this.dataManager.setDescription( this.descriptionField.getText() );
			this.dataManager.setExtension( this.extensionField.getText() );
			this.dataManager.saveDefinition(this.definitionFile);
			this.statusBar.message( "saved " + this.definitionFile.getName() );
			updateTitle();
		} catch (Exception e) {
			showErrorMessage( e );
		}
	}

	/**
	 * 
	 */
	private void saveDefinitionAs() {
		File file = openFile( ".definition", false );
		if (file != null ) {
			try {
				this.dataManager.setDescription( this.descriptionField.getText() );
				this.dataManager.setExtension( this.extensionField.getText() );
				this.dataManager.saveDefinition( file );
				this.definitionFile = file;
				this.statusBar.message("saved " + file.getName() );
				updateTitle();
			} catch (Exception e) {
				showErrorMessage( e );
			}
		}
	}

	private void openDefinition() {
		File file = openFile( ".definition", true );
		openDefinition( file );
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#openDocument(java.io.File)
	 */
	public void openDocument(File file) {
		//this.statusBar.setText("Opening file " + file.getName() );
		if ( this.definitionFile == null || file.getName().endsWith(".definition")) {
			openDefinition( file );
		} else {
			openData( file );
		}
	}
	
	public void openDefinition( File file ) {
		if (file != null) {
			try {
				this.dataManager.loadDefinition(file);
				this.dataView.updateTypes(this.dataManager);
				this.dataTableModel.refresh( this.dataView );
				this.definitionFile = file;
				this.dataFile = null;
				this.descriptionField.setText( this.dataManager.getDescription() );
				this.extensionField.setText( this.dataManager.getExtension() );
				updateTitle();
				this.statusBar.message("Loaded " + file.getName() );
				this.createCodeDialog = null;
			} catch (Exception e) {
				this.statusBar.warn("Unable to load data: " + e );
				showErrorMessage( e );
			}
		}
	}

	private void saveData() {
		if (this.dataFile == null) {
			saveDataAs();
			return;
		}
		try {
			this.dataManager.saveData( this.dataFile );
			this.statusBar.message("saved " + this.dataFile.getName()  + " (" + this.dataFile.length() + " bytes)");
			updateTitle();
		} catch (Exception e) {
			showErrorMessage( e );
		}
	}
	
	private void saveDataAs() {
		File file = openFile( null, false );
		if (file != null) {
			try {
				this.dataManager.saveData(file);
				this.dataFile = file;
				this.statusBar.message("saved " + file.getName() + " (" + file.length() + " bytes)");
				updateTitle();
			} catch (Exception e) {
				showErrorMessage( e );
			}
		}
	}
	
	private void openData() {
		File file = openFile( this.extensionField.getText(), true );
		openData( file );
	}

	public void openData( File file) {
		if (file != null) {
			try {
				this.dataManager.loadData(file);
				this.dataFile = file;
				this.dataTableModel.refresh( this.dataView );
				updateTitle();
				this.statusBar.message("Loaded " + file.getName() );
			} catch (Exception e) {
				this.statusBar.warn("Unable to load data: " + e );
				showErrorMessage( e );
			}
		}
	}

	/**
	 * @param extension
	 * @return
	 */
	private File openFile(String extension, boolean open ) {
		File selectedFile = SwingUtil.openFile( extension, open, this.currentDirectory, this );
		if (selectedFile != null) {
			this.currentDirectory = selectedFile.getParentFile();
		} 
		return selectedFile;
	}
	
	private void updateTitle() {
		String title;
		boolean isDirty = this.dataManager.isDataChanged() || this.dataManager.isDefinitionChanged();
		if (this.definitionFile != null) {
			if (this.dataFile != null) {
				title = "J2ME Polish: Binary Data Editor: " + this.definitionFile.getName();
				if (this.dataManager.isDefinitionChanged()) {
					title += "*";
				}
				title += " - " + this.dataFile.getName();
				if (this.dataManager.isDataChanged()) {
					title += "*";
				}
			} else {
				title = "J2ME Polish: Binary Data Editor: " + this.definitionFile.getName();
				if (isDirty) {
					title += " *";
				}
			}
		} else {
			title = "J2ME Polish: Binary Data Editor";
			if (isDirty) {
				title += " *";
			}
		}
		setWindowDirtyFlag( isDirty );
		setTitle( title );
	}

	public void quit() {
		if (this.dataManager.isDataChanged() || this.dataManager.isDefinitionChanged()) {
			int result = JOptionPane.showConfirmDialog( this, "Should the changed data be saved before exiting?", "Changed Data", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
			if (result == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (result == JOptionPane.YES_OPTION) {
				saveAll();
			}
		}
		super.quit();
	}
	
	protected boolean saveSettings() {
		if (this.definitionFile != null) {
			ArrayList lines = new ArrayList();
			// save start of file:
			lines.add( "<!-- Settings of the J2ME Polish binary editor from " + (new Date()).toString() + " -->" );
			lines.add( "<binaryeditor-settings>" );
			lines.add( "\t<setting" );
			lines.add( "\t\tdefinitionPath=\"" + this.definitionFile.getAbsolutePath() + "\"" );
			if (this.dataFile != null) {
				lines.add( "\t\tdataPath=\"" + this.dataFile.getAbsolutePath() + "\"" );
			}
			if (this.packageName != null) {
				lines.add( "\t\tpackage=\"" + this.packageName + "\"" );
			}
			lines.add( "\t/>");
			lines.add( "</binaryeditor-settings>");
			try {
				String[] textLines = (String[]) lines.toArray( new String[ lines.size() ] );
				FileUtil.writeTextFile( new File(".binaryeditor.settings"), textLines );
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Unable to save settings: " + e.toString() );
			}
		}
		return true;
	} 
	
	private void loadSettings() {
		try {
			File settingsFile = new File(".binaryeditor.settings");
			if (settingsFile.exists()) {
				FileInputStream in = new FileInputStream( settingsFile );
				SAXBuilder builder = new SAXBuilder( false );
				Document document = builder.build( in );
				Element setting = document.getRootElement().getChild("setting");
				if (setting != null) {
					String definitionPath = setting.getAttributeValue("definitionPath");
					if (definitionPath != null) {
						File defFile = new File( definitionPath );
						if (defFile.exists()) {
							this.dataManager.loadDefinition(defFile);
							this.descriptionField.setText( this.dataManager.getDescription() );
							this.extensionField.setText( this.dataManager.getExtension() );
							this.definitionFile = defFile;
							this.currentDirectory = this.definitionFile.getParentFile();
							String dataPath = setting.getAttributeValue("dataPath");
							if (dataPath != null) {
								File file = new File( dataPath );
								if (file.exists()) {
									this.dataManager.loadData(file);
									this.dataFile = file;
								}
							}
						}
					}
					this.packageName = setting.getAttributeValue("package");
					updateTitle();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to load the settings: " + e.toString() );
		}
	}
	
	private void showErrorMessage( Throwable exception ) {
		exception.printStackTrace();
		JOptionPane.showMessageDialog( this, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
	}
	
	public static void main(String[] args) {
		File definitionFile = null;
		File dataFile = null;
		if (args.length > 0) {
			File file = new File( args[0] );
			if (file.exists()) {
				definitionFile = file;
			} else {
				System.out.println("definition-file does not exist: " + args[0]);
			}
			if (args.length > 1) {
				file = new File( args[1] );
				if (file.exists()) {
					dataFile = file;
				} else {
					System.out.println("data-file does not exist: " + args[1]);
				}
			}
		}
		SwingDataEditor editor = new SwingDataEditor( definitionFile, dataFile, true );
		editor.setVisible( true );
	}

		
	class CustomFileFilter extends FileFilter {
		String type;
		public CustomFileFilter( String type ) {
			this.type = type;
		}
		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith( this.type );
		}
		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		public String getDescription() {
			return this.type;
		}	
	}

	/**
	 * Sets the status bar message.
	 * 
	 * @param message the message which should be shown on the status bar
	 */
	public void setStatusBarMessage(String message) {
		this.statusBar.message( message );
	}

	/**
	 * Sets the status bar message.
	 * 
	 * @param message the message which should be shown on the status bar
	 */
	public void setStatusBarWarning(String message) {
		this.statusBar.warn( message );
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.dataeditor.DataEditorUI#signalChangedData()
	 */
	public void signalChangedData() {
		updateTitle();		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.dataeditor.DataEditorUI#signalChangedDefinition()
	 */
	public void signalChangedDefinition() {
		updateTitle();		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.dataeditor.DataEditorUI#signalUnchangedData()
	 */
	public void signalUnchangedData() {
		updateTitle();		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.dataeditor.DataEditorUI#signalUnchangedDefinition()
	 */
	public void signalUnchangedDefinition() {
		updateTitle();		
	}

}
