/*
 * Created on Dec 20, 2009 at 8:02:13 PM.
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
package de.enough.polish.event;


/**
 * <p>Allows to handle UI events without extending J2ME Polish UI components</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.ui.UiAccess#setUiEventListener(de.enough.polish.ui.Screen, UiEventListener)
 * @see de.enough.polish.ui.UiAccess#setUiEventListener(de.enough.polish.ui.Item, UiEventListener)
 */
public interface UiEventListener {
	/**
	 * Processes a UI event.
	 * When the processing of this ui event should be stopped subsequently, the called method needs to call event.setHandled().
	 * 
	 * @param event the event
	 * @param source the source of the event, typically either a Screen or an Item
	 * @see UiEvent#setHandled()
	 */
	void handleUiEvent( UiEvent event, Object source );
}
