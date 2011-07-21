/*
 * Created on 21-Jan-2003 at 15:44:47.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

/**
 * <p>Specifies a log level for a specific class or package.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        21-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class LogFilterSetting {
	String pattern;
	String level;

	/**
	 * Creates a new log filter
	 */
	public LogFilterSetting() {
		super();
		// TODO enough implement Filter
	}

	/**
	 * @return Returns the level.
	 */
	public String getLevel() {
		return this.level;
	}

	/**
	 * @param level The level to set.
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return Returns the pattern.
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 * @param pattern The pattern to set.
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public void setClass( String className ) {
		setPattern( className );
	}
	
	public void setPackage( String packageName ) {
		setPattern( packageName + ".*" );
	}

}
