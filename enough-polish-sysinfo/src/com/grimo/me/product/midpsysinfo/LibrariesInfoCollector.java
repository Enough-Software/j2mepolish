/*
 * Created on 11-Feb-2005 at 23:48:41.
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
package com.grimo.me.product.midpsysinfo;

import javax.microedition.lcdui.Display;

/**
 * <p>Collects information about supported APIs</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        11-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LibrariesInfoCollector extends InfoCollector {

	/**
	 * Creates a new collector.
	 */
	public LibrariesInfoCollector() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.InfoCollector#collectInfos(com.grimo.me.product.midpsysinfo.MIDPSysInfoMIDlet, javax.microedition.lcdui.Display)
	 */
	public void collectInfos(MIDPSysInfoMIDlet midlet, Display display) {
		try {
			Class.forName("javax.microedition.media.control.VideoControl");
			addInfo("MMAPI: ", "yes" );
            addInfo("MMAPI-Version: ",  getInfo(System.getProperty("microedition.media.version")) );
		} catch (ClassNotFoundException e) {
			addInfo("MMAPI: ", "no" );
		}
		try {
			Class.forName("javax.wireless.messaging.Message");
			addInfo("WMAPI 1.1: ", "yes" );
			try {
				Class.forName("javax.wireless.messaging.MultipartMessage");
				addInfo("WMAPI 2.0: ", "yes" );
			} catch (ClassNotFoundException e) {
				addInfo("WMAPI 2.0: ", "no" );
			}
		} catch (ClassNotFoundException e) {
			addInfo("WMAPI 1.1: ", "no" );
		}
		try {
			Class.forName("javax.bluetooth.DiscoveryAgent");
			addInfo("Bluetooth-API: ", "yes" );
			try {
				Class.forName("javax.obex.ClientSession");
				addInfo("Bluetooth-Obex-API: ", "yes" );
			} catch (ClassNotFoundException e) {
				addInfo("Bluetooth-Obex-API: ", "no" );
			}
		} catch (ClassNotFoundException e) {
			addInfo("Bluetooth-API: ", "no" );
		}
		try {
			Class.forName("javax.microedition.m3g.Graphics3D");
			addInfo("M3G-API: ", "yes" );
		} catch (ClassNotFoundException e) {
			addInfo("M3G-API: ", "no" );
		}
		try {
			Class.forName("javax.microedition.pim.PIM");
			addInfo("PIM-API: ", "yes" );
		} catch (ClassNotFoundException e) {
			addInfo("PIM-API: ", "no" );
		}
		try {
			Class.forName("javax.microedition.io.file.FileSystemRegistry");
			addInfo("FileConnection-API: ", "yes" );
		} catch (ClassNotFoundException e) {
			addInfo("FileConnection-API: ", "no" );
		}
		try {
			Class.forName("javax.microedition.location.Location");
			addInfo("Location-API: ", "yes" );
		} catch (ClassNotFoundException e) {
			addInfo("Location-API: ", "no" );
		}
		try {
			Class.forName("javax.microedition.xml.rpc.Operation");
			addInfo("WebServices-API: ", "yes" );
		} catch (ClassNotFoundException e) {
			addInfo("WebServices-API: ", "no" );
		}
		try {
			Class.forName("javax.microedition.sip.SipConnection");
			addInfo("SIP-API: ", "yes" );
		} catch (ClassNotFoundException e) {
			addInfo("SIP-API: ", "no" );
		}
		try {
			Class.forName("com.nokia.mid.ui.FullCanvas");
			addInfo("Nokia-UI-API: ", "yes" );
		} catch (ClassNotFoundException e) {
			addInfo("Nokia-UI-API: ", "no" );
		}
		try {
			Class.forName("com.siemens.mp.MIDlet");
			addInfo("Siemens-Extension-API: ", "yes" );
			try {
				Class.forName("com.siemens.mp.color_game.GameCanvas");
				addInfo("Siemens-ColorGame-API: ", "yes" );
			} catch (ClassNotFoundException e) {
				addInfo("Siemens-ColorGame-API: ", "no" );
			}
		} catch (ClassNotFoundException e) {
			addInfo("Siemens-Extension-API: ", "no" );
		}
		
	}

}
