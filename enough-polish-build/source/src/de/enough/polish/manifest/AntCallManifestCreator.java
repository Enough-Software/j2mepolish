/*
 * Created on 25-Oct-2005 at 00:18:24.
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
import java.util.ArrayList;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Attribute;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Variable;
import de.enough.polish.ant.build.Manifest;

/**
 * <p>Does create a manifest file from within an Ant target.</p>
 * <p>Following Ant properties can be used (next to all J2ME Polish properties):</p>
 * <ul>
 * 	<li><b>polish.manifest.target</b>: absolute path to the MANIFEST.MF file that should be created</li>
 * 	<li><b>polish.manifest.encoding</b>: the encoding that should be used, usually UTF-8</li>
 * 	<li><b>polish.manifest.attributes</b>: All attributes seperated by a CR.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        25-Oct-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AntCallManifestCreator extends ManifestCreator {

	/**
	 * Creates a new instance
	 */
	public AntCallManifestCreator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.manifest.ManifestCreator#createManifest(java.io.File, de.enough.polish.Attribute[], java.lang.String, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void createManifest(File manifestFile,
			Attribute[] manifestAttributes, String encoding, Device device,
			Locale locale, Environment env) 
	throws IOException 
	{
		String target = this.extensionSetting.getTarget();
		if ( target == null ) {
			throw new BuildException( "You need to define the \"target\" attribute in the [antcall] preprocessor." );
		}
		ArrayList properties = new ArrayList();
		properties.add( new Variable( "polish.manifest.target", manifestFile.getAbsolutePath() ) );
		properties.add( new Variable( "polish.manifest.encoding", encoding ) );
		StringBuffer attributesBuffer = new StringBuffer();
		for (int i = 0; i < manifestAttributes.length; i++) {
			Attribute attribute = manifestAttributes[i];
			properties.add( attribute );
			attributesBuffer.append( attribute.getName()).append(": ").append( attribute.getValue() ).append( Manifest.EOL ); 			
		}
		properties.add( new Variable( "polish.manifest.attributes", attributesBuffer.toString() ) );
		
		executeAntTarget( target, properties );
	}

}
