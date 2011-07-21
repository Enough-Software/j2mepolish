/*
 * Created on 04-Apr-2005 at 13:23:12.
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
package de.enough.polish.resources;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.Device;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Removes all curly braces from any resource-names before copying them.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        04-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RenamingResourceCopier extends ResourceCopier {

	private Pattern searchPattern;
	private String replacement = "";

	/**
	 * Create a new copier
	 */
	public RenamingResourceCopier() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ResourceCopier#copyResources(de.enough.polish.Device, java.util.Locale, java.io.File[], java.io.File)
	 */
	public void copyResources(Device device, Locale locale, File[] resources,
			File targetDir) 
	throws IOException 
	{
		for (int i = 0; i < resources.length; i++) {
			File sourceFile = resources[i];
			String fileName = sourceFile.getName();
			if (this.searchPattern != null) {
				Matcher matcher = this.searchPattern.matcher(fileName);
				while (matcher.find()) {
					fileName = StringUtil.replace( fileName, matcher.group(), this.replacement );
				}
			}
			File targetFile = new File( targetDir, fileName );
			FileUtil.copy( sourceFile, targetFile );
		}
	}
	
	public void setSearchPattern( String pattern ) {
		//System.out.println("search-patttern is: " + pattern );
		this.searchPattern = Pattern.compile( pattern );
	}

	public void setReplacement( String replacement ) {
		this.replacement = replacement;
	}

}
