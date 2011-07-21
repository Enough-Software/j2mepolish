/*
 * Created on 13-Mar-2006 at 20:02:57.
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

import java.io.IOException;
import java.util.Enumeration;

/**
 * <p>Base interface for storage systems.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        13-Mar-2006 - rob creation
 * </pre>
 * @param <K> when you use the enough-polish-client-java5.jar you can parameterize the Storage subclasses, e.g. RmsStorage&lt;Vector&lt;Note&gt;&gt;.
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface Storage 
//#if polish.java5
<K>
//#endif
{
	/**
	 * Serializes the given object and stores is under the given name.
	 * 
	 * @param object the object
	 * @param name the name under which the object should be stored
	 * @throws IOException when serializing or storage fails
	 */
	//#if polish.java5
		void save(K object, String name)
	//#else
		//# void save(Object object, String name)
	//#endif
		throws IOException;
	
//	/**
//	 * Serializes the given objects and stores them under the given name.
//	 * All objects need to be of the same class for this operation, since the class information is only stored once.
//	 * 
//	 * @param objects the objects
//	 * @param name the name under which the object should be stored
//	 * @throws IOException when serializing or storage fails
//	 */
//	void saveAll( Serializable[] objects, String name )
//	throws IOException;
	
	/**
	 * Reads and de-serializes the given object which has been previously saved under the given name.
	 * 
	 * @param name the name under which the object has been stored
	 * @return the initialized object
	 * @throws IOException when de-serializing or reading fails
	 */
	//#if polish.java5
		K read( String name )
	//#else
		//# Object read( String name )
	//#endif
		throws IOException;
	
	/**
	 * Enumerates the objects that have been previously stored under the given name.
	 * 
	 * @param name the name under which the objects have been stored
	 * @return an enumeration containing all objects
	 * @throws IOException when de-serializing or reading fails
	 *
	 */
//	@see #saveAll(Serializable[], String)
	Enumeration enumerate( String name )
	  throws IOException;
	
//	/**
//	 * Reads all the objects that have been previously stored under the given name.
//	 * When there are many objects that can be handled singlely the enumerate() method
//	 * is more efficient. 
//	 * 
//	 * @param name the name under which the objects have been stored
//	 * @return an array containing all initialized objects
//	 * @throws IOException when de-serializing or reading fails
//	 * @see #saveAll(Serializable[], String)
//	 */
//	Serializable[] readAll( String name )
//	throws IOException;

	/**
	 * Retrieves a list of all entries that have been stored in this storage system.
	 * WARNING: Not all storage implementations might support this.
	 *  
	 * @return an array of names of stored objects.
	 * @throws IOException when the names could not be read
	 * @throws IllegalStateException whent the storage implementation or configuration does not support a listing of names.
	 */
	String[] list()
	  throws IOException;
  
  /**
   * Delete the object with the given name.
   * 
   * @param name the name under which the object has been stored
   * @throws IOException when deletion fails
   */
  void delete( String name )
    throws IOException;
}
