/*
 * Created on 22-May-2005 at 20:41:12.
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
package de.enough.polish.postobfuscate;

import java.io.File;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Variable;

/**
 * <p>Call an Ant target in the postobfuscator build phase.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AntCallPostObfuscator extends PostObfuscator {

	private Variable[] properties;

	/**
	 * Creates a new post obfuscator
	 */
	public AntCallPostObfuscator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.postobfuscate.PostObfuscator#postObfuscate(java.io.File, de.enough.polish.Device)
	 */
	public void postObfuscate(File classesDir, Device device)
			throws BuildException 
	{
		String target = this.extensionSetting.getTarget();
		if ( target == null ) {
			throw new BuildException( "You need to define the \"target\" attribute in the [antcall] postcompiler." );
		}
		Variable[] props = null;
		if (this.properties != null) {
			props = new Variable[ this.properties.length ];
			System.arraycopy( this.properties, 0, props, 0, this.properties.length );
		}
		executeAntTarget( target, props );
	}
	
	public void setParameters( Variable[] properties, File baseDir ) {
		this.properties = properties;
	}


}
