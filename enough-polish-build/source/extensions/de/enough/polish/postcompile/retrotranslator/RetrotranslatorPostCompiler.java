/*
 * Created on Jun 22, 2006 at 8:27:07 PM.
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
package de.enough.polish.postcompile.retrotranslator;

import java.io.File;
import java.util.Locale;

//import net.sf.retrotranslator.transformer.Retrotranslator;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.util.StringUtil;

/**
 * <p>Transforms Java classes that contain 1.5 syntax into 1.4 class format usung Retrotranslator. Since RetroWeaver proved much more powerful, Retrotranslator is currently inactive: DO NOT USE, UNLESS YOU MODIFY THE CODE YOURSELF.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jun 22, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RetrotranslatorPostCompiler extends PostCompiler {

	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
	 */
	public void postCompile(File classesDir, Device device)
			throws BuildException 
	{
//		File targetDir = new File( classesDir.getParentFile(), "classes14" );
//		targetDir.mkdir();
//		Retrotranslator retrotranslator = new Retrotranslator();
//		retrotranslator.addSrcdir( classesDir );
//		retrotranslator.setDestdir( targetDir );
//		retrotranslator.addClasspath( device.getBootClassPath() );
//		retrotranslator.addClasspath( device.getClassPath() );
//		retrotranslator.setVerify( false );
//		retrotranslator.setStripsign( true );
//		retrotranslator.setVerbose( true );
//		boolean success = retrotranslator.run();
//		System.out.println("Retrotranslator transformed classes successfully: " + success );
		
//		device.setClassesDir( targetDir.getAbsolutePath() );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#initialize(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void initialize(Device device, Locale locale, Environment env) {
		super.initialize(device, locale, env);
		env.addVariable("javac.source", "1.5");
		env.addVariable("javac.target", "1.5");
		env.addVariable("polish.Java5", "true" );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.PostCompiler#verifyBootClassPath(de.enough.polish.Device, java.lang.String)
	 */
	public String verifyBootClassPath(Device device, String bootClassPath) {
		if (bootClassPath.indexOf("cldc-1.1.jar") != -1) {
			return  StringUtil.replace( bootClassPath, "cldc-1.1.jar", "cldc-1.1-java5.0.jar" );			
		} else {
			return  StringUtil.replace( bootClassPath, "cldc-1.0.jar", "cldc-1.0-java5.0.jar" );
		}
	}
	
	
	
	

}
