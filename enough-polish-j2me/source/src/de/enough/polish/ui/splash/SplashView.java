//#condition polish.midp || polish.usePolishGui

/*
 * Created on 23-Mar-2005 at 21:53:09.
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
package de.enough.polish.ui.splash;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Paints a custom view for the IntializerSplashScreen.
 * This interface can only be used during the development phase,
 * since it is not actually included in the application.
 * What you need to do is to define the preprocessing variable
 * "polish.classes.SplashView". Your SplashView implementation needs to look like this:
 * public class MySplashView
 * //#if false
 *     implements SplashView
 * //#endif  
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        23-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface SplashView {
	
	void paint( int width, int height, boolean isInitialized, Graphics g );

}
