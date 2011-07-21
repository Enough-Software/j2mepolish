/*
 * Created on 24-Oct-2005 at 23:55:09.
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
package de.enough.polish.manifest;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Attribute;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;

/**
 * <p>Is responsible for creating the Manifest file.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        24-Oct-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class ManifestCreator extends Extension {
	
	public static final String MANIFEST_ENCODING_KEY = "key.manifest.encoding";
	public static final String MANIFEST_ATTRIBUTES_KEY = "key.manifest.attributes";

	/**
	 * Creates a new instance
	 */
	public ManifestCreator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
	throws BuildException 
	{
		File manifestFile = new File( device.getClassesDir() + File.separatorChar + "META-INF" + File.separatorChar + "MANIFEST.MF" );
		Attribute[] manifestAttributes = (Attribute[]) env.get(MANIFEST_ATTRIBUTES_KEY);
		if (manifestAttributes == null) {
			throw new BuildException("No manifest attributes stored.");
		}
		for (int i = 0; i < manifestAttributes.length; i++) {
			Attribute attribute = manifestAttributes[i];
			attribute.setValue( this.environment.writeProperties( attribute.getValue() ));
		}

		String encoding = (String) env.get( MANIFEST_ENCODING_KEY );
		if (encoding == null) {
			throw new BuildException("No manifest encoding stored.");
		}
		try {
			createManifest( manifestFile, manifestAttributes, encoding, device, locale, env );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to create manifest for device [" + device.getIdentifier() + "] in [" + manifestFile.getAbsolutePath() + "]: " + e.toString() );
		}
	}

	/**
	 * Creates the manifest file for the given target device.
	 * 
	 * @param manifestFile the manifest file that should be created
	 * @param manifestAttributes  the attributes that should be stored, the attributes are already sorted and filtered
	 * @param encoding the encoding of the manifest, usually UTF-8
	 * @param device the target device
	 * @param locale the current locale
	 * @param env the environment
	 * @throws IOException when the MANIFEST.MF file could not be written.
	 */
	public abstract void createManifest(File manifestFile, Attribute[] manifestAttributes, String encoding, Device device, Locale locale, Environment env)
	throws IOException;

}
