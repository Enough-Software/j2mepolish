/*
 * Created on Feb 4, 2009 at 2:55:57 PM.
 * 
 * Copyright (c) 2009 Robert Virkus / Enough Software
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
package de.enough.polish.libraryprocessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import de.enough.bytecode.ASMClassLoader;
import de.enough.bytecode.ClassHelper;
import de.enough.bytecode.DirClassLoader;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.postcompile.renaming.ClassRenamingClassVisitor;
import de.enough.polish.postcompile.renaming.ClassRenamingHelper;

/**
 * <p>Exchanges import statements in class files of binary libraries</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class ImportLibraryProcessor extends LibraryProcessor
{
	
	protected abstract void addImportConversions( ImportConversionMap conversions, Device device, Locale locale, Environment env );

	/* (non-Javadoc)
	 * @see de.enough.polish.libraryprocessor.LibraryProcessor#processLibrary(java.io.File, java.lang.String[], de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void processLibrary(File baseDir, String[] relativeClassPaths,
			Device device, Locale locale, Environment env) 
	throws IOException
	{
		//boolean isSlash = File.separatorChar == '/';
		ImportConversionMap renamingMap = new ImportConversionMap();
		addImportConversions( renamingMap, device, locale, env );
		
		ArrayList classes = new ArrayList();
		for (int i = 0; i < relativeClassPaths.length; i++)
		{
			// Cut off file extension.
			String classPath = relativeClassPaths[i];
			String className = classPath.substring(0, classPath.length() - ".class".length());

			// Add class name to known classes.
			classes.add(className);
		}
		System.out.println("Converting imports in " + classes.size() + " classes from " + baseDir.getPath() );

		// Create classloader for the classesDir.
		DirClassLoader loader = DirClassLoader.createClassLoader(baseDir);

		ASMClassLoader asmLoader = new ASMClassLoader(loader);
		Iterator classesIt = classes.iterator();

		while (classesIt.hasNext())
		{
			String className = (String) classesIt.next();

			try
			{
				ClassNode classNode = asmLoader.loadClass(className);
				ClassWriter writer = new ClassWriter(0);
				ClassRenamingClassVisitor visitor =
					new ClassRenamingClassVisitor(writer, renamingMap);
				classNode.accept(visitor);

				writeClass(baseDir, className, writer.toByteArray(), renamingMap );
			}
			catch (ClassNotFoundException e)
			{
				System.out.println("Error loading class " + className);
			}
			catch (IOException e)
			{
				throw new BuildException(e);
			}
			catch (Exception e)
			{
				System.out.println("Error loading class " + className + ": " + e.toString() );
				e.printStackTrace();
				throw new IOException(e.toString());
			}
		}
	}

	protected void writeClass(File classesDir, String className, byte[] byteArray, ImportConversionMap renamingMap)
	throws IOException
	{
		String tmpClassName = className.replace(File.separatorChar, '/');
		String newClassName = ClassRenamingHelper.doRenaming(tmpClassName, renamingMap);
		newClassName = newClassName.replace('/', File.separatorChar);

		// Write class file.
		ClassHelper.writeClass(classesDir, newClassName, byteArray);

		if (! className.equals(newClassName))
		{
			new File(classesDir, className + ".class").delete();
		}
	}

}
