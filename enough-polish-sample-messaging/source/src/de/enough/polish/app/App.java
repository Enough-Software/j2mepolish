/*
 * Created on Mar 13, 2008 at 12:58:57 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.app;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.app.control.Controller;

/**
 * A blank MIDlet that can be used as a starting point for your own apps.
 * <br>Copyright Enough Software 2005-2010
 * <pre>
 * history
 *        Mar 13, 2008 - rickyn creation
 * </pre>
 * @author Richard Nkrumah
 */
public class App 
extends MIDlet
{
	
	private Controller controller;
	
	/**
	 * Creates a new app
	 */
	public App() {
		// nothing to init
	}
	

    protected void startApp(){
    	if (this.controller == null) {
    		this.controller = new Controller( this );
    		this.controller.appStart();
    	} else {
    		this.controller.appContinue();
    	}
    }
    
    protected void pauseApp() {
        this.controller.appPause();
    }

    
	/*
	 * (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		this.controller = null;
	}

	/**
	 * Exits this application
	 */
	public void exit() {
		try {
			destroyApp(true);
		} catch (MIDletStateChangeException e) {
			//#debug error
			System.out.println("Unable to destroyApp" + e);
		}
		notifyDestroyed();
	}


}
