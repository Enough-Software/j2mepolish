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
import java.io.InputStream;

import javax.microedition.lcdui.Image;

import de.enough.polish.app.App;
import de.enough.polish.app.model.Configuration;
import de.enough.polish.app.view.MainMenuList;
import de.enough.polish.authentication.AccessToken;
import de.enough.polish.authentication.AuthenticationItem;
import de.enough.polish.authentication.AuthenticationListener;
import de.enough.polish.authentication.AuthenticationProcess;
import de.enough.polish.browser.BrowserListener;
import de.enough.polish.facebook.FacebookClientAuthenticationProcess;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.json.JsonObject;
import de.enough.polish.json.JsonParser;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.ScreenInfo;
import de.enough.polish.ui.SimpleScreenHistory;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.splash2.ApplicationInitializer;
import de.enough.polish.ui.splash2.InitializerSplashScreen;
import de.enough.polish.util.Locale;
import de.enough.polish.util.StreamUtil;

/**
 * <p>Controls the UI of the mobile app</p>
 *
 * <p>Copyright Enough Software 2010 - 2012</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Controller
implements ApplicationInitializer, CommandListener, AuthenticationListener
{

	private final App midlet;
	private Display display;
	private Configuration configuration;
	private RmsStorage storage;
	
	private Command cmdExit = new Command(Locale.get("cmd.exit"), Command.EXIT, 10);
	private Command cmdBack = new Command(Locale.get("cmd.back"), Command.BACK, 2);
	private Command cmdRetryAuthentication = new Command("Retry", Command.OK, 1);

	private MainMenuList screenMainMenu;
	private static final int MAIN_ACTION_START_AUTENTICATION = 0;
	private static final int MAIN_ACTION_REFRESH_AUTHENTICATION = 1;
	private static final int MAIN_ACTION_DELETE_AUTHENTICATION = 2;
	private static final int MAIN_ACTION_ABOUT = 3;
	private static final int MAIN_ACTION_EXIT = 4;
	
	private SimpleScreenHistory screenHistory;
	private int busyIndicators;
	
	private Form screenAuthentication;
	private AuthenticationItem authenticationItem;
	private AuthenticationProcess authenticationProcess;

	
	

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
		
		String appId = "251715528252422";
		String appSecret = "3873ca18ab1dee5ceb07005b0532bbc9";
		String redirectUrl = "http://www.j2mepolish.org/";
		String permissions = "xmpp_login";
		this.authenticationProcess = new FacebookClientAuthenticationProcess(appId, appSecret, redirectUrl, permissions, this);

		
		//#style browserDownloadIndicator
		Gauge busyGauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING );
		ScreenInfo.setItem(busyGauge);
		ScreenInfo.setVisible(false);
		
		this.storage = new RmsStorage();
		this.configuration = configurationLoad();
		// create main menu:
		this.screenMainMenu = createMainMenu();
		long currentTime = System.currentTimeMillis();
		long maxTime = 1500;
		if (currentTime - initStartTime < maxTime) { // show the splash at least for 1500ms / 1.5 seconds:
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
		list.addEntry("Start Authentication");
		list.addEntry("Refresh Authentication");
		list.addEntry("Delete Authentication");
		list.addEntry("About");
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
		} else if (cmd == this.cmdRetryAuthentication) {
			showMainMenu();
		} else if (cmd == this.cmdBack) {
			if (this.screenHistory.hasPrevious()) {
				this.screenHistory.showPrevious();
			} else {
				this.screenHistory.clearHistory();
				this.display.setCurrent(this.screenMainMenu);
			}
		}
		
	}

	private void showMainMenu() {
		this.screenHistory.clearHistory();
		stopBusyIndicator(true);
		this.display.setCurrent(this.screenMainMenu);
	}

	/**
	 * Handles commands for the main menu
	 * @param cmd the command of the main menu
	 * @return true when a command was handled
	 */
	private boolean handleCommandMainMenu(Command cmd) {
		boolean handled = false;
		if (cmd == MainMenuList.SELECT_COMMAND) {
			int index = this.screenMainMenu.getSelectedIndex();
			handled = true;
			switch (index) {
			case MAIN_ACTION_START_AUTENTICATION:
				startAuthentication();
				break;
			case MAIN_ACTION_REFRESH_AUTHENTICATION:
				refreshAuthentication();
				break;
			case MAIN_ACTION_DELETE_AUTHENTICATION:
				deleteAuthentication();
				break;
			case MAIN_ACTION_ABOUT:
				about();
				break;
			case MAIN_ACTION_EXIT:
				exit();
				break;
			}
		}
		return handled;
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
	 * Starts the authentication process.
	 */
	private synchronized void startAuthentication() {
		if (this.screenAuthentication == null) {
			
			//#style screenAuthentication
			Form form = new Form("Authentication Process");

			//#style authenticationItem
			this.authenticationItem = new AuthenticationItem(this.authenticationProcess );
			this.authenticationItem.setBrowserListener( new BrowserListener() {
				public void notifyPageStart(String url) {
					startBusyIndicator();
				}
				public void notifyPageEnd() {
					stopBusyIndicator();
				}
				public void notifyDownloadStart(String url) {
					startBusyIndicator();
				}
				public void notifyDownloadEnd() {
					stopBusyIndicator();
				}
				public void notifyPageError(String url, Exception e) {
					stopBusyIndicator();
				}				
			});
			form.append(this.authenticationItem);
			this.authenticationItem.setBackCommand( this.cmdBack );
			form.addCommand(this.cmdBack);
			form.setCommandListener( this );
			this.screenAuthentication = form;
		} else {
			this.authenticationItem.startAuthentication();
		}
		this.display.setCurrent( this.screenAuthentication );

	}
	
	/**
	 * Refreshes the authentication token
	 */
	private synchronized void refreshAuthentication() {
		startBusyIndicator();
		this.authenticationProcess.refreshAccess(this);
	}

	/**
	 * Removes local authentication data
	 */
	private void deleteAuthentication() {
		try {
			this.authenticationProcess.clearAuthenticationData();
		} catch (IOException e) {
			//#debug info
			System.out.println("Unable to clear the authentication data. Typically this is because there is not authentication data yet" + e);
		}
		showAlert("Authentication Data Cleared", "The local authentication data has been cleared. Note that you need to remove the application from Facebook (Account Settings > Apps).");
	}
	

	private void about() {
		showAlert("About", "OAuth Sample Application.\n(c) 2012 Enough Software");
		
	}

	private void showAlert(String title, String text) {
		//#style screenAlert
		Alert alert = new Alert( title, text );
		this.display.setCurrent(alert);
	}

	
	/**
	 * Starts the busy indicator.
	 * When this is the first indicator, the busy indicator will be made visible.
	 * The busy indicator uses ScreenInfo, this element requires the preprocessing variable 
	 * &lt;variable name=&quot;polish.ScreenInfo.enable&quot; value=&quot;true&quot; /&gt;
	 * in your build.xml script.
	 * Each long running operation should call startBusyIndicator() and stopBusyIndicator() for giving the user feedback.
	 * @see #refreshAuthentication()
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
	

	/**
	 * Stops the busy indicator.
	 * When no busy indicators are left, the busy indicator won't be shown any more.
	 * The busy indicator uses ScreenInfo, this element requires the preprocessing variable 
	 * &lt;variable name=&quot;polish.ScreenInfo.enable&quot; value=&quot;true&quot; /&gt;
	 * in your build.xml script.
	 * Each long running operation should call startBusyIndicator() and stopBusyIndicator() for giving the user feedback.
	 * @see #startAuthentication()
	 */
	private void stopBusyIndicator() {
		stopBusyIndicator(false);
	}
	
	/**
	 * Stops the busy indicator.
	 * When no busy indicators are left, the busy indicator won't be shown any more.
	 * The busy indicator uses ScreenInfo, this element requires the preprocessing variable 
	 * &lt;variable name=&quot;polish.ScreenInfo.enable&quot; value=&quot;true&quot; /&gt;
	 * in your build.xml script.
	 * Each long running operation should call startBusyIndicator() and stopBusyIndicator() for giving the user feedback.
	 * @see #startAuthentication()
	 */
	private synchronized void stopBusyIndicator(boolean force) {
		if (this.busyIndicators > 0) {
			this.busyIndicators--;
			if (this.busyIndicators == 0 || force) {
				ScreenInfo.setVisible(false);
				if (force) {
					this.busyIndicators = 0;
				}
			}
		}
		//#debug
		System.out.println("stop busy indicator: Number of busy indicators: " + this.busyIndicators);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.authentication.AuthenticationListener#onAuthenticationSuccess(de.enough.polish.authentication.AccessToken)
	 */
	public void onAuthenticationSuccess(AccessToken token) {
		stopBusyIndicator();
		//#debug
		System.out.println("AUTHENTICATION SUCCESS");
		String url = "https://graph.facebook.com/me?access_token=" + token.getToken();
		//#style screenResult
		Form form = new Form("OAuth Success");
		//#style infoText
		form.append("OAuth authentication has succeeded.\nToken: " + token.getToken() );

		try {
			RedirectHttpConnection connection = new RedirectHttpConnection(url);
			InputStream is = connection.openInputStream();
			String jsonText = StreamUtil.getString(is, "UTF-8");
			//#debug
			System.out.println(jsonText);
			connection.close();
			JsonParser parser = new JsonParser();
			JsonObject result = (JsonObject) parser.parseJson( jsonText ); //is, "UTF-8" );

			String userName = (String) result.get("name");
			String userEmail = (String) result.get("email");
			String userBio = (String) result.get("bio");
			//#style infoText
			form.append("Welcome " + userName);
			if (userEmail != null) {
				//#style infoText
				form.append("Your email: " + userEmail);
			}
			if (userBio != null) {
				//#style infoText
				form.append("Your bio: " + userBio);
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to resolve user name" + e);
		}
		try {
			url = "https://graph.facebook.com/me/permissions?access_token=" + token.getToken();
			RedirectHttpConnection connection = new RedirectHttpConnection(url);
			InputStream is = connection.openInputStream();
			String jsonText = StreamUtil.getString(is, "UTF-8");
			//#debug
			System.out.println(jsonText);
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to resolve permissions" + e);
		}
		//#style action
		StringItem item = new StringItem(null, "Restart Authentication");
		item.setDefaultCommand(this.cmdRetryAuthentication);
		form.append(item);
		form.addCommand(this.cmdExit);
		form.setCommandListener(this);
		this.display.setCurrent( form );
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.authentication.AuthenticationListener#onAuthenticationFailure(int, java.lang.Object)
	 */
	public void onAuthenticationFailure(int reason, Object errorData) {
		stopBusyIndicator();
		//#debug warn
		System.out.println("AUTHENTICATION FAILURE: reason=" + reason + ", error=" + errorData);
		//#style screenResult
		Form form = new Form("OAuth failed");
		String[] reasons = new String[]{ "unspecified", "aborted", "access denied", "network error" }; 
		//#style infoText
		form.append("OAuth authentication has failed.\nReason: " + reasons[reason] );
		//#style action
		StringItem item = new StringItem(null, "Retry");
		item.setDefaultCommand(this.cmdRetryAuthentication);
		form.append(item);
		form.addCommand(this.cmdExit);
		form.setCommandListener(this);
		this.display.setCurrent( form );
	}

}
