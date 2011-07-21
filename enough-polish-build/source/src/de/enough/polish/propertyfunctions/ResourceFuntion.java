/*
 * Created on Mar 29, 2006 at 9:27:45 PM.
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
package de.enough.polish.propertyfunctions;

import java.io.File;
import java.io.IOException;

import de.enough.polish.BuildException;

import de.enough.polish.Environment;

/**
 * <p>Provides a base class for functions that have to handle with resources like images.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 29, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see ImageWidthFunction
 */
public abstract class ResourceFuntion extends PropertyFunction {

	private final boolean failOnMissingResource;

	/**
	 * Creates a new resource function.
	 * 
	 * @param failOnMissingResource true when this property function requires that the named resource really exists
	 */
	public ResourceFuntion( boolean failOnMissingResource ) {
		super();
		this.failOnMissingResource = failOnMissingResource;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments, Environment env) {
		File resourceDir = env.getDevice().getResourceDir();
		File resource = new File( resourceDir, input );
		if (this.failOnMissingResource && !resource.exists()) {
			throw new BuildException("Unable to use property function " + getClass().getName() + " with unknown resource \"" + input + "\": resource not found." );
		}
		try {
			return process(resource, input, arguments, env);
		} catch (IOException e) {
			throw new BuildException("Unable to handle property function " + getClass().getName() + " for resource \"" + input + "\": " + e.toString() );
		}
	}
	
	/**
	 * Evaluates the given resource.
	 * 
	 * @param resource the resource in question, can be not null even though it does not exist!
	 * @param resourceName the name of the resource
	 * @param arguments any arguments
	 * @param env the environment
	 * @return the result from this operation
	 * @throws IOException when the processing fails
	 */
	public abstract String process( File resource, String resourceName, String[] arguments, Environment env )
	throws IOException;

}
