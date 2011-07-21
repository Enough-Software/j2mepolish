/*
 * Created on 22-May-2005 at 18:01:05.
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
package de.enough.polish.preprocess.custom;

import java.io.File;
import java.util.ArrayList;

import de.enough.polish.BuildException;

import de.enough.polish.Variable;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.util.StringList;

/**
 * <p>Calls another Ant target in the preprocessing phase.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AntCallPreprocessor extends CustomPreprocessor {
	
	private ArrayList properties;

	/**
	 * Creates a new preprocessor that just calls an Ant target for each processed file. 
	 */
	public AntCallPreprocessor() {
		super();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#processClass(de.enough.polish.util.StringList, java.lang.String)
	 */
	public void processClass(StringList lines, String className) {
		String target = this.extensionSetting.getTarget();
		if ( target == null ) {
			throw new BuildException( "You need to define the \"target\" attribute in the [antcall] preprocessor." );
		}
		if (this.properties == null) {
			this.properties = new ArrayList();
		}
		this.properties.add( new Variable( "polish.classname", className ) );
		executeAntTarget( target, this.properties );
	}

	/**
	 * Sets the parameters for this preprocessor.
	 * 
	 * @param properties the parameters
	 * @param baseDir the base directory
	 */
	public void setParameters( Variable[] properties, File baseDir ) {
		this.properties = new ArrayList();
		for (int i = 0; i < properties.length; i++) {
			Variable variable = properties[i];
			this.properties.add( variable );			
		}
	}

}
