/*
 * Created on May 23, 2008 at 4:52:47 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package com.grimo.me.product.midpsysinfo;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Form;

import de.enough.sysinfo.MIDPSysInfoMIDlet;

/**
 * Tests MIDP platform request.
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PlatformRequestViewer extends Form
{
	private final MIDPSysInfoMIDlet sysInfoMIDlet;
	
	public PlatformRequestViewer(MIDPSysInfoMIDlet sysInfoMIDlet) {
		super("Platform Request");
		this.sysInfoMIDlet = sysInfoMIDlet;
		this.setCommandListener(sysInfoMIDlet);
		this.addCommand( sysInfoMIDlet.backCmd );
		try
		{
			boolean exit = sysInfoMIDlet.platformRequest("http://m.j2mepolish.org");
			append("http platformRequest works, exit required=" + exit );
		} catch (ConnectionNotFoundException e)
		{
			append("http platformRequest results in " + e.toString() );
		}
		try
		{
			boolean exit = sysInfoMIDlet.platformRequest("tel:+49123123123");
			append("tel platformRequest works, exit required=" + exit );
		} catch (ConnectionNotFoundException e)
		{
			append("tel platformRequest results in " + e.toString() );
		}
	}
}
