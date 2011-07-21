//#condition polish.usePolishGui
/*
 * Created on May 30, 2007 at 12:09:08 AM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.ui.clockviews;

import java.util.Calendar;

/**
 * <p>Visualizes the clock as a hexadecimal string, e.g. 04:0b instead of 4:11.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        May 30, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HexTextClockView extends BinaryTextClockView {


	
	/**
	 * Updates the shown time.
	 * @param time the currently shown time
	 * @return the time as a hex string
	 */
	protected String updateTime( long time ) {
		this.lastTimeUpdate = time;
		this.date.setTime(time);
		this.calendar.setTime(this.date);
		String hours = Integer.toHexString( this.calendar.get( Calendar.HOUR_OF_DAY ) );
		if (hours.length() == 1) {
			hours = '0' + hours;
		}
		String minutes = Integer.toHexString( this.calendar.get( Calendar.MINUTE ) );
		if (minutes.length() == 1) {
			minutes = '0' + minutes;
		}
		String seconds = null;
		if (this.clockItem.includeSeconds()) {
			seconds = Integer.toHexString( this.calendar.get( Calendar.SECOND ) );
			if (seconds.length() == 1) {
				seconds = '0' + seconds;
			}
		}
		return this.clockItem.updateTime(hours, minutes, seconds); 
	}

}
