//#condition polish.usePolishGui
/*
 * Created on Jul 8, 2010 at 10:11:08 PM.
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
package de.enough.polish.calendar;

import de.enough.polish.ui.Item;
import de.enough.polish.util.TimePoint;

/**
 * <p>Allows to create calendar items depending on their corresponding calendar entries (or whatever)</p>
 * <p>
 * Implementation can use CalendarItem.createCalendaryDay(..) if they only want to adapt style or background settings.
 * </p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see CalendarItem#createCalendaryDay(TimePoint, TimePoint, TimePoint, CalendarEntry[], CalendarItem)
 */
public interface CalendarRenderer {

	/**
	 * Creates an item that represents a day within this CalendarItem
	 * @param day the corresponding day
	 * @param currentMonth the month that is currently shown
	 * @param originalCurrentDay the original day (e.g. today) that was used to initialize this CalendarItem (should be highlighted in most cases)
	 * @param entriesForTheDay the events for the day, may be null
	 * @param parent the parent calendar item
	 * @return the created item, must not be null
	 */
	Item createCalendaryDay(TimePoint day, TimePoint currentMonth, TimePoint originalCurrentDay, CalendarEntry[] entriesForTheDay, CalendarItem parent);
}
