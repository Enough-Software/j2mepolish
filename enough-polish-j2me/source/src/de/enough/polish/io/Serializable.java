/*
 * Created on 13-Mar-2006 at 19:03:56.
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

/**
 * <p>An empty interface to flag classes that need to be processed by the J2ME Polish serializing framework.</p>
 * <p>Classes that implement the Serializable framework receive automatically 
 *    a default constructor( if not present) as well as the following methods:
 * </p>
 * <ul>
 * 	<li><b>public void write( DataOutputStream out )</b>: stores the internal instance fields to the output stream.</li>
 * 	<li><b>public void read( DataInputStream in )</b>: restores the internal instance fields from the given input stream.</li>
 * </ul>
 * 
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        13-Mar-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.io.Externalizable
 * @see de.enough.polish.io.Storage
 * @see de.enough.polish.io.RmsStorage
 */
public interface Serializable {
	// this interface just flags classes that need to be processed by the J2ME Polish serialization framework
}
