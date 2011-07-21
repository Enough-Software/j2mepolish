/*
 * Created on Dec 18, 2006 at 2:16:07 AM.
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

/**
 * <p>The base interface for any remote services. Defined methods need to declare to throw at least a RemoteException.</p>
 *
 * <p>Copyright Enough Software 2006, 2007 - 2009</p>
 * <pre>
 * history
 *        Dec 18, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface Remote {
	/** indicates the method call finished normally */
	int STATUS_OK = 0;
	/** indicates the method call threw a checked exception */
	int STATUS_CHECKED_EXCEPTION = 1;
	/** indicates the method call threw an unchecked exception */
	int STATUS_UNCHECKED_EXCEPTION = 2;
}
