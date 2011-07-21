/*
 * Created on 23-Feb-2004 at 14:24:04.
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

import de.enough.polish.ExtensionSetting;

/**
 * <p>Containts information about the obfuscator which should be used.</p>
 * <p>Can be used for a more detailed setting than just using the 
 * &lt;build&gt;attributes "obfuscator" and "obfuscate".</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ObfuscatorSetting 
extends ExtensionSetting 
{
	
	private ArrayList keeps;
	private boolean enable = true;
	private boolean renameMidlets;

	/**
	 * Creates a new empty obfuscator setting. 
	 */
	public ObfuscatorSetting() {
		this.keeps = new ArrayList();
	}
	
	public void addConfiguredKeep( Keep keep ) {
		if (keep.getClassName() == null) {
			throw new BuildException("The <keep> element needs to define the attribute [class]. Please check your <obfuscator> setting.");
		}
		this.keeps.add( keep );
	}
	
	public void addConfiguredPreserve( Keep keep ) {
		addConfiguredKeep(keep);
	}
	
	public void setEnable( boolean enable ) {
		System.out.println("Deprecation-Warning: The \"enable\" attribute of the <obfuscator> is now deprecated (and \"true\" by default). Please use \"if\" and \"unless\" attributes for controlling the <obfuscator>-setting.");
		this.enable = enable;
	}
	
	public boolean isEnabled() {
		return this.enable;
	}
		
	public boolean hasKeepDefinitions() {
		return this.keeps.size() > 0;
	}
	
	/**
	 * Retrieves the names of classes which should not be obfuscated.
	 * 
	 * @return An array with the names of classes which should not be obfuscated.
	 */
	public String[] getPreserveClassNames() {
		Keep[] keepDefinitions = (Keep[]) this.keeps.toArray( new Keep[ this.keeps.size() ] );
		String[] preserves = new String[ keepDefinitions.length ];
		for (int i = 0; i < keepDefinitions.length; i++) {
			Keep keep = keepDefinitions[i];
			preserves[i] = keep.getClassName();
		}
		return preserves;
	}

	/**
	 * Determines whether the MIDlet classes should be renamed to short "A", "B", "C" etc.
	 * 
	 * @return Returns true when the midlet-classes should be renamed.
	 */
	public boolean renameMidlets() {
		return this.renameMidlets;
	}
	
	/**
	 * Sets whether the midlet-classes should be renamed
	 * 
	 * @param renameMidlets true when the midlet-classes should be renamed.
	 */
	public void setRenameMidlets(boolean renameMidlets) {
		this.renameMidlets = renameMidlets;
	}
	
	
	/**
	 * Sets whether all classes should be moved into the default package ("").
	 * 
	 * @param useDefaultPackage true when all classes should be moved into the default package.
	 */
	public void setUseDefaultPackage(boolean useDefaultPackage) {
		if (useDefaultPackage) {
			System.out.println("Warning: the \"useDefaultPackage\" option of the <obfuscator> element will be ignored. Please change your build.xml script: <obfuscator useDefaultPackage=\"true\".../>." );
		}
	}
}
