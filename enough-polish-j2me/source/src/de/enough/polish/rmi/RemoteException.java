/*
 * Created on Dec 20, 2006 at 11:16:26 AM.
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
package de.enough.polish.rmi;

import java.io.IOException;

/**
 * <p>An unchecked exception.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Dec 20, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RemoteException extends IOException {
	
	private Throwable cause;

	/**
	 * Creates a new remote exception.
	 * 
	 * @param message the message of the exception.
	 */
	public RemoteException( String message ) {
		super( message );
	}
	
	/**
	 * Creates a new remote exception.
	 * 
	 * @param cause the wrappend checked exception of the exception.
	 */
	public RemoteException( Throwable cause ) {
		super( cause.toString() );
		this.cause = cause;
	}
	
	/**
	 * Retrieves the wrapped checked exception, if any.
	 * 
	 * @return the checked exception that has occurred - can be null.
	 */
	public Throwable getCause() {
		return this.cause;
	}
	
}
