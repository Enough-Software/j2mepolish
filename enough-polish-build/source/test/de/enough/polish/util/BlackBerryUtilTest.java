/*
 * Created on 07-04-2010 at 21:38:16.
 *
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.util;

import de.enough.polish.Device;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceManager;
import java.io.File;
import junit.framework.TestCase;

/**
 * <p>Tests the Native2Ascii implementation</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        28-Jun-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BlackBerryUtilTest extends TestCase {

    public BlackBerryUtilTest(String name) {
        super(name);
    }

    public void testBlackBerryOSVersion() {

        String platform = "BlackBerry/4.2";

        assertEquals("4.2", BlackBerryUtils.getBlackBerryOSVersion(platform));
        platform = "MIDP/2.0, BlackBerry/4.2";
        assertEquals("4.2", BlackBerryUtils.getBlackBerryOSVersion(platform));

        platform = "BlackBerry/4.2,MIDP/2.0";
        assertEquals("4.2", BlackBerryUtils.getBlackBerryOSVersion(platform));

        platform = "BlackBerry/4.3,MIDP/2.0";
        assertEquals("4.3", BlackBerryUtils.getBlackBerryOSVersion(platform));

        platform = "BlackBerry/5.0,MIDP/2.0";
        assertEquals("5.0", BlackBerryUtils.getBlackBerryOSVersion(platform));


        platform = "BlackBerry/5.0 , MIDP/2.0";
        assertEquals("5.0", BlackBerryUtils.getBlackBerryOSVersion(platform));

        platform = " BlackBerry/5.0 , MIDP/2.0 ";
        assertEquals("5.0", BlackBerryUtils.getBlackBerryOSVersion(platform));

        platform = " BlackBerry/5.1 , MIDP/2.0 ";
        assertEquals("5.1", BlackBerryUtils.getBlackBerryOSVersion(platform));
    }
}
