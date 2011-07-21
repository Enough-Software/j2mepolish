/*
 * Created on Feb 4, 2009 at 2:34:57 PM.
 * 
 * Copyright (c) 2009 Robert Virkus / Enough Software
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
package de.enough.polish.libraryprocessor;

import java.io.File;
import java.io.IOException;
import java.util.Locale;


import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;

/**
 * <p>Processes binary libraries before they are compiled.</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class LibraryProcessor extends Extension
{

	/**
	 * Creates a new library processor
	 */
	public LibraryProcessor()
	{
		// initialization is done with environment
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
			throws BuildException
	{
		throw new BuildException("Use library processors directly by calling processLibrary(..)");
	}
	
	/**
	 * Processes a binary library
	 * @param baseDir the base directory
	 * @param relativeClassPaths  an array of paths to class files of that library
	 * @param device the device 
	 * @param locale the current locale
	 * @param env the environment
	 * @throws IOException when processing fails
	 */
	public abstract void processLibrary( File baseDir, String[] relativeClassPaths, Device device, Locale locale, Environment env ) throws IOException;

}
