/*
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ide.swing;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DeviceSelectionComponentTest {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 * @throws ClassNotFoundException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		String polishHome = "C:\\Programme\\J2ME-Polish";
		if (args.length != 0) {
			polishHome = args[0];
		}
		if (polishHome.charAt(0) != '/') {
			polishHome = '/' + polishHome; 
		}
			
        URL[] urls = new URL[] {
                new URL("file://" + polishHome + "/lib/enough-j2mepolish-build.jar"),
                new URL("file://" + polishHome + "/lib/jdom.jar"),
                new URL("file://" + polishHome + "/lib/ant.jar")
        };
        System.out.println(polishHome + "/lib/enough-j2mepolish-build.jar exists: " + (new File(polishHome + "/lib/enough-j2mepolish-build.jar").exists()) );
        System.out.println(polishHome + "/lib/jdom.jar exists: " + (new File(polishHome + "/lib/jdom.jar").exists()) );
        System.out.println(polishHome + "/lib/ant.jar exists: " + (new File(polishHome + "/lib/ant.jar").exists()) );
        URLClassLoader urlClassLoader = new URLClassLoader( urls, DeviceSelectionComponentTest.class.getClassLoader() );
        Class componentClass = urlClassLoader.loadClass("de.enough.polish.ide.swing.DeviceSelectionComponent");
        Constructor constructor = componentClass.getConstructor( new Class[]{ String.class } );
        constructor.newInstance( new Object[]{ polishHome} );
        System.out.println("component class loaded successfully :-)");
        
	}

}
