/*
 * Created on 25-Apr-2005 at 13:05:48.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.propertyfunctions;

import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;

/**
 * <p>Can be used to change property values, e.g. ${ uppercase( polish.Vendor ) }.</p>
 * <p>
 * You can register your own functions in ${polish.home}/custom-extensions.xml
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        25-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class PropertyFunction extends Extension {

	/**
	 * Creates a new property function.
	 */
	public PropertyFunction() {
		super();
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment env)
	throws BuildException
	{
		// ignore...

	}
	/**
	 * Processes the given input.
	 * 
	 * @param input the input
	 * @param arguments any additional arguments, null if none are given
	 * @param env the environment settings
	 * @return the processed input
	 */
	public abstract String process( String input, String[] arguments, Environment env );
	
	/**
	 * A property function can work on the values of properties or on the given input directly.
	 * This method allows to specify whether the property needs to be defined (that is the
	 * current target device has a defined value for the given property name) or whether this
	 * function can work on normal/static values as well.
	 * 
	 * The default implementation returns false. Subclasses may override this.
	 * 
	 * @return false by default, that is the property function can operate on normal values as well. 
	 */
	public boolean needsDefinedPropertyValue() {
		return false;
	}

}
