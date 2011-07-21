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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
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
public class SetPropertiesPanel extends IzPanel {
	private static final long serialVersionUID = -375985673990148819L;
	private boolean isMacOsX;
	private FilePropertyPanel[] propertyPanels;
	private boolean activated;
	private GridBagLayout gridBagLayout;
	private boolean labelWidthsSet;

	/**
	 * @param parent
	 * @param idata
	 */
	public SetPropertiesPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
		this.isMacOsX = (System.getProperty("mrj.version") != null);
		
		this.gridBagLayout = new GridBagLayout();
		
	}
	
	
	/** Called when the panel becomes active. */
    public void panelActivate()
    {
        // Resolve the default for chosenPath
        super.panelActivate();
        
        if (this.activated) {
        	return;
        }
        
        ArrayList paths = new ArrayList(3);
 		File installPath = (new File(this.idata.getInstallPath() )).getParentFile();
		paths.add( installPath );
        
		PathInputPanel.loadDefaultInstallDir( this.parent,  this.idata);
		String defaultInstallDir = PathInputPanel.getDefaultInstallDir();
		if (defaultInstallDir != null) {
			File defaultPath = (new File( defaultInstallDir ) ).getParentFile();
			if (!defaultPath.equals( installPath ) ) {
				paths.add( defaultPath );
			}
		}
		String userHome = System.getProperty("user.home");
		if (userHome == null) {
			userHome = System.getProperty("user.dir");
		}
		if (userHome != null) {
			File home = new File( userHome );
			if (!home.equals( installPath )) {
				paths.add( home );
			}
		}
		if (File.separatorChar == '\\' ) { // this is windows, add C:
			paths.add( new File( "C:\\"));
    	}	
		File[] lookupPaths = (File[]) paths.toArray( new File[ paths.size() ] );
		
		//Map propertiesMap = null;
		String[] propertyLines = null;
		InputStream in = getClass().getResourceAsStream("/com/izforge/izpack/panels/polish.properties");
		if (in == null) {
			System.out.println("Error: no property definitions found!");
        	emitError(this.parent.langpack.getString("installer.error"), "Unable to load global.properties: property definitions not found!"  );
        	this.parent.lockNextButton();
        	return;
		} else {
			try {
				propertyLines = FileUtil.readTextLines(in);
				//propertiesMap = FileUtil.readProperties(in);
			} catch (IOException e) {
				e.printStackTrace();
	        	emitError(this.parent.langpack.getString("installer.error"), "Unable to load global.properties: " + e.toString() );
	        	return;
			}
		}
		Map globalProperties = (Map) this.idata.getAttribute("global.properties");
		ArrayList panelsList = new ArrayList();
		addPropertyPanels( this.parent, propertyLines, globalProperties, panelsList, lookupPaths );
		if (globalProperties != null) {
			// add remaining global properties, if there are any:
			addPropertyPanels( this.parent, globalProperties, panelsList );
		}
		JComponent[] components = (JComponent[]) panelsList.toArray( new JComponent[ panelsList.size() ] );
		JPanel propertiesPanel =  new JPanel( new GridLayout( components.length, 1 ) );
		ArrayList filePropertyPanelsList = new ArrayList();
		for (int i = 0; i < components.length; i++) {
			JComponent component = components[i];
			if (component instanceof FilePropertyPanel) {
				FilePropertyPanel fpPanel = (FilePropertyPanel) component;
				filePropertyPanelsList.add( fpPanel );
			}
			propertiesPanel.add( component );			
		}
		FilePropertyPanel[] filePropertyPanels = (FilePropertyPanel[]) filePropertyPanelsList.toArray( new FilePropertyPanel[ filePropertyPanelsList.size() ] );
		this.propertyPanels = filePropertyPanels;
		
		JPanel subPanel = new JPanel( new BorderLayout() );
	    JLabel title = new JLabel("Paths to Emulators, SDKs and IDEs");
	    title.setFont( title.getFont().deriveFont( title.getFont().getSize() * 2F ));
	    subPanel.add( title, BorderLayout.NORTH );
	    String message = "Please specify the paths to your emulators and SKDs that you are using. These paths will be written to ${polish.home}/global.properties.\n";
	    if (this.isMacOsX) {
	    	message += "On Mac OS X you should install the Mobile Power Player SDK (mpp), which you can download at http://sdk.mpowerplayer.com. In that case the Wireless Toolkit is not needed.\n";
	    }

	    JTextArea area = new JTextArea( message );
	    area.setEditable( false );
	    area.setLineWrap( true );
	    area.setWrapStyleWord(true);
	    JLabel label = new JLabel("hello");
	    area.setFont( label.getFont() );
	    area.setBackground( title.getBackground() );
	    subPanel.add( area, BorderLayout.CENTER );
	    
	    JScrollPane scrollPane = new JScrollPane( propertiesPanel );
	    scrollPane.setPreferredSize( new Dimension( 200, 450 ));
	    subPanel.add( scrollPane, BorderLayout.SOUTH );

	    setLayout( new BorderLayout() );
	    add( subPanel, BorderLayout.NORTH );
	    
	    // layout the labels of the FilePropertyPanels:
		int maxWidth = 0;
		FilePropertyPanel[] panels = this.propertyPanels;
		for (int i = 0; i < panels.length; i++) {
			FilePropertyPanel fpPanel = panels[i];
			int width = fpPanel.getLabelWidth();
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		if (maxWidth > 0) {
			this.labelWidthsSet = true;
			for (int i = 0; i < panels.length; i++) {
				FilePropertyPanel fpPanel = panels[i];
				fpPanel.setLabelWidth(maxWidth);
			}
		}

	    
	    this.activated = true;
    }
    
    
//	
//	
//	public void paint(Graphics g) {
//		if (!this.labelWidthsSet) {
//			int maxWidth = 0;
//			FilePropertyPanel[] panels = this.propertyPanels;
//			for (int i = 0; i < panels.length; i++) {
//				FilePropertyPanel fpPanel = panels[i];
//				int width = fpPanel.getLabelWidth();
//				if (width > maxWidth) {
//					maxWidth = width;
//				}
//			}
//			System.err.println("paint: maxWidth=" + maxWidth);
//			if (maxWidth > 0) {
//				this.labelWidthsSet = true;
//				for (int i = 0; i < panels.length; i++) {
//					FilePropertyPanel fpPanel = panels[i];
//					fpPanel.setLabelWidth(maxWidth);
//				}
//			}
//		}
//		super.paint(g);
//	}


	private void addPropertyPanels(InstallerFrame parent, Map propertiesMap, ArrayList panelsList) {
		String[] keys = (String[]) propertiesMap.keySet().toArray( new String[ propertiesMap.size() ] );
		Arrays.sort( keys );
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = (String) propertiesMap.get( key );
			FilePropertyPanel panel = new FilePropertyPanel( parent, key + ": ", null, new String[]{value}, "Choose...", false, true, this.gridBagLayout  );
			panel.setPropertyName( key );
			panelsList.add( panel );
		}
	}


	private void addPropertyPanels(InstallerFrame parent, String[] propertyLines, Map knownProperties, ArrayList panelsList, File[] lookupPaths) {
		//String[] keys = (String[]) propertiesMap.keySet().toArray( new String[ propertiesMap.size() ] );
		//Arrays.sort( keys );
		for (int i = 0; i < propertyLines.length; i++) {
			String line = propertyLines[i].trim();
			if (line.length() == 0 || line.startsWith("#")) {
				// ignore comments:
				continue;
			}
			int splitPos = line.indexOf('=');
			if (splitPos == -1) {
				if ("separator".equals(line)) {
					panelsList.add( new JSeparator() );
				}
				continue;
			}
			String key = line.substring( 0, splitPos );
			String value = line.substring(splitPos + 1);
			String knownValue = System.getProperty(key);
			if (knownValue == null && knownProperties != null) {
				knownValue = (String) knownProperties.remove( key );
			}
			addPropertyPanel( parent, key, value, knownValue, lookupPaths, panelsList );
		}
		
	}


	private void addPropertyPanel(InstallerFrame parent, String key, String value, String knownValue, File[] lookupPaths, ArrayList panelsList) {
		int semicolonPos = value.indexOf(';');
		String description = null;
		if (semicolonPos != -1) {
			description = value.substring(0, semicolonPos);
			value = value.substring( semicolonPos + 1 );
		}
		String[] values = getMatches( value, knownValue, lookupPaths );
		String label = key + ": ";
		FilePropertyPanel panel = new FilePropertyPanel( parent, description, label, values, "Choose...", true, true, this.gridBagLayout  );
		panel.setPropertyName( key );
		panelsList.add( panel );
	}

	private String[] getMatches(String value, String knownValue, File[] lookupPaths) {
		
		ArrayList matchesList = new ArrayList();
		String[] values = StringUtil.split(value, ':');
		for (int i = 0; i < values.length; i++) {
			String pathStart = values[i];
			String nameStart = null;
			if  (pathStart.charAt( pathStart.length() - 1) == '*') {
				int slashPos = pathStart.indexOf('/');
				if (slashPos == -1) {
					nameStart = pathStart.substring(0, pathStart.length() -1);
					pathStart = "";
				} else {
					nameStart = pathStart.substring( slashPos + 1, pathStart.length() -1);
					pathStart = pathStart.substring( 0, slashPos );
				}
			}
			for (int j = 0; j < lookupPaths.length; j++) {
				File lookupPath = lookupPaths[j];
				addMatches( pathStart, nameStart, lookupPath, matchesList );				
			}
			
		}
		// sort results, but knownValue as the first result (if defined):
		int length = matchesList.size();
		int start = 0;
		if (knownValue != null) {
			length += 1;
			start = 1;
		}
		String[] results = new String[ length ];
		Object[] matches = matchesList.toArray();
		Arrays.sort( matches );
		for (int i = matches.length - 1; i >= start; i--) {
			results[i] = (String) matches[i];
		}
		if (knownValue != null) {
			results[0] = knownValue;
		}
		return results;
	}

	private void addMatches(String pathStart, String nameStart, File lookupPath, ArrayList matchesList) {
		//System.err.println("addMatches: pathStart=" + pathStart + ", nameStart=" + nameStart + ", lookupPath=" + lookupPath.getAbsolutePath() );
		if (lookupPath == null || pathStart == null || matchesList == null) {
			System.err.println("SetPropertiesPanel: Unable to add matches for pathStart=" + pathStart + ", nameStart=" + nameStart  + ", matchesList=" + matchesList );
			return;
		}
		File home = new File( lookupPath, pathStart );
		if (!home.exists()) {
			return;
		}
		if (nameStart == null) {
			matchesList.add( home.getAbsolutePath() );
			return;
		}
		File[] files = home.listFiles();
		if (files == null) {
			System.err.println("no files in " + home.getAbsolutePath() + ": exists=" + home.exists());
			return;
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().startsWith( nameStart )) {
				matchesList.add( file.getAbsolutePath() );
			}
		}
	}
    

	
    /**
     * This method is called when the panel gets desactivated, when the user switches to the next
     * panel. By default it doesn't do anything.
     */
    public void panelDeactivate()
    {
    	Map globalProperties = readProperties();
    	this.idata.setAttribute("global.properties", globalProperties );
    	
//        try {
//        	File globalPropertiesFile = new File( this.idata.getInstallPath() + File.separatorChar + "global.properties" );
//        	Map globalProperties = readProperties();
//        	FileUtil.writePropertiesFile(globalPropertiesFile, globalProperties );
//        } catch (Exception e) {
//        	e.printStackTrace();
//        	emitError(this.parent.langpack.getString("installer.error"), "Unable to write global.properties to " + this.idata.getInstallPath() + ": " + e.toString() );
//        }
    }

    /**
     * Gets the properties from the input panels.
     * 
     * @return a map containing all defined properties.
     */
	private Map readProperties() {
		boolean isWindows = File.separatorChar == '\\';
		FilePropertyPanel[] panels = this.propertyPanels;
		HashMap properties = new HashMap( panels.length );
		for (int i = 0; i < panels.length; i++) {
			FilePropertyPanel panel = panels[i];
			String value = panel.getValueString();
			if (value != null && value.length() > 0) {
				if (isWindows) {
					value = StringUtil.replace(value, "\\\\", "/");
					value = value.replace('\\', '/' );
				}
				properties.put( panel.getPropertyName(), value );
			} else {
				properties.put( "# " + panel.getPropertyName(), "(please define if needed)" );
			}
		}
		// check for install path of the netbeans module:
		String netbeansHomePath = (String) properties.get("netbeans.home");
		if (netbeansHomePath != null) {
			boolean netbeansSuccess = false;
			File netbeansHome = new File( netbeansHomePath );
			if ( this.isMacOsX && netbeansHomePath.endsWith(".app")) {
				netbeansHome = new File( netbeansHome, "Contents/Resources/NetBeans");
			}
			if (netbeansHome.exists() && netbeansHome.canWrite()) {
				File[] files = netbeansHome.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (file.isDirectory() && file.getName().startsWith("nb")) {
						// found correct installation directory:
						File autoupdateDir = new File( file, "update/download");
						autoupdateDir.mkdirs();
						this.idata.setVariable("NETBEANS_INSTALL_HOME", autoupdateDir.getAbsolutePath() );
						netbeansSuccess = true;
						break;
					}
				}
			}
			if (!netbeansSuccess) {
				System.err.println("netbeans.home is not writeable, so the NetBeans module cannot be installed automatically: " + netbeansHomePath);
			}
		}
		// check for install path of the mepose plugin in eclipse:
		String eclipseHomePath = (String) properties.get("eclipse.home");
		if (eclipseHomePath != null) {
			File eclipseHome = new File( eclipseHomePath );
			if (eclipseHome.exists() && eclipseHome.canWrite()) {
				this.idata.setVariable("ECLIPSE_INSTALL_HOME", eclipseHome.getAbsolutePath() );
				//System.err.println("Setting ECLIPSE_INSTALL_HOME = " + eclipseHome.getAbsolutePath());
			} else {
				System.err.println("eclipse.home is not writable, so the Eclipse plugin cannot be installed automatically: " + eclipseHomePath);
			}
		}
		
		return properties;
	}


}
