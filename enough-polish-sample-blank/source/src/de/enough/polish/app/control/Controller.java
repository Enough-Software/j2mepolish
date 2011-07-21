/*
 * Created on Dec 15, 2010 at 9:19:09 AM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.app.control;

import java.io.IOException;

import javax.microedition.lcdui.Image;


import de.enough.polish.app.App;
import de.enough.polish.app.model.Configuration;
import de.enough.polish.app.view.MainMenuList;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.ScreenInfo;
import de.enough.polish.ui.SimpleScreenHistory;
import de.enough.polish.ui.splash2.ApplicationInitializer;
import de.enough.polish.ui.splash2.InitializerSplashScreen;
import de.enough.polish.util.Locale;

/**
 * <p>Controls the UI of the mobile app</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Controller
implements ApplicationInitializer, CommandListener
{

	private final App midlet;
	private Display display;
	private Configuration configuration;
	private RmsStorage storage;
	
	private Command cmdExit = new Command(Locale.get("cmd.exit"), Command.EXIT, 10);
	private Command cmdBack = new Command(Locale.get("cmd.back"), Command.BACK, 2);
	
	private MainMenuList screenMainMenu;
	private static final int MAIN_ACTION_START = 0;
	private static final int MAIN_ACTION_STOP = 1;
	private static final int MAIN_ACTION_ABOUT = 2;
	private static final int MAIN_ACTION_EXIT = 3;
	
	private SimpleScreenHistory screenHistory;
	private int busyIndicators;
	
	

	/**
	 * Creates a new controller.
	 * @param midlet the main application
	 */
	public Controller(App midlet) {
		this.midlet = midlet;
		this.display = Display.getDisplay(midlet);
		this.screenHistory = new SimpleScreenHistory(this.display);
	}

	/**
	 * Lifecycle: starts the application for the first time.
	 */
	public void appStart() {
		String splashUrl = "/Splash.png";
		Image splashImage = null;
		try {
			splashImage = Image.createImage(splashUrl);
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to load splash image " + splashUrl +  e);
		}
		int backgroundColor = 0xffffff;
		InitializerSplashScreen splash = new InitializerSplashScreen(splashImage, backgroundColor,  this);
		this.display.setCurrent( splash );
	}

	/**
	 * Lifecycle: pauses the application, e.g. when there is an incoming call.
	 */
	public void appPause() {
		// TODO implement pauseApp, e.g. stop streaming
	}

	/**
	 * Lifecycle: continues the application after it has been paused.
	 */
	public void appContinue() {
		// TODO implement continueApp, e.g. start streaming again
	}

	/**
	 * Initializes this application in a background thread that is called from within the splash screen.
	 */
	public void initApp() {
		long initStartTime = System.currentTimeMillis();
		//#style busyGauge
		Gauge busyGauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING );
		ScreenInfo.setItem(busyGauge);
		ScreenInfo.setVisible(false);
		
		this.storage = new RmsStorage();
		this.configuration = configurationLoad();
		// create main menu:
		this.screenMainMenu = createMainMenu();
		long currentTime = System.currentTimeMillis();
		long maxTime = 1500;
		if (currentTime - initStartTime < maxTime) { // show the splash at least for 1500 ms / 2 seconds:
			try {
				Thread.sleep(maxTime - currentTime + initStartTime);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		this.display.setCurrent( this.screenMainMenu );
	}

	private MainMenuList createMainMenu() {
		MainMenuList list = new MainMenuList();
		list.setCommandListener(this);
		list.addCommand(this.cmdExit);
		list.addEntry("Start Busy Indicator");
		list.addEntry("Stop Busy Indicator");
		list.addEntry("entry 3");
		list.addEntry(Locale.get("cmd.exit"));
		return list;
	}

	/**
	 * Loads the configuration of this app.
	 * @return the loaded configuration or an new one.
	 */
	private Configuration configurationLoad() {
		try {
			Configuration cfg = (Configuration) this.storage.read(Configuration.KEY);
			return cfg;
		} catch (IOException e) {
			//#debug info
			System.out.println("Unable to load configuration" + e);
		}
		return new Configuration();
	}

	/**
	 * Persists the configuration.
	 * @return true when saving was successful, otherwise false is returned.
	 */
	private boolean configurationSave() {
		try {
			this.storage.save(this.configuration, Configuration.KEY);
			return true;
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to store the configuration" + e);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.CommandListener#commandAction(de.enough.polish.ui.Command, de.enough.polish.ui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == this.cmdExit) {
			exit();
		} else if (disp == this.screenMainMenu) {
			if (handleCommandMainMenu(cmd)) {
				return;
			}
		} else if (cmd == this.cmdBack) {
			if (this.screenHistory.hasPrevious()) {
				this.screenHistory.showPrevious();
			} else {
				this.screenHistory.clearHistory();
				this.display.setCurrent(this.screenMainMenu);
			}
		}
		
	}

	/**
	 * Handles commands for the main menu
	 * @param cmd the command of the main menu
	 * @return true when a command was handled
	 */
	private boolean handleCommandMainMenu(Command cmd) {
		if (cmd == MainMenuList.SELECT_COMMAND) {
			int index = this.screenMainMenu.getSelectedIndex();
			switch (index) {
			case MAIN_ACTION_START:
				startBusyIndicator();
				break;
			case MAIN_ACTION_STOP:
				stopBusyIndicator();
				break;
			case MAIN_ACTION_ABOUT:
				break;
			case MAIN_ACTION_EXIT:
				exit();
				return true;
			}
		}
		return false;
	}

	/**
	 * Exits this app
	 */
	private void exit() {
		if (this.configuration.isDirty()) {
			configurationSave();
		}
		this.midlet.exit();
	}
	
	/**
	 * Stops the busy indicator.
	 * When no busy indicators are left, the busy indicator won't be shown any more.
	 * The busy indicator uses ScreenInfo, this element requires the preprocessing variable 
	 * <variable name="polish.ScreenInfo.enable" value="true" />
	 * in your build.xml script.
	 * Each long running operation should call startBusyIndicator() and stopBusyIndicator() for giving the user feedback.
	 * @see #startBusyIndicator()
	 */
	private synchronized void stopBusyIndicator() {
		if (this.busyIndicators > 0) {
			this.busyIndicators--;
			if (this.busyIndicators == 0) {
				ScreenInfo.setVisible(false);
			}
		}
		//#debug
		System.out.println("stop busy indicator: Number of busy indicators: " + this.busyIndicators);
	}

	/**
	 * Starts the busy indicator.
	 * When this is the first indicator, the busy indicator will be made visible.
	 * The busy indicator uses ScreenInfo, this element requires the preprocessing variable 
	 * <variable name="polish.ScreenInfo.enable" value="true" />
	 * in your build.xml script.
	 * Each long running operation should call startBusyIndicator() and stopBusyIndicator() for giving the user feedback.
	 * @see #stopBusyIndicator()
	 * @see #initApp() for initialization of the gauge
	 */
	private synchronized void startBusyIndicator() {
		if (this.busyIndicators == 0) {
			ScreenInfo.setVisible(true);
		}
		this.busyIndicators++;
		//#debug
		System.out.println("start busy indicator: Number of busy indicators: " + this.busyIndicators);
	}
}
