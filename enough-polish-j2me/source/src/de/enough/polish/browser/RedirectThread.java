//#condition polish.usePolishGui
/*
 * Created on Oct 28, 2008 at 9:25:23 PM.
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
package de.enough.polish.browser;

/**
 * <p>Provides an easy way to redirect the browser after a given time interval.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RedirectThread extends Thread
{

	private final Browser browser;
	private final long delay;
	private final String url;

	/**
	 * Creates a new redirect thread
	 * @param browser the browser
	 * @param delay the delay in milliseconds
	 * @param url the url to which the browser should be redirected
	 */
	public RedirectThread( Browser browser, long delay, String url) {
		this.browser = browser;
		this.delay = delay;
		this.url = url;
	}
	
	public void run() {
		try {
			Thread.sleep( this.delay );
		} catch (Exception e) {
			// ignore
		}
		this.browser.go( this.url );
	}
}
