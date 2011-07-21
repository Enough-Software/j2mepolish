//#condition polish.usePolishGui
/*
 * Created on 13-Mar-2007 at 16:51:32.
 * 
 * Copyright (c) 2009 Michael / Enough Software
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
 * <p>Is used to detect internal changes of TabbedForms, e.g. when the user changes the tab of a TabbedForm.</p>
 *
 * <p>Copyright (c) 2009 Enough Software</p>
 * <pre>
 * history
 *        12-Mar-2007 - mkoch creation
 * </pre>
 * @author Michael Koch, michael.koch@enough.de
 * @since J2ME Polish 2.0
 */
public interface TabbedFormListener {
	/**
	 * Called when a tab change in a TabbedForm is requested. This method needs to
	 * return <code>true</code> to make tab changes complete. When this method returns
	 * <code>false</code> the tab change is cancelled.
	 * 
	 * @param oldTabIndex the index of the current tab
	 * @param newTabIndex the index of the requested tab
	 * @return <code>true</code> if a tab change is okay, <code>false</code> otherwise
	 */
	boolean notifyTabChangeRequested( int oldTabIndex, int newTabIndex );

	/**
	 * Called when a tab change in a TabbedForm is completed.
	 * 
	 * @param oldTabIndex the index of the previous tab
	 * @param newTabIndex the index of the current tab
	 */
	void notifyTabChangeCompleted( int oldTabIndex, int newTabIndex );
}
