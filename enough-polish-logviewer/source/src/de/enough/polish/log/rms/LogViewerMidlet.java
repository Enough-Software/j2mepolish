/*
 * Created on 26-Apr-2005 at 23:35:07.
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
package de.enough.polish.log.rms;


import java.io.DataOutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
//#if polish.api.pdaapi
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
//#endif
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.log.LogEntry;
import de.enough.polish.ui.splash.ApplicationInitializer;
import de.enough.polish.ui.splash.InitializerSplashScreen;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;

/**
 * <p>Views the log that has been written to the RMS by another MIDlet.</p>
 * <p>You need to specify some preprocessing variables when you want to
 *    build this viewer:</p>
 * <ul>
 *   <li>polish.log.MIDletSuite: The name of the MIDlet suite to which this log belongs to, compare javax.microedition.rms.RecordStore.openRecordStore(String, String, String).</li>
 *   <li>polish.log.Vendor: The name of the MIDlet vendor (see the "vendor" attribute of your &lt;info&gt; element, compare javax.microedition.rms.RecordStore.openRecordStore(String, String, String).</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Apr-2005 - rob creation
 * </pre>
 * @see javax.microedition.rms.RecordStore#openRecordStore(java.lang.String, java.lang.String, java.lang.String)
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LogViewerMidlet 
extends MIDlet
//#if !polish.classes.ApplicationInitializer:defined
	implements CommandListener, ApplicationInitializer, Runnable 
//#else
	//# implements CommandListener, Runnable
//#endif

{

	private Display display;
	private final Command exitCmd = new Command( Locale.get("cmd.quit"), Command.SCREEN, 10 );
	private final Command clearLogCmd = new Command( Locale.get("cmd.clearLog"), Command.SCREEN, 10 );
	private final Command uploadLogCmd = new Command( Locale.get("cmd.uploadLog"), Command.SCREEN, 10 );
	//#ifdef polish.api.pdaapi
		private final Command saveLogCmd = new Command( Locale.get("cmd.saveLog"), Command.SCREEN, 10 );
	//#endif
	private LogView logView;
	private RecordStore logStore;
	private Alert deleteAlert;
	private InitializerSplashScreen splashScreen;
	
	//private final Command saveLogCmd2 = new Command( Locale.get("cmd.save2Log"), Command.SCREEN, 10 );


	/**
	 * Creates a new MIDlet.
	 */
	public LogViewerMidlet() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		this.display = Display.getDisplay( this );
		Image splashImage = null;
		try {
			splashImage = Image.createImage( "/splash.png" );
		} catch (Exception e) {
			e.printStackTrace();
		}
		int backgroundColor =  0xebe8e0;
		this.splashScreen = new InitializerSplashScreen( 
				this.display, 
				splashImage,
				backgroundColor, 
				null, // no message, so we proceed to the initial screen as soon as possible
				0,    // since we have no message, there's no need to define a message color
				this );
		this.display.setCurrent( this.splashScreen );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// ignore
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// just quit
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable displayable) {
		if (cmd == this.exitCmd ) {
			notifyDestroyed();
		} else if (cmd == this.clearLogCmd ) {
			if (this.deleteAlert == null) {
				this.deleteAlert = new Alert( "Deleting",  "Preparing to delete entries....", null, AlertType.INFO  );
				this.display.setCurrent( this.deleteAlert );
				Thread thread = new Thread( this );
				thread.start();
			}
			return;
		} else if ( cmd == this.uploadLogCmd ) {
			new HttpUploader( this.logView.getLogEntries(), this.display, this.logView );
		}
		//#ifdef polish.api.pdaapi
			else if ( cmd ==  this.saveLogCmd  ) {
				String message = null;
				FileConnection connection = null;
				String url = null;
				Enumeration rootEnumeration = null;
				try {
					rootEnumeration = FileSystemRegistry.listRoots();
					String root = (String) rootEnumeration.nextElement();
					url = "file:///" + root + "j2melog.txt";
					//url = "file://C:/j2melog.txt";
					connection = (FileConnection) Connector.open( url, Connector.READ_WRITE );
					if (!connection.exists()) {
						connection.create();
					}
					DataOutputStream out = connection.openDataOutputStream();
					out.writeUTF("time\tthread\tlevel\tclass\tline\tmessage\terror");
					ArrayList entriesList = this.logView.getLogEntries();
					int i = 0;
					StringBuffer buffer = new StringBuffer();					
					while (i < entriesList.size()) {
						LogEntry entry = (LogEntry) entriesList.get( i );
						buffer.append( entry.time ).append('\t')
							.append( entry.thread ).append('\t')
							.append( entry.level ).append('\t')
							.append( entry.className ).append('\t')
							.append( entry.lineNumber ).append('\t')
							.append( entry.message ).append('\t')
							.append( entry.exception ).append('\n');
						out.writeUTF( buffer.toString() );
						buffer.delete(0, buffer.length() );
						i++;
					}
					out.close();
					message = Locale.get("message.savedLogTo", url );
				} catch (Exception e) {
					String errorMessage = e.toString() + "(" + url + ")";
					while (rootEnumeration.hasMoreElements()) {
						errorMessage += ", " + rootEnumeration.nextElement();
					}
					message = Locale.get("message.unableToSave", errorMessage );
					e.printStackTrace();
				} finally {
					if ( connection != null ) {
						try {
							connection.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				Alert alert = new Alert( "error",  message, null, AlertType.INFO );
				alert.setTimeout( Alert.FOREVER );
				this.display.setCurrent( alert, displayable );
			}
		//#endif

		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.splash.ApplicationInitializer#initApp()
	 */
	public Displayable initApp() {
		Exception error = null; 
		try {
			try {
				//#ifdef polish.midp1
					this.logStore = RecordStore.openRecordStore("j2mepolishlog", false);
				//#else
					//#if !(polish.log.MIDletSuite:defined && polish.log.Vendor:defined)
						//#abort You need to defined the preprocessing variables polish.log.MIDletSuite and polish.log.Vendor!
					//#endif
					//#= this.logStore = RecordStore.openRecordStore( "j2mepolishlog", "${polish.log.Vendor}", "${polish.log.MIDletSuite}" );
				//#endif
			} catch (RecordStoreException e) {
				error = e;
				e.printStackTrace();
			}	 
			if (this.logStore != null) {
				this.logView = new LogView( this.logStore );
				this.logView.addCommand( this.exitCmd );
				this.logView.addCommand( this.clearLogCmd );
				this.logView.addCommand( this.uploadLogCmd );
				//#ifdef polish.api.pdaapi
					this.logView.addCommand( this.saveLogCmd  );
				//#endif
				this.logView.setCommandListener( this );
				return this.logView;
			} 
		} catch (Exception e) {
			error = e;
		} finally {
			this.splashScreen = null;
		}
		// found no suitable record logs... show error
		//#style errorForm
		Form errorForm = new Form( Locale.get("title.error") );
		//#style message
		errorForm.append( Locale.get("message.noRecordStoreFound") );
		if (error != null) {
			//#style message
			errorForm.append( error.toString() );
		}
		String[] stores = RecordStore.listRecordStores();
		if ( stores != null ) {
			for (int i = 0; i < stores.length; i++) {
				//#style message
				errorForm.append( stores[i] );
			}
		}
		errorForm.addCommand(this.exitCmd);
		errorForm.setCommandListener( this );
		return errorForm;
	}

	public void run() {
		String lastAction = "about to open store...";
		try {
			int numberOfLogEntries = 0;
			if (this.logStore != null) {
				//#ifdef polish.midp1
					this.logStore = RecordStore.openRecordStore("j2mepolishlog", false);
				//#else
					//#if !(polish.log.MIDletSuite:defined && polish.log.Vendor:defined)
						//#abort You need to defined the preprocessing variables polish.log.MIDletSuite and polish.log.Vendor!
					//#endif
					try {
					//#= this.logStore = RecordStore.openRecordStore( "j2mepolishlog", "${polish.log.Vendor}", "${polish.log.MIDletSuite}" );
					} catch (RuntimeException e) {
						//#debug error
						System.out.println("Unable to open log in a MIDP 2.0 manner" + e );
						this.logStore = RecordStore.openRecordStore("j2mepolishlog", false);
					}
				//#endif
				lastAction = "store opened";
				RecordEnumeration enumeration = this.logStore.enumerateRecords(null, null, false);
				numberOfLogEntries = enumeration.numRecords();
				lastAction = "enumeration opened";
				//#if polish.midp2
					//javax.microedition.lcdui.Gauge gauge = new javax.microedition.lcdui.Gauge( null, false,numberOfLogEntries, 0 );
					//this.deleteAlert.setIndicator( gauge );
				//#endif

				int[] idsArray = new int[ numberOfLogEntries ];
				int i = 0;
				lastAction = "about to read IDs";
				while ( enumeration.hasNextElement() ) {
					int id = enumeration.nextRecordId();
					idsArray[ i ] = id;
					i++;
				}
				enumeration.destroy();
				lastAction = "read IDs";
				for (int j = 0; j < idsArray.length; j++) {
					int id = idsArray[j];
					this.logStore.deleteRecord( id );
					//#if polish.midp2
						//gauge.setValue( j );
					//#endif
				}
				Alert alert = new Alert( "ok",  "removed all entries", null, AlertType.INFO  );
				alert.setTimeout( Alert.FOREVER );
				this.display.setCurrent( alert, this.logView );
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			Alert alert = new Alert( "error",  "Unable to delete log: " + e.toString() + ", lastAction=" + lastAction, null, AlertType.ERROR  );
			alert.setTimeout( Alert.FOREVER );
			this.display.setCurrent( alert, this.logView );
		}	
	}

}
