/*
 * Created on 26-Jan-2006 at 19:58:39.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.devices;

/**
 * <p>Is used by the DeviceTreeItem for forwarding selection states to the User Interface.</p>
 * <p>The UI can register this listener by calling setSelectionListener() on the DeviceTreeItem.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface DeviceTreeItemSelectionListener {

	/**
	 * Notifies the GUI that the item has been selected.
	 * 
	 * @param source the originating DeviceTreeItem
	 * @param selected true when the GUI representation should have the selected state.
	 */
	void notifySelected( DeviceTreeItem source, boolean selected );
}
