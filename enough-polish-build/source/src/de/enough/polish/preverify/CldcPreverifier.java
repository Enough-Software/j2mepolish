/*
 * Created on 30-Jul-2005 at 20:44:23.
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
import java.util.Iterator;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;

import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Uses the normal WTK based preverifier for verifying the compiled classes.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        30-Jul-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CldcPreverifier extends Preverifier {

	/**
	 * Creates a new un-initialized preverifier
	 */
	public CldcPreverifier() {
		super();
	}

	public void preverify(Device device, File sourceDir, File targetDir, Path bootClassPath, Path classPath) 
	throws IOException 
	{
		ArrayList arguments = new ArrayList();
		if ( this.preverifyExecutable != null && this.preverifyExecutable.exists() ) {
			arguments.add( this.preverifyExecutable.getAbsolutePath() );
		} else {
			// hope that the "preverify" executable is on the path:
			if ( File.separatorChar == '\\') {
				System.out.println("Warning: did not find 'preverify.exe', assuming it is found on the %PATH% environment...");
				// this is a windows environment:
				arguments.add( "preverify.exe" );
			} else {
				System.out.println("Warning: did not find 'preverify' executable, assuming it is found on the %PATH% environment...");
				// this is a unix environment:
				arguments.add( "preverify" );
			}
			if (Environment.getInstance().getVariable("wtk.home") == null) {
				System.out.println("Please define the ${wtk.home} ANT property.");
			} else if ( !(new File(Environment.getInstance().getVariable("wtk.home")).exists())) {
				System.out.println("Please redefine the ${wtk.home} ANT property, the current setting '" + Environment.getInstance().getVariable("wtk.home") + "' points to a non-existing directory.");
			}
		}
		String classPathStr = bootClassPath.toString();
		if ( classPath != null ) {
			classPathStr += File.pathSeparatorChar + classPath.toString();
		}
		arguments.add( "-classpath" );
		arguments.add( classPathStr );
		arguments.add( "-d" );
		arguments.add( targetDir.getAbsolutePath() );

		// set restrictions for the preverification:
		if ( device.isCldc10() ) {
			arguments.add( "-nofp" );
		}
		arguments.add( "-nofinalize" );
		arguments.add( "-nonative" );
		
		// tell the preverify what classes should be preverified:
		arguments.add( sourceDir.getAbsolutePath() );
		
		// launch the preverifier:
		int result = ProcessUtil.exec( arguments, "preverify:", true  );
		if ( result != 0 ) {
			System.out.println("Preverify call failed: " + result );
			for (Iterator iter = arguments.iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				System.out.print( element + " " );
			}
			System.out.println();
			throw new BuildException("Unable to preverify for device [" + device.getIdentifier() + "] - preverify returned result " + result );
		}
	}

	

}
