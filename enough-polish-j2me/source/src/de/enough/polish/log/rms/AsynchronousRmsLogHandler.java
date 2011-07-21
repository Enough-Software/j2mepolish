//#condition polish.midp || polish.usePolishGui
/*
 * Created on 26-Apr-2005 at 13:54:17.
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
package de.enough.polish.log.rms;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.log.LogEntry;
import de.enough.polish.log.LogHandler;
import de.enough.polish.util.ArrayList;

/**
 * <p>Stores log messages in an asynchronous way in the record store management system.</p>
 * <p>
 * Use the loghandler by specifying the following handler in your build.xml script:
 * <pre>
 * &lt;debug ...&gt;
 *    &lt;handler name=&quot;asynchronousrms&quot; />
 * &lt;/debug&gt;
 * </pre>
 * </p>
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        06-Aug-2008 - timon creation
 * </pre>
 * @author Timon Gruetzmacher, j2mepolish@enough.de
 */
public class AsynchronousRmsLogHandler extends LogHandler implements Runnable{
	/** The record store to log in */
	private RecordStore logStore;
	/** A flag indicating shutting down */
	private boolean isShuttingDown;
	/** The list containing log entries to be written */
	private ArrayList scheduledLogEntries;
	/** A flag indicating a permanent log error */
	private boolean isPermanentLogError;

	/**
	 * Creates a new asynchronous rms log handler.
	 */
	public AsynchronousRmsLogHandler() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.log.LogHandler#handleLogEntry(de.enough.polish.log.LogEntry)
	 */
	public void handleLogEntry(LogEntry entry) throws Exception {
		if (this.isPermanentLogError) {
			return;
		}
		if (this.scheduledLogEntries == null) {
			this.scheduledLogEntries = new ArrayList( 7 );
			Thread thread = new Thread( this );
			thread.start();
		}
		synchronized ( this.scheduledLogEntries ) {
			this.scheduledLogEntries.add(entry);
			this.scheduledLogEntries.notify();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.log.LogHandler#exit()
	 */
	public void exit() {
		super.exit();
		if (this.logStore != null) {
			try {
				this.logStore.closeRecordStore();
			} catch (RecordStoreException e) {
				// ignore
			}
			this.logStore = null;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//check the record store
		if (this.logStore == null) {
			try {
				// try to delete last record store first
				RecordStore.deleteRecordStore( "j2mepolishlog" );
			} catch (Exception e) {
				// ignore
			}
			try {
				//#if polish.midp2
				this.logStore = RecordStore.openRecordStore( "j2mepolishlog", true, RecordStore.AUTHMODE_ANY, true );
				//#else
				this.logStore = RecordStore.openRecordStore( "j2mepolishlog", true );
				//#endif
			}catch (Exception e) {
				e.printStackTrace();
				System.err.println("Unable to create record store: " + e );
				this.isPermanentLogError = true;
				return;
			}
		}

		while (!this.isShuttingDown) {
			while ( this.scheduledLogEntries.size() != 0 ) {
				LogEntry entry;
				synchronized ( this.scheduledLogEntries ) {
					entry = (LogEntry) this.scheduledLogEntries.remove(0);
				}
				try{
					byte[] data = entry.toByteArray();
					this.logStore.addRecord( data, 0, data.length );
				}catch (Exception e) {
					e.printStackTrace();
					System.err.println("Unable to write log entry: " + e );
				}
			}
			// wait for next log entry:
			try {
				synchronized ( this.scheduledLogEntries ) {
					this.scheduledLogEntries.wait();
				}
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

}
