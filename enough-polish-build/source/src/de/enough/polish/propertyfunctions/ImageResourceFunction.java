/*
 * Created on Mar 29, 2006 at 9:41:40 PM.
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.enough.polish.Environment;

/**
 * <p>Evaluates images that are embedded for the current device.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 29, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class ImageResourceFunction extends ResourceFuntion {

	/**
	 * Creates a new resource function.
	 * 
	 * @param failOnMissingResource true when this property function requires that the named resource really exists
	 */
	public ImageResourceFunction(boolean failOnMissingResource) {
		super(failOnMissingResource);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.ResourcePropertyFuntion#process(java.io.File, java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(File resource, String resourceName,
			String[] arguments, Environment env) 
	throws IOException 
	{
		BufferedImage image = null;
		if ( resource != null ) {
			image = ImageIO.read(resource);
		}
		return process( image, resource, resourceName, arguments, env );
	}

	/**
	 * Evaluates the given image.
	 * 
	 * @param image the image 
	 * @param resource the resource in question
	 * @param resourceName the name of the resource
	 * @param arguments any arguments
	 * @param env the environment
	 * @return the result from this operation
	 * @throws IOException when the processing fails
	 */
	public abstract String process(BufferedImage image, File resource, String resourceName, String[] arguments, Environment env )
	throws IOException; 

}
