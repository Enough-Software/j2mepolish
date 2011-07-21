/*
 * Created on 18-Aug-2005 at 15:58:25.
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
package de.enough.polish.precompile.blackberry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import de.enough.bytecode.ASMClassLoader;
import de.enough.bytecode.ClassHelper;
import de.enough.bytecode.DirClassLoader;
import de.enough.polish.Device;
import de.enough.polish.postcompile.renaming.ClassRenamingClassVisitor;
import de.enough.polish.postcompile.renaming.ClassRenamingHelper;
import de.enough.polish.precompile.PreCompiler;
import de.enough.polish.util.FileUtil;

/**
 * <p>Maps imports from javax.microedition.lcdui.* to de.enough.polish.blackberry.ui.*</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        18-Aug-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ImportResolver extends PreCompiler
{
  private static final String GRAPHICS_LCDUI = "javax/microedition/lcdui/Graphics";
  
  private static final String GRAPHICS_POLISH = "de/enough/polish/blackberry/ui/Graphics";
  
  private HashMap renamingMap;

	public ImportResolver()
  {
    this.renamingMap = new HashMap();
    this.renamingMap.put(GRAPHICS_LCDUI, GRAPHICS_POLISH);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.precompile.PreCompiler#preCompile(java.io.File, de.enough.polish.Device)
	 */
	public void preCompile(File classesDir, Device device)
	  throws BuildException 
	{
    // Do nothing if classesDir doesn't exist yet.
    // This happens when no classes where unpacked to classesDir at this stage.
    if (!classesDir.exists())
    {
      return;
    }

    // Find all classes and put their class names into the classes list.
    ArrayList classes = new ArrayList();
    String[] fileNames = FileUtil.filterDirectory( classesDir, ".class", true );
    System.out.println("Precompiling " + fileNames.length + " classes from " + classesDir.getPath() );
    
    for (int i = 0; i < fileNames.length; i++)
      {
        // Cut off file extension.
        String className = fileNames[i].substring(0, fileNames[i].length() - 6);
        
        // Add class name to known classes.
        classes.add(className);
      }

    // Create classloader for the classesDir.
    DirClassLoader loader = DirClassLoader.createClassLoader(classesDir);

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
              new ClassRenamingClassVisitor(writer, this.renamingMap);
            classNode.accept(visitor);
                    
            writeClass(classesDir, className, writer.toByteArray());
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
          throw new BuildException(e);
        }
      }
	}

  protected void writeClass(File classesDir, String className, byte[] byteArray)
    throws IOException
  {
    String tmpClassName = className.replace(File.separatorChar, '/');
    String newClassName = ClassRenamingHelper.doRenaming(tmpClassName, this.renamingMap);
    newClassName = newClassName.replace('/', File.separatorChar);
    
    // Write class file.
    ClassHelper.writeClass(classesDir, newClassName, byteArray);

    if (! className.equals(newClassName))
      {
        new File(classesDir, className + ".class").delete();
      }
  }
}
