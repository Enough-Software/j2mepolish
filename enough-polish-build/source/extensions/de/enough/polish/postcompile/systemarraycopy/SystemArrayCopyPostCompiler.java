/*
 * Created on 07-August-2008 at 13:09:58.
 * 
 * Copyright (c) 2008 Robert Virkus / Enough Software
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

package de.enough.polish.postcompile.systemarraycopy;

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
 * <p>Maps System.arraycopy() to Arrays.arraycopy()</p>
 * <p>
 * Copyright Enough Software 2008
 * </p>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SystemArrayCopyPostCompiler extends BytecodePostCompiler
{
  /**
   * Creates a new post compiler
   */
  public SystemArrayCopyPostCompiler()
  {
    // Do nothing here.
  }

  /*
   * (non-Javadoc)
   * @see de.enough.polish.postcompile.BytecodePostCompiler#postCompile(java.io.File, de.enough.polish.Device, de.enough.bytecode.DirClassLoader, java.util.List)
   */
  public void postCompile(File classesDir, Device device, DirClassLoader loader, List classes) throws BuildException
  {


        MethodMapper mapper = new MethodMapper();
        mapper.setClassLoader(loader);

      
        mapper.addMapping(new MethodInvocationMapping(false,
                                                      "java/lang/System",
                                                      "arraycopy",
                                                      "(Ljava/lang/Object;ILjava/lang/Object;II)V",
                                                      false,
                                                      getArraysClassName(),
                                                      "arraycopy",
                                                      "(Ljava/lang/Object;ILjava/lang/Object;II)V"));

         
        try
		{
			mapper.doMethodMapping(classesDir, classes);
		} catch (IOException e)
		{
			e.printStackTrace();
			throw new de.enough.polish.BuildException(e);
		}
        System.out.println("SystemArrayCopyPostCompiler finished.");
     
  }
  
 
  private String getArraysClassName()
  {
    boolean useDefaultPackage = this.environment.hasSymbol("polish.useDefaultPackage");
    String arraysClassName;
    if ( useDefaultPackage )
      {
        arraysClassName = "Arrays";
      }
    else
      {
        arraysClassName = "de/enough/polish/util/Arrays";
      }
    return arraysClassName;
  }
  

}
