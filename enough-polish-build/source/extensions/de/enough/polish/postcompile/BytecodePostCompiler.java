package de.enough.polish.postcompile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;

import de.enough.bytecode.ClassHelper;
import de.enough.bytecode.DirClassLoader;
import de.enough.polish.Device;
import de.enough.polish.util.FileUtil;

/**
 * Abstract post compiler that helps to write post compilers that need to
 * rewrite class files.
 */
public abstract class BytecodePostCompiler extends PostCompiler
{
  /* (non-Javadoc)
   * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
   */
  public void postCompile(File classesDir, Device device)
    throws BuildException
  {
    // Find all classes and put their class names into the classes list.
    ArrayList classes = new ArrayList();
    String[] fileNames = FileUtil.filterDirectory( classesDir, ".class", true );
    
    for (int i = 0; i < fileNames.length; i++)
      {
        // Cut off file extension.
        String className = fileNames[i].substring(0, fileNames[i].length() - 6);
        
        // Add class name to known classes.
        classes.add(className);
      }

    // Create classloader for the classesDir.
    DirClassLoader loader = DirClassLoader.createClassLoader(device.getClassLoader(), classesDir);
    
    // Post compile stuff.
    postCompile(classesDir, device, loader, filterClassList(loader, classes));
  }
  
  /**
   * Post compile the given classes.
   * 
   * @param classesDir The directory the class files are located
   * @param device The device to post compile for
   * @param loader A class loader for the classes directory
   * @param classes List of classes to post compile
   * @throws BuildException
   */
  public abstract void postCompile(File classesDir, Device device,
                                   DirClassLoader loader, List classes)
    throws BuildException;
  
  /**
   * Filters the class list. This can be used to only post compile classes
   * with a given class name schema.
   * 
   * The default implementation just returns the given list.
   * 
   * @param classLoader class loader to load the classes if needed.
   * @param classes The list of class names to filter
   * 
   * @return The list of classes to post compile
   */
  public List filterClassList(DirClassLoader classLoader, List classes)
  {
    return classes;
  }

  protected void writeClass(File classesDir, String className, byte[] byteArray)
    throws IOException
  {
    // Write class file.
    ClassHelper.writeClass(classesDir, className, byteArray);
  }
}
