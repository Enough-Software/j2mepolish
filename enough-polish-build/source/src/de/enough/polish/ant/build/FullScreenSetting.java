/*
 * Created on 22-Jan-2003 at 15:37:27.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.ant.build;

/**
 * <p>Represents the settings for the use of full-screen-classes.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        22-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class FullScreenSetting {

	private boolean menu;
	private boolean enable;

	/**
	 * Creates a new full-screen setting
	 */
	public FullScreenSetting() {
		// initialisation is done via the setter methods
	}
	
	public void setEnable( boolean enable ) {
		this.enable = enable;
	}
	
	public void setMenu( boolean menu ) {
		this.menu = menu;
	}

	/**
	 * @return Returns the enable.
	 */
	public boolean isEnabled() {
		return this.enable;
	}

	/**
	 * @return Returns the menu.
	 */
	public boolean isMenu() {
		return this.menu;
	}

}
