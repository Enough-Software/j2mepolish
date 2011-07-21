/*
 * Created on 04-Apr-2005 at 13:23:12.
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
import java.util.ArrayList;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Variable;

/**
 * <p>Removes all curly braces from any resource-names before copying them.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        04-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AntCallResourceCopier extends ResourceCopier {


	private ArrayList properties;

	/**
	 * Create a new copier
	 */
	public AntCallResourceCopier() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourceCopier#copyResources(de.enough.polish.Device, java.util.Locale, java.io.File[], java.io.File)
	 */
	public void copyResources(Device device, Locale locale, File[] resources,
			File targetDir) 
	throws IOException 
	{
		String target = this.extensionSetting.getTarget();
		if ( target == null ) {
			throw new BuildException( "You need to define the \"target\" attribute in the [antcall] preprocessor." );
		}
		if (this.properties == null) {
			this.properties = new ArrayList();
		}
		this.properties.add( new Variable( "polish.resources.target", targetDir.getAbsolutePath() ) );
		StringBuffer filesBuffer = new StringBuffer();
		for (int i = 0; i < resources.length; i++) {
			File resource = resources[i];
			filesBuffer.append( resource.getAbsolutePath() );
			if ( i != resources.length - 1 ) {
				filesBuffer.append( ',' );
			}
		}
		String filesStr = filesBuffer.toString();
		this.properties.add( new Variable( "polish.resources.files", filesStr ) );
		filesStr = filesStr.replace(',', File.pathSeparatorChar );
		this.properties.add( new Variable( "polish.resources.filePaths", filesStr ) );
		executeAntTarget( target, this.properties );
	}
	
	public void setParameters( Variable[] properties, File baseDir ) {
		this.properties = new ArrayList();
		for (int i = 0; i < properties.length; i++) {
			Variable variable = properties[i];
			this.properties.add( variable );			
		}
	}

}
