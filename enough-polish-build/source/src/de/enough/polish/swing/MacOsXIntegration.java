/*
 * Created on Dec 3, 2004 at 6:15:06 PM.
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
package de.enough.polish.swing;

import java.io.File;

import javax.swing.JMenu;

import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;


/**
 * <p>Integrates Mac OS X for a swing application.</p>
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        Dec 3, 2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */

public class MacOsXIntegration 
extends NativeIntegration 
implements ApplicationListener 
{
	
	protected Application parent;

	/**
	 * Creates a new integration.
	 */
	protected MacOsXIntegration() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.NativeIntegration#init(javax.swing.JFrame)
	 */
	public void init(Application parentApplication, String applicationName) {
		this.parent = parentApplication;
		/* has no effect anyhow 
		if (applicationName != null ) {
			System.setProperty("Xdock:name", applicationName );
		}
		*/
		// move menu to screenbar:
		System.setProperty( "apple.laf.useScreenMenuBar", "true");
		System.setProperty( "com.apple.macos.useScreenMenuBar", "true");
		// all butons should be fully visible:
		System.setProperty( "com.apple.mrj.application.growbox.intrudes", "false" );
		// register this class for handling Mac OS X application events:
		com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
		app.addApplicationListener( this );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.NativeIntegration#createMenu()
	 */
	public JMenu createMenu() {
		// TODO Robert implement MacOsXIntegration.createMenu
		return null;
	}

	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handleOpenApplication(com.apple.eawt.ApplicationEvent)
	 */
	public void handleOpenApplication(ApplicationEvent event) {
		this.parent.openApplication();
		event.setHandled( true );
	}

	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handleOpenFile(com.apple.eawt.ApplicationEvent)
	 */
	public void handleOpenFile(ApplicationEvent event) {
		File file = new File( event.getFilename() );
		this.parent.openDocument( file );
		event.setHandled( true );
	}

	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handlePreferences(com.apple.eawt.ApplicationEvent)
	 */
	public void handlePreferences(ApplicationEvent event) {
		this.parent.preferences();	
	}

	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handlePrintFile(com.apple.eawt.ApplicationEvent)
	 */
	public void handlePrintFile(ApplicationEvent event) {
		// TODO Robert implement MacOsXIntegration.handlePrintFile
		
	}

	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handleQuit(com.apple.eawt.ApplicationEvent)
	 */
	public void handleQuit(ApplicationEvent event) {
		this.parent.quit();
		event.setHandled( true );
	}

	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handleReOpenApplication(com.apple.eawt.ApplicationEvent)
	 */
	public void handleReOpenApplication(ApplicationEvent event) {
		// TODO Robert implement MacOsXIntegration.handleReOpenApplication
		
	}

	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handleAbout(com.apple.eawt.ApplicationEvent)
	 */
	public void handleAbout(ApplicationEvent event) {
		this.parent.about();
		event.setHandled( true );
	}
	
}
