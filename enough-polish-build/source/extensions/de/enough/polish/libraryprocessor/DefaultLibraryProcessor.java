//#condition polish.usePolishGui
/*
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

package de.enough.polish.libraryprocessor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.preprocess.custom.ImportPreprocessor;

/**
 * Processes import conversions defined in extensions.xml and custom-extensions.xml
 * 
 * @author Robert Virkus
 */
public class DefaultLibraryProcessor extends ImportLibraryProcessor {
	
	private static final HashMap CLASS_TO_INTERFACE_MAP = new HashMap();
	static {
		CLASS_TO_INTERFACE_MAP.put("de.enough.polish.ui.Displayable", Boolean.TRUE);
	}

	/**
	 * Creates a new default library processor
	 */
	public DefaultLibraryProcessor() {
		// no configuraiton needed, as this is derived by the environment during runtime
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.libraryprocessor.ImportLibraryProcessor#addImportConversions(de.enough.polish.libraryprocessor.ImportConversionMap, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	protected void addImportConversions(ImportConversionMap conversions,
			Device device, Locale locale, Environment env)
	{
		Map currentImportMappings = (Map) env.get( ImportPreprocessor.KEY_IMPORT_MAP );
		Object[] keys = currentImportMappings.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String from = (String) keys[i];
			String to = (String) currentImportMappings.get(from);
			int splitIndex = to.indexOf(';');
			if (splitIndex != -1) {
				to = to.substring(0, splitIndex);
			}
			if ( to.charAt(to.length() -1 ) != '*') {
				conversions.addConversion(from, to, CLASS_TO_INTERFACE_MAP.containsKey(to) );
			}
		}
	}


}
