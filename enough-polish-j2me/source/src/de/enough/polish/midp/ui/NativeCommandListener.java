//#condition polish.usePolishGui && polish.midp
/*
 * Created on Aug 12, 2008 at 12:56:52 AM.
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
package de.enough.polish.midp.ui;

/**
 * <p>Wraps a J2ME Polish CommandListener into a native CommandListener</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class NativeCommandListener implements javax.microedition.lcdui.CommandListener
{

	de.enough.polish.ui.CommandListener listener;
	de.enough.polish.ui.Displayable displayable;
	
	public NativeCommandListener( de.enough.polish.ui.CommandListener listener, de.enough.polish.ui.Displayable displayable ) {
		this.listener = listener;
		this.displayable = displayable;
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(javax.microedition.lcdui.Command cmd, javax.microedition.lcdui.Displayable dis)
	{
		
		if ( cmd instanceof de.enough.polish.ui.Command) {
			this.listener.commandAction((de.enough.polish.ui.Command)cmd, this.displayable);
		}
	}

}
