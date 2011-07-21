/*
 * Created on Aug 8, 2006 at 12:47:16 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
import java.util.Locale;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.preprocess.CustomPreprocessor;

/**
 * <p>You can extend this preprocessor for adding sources or processing resources before the actual preprocessing is started.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Aug 8, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class InitializerPreprocessor extends CustomPreprocessor {

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#notifyDevice(de.enough.polish.Device, boolean)
	 */
	public void notifyDevice(Device device, boolean usesPolishGui) {
		super.notifyDevice(device, usesPolishGui);
		Environment env = device.getEnvironment();
		initializePreprocessing(
				new File( device.getSourceDir() ),
				device.getResourceDir(),
				env.getLocale(),
				device,
				env );
	}
	
	/**
	 * Initializes the preprocessing.
	 * 
	 * @param sourceDir the directory to which the preprocessed sources will be written
	 * @param resourcesDir the directory containing all resources
	 * @param locale the current locale
	 * @param device the current device
	 * @param env environment settings
	 */
	public abstract void initializePreprocessing( File sourceDir, File resourcesDir, Locale locale, Device device, Environment env );
	

}
