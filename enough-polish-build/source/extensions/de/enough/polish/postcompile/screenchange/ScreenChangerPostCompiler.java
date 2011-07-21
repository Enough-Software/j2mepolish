/*
 * Created on 18-May-2005 at 15:44:27.
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
package de.enough.polish.postcompile.screenchange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;

import de.enough.bytecode.DirClassLoader;
import de.enough.bytecode.MethodInvocationMapping;
import de.enough.bytecode.MethodMapper;
import de.enough.polish.Device;
import de.enough.polish.postcompile.BytecodePostCompiler;

/**
 * <p>Moves calls to Display.setCurrent( Displayable ) to StyleSheet.setCurrent( Display, Displayable ) when screen change effects are activated.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        18-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScreenChangerPostCompiler extends BytecodePostCompiler {

	/**
	 * Creates a new screen changer post compiler.
	 */
	public ScreenChangerPostCompiler() {
		super();
	}
	
  /* (non-Javadoc)
   * @see de.enough.polish.postcompile.BytecodePostCompiler#filterClassList(de.enough.bytecode.DirClassLoader, java.util.List)
   */
  public List filterClassList(DirClassLoader classLoader, List classes)
  {
    Iterator it = classes.iterator();
    ArrayList filteredClasses = new ArrayList();
    
    while (it.hasNext())
      {
        String classFileName = (String) it.next();
        if (!(classFileName.endsWith("StyleSheet") 
          || classFileName.endsWith("MasterCanvas")
          || classFileName.endsWith("ScreenChangeAnimation") 
          || (classFileName.indexOf("screenanimations") != -1) )) 
          {
            filteredClasses.add( classFileName );
//            System.out.println("ScreenChanger: ADDING class " + classFileName);
//            } else {
//              System.out.println("ScreenChanger: skipping class " + classFileName);
          } 
      }
    
    return filteredClasses;
  }

  /*
   * (non-Javadoc)
   * @see de.enough.polish.postcompile.BytecodePostCompiler#postCompile(java.io.File, de.enough.polish.Device, de.enough.bytecode.DirClassLoader, java.util.List)
   */
  public void postCompile(File classesDir, Device device, DirClassLoader loader, List classes) throws BuildException
  {
    /*
     * this is tested within this extension's autostart XML definition
    boolean enableScreenEffects = this.environment.hasSymbol("polish.css.screen-change-animation");
    if (!enableScreenEffects) {
      return;
    }*/
		try {
			//System.out.println("mapping of Display.setCurrent() for " + files.length + " class files.");
			String targetClassName;
			if (this.environment.hasSymbol("polish.useDefaultPackage")) {
				targetClassName = "StyleSheet";
			} else {
				targetClassName = "de/enough/polish/ui/StyleSheet";
			}
			MethodMapper mapper = new MethodMapper();
			mapper.setClassLoader(loader);
			
			// Note: This code is duplicated in MasterCanvasPostCompiler.
			
			// on blackberry devices we provide our own display classes,
			// so there is a special case then:
			boolean usePolishBlackberryImpl = this.environment.hasSymbol("polish.blackberry");
			if (!usePolishBlackberryImpl) {
				// normal case:
				mapper.addMapping(
					new MethodInvocationMapping(true, "javax/microedition/lcdui/Display", "setCurrent",
												"(Ljavax/microedition/lcdui/Displayable;)V",
												false, targetClassName, "setCurrent",
												"(Ljavax/microedition/lcdui/Display;Ljavax/microedition/lcdui/Displayable;)V")
				);
			} else {
				// special blackberry case:
				mapper.addMapping(
						new MethodInvocationMapping(true, "de/enough/polish/blackberry/ui/Display", "setCurrent",
													"(Lde/enough/polish/blackberry/ui/Displayable;)V",
													false, targetClassName, "setCurrent",
													"(Lde/enough/polish/blackberry/ui/Display;Lde/enough/polish/blackberry/ui/Displayable;)V")
				);
			}
			
			mapper.doMethodMapping( classesDir, classes );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to map Display.setCurrent( Displayable ) to StyleSheet.setCurrent( Display, Displayable ): " + e.toString(), e );
		}
	}

}
