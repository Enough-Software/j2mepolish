/*
 * Created on Jun 2, 2008 at 12:57:55 PM.
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
package de.enough.polish.devices;

import java.io.File;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DeviceManagerTest extends TestCase
{
	
	private static final String POLISH_HOME = ".";

    public void testResolveUserAgent(){
    	implResolveUserAgent( new FileUserAgentStorage(  new File("tmp/useragents.txt"), new File("tmp/useragents_unresolved.txt")) );
    	implResolveUserAgent(null);
    }
    
    public void implResolveUserAgent(UserAgentStorage storage){
		DeviceDatabase db = DeviceDatabase.getInstance( new File(POLISH_HOME ) );
		DeviceManager manager = db.getDeviceManager();
		String userAgent;
		de.enough.polish.Device device;
       
		userAgent = "MOT-RAZRV3x/85.97.41P MIB/BER2.2 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertTrue( device.getName().endsWith("V3x"));

		userAgent = "MOT-ROKR E2/R564_G_12.00.45P Mozilla/4.0 (compatible; MSIE 6.0; Linux; Motorola ROKR E2; 781) Profile/MIDP-2.0 Configuration/CLDC-1.1 Opera 8.50 [";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertTrue( device.getName().endsWith("E2"));

		userAgent = "Nokia6300/2.0 (05.50) Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("6300", device.getName() );

		userAgent = "NokiaN90-1/2.0523.1.4 Series60/2.8 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("N90", device.getName() );

		userAgent = "Mozilla/5.0 (SymbianOS/9.2 U; Series60/3.1 NokiaN95_8GB/10.0.007; Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("N95_8GB", device.getName() );
		
		userAgent = "SAMSUNG-SGH-U700/1.0 SHP/VPP/R5 NetFront/3.4 SMM-MMS/1.2.0 profile/MIDP-2.0 configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("SGH-U700", device.getName() );

		userAgent = "MOT-V3c/08.B5.10R MIB/2.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("V3c", device.getName() );

		userAgent = "Mozilla/2.0 (compatible; MSIE 3.02; Windows CE; PPC; 240x320) BlackBerry8800/4.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1 VendorID/102";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("8800", device.getName() );

		userAgent = "LG-KG800/V10e Obigo/WAP2.0 MIDP-2.0/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("KG800", device.getName() );

		userAgent = "LGE-KG800/V10e Obigo/WAP2.0 MIDP-2.0/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("KG800", device.getName() );

		userAgent = "LGKG800/V10e Obigo/WAP2.0 MIDP-2.0/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("KG800", device.getName() );

		userAgent = "Nokia5500d/2.0 (03.18) SymbianOS/9.1 Series60/3.0 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("5500", device.getName() );

		userAgent = "Nokia5500d/2.0 (03.19) SymbianOS/9.1 Series60/3.0 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("5500", device.getName() );
		
		
		userAgent = "MOTOROKR Z6/R60_G_80.xx.yyI Mozilla/4.0 (compatible; MSIE 6.0 Linux; MOTOROKR Z6;nnn) Profile/MIDP-2.0 Configuration/CLDC-1.1 Opera 8.50[yy]";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("Z6", device.getName() );
		
		userAgent = "MOT-Motorola L6/0A.52.26RX MIB/2.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("L6", device.getName() );
		
		userAgent = "MOT-SLVR L6/0A.52.26ZZ MIB/2.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("L6", device.getName() );

		userAgent = "MOT-SLVR";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNotNull(device);
		assertEquals("SLVR", device.getName() );
		
		userAgent = "Fantasy/6600";
		device = manager.getDeviceByUserAgent(userAgent, storage );
		assertNull(device);

	}
    
//    public void testA() {
//        DeviceDatabase deviceDatabase = DeviceDatabase.getInstance(new File(POLISH_HOME));
//        Device device = deviceDatabase.getDeviceManager().getDeviceByUserAgent("BlackBerry8110/4.3.0 Profile/MIDP-2.0 Configuration/CLDC-1.1 VendorID/124");
//        System.out.println(device);
//    }

}
