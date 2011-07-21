/*
 * Created on 26-Jun-2004 at 18:25:03.
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.util.StringUtil;

/**
 * <p>Obfuscates jar-files using the RetroGuard-obfuscator from Retrologic.</p>
 * <p>For details regarding the RetroGuard obfuscator, please refer to
 * http://www.retrologic.com/ 
 * </p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        26-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RetroGuardObfuscator extends Obfuscator {
	
	PrintWriter log;
	StringBufferOutputStream logBuffer;
		
	/**
	 * Creates a new obfuscator.
	 */
	public RetroGuardObfuscator() {
		super();
		this.logBuffer = new StringBufferOutputStream();
		this.log = new PrintWriter(
                new BufferedOutputStream( this.logBuffer ) );		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile,
			String[] preserve, Path bootClassPath) throws BuildException 
	{
        try {
    		System.out.println("Starting obfuscation with RetroGuard.");
	        StringBuffer preserveBuffer = new StringBuffer();
	        for (int i = 0; i < preserve.length; i++) {
				preserveBuffer.append(".class " )
							  .append( StringUtil.replace( preserve[i], '.', '/') )
							  .append('\n');
			}
        	this.logBuffer.clear();
        	AntClassLoader antClassLoader = new AntClassLoader( this.project, bootClassPath );
        	antClassLoader.addPathElement( this.libDir.getAbsolutePath() + File.separator + "retroguard.jar");
        	String polishHomeProperty = this.project.getProperty("polish.home");
        	if (polishHomeProperty != null) {
            	antClassLoader.addPathElement( polishHomeProperty + File.separator + "import" + File.separator + "retroguard.jar");
        	}
    		String[] apiPaths = device.getClassPaths();
    		for (int i = 0; i < apiPaths.length; i++) {
				antClassLoader.addPathElement( apiPaths[i] );
			}
        	// load the RetroGuard main class through the classloader:
        	Class guardDbClass = antClassLoader.forceLoadClass("COM.rl.obf.GuardDB");
        	Constructor guardDbConstructor = guardDbClass.getConstructor( new Class[]{ File.class} );
        	Class rgsEnumClass = antClassLoader.forceLoadClass("COM.rl.obf.RgsEnum");
        	Constructor rgsEnumConstructor = rgsEnumClass.getConstructor( new Class[]{ String.class });
        	Object rgsEnum = rgsEnumConstructor.newInstance( new Object[]{ preserveBuffer.toString() } );
        	Object database = guardDbConstructor.newInstance( new Object[]{ sourceFile } );
        	Method retain = guardDbClass.getMethod( "retain", new Class[]{ rgsEnumClass, PrintWriter.class} );
        	Method remapTo = guardDbClass.getMethod( "remapTo", new Class[]{ File.class, PrintWriter.class } );
        	Method close = guardDbClass.getMethod( "close", new Class[0] );
        	//System.out.println("ALL SYSTEMS GO.");
        	
        	//GuardDB db = (GuardDB) guardDbConstructor.newInstance( new Object[]{ sourceFile } );
        	//GuardDB db = new GuardDB( sourceFile );
        	try {
        		retain.invoke(database, new Object[]{ rgsEnum, this.log } );
        		remapTo.invoke(database, new Object[]{ targetFile, this.log } );
        		/*
		        db.retain(new RgsEnum(new PreserveInputStream( preserve )), log);
        		db.remapTo(targetFile, log);
        		*/
        	} finally {
        		close.invoke( database, new Object[0] );
        	}
        	System.out.println("RetroGuard has successfully finished obfuscation.");
        	//this.logBuffer.print();
        } catch (Exception e) {
        	System.out.println("RetroGuard-Log:\n");
        	this.logBuffer.print();
        	e.printStackTrace();
        	throw new BuildException("Unable to obfuscate: " + e.toString(), e );
        } finally {
			this.log.flush();
			this.log.close();
        }
	}
	
	class PreserveInputStream extends InputStream {
		byte[] buffer;
		int pos;
		
		PreserveInputStream( String[] preserve ) {
	        StringBuffer preserveBuffer = new StringBuffer();
	        for (int i = 0; i < preserve.length; i++) {
				preserveBuffer.append(".class " )
							  .append( preserve[i] )
							  .append( " protected\n");
			}
	        System.out.println("RETROGUARD PRESERVING: " + preserveBuffer.toString() );
	        this.buffer = preserveBuffer.toString().getBytes();
		}
		
		/* (non-Javadoc)
		 * @see java.io.InputStream#read()
		 */
		public int read() throws IOException {
			if (this.pos >= this.buffer.length) {
				return -1;
			}
			int c = this.buffer[ this.pos ];
			this.pos++;
			return c;
		}
	}
	
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
	
}
