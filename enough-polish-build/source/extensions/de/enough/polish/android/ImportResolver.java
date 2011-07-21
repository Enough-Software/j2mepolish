/*
 * Created on 22-March-2009
 * 
 * Copyright (c) 2009 Robert Virkus / Enough Software
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
package de.enough.polish.android;

import java.util.Locale;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.libraryprocessor.ImportConversionMap;
import de.enough.polish.libraryprocessor.ImportLibraryProcessor;

/**
 * <p>Maps imports from javax.microedition.lcdui.* to de.enough.polish.android.lcdui.*</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-March-2009 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ImportResolver extends ImportLibraryProcessor
{
  

	/* (non-Javadoc)
	 * @see de.enough.polish.libraryprocessor.ImportLibraryProcessor#addImportConversions(de.enough.polish.libraryprocessor.ImportConversionMap, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	protected void addImportConversions(ImportConversionMap conversions,
			Device device, Locale locale, Environment env)
	{
		conversions.addConversion( "javax.microedition.lcdui.Graphics", "de.enough.polish.android.lcdui.Graphics" );
		conversions.addConversion( "javax.microedition.lcdui.Font", "de.enough.polish.android.lcdui.Font" );
		conversions.addConversion( "javax.microedition.lcdui.Image", "de.enough.polish.android.lcdui.Image" );
		conversions.addConversion( "javax.microedition.lcdui.AlertType", "de.enough.polish.android.lcdui.AlertType" );
		conversions.addConversion( "javax.microedition.lcdui.Graphics", "de.enough.polish.android.lcdui.Graphics" );
		conversions.addConversion( "javax.microedition.lcdui.Font", "de.enough.polish.android.lcdui.Font" );
		conversions.addConversion( "javax.microedition.lcdui.Image", "de.enough.polish.android.lcdui.Image" );
		conversions.addConversion( "javax.microedition.lcdui.AlertType", "de.enough.polish.android.lcdui.AlertType" );
		
		conversions.addConversion( "javax.microedition.midlet.MIDlet", "de.enough.polish.android.midlet.MIDlet" );
		conversions.addConversion( "javax.microedition.midlet.MIDletStateChangeException", "de.enough.polish.android.midlet.MIDletStateChangeException" );
	}

}
