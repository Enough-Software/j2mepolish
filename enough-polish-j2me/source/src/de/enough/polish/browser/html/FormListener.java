/*
 * Created on May 16, 2008 at 7:17:09 AM.
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
package de.enough.polish.browser.html;


/**
 * <p>Listens for form creation and form submit events.</p>
 * <p>The listener needs to process each event swiftly and must not block the application.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface FormListener
{
	/**
	 * Verifies the initial value for a HTML form element.
	 * @param formAction the target URL of the corresponding HTML form
	 * @param name the name of the form element
	 * @param value the original value that should be used for the form element, can be null
	 * @return the value that should be used, the listener should return the speicifed value by default if no change is requested
	 */
	String verifyInitialFormValue( String formAction, String name, String value );

	/**
	 * Verifies the submission value for a HTML form element.
	 * @param formAction the target URL of the corresponding HTML form
	 * @param name the name of the form element
	 * @param value the original value that should be used for the form element, can be null
	 * @return the value that should be used, the listener should return the speicifed value by default if no change is requested
	 */
	String verifySubmitFormValue(  String formAction, String name, String value );

}
