/*
 * Created on 25-Oct-2005 at 19:03:51.
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
package de.enough.polish.descriptor;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Attribute;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;

/**
 * <p>Creates the descriptor file such as the JAD (MIDP) or JAM (DoJa).</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        25-Oct-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class DescriptorCreator extends Extension {

	public static final String DESCRIPTOR_ENCODING_KEY = "key.descriptor.encoding";
	public static final String DESCRIPTOR_ATTRIBUTES_KEY = "key.descriptor.attributes";

	/**
	 * Creates a new instance
	 */
	public DescriptorCreator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
	throws BuildException 
	{
		this.environment = env;
		File descriptorFile = new File( env.getVariable("polish.jadPath") );
		Attribute[] descriptorAttributes = (Attribute[]) env.get(DESCRIPTOR_ATTRIBUTES_KEY);
		if (descriptorAttributes == null) {
			throw new BuildException("No descriptor attributes stored.");
		}
		String encoding = (String) env.get( DESCRIPTOR_ENCODING_KEY );
		if (encoding == null) {
			throw new BuildException("No descriptor encoding stored.");
		}
		try {
			createDescriptor( descriptorFile, descriptorAttributes, encoding, device, locale, env );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to create descriptor for device [" + device.getIdentifier() + "] in [" + descriptorFile.getAbsolutePath() + "]: " + e.toString() );
		}

	}

	/**
	 * Creates a new descriptor file.
	 * 
	 * @param descriptorFile the file that should be created
	 * @param descriptorAttributes the attributes
	 * @param encoding the encoding, usually UTF-8
	 * @param device the target device
	 * @param locale the target locale
	 * @param env the evironment
	 * @throws IOException when the descriptor could not be saved
	 */
	public abstract void createDescriptor(File descriptorFile, Attribute[] descriptorAttributes, String encoding, Device device, Locale locale, Environment env)
	throws IOException
	;

}
