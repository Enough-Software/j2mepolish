/*
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
package de.enough.polish.sample.accessible;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

//#ifdef polish.debugEnabled
import de.enough.polish.util.Debug;
//#endif
	
/**
 * <p>Shows a demonstration of the possibilities of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2004 - 2008</p>

 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MenuMidlet extends MIDlet implements CommandListener {
	
	List menuScreen;
	Command startGameCmd = new Command( Locale.get( "cmd.StartGame" ), Command.ITEM, 8 );
	Command quitCmd = new Command( Locale.get("cmd.Quit"), Command.EXIT, 10 );
	Command enableSaveGameCmd = new Command( Locale.get("cmd.EnableSaveGame"), Command.SCREEN, 9 );
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
		//#style mainItem
		this.menuScreen.append( Locale.get( "menu.StartGame"), null);
		//#style mainItem
		this.menuScreen.append(Locale.get( "menu.SaveGame"), null);
		//#style mainItem
		this.menuScreen.append(Locale.get( "menu.Highscore"), null);
		//#style mainItem
		this.menuScreen.append(Locale.get( "menu.Quit"), null);
		
		this.menuScreen.setCommandListener(this);
		this.menuScreen.addCommand( this.startGameCmd ); 
		this.menuScreen.addCommand( this.quitCmd );
		this.menuScreen.addCommand( this.enableSaveGameCmd );
		//#ifdef polish.debugEnabled
			this.menuScreen.addCommand( this.showLogCmd );
		//#endif
		
		// You can also use further localization features like the following: 
		//System.out.println("Today is " + Locale.formatDate( System.currentTimeMillis() ));
		
		//#debug
		System.out.println("initialisation done.");
	}

	protected void startApp() throws MIDletStateChangeException {
		//#debug
		System.out.println("setting display.");
		this.display = Display.getDisplay(this);
		this.display.setCurrent( this.menuScreen );
		//#style fadeOut
		UiAccess.setAccessible(this.menuScreen, 1, false);
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
					case 1:
						UiAccess.setFocusedIndex(this.menuScreen,0);
						//#style fadeOut
						UiAccess.setAccessible(this.menuScreen,1,false);
						//#style menuItem
						UiAccess.setAccessible( this.menuScreen, this.enableSaveGameCmd, true );
						break;
					case 2: showHighscore(); break;
					default: notifyDestroyed(); 
				}
			} else if (cmd == this.startGameCmd) {
				startGame();
			} else if (cmd == this.quitCmd) {
				quit();
			} else if (cmd == this.enableSaveGameCmd) {
				//#style fadeIn
				UiAccess.setAccessible(this.menuScreen,1,true);
				//#style deactivatedCommand
				UiAccess.setAccessible( this.menuScreen, this.enableSaveGameCmd, false );
			}
		}
	}
	
	private void startGame() {
		Alert alert = null;
		//#= alert = new Alert( "Welcome", Locale.get( "messages.welcome", "${user.name}" ), null, AlertType.INFO );
		alert.setTimeout( Alert.FOREVER );
		this.display.setCurrent( alert, this.menuScreen );
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
