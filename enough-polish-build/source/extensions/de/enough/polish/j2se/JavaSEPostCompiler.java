/*
 * Created on Feb 5, 2007 at 11:03:06 AM.
 * 
 * Copyright (c) 2007 Michael Koch / Enough Software
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
package de.enough.polish.j2se;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.util.JarUtil;

import java.io.File;
import java.io.IOException;

public class JavaSEPostCompiler
extends PostCompiler
{
	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
	 */
	public void postCompile(File classesDir, Device device) throws BuildException
	{
//		byte[] buffer = new byte[1024 * 1024];
		String lastJarFile = null;
		try
		{
			Environment env = Environment.getInstance();
			String microemulatorHome = env.getVariable("microemulator.home");
			if (microemulatorHome == null) {
				microemulatorHome = env.getVariable("polish.home") + "/lib/microemulator";
			}
			File microemulatorDir = new File( microemulatorHome);
			if (!microemulatorDir.exists()) {
				throw new BuildException("Error: unable to locate the microemulator - the \"microemulator.home\" property in your build.xml script points to the invcalid path [" + microemulatorHome + "]. \"microemulator.home\" needs to point to the installation folder of the microemulator (=the folder in which the \"microemulator.jar\" is located).");
			}
			File jarFile = new File( microemulatorDir, "microemulator.jar" );
			lastJarFile = jarFile.getAbsolutePath();
			JarUtil.unjar(jarFile, classesDir);
			// copy libraries:
			File libDir = new File( microemulatorHome, "lib");
			String[] libFiles = libDir.list();
			if (libFiles != null) {
				for (int i = 0; i < libFiles.length; i++) {
					String lib = libFiles[i];
					if (lib.startsWith("micro") && lib.endsWith(".jar") && (lib.indexOf("applet") == -1)) {
						jarFile = new File( libDir, lib);
						lastJarFile = jarFile.getAbsolutePath();
						JarUtil.unjar(jarFile, classesDir);
					}
				}
			}
			//copy device skin:
			jarFile = new File( microemulatorDir, "devices/microemu-device-resizable.jar" );
			lastJarFile = jarFile.getAbsolutePath();
			JarUtil.unjar( jarFile, classesDir );
			//copy starter: (command line arguments cannot be specified using the manifest)
			jarFile = new File( env.getVariable("polish.home") + "/lib/enough-j2mepolish-microemulatorstarter.jar" );
			lastJarFile = jarFile.getAbsolutePath();
			JarUtil.unjar( jarFile, classesDir );
			// add keep settings for obfuscator:
			env.addToVariable( "polish.buildcontrol.obfuscation.keep", 
					"de.enough.polish.emulator.MicroEmulatorStarter,"
					//+ "org.microemu.app.Main,"
					//+ "org.microemu.device.impl.Rectangle"
					+  "org.microemu.**,"
					+  "javax.**"
			);
		}
		catch (IOException e)
		{
			e.printStackTrace();

			BuildException be = new BuildException("Unable to extract microemulator libraries: " + e.toString() + " for file " + lastJarFile );
			be.initCause(e);
			throw be;
		}
	}

//	/**
//	 * @param classesDir
//	 * @param buffer
//	 * @param fileIn
//	 * @throws IOException
//	 * @throws FileNotFoundException
//	 */
//	private void extractJar(File classesDir, byte[] buffer, File fileIn)
//			throws IOException, FileNotFoundException {
//		JarFile jarFile = new JarFile(fileIn);
//		Enumeration e = jarFile.entries();
//
//		while (e.hasMoreElements())
//		{
//			JarEntry entry = (JarEntry) e.nextElement();
//
//			if (!entry.isDirectory())
//			{
//				int bytesRead;
//				InputStream in = jarFile.getInputStream(entry);
//				File fileOut = new File(classesDir, entry.getName());
//				File parentDir = fileOut.getParentFile();
//
//				if (!parentDir.exists())
//				{
//					parentDir.mkdirs();
//				}
//
//				FileOutputStream out = new FileOutputStream(fileOut);
//
//				while ((bytesRead = in.read(buffer)) >= 0)
//				{
//					out.write(buffer, 0, bytesRead);
//				}
//
//				out.close();
//				in.close();
//			}
//		}
//	}
}
