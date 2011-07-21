//#condition polish.usePolishGui && polish.blackberry

/*
 * Created on Jul 7, 2008 at 2:16:30 PM.
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
package de.enough.polish.blackberry.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Maps from a command to a menuitem</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CommandMenuItem extends MenuItem {

	final Command cmd;
	private Displayable displayable;
	private Item item;
	protected final boolean isSeparator;

	/**
	 * @param cmd the command associated with this item
	 * @param displayable the displayable to which this command belongs to
	 */
	public CommandMenuItem(Command cmd, Displayable displayable) {
		this( cmd, displayable, null);
	}


	/**
	 * @param cmd the command associated with this item
	 * @param item the item to which this command belongs to
	 */
	public CommandMenuItem(Command cmd, Item item) {
		this( cmd, null, item);
	}
	
	private CommandMenuItem( Command cmd, Displayable displayable, Item item) {
		super( getLabel( cmd  ), 0, cmd.getPriority() );
		this.cmd = cmd;
		this.displayable = displayable;
		this.item = item;
		this.isSeparator = (cmd.getCommandType() == Command.SEPARATOR);		
	}
	
	
	private static String getLabel(Command command) {
		if (command.hasSubCommands()) {
			return command.getLabel() + "   >";
		}
		return command.getLabel();
	}



	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		handleCommand(this.cmd, this.displayable, this.item);
	}


	/**
	 * Handles the given command.
	 * 
	 * @param cmd the command associated with this item
	 * @param displayable the displayable to which this command belongs to
	 * @param item the item to which this command belongs to
	 * 
	 */
	public static void handleCommand(Command cmd, Displayable displayable, Item item) {
		if (cmd.hasSubCommands()) {
			// display children commands:
			CommandsPopup popup = new CommandsPopup( cmd, displayable, item );
	        Object lock = Application.getEventLock();
	        synchronized (lock) {
	        	UiApplication.getUiApplication().pushScreen(popup);
	        }
		} else if (displayable != null) {
			if (displayable instanceof Screen) {
				UiAccess.handleCommand((Screen)displayable, cmd);
			} else if (displayable instanceof CommandListener) {
				((CommandListener)displayable).commandAction(cmd, displayable);
	//		} else {
	//			Display.getInstance().commandAction(cmd, displayable);
			}
		} else {
			boolean handled = UiAccess.handleCommand(item, cmd);
			if (!handled) {
				UiAccess.handleCommand(item.getScreen(), cmd);
			}
		}
	}
	
	/**
	 * Checks if this item should be added to the context menu.
	 * @return true when this item should be added
	 */
	public boolean isContextMenu() {
		int type = this.cmd.getCommandType();
		return (type == Command.ITEM || type == Command.OK);
	}

}
