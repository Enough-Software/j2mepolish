/*
 * Created on Jan 12, 2007 at 12:49:07 PM.
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
package de.enough.polish;

/**
 * <p>An exception indicating that a build step has failed.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 12, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BuildException extends RuntimeException {

	private static final long serialVersionUID = -3310966948356704622L;

	/**
	 * Creates a new empty BuildException 
	 */
	public BuildException() {
		super();
	}

	/**
	 * Creates a empty BuildException 
	 * @param message the message indicating the cause of the error
	 */
	public BuildException(String message) {
		super(message);
	}

	/**
	 * Creates a empty BuildException 
	 * @param cause the cause of the error
	 */
	public BuildException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a empty BuildException 
	 * @param message the message indicating the cause of the error
	 * @param cause the cause of the error
	 */
	public BuildException(String message, Throwable cause) {
		super(message, cause);
	}

}
