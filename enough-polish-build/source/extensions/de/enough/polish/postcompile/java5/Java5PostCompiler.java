/*
 * Created on Jun 24, 2006 at 2:10:06 AM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.postcompile.java5;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import com.rc.retroweaver.RetroWeaver;
import com.rc.retroweaver.event.WeaveListener;

import de.enough.bytecode.ASMClassLoader;
import de.enough.bytecode.DirClassLoader;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.postcompile.BytecodePostCompiler;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Allows to use Java 5.0 syntax for J2ME applications.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jun 24, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Java5PostCompiler extends BytecodePostCompiler {

  private static final String POLISH_USE_DEFAULT_PACKAGE = "polish.useDefaultPackage";

  private static final String CLASS_ENUM = "de/enough/polish/java5/Enum";
  private static final String CLASS_ENUM_DEFAULT = "Enum";

  /**
	 * The class file version number.
	 */
	private static final Map versionMap = new HashMap();

	/**
	 * Initialize the version map.
	 */
	static {
		versionMap.put("1.2", new Integer( 46) );
		versionMap.put("1.3", new Integer( 47 ) );
		versionMap.put("1.4", new Integer( 48 ) );
		versionMap.put("1.5", new Integer( 49 ) );
	}
	
	private String target = "1.2";
	protected boolean isVerbose;

  /* (non-Javadoc)
   * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
   */
  public void postCompile(File classesDir, Device device, DirClassLoader loader, List classes) throws BuildException
  {
    int version = ( (Integer)versionMap.get( this.target)).intValue();
    RetroWeaver task = new RetroWeaver( version );
    task.setStripSignatures( true );
    boolean useDefaultPackage = this.environment.hasSymbol("polish.useDefaultPackage");
    boolean isCldc10 = this.environment.hasSymbol("polish.cldc1.0");
    if (isCldc10) {
    	task.addClassTranslation("java.lang.NoClassDefFoundError", "java.lang.Throwable");
    }
    task.addClassTranslation("java.lang.NoSuchFieldError", "java.lang.Throwable");
    task.addClassTranslation("java.lang.NoSuchMethodError", "java.lang.Throwable");
    if(!useDefaultPackage) {
	    task.setAutoboxClass("de.enough.polish.java5.Autobox");
	    task.setEnumClass("de.enough.polish.java5.Enum");
	    task.addClassTranslation("java.lang.Iterable", "de.enough.polish.util.Iterable");
	    task.addClassTranslation("java.util.Iterator", "de.enough.polish.util.Iterator");
    } else {
        task.setAutoboxClass("Autobox");
        task.setEnumClass("Enum");
        task.addClassTranslation("java.lang.Iterable", "Iterable");
        task.addClassTranslation("java.util.Iterator", "Iterator");    	
    }
    task.setListener( new WeaveListener() {
      public void weavingStarted(String msg) {
        System.out.println(msg);
      }

      public void weavingCompleted(String msg) {
        System.out.println(msg);
      }

      public void weavingPath(String pPath) {
        if (Java5PostCompiler.this.isVerbose) {
          System.out.println("Weaving " + pPath);
        }
      }
    });
    
    // We use a new classes directory, so that the user does not need to make a clean build each time he makes a small update.
    File newClassesDir = new File( classesDir.getParentFile(), "classes_12" );

    if (!newClassesDir.exists())
      {
        newClassesDir.mkdir();
      }
    
    try
      {
        FileUtil.copyDirectoryContents( classesDir, newClassesDir, true );
        loader = DirClassLoader.createClassLoader(newClassesDir);
      }
    catch (IOException e)
      {
        e.printStackTrace();
        BuildException be = new BuildException("Unable to copy classes to temporary directory.");
        be.initCause(e);
        throw be; 
      }
    
    device.setClassesDir( newClassesDir.getAbsolutePath() );
    
    try
      {
        task.weave( newClassesDir );
      }
    catch (IOException e)
      {
        e.printStackTrace();
        throw new BuildException("Unable to transform bytecode: " + e.toString() );
      }

    ASMClassLoader asmLoader = new ASMClassLoader(loader);
    EnumManager manager = EnumManager.getInstance();
    
    String enumClass = this.environment != null && this.environment.hasSymbol(POLISH_USE_DEFAULT_PACKAGE)
                       ? CLASS_ENUM_DEFAULT : CLASS_ENUM;
    
    // Clear global EnumManager instance.
    manager.clear();

    // Find all classes implementing java.lang.Enum.
    Iterator it = classes.iterator();
    
    while (it.hasNext())
      {
        String className = (String) it.next();
        
        try
          {
            ClassNode classNode = asmLoader.loadClass(className);
            
            if (enumClass.equals(classNode.superName))
              {
                manager.addEnumClass(className);
              }
          }
        catch (ClassNotFoundException e)
          {
            System.out.println("Error loading class " + className);
          }
      }
    
    // Find all local variables with enum classes as type.
    it = classes.iterator();
    
    while (it.hasNext())
      {
        String className = (String) it.next();
        
        try
          {
            // Load class.
            ClassNode classNode = asmLoader.loadClass(className, false);
            
            // Read the class and collect infos about enums.
            // TODO: Don't use a ClassWriter instance here. The stuff gets dropped
            // into nirvana anyway.
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            Type type = Type.getType("L" + className.replace('\\', '/') + ";");
            Java5CollectorClassVisitor visitor = new Java5CollectorClassVisitor(writer, type);
            classNode.accept(visitor);
          }
        catch (ClassNotFoundException e)
          {
            System.out.println("Error loading class " + className);
          }
      }
            
    // Process all classes.
    it = classes.iterator();
    
    while (it.hasNext())
      {
        String className = (String) it.next();
        
        try
          {
            // Load class.
            ClassNode classNode = asmLoader.loadClass(className);
            
            // Transform class. We need to write the transformed classes into another
            // ClassNode object as some transformations are done in visitEnd() methods
            // and the changes from there would be lost when writing the class directly. 
            ClassNode targetNode = new ClassNode();
            Java5ClassVisitor visitor = new Java5ClassVisitor(targetNode);
            classNode.accept(visitor);
            
            // Write class.
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            targetNode.accept(writer);
            writeClass(newClassesDir, className, writer.toByteArray());
          }
        catch (IOException e)
          {
            BuildException be = new BuildException("Error writing class " + className);
            be.initCause(e);
            throw be; 
          }
        catch (ClassNotFoundException e)
          {
            System.out.println("Error loading class " + className);
          }
      }
  }

  /* (non-Javadoc)
	 * @see de.enough.polish.Extension#initialize(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void initialize(Device device, Locale locale, Environment env) {
		super.initialize(device, locale, env);
		env.addVariable("javac.source", "1.5");
		env.addVariable("javac.target", "1.5");
		env.addVariable("polish.java5", "true" );
		env.addVariable("polish.classes.dirname", "classes_12" );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.PostCompiler#verifyBootClassPath(de.enough.polish.Device, java.lang.String)
	 */
	public String verifyBootClassPath(Device device, String bootClassPath) {
		if (bootClassPath.indexOf("cldc-1.1.jar") != -1) {
			return  StringUtil.replace( bootClassPath, "cldc-1.1.jar", "cldc-1.1-java5.0.jar" );			
		} else {
			return  StringUtil.replace( bootClassPath, "cldc-1.0.jar", "cldc-1.0-java5.0.jar" );
		}
	}
	
	/**
	 * Sets the class format target for the class transformation, which defaults to "1.2".
	 * 
	 * @param target the target, e.g. "1.2", "1.3" or "1.4"
	 */
	public void setTarget( String target ) {
		this.target = target;
	}

}