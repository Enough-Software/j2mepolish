/*
 * Created on 04-Apr-2005 at 13:18:04.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.resources;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import de.enough.polish.Device;
import de.enough.polish.util.FileUtil;

/**
 * <p>Just copies all resource files without renaming them.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        04-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DefaultResourceCopier extends ResourceCopier {

	/**
	 * Creates a new instance
	 */
	public DefaultResourceCopier() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourceCopier#copyResources(de.enough.polish.Device, java.util.Locale, java.io.File[], java.io.File)
	 */
	public void copyResources(Device device, Locale locale, File[] resources,
			File targetDir) 
	throws IOException 
	{
		FileUtil.copy(resources, targetDir);
	}

}
