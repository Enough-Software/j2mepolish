/*
 * Created on 26-Jan-2006 at 17:02:52.
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
package de.enough.polish.ide.swing;

import javax.swing.JCheckBox;

import de.enough.polish.devices.DeviceTreeItem;
import de.enough.polish.devices.DeviceTreeItemSelectionListener;

/**
 * <p>Embeds a checkbox into a device tree item.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DeviceTreeCheckBox 
extends JCheckBox
implements DeviceTreeItemSelectionListener
{

	private static final long serialVersionUID = -7009258022433559783L;
	private final DeviceTreeItem item;

	/**
	 * @param item 
	 * @param selected 
	 * 
	 */
	public DeviceTreeCheckBox( DeviceTreeItem item, boolean selected) {
		super( item.toString(), selected );
		this.item = item;
		this.item.setSelectionListener( this );
		setSelected( item.isSelected() );
		setToolTipText( item.getDescription() );
	}
	
	public DeviceTreeItem getDeviceTreeItem() {
		return this.item;
	}

	public void notifySelected(DeviceTreeItem source, boolean selected) {
		setSelected( selected );
	}

}
