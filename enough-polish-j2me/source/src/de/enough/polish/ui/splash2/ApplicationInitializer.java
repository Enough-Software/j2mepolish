//#condition polish.usePolishGui
/*
 * Created on 15-Dec-2010 at 09:50:45.
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
package de.enough.polish.ui.splash2;

/**
 * <p>Initializes the actual application.
 * The SplashScreen is launching a background thread that calls the initApp() method.
 * </p>
 *
 * <p>Copyright (c) Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface ApplicationInitializer {
	
	/**
	 * Initializes the application.
	 * This method is called by a background thread of the SplashScreen.
	 * The app should then show the next screen or exit.
	 */
	void initApp();

}
