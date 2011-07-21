/*
 * Created on Feb 5, 2007 at 3:06:26 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.j2se;

import de.enough.polish.Attribute;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.build.Manifest;
import de.enough.polish.manifest.ManifestCreator;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class JavaSEManifestCreator
extends ManifestCreator
{
	public void createManifest(File manifestFile, Attribute[] manifestAttributes, String encoding, Device device, Locale locale, Environment env)
	throws IOException
	{
		Manifest manifest = new Manifest( env, encoding );
		manifest.setAttributes( manifestAttributes );
		manifest.addAttribute(new Attribute("Main-Class", "de.enough.polish.emulator.MicroEmulatorStarter"));
		manifest.write(manifestFile);
		manifest.write( new File( manifestFile.getParentFile(), "J2MEPOLISHMANIFEST.MF") );
	}
}
