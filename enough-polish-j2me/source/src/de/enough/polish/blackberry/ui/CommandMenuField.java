//#condition polish.usePolishGui && polish.blackberry
/*
 * Created on Jun 23, 2010 at 12:25:53 PM.
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
package de.enough.polish.blackberry.ui;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ArrayList;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;

/**
 * <p>Displays a command as a native BB field</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CommandMenuField
extends ButtonField
implements FieldChangeListener
{

	private final Command cmd;
	private final Displayable displayable;
	private final Item item;
	private final CommandsPopup popup;
	private CommandListener listener;
	
	public CommandMenuField(CommandsPopup popup, Command cmd, CommandListener listener) {
		this( popup, cmd, Display.getInstance().getCurrent(), null );
		this.listener = listener;
	}


	public CommandMenuField(CommandsPopup popup, Command cmd, Displayable displayable, Item item) {
		super( cmd.getLabel(), Field.FOCUSABLE | ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER );
		this.popup = popup;
		this.cmd = cmd;
		this.displayable = displayable;
		this.item = item;
		setChangeListener(this);
	}
	
	public void fieldChanged(Field field, int context) {
		if (context != PROGRAMMATIC) {
			if (!this.cmd.hasSubCommands()) {
				this.popup.close();
			}
			if (this.listener != null) {
				this.listener.commandAction(this.cmd, this.displayable);
			} else {
				CommandMenuItem.handleCommand(this.cmd, this.displayable, this.item);
			}
		}
	}
	
	
}
