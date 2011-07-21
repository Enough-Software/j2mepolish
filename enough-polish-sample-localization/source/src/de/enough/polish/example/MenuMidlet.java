/*
 * Created on 04-Apr-2004 at 16:14:27.
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
package de.enough.polish.example;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.util.Debug;
import de.enough.polish.util.Locale;



/**
 * <p>Shows a demonstration of the possibilities of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        04-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MenuMidlet extends MIDlet implements CommandListener {
	
	List menuScreen;
	Command startGameCmd = new Command( Locale.get( "cmd.StartGame" ), Command.ITEM, 8 );
	Command quitCmd = new Command( Locale.get("cmd.Quit"), Command.EXIT, 10 );
	//#ifdef polish.debugEnabled
		Command showLogCmd = new Command( Locale.get("cmd.ShowLog"), Command.ITEM, 9 );
	//#endif
	Display display;
	
	public MenuMidlet() {

		super();
		//#debug
		System.out.println("starting MenuMidlet");
		//#ifdef title:defined
			//#= String title = "${ title }";
		//#else
			String title = "J2ME Polish";
		//#endif
		//#style mainScreen
		this.menuScreen = new List(title, List.IMPLICIT);
		//#style mainCommand
		this.menuScreen.append( Locale.get( "menu.StartGame"), null);
		//#style mainCommand
		this.menuScreen.append(Locale.get( "menu.LoadGame"), null);
		//#style mainCommand
		this.menuScreen.append(Locale.get( "menu.Highscore"), null);
		//#style mainCommand
		this.menuScreen.append(Locale.get( "menu.Quit"), null);
		
		this.menuScreen.setCommandListener(this);
		this.menuScreen.addCommand( this.startGameCmd ); 
		this.menuScreen.addCommand( this.quitCmd );
		//#ifdef polish.debugEnabled
			this.menuScreen.addCommand( this.showLogCmd );
		//#endif
		
		// You can also use further localization features like the following: 
		//System.out.println("Today is " + Locale.formatDate( System.currentTimeMillis() ));
			
			String[] params = new String[] { "hello", "world" };
			System.out.println( Locale.get("sms.send.successfully", params) ); 
		
		//#debug
		System.out.println("initialisation done.");
	}

	protected void startApp() throws MIDletStateChangeException {
		//#debug
		System.out.println("setting display.");
		this.display = Display.getDisplay(this);
		this.display.setCurrent( this.menuScreen );
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
		if (screen == this.menuScreen) {
			//#ifdef polish.debugEnabled
				if (cmd == this.showLogCmd ) {
					Debug.showLog(this.display);
					return;
				}
			//#endif
			if (cmd == List.SELECT_COMMAND) {
				int selectedItem = this.menuScreen.getSelectedIndex();
				switch (selectedItem) {
					case 0: startGame(); break;
					case 1: loadGame(); break;
					case 2: showHighscore(); break;
					default: notifyDestroyed(); 
				}
			} else if (cmd == this.startGameCmd) {
				startGame();
			} else if (cmd == this.quitCmd) {
				quit();
			}
		}
	}
	
	private void startGame() {
		//this.menuScreen.set(0, "Started", null );
		String userName = null;
		//#= userName = "${user.name}";
		Alert alert =  new Alert( "Welcome", Locale.get( "messages.welcome", userName ), null, AlertType.INFO );
		alert.setTimeout( Alert.FOREVER );
		this.display.setCurrent( alert, this.menuScreen );
	}
	
	
	private void loadGame() {
		//#style loadGameAlert
		Alert alert = new Alert( "Sorry", "load game not implemented", null, AlertType.INFO );
		alert.setTimeout( 3000 );
		this.display.setCurrent( alert );
	}
	
	private void showHighscore() {
		Alert alert = new Alert( "Sorry", "highscore not implemented", null, AlertType.INFO );
		alert.setTimeout( Alert.FOREVER );
		this.display.setCurrent( alert, this.menuScreen );
	}
	
	private void quit() {
		notifyDestroyed();
	}
	
}


