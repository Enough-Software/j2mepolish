/*
 * Created on 02-Nov-2004 at 15:33:16.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.jar;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.util.JarUtil;

/**
 * <p>Uses the internal J2ME Polish functions to create a jar-file.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DefaultPackager extends Packager {

	/**
	 * Creates a new packager
	 */
	public DefaultPackager() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.jar.Packager#doPackage(java.io.File, java.io.File, de.enough.polish.Device, de.enough.polish.preprocess.BooleanEvaluator, java.util.Map, org.apache.tools.ant.Project)
	 */
	public void createPackage(File sourceDir, File targetFile, Device device, Locale locale, Environment env)
	throws IOException, BuildException 
	{
		JarUtil.jar(sourceDir, targetFile, true );
	}

	public String toString() {
		return "Default Jar Packager";
	}
}
