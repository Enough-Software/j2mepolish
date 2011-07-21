package de.enough.polish.postcompile.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import de.enough.bytecode.ASMClassLoader;
import de.enough.bytecode.DirClassLoader;
import de.enough.polish.Device;
import de.enough.polish.postcompile.BytecodePostCompiler;

/**
 * This post compiler adds the needed serialization methods for classes
 * implementing de.enough.polish.serialization.Serializable.
 * 
 * @see de.enough.polish.io.Externalizable
 * @see de.enough.polish.io.Serializable
 * @see de.enough.polish.io.Serializer
 */
public class SerializationPostCompiler extends BytecodePostCompiler
{
	
	private List serializableAndExternalizableClassNames;
	
  /**
   * Weaves the serialization framework into classes implementing
   * de.enough.polish.serialization.Serializable.
   * 
   * @param classesDir The directory where the classes to post compile are located 
   * @param device The device to post compile for 
   * @param loader A class loader that loads from the classesDir
   * @param classes A list of class names to post compile
   *  
   * @exception BuildException If an error during build occurred
   */
  public void postCompile(File classesDir, Device device, DirClassLoader loader,
                          List classes)
    throws BuildException
  {
    // Find all classes implementing de.enough.polish.serialization.Serializable
    // and make them implement de.enough.polish.serialization.Externalizable
    // instead. In addition the two methods of Externalizable needs to be
    // added. When of these methods exist already they are not overridden.
    // If an already existing method has the wrong signature (aka wrong
    // modifiers) an error has to be thrown.
    
    // We should think about support for creating new objects with deserialization
    // too. This can be done by creating a constructor that takes a DataInputStream
    // to initialize its fields from.

    ASMClassLoader asmLoader = new ASMClassLoader(loader);
    Iterator classesIt = classes.iterator();

    while (classesIt.hasNext())
      {
        String className = (String) classesIt.next();
        
        try
          {
            ClassNode classNode = asmLoader.loadClass(className);
            ClassWriter writer = new ClassWriter(0);
            SerializationVisitor visitor = 
              new SerializationVisitor(writer, asmLoader, this.environment);
            classNode.accept(visitor);
                    
            writeClass(classesDir, className, writer.toByteArray());
          }
        catch (IOException e)
          {
            BuildException be = new BuildException("Error writing class " + className);
            be.initCause(e);
            throw be; 
          }
        catch (ClassNotFoundException e)
          {
            System.err.println("Serialization: error loading class " + className + ": " + e.toString() );
          }
        catch (Exception e)
        {
          BuildException be = new BuildException("Serialization: error processing class " + className + ": " + e.toString() );
          be.initCause(e);
          throw be; 
        }
      }
  }
  
  /**
   * Filters the classes so that only Serializable classes are processed which are not Externalizable.
   * 
   * @param classLoader class loader to load the classes if needed.
   * @param classes The list of class names to filter
   * @return The list of classes to post compile
   */
  public List filterClassList(DirClassLoader classLoader, List classes)
  {
	this.serializableAndExternalizableClassNames = new ArrayList();
    Iterator it = classes.iterator();
    LinkedList resultList = new LinkedList();
    ASMClassLoader asmClassLoader = new ASMClassLoader(classLoader);
    String serializableClassName = SerializationVisitor.getSerializableClassName(asmClassLoader, getEnvironment());
    String externalizableClassName = SerializationVisitor.getClassName(SerializationVisitor.EXTERNALIZABLE, getEnvironment());
    
    // rmi support:
    File sourceDir = new File( this.environment.getDevice().getClassesDir() );
	ArrayList rmiClassFiles = (ArrayList) this.environment.get("rmi-classes");
    while (it.hasNext())
      {
        String className = (String) it.next();

        try
          {
            ClassNode classNode = asmClassLoader.loadClass(className);
            
            // Continue with next class if node is an interface. 
            if ((classNode.access & Opcodes.ACC_INTERFACE) != 0)
              {
                continue;
              }
          }
        catch (ClassNotFoundException e)
          {
            // Continue with next class.
            continue;
          }
        
        if (asmClassLoader.inherits(serializableClassName, className))
          {
        	this.serializableAndExternalizableClassNames.add( className.replace(File.separatorChar, '.') );
        	if (rmiClassFiles != null) {
        		rmiClassFiles.add( new File( sourceDir, className + ".class" ) );
        	}
        	if (!asmClassLoader.inherits(externalizableClassName, className))
        	{
        		resultList.add(className);
        	}
          }
        
      }
    this.environment.set("serializable-classes", this.serializableAndExternalizableClassNames );
    if (rmiClassFiles != null) {
	    this.environment.set("rmi-classes-dir", sourceDir );
    }
    return resultList;
  }
  
  // TODO: This is only for testing yet.
  public static void main(String[] args)
  {
    SerializationPostCompiler postCompiler = new SerializationPostCompiler();
    postCompiler.postCompile(new File("."), null);
  }
}