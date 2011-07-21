/*
 * Created on 03-Nov-2004 at 23:18:59.
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
package de.enough.polish.obfuscate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Obfuscates the MIDlet using the free JODE obfuscator.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        03-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class JodeObfuscator extends Obfuscator {

	private File scriptFile;

	/**
	 * Creates a new instance
	 */
	public JodeObfuscator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile,
			String[] preserve, Path bootClassPath) 
	throws BuildException 
	{
		String scriptFilePath;
		if ( this.scriptFile == null ) {
			// create script file:
			File baseDir = new File( device.getBaseDir() );
			File tempScriptFile = new File( baseDir, "jode-script.jos");
			scriptFilePath = tempScriptFile.getAbsolutePath();
			try {
				createScriptFile( tempScriptFile, sourceFile, targetFile, device, preserve, bootClassPath );
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to obfuscate: " + e.toString() );
			}
		} else {
			scriptFilePath = this.scriptFile.getAbsolutePath();
		}
		// call JODE:
		jode.obfuscator.Main.main( new String[]{ scriptFilePath } );
	}
	
	/**
	 * Creates a new script file.
	 * 
	 * @param tempScriptFile the file which should be created
	 * @param device current device
	 * @param preserve classes which should not be obfuscated
	 * @param bootClassPath the boot class path 
	 * @throws IOException when the file could not be created
	 */
	private void createScriptFile(File tempScriptFile, File sourceFile, File targetFile, Device device, String[] preserve, Path bootClassPath )
	throws IOException
	{
		ArrayList lines = new ArrayList();
		// set the class path:
		StringBuffer buffer = new StringBuffer();
		buffer.append("classpath = ");
		appendPath( bootClassPath.toString(), buffer );
		String[] classPaths = device.getClassPaths();
		for (int i = 0; i < classPaths.length; i++) {
			String path = classPaths[i];
			buffer.append(',');
			appendPath( path, buffer );
		}
		buffer.append(',');
		appendPath( sourceFile.getAbsolutePath(), buffer );
		lines.add( buffer.toString() );
		// set the target file:
		lines.add( "dest = \"" +  StringUtil.replace( targetFile.getAbsolutePath(), "\\", "\\\\" ) + "\"" );
		// create a file which provides the translations for the obfuscated files:
		File obfuscationMapFile = new File( tempScriptFile.getParentFile(), "obfuscation-map.txt");
		lines.add( "revtable = \"" +  StringUtil.replace( obfuscationMapFile.getAbsolutePath(), "\\", "\\\\" ) + "\"" );
		// strip all unnecessary information:
		lines.add( "strip = \"unreach\",\"source\",\"lvt\",\"lnt\",\"inner\"");
		// specify the packages which should be included into the target-jar:
		String[] packageNames = JarUtil.getPackageNames( sourceFile );
		lines.add("load = ");
		for (int i = 0; i < packageNames.length; i++) {
			String packageName = packageNames[i];
			buffer.delete(0, buffer.length() );
			buffer.append("new WildCard { value = \"" )
				.append( packageName )
				.append( "\" }" );
			if (i != packageNames.length -1) {
				buffer.append(",");
			}
			lines.add( buffer.toString() );			
		}
		// mark classes which should remain untouched:
		lines.add( "preserve = " );
		for (int i = 0; i < preserve.length; i++) {
			String className = preserve[i];
			// preserve constructor:
			buffer.delete(0, buffer.length() );
			buffer.append( "new WildCard { value = \"" )
				.append( className )
				.append( ".<init>.*\" }," );
			lines.add( buffer.toString() );
			// preserve startApp:
			buffer.delete(0, buffer.length() );
			buffer.append( "new WildCard { value = \"" )
				.append( className )
				.append( ".startApp.()V\" }," );
			lines.add( buffer.toString() );
			// preserve pauseApp:
			buffer.delete(0, buffer.length() );
			buffer.append( "new WildCard { value = \"" )
				.append( className )
				.append( ".pauseApp.()V\" }," );
			lines.add( buffer.toString() );
			// preserve pauseApp:
			buffer.delete(0, buffer.length() );
			buffer.append( "new WildCard { value = \"" )
				.append( className )
				.append( ".destroyApp.*\" }" );
			if ( i != preserve.length - 1) {
				buffer.append(',');
			}
			lines.add( buffer.toString() );
		}
		// rename all classes as much as possible:
		lines.add( "renamer = new StrongRenamer" );
		// use the constant analyzer for removing dead code:
		lines.add( "analyzer = new ConstantAnalyzer" );
		// optimize the code:
		lines.add( "post = new LocalOptimizer, new RemovePopAnalyzer" );
		FileUtil.writeTextFile( tempScriptFile, (String[]) lines.toArray( new String[ lines.size() ]) );
	}
	
	private void appendPath( String path, StringBuffer buffer ) {
		buffer.append( '"' ); 
		if (File.separatorChar == '\\' ) {
			// we have a windows-environment:
			path = StringUtil.replace( path, "\\", "\\\\" );
		}
		buffer.append( path ).append('"');
	}

	public void setScriptFile( File scriptFile ) {
		this.scriptFile = scriptFile;
	}

}
