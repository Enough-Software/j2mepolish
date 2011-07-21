/*
 * Created on Jul 30, 2008
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

import javax.bluetooth.LocalDevice;
import javax.microedition.lcdui.Display;

import de.enough.sysinfo.MIDPSysInfoMIDlet;

/**
 * @author timon
 *
 */
public class BTPropsCollector extends SysPropCollector {

	
	private static String[] propNames = new String[]{
		"bluetooth.api.version",
		"bluetooth.connected.devices.max",
		"bluetooth.connected.inquiry",
		"bluetooth.connected.inquiry.scan",
		"bluetooth.connected.page",
		"bluetooth.connected.page.scan"
	};
	                                               
	
	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.InfoCollector#collectInfos(de.enough.sysinfo.MIDPSysInfoMIDlet, javax.microedition.lcdui.Display)
	 */
	public void collectInfos(MIDPSysInfoMIDlet midlet, Display display) {
		//#debug
		System.out.println( "Bluetooth property names: " + propNames.length );
		
		for (int i = 0; i < propNames.length; i++) {
			try{
				String name = propNames[i];
				String value = LocalDevice.getProperty( name );
				if( (value != null) && (value.length() > 0 ) ){
					String pName = "property." + name; 
					addInfo( pName, value);
				}
			}catch (Exception e) {
				//#debug
				System.out.println( "Could not get bluetooth property " + propNames[i] );
			}
		}
		
	}

}
