/*
 * Created on 08-Jun-2004 at 15:00:28.
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

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

/**
 * <p>Let the user choose where to install or where to find a resource.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        08-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ChooseTargetPanel extends IzPanel {
	private static final long serialVersionUID = -7966526000206872182L;
	private FilePropertyPanel polishHomePanel;
	private boolean isWindowsVistaOrWindows7;
	private boolean vistaBatchFileCreated;
	private String vistaBatchFilePath;

	/**
	 * @param parent
	 * @param idata
	 */
	public ChooseTargetPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
		
//		System.err.println("choose target, existing install path=" + idata.getInstallPath());
//		if (idata.getInstallPath() == null) {
//			setDefaultPath( parent, idata );
//		}
		
		JPanel subPanel = new JPanel( new BorderLayout() );
				
	    JLabel title = new JLabel("Installation Directory");
	    title.setFont( title.getFont().deriveFont( title.getFont().getSize() * 2F ));
	    subPanel.add( title, BorderLayout.NORTH );
	    
	    JTextArea area = new JTextArea( "Please select the destination directory into which to install J2ME Polish.\nIf you already have installed a previous J2ME Polish version in this location, the installer will preserve your custom device definitions, global.properties and license keys.\n");
	    area.setEditable( false );
	    area.setLineWrap( true );
	    area.setBackground( title.getBackground() );
	    area.setWrapStyleWord(true);
	    JLabel label = new JLabel("hello");
	    area.setFont( label.getFont() );
	    subPanel.add( area, BorderLayout.CENTER );
	    
	    this.polishHomePanel = new FilePropertyPanel( parent, "polish.home: ", idata.getInstallPath(), "Choose...", true, false );
	    subPanel.add( this.polishHomePanel, BorderLayout.SOUTH );

	    setLayout( new BorderLayout() );
	    add( subPanel, BorderLayout.NORTH );
	    String osName = System.getProperty("os.name");
	    boolean isWindowsVista = osName != null && osName.indexOf("Vista") != -1;
	    boolean isWindows7 = osName != null && osName.indexOf("Windows 7") != -1;
	    this.isWindowsVistaOrWindows7 = isWindowsVista || isWindows7;
	    
	    if (this.isWindowsVistaOrWindows7) {
	    	this.vistaBatchFileCreated = createVistaOrWindows7InstallerBatchFile();
	    	if (this.vistaBatchFileCreated) {
	    		JLabel vistaTitle;
	    		if (isWindows7) {
		    		vistaTitle = new JLabel("Windows 7 Installation");
		    		area = new JTextArea( "Please note:\n"
		    				+ "On Windows 7 (tm) you need to run the installer with administrator rights if you want to install J2ME Polish to a restricted location like C:\\Program Files\\J2ME-Polish.\n"
		    				+ "A batch file for this purpose has been created at " + this.vistaBatchFilePath + ".\n"
		    				+ "Right click the bat file and choose \"Run as administrator\" from the popup context menu.\n\n"
		    				+ "You can install J2ME-Polish to a user directory like \"" + System.getProperty("user.home") + "\\J2ME-Polish\" without needing administrator rights."
		    				);	    			
	    		} else {
		    		vistaTitle = new JLabel("Windows Vista Installation");
		    		area = new JTextArea( "Please note:\n"
		    				+ "On Windows Vista (tm) you need to run the installer with administrator rights if you want to install J2ME Polish to a restricted location like C:\\Program Files\\J2ME-Polish.\n"
		    				+ "A batch file for this purpose has been created at " + this.vistaBatchFilePath + ".\n"
		    				+ "Right click the bat file and choose \"Run as administrator\" from the popup context menu.\n\n"
		    				+ "You can install J2ME-Polish to a user directory like \"" + System.getProperty("user.home") + "\\J2ME-Polish\" without needing administrator rights."
		    				);
	    		}
	    		vistaTitle.setFont( title.getFont() );
	    	    area.setEditable( false );
	    	    area.setLineWrap( true );
	    	    area.setBackground( title.getBackground() );
	    	    area.setWrapStyleWord(true);
	    	    area.setFont( label.getFont() );
	    	    subPanel = new JPanel( new BorderLayout() );
	    	    subPanel.add( vistaTitle, BorderLayout.NORTH );
	    	    subPanel.add( area, BorderLayout.CENTER );
	    	    add( subPanel, BorderLayout.CENTER );
	    	}
	    }
	}
	
	/**
	 * @return true when a batch has been created or when it already exists
	 */
	private boolean createVistaOrWindows7InstallerBatchFile()
	{
    	try {
	    	URL url = getClass().getResource("/com/izforge/izpack/panels/polish.properties");
	    	String jarPath = url.getPath();
	    	if (!jarPath.startsWith("file:/")) {
	    		return false;
	    	}
    		int exclamationMarkPos = jarPath.indexOf('!');
    		if (exclamationMarkPos == -1) {
    			return false;
    		}
    		jarPath = jarPath.substring("file:/".length(), exclamationMarkPos  );
    		File batchFile = new File( jarPath.substring( 0, jarPath.length() - ".jar".length() ) + ".bat" );
    		if (batchFile.exists()) {
    			return true;
    		}
    		FileUtil.writeTextFile(batchFile, new String[]{ "java -jar \"" + jarPath + "\""} );
    		this.vistaBatchFilePath = batchFile.getAbsolutePath();
			return true;
    	} catch (Exception e) {
    		System.err.println("Unable to check/create batch file: " + e);
    		e.printStackTrace();
    		return false;
    	}
	}

	private void setDefaultPath(InstallerFrame parent, InstallData idata) {
		Preferences prefs = Preferences.userRoot().node( "J2ME-Polish" );
		String polishHome = prefs.get("polish.home",  null );
		String polishBase = prefs.get("polish.base", null);
		if (polishBase != null) {
			// polish basic install location is known...
			idata.setVariable("polish.base", polishBase );
			idata.setInstallPath( polishBase + idata.info.getAppVersion() );
		} else if (polishHome != null) {
			// previous polish install location is known...
			int index = polishHome.length() - 1;
			char c = polishHome.charAt(index);
			while (Character.isDigit(c) || c == '.') {
				index--;
				c = polishHome.charAt(index);
			}
			polishHome = polishHome.substring(0, index + 1);
			idata.setVariable("polish.base", polishHome );
			idata.setInstallPath( polishHome + idata.info.getAppVersion() );
		} else if (this.isWindowsVistaOrWindows7) {
			idata.setVariable("polish.base", System.getProperty("user.home") + "\\J2ME-Polish" );
			idata.setInstallPath( System.getProperty("user.home") + "\\J2ME-Polish"  + idata.info.getAppVersion() );
		}
	}

	/** Called when the panel becomes active. */
    public void panelActivate()
    {
        // Resolve the default for chosenPath
        super.panelActivate();
        setDefaultPath(this.parent, this.idata);
        this.polishHomePanel.setValue( this.idata.getInstallPath() );
    }

    

    /**
     * Indicates whether the panel has been validated or not.
     * 
     * @return Whether the panel has been validated or not.
     */
    public boolean isValidated()
    {
        // Standard behavior of PathInputPanel.
    	File installDir = this.polishHomePanel.getValueFile();
        try {
        	if (!installDir.exists()) {
        		installDir.mkdirs();
        	}
        	// check if I can write to the install dir:
        	File testFile = new File( installDir, ".test" );
        	FileOutputStream out = new FileOutputStream( testFile );
        	out.write( new byte[]{12,12,45,55} );
        	out.flush();
        	out.close();
        	testFile.delete();
        	System.err.println("validated: path " + installDir.getAbsolutePath() + " is writable.");
        } catch (Exception e) {
        	e.printStackTrace();
        	emitError(this.parent.langpack.getString("installer.error"), getI18nStringForClass(
                    "notwritable", "TargetPanel"));
            return false;
        }
        this.idata.setInstallPath( installDir.getAbsolutePath() );
        return true;
    }
    
    public void panelDeactivate()
    {
    	File installDir = this.polishHomePanel.getValueFile();
    	try {
        	boolean installDirCreated = false;
        	if (!installDir.exists()) {
        		installDir.mkdirs();
        		installDirCreated = true;
        	}
        	// backup:
        	if (!installDirCreated) {
        		// check for existing device database customizations, license keys and global.properties:
        		File backupDir = new File( installDir.getAbsolutePath() + "_Backup" + System.currentTimeMillis() );
        		backupDir.mkdir();
        		int backupedFiles = 0;
        		File[] files = installDir.listFiles();
        		for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (!file.isDirectory() && file.getName().startsWith("custom-")) {
						FileUtil.copy( file, backupDir );
						backupedFiles++;
					}
				}
        		// check license keys:
        		File licenseKey = new File( installDir, "license.key" );
        		if (licenseKey.exists()) {
        			FileUtil.copy( licenseKey, backupDir );
        			backupedFiles++;
        		}
        		// check global.properties:
        		File globalPropertiesFile = new File( installDir, "global.properties");
        		if (globalPropertiesFile.exists()) {
        			Map globalProperties = FileUtil.readPropertiesFile( globalPropertiesFile );
        			this.idata.setAttribute("global.properties",  globalProperties );
        		}
        		if (backupedFiles > 0) {
        			this.idata.setAttribute("polish.backup",  backupDir );
        		} else {
        			backupDir.delete();
        		}
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        	emitError(this.parent.langpack.getString("installer.error"), getI18nStringForClass(
                    "notwritable", "TargetPanel"));
            return;
        }
        this.idata.setInstallPath( installDir.getAbsolutePath() );
    }
	

}
