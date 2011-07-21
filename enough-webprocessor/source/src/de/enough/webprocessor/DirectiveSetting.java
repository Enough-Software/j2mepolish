/*
 * Created on Dec 21, 2007 at 1:53:55 PM.
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
package de.enough.webprocessor;

/**
 * <p>Allows to specify an external directive handler</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Dec 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DirectiveSetting
{
	
	private String className;
	private String classPath;
	private String directive;
	
	public DirectiveSetting() {
		// use setters
	}
	/**
	 * @return the className
	 */
	public String getClassName() {
	return this.className;}
	
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	public void setClass(String className)
	{
		setClassName(className);
	}

	/**
	 * @return the classPath
	 */
	public String getClassPath() {
	return this.classPath;}
	
	/**
	 * @param classPath the classPath to set
	 */
	public void setClassPath(String classPath)
	{
		this.classPath = classPath;
	}
	/**
	 * @return the directive
	 */
	public String getDirective() {
	return this.directive;}
	
	/**
	 * @param directive the directive to set
	 */
	public void setDirective(String directive)
	{
		this.directive = directive;
	}
	
	public void setName( String directive ) {
		setDirective(directive);
	}
	
	

}
