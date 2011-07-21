//#condition polish.usePolishGui

/*
 * Created on 24-Apr-2007 at 19:20:28.
 * 
 * Copyright (c) 2009 - 2009 Michael Koch / Enough Software
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
 * Interface to be able to listen to the status of the browser.
 * 
 * <p>copyright Enough Software 2006 - 2009</p>
 * @see Browser
 */
public interface BrowserListener
{
	void notifyPageStart(String url);
	void notifyPageEnd();

	void notifyDownloadStart(String url);
	void notifyDownloadEnd();
	void notifyPageError(String url, Exception e);
}
