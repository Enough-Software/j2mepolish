/*
 * Created on 17-March-2009 at 16:14:27.
 * 
 * Copyright (c) 2009 Robert Virkus / Enough Software
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
package de.enough.polish.sample;

import java.util.Date;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.util.DeviceControl;
import de.enough.polish.util.Locale;

/**
 * <p>Demonstrates DateField input.</p>
 *
 * <p>Copyright Enough Software 2009</p>

 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DateFieldMidlet extends MIDlet implements CommandListener {
	
	Form menuScreen;
	Command cmdQuit = new Command( Locale.get("cmd.Quit"), Command.EXIT, 10 );
	
	public DateFieldMidlet() {
		super();
		//#debug
		System.out.println("starting DateFieldMidlet");
		//#style mainScreen
		this.menuScreen = new Form("J2ME Polish");
		//#style dateField
		DateField field = new DateField( "Birthdate: ", DateField.DATE);
		this.menuScreen.append( field );
		//#style dateField
		field = new DateField( "Birthtime: ", DateField.TIME);
		this.menuScreen.append( field );
		//#style dateField
		field = new DateField( "Current Time and Date: ", DateField.DATE_TIME);
		field.setDate( new Date() );
		this.menuScreen.append( field );
		
		this.menuScreen.setCommandListener(this);
		this.menuScreen.addCommand( this.cmdQuit );
		
		//#debug
		System.out.println("initialisation done.");
	}

	protected void startApp() throws MIDletStateChangeException {
		//#debug
		System.out.println("setting display.");
		Display display = Display.getDisplay(this);
		display.setCurrent( this.menuScreen );
		//#debug
		System.out.println("sample application is up and running.");
	}

	protected void pauseApp() {
		// ignore
	}
	
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// just quit
	}
	
	public void commandAction(Command cmd, Displayable screen) {		
		if (cmd == this.cmdQuit) {
			quit();
		}
	}
	
	private void quit() {
		notifyDestroyed();
	}
	
	
}
