//#condition polish.api.btapi

/*
 * Created on 27-Apr-2005 at 20:00:55.
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
package de.enough.polish.log.bluetooth;

import java.io.IOException;

import javax.bluetooth.L2CAPConnection;

import de.enough.polish.bluetooth.DiscoveryHelper;
import de.enough.polish.bluetooth.L2CapOutputStream;
import de.enough.polish.log.LogEntry;
import de.enough.polish.log.LogHandler;
import de.enough.polish.util.ArrayList;

/**
 * <p>Forwards log messages to the first preknown device.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        27-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BluetoothLogHandler 
extends LogHandler
implements Runnable
{
	private final static String UUID = "21dc585c319b4dc39cf8457e90a07444";

	private ArrayList buffer;
	private Exception exception;
	private L2CAPConnection connection;
	
	/**
	 * Creates a new handler 
	 */
	public BluetoothLogHandler() {
		super();
		Thread thread = new Thread( this );
		thread.start();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.log.LogHandler#handleLogEntry(de.enough.polish.log.LogEntry)
	 */
	public void handleLogEntry(LogEntry logEntry) 
	throws Exception 
	{
		if (this.connection != null) {
			sendLogEntry( logEntry );
		} else {
			ArrayList list = this.buffer;
			if (list != null) {
				list.add( logEntry );
			} else if (this.exception != null) {
				Exception e = this.exception;
				this.exception = null;
				throw e;
			}
		}
		
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			this.connection = (L2CAPConnection) DiscoveryHelper.findAndConnectService(UUID, DiscoveryHelper.SEARCH_MODE_GIAC, DiscoveryHelper.DEVICE_CLASS_MAJOR_PC);
			ArrayList list = this.buffer;
			if (list == null) {
				return;
			}
			this.buffer = null;
			for (int i = 0; i < list.size(); i++ ) {
				LogEntry entry = (LogEntry) list.get( i );
				sendLogEntry( entry );
			}
		} catch (Exception e) {
			this.exception = e;
			e.printStackTrace();
			this.buffer = null;
		}
	}

	/**
	 * @param entry
	 * @throws IOException 
	 */
	private void sendLogEntry(LogEntry entry) throws IOException
	{
		L2CapOutputStream out = new L2CapOutputStream(this.connection);
		out.write( entry.toString().getBytes() );
		out.close();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.log.LogHandler#exit()
	 */
	public void exit()
	{
		super.exit();
		if (this.connection != null) {
			try
			{
				this.connection.close();
			} catch (IOException e)
			{
				// ignore
			}
		}
	}


	
}
