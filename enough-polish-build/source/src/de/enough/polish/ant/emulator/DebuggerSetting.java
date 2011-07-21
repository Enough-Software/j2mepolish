/*
 * Created on 27-Oct-2005 at 00:37:12.
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
package de.enough.polish.ant.emulator;

import de.enough.polish.ExtensionSetting;
import de.enough.polish.util.CastUtil;

public class DebuggerSetting extends ExtensionSetting {
	
	private int port = 8000;
	private String transport = "dt_socket";
	private boolean isServer = true;
	private boolean isSuspend = false;

	public DebuggerSetting() {
		super();
		this.name="default";
	}

	/**
	 * @return Returns debugger the port, this defaults to 8000.
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * @param port The debugger port. Default port is 8000.
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setAddress( int address ){
		setPort( address );
	}
	
	public void setTransport( String transport ) {
		this.transport = transport;
	}
	
	public String getTransport() {
		return this.transport;
	}
	
	public void setServer( String isServerStr ) {
		this.isServer = CastUtil.getBoolean( isServerStr );
	}
	
	public boolean isServer() {
		return this.isServer;
	}
	
	public void setSuspend( String isSuspendStr ) {
		this.isSuspend = CastUtil.getBoolean(isSuspendStr);
	}
	
	public boolean isSuspend() {
		return this.isSuspend;
	}

}
