/*
 * Created on 27-Oct-2005 at 19:21:53.
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
package de.enough.polish.emulator;

import java.io.File;
import java.util.ArrayList;


import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;

/**
 * <p>Starts an emulation session with the DoJa SDK.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        27-Oct-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DoJaEmulator extends Emulator {
	
	private File dojaHome;
	private String jarPath;
	private String heapSize;

	/**
	 * Creates a new emulator.
	 */
	public DoJaEmulator() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#init(de.enough.polish.Device, de.enough.polish.ant.emulator.EmulatorSetting, de.enough.polish.Environment, org.apache.tools.ant.Project, de.enough.polish.BooleanEvaluator, java.lang.String)
	 */
	public boolean init(Device device, EmulatorSetting setting,
			Environment env) 
	{
		File home = resolveDoJaHome( env );
		if (home == null) {
			System.err.println("Unable to find a suitable DoJa SDK, please specify the Ant property \"doja.home\".");
			return false;
		}
		this.dojaHome = home;
		this.jarPath = env.getVariable( "polish.jarPath" );
		
		if (env.hasSymbol("polish.HeapSize:defined")) {
			String heapStr = env.writeProperties("${bytes(polish.HeapSize)}");
			int heap = Integer.parseInt(heapStr);
			if (heap > 1024 * 1024) {
				// megabytes:
				this.heapSize = (heap / 1024 * 1024) + "M";
			} else {
				// kilobytes:
				this.heapSize = (heap / 1024) + "K";
			}
		}
		return true;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#supportsDebugger(de.enough.polish.emulator.Debugger)
	 */
	protected boolean supportsDebugger(Debugger debugger) {
		return false;
	}

	private File resolveDoJaHome(Environment env) {
		String path = env.resolveVariable("doja.home");
		if (path != null) {
			File file = new File( path );
			if (file.exists()) {
				return file;
			} else {
				System.err.println("The \"doja.home\" Ant property points to the invalid folder [" + path + "]. Please adjust this value in the build.xml script.");
				return null;
			}
		}
		if (File.separatorChar == '\\') {
			// this is a window system, so check for the default installation path:
			File file = new File("C:\\jDKDoJa2.5");
			if (file.exists()) {
				return file;
			}
			file = new File("C:\\iDK");
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getArguments()
	 */
	public String[] getArguments() {
		ArrayList argsList = new ArrayList();
		if (File.separatorChar == '/') {
			// this is a Unix system, use wine for execution:
			argsList.add("wine");
		}
		argsList.add(  this.dojaHome.getAbsolutePath() 
				+ File.separatorChar + "bin" + File.separatorChar + "doja.exe" );
		if (this.heapSize != null) {
			argsList.add("-mx");
			argsList.add( this.heapSize );
		}
		argsList.add( "-j" );
		argsList.add( this.jarPath );
		
		return (String[]) argsList.toArray( new String[ argsList.size() ] );
	}

}
