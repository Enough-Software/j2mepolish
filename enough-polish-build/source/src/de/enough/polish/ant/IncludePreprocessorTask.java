/*
 * Created on 04-Mar-2005 at 10:46:53.
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
package de.enough.polish.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import de.enough.polish.Variable;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.PropertyUtil;
import de.enough.polish.util.StringList;

/**
 * <p>Processes "include" statements in any source files.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        04-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class IncludePreprocessorTask extends Task {
	
	private final Map variables;
	private final ArrayList fileSets;
	private File dir;
	
	/**
	 * Creates a new inclusion task
	 */
	public IncludePreprocessorTask() {
		super();
		this.variables = new HashMap();
		this.fileSets = new ArrayList();
	}
	
	public void addConfiguredFileset( FileSet set ) {
		this.fileSets.add(set );
	}

	public void addConfiguredVariable( Variable variable ) {
		this.variables.put( variable.getName(), variable.getValue() );
	}
	
	public void setDir( File dir ) {
		this.dir = dir;
	}

	public void execute() throws BuildException {
		if (this.fileSets.size() == 0) {
			throw new BuildException("The IncludePreprocessorTask needs at least one nested <fileset> element.");
		}
		if (this.dir == null) {
			this.dir = getProject().getBaseDir();
		}
		this.variables.putAll( getProject().getProperties() );
		FileSet[] sets = (FileSet[]) this.fileSets.toArray( new FileSet[ this.fileSets.size()] );
		for (int i = 0; i < sets.length; i++) {
			FileSet set = sets[i];
			DirectoryScanner scanner = set.getDirectoryScanner(getProject());
			String[] fileNames = scanner.getIncludedFiles();
			for (int j = 0; j < fileNames.length; j++) {
				String name = fileNames[j];
				try {
					preprocess( name );
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to process file [" + name + "]: " + e.toString() );
				}
			}
		}	
	}

	/**
	 * Preprocesses the given file.
	 * 
	 * @param fileName the file
	 */
	private void preprocess(String fileName)
	throws IOException, BuildException
	{
		
		File file = new File( this.dir, fileName );
		String[] lines = FileUtil.readTextFile( file );
		StringList sourceCode = new StringList( lines );
		boolean inserted = false;
		String lastLine = null;
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent().trim();
			if (line.startsWith( "//#include") ) {
				String includeStatement = line.substring( "//#include".length() ).trim();
				try {
					includeStatement = PropertyUtil.writeProperties( includeStatement, this.variables, true );
					File includeFile = new File( includeStatement );
					if (includeFile.exists()) {
						String[] includeLines = FileUtil.readTextFile(includeFile);
						
						if (lastLine != null && lastLine.startsWith("//#if")) {
							sourceCode.next();
						}
						sourceCode.insert(includeLines);
						System.out.println("Inserting [" + includeFile.getName() + "] into [" + fileName + "]." );
						inserted = true;
					} else {
						System.err.println("Warning: unable to include [" + includeStatement + "] - the file does not exist." );
					}
				
				} catch (IllegalArgumentException e) {
					System.err.println("Warning: unable to include [" + includeStatement + "] - the needed property is not defined: " + e.toString() );
				}
			}
			lastLine = line;
		}
		if (inserted) {
			System.out.println("Saving [" + fileName + "].");
			FileUtil.writeTextFile(file, sourceCode.getArray() );
		}
	}
}
