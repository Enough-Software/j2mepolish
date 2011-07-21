/*
 * Created on Mar 29, 2006 at 9:53:43 PM.
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

import de.enough.polish.Environment;

/**
 * <p>Measures the width of the given image. Usage: <code>imagewidth( name )</code></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 29, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ImageWidthFunction extends ImageResourceFunction {

	/**
	 * Creates a new width function.
	 */
	public ImageWidthFunction() {
		super(true);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.ImageResourcePropertyFunction#process(java.awt.image.BufferedImage, java.io.File, java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(BufferedImage image, File resource,
			String resourceName, String[] arguments, Environment env)
			throws IOException 
	{
		return "" + image.getWidth();
	}

}
