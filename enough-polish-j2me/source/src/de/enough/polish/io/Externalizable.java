/*
 * Created on 13-Mar-2006 at 19:20:28.
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
package de.enough.polish.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>Defines methods for serializing and de-serializing classes.</p>
 * <p>Note that classes implementing the Externalizable interface are required
 *    to provide a default constructor without any parameters.
 *    This is turn means that no final instance fields are allowed.
 * </p>
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        13-Mar-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.io.Storage
 * @see de.enough.polish.io.RmsStorage
 */
public interface Externalizable extends Serializable {
	
	/**
	 * Stores the internal instance fields to the output stream.
	 * 
	 * @param out the output stream to which instance fields should be written
	 * @throws IOException when writing fails
	 */
	void write( DataOutputStream out )
	throws IOException;
	
	/**
	 * Restores the internal instance fields from the given input stream.
	 * 
	 * @param in the input stream from which the data is loaded
	 * @throws IOException when reading fails
	 */
	void read( DataInputStream in )
	throws IOException;
	
	/**
	 * Retrieves the unique serial ID for this class.
	 * The serial ID needs to be unique because the ID is stored instead of the classname for the serialization.
	 * Goals:
	 * - ensure integrity between different versions of Serializable classes
	 * - use (relatively) short serial IDs instead of classnames for identifying classes
	 *  
	 * @return the serial ID.
	 * @see Serializer#calculateSerialVersionId( Serializable )
	 * @see Serializer#getClassName( long serialId )
	 */
	//long getSerialVersionId();
	/*
	 * Problems with this approach:
	 * 1. we don't want to re-calculate the serial ID each time we serialize an Externalizable class, this can happen with this method,
	 *    especially when we have a Serilizer.calculateSerialVersionId. Ideally we would have a static method.
	 * 2. We need to ensure integrity of serial ID and its classname (classname needs to be mapped)
	 * 3. Serial ID is required for ensuring the integrity between different versions of the same class - this will be difficult
	 *    to implement during runtime (however, when the Externalizable is implemented, this should not really be an issue)
	 */
	

}
