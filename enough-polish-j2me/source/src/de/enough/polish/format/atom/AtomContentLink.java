/*
 * Created on Aug 16, 2010 at 11:48:14 PM.
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
package de.enough.polish.format.atom;

/**
 * <p>Represents an HTML &lt;a&gt; link within an HTML content of an AtomEntry</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AtomContentLink {

	private final String href;
	private final String description;

	/**
	 * Creates a new link 
	 * @param href the link URL
	 * @param description the description
	 */
	public AtomContentLink( String href, String description) {
		this.href = href;
		this.description = description;
	}
	
	/**
	 * Retrieves the URL of the link
	 * @return the URL link
	 */
	public String getHref() {
		return this.href;
	}
	
	/**
	 * Retrieves the link text
	 * @return the link text
	 */
	public String getDescription() {
		return this.description;
	}
}
