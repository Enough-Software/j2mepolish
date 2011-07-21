//#condition polish.usePolishGui && polish.blackberry

/*
 * Created on Jun 23, 2010 at 12:16:01 PM.
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
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * <p>Displays a popup screen with subcommands</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CommandsPopup
//#if polish.JavaPlatform >= BlackBerry/4.5
	extends PopupScreen
//#else
	//# extends BaseScreen
//#endif
{
	
	/**
	 * Creates a new sub commands popup that is populated with the subcommands from the command parameter
	 * @param command
	 * @param displayable
	 * @param item
	 */
	public CommandsPopup(Command command, Displayable displayable, Item item) {
		super(new VerticalFieldManager(Manager.VERTICAL_SCROLL), Field.FIELD_BOTTOM | Field.FIELD_HCENTER);
		LabelField title = new LabelField( command.getLabel() ); 
		add( title );
		Object[] cmds = command.getSubCommmandsArray();
		for (int i = 0; i < cmds.length; i++) {
			Command child = (Command) cmds[i];
			if (child == null) {
				break;
			}
			CommandMenuField field = new CommandMenuField( this, child, displayable, item );
			add(field);
		}
	}
	
	/**
	 * Creates a new sub commands popup that is populated with the subcommands from the command parameter
	 * @param cmds the commands that should be displayed
	 * @param listener the command listener
	 */
	public CommandsPopup(Command[] cmds, CommandListener listener) {
		super(new VerticalFieldManager(Manager.VERTICAL_SCROLL), Field.FIELD_BOTTOM | Field.FIELD_HCENTER);
		for (int i = 0; i < cmds.length; i++) {
			Command child = (Command) cmds[i];
			if (child == null) {
				break;
			}
			CommandMenuField field = new CommandMenuField( this, child, listener );
			add(field);
		}
	}

	 //#if polish.hasTrackballEvents
    /* (non-Javadoc)
     * @see net.rim.device.api.ui.Screen#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(int dx, int dy, int status, int time)
    {
    	boolean processed = super.navigationMovement(dx, dy, status, time);
    	if (!processed) {
            int absDx = dx < 0 ? -dx : dx;
            int absDy = dy < 0 ? -dy : dy;
            if (absDx > absDy && dx < 0) {
            	close();
            	processed = true;
            }
    	}
    	return processed;
    }
	//#endif

	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Screen#onMenu(int)
	 */
	public boolean onMenu(int instance) {
		close();
		return true;
		//return super.onMenu(instance);
	}

	/* (non-Javadoc)
     * @see net.rim.device.api.ui.Screen#keyUp(int, int)
     */ 
	protected boolean keyUp(int keyCode, int time) {
		boolean handled = super.keyUp( keyCode, time );
		if (!handled && (Keypad.map( keyCode ) == Keypad.KEY_ESCAPE)) {
			close();
			handled = true;
		}
		return handled;
	}

	//#if polish.JavaPlatform < BlackBerry/4.5
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.blackberry.ui.BaseScreen#paint(de.enough.polish.blackberry.ui.Graphics)
	 */
	protected void paint(Graphics g) {
		// ignore
	}
	//#endif
	
}
