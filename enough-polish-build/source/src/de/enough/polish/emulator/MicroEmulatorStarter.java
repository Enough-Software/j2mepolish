//#condition polish.usePolishGui
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

package de.enough.polish.emulator;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Provides an easy start for the MicroEmulator using reflection.
 * 
 * @author Robert Virkus
 *
 */
public class MicroEmulatorStarter {
	
	private MicroEmulatorStarter() {
		// nothing to init...
	}

	/**
	 * @param args further arguments for the MicroEmulator
	 */
	public static void main(String[] args)  {
		try {
			// read manifest:
			Properties props = new Properties();
			props.load( new MicroEmulatorStarter().getClass().getResourceAsStream("/META-INF/J2MEPOLISHMANIFEST.MF") );
			String midletClassName = props.getProperty("MIDlet-1");
			if (midletClassName == null) {
				System.err.println("Unable to read midlet class name from manifest, check the MIDlet-1 attribute within META-INF/J2MEPOLISHMANIFEST.MF.");
				System.out.println("got properties: ");
				Object[] keys = props.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {
					String key = (String)keys[i];
					System.out.println(key + "=" + props.getProperty(key));
				}
				if (args == null || args.length == 0) {
					System.exit(2);
				} else {
					System.out.println("Assuming necessary attributes are specified as command line arguments.");
				}
			}
			int startIndex = midletClassName.lastIndexOf(',');
			midletClassName = midletClassName.substring(startIndex+1).trim();
			Class meClass = Class.forName("org.microemu.app.Main");
			String[] newArgs = new String[] { 
					"--resizableDevice", "320", "240",
					"--device",  "org/microemu/device/resizable/device.xml",
					midletClassName
			};
			if (args == null || args.length == 0) {
				args = newArgs;
			} else {
				String[] cArgs = new String[ args.length + newArgs.length ];
				System.arraycopy(args, 0, cArgs, 0, args.length );
				System.arraycopy( newArgs, 0, cArgs, args.length, newArgs.length);
				args = cArgs;
			}
			Method mainMethod = meClass.getMethod("main", new Class[]{ args.getClass() } );
			mainMethod.invoke(null, new Object[]{ args } );
		} catch (Exception e) {
			System.out.println("Unable to start the MicroEmulator: " + e );
			e.printStackTrace();
			System.exit(1);
		}
	}

}
