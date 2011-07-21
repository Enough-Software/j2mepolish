/*
 * Created on 06-Nov-2005 at 16:05:03.
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
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.log.LogEntry;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;

/**
 * <p>Uploads a complete log to a webserver for further analysis.</p>
 * <p>This class supports some preprocessing settings for the default
 *    settings:</p>
 * <ul>
 *   <li>polish.log.UploadServer: The complete address of the upload server which 
 *   receives the data in a GET request with three parameters:
 *   <br />
 *   vendor: the vendor of the MIDlet.
 *   <br />
 *   suite: the name of the MIDlet suite.
 *   <br />
 *   pw: an optional password, might be empty.
 *   <br />
 *   The actual data (= the log entries) is in a specific format that transmits all log entries. 
 *   <br />
 *   Default upload server is
 *   "http://enough.dyndns.org/logger".
 *   </li>
 *   <li>polish.log.Password: A standard password, is empty by default.</li>
 *   <li></li>
 * </ul>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        06-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HttpUploader
implements CommandListener, Runnable
{
	
	private static final int VERSION = 100;
	private static final int MAX_REDIRECTS = 3;
	//#if polish.log.UploadServer:defined
		//#= private String uploadServer = "${polish.log.UploadServer}";
	//#else
		private String uploadServer = "http://enough.dyndns.org/logger";
	//#endif
	
	//#if polish.log.Password:defined
		//#= private String password = "${polish.log.Password}";
	//#else
		private String password = "";
	//#endif
		
	private final Display display;
	private final Displayable returnScreen;
	
	private TextField uploadServerField;
	private TextField passwordField;
	private final Command cancelCommand = new Command(  Locale.get("cmd.cancel"), Command.CANCEL, 2 );
	private final Command okCommand = new Command( Locale.get("cmd.ok"), Command.OK, 1 );
	private final ArrayList logEntries;
	private boolean isCanceled;
	private boolean uploadingFinished;
	

	/**
	 * Creates a new uploader.
	 * The settings are loaded from the recordstore and can be edited
	 * before the log is uploaded.
	 * 
	 * @param logEntries the actual entries 
	 * @param display display for settings screens and progress dialogs. 
	 * @param returnScreen  the screen to which the uploader should return when the upload has been finished or cancelled.
	 */
	public HttpUploader( ArrayList logEntries, Display display, Displayable returnScreen ) {
		super();
		this.logEntries = logEntries;
		this.display = display;
		this.returnScreen = returnScreen;
		loadSettings();
		showSettings();
	}
	private void showSettings() {
		//#style uploadSettingsForm
		Form settingsForm = new Form( Locale.get("title.upload.settings") );
		if (this.logEntries == null || this.logEntries.size() == 0) {
			//#style message
			settingsForm.append( Locale.get("message.upload.noLogEntries") );					
		} else {
			//#style input
			this.uploadServerField = new TextField( Locale.get("input.upload.serverUrl"), this.uploadServer, 60, TextField.URL );
			//#style input
			this.passwordField = new TextField( Locale.get("input.upload.password"), this.password, 30, TextField.ANY );
			settingsForm.append( this.uploadServerField );
			settingsForm.append( this.passwordField );
			settingsForm.addCommand(this.okCommand);
		}
		settingsForm.addCommand(this.cancelCommand);
		settingsForm.setCommandListener( this );
		this.display.setCurrent( settingsForm );
	}
	private void loadSettings() {
		try {
			RecordStore store = RecordStore.openRecordStore( "HttpSettings", false);
			int nextRecordId = store.getNextRecordID();
			this.uploadServer = new String( store.getRecord(nextRecordId - 2) );
			this.password = new String( store.getRecord(nextRecordId - 1) );
			store.closeRecordStore();
		} catch (RecordStoreException e) {
			//#debug warn
			System.out.println("Unable to read HTTP upload settings: " + e.toString() );
		}
	}
	
	private void saveSettings( String newServerUrl, String newPassword ) {
		this.uploadServer = newServerUrl;
		this.password = newPassword;
		try {
			RecordStore store = RecordStore.openRecordStore( "HttpSettings", true);
			int nextRecordId = store.getNextRecordID();
			byte[] data = this.uploadServer.getBytes();
			if (nextRecordId < 2) {
				store.addRecord( data, 0, data.length );
				data = this.password.getBytes();
				store.addRecord( data, 0, data.length );
			} else {
				store.setRecord( nextRecordId - 2, data, 0, data.length );
				data = this.password.getBytes();
				store.setRecord( nextRecordId - 1, data, 0, data.length );				
			}
			store.closeRecordStore();
		} catch (RecordStoreException e) {
			//#debug warn
			System.out.println("Unable to read HTTP upload settings: " + e.toString() );
		}
	}

	public void commandAction(Command cmd, Displayable screen) {
		if (cmd == this.cancelCommand ) {
			this.isCanceled = true;
			this.display.setCurrent( this.returnScreen );
		} else if ( cmd == this.okCommand ) {
			if (this.uploadingFinished) {
				this.display.setCurrent( this.returnScreen );
				return;
			}
			String newServerUrl = this.uploadServerField.getString();
			String newPassword = this.passwordField.getString();
			if ( !this.uploadServer.equals( newServerUrl ) || !this.password.equals( newPassword )) {
				saveSettings( newServerUrl, newPassword );
			}
			Thread thread = new Thread( this );
			thread.start();
		}
	}
	
	/**
	 * Uploads the HTTP data in the background...
	 */
	public void run() {
		//#style activityMonitor
		Gauge gauge = new Gauge( null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING );
		//#style uploadScreen
		Form form = new Form( Locale.get("title.upload.screen") );
		//#style message
		StringItem item = new StringItem( null, Locale.get("message.uploading") );
		form.append( item );
		form.append( gauge );
		form.addCommand( this.cancelCommand );
		form.setCommandListener( this );
		this.display.setCurrent( form );
		HttpConnection con = null;
		try {
			int responseCode = 0;
			int size = this.logEntries.size();
			//#if polish.log.MidletSuite:defined
				//#= String url = this.uploadServer + "?password=" + TextUtil.encodeUrl( this.password ) + "&vendor=${ encodeurl( polish.log.Vendor ) }&suite=${ encodeurl( polish.log.MIDletSuite) }" ;
			//#else
				String url = this.uploadServer + "?password=" + TextUtil.encodeUrl( this.password );
			//#endif
			for (int tries = 0; tries < MAX_REDIRECTS; tries++ ) {
				con = (HttpConnection) Connector.open( url, Connector.READ_WRITE );
				con.setRequestMethod( HttpConnection.GET );
				con.setRequestProperty("Connection", "close" );
				con.setRequestProperty("User-Agent", "LogViewerUploader" );
				con.setRequestProperty("Content-Language", "en-US" );
				con.setRequestProperty("Accept", "application/octet-stream" );
				con.setRequestProperty("Connection", "close" );
				//con.setRequestProperty("Content-Length", Integer.toString( data.length ) );				
				// write data:
				DataOutputStream out = con.openDataOutputStream();
				out.writeInt( VERSION );
				out.writeInt( size );
				for (int i = 0; i < size; i++) {
					if (this.isCanceled) {
						return;
					}
					LogEntry entry = (LogEntry) this.logEntries.get( i );
					entry.write( out );
					String number = "" + (i + 1);
					item.setText( Locale.get("message.uploadingNr", number) );
					form.set( 0, item );
				}
				// trigger opening of connection:
				con.openInputStream();
				responseCode = con.getResponseCode();
				if ( responseCode == HttpConnection.HTTP_MOVED_TEMP ||
						responseCode == HttpConnection.HTTP_SEE_OTHER )
				{
					url = con.getHeaderField("Location");
				} else {
					break;
				}
			}
			form.deleteAll();
			if (responseCode == HttpConnection.HTTP_OK ) {
				String number = "" + size;
				form.append( Locale.get("message.upload.success", number ) );
				form.removeCommand( this.cancelCommand );
				form.addCommand( this.okCommand );
			} else {
				String[] params = new String[] { "" + responseCode, url };
				String message = Locale.get("message.upload.failure", params );
				//#debug error
				System.out.println( message );
				form.append( message );
				form.append( con.getResponseMessage() );
				
			}
		} catch (Exception e) {
			form.deleteAll();
			String code = "" + e.toString();
			form.append( Locale.get("message.upload.error", code ) );				

		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to close HTTP Upload Connection" + e );
				}
			}
			this.uploadingFinished = true;
		}
	}

}
