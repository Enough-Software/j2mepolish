//#condition polish.usePolishGui && polish.api.j2mepolish
/*
 * Created on 27-Aug-2006
 *
 * Copyright (c) 2004-2006 Robert Virkus / Enough Software
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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;

import javax.microedition.lcdui.*;

import de.enough.polish.ui.tasks.ImageTask;
import de.enough.polish.util.Locale;

/**
 * <p>Manages all defined styles of a specific project - THIS IS AN INTERNAL CLASS AND NOT MEANT FOR USAGE.</p>
 * <p>This class is actually pre-processed to get the styles specific for the project and the device.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        27-Aug-2006 - rob creation
 * </pre>
 */
public final class StyleCache {
	
	
	//#ifdef polish.LibraryBuild
		public static Style defaultStyle = null;
		public static Style focusedStyle = null;
		public static Style labelStyle = null; 
		public static Style menuStyle = null;
		private static Hashtable stylesByName = new Hashtable();
	//#endif
	
	// do not change the following line!
//$$IncludeStyleSheetDefinitionHere$$//
		
	
	
	
//#ifdef polish.StyleCache.additionalMethods:defined
	//#include ${polish.StyleCache.additionalMethods}
//#endif

	static {
		StyleSheet.focusedStyle = focusedStyle;
		StyleSheet.labelStyle = labelStyle;
	}

}
