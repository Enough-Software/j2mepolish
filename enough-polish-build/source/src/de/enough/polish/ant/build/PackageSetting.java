/*
 * Created on 02-Nov-2004 at 15:05:47.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.ant.build;

import de.enough.polish.ExtensionSetting;

/**
 * <p>Allows the usage of different packagers for the project.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PackageSetting extends ExtensionSetting {
	
	private String executable;
	private String arguments;

	/**
	 * Creates a new package setting
	 */
	public PackageSetting() {
		super();
	}
	

	/**
	 * @return Returns the executable.
	 */
	public String getExecutable() {
		return this.executable;
	}
	
	/**
	 * @param executable The executable to set.
	 */
	public void setExecutable(String executable) {
		if (this.name == null) {
			this.name = "external";
		}
		this.executable = executable;
	}
	

	/**
	 * @return Returns the arguments.
	 */
	public String getArguments() {
		return this.arguments;
	}
	
	/**
	 * @param arguments The arguments to set.
	 */
	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

}
