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
import de.enough.polish.app.model.Contact;
import de.enough.polish.app.model.ContactCollection;
import de.enough.polish.app.model.Message;
import de.enough.polish.app.view.ContactSelectionForm;
import de.enough.polish.app.view.MainMenuList;
import de.enough.polish.app.view.MessageForm;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.ScreenInfo;
import de.enough.polish.ui.SimpleScreenHistory;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.splash2.ApplicationInitializer;
import de.enough.polish.ui.splash2.InitializerSplashScreen;
import de.enough.polish.ui.texteffects.MessageTextEffect;
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
	private Command cmdAbout = new Command(Locale.get("cmd.about"), Command.SCREEN, 3);
	private Command cmdSelectContact = new Command(Locale.get("cmd.contact.select"), Command.SCREEN, 3);
	private Command cmdMessageSend = new Command(Locale.get("cmd.message.send"), Command.OK, 1);
	private Command cmdMessageShowSource = new Command(Locale.get("cmd.message.showSource"), Command.SCREEN, 3);
	private Command cmdMessageShowParsed = new Command(Locale.get("cmd.message.showParsed"), Command.SCREEN, 3);
	private Command cmdOpenWebsite = new Command(Locale.get("cmd.message.openWebsite"), Command.SCREEN, 4);
	private Command cmdOpenMailto = new Command(Locale.get("cmd.message.openMailto"), Command.SCREEN, 4);
	private Command cmdOpenCall = new Command(Locale.get("cmd.message.openCall"), Command.SCREEN, 4);
	
	private MainMenuList screenMainMenu;
	private static final int MAIN_ACTION_START = 0;
	private static final int MAIN_ACTION_STOP = 1;
	private static final int MAIN_ACTION_SELECT_CONTACT = 2;
	private static final int MAIN_ACTION_MESSAGES = 3;
	private static final int MAIN_ACTION_EXIT = 4;
	
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
		// init contacts:
		ContactCollection.getInstance();
		long currentTime = System.currentTimeMillis();
		long maxTime = 1500;
		if (currentTime - initStartTime < maxTime) { // show the splash at least for 1500 ms / 1.5 seconds:
			try {
				Thread.sleep(maxTime - currentTime + initStartTime);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		MessageTextEffect.setMidlet(this.midlet, this.cmdOpenWebsite, this.cmdOpenMailto, this.cmdOpenCall);
		this.display.setCurrent( this.screenMainMenu );
	}

	private MainMenuList createMainMenu() {
		MainMenuList list = new MainMenuList();
		list.setCommandListener(this);
		list.addCommand(this.cmdExit);
		list.addCommand(this.cmdAbout);
		list.addEntry("Start Busy Indicator");
		list.addEntry("Stop Busy Indicator");
		list.addEntry("Select Contact");
		list.addEntry("Messages");
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
		} else if (disp instanceof MessageForm) {
			if (handleCommandMessageForm( cmd, (MessageForm)disp)) {
				return;
			}
		} else if (disp instanceof ContactSelectionForm) {
			if (handleCommandContactSelectionForm(cmd, (ContactSelectionForm)disp)) {
				return;
			}
		}
		if (cmd == this.cmdBack) {
			if (this.screenHistory.hasPrevious()) {
				this.screenHistory.showPrevious();
			} else {
				this.screenHistory.clearHistory();
				this.display.setCurrent(this.screenMainMenu);
			}
		}
		
	}

	private boolean handleCommandMessageForm(Command cmd, MessageForm form) {
		if (cmd == this.cmdMessageSend) {
			String input = form.getInputAndClearField();
			if (input.length() > 0) {
				Message message = new Message("me", input);
				form.addMessage(message);
				UiAccess.scrollToBottom(form);
			}
			return true;
		} else if (cmd == this.cmdMessageShowSource) {
			form.removeCommand(this.cmdMessageShowSource);
			form.addCommand(this.cmdMessageShowParsed);
			form.showMessageSource();
			return true;
		} else if (cmd == this.cmdMessageShowParsed) {
			form.addCommand(this.cmdMessageShowSource);
			form.removeCommand(this.cmdMessageShowParsed);
			form.showMessageParsed();
			return true;
		} 
		return false;
	}
	
	private boolean handleCommandContactSelectionForm(Command cmd, final ContactSelectionForm form)
	{
		if (cmd == this.cmdSelectContact)
		{
			Contact contact = form.getCurrentContact();
			if (contact != null)
			{
				final String userName = contact.getFirstName() + " " + contact.getLastName();
				Alert alert = new Alert("Start Chat", "Chat with " + userName + "?", null, AlertType.CONFIRMATION);
				final Command cmdYes = new Command("Yes", Command.OK, 1);
				final Command cmdNo = 	new Command("No", Command.CANCEL, 1);
				alert.addCommand(cmdYes);
				alert.addCommand(cmdNo);
				alert.setCommandListener(new CommandListener() {
					public void commandAction(Command c, Displayable d) {
						if (c == cmdYes) 
						{
							showMessages(userName);
						}
						else
						{
							Controller.this.display.setCurrent(form);
						}				
					}
				});
				this.display.setCurrent(alert);
				return true;
			}
		}
		return false;
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
			case MAIN_ACTION_SELECT_CONTACT:
				showContacts();
				break;
			case MAIN_ACTION_MESSAGES:
				showMessages("j2mepolish");
				break;
			case MAIN_ACTION_EXIT:
				showExitDialog();
			}
			return true;			
		} else if (cmd == this.cmdAbout) {
			showAbout();
			return true;
		} else if (cmd == this.cmdSelectContact)
		{
			showContacts();
			return true;
		}
		return false;
	}

	private void showContacts()
	{
		ContactSelectionForm form = new ContactSelectionForm(Locale.get("contact.select.title"), this.cmdSelectContact);
		form.addCommand(this.cmdBack);
		form.setCommandListener(this);
		this.screenHistory.show(form);
	}

	private void showAbout() {
		//#style screenAbout
		Form form = new Form(Locale.get("about.title"));
		//#style aboutText
		form.append(Locale.get("about.text"));
		form.addCommand(this.cmdBack);
		form.setCommandListener(this);
		this.screenHistory.show(form);
	}

	private void showMessages(String userName) {
		Message[] messages = new Message[]{
				new Message("me", "Hi J2ME Polish, what's up...?"),
				new Message(userName, "Hey, this is the new *message* text effect in <div style=\"color: red;\">action</div>. "),
				new Message("me", "Cool! When will it be out?"),
				new Message(userName, "That's the /good part/, it's available now: www.j2mepolish.org."),
				new Message("me", "fan-tas-tic :-)")
		};
		MessageForm form = new MessageForm(this.cmdMessageSend);
		for (int i = 0; i < messages.length; i++) {
			Message message = messages[i];
			form.addMessage(message);
		}
		form.addCommand(this.cmdBack);
		form.addCommand(this.cmdMessageShowSource);
		form.setCommandListener(this);
		UiAccess.init(form);
		form.scrollToBottom();
		this.screenHistory.show(form);
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
	
	private void showExitDialog() {
		Alert alert = new Alert("Exit?", "Wanna exit?", null, AlertType.CONFIRMATION);
		final Command cmdYes = new Command("Yes", Command.OK, 1);
		final Command cmdNo = 	new Command("No", Command.CANCEL, 1);
		alert.addCommand(cmdYes);
		alert.addCommand(cmdNo);
		alert.setCommandListener(new CommandListener() {
			public void commandAction(Command c,
					Displayable d) {
				if (c == cmdYes) {
					exit();
				}
				else
				{
					Controller.this.display.setCurrent(Controller.this.screenMainMenu);
				}				
			}
		});
		this.display.setCurrent(alert);
	}

}

