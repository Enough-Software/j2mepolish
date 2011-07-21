/*
 * Created on 03-Nov-2004 at 17:42:25.
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

import com.sun.kvem.environment.Obfuscator;
import com.zelix.ZKMWtkPlugin;

import de.enough.polish.Device;
import de.enough.polish.util.FileUtil;

/**
 * <p>Allows the obfuscation using the Zelix Klass Master WTK plugin.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        03-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class KlassMasterObfuscator extends WtkPluginObfuscator {
	

	private boolean enableFlowObfuscation;
	private String obfuscateFlowLevel;
	private File scriptFile;

	/**
	 * Creates a new obfuscator wrapper
	 */
	public KlassMasterObfuscator() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.WtkPluginObfuscator#getObfuscatorPlugin()
	 */
	protected Obfuscator getObfuscatorPlugin() {
		return new ZKMWtkPlugin();
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.WtkPluginObfuscator#runObfuscator(java.io.File, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void runObfuscator(File jarFileObfuscated, String wtkBinDir,
			String wtkLibDir, String jarFilename, String projectDir,
			String classPath, String emptyApi) 
	throws IOException 
	{
		this.plugin.run(jarFileObfuscated, wtkBinDir, wtkLibDir,
				jarFilename, projectDir, classPath, emptyApi);
		// for some reason the jar-file is just renamed, but the target-file is not created:
		File destinationFile = new File( jarFilename );
		destinationFile.renameTo( jarFileObfuscated );
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.WtkPluginObfuscator#createScriptFile(java.io.File, java.io.File)
	 */
	protected void createScriptFile( Device device, File jadFile, File projectDir, String classPath, String[] preserve )
	throws IOException
	{
		File script = new File( projectDir, "script.txt");
		if (this.scriptFile != null) {
			FileUtil.copy( this.scriptFile, script );
		} else {
			ArrayList lines = new ArrayList();
			StringBuffer excludeBuffer = new StringBuffer();
			excludeBuffer.append( "trimExclude " );
			for (int i = 0; i < preserve.length; i++) {
				excludeBuffer.append( preserve[i] );
				if (i != preserve.length -1 ) {
					excludeBuffer.append(" and ");
				}
			}
			excludeBuffer.append(';');
			lines.add( excludeBuffer.toString() );
			lines.add( "trim			deleteSourceFileAttributes=true" );
			lines.add( "				deleteDeprecatedAttributes=true" );
			lines.add( "				deleteUnknownAttributes=false;" );
			excludeBuffer = new StringBuffer();
			excludeBuffer.append( "exclude " );
			for (int i = 0; i < preserve.length; i++) {
				String className = preserve[i];
				int packageEnd = className.lastIndexOf('.');
				if (packageEnd == -1) {
					excludeBuffer.append( className )
						.append('^');
				} else {
					excludeBuffer.append( className.substring(0, packageEnd +1 ) )
						.append('^')
						.append( className.substring( packageEnd + 1))
						.append('^');
				}
				if (i != preserve.length -1 ) {
					excludeBuffer.append(" and ");
				}
			}
			excludeBuffer.append(';');
			lines.add( excludeBuffer.toString() );
			lines.add( "obfuscate	changeLogFileOut=\"ChangeLog.txt\"" );
			if (this.obfuscateFlowLevel != null) {
				lines.add("				obfuscateFlow=" + this.obfuscateFlowLevel );
			} else if (this.enableFlowObfuscation) {
				lines.add("				obfuscateFlow=aggressive" );
			} else {
				lines.add("				obfuscateFlow=none" );
			}
			lines.add("				encryptStringLiterals=none" );
			lines.add("				collapsePackagesWithDefault=\"\"");
			lines.add("				lineNumbers=delete;");
			FileUtil.writeTextFile( script, (String[]) lines.toArray( new String[ lines.size() ]) );
		} 
		this.plugin.createScriptFile(jadFile, projectDir);
	}
	
	public void setEnableFlowObfuscation( boolean enable ) {
		this.enableFlowObfuscation = enable;
	}
	
	public void setScriptFile( File scriptFile ) {
		this.scriptFile = scriptFile;
	}
	
	public void setObfuscateFlowLevel( String level ) {
		this.obfuscateFlowLevel = level;
	}

}
