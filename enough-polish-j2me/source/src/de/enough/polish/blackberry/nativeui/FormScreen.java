//#condition polish.blackberry && polish.useNativeGui
/*
 * Created on Jan 23, 2010 at 3:36:21 AM.
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
package de.enough.polish.blackberry.nativeui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.MainScreen;

import de.enough.polish.blackberry.ui.CommandMenuItem;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.NativeItem;
import de.enough.polish.ui.NativeScreen;

/**
 * <p>Display Form contents</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FormScreen 
extends MainScreen
implements NativeScreen
{

	/**
	 * 
	 */
	public FormScreen(Form parent) {
		super( DEFAULT_MENU );
		//#if polish.BlackBerry.addDefaultClose != true
			super.setDefaultClose(false);
		//#endif
		//todo add support for title items
		setTitle( parent.getTitle() );
		Container root = parent.getRootContainer();
		if (root != null) {
			Item[] items = root.getItems();
			for (int i = 0; i < items.length; i++) {
				Item item = items[i];
				add( FieldHelper.getField( item ));
			}
		}
		Object[] commands = parent.getCommands();
		if (commands != null) {
			for (int i = 0; i < commands.length; i++) {
				Command command = (Command) commands[i];
				if (command == null) {
					break;
				}
				CommandMenuItem commandMenuItem = new CommandMenuItem( command, parent );
				addMenuItem(commandMenuItem);
			}
		}
		parent.setNativeScreen(this);
	}

	public void animate(long currentTime, ClippingRegion repaintRegion) {
		Field field = getFieldWithFocus();
		if (field instanceof NativeItem) {
			((NativeItem)field).animate(currentTime, repaintRegion);
		}
	}

	

}
