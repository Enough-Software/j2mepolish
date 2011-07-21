/*
 * Created on 07-Jun-2005 at 01:01:47.
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
package de.enough.polish.postcompile.i18n;

import java.io.File;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.resources.TranslationManager;
import de.enough.polish.util.FileUtil;

/**
 * <p>Embedds the translations directly into the compiled classes.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        07-Jun-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslationPostCompiler extends PostCompiler {

	/**
	 *  Creates a new post compiler
	 */
	public TranslationPostCompiler() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
	 */
	public void postCompile(File classesDir, Device device)
	throws BuildException 
	{
		TranslationManager translationManager = (TranslationManager) this.environment.get( TranslationManager.ENVIRONMENT_KEY );
		String[] fileNames = FileUtil.filterDirectory( classesDir, ".class", true );
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			
		}
	}

}
