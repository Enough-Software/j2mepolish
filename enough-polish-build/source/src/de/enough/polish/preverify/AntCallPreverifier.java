/*
 * Created on 30-Jul-2005 at 21:44:17.
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
package de.enough.polish.preverify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.enough.polish.BuildException;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.Variable;

public class AntCallPreverifier extends Preverifier {

	public AntCallPreverifier() {
		super();
	}

	public void preverify(Device device, File sourceDir, File targetDir,
			Path bootClassPath, Path classPath) 
	throws IOException 
	{
		String target = this.extensionSetting.getTarget();
		if ( target == null ) {
			throw new BuildException( "You need to define the \"target\" attribute in the [antcall] preprocessor." );
		}
		ArrayList properties = new ArrayList();
		properties.add( new Variable( "polish.preverify.source", sourceDir.getAbsolutePath() ) );
		properties.add( new Variable( "polish.preverify.target", targetDir.getAbsolutePath() ) );
		properties.add( new Variable( "polish.preverify.bootclasspath", bootClassPath.toString() ) );
		if ( classPath != null ) {
			properties.add( new Variable( "polish.preverify.classpath", classPath.toString() ) );
		} else {
			properties.add( new Variable( "polish.preverify.classpath", "" ) );
		}
		
		executeAntTarget( target, properties );
	}

}
