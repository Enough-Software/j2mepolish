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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Attribute;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.util.FileUtil;

/**
 * <p>Creates the descriptor JAM file for DoJa devices.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        25-Oct-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DoJaDescriptorCreator extends DescriptorCreator  {

	/**
	 * Creates a new instance
	 */
	public DoJaDescriptorCreator() {
		super();
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
	public void createDescriptor(File descriptorFile, Attribute[] descriptorAttributes, String encoding, Device device, Locale locale, Environment env)
	throws IOException
	{
//		for (int i = 0; i < descriptorAttributes.length; i++) {
//			Attribute attribute = descriptorAttributes[i];
//			env.addVariable( attribute.getName(), attribute.getValue() );
//		}
		String jamName = descriptorFile.getName();
		jamName = jamName.substring( 0, jamName.length() - 1 ) + "m";
		File jamFile = new File( descriptorFile.getParent(), jamName );
		
		env.setVariable( "polish.jamPath", jamFile.getAbsolutePath() );
		
		ArrayList jamEntries = new ArrayList();
		String value = env.resolveVariable("MIDlet-Name");
		jamEntries.add( "AppName = " + value );
		value = env.resolveVariable("MIDlet-Version");
		jamEntries.add( "AppVer = " + value );
		value = env.resolveVariable("MIDlet-Jar-URL");
		jamEntries.add( "PackageURL = " + value );
		value = env.resolveVariable("MIDlet-Jar-Size");
		jamEntries.add( "AppSize = " + value );
		if (env.hasSymbol("polish.usePolishGui")) {
			// the J2ME Polish GUI is used:
			value = env.resolveVariable("polish.classes.midlet-1");			
		} else {
			// traditional DoJa application is being used:
			value = env.resolveVariable("polish.classes.iapplication");
		}
		jamEntries.add( "AppClass = " + value );
		jamEntries.add( "LastModified = " + getCurrentDate() );
		addOptionalAttribute("ConfigurationVer", jamEntries, env );
		addOptionalAttribute("ProfileVer", jamEntries, env );
		addOptionalAttribute("SPsize", jamEntries, env );
		addOptionalAttribute("UseNetwork", jamEntries, env );
		addOptionalAttribute("TargetDevice", jamEntries, env );
		addOptionalAttribute("LaunchAt", jamEntries, env );
		addOptionalAttribute("AppTrace", jamEntries, env );
		addOptionalAttribute("GetUtn", jamEntries, env );
		
		try {
			System.out.println("creating JAM file [" + jamFile.getAbsolutePath() + "].");
			String[] lines = (String[]) jamEntries.toArray(  new String[ jamEntries.size() ] );
			FileUtil.writeTextFile(jamFile, lines, encoding );
		} catch (IOException e) {
			throw new BuildException("Unable to create JAM file [" + jamFile.getAbsolutePath() +"] for device [" + device.getIdentifier() + "]: " + e.getMessage() );
		}
		
	}

	private void addOptionalAttribute(String name, ArrayList jamEntries, Environment env ) {
		String value = env.resolveVariable("doja." + name);
		if (value != null) {
			jamEntries.add( name + " = " + value );
		}		
	}

	private String getCurrentDate() {
		SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
		return format.format( new Date() );
	}

}
