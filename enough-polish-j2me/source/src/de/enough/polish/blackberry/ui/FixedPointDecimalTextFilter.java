//#condition polish.usePolishGui && polish.blackberry
/*
 * Created on May 24, 2011 at 10:42:29 AM.
 * 
 * Copyright (c) 2011 Robert Virkus / Enough Software
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
package de.enough.polish.blackberry.ui;

import de.enough.polish.ui.TextField;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;
import net.rim.device.api.ui.text.NumericTextFilter;

/**
 * <p>Filters text that have a fixed point decimal constraint</p>
 *
 * <p>Copyright Enough Software 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see TextField#FIXED_POINT_DECIMAL
 * @see UiAccess#CONSTRAINT_FIXED_POINT_DECIMAL
 */
public class FixedPointDecimalTextFilter extends NumericTextFilter {


	/**
	 * Creates a new filter
	 */
	public FixedPointDecimalTextFilter() {
		super();
	}

	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.text.TextFilter#convert(char, int)
	 */
	public char convert(char c, int status) {
		if (Character.isDigit(c) || c == Locale.DECIMAL_SEPARATOR  || c == Locale.GROUPING_SEPARATOR || c == '.') {
			return c;
		}
		return super.convert(c, status);
	}

	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.text.TextFilter#validate(char)
	 */
	public boolean validate(char c) {
		if (Character.isDigit(c) || c == Locale.DECIMAL_SEPARATOR || c == Locale.GROUPING_SEPARATOR || c == '.') {
			return true;
		}
		return super.validate(c);
	}

}
