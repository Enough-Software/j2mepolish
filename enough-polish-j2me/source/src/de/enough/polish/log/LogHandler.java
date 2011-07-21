/*
 * Created on 26-Apr-2005 at 12:38:19.
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
package de.enough.polish.log;

/**
 * <p>Handles log entries.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        26-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class LogHandler {

	/**
	 * Creates a new log handler.
	 * For initializing a log handler, please use the preprocessing configuration.
	 */
	public LogHandler() {
		super();
	}
	
	/**
	 * Handles the given log entry.
	 * 
	 * @param logEntry the entry
	 * @throws Exception when the entry could not be handled
	 */
	public abstract void handleLogEntry( LogEntry logEntry )
	throws Exception;
	
	/**
	 *  Is called before the corresponding MIDlet exits.
	 *  
	 *  Subclasses can override this method for cleaning up.
	 *
	 */
	public void exit() {
		// ignore
	}

}
