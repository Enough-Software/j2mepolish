/*
 * Created on 06-Oct-2005 at 18:04:27.
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
package de.enough.polish.ant.build;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.enough.polish.ant.Setting;

/**
 * <p>Can be used to exclude files for certain devices or device groups.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        06-Oct-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceFilterSetting extends Setting {
	
	private Pattern[] excludePatterns;

	public ResourceFilterSetting() {
		super();
	}
	
	public void setExcludes( String filter ) {
		Pattern pattern = null;
		try {
			pattern = Pattern.compile(filter);
		} catch ( PatternSyntaxException e ) {
			StringBuffer regexBuffer = new StringBuffer();
			char[] chars = filter.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (c == '*' ) {
					regexBuffer.append(".*");
				} else if ( c == '.' ) {
					regexBuffer.append("\\.");
				} else {
					regexBuffer.append( c );
				}
			}
			pattern = Pattern.compile( regexBuffer.toString() );
		}
		this.excludePatterns = new Pattern[]{ 
				pattern 
		};
	}
	
	public Pattern[] getExcludePatterns() {
		return this.excludePatterns;
	}

}
