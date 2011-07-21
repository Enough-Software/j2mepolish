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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.media.MediaException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.event.ThreadedCommandListener;
import de.enough.polish.rmi.RemoteClient;
import de.enough.polish.rmi.RemoteException;
//#if polish.api.mmapi
import de.enough.polish.ui.SnapshotScreen;
//#endif

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
public class GameMidlet extends MIDlet implements Runnable, CommandListener {
	
	private GameServer server;
	private Form form;
	private Command cmdRegister = new Command("Register", Command.SCREEN, 2);
	private Command cmdTakeScreenshot = new Command("Take Screenshot", Command.OK, 3 );
	private Command cmdQuit = new Command("Quit", Command.EXIT, 9);
	private Display display;
	private final ThreadedCommandListener commandListener;
	
	public GameMidlet(){
		this.commandListener = new ThreadedCommandListener( this );
		// when you are importing the complete package of the remote interface or when you within the same package, you do not
		// need to specify the fully qualified name, in this example following line would also suffice:
		// this.server = (GameServer) RemoteClient.open("GameServer", "http://localhost:8080/gameserver/myservice");
		//#if cfg.gameserver.url:defined
			//#= String gameServerUrl = "${cfg.gameserver.url}";
		//#else
			String gameServerUrl = "http://localhost:8080/gameserver/myservice";
		//#endif
		this.server = (GameServer) RemoteClient.open("de.enough.polish.sample.rmi.GameServer", gameServerUrl );
		//#style mainScreen
		this.form = new Form("RMI");
		this.form.addCommand( this.cmdRegister );
		//#if polish.api.mmapi
			this.form.addCommand( this.cmdTakeScreenshot );
		//#endif
		this.form.addCommand( this.cmdQuit );
		this.form.setCommandListener( this.commandListener );
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
		this.form.deleteAll();
		display.setCurrent( this.form );
		Thread thread = new Thread( this );
		thread.start();
	}

	public void run() {
		try {
			//GameHighscore highscore = new GameHighscore( "PowerScore", new int[] {1,2,3,4,5,6,7,8,9,10});
			//highscore = this.server.storeHighscore(highscore, true );
			//System.out.println("highscore stored successfully:" + highscore.getName() );

			GameUser response = this.server.registerUser( System.currentTimeMillis(), "testuser", "password");
			this.form.deleteAll();
			this.form.setTitle("added user!");
			this.form.append( response.toString() );
			
			GameUser[] users = this.server.getAllUsers();
			System.out.println("Got users:");
			for (int i = 0; i < users.length; i++)
			{
				GameUser gameUser = users[i];
				System.out.println( i + ": " + gameUser.getUserName() );
			}
			
			//boolean pingSucceeded = this.server.ping( System.currentTimeMillis() );
			//System.out.println("ping succeeded: " + pingSucceeded);
			
		} catch (RemoteException e) {
			//#debug error
			System.out.println("Unable to access server" + e);
			this.form.append( e.toString() );
		} catch (DuplicateUserException e) {
			e.printStackTrace();
			System.out.println("Yeah, this is a dupe!");
			this.form.append( "Dupe=" + e.getDuplicateUserName() + " (" + e.getMessage() + ")");
		}
	}

	public void commandAction(Command cmd, Displayable dis) {
		if (cmd == this.cmdQuit ) {
			notifyDestroyed();
		} else if (cmd == this.cmdRegister) {
			Thread thread = new Thread( this );
			thread.start();			
		//#if polish.api.mmapi
		} else if (cmd == this.cmdTakeScreenshot) {
			if (dis == this.form ) {
				//#style mainScreen
				SnapshotScreen screen = new SnapshotScreen( "Snapshot" );
				screen.addCommand( this.cmdTakeScreenshot );
				screen.setCommandListener( this.commandListener );
				this.display.setCurrent( screen );
			} else {
				SnapshotScreen screen = (SnapshotScreen) dis;
				try {
					Image image = screen.getSnapshotImage();
					this.form.append(image);
					this.form.append("uploading image...");
					this.display.setCurrent( this.form );
					this.server.uploadScreenShot( image );
					this.form.append("Image uploaded!");
					this.form.setTitle("image uploaded");
				} catch (MediaException e) {
					Alert alert = new Alert( "Media-Error", e.toString(), null, AlertType.ERROR );
					System.out.println("returning to form...");
					this.display.setCurrent(alert, this.form);
				} catch (RemoteException e) {
					Alert alert = new Alert( "RMI-Error", e.toString(), null, AlertType.ERROR );
					this.display.setCurrent(alert, this.form);
				}
			}
		//#endif
		}
		
	}

}

