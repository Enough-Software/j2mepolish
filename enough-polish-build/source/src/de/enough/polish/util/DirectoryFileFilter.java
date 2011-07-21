/*
 * Created on Oct 6, 2006 at 8:47:33 PM.
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
package de.enough.polish.util;

import java.io.File;
import java.io.FileFilter;

/**
 * <p>Filters directories in File.listFiles() calls.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Oct 6, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DirectoryFileFilter implements FileFilter {
	
	private final boolean acceptDirs;

	/**
	 * Creates a new directory filter
	 * 
	 * @param acceptDirs true when only directories should returned, false when only files that are not directories should be returned
	 */
	public DirectoryFileFilter( boolean acceptDirs ) {
		this.acceptDirs = acceptDirs;
	}

	public boolean accept(File file) {
		return file.isDirectory() == this.acceptDirs;
	}

}
