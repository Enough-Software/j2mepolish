/*
 * Created on 08-Jan-2007 at 15:00:28.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.izforge.izpack.Pack;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;

/**
 * <p>Installs the application data.</p>
 *
 * <p>copyright Enough Software 2007</p>
 * <pre>
 * history
 *        08-Jan-2007 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class CustomInstallPanel 
extends InstallPanel
implements Runnable
{

	private boolean isActivated;
	private JLabel deletingInfoLabel;
	private JLabel installationFinishInfoLabel;

	/**
	 * @param parent
	 * @param idata
	 */
	public CustomInstallPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.izforge.izpack.panels.InstallPanel#panelActivate()
	 */
	public void panelActivate() {
		if (isPackSelected("thirdparty-smaato-soma")) {
			// show another window with the HTML content:
			JTextPane tp = new JTextPane();
			tp.setEditable(false);
			tp.setBorder( new EmptyBorder( 20, 20, 20, 20 ));
			JScrollPane js = new JScrollPane();
			js.getViewport().add(tp);
			JFrame jf = new JFrame();
			jf.getContentPane().add(js);
			jf.pack();
			jf.setSize(800,800);

			jf.setVisible(true); 

			try {
				URL url = new URL("http://www.smaato.com/download/j2mepolish/after-install.html");
				tp.setPage(url);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		// store variable for substitution in property files - the install path
		// should not use the backslash on Windows systems:
		String installPathProperty = this.idata.getInstallPath();
		installPathProperty = installPathProperty.replace('\\', '/' );
//		System.err.println("1: INSTALL_PATH_PROPERTY=" + this.idata.getVariable("INSTALL_PATH_PROPERTY"));
		//this.idata.setAttribute( "INSTALL_PATH_PROPERTY", installPathProperty );
		this.idata.setVariable( "INSTALL_PATH_PROPERTY", installPathProperty );
//		System.err.println("2: INSTALL_PATH_PROPERTY=" + this.idata.getVariable("INSTALL_PATH_PROPERTY"));
//		this.idata.getVariables().put( "INSTALL_PATH_PROPERTY", installPathProperty );
//		System.err.println("3: INSTALL_PATH_PROPERTY=" + this.idata.getVariable("INSTALL_PATH_PROPERTY"));

		File existingInstallationDir = new File( this.idata.getInstallPath() );
		// store install path to preferences (in windows: registry):
		Preferences prefs = Preferences.userRoot().node( "J2ME-Polish" );
		String versions = prefs.get("polish.versions", null);
		String polishHome = existingInstallationDir.getAbsolutePath();
		String appVersion = this.idata.info.getAppVersion();
		if (versions != null) {
			String[] chunks = StringUtil.split(versions, ',');
			StringBuffer buffer = new StringBuffer();
			buffer.append( appVersion );
			for (int i = 0; i < chunks.length; i++)
			{
				String version = chunks[i];
				String key = "polish.home." + version;
				String path = prefs.get(key, null);
				if (path != null && path.equals(polishHome)) {
					// a previous version has used the same installation path, so remove it:
					prefs.remove( key );
				} else if ( path != null && (new File(path)).exists() ){
					buffer.append(',');
					buffer.append( version );
				}
			}
			versions = buffer.toString();
		} else {
			versions = appVersion;
		}
		prefs.put( "polish.home",  polishHome );
		String polishBase = this.idata.getVariable("polish.base");
		if (polishBase != null) {
			prefs.put( "polish.base",  polishBase );
		}
		prefs.put( "polish.home." + appVersion,  polishHome );
		prefs.put( "polish.versions",  versions );
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			System.err.println("Unable to store polish.home into preferences");
			e.printStackTrace();
		}
		if (existingInstallationDir.exists()) {
			// make a clean installation by removing the existing installation first:
			this.deletingInfoLabel = LabelFactory.create("Please stand by while clearing previous installation...",
		                this.parent.icons.getImageIcon("information"), LEADING);
			add(this.deletingInfoLabel, IzPanelLayout.getDefaultConstraint(FULL_LINE_CONTROL_CONSTRAINT));
			this.installationFinishInfoLabel = LabelFactory.create("Waiting for installation...",
	                this.parent.icons.getImageIcon("information"), LEADING);
			add(this.installationFinishInfoLabel, IzPanelLayout.getDefaultConstraint(FULL_LINE_CONTROL_CONSTRAINT));
			Thread thread = new Thread( this );
			thread.start();
		} else {
			this.installationFinishInfoLabel = LabelFactory.create("Waiting for installation...",
	                this.parent.icons.getImageIcon("information"), LEADING);
			add(this.installationFinishInfoLabel, IzPanelLayout.getDefaultConstraint(FULL_LINE_CONTROL_CONSTRAINT));

			super.panelActivate();
		}
	}




	public void stopAction()
    {
		// restore backup and write global.properties:
	    // now write global properties:
	    Map globalProperties = (Map) this.idata.getAttribute("global.properties");
	    if (globalProperties == null) {
	    	System.err.println("Unable to store global.properties - no properties found!");
	    } else {
	        try {
	        	if (isPackSelected("sources")) {
	        		globalProperties.put("#polish.client.source", this.idata.getInstallPath() + "/j2mepolish-src/j2me/src/    (uncomment if you want to modify J2ME Polish client sources)");
	        	}
	        	File globalPropertiesFile = new File( this.idata.getInstallPath() + File.separatorChar + "global.properties" );
	        	FileUtil.writePropertiesFile(globalPropertiesFile, globalProperties );
				this.installationFinishInfoLabel.setText("Wrote global.properties successfully.");
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	emitError(this.parent.langpack.getString("installer.error"), "Unable to write global.properties to " + this.idata.getInstallPath() + ": " + e.toString() );
	        	this.installationFinishInfoLabel.setText("Unable to write global.properties to " + this.idata.getInstallPath() + ": " + e.toString());
	    	}	
	    }
	    // restore backuped files:
	    File backupDir = (File) this.idata.getAttribute("polish.backup" );
	    boolean isBackupError = false;
	    if (backupDir != null) {
	    	File[] files = backupDir.listFiles();
	    	File target = new File( this.idata.getInstallPath() );
	    	for (int i = 0; i < files.length; i++) {
				File file = files[i];
				try {
					FileUtil.copy(file, target );
					this.installationFinishInfoLabel.setText("Restored backup files from previous installation successfully.");
				} catch (IOException e) {
					e.printStackTrace();
		        	emitError(this.parent.langpack.getString("installer.error"), "Unable to restore backup from " + file.getAbsolutePath() + ": " + e.toString() + "\nPlease restore the backup files manually." );
					this.installationFinishInfoLabel.setText("Unable to restore backup from " + file.getAbsolutePath() + ": " + e.toString() );
		        	isBackupError = true;
				}
			}
	    	if (!isBackupError) {
	    		FileUtil.delete( backupDir );
	    	}
	    }
	    
	    super.stopAction();
	    
//	    this.idata.canClose = true;
//	    
//	    // inform user about installation success:
//		final InstallPanel p = this;
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run()
//            {
//                parent.releaseGUI();
//                parent.lockPrevButton();
//                
//                // With custom actions it is possible, that the current value
//                // is not max - 1. Therefore we use always max for both
//                // progress bars to signal finish state.
//                overallProgressBar.setValue(overallProgressBar.getMaximum());
//                int ppbMax = packProgressBar.getMaximum();
//                if (ppbMax < 1)
//                {
//                    ppbMax = 1;
//                    packProgressBar.setMaximum(ppbMax);
//                }
//                packProgressBar.setValue(ppbMax);
//
//                packProgressBar.setString(parent.langpack.getString("InstallPanel.finished"));
//                packProgressBar.setEnabled(false);
//                try {
//	                String no_of_packs = Integer.toString( ReflectionUtil.getIntField( p, "noOfPacks") );
//	                overallProgressBar.setString(no_of_packs + " / " + no_of_packs);
//	                overallProgressBar.setEnabled(false);
//	                packOpLabel.setText(" ");
//	                packOpLabel.setEnabled(false);
//	                //idata.canClose = true;
//	                ReflectionUtil.setField(p, "validated", true);
//                } catch (NoSuchFieldException e) {
//                	e.printStackTrace();
//                }
//                //validated = true;
//                if (idata.panels.indexOf(this) != (idata.panels.size() - 1)) parent.unlockNextButton();
//            }
//        });
    }



	/**
	 * Checks if the pack with the specified ID has been selected
	 * @param id the id
	 * @return true when it has been selected by the user
	 */
	private boolean isPackSelected(String id)
	{
		List selectedPacks = this.idata.selectedPacks;
		for (int i=0; i<selectedPacks.size(); i++) {
			Pack pack = (Pack) selectedPacks.get(i);
			if (id.equals( pack.id )) {
				return true;
			}
		}
		return false;
	}



	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			File existingInstallationDir = new File( this.idata.getInstallPath() );
			// for some reason I'm not able to delete the installation directory itself - could be
			// that IzPack already holds a reference or something.
			// So instead remove files within the install directory:
			File[] files = existingInstallationDir.listFiles();
//			StringBuffer failuresBuffer = new StringBuffer();
			boolean overallSuccess = true;
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				boolean success = FileUtil.delete(file);
				overallSuccess &= success;
//				if (!success) {
//					failuresBuffer.append( file.getAbsolutePath() + "\n");
//				}
			}
			if (overallSuccess) {
				this.deletingInfoLabel.setText("Previous installation has been removed.");
			} else {
				this.deletingInfoLabel.setText("Previous installation could not be removed. Please make a clean install if you encounter problems.");
				// does only work after manually resizing install window...
//				JTextArea area = new JTextArea( "\nFollowing files/directories could not be removed:\n" + failuresBuffer.toString() );
//				area.setEditable( false );
//				area.setLineWrap( true );
//				area.setBackground( getBackground() );
//			    add( area, IzPanelLayout.getDefaultConstraint(FULL_LINE_CONTROL_CONSTRAINT) );
//			    SwingUtilities.updateComponentTreeUI( this );
			}
		} catch (Exception e) {
			System.err.println("Unable to remove previous installation: " + e.toString() );
			this.deletingInfoLabel.setText("Previous installation could not be removed, please make a clean install if you encounter problems.");
			e.printStackTrace();
		} finally {
			super.panelActivate();
		}
	}


}
