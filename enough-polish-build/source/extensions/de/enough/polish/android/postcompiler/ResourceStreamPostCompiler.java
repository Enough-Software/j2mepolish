/*
 * Created on Oct 13, 2008 at 3:39:34 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.android.postcompiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.tools.ant.BuildException;

import de.enough.bytecode.DirClassLoader;
import de.enough.bytecode.MethodInvocationMapping;
import de.enough.bytecode.MethodMapper;
import de.enough.polish.Device;
import de.enough.polish.postcompile.BytecodePostCompiler;

/**
 * <p>Replaces calls to getClass().getResourceAsStream() with calls to ResourcesHelper.getResourceAsStream()</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceStreamPostCompiler extends BytecodePostCompiler
{

	/**
	 * 
	 */
	public ResourceStreamPostCompiler()
	{
		// nothing to init 
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.BytecodePostCompiler#postCompile(java.io.File, de.enough.polish.Device, de.enough.bytecode.DirClassLoader, java.util.List)
	 */
	public void postCompile(File classesDir, Device device,
			DirClassLoader loader, List classes) throws BuildException
	{

        MethodMapper mapper = new MethodMapper();
        mapper.setClassLoader(loader);

        mapper.addMapping(new MethodInvocationMapping(true,
                "java/lang/Class",
                "getResourceAsStream",
                "(Ljava/lang/String;)Ljava/io/InputStream;",
                false,
                "de/enough/polish/android/helper/ResourcesHelper",
                "getResourceAsStream",
                "(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;"));
                 
        try
		{
			mapper.doMethodMapping(classesDir, classes);
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new de.enough.polish.BuildException(e);
		}
        System.out.println("ResourceStreamPostCompiler finished.");
	}

}
