/*
 * Created on Dec 28, 2006 at 2:56:01 AM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.sample.rmi;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.event.ThreadedCommandListener;
import de.enough.polish.rmi.RemoteClient;
import de.enough.polish.rmi.RemoteException;
import de.enough.polish.rmi.xmlrpc.XmlRpcRemoteException;
//#if polish.api.mmapi
import de.enough.polish.ui.SnapshotScreen;
//#endif
import de.enough.polish.util.TextUtil;

/**
 * <p>Uses a remote server for user management and more aspects of a (non existing) game.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 28, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class WordTrackerMidlet extends MIDlet implements CommandListener {
	
	private WordTracker server;
	private Form form;
	private TextField textField;
	private Command cmdTrackWords = new Command("Track Words", Command.SCREEN, 2);
	private Command cmdQuit = new Command("Quit", Command.EXIT, 9);
	private Display display;
	
	/**
	 * Creates a new Word Tracker MIDlet
	 */
	public WordTrackerMidlet(){
		// when you are importing the complete package of the remote interface or when you within the same package, you do not
		// need to specify the fully qualified name, in this example following line would also suffice:
		// this.server = (WordTracker) RemoteClient.open("WordTracker", "http://localhost:8080/gameserver/myservice");
		String serviceUrl = "http://test.xmlrpc.wordtracker.com";
		this.server = (WordTracker) RemoteClient.open("de.enough.polish.sample.rmi.WordTracker", serviceUrl );
		//#style mainScreen
		this.form = new Form("XML-RPC");
		//#style itemInput
		this.textField = new TextField("Words: ", "mp3, Madonna", 30, TextField.ANY );
		this.form.append( this.textField ); 
		this.form.addCommand( this.cmdTrackWords );
		this.form.addCommand( this.cmdQuit );
		this.form.setCommandListener( new ThreadedCommandListener( this ) );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// nothing the clean up
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// ignore
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		this.display = Display.getDisplay( this );
		this.display.setCurrent( this.form );
	}

	/*
	 * (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable dis) {
		if (cmd == this.cmdQuit ) {
			notifyDestroyed();
		} else if (cmd == this.cmdTrackWords) {
			String wordsStr = this.textField.getString();
			String[] words = TextUtil.splitAndTrim(wordsStr, ',');
			try
			{
				Hashtable table = this.server.get_exact_phrase_popularity("guest", words, WordTracker.CASE_FOLDED, true, false, WordTracker.ADULT_EXCLUDE, 100, 10 );
				Enumeration enumeration = table.keys();
				while (enumeration.hasMoreElements()) {
					String key = (String) enumeration.nextElement();
					Integer value = (Integer) table.get(key);
					//#style result
					StringItem item = new StringItem( key + ": " , value.toString() );
					this.form.append( item );
					//#debug
					System.out.println("ENTRY: " + key + "=" + value + " (class=" + value.getClass() + ")");
				}
			} catch (XmlRpcRemoteException e) {
				//#style resultError
				this.form.append( e.getMessage() );
			} catch (RemoteException e)
			{
				//#debug error
				System.out.println("Unable to call wordtracker" + e);
				//#style resultError
				this.form.append( e.toString() );				
			}
		}
		
	}

}

