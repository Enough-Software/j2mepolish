/*
 * Created on 22-May-2005 at 18:01:05.
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
package de.enough.polish.preprocess.custom;

import java.io.File;

import de.enough.polish.BuildException;

import de.enough.polish.Variable;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.util.StringList;

/**
 * <p>Serializes styles during preprocessing stage.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataSerializerPreprocessor extends CustomPreprocessor {
	private String file;
	private String regex;

	public DataSerializerPreprocessor() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#processClass(de.enough.polish.util.StringList, java.lang.String)
	 */
	public void processClass(StringList lines, String className) {
		try
		{
			//If required fields are not set, stop build
			if(this.regex == null)
			{
				throw new IllegalArgumentException("regex is not set");
			}
			
			removeStyles();
		}
		catch (Exception e) {
			throw new BuildException(e.getMessage());
		}
	}
	
	private void removeStyles()
	{
		System.out.println("stylesheet : " + this.preprocessor.getStyleSheet());
		Style[] styles = this.preprocessor.getStyleSheet().getAllStyles();
		System.out.println(styles.length);
		for (int i = 0; i < styles.length; i++) {
			System.out.println(styles[i].getStyleName());
			if(styles[i].getStyleName().matches(this.regex))
			{
				
				this.preprocessor.getStyleSheet().removeStyle(i);
				
				i=0;
				styles = this.preprocessor.getStyleSheet().getAllStyles();
			}
		}
	}
	
	/*public de.enough.polish.ui.Style getStyle(Style style)
	{
		de.enough.polish.ui.Style result = null;
		
		
		return result;
	}*/
	
	

	/**
	 * Sets the parameters for this preprocessor.
	 * 
	 * @param properties the parameters
	 * @param baseDir the base directory
	 */
	public void setParameters( Variable[] properties, File baseDir ) {
		this.file 	= getValue("file", properties);
		this.regex 	= getValue("regex", properties);
	}
	
	/**
	 * Returns the value of a variable named <code>name</code> if found, otherwise null  
	 * @param name the name of the variable
	 * @param properties the variables to search
	 * @return the value of the found variable
	 */
	private String getValue(String name, Variable[] properties)
	{
		for (int i = 0; i < properties.length; i++) {
			if(properties[i].getName().equals(name))
			{
				return properties[i].getValue();
			}
		}
		
		return null;
	}

}
