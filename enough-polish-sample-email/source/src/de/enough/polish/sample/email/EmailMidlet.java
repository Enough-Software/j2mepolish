/*
 * Created on 30-Jan-2006 at 03:27:38.
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
package de.enough.polish.sample.email;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.ScreenInfo;
import de.enough.polish.ui.TreeItem;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Example for using the TreeItem.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        30-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EmailMidlet 
extends MIDlet
implements CommandListener, ItemStateListener
{
	private final static int STATUS_OFFLINE = 0;
	private final static int STATUS_ONLINE = 1;
	private final static int STATUS_INVISIBLE = 2;
	private final Command createNewCommand = new Command("New", Command.SCREEN, 1 );
	private final Command createNewMailCommand = new Command( "E-Mail", Command.SCREEN, 1 );
	private final Command createNewIMCommand = new Command( "Instant Message", Command.SCREEN, 2 );
	private final Command exitCommand = new Command( "Exit", Command.EXIT, 10 );
	private final Command okCommand = new Command( "OK", Command.OK, 1 );
	private final Command abortCommand = new Command( "Cancel", Command.BACK, 2 );
	private final Command setStatusCommand = new Command("Status", Command.SCREEN, 1 );
	private final Command setStatusOnlineCommand = new Command( "Online", Command.SCREEN, 1 );
	private final Command setStatusOfflineCommand = new Command( "Offline", Command.SCREEN, 2 );
	private final Command setStatusInvisibleCommand = new Command( "Invisible", Command.SCREEN, 2 );

	private Form mainScreen;
	private CreateMessageForm createMessageForm;
	private Display display;

	/**
	 * Creates a new midlet.
	 */
	public EmailMidlet() {
		super();
		//#debug
		System.out.println("email midlet created");
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		//#debug
		System.out.println("start app");

		//#style mailForm
		Form form = new Form("Mobile Mail");
		
		//#style mailTree
		TreeItem tree = new TreeItem( null );
		//#style mailbox
		Item node = tree.appendToRoot("Inbox", null);
		addMessage(tree, node, "Bill Gates", "What's next?", "After conquering the world, what's left?" );
		addMessage(tree, node, "Stephen Hawkings", "Black Holes",  "They are my favourite!" );
		addMessage(tree, node, "David Byrne", "String Theory", "Or is it m-theory with multidimensional branes?!" );

		//#style mailbox
		node = tree.appendToRoot("Outbox", null);
		addMessage(tree, node, "Enough Software", "J2ME Polish",  "Powerful, Flexible, Extensible.");

		//#style mailbox
		node = tree.appendToRoot("Sent", null);
		addMessage(tree, node, "Steve Jobs", "iPhone?", "Gimme that phone, please :-)  - and don't forget the Java support!"  );

		form.append( tree );

		//#style mailTicker
		Ticker ticker = new Ticker( "Sending mail \"J2ME Polish Test Result\" to sales@mycompany.com" );
		/*
		form.setTicker( ticker );
		*/
		
		//#style messageInput, input, addressInput
		TextField text = new TextField( "message: ", "Hello J2ME Polish World with two lines!", 255, TextField.ANY );
		form.append( text );

		//#style pininput
		text = new TextField( "PIN: ", "", 4, TextField.NUMERIC | TextField.PASSWORD );
		form.append( text );

		//#style messageInput, input, addressInput
		text = new TextField( "Decimal: ", "2", 6, TextField.DECIMAL);
		form.append( text );
		
		//#style messageInput, input, addressInput
		text = new TextField( "Pass: ", "secret", 255, TextField.ANY | TextField.PASSWORD );
		form.append( text );

		
		form.setCommandListener( this );
		form.setItemStateListener( this );
		form.addCommand( this.createNewCommand );
		form.addCommand( this.exitCommand );
		UiAccess.addSubCommand( this.createNewMailCommand, this.createNewCommand, form );
		UiAccess.addSubCommand( this.createNewIMCommand, this.createNewCommand, form );
		form.addCommand( this.setStatusCommand );
		UiAccess.addSubCommand( this.setStatusOnlineCommand, this.setStatusCommand, form );
		UiAccess.addSubCommand( this.setStatusOfflineCommand, this.setStatusCommand, form );
		UiAccess.addSubCommand( this.setStatusInvisibleCommand, this.setStatusCommand, form );
		
		this.mainScreen = form;
		setStatus( STATUS_OFFLINE );
		
		this.display = Display.getDisplay( this );
		this.display.setCurrent( form );
		// you need to specify the preprocessing variable polish.Screen.ManualOrientationChange in your build.xml script
		// for rotating screens:
		// <variable name="polish.Screen.ManualOrientationChange" value="true" />
		// UiAccess.setScreenOrientation(90);
	}

	private void addMessage(TreeItem tree, Item node, String from, String subject, String text ) {
		Item fromItem;
		Item detailItem;
		//#style mailSummary
		fromItem = tree.appendToNode(node, from, null );
		//#style mailDetail
		detailItem = new StringItem( "Subject: ", subject);
		tree.appendToNode( fromItem, detailItem );
		//#style mailDetail
		detailItem = new StringItem( "Text: ", text);
		tree.appendToNode( fromItem, detailItem );
	}

	private void setStatus( int status ) {
		String url;
		switch (status) {
		case STATUS_ONLINE: url = "/info_online.png"; break;
		case STATUS_INVISIBLE: url = "/info_invisible.png"; break;
		default: url = "/info_offline.png";
		}
		//#if polish.ScreenInfo.enable
			try {
				Image img = Image.createImage(url);
				ScreenInfo.setImage( img );
			} catch (IOException e) {
				//#debug error
				System.out.println("Unable to switch to status " + status + ": " + url + " could not be loaded" + e );
			}
		//#else
			//#debug info
			System.out.println("status set to " + status  );
		//#endif
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// just keep on pausing
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// nothing to clean up
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
					notifyDestroyed();
				}
				else
				{
					EmailMidlet.this.display.setCurrent(EmailMidlet.this.mainScreen);
				}				
			}
		});
		this.display.setCurrent(alert);
	}

	public void commandAction(Command cmd, Displayable disp) {
		//#debug
		System.out.println("commandAction with cmd=" + cmd.getLabel() + ", screen=" + disp );
		if ( disp == this.mainScreen ) {
			if (cmd == this.exitCommand ) {
				//notifyDestroyed();
				showExitDialog();
			} else if (cmd == this.createNewMailCommand) {
				//#style createMessageForm
				CreateMessageForm form = new CreateMessageForm( "Create E-Mail");
				form.setCommandListener( this );
				form.addCommand( this.okCommand );
				form.addCommand( this.abortCommand );
				this.createMessageForm = form;
				this.display.setCurrent( form );
			} else if (cmd == this.createNewIMCommand) {
				//#style createMessageForm
				CreateMessageForm form = new CreateMessageForm( "Create Instant Message");
				form.setCommandListener( this );
				form.addCommand( this.okCommand );
				form.addCommand( this.abortCommand );
				this.createMessageForm = form;				
				this.display.setCurrent( form );
			} else if (cmd == this.setStatusOnlineCommand) {
				setStatus( STATUS_ONLINE );
			} else if (cmd == this.setStatusOfflineCommand) {
				setStatus( STATUS_OFFLINE );
			} else if (cmd == this.setStatusInvisibleCommand) {
				setStatus( STATUS_INVISIBLE );
			} else {
				//#style mailAlert
				Alert alert = new Alert( "Not supported", 
						"The action is not yet implemented.", null, AlertType.INFO );
				this.display.setCurrent( alert, this.mainScreen );
			}
		} else if (disp == this.createMessageForm ){
			//#debug
			System.out.println("command for create message form...");
			if (cmd == this.okCommand) {
				//#debug
				System.out.println("creating message for " + this.createMessageForm.getReceiver() + " from " + this.createMessageForm.getSender() );
				//#style mailAlert
				Alert alert = new Alert( "Creating New Message", 
						"Receiver: " + this.createMessageForm.getReceiver()  + "\nSender: " + this.createMessageForm.getSender(),
						null, AlertType.INFO );
				
				this.createMessageForm = null;
				this.display.setCurrent( alert, this.mainScreen );
			} else {
				//#debug
				System.out.println("aborting message creation.");
				this.display.setCurrent( this.mainScreen );
			}
		}
	}

	public void itemStateChanged(Item item) {
		System.out.println("ItemStateChanged " + item);
		if (item instanceof ChoiceGroup) {
			try { throw new RuntimeException(); } catch (Exception e) { e.printStackTrace(); }
		}
	}

}

