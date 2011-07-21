/*
 * Created on Jun 8, 2010 at 9:37:08 PM.
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
package de.enough.polish.util;

import java.util.Date;
import java.util.TimeZone;

/**
 * <p>Eases date handling</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class DateUtil {

	/**
	 * private constructor - just use static methods
	 */
	private DateUtil() {
		// nothing to do
	}
	
	/**
	 * Retrieves the current time in ms for the default timezone.
	 * @return the current time in milliseconds since 01.01.1970 with adjustments for the default timezone.
	 */
	public final static long getCurrentTimeZoneTime() {
		return System.currentTimeMillis() - TimeZone.getDefault().getRawOffset();
	}


	/**
	 * Retrieves the current date for the default timezone.
	 * @return the current date with adjustments for the default timezone.
	 */
	public final static Date getCurrentTimeZoneDate() {
		return new Date( getCurrentTimeZoneTime() );
	}

}
