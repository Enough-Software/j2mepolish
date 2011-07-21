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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Attribute;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.Jad;
import de.enough.polish.util.FileUtil;

/**
 * <p>Creates the descriptor JAD file).</p>
 *
 * <p>Copyright Enough Software 2005 - 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MultiLineDescriptorCreator extends DescriptorCreator  {

    /** The max number of bytes of one line in a JAD */
    public static final int MAX_LINE_LENGTH = 72;
    /** The max number of bytes without the line break */
	private static final int MAX_SECTION_LENGTH = 70;
    /** The End-Of-Line marker in manifests */
    public static final String EOL = "\r\n";


	/**
	 * Creates a new instance
	 */
	public MultiLineDescriptorCreator() {
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
		Jad jad = new Jad( env );
		jad.setAttributes( descriptorAttributes );
		
		try {
			System.out.println("creating JAD file [" + descriptorFile.getAbsolutePath() + "].");
			ArrayList list = new ArrayList();
			String[] lines = jad.getContent();
			for (int i = 0; i < lines.length; i++)
			{
				String line = lines[i];
				addAttribute( line, list, encoding );
			}
			lines = (String[]) list.toArray( new String[ list.size() ]);
			FileUtil.writeTextFile(descriptorFile, lines, encoding );
		} catch (IOException e) {
			throw new BuildException("Unable to create JAD file [" + descriptorFile.getAbsolutePath() +"] for device [" + device.getIdentifier() + "]: " + e.getMessage() );
		}
		
	}
	

    /**
     * Adds an attribute to the list of string
     * @throws IOException 
     * @throws UnsupportedEncodingException 
     */
    private void addAttribute(String attribute, List lines, String encoding) 
    throws UnsupportedEncodingException, IOException 
    {
    	String original = attribute;
        while (attribute.getBytes(encoding).length > MAX_SECTION_LENGTH) {
            // try to find a MAX_LINE_LENGTH byte section
            int breakIndex = MAX_SECTION_LENGTH;
            if (breakIndex >= attribute.length()) {
                breakIndex = attribute.length() - 1;
            }
            String section = attribute.substring(0, breakIndex);
            while (section.getBytes(encoding).length > MAX_SECTION_LENGTH && breakIndex > 0) {
                breakIndex--;
                section = attribute.substring(0, breakIndex);
            }
            if (breakIndex == 0) {
                throw new IOException("Unable to process JAD attribute " + original + ": unable to break at offset 0 in section " + attribute);
            }
            lines.add(section + EOL);
            attribute = " " + attribute.substring(breakIndex);
        }
        lines.add(attribute + EOL);
    }


}
