/*
 * Created on Jan 9, 2007 at 7:17:51 PM.
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 9, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DuplicateUserException extends Exception implements Externalizable {
	private String message;
	private String duplicateUserName;

	public DuplicateUserException() {
		 // for serialialization
	 }
	 
	 public DuplicateUserException( String message, String duplicateUserName ) {
		super( message );
		this.message = message;
		this.duplicateUserName = duplicateUserName;
	 }

	public void read(DataInputStream in) throws IOException {
		this.message = in.readUTF();
		this.duplicateUserName = in.readUTF();
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeUTF( this.message );
		out.writeUTF( this.duplicateUserName );
	}

	public String getDuplicateUserName() {
		return this.duplicateUserName;
	}
	
	public String getMessage() {
		return this.message;
	}

}

