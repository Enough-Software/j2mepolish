package de.enough.polish.postcompile.renaming;

import de.enough.bytecode.ASMClassLoader;
import de.enough.bytecode.ClassHelper;
import de.enough.bytecode.DirClassLoader;
import de.enough.polish.Device;
import de.enough.polish.postcompile.BytecodePostCompiler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassRenamingPostCompiler
    extends BytecodePostCompiler
{
  private static final String RESEVERD_KEYWORD_SERIALIZABLE =
    "polish.Bugs.ReservedKeywordSerializable";

  private HashMap renamingMap;
  
  public ClassRenamingPostCompiler()
  {
    this.renamingMap = new HashMap();
  }

  /* (non-Javadoc)
   * @see de.enough.polish.postcompile.BytecodePostCompiler#postCompile(java.io.File, de.enough.polish.Device, de.enough.bytecode.DirClassLoader, java.util.List)
   */
  public void postCompile(File classesDir, Device device, DirClassLoader loader, List classes) throws BuildException
  {
    if (device.getEnvironment().hasSymbol(RESEVERD_KEYWORD_SERIALIZABLE))
      {
        this.renamingMap.put("de/enough/polish/io/Serializable", "de/enough/polish/io/EnoughSerializable");
        System.out.println("Renaming de.enough.polish.io.Serializable (reserved keyword)...");
      }
    
    if (this.renamingMap.size() == 0)
      {
        //System.out.println("Nothing to do. Exiting.");
        return;
      }
    
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

    // Delete old class file if renamed.
    if (! className.equals(newClassName))
      {
        new File(classesDir, className + ".class").delete();
      }
  }
}
