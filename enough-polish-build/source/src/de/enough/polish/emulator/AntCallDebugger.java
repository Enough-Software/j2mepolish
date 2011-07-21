/*
 * Created on 22-May-2005 at 17:17:59.
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
package de.enough.polish.emulator;

import java.io.File;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Variable;

/**
 * <p>Just calls an Ant target. Specify additional properties with nested &lt;parameter&gt; elements.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AntCallDebugger extends Debugger {
	
	private Variable[] properties;

	/**
	 * Creates a new call ant target finalizer
	 */
	public AntCallDebugger() {
		super();
	}

	public void setParameters( Variable[] properties, File baseDir ) {
		this.properties = properties;
	}

	public void connectDebugger(int port, Device device, Locale locale, Environment env) {
		String target = this.extensionSetting.getTarget();
		if ( target == null ) {
			throw new BuildException( "You need to define the \"target\" attribute in the [antcall] debugger." );
		}
		Variable[] props;
		if (this.properties == null) {
			props = new Variable[ 1 ];
		} else {
			props = new Variable[ this.properties.length + 1 ];
			System.arraycopy( this.properties, 0, props, 1, this.properties.length );
		}
		props[0] = new Variable( "polish.debug.port", "" + port );
		//System.out.println("AntCallDebugger: calling target [" + target + "] - antProject == null: " + (this.antProject == null));
		executeAntTarget( target, props );
	}

}
