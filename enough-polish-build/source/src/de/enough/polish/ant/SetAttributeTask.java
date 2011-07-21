/*
 * Created on Jan 10, 2007 at 7:04:31 PM.
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
package de.enough.polish.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;


/**
 * <p>Sets correct attribute values resolved from fuzzy filenames like foo*.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 10, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SetAttributeTask extends ConditionalTask {
	
	private String property;
	private String fileName;
	private File dir;
	
	public void setDir( File dir ) {
		this.dir = dir;
	}

	/**
	 * @param name the fuzzy name of the file that should be resolved
	 */
	public void setFileValue(String name) {
		this.fileName = name;
	}
	/**
	 * @return the property
	 */
	public String getProperty() {
		return this.property;
	}
	/**
	 * @param property the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		if (this.fileName == null || this.dir == null || this.property == null ) {
			System.err.println("SetAttributeTask: specify \"property\", \"fileValue\" and \"dir\" attributes.");
			return;
		}
		if (!this.dir.exists()) {
			System.err.println("SetAttributeTask: \"dir\" attribute points to invalid directory " + this.dir.getAbsolutePath() );			
		}
		String[] names = this.dir.list();
		if (this.fileName.endsWith("*")) {
			String name = this.fileName.substring(0, this.fileName.length() - 1);
			for (int i = 0; i < names.length; i++) {
				String potentialMatch = names[i];
				if (potentialMatch.startsWith( name)) {
					getProject().setProperty(this.property, potentialMatch);
					return;
				}
				
			}
		} else {
			System.err.println("SetAttributeTask: unsupported fileValue=" + this.fileName );
		}
	}
	
	
	

}
