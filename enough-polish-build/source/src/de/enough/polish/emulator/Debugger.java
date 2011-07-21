/*
 * Created on 27-Oct-2005 at 00:46:30.
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

import java.util.List;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ant.emulator.DebuggerSetting;
import de.enough.polish.util.StringUtil;

/**
 * <p>Connects the emulator to a debugger.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        27-Oct-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Debugger extends Extension {

	/**
	 * Creates a new debugger. 
	 */
	public Debugger() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
	throws BuildException 
	{
		int port = ((DebuggerSetting) this.extensionSetting).getPort();
		connectDebugger( port, device, locale, env );
	}

	/**
	 * Connects to a debugger interface.
	 * 
	 * @param port the port in which the emulator listens
	 * @param device the current device
	 * @param locale the current locale
	 * @param env the environment
	 */
	public abstract void connectDebugger(int port, Device device, Locale locale, Environment env);

	/**
	 * Adds the debugging settings to the arguments list.
	 * By default the UEI arguments -Xdebug and -Xrunjdwp arguments are added by calling debugger.addDebugArguments( List ),
	 * unless the "polish.debug.commandline" variable is defined.
	 * The "debug.commandline" variable can be defined
	 * in one of the device database files (devices.xml, vendors.xml, etc) or in the &l;variables&g; section
	 * of the build.xml script. Several arguments can be defined by separating them with two semicolons (;;).
	 * Addtionally the "polish.debug.port" settings can be used, this is set by the "port" attribute
	 * of the &lt;debugger&gt; element.
	 * The following example sets the same arguments as the default UEI arguments:
	 * <pre>
	 * &lt;capability 
	 *     name="polish.debug.commandline"
	 *     value="-Xdebug;;-Xrunjdwp:address=${polish.debug.port},transport=dt_socket,server=y,suspend=n"
	 * /&gt;
	 * </pre>
	 * 
	 * @param env the environment settings. 
	 * @param argsList the arguments list
	 */
	public void addDebugArguments(Environment env, List argsList) {
		String line = env.getVariable("polish.debug.commandline");
		DebuggerSetting setting = (DebuggerSetting) this.extensionSetting;
		if ( line != null && line.length() > 1 ) {
			int port = setting.getPort();
			env.setVariable("polish.debug.port", "" + port );
			line = env.writeProperties(line);
			String[] lines = StringUtil.split( line, ";;" );
			for (int i = 0; i < lines.length; i++) {
				argsList.add( lines[i] );
			}
		} else {
			argsList.add( "-Xdebug" );
			argsList.add( getXRunJdwpCommandLine( setting ) );
		}

	} 
	
	public String getXRunJdwpCommandLine( DebuggerSetting setting ) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("-Xrunjdwp:transport=").append(setting.getTransport() );
		if (setting.isServer()) {
			buffer.append(",server=y");
		} else {
			buffer.append(",server=n");		
		}
		if (setting.isSuspend()) {
			buffer.append(",suspend=y");
		} else {
			buffer.append(",suspend=n");		
		}
		buffer.append(",address="  ).append( setting.getPort() );
		return buffer.toString();
	}

}
