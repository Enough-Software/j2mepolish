/*
 * Created on 07-Dec-2004 at 15:49:21.
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

import java.util.ArrayList;

import de.enough.polish.BuildException;

import de.enough.polish.ant.Setting;

/**
 * <p>Allows a fine-tuned setting of source directories.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        07-Dec-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SourcesSetting extends Setting {
	
	private final ArrayList sourceSettings;

	/**
	 * Creates a new sources setting.
	 */
	public SourcesSetting() {
		super();
		this.sourceSettings = new ArrayList();
	}
	
	public void addConfiguredSource( SourceSetting setting ) {
		if (setting.getDir() == null) {
			throw new BuildException("Invalid <source>-setting: Each <source>-element needs to define the \"dir\"-attribute. Please correct your \"build.xml\" file.");
		}
		this.sourceSettings.add( setting );
	}
	
	public SourceSetting[] getSources() {
		return (SourceSetting[]) this.sourceSettings.toArray( new SourceSetting[this.sourceSettings.size()]);
	}

}
