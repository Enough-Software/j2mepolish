/*
 * Created on 27-Apr-2005 at 00:26:31.
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

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.log.LogEntry;
import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.splash.InitializerSplashScreen;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;

/**
 * <p>Displays the log-items.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        27-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LogView
//#ifdef polish.usePolishGui
	extends FramedForm
//#else
	//# extends Form
//#endif
implements Runnable
//#if polish.midp2 || polish.usePolishGui
, ItemStateListener
//#endif
{

	private final RecordStore logStore;
	private ArrayList logEntries;
	private int numberOfLogEntries;
	protected boolean showLevel = true;
	protected boolean showClassInfo = true;
	protected boolean showTime = false;
	protected TextField filterField;
	private boolean isFinishedLoading;

	/**
	 * Creates a new log view.
	 * 
	 * @param logStore the record store from wich the logs are loaded.
	 * @param screen the splash screen which can display a message about the loaded log entries.
	 */
	public LogView(RecordStore logStore ) {
		//#style logView
		super( Locale.get("title.logView") );
		this.logStore = logStore;
		//#style message
		append( Locale.get("message.pleaseWait") );		
		
		//#ifdef polish.usePolishGui
			//#style filterField
			this.filterField = new TextField( null, "", 30, TextField.ANY );
			append( Graphics.BOTTOM, this.filterField );
			setItemStateListener( this );
		//#endif
		
		Thread thread = new Thread( this );
		thread.start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// sleep just a little so that the form is really shown...
		try {
			Thread.sleep( 100 );
		} catch (InterruptedException e) {
			// ignore
		}
		try {
			RecordEnumeration enumeration = this.logStore.enumerateRecords(null, null, false);
			this.numberOfLogEntries = enumeration.numRecords();
			while ( enumeration.hasNextElement() ) {
				byte[] data = enumeration.nextRecord();
				LogEntry entry = LogEntry.newLogEntry( data );
				addLogEntry( entry, null );
			}
			this.logStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			notifyInvalidLogFormat();
		}
		notifyFinishedLoading();
	}

	/**
	 * 
	 */
	private void notifyFinishedLoading() {
		this.isFinishedLoading = true;
	}

	/**
	 * @param entry
	 */
	private void addLogEntry(LogEntry entry, String filter) {
		//#ifdef polish.usePolishGui
			if ( matches( entry, filter ) ) {
		//#endif
			StringBuffer buffer = new StringBuffer();
			if (this.showLevel) {
				buffer.append( "[" ).append( entry.level ).append("] ");
			}
			if (this.showClassInfo) {
				int lastDotPos = entry.className.lastIndexOf('.');
				if (lastDotPos != -1) {
					buffer.append( entry.className.substring( lastDotPos + 1));
				} else {
					buffer.append( entry.className );
				}
				buffer.append( " <" ).append( entry.lineNumber ).append( ">: ");
			}
			if (this.showTime) {
				buffer.append( entry.time ).append(": ");
			}
			buffer.append( entry.message );
			if ( entry.exception.length() > 0 ) {
				buffer.append( '*' ).append( entry.exception ).append('*');
			}
			//#style logEntry
			StringItem item = new StringItem( null, buffer.toString(), StringItem.BUTTON );
			append( item );
		//#ifdef polish.usePolishGui
			}
		//#endif
		if (!this.isFinishedLoading) {
			if (this.logEntries == null) {
				this.logEntries = new ArrayList( Math.max( this.numberOfLogEntries, 10 ) );
				// delete "please wait" message:
				delete( 0 );
			}
			this.logEntries.add( entry );
		}
	}

	/**
	 * 
	 */
	private void notifyInvalidLogFormat() {
		//#if polish.midp2 || polish.usePolishGui
			deleteAll();
		//#else
			while (size() > 0) {
				delete( 0 );
			}
		//#endif
		//#style message
		append( Locale.get("message.invalidLogFormat") );		
	}
	
	private boolean matches( LogEntry entry, String filter ) {
		if (filter == null || filter.length() == 0) {
			return true;
		}
		return entry.level.indexOf( filter ) != -1
			|| entry.className.indexOf( filter ) != -1
			|| entry.message.indexOf( filter ) != -1
			|| entry.exception.indexOf( filter ) != -1;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.ItemStateListener#itemStateChanged(javax.microedition.lcdui.Item)
	 */
	public void itemStateChanged(Item item) {
		if (!this.isFinishedLoading) {
			return;
		}
		int size = this.logEntries.size();
		String filter = this.filterField.getString();
		//#if polish.midp2 || polish.usePolishGui
			deleteAll();
		//#else
			while (size() > 0) {
				delete( 0 );
			}
		//#endif
		for (int i = 0; i < size; i++) {
			LogEntry entry = (LogEntry) this.logEntries.get(i);
			addLogEntry( entry, filter );
		}
	}

	/**
	 * @return all entries
	 */
	public ArrayList getLogEntries() {
		return this.logEntries;
	}


}
