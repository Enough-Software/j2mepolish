/*
 * Created on 29-Sep-2004 at 22:44:24.
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
package de.enough.roadrunner;

import java.io.IOException;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.util.Debug;
import de.enough.polish.util.Locale;

/**
 * <p>Manages the inital user interactions and starts the actual game.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        29-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RoadRunner 
extends MIDlet
implements CommandListener
{
	
	private List mainMenu;
	private final RoadRunnerScreen gameScreen;
	private final Command okCmd = new Command( Locale.get( "cmd.Ok" ), Command.BACK, 2 );
	private Command quitCmd; // = new Command( Locale.get("cmd.Quit"), Command.EXIT, 10 );
	//#ifdef polish.debugEnabled
		Command debugCmd = new Command("Log", Command.ITEM, 12); 
	//#endif
	private Display display;
	protected boolean enableSound = true;
	private Form settingsForm;
	//#ifdef polish.i18n.useDynamicTranslations
		private int lastSelectedLocaleId;
		private String[] localesFileNames;
	//#endif

	/**
	 * Creates a new implementation.
	 */
	public RoadRunner() {
		super();
		createMainMenu( 0 );
		this.gameScreen = new RoadRunnerScreen( this );
		
		//#debug info
		System.out.println("Roadrunner started.");
	}

	private void createMainMenu(int selectedIndex) {
		// create the main menu:
		//#style mainScreen
		this.mainMenu = new List( "RoadRunner", List.IMPLICIT );
		//#style mainMenuItem
		this.mainMenu.append( Locale.get( "menu.StartGame"), null);
		//#style mainMenuItem
		this.mainMenu.append( Locale.get( "menu.Settings"), null);
		//#style mainMenuItem
		this.mainMenu.append( Locale.get( "menu.About"), null);
		//#style mainMenuItem
		this.mainMenu.append( Locale.get( "menu.Quit"), null);
				
		this.quitCmd = new Command( Locale.get("cmd.Quit"), Command.EXIT, 10 );
		this.mainMenu.addCommand( this.quitCmd );
		//#ifdef polish.debugEnabled
			this.mainMenu.addCommand( this.debugCmd );
		//#endif

		this.mainMenu.setCommandListener( this );
		this.mainMenu.setSelectedIndex(selectedIndex, true);
	}

	/**
	 * Starts or resumes the game.
	 */
	protected void startApp() throws MIDletStateChangeException {
		this.display = Display.getDisplay( this );
		this.display.setCurrent( this.mainMenu );
	}

	/**
	 * Pauses the game.
	 * This method is never called on Symbian phones, so we do nothing here.
	 */
	protected void pauseApp() {
		// do nothing
	}

	/**
	 * Is called when the application is shut down.
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command cmd, Displayable screen ) {
		//#ifdef polish.debugEnabled
			if (cmd == this.debugCmd) {
				Debug.showLog( this.display );
				return;
			}
		//#endif
		//#debug
		System.out.println("processing command " + cmd.getLabel() );
		if (cmd == List.SELECT_COMMAND) {
			switch (((List) screen).getSelectedIndex()) {
				case 0:
					this.gameScreen.start();
					this.display.setCurrent( this.gameScreen );
					break;
				case 1:
					showSettings();
					break;
				case 2:
					showAbout();
					break;
				case 3: 
					cmd = this.quitCmd; 
					break;
			}
			//#debug
			System.out.println("now using command " + cmd.getLabel() );
		}
		if (cmd == this.quitCmd) {
			notifyDestroyed();
		} else if (cmd == this.gameScreen.returnCommand) {
			this.gameScreen.world.gameOver = true;
			this.display.setCurrent( this.mainMenu );
		} else if (cmd == this.okCmd) {
			if (screen == this.settingsForm) {
				//#if polish.i18n.useDynamicTranslations
					// read locale settings:
					ChoiceGroup group = (ChoiceGroup) ((Form) screen).get( 1 );
					int selectedIndex = group.getSelectedIndex();
					if (selectedIndex != this.lastSelectedLocaleId) {
						loadTranslations( this.localesFileNames[ selectedIndex ] );
						this.lastSelectedLocaleId = selectedIndex;
					}
				//#endif
			}
			this.display.setCurrent( this.mainMenu );
		} else if (cmd == this.gameScreen.replayCommand) {
			this.gameScreen.start();
		}
	}

	private void showAbout() {
		// show about
		//#style aboutScreen
		Form aboutForm = new Form( Locale.get("title.About") );
		//#style .aboutText, focused, default
		aboutForm.append( Locale.get("about.text"));
		
		aboutForm.addCommand( this.okCmd );
		aboutForm.setCommandListener( this );
		
		this.display.setCurrent( aboutForm );
	}

	private void showSettings() {
		// show settings:
		if ( this.settingsForm == null ) {
			//#style settingsScreen
			this.settingsForm = new Form( Locale.get("title.Settings") );
			//#style exclusiveChoice
			ChoiceGroup group = new ChoiceGroup( Locale.get("setting.sound"), ChoiceGroup.EXCLUSIVE );
			//#style exclusiveChoiceItem?
			group.append( Locale.get("setting.sound.off"), null );
			//#style exclusiveChoiceItem?
			group.append( Locale.get("setting.sound.on"), null );
			//#style exclusiveChoiceItem?
			group.append( Locale.get("setting.sound.fxonly"), null );
			this.settingsForm.append( group );
			
			//#ifdef polish.i18n.useDynamicTranslations
				 //#style exclusiveChoice
				group = new ChoiceGroup( Locale.get("setting.language"), ChoiceGroup.EXCLUSIVE );
				//#= this.localesFileNames = new String[ ${number(polish.SupportedLocales)} ];
				int i = 0;
				//#foreach locale in polish.SupportedLocales
					//#message adding supported locale ${locale}
					//#style exclusiveChoiceItem?
					//#= group.append( "${ displaylanguage( locale) }", null );
					//#= this.localesFileNames[i] = "${ localefilename( locale) }";
					i++;					
				//#next locale
				group.setSelectedIndex( this.lastSelectedLocaleId, true );	
				this.settingsForm.append( group );
			//#endif
			String userName = "";
			//#if true
				//#= userName = "${user.name}";
			//#endif

			//#style input?
			TextField nameField = new TextField( Locale.get("setting.name"), userName, 1000, TextField.ANY);
			this.settingsForm.append( nameField );
			
			
			this.settingsForm.addCommand( this.okCmd );
			this.settingsForm.setCommandListener( this );
		}
		
		this.display.setCurrent( this.settingsForm );
	}

	//#ifdef polish.i18n.useDynamicTranslations
	/**
	 * @param url
	 */
	private void loadTranslations(String url) {
		try {
			Locale.loadTranslations(url);
//			this.mainMenu.set( 0, Locale.get( "menu.StartGame"), null);
//			this.mainMenu.set( 1, Locale.get( "menu.Settings"), null);
//			this.mainMenu.set( 2, Locale.get( "menu.About"), null);
//			this.mainMenu.set( 3, Locale.get( "menu.Quit"), null);
//			this.mainMenu.removeCommand( this.quitCmd );
//			this.quitCmd = new Command( Locale.get("cmd.Quit"), Command.EXIT, 10 );
//			this.mainMenu.addCommand( this.quitCmd );
			createMainMenu( 1 );
			this.settingsForm = null;
			String language = Locale.LANGUAGE;
			System.out.println( Locale.get( "message.LanguageChanged", language));
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to load translations [" + url + "]" + e );
		}
	}
	//#endif

}
