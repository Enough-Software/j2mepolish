//#condition polish.usePolishGui
/*
 * Created on 01-May-2005 at 02:18:32.
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
package de.enough.polish.ui;

/**
 * <p>Is used to detect internal changes of Screens, e.g. when the user changes the tab of a TabbedForm.</p>
 * <p>This is a J2ME Polish specific mechanism and not supported by the MIDP standard.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        01-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @since J2ME Polish 1.3
 */
public interface ScreenStateListener {
	
	/**
	 * Called when internal state of an Item has been changed by the user. 
	 * This happens when the user:
	 * <ul>
	 *    <li>Changes the tab of a TabbedForm</li>
	 *    <li>Changes the frame of a FramedForm</li>
	 *    <li>There could be further events later onwards</li>
	 * </ul> 
	 * @param screen the de.enough.polish.ui.Screen that has been changed, please note that you need
	 *        to import specifically the de.enough.polish.ui.Screen class and not the javax.microedition.lcdui.Screen class
	 *        when you want to implement this listener.
	 */
	void screenStateChanged( Screen screen );

}
