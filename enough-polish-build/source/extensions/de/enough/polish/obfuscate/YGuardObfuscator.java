/*
 * Created on 02-Nov-2004 at 22:32:12.
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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import com.yworks.yguard.ObfuscationListener;
import com.yworks.yguard.obf.Cl;
import com.yworks.yguard.obf.GuardDB;
import com.yworks.yguard.obf.YGuardRule;

import de.enough.polish.Device;

/**
 * <p>Obfuscates classes using the LGPL yGuard obfuscator library.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        02-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class YGuardObfuscator 
extends Obfuscator
implements ObfuscationListener
{

	/**
	 * Creates a new obfuscator
	 */
	public YGuardObfuscator() {
		super();
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile,
			String[] preserve, Path bootClassPath) 
	throws BuildException 
	{
		System.out.println("Obfuscating with the yGuard library.");
		try {
			// set class resolver for this device:
			Cl.setClassResolver( new J2MEPolishClassResolver( this.project, device, bootClassPath, sourceFile ) );
			// create new obfuscator database:
			GuardDB database = new GuardDB( new File[]{ sourceFile} );
			//database.setResourceHandler( )
			database.setPedantic(true);
			database.setReplaceClassNameStrings(true);
			//database.addListener( this );
			Collection yGuardRules = new ArrayList();
			for (int i = 0; i < preserve.length; i++) {
				String className = preserve[i];
				className = className.replace('.', '/');
				//className = database.translateJavaClass(className);
				//System.out.println("Preservering class " + className);
				YGuardRule rule = new YGuardRule( YGuardRule.TYPE_CLASS, className, className);
				rule.retainClasses = YGuardRule.LEVEL_PUBLIC;
				rule.retainMethods = YGuardRule.LEVEL_PUBLIC;
				rule.retainFields = YGuardRule.LEVEL_PUBLIC;
				yGuardRules.add( rule );
			}
			StringBufferOutputStream loggerStream = new StringBufferOutputStream();
			PrintWriter logger = new PrintWriter( loggerStream );
			// define which elements should be spared from the obfuscation:
			database.retain(yGuardRules, logger );
			// obfuscate:
			database.remapTo( new File[]{ targetFile }, null, logger, false);
			// close obfuscator:
			database.close();
			// loggerStream.print();
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("yGuard was unable to obfuscate: " + e.toString(), e );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new BuildException("yGuard was unable to obfuscate: " + e.toString(), e );
		}
		System.out.println("Obfuscating finished successfully.");
	}

	/* (non-Javadoc)
	 * @see com.yworks.yguard.ObfuscationListener#obfuscatingJar(java.lang.String, java.lang.String)
	 */
	public void obfuscatingJar(String inJar, String outJar) {
		System.out.println("Obfuscating jar " + inJar + " to " + outJar);
	}

	/* (non-Javadoc)
	 * @see com.yworks.yguard.ObfuscationListener#obfuscatingClass(java.lang.String)
	 */
	public void obfuscatingClass(String name) {
		System.out.println("Obfuscating class " + name);
	}

	/* (non-Javadoc)
	 * @see com.yworks.yguard.ObfuscationListener#parsingClass(java.lang.String)
	 */
	public void parsingClass(String name) {
		System.out.println("Parsing class " + name);
	}

	/* (non-Javadoc)
	 * @see com.yworks.yguard.ObfuscationListener#parsingJar(java.lang.String)
	 */
	public void parsingJar(String name) {
		System.out.println("Parsing jar " + name);
	}

	/**
	 * <p>The logger stream.</p>
	 *
	 * <p>Copyright Enough Software 2004, 2005</p>

	 * <pre>
	 * history
	 *        02-Nov-2004 - rob creation
	 * </pre>
	 * @author Robert Virkus, j2mepolish@enough.de
	 */
	class StringBufferOutputStream extends OutputStream {
		
		StringBuffer buffer;
		
		StringBufferOutputStream() {
			this.buffer = new StringBuffer();
		}
		
		public void clear() {
			this.buffer.delete(0, this.buffer.length() );
		}

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			this.buffer.append( (char) b );
		}
		
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		public void write(byte[] b, int off, int len) throws IOException {
			this.buffer.append( new String( b, off, len ));
		}
		
		public void print() {
			System.out.println( this.buffer.toString() );
		}
	}

	class J2MEPolishClassResolver implements Cl.ClassResolver
	{
		
		private final AntClassLoader antClassLoader;

		public J2MEPolishClassResolver( Project project, Device device, Path bootClassPath, File sourceFile ) {
			this.antClassLoader = new AntClassLoader( project, bootClassPath );
			String[] paths = device.getClassPaths();
			for (int i = 0; i < paths.length; i++) {
				String path = paths[i];
				this.antClassLoader.addPathElement(path);
			}
			this.antClassLoader.addPathElement( sourceFile.getAbsolutePath() );
		}

		/* (non-Javadoc)
		 * @see com.yworks.yguard.obf.Cl.ClassResolver#resolve(java.lang.String)
		 */
		public Class resolve(String name ) throws ClassNotFoundException {
			//System.out.println("resolving class " + name + "...");
			// this results in a security exception for classes from the java.lang packages:
			//Class resolvedClass = this.antClassLoader.forceLoadClass(name);
			Class resolvedClass = this.antClassLoader.loadClass(name);
			//System.out.println("done.");
			return resolvedClass;
		}
		
	}
	
}
