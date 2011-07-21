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

/**
 * <p>Stores log messages in the record store management system.</p>
 * <p>
 * Use the loghandler by specifying the following handler in your build.xml script:
 * <pre>
 * &lt;debug ...&gt;
 *    &lt;handler name=&quot;rms&quot; />
 * &lt;/debug&gt;
 * </pre>
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        26-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RmsLogHandler extends LogHandler {

	private RecordStore logStore;

	/**
	 * Creates a new log handler 
	 */
	public RmsLogHandler() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.log.LogHandler#handleLogEntry(de.enough.polish.log.LogEntry)
	 */
	public void handleLogEntry(LogEntry logEntry)
	throws Exception
	{
		//if (true) {
		//	throw new RuntimeException("FEHLERBEHANDLUNG KLAPPT!");
		//}
		if (this.logStore == null) {
			try {
				// try to delete last record store first
				RecordStore.deleteRecordStore( "j2mepolishlog" );
			} catch (Exception e) {
				// ignore
			}
			//#if polish.midp2
				this.logStore = RecordStore.openRecordStore( "j2mepolishlog", true, RecordStore.AUTHMODE_ANY, true );
			//#else
				this.logStore = RecordStore.openRecordStore( "j2mepolishlog", true );
			//#endif
		}
		byte[] data = logEntry.toByteArray();
		this.logStore.addRecord( data, 0, data.length );
		//this.logStore.closeRecordStore();
		//this.logStore = null;
		//System.out.println("Stored log in RMS!");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.log.LogHandler#exit()
	 */
	public void exit() {
		if (this.logStore != null) {
			try {
				this.logStore.closeRecordStore();
			} catch (RecordStoreException e) {
				// ignore
			}
			this.logStore = null;
		}
	}
	
	

}
