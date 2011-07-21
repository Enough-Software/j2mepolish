/*
 * Created on Feb 4, 2009 at 3:02:48 PM.
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
package de.enough.polish.libraryprocessor;

import java.util.HashMap;

/**
 * <p>Stores an import conversion.</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ImportConversionMap extends HashMap
{
	public static class ConversionTarget
	{
		private String value;
		private boolean isInterface;

		public ConversionTarget(String value)
		{
			this.value = value;
		}

		public ConversionTarget(String value, boolean isInterface)
		{
			this.value = value;
			this.isInterface = isInterface;
		}

		public String getValue()
		{
			return this.value;
		}

		public boolean isInterface()
		{
			return this.isInterface;
		}

		public String toString()
		{
			return this.value;
		}
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Adds a conversion
	 * @param from the original name, e.g. javax.microedition.lcdui.Command
	 * @param to the target import name, e.g. de.enough.polish.ui.Command
	 */
	public void addConversion( String from, String to) {
		from = from.replace('.', '/');
		to = to.replace('.', '/');
		put( from, new ConversionTarget(to) );
	}
	
	/**
	 * Adds a conversion
	 * @param from the original name, e.g. javax.microedition.lcdui.Command
	 * @param to the target import name, e.g. de.enough.polish.ui.Command
	 * @param isInterface if the target import name references an interface
	 */
	public void addConversion( String from, String to, boolean isInterface)
	{
		from = from.replace('.', '/');
		to = to.replace('.', '/');
		put( from, new ConversionTarget(to, isInterface) );
	}
}
