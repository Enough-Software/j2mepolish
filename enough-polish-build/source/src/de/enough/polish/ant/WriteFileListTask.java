/*
 * Created on 15-Apr-2004 at 16:01:30.
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

import de.enough.polish.util.FileUtil;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>Writes the list of included files to the specified text-file.</p>
 * <p>This tasks accepts an arbitrary number of nested file names.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        15-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class WriteFileListTask extends Task {
	
	private File target;
	private final ArrayList fileSets;
	
	/**
	 * Creates a new empty task
	 */
	public WriteFileListTask() {
		super();
		this.target = new File("index.txt");
		this.fileSets = new ArrayList();
	}
	
	/**
	 * Sets the target to which the index-list should be written.
	 * Default value is "index.txt".
	 * 
	 * @param target the target
	 */
	public void setTarget( File target ) {
		this.target = target;
	}
	
	public void addConfiguredFileset( FileSet set ) {
		this.fileSets.add(set );
	}
	
	public void execute(){
		if (this.fileSets.size() == 0) {
			throw new BuildException("The WriteFileListTask needs at least one nested <fileset> element.");
		}
		ArrayList fileNamesList = new ArrayList();
		FileSet[] sets = (FileSet[]) this.fileSets.toArray( new FileSet[ this.fileSets.size()] );
		for (int i = 0; i < sets.length; i++) {
			FileSet set = sets[i];
			DirectoryScanner scanner = set.getDirectoryScanner(getProject());
			String[] fileNames = scanner.getIncludedFiles();
			for (int j = 0; j < fileNames.length; j++) {
				String name = fileNames[j];
				name = name.replace('\\', '/');
				fileNamesList.add( name );
			}
		}
		String[] fileNames = (String[]) fileNamesList.toArray( new String[ fileNamesList.size()] );
		try {
			FileUtil.writeTextFile( this.target, fileNames );
		} catch (IOException e) {
			throw new BuildException("Unable to write file index [" 
					+ this.target.getAbsolutePath() + "]: " + e.getMessage(), e );
		}
	}
	
}
