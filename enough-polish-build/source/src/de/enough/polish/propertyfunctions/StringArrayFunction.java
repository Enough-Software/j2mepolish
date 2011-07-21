/*
 * Created on Oct 25, 2007 at 10:49:55 PM.
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
package de.enough.polish.propertyfunctions;

import de.enough.polish.Environment;
import de.enough.polish.util.StringUtil;

/**
 * <p>Splits up a String into a stringarray</p>
 * <p>Example 1:
 * <pre>
 *   ${ stringarray(abcd) }
 * </pre>
 * </p>
 * <p>gives:
 * <pre>new String[]{"a","b","c","d");</pre>
 * </p>
 * <p>Example 2:
 * <pre>
 *   ${ stringarray(ab.cd, .) }
 * </pre>
 * </p>
 * <p>gives:
 * <pre>new String[]{"ab","cd");</pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Oct 25, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StringArrayFunction extends PropertyFunction
{

	private EscapeFunction escape;

	/**
	 * creates a new function
	 */
	public StringArrayFunction()
	{
		super();
		this.escape = new EscapeFunction();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments, Environment env)
	{
		if (input.trim().startsWith("new String[]")) {
			return input;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append( "new String[] {");
		if (arguments != null && arguments.length > 0 && arguments[0].length() == 1) {
			char separator = arguments[0].charAt(0);
			String[] results = StringUtil.split(input, separator);
			for (int i = 0; i < results.length; i++)
			{
				String result = results[i];
				buffer.append('"').append( this.escape.process(result, new String[0], env) ).append('"');
				if (i != results.length -1) {
					buffer.append(", ");
				}
			}
		} else {
			char[] chars = input.toCharArray();
			for (int i = 0; i < chars.length; i++)
			{
				char c = chars[i];
				buffer.append('"').append(this.escape.process(Character.toString(c), new String[0], env) ).append('"');
				if (i != chars.length -1) {
					buffer.append(", ");
				}
			}
		}
		buffer.append('}');
		return buffer.toString();
	}

}
