/*
 * Created on Jun 2, 2004
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
package de.enough.polish.ant;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.tools.ant.Task;

import de.enough.polish.BuildException;
import de.enough.polish.Variable;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.PropertyUtil;
import de.enough.polish.util.StringUtil;


/**
 * Includes properties into a text-file.
 * 
 * @author robert virkus, robert@enough.de
 */
public class PropertyWriterTask extends Task {
	
	private File sourceFile;
	private File destinationFile;
	private HashMap properties = new HashMap();
	private HashMap definedProperties = new HashMap();
	private String ignore;
	
	/**
	 * Creates an empty property writer
	 */
	public PropertyWriterTask() {
		super();
	}
	
	public void addConfiguredVariable( Variable var ) {
		if (var.getName() == null) {
			throw new BuildException("Each <property> element needs to have a name attribute.");
		}
		if (var.getValue() == null) {
			throw new BuildException("Each <property> element needs to have a value attribute.");
		}
		this.definedProperties.put( var.getName().toLowerCase(), var.getValue() );
	}
	
	public void setSrcfile( File srcfile ) {
		this.sourceFile = srcfile;
	}
	
	public void setDestfile( File destfile ) {
		this.destinationFile = destfile;
	}
	
	public void setIgnore( String ignoreList ) {
		this.ignore = ignoreList;
	}
	
	private  void checkSettings() {
		if (this.sourceFile == null) {
			throw new BuildException("The PropertyWriter task needs the srcfile attribute.");
		} else if ( !this.sourceFile.exists()) {
			throw new BuildException("The srcfile attribute points to a non-existing file (" + this.sourceFile.getAbsolutePath() + ").");
		}
		if (this.sourceFile == null) {
			throw new BuildException("The PropertyWriter task needs the destfile attribute.");
		}
	}
	
	
	private void initSettings() {
		// copy the Ant-properties:
		Hashtable antProperties = getProject().getProperties();
		this.properties.putAll( antProperties );	
		// now copy all specifically defined properties:
		// this is done here so that these properties overwrite
		// any Ant-properties:
		this.properties.putAll( this.definedProperties );
		if (this.ignore != null) {
			String[] irgnores = StringUtil.splitAndTrim(this.ignore, ',');
			for (int i = 0; i < irgnores.length; i++) {
				String property = irgnores[i];
				this.properties.remove(property);
			}
		}
	}
	
	public void execute() {
		System.out.println("polish.home" + getProject().getProperty("polish.home"));
		checkSettings();
		initSettings();
		// read source file:
		String[] lines = null;
		try {
			lines = FileUtil.readTextFile( this.sourceFile );
		} catch (IOException e) {
			throw new BuildException("Unable to read the srcfile (" + this.sourceFile.getAbsolutePath() + "): " + e.toString(), e );
		}
		// replace all properties:
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			lines[i] = PropertyUtil.writeProperties( line, this.properties, this.ignore == null );
		}
		try {
			// write destination file:
			FileUtil.writeTextFile( this.destinationFile, lines );
		} catch (IOException e) {
			throw new BuildException("Unable to write the destfile (" + this.destinationFile.getAbsolutePath() + "): " + e.toString(), e );
		}
	}

}
