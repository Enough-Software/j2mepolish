/*
 * Created on Dec 12, 2007 at 11:58:19 PM.
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
package de.enough.polish.rmi.xmlrpc;

import de.enough.polish.rmi.RemoteException;

/**
 * <p>Provides access to XML RPC specific faults</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Dec 12, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class XmlRpcRemoteException extends RemoteException
{

	private final int faultCode;

	/**
	 * Creates a new remote exception.
	 * 
	 * @param faultCode the fault code of the server error
	 * @param message the message of the server error
	 */
	public XmlRpcRemoteException(int faultCode, String message)
	{
		super(message);
		this.faultCode = faultCode;
	}
	
	/**
	 * Retrieves the XML RPC fault code of the server.
	 * 
	 * @return an int with the fault code of the server
	 */
	public int getFaultCode() {
		return this.faultCode;
	}


}
