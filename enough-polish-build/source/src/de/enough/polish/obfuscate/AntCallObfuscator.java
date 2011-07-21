/*
 * Created on 22-May-2005 at 20:46:34.
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
package de.enough.polish.obfuscate;

import java.io.File;
import java.util.ArrayList;

import de.enough.polish.BuildException;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.Variable;

/**
 * <p>Call another Ant target for obfuscating.</p>
 * <p>Within the target you can use following Ant properties:</p>
 * <ul>
 *  <li><code>polish.obfuscate.source</code>: the source JAR file (which is not obfuscated)</li>
 *  <li><code>polish.obfuscate.target</code>: the target JAR file (which should be generated)</li>
 *  <li><code>polish.obfuscate.bootclasspath</code>: the boot class path</li>
 *  <li><code>polish.obfuscate.classpath</code>: the class path</li>
 *  <li><code>polish.obfuscate.keepcount</code>: the number of classes that should be kept</li>
 *  <li><code>polish.obfuscate.keep</code>: a comma separated list of classnames that should be kept/not obfuscated</li>
 *  <li><code>polish.obfuscate.keep.0..polish.obfuscate.keep.[keepcount-1]</code>: each class name that should be kept in a single Ant property</li>
 * </ul>
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AntCallObfuscator extends Obfuscator {

	private ArrayList properties;

	/**
	 * Creates a new obfuscator.
	 */
	public AntCallObfuscator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile,
			String[] preserve, Path bootClassPath) throws BuildException 
	{
		String target = this.extensionSetting.getTarget();
		if ( target == null ) {
			throw new BuildException( "You need to define the \"target\" attribute in the [antcall] obfuscator." );
		}
		if (this.properties == null) {
			this.properties = new ArrayList();
		}
		this.properties.add( new Variable( "polish.obfuscate.source", sourceFile.getAbsolutePath() ) );
		this.properties.add( new Variable( "polish.obfuscate.target", targetFile.getAbsolutePath() ) );
		this.properties.add( new Variable( "polish.obfuscate.bootclasspath", bootClassPath.toString() ) );
		this.properties.add( new Variable( "polish.obfuscate.classpath", device.getClassPath() ) );
		this.properties.add( new Variable( "polish.obfuscate.keepcount", "" + preserve.length ) );
		StringBuffer preserveBuffer = new StringBuffer();
		for (int i = 0; i < preserve.length; i++) {
			String keep = preserve[i];
			this.properties.add( new Variable( "polish.obfuscate.keep." + i, keep ) );
			preserveBuffer.append( keep );
			if (i != preserve.length - 1 ) {
				preserveBuffer.append( ',' );
			}
		}
		this.properties.add( new Variable( "polish.obfuscate.keep", preserveBuffer.toString() ) );
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
