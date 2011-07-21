/*
 * Created on 6-Feb-2007 at 10:29:45.
 * 
 * Copyright (c) 2007 Michael Koch / Enough Software
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
package de.enough.polish.finalize.applet;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class AppletFinalizer
  extends Finalizer
{
  /* (non-Javadoc)
   * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
   */
  public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env)
  {
    String name = env.getVariable("MIDlet-Name");
    String jarName = jarFile.getName();
    String width = "240";
    String height = "320";
    if (device.getCapability("polish.ScreenSize") != null) {
    	width = device.getCapability("polish.ScreenWidth");
    	height = device.getCapability("polish.ScreenHeight");
    }
    
    try
    {
      File htmlFile = new File(jarFile.getParentFile(),
                              StringUtil.replace( jarFile.getName(), ".jar", ".html" ) );
      FileOutputStream out = new FileOutputStream(htmlFile);
      PrintWriter writer = new PrintWriter(out);
      
      writer.println("<html>");
      writer.println("<head><title>" + name + "</title></head>");
      writer.println("<body>");
      writer.println("\t<applet code=\"de.enough.polish.runtime.swing.SwingAppletSimulator\"");
      writer.println("\t        archive=\"" + jarName + "\" width=\"" + width + "\" height=\"" + height + "\">");
      writer.println("\t</applet>");
      writer.println("</body>");
      writer.println("</html>");
      
      writer.close();
      out.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      throw new BuildException(e.getMessage(), e);
    }
  }
}
