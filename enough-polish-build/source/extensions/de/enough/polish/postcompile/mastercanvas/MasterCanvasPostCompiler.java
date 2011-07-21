/*
 * Created on 07-Jun-2005 at 13:09:58.
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

package de.enough.polish.postcompile.mastercanvas;

import de.enough.bytecode.DirClassLoader;
import de.enough.bytecode.MethodInvocationMapping;
import de.enough.bytecode.MethodMapper;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.postcompile.BytecodePostCompiler;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;

/**
 * <p>
 * Maps display.setCurrent(), display.getCurrent() and Canvas.repaint() on
 * MasterCanvas.
 * </p>
 * <p>
 * Copyright Enough Software 2005
 * </p>
 * 
 * <pre>
 *  history
 *         07-Jun-2005 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MasterCanvasPostCompiler extends BytecodePostCompiler
{
  /**
   * Creates a new post compiler
   */
  public MasterCanvasPostCompiler()
  {
    // Do nothing here.
  }

  /*
   * (non-Javadoc)
   * @see de.enough.polish.postcompile.BytecodePostCompiler#postCompile(java.io.File, de.enough.polish.Device, de.enough.bytecode.DirClassLoader, java.util.List)
   */
  public void postCompile(File classesDir, Device device, DirClassLoader loader, List classes) throws BuildException
  {
	  Environment env = device.getEnvironment();
	  if (!env.hasSymbol("polish.useFullScreen")) {
		  System.out.println("Warning: the device " + device.getIdentifier() + " flickers when a new displayable is shown in Display.setCurrent(). J2ME Polish can fix this, but only in the fullscreen mode. Set the \"fullscreen\" attribute of the <build> element to \"menu\" to handle this: <build fullscreen=\"menu\">.");
		  return;
	  }
	  if (env.hasSymbol("polish.skipMasterCanvas")) {
		  System.out.println("Skipping MasterCanvasPostCompiler.");
		  return;
	  }
    try
      {
        System.out.println("MasterCanvas: mapping of Display.setCurrent() for "
                           + classes.size() + " classes.");
        String masterCanvasClassName = getMasterCanvasClassName();

        MethodMapper mapper = new MethodMapper();
        mapper.setClassLoader(loader);

        boolean enableScreenEffects = this.environment.hasSymbol("polish.css.screen-change-animation")
                                      || this.environment.hasSymbol("polish.ScreenChangeAnimation.forward:defined");
        if (enableScreenEffects)
          {
            String styleSheetClass = getStyleSheetClassName();

            mapper.addMapping(new MethodInvocationMapping(true,
                                                          "javax/microedition/lcdui/Display",
                                                          "setCurrent",
                                                          "(Ljavax/microedition/lcdui/Displayable;)V",
                                                          false,
                                                          styleSheetClass,
                                                          "setCurrent",
                                                          "(Ljavax/microedition/lcdui/Display;Ljavax/microedition/lcdui/Displayable;)V"));

          }
        else
          {
            mapper.addMapping(new MethodInvocationMapping(true,
                                                          "javax/microedition/lcdui/Display",
                                                          "setCurrent",
                                                          "(Ljavax/microedition/lcdui/Displayable;)V",
                                                          false,
                                                          masterCanvasClassName,
                                                          "setCurrent",
                                                          "(Ljavax/microedition/lcdui/Display;Ljavax/microedition/lcdui/Displayable;)V"));
          }
        mapper.addMapping(new MethodInvocationMapping(true,
                                                      "javax/microedition/lcdui/Display",
                                                      "getCurrent",
                                                      "()Ljavax/microedition/lcdui/Displayable;",
                                                      false,
                                                      masterCanvasClassName,
                                                      "getCurrent",
                                                      "(Ljavax/microedition/lcdui/Display;)Ljavax/microedition/lcdui/Displayable;"));

        // mapping of isShown(), please note the different signature ()Z instead of ()V, since
        // a boolean value is returned:
        mapper.addMapping(new MethodInvocationMapping(true,
                                                      "javax/microedition/lcdui/Displayable",
                                                      "isShown", "()Z", false,
                                                      masterCanvasClassName,
                                                      "isDisplayableShown",
                                                      "(Ljavax/microedition/lcdui/Displayable;)Z"));

        // mapping of repaint():
        mapper.addMapping(new MethodInvocationMapping(true,
                                                      "javax/microedition/lcdui/Canvas",
                                                      "repaint", "()V", false,
                                                      masterCanvasClassName,
                                                      "repaintCanvas",
                                                      "(Ljavax/microedition/lcdui/Canvas;)V"));
        
        // mapping of repaint(int, int. int, int):
        mapper.addMapping(new MethodInvocationMapping(true,
                                                      "javax/microedition/lcdui/Canvas",
                                                      "repaint", "(IIII)V", false,
                                                      masterCanvasClassName,
                                                      "repaintCanvas",
                                                      "(Ljavax/microedition/lcdui/Canvas;IIII)V"));

        mapper.doMethodMapping(classesDir, classes);
        System.out.println("MasterCanvasPostCompiler finished.");
      }
    catch (Throwable e)
      {
        e.printStackTrace();
        throw new BuildException("Unable to map Display.setCurrent( Displayable ) to StyleSheet.setCurrent( Display, Displayable ): "
                                 + e.toString(), e);
      }
  }
  
  /* (non-Javadoc)
   * @see de.enough.polish.postcompile.BytecodePostCompiler#filterClassList(de.enough.bytecode.DirClassLoader, java.util.List)
   */
  public List filterClassList(DirClassLoader classLoader, List classes)
  {
    Iterator it = classes.iterator();
    LinkedList list = new LinkedList();

    while (it.hasNext())
      {
        String className = (String) it.next();
        
        if (className.endsWith("StyleSheet") || className.endsWith("MasterCanvas"))
            {
              continue;
            }
        
	      list.add(className);
      }
    
    return list;
  }

  private String getMasterCanvasClassName()
  {
    boolean useDefaultPackage = this.environment.hasSymbol("polish.useDefaultPackage");
    String accessibleCanvasClassName;
    if ( useDefaultPackage )
      {
        accessibleCanvasClassName = "MasterCanvas";
      }
    else
      {
        accessibleCanvasClassName = "de/enough/polish/ui/MasterCanvas";
      }
    return accessibleCanvasClassName;
  }
  
  private String getStyleSheetClassName()
  {
    boolean useDefaultPackage = this.environment.hasSymbol("polish.useDefaultPackage");
    String accessibleCanvasClassName;
    if ( useDefaultPackage )
      {
        accessibleCanvasClassName = "StyleSheet";
      }
    else
      {
        accessibleCanvasClassName = "de/enough/polish/ui/StyleSheet";
      }
    return accessibleCanvasClassName;
  }
}
