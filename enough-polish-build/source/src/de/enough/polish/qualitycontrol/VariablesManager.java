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

package de.enough.polish.qualitycontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.BuildException;
import de.enough.polish.exceptions.InvalidComponentException;

/**
 * Allows to access variables.xml programmatically.
 * 
 * @author Robert Virkus
 *
 */
public class VariablesManager {
	
	private final Map variablesByName;
	
	
	/**
	 * Loads the variables.xml file.
	 * 
	 * @param polishHome the base directory of the J2ME Polish installation
	 * @throws JDOMException when there are syntax errors in variables.xml
	 * @throws IOException when variables.xml could not be read
	 * @throws InvalidComponentException when an variable definition has errors
	 */
	public VariablesManager(File polishHome)
	throws JDOMException, IOException, InvalidComponentException
	{
		this.variablesByName = new HashMap();
		loadVariables( new FileInputStream( new File( polishHome, "variables.xml") ));
	}
	
	/**
	 * Loads the variables.xml file.
	 * 
	 * @param is input stream for reading the variables.xml file
	 * @throws JDOMException when there are syntax errors in variables.xml
	 * @throws IOException when variables.xml could not be read
	 * @throws InvalidComponentException when an variable definition has errors
	 */
	private void loadVariables(InputStream is) 
	throws JDOMException, IOException, InvalidComponentException 
	{
		if (is == null) {
			throw new BuildException("Unable to load bugs.xml, no file found.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			PolishVariable issue = new PolishVariable( definition, this );
			PolishVariable existingIssue = (PolishVariable) this.variablesByName.get( issue.getName() ); 
			if ( existingIssue != null ) {
				throw new InvalidComponentException("The variable [" + issue.getName() 
						+ "] is defined twice. Please remove one in [bugs.xml].");
			}
			
			this.variablesByName.put( issue.getName(), issue );
		}		
	}
	
	public PolishVariable getVariable( String name) {
		PolishVariable var = (PolishVariable) this.variablesByName.get(name);
		if (var == null && !name.startsWith("polish.")) {
			var = (PolishVariable) this.variablesByName.get("polish." + name);
		}
		return var;
	}
	
	public PolishVariable[] getVisibleVariables() {
		ArrayList variables = new ArrayList();
		Object[] values = this.variablesByName.values().toArray();
		for (int i = 0; i < values.length; i++) {
			PolishVariable var = (PolishVariable) values[i];
			if (var.isVisibleAndDefined()) {
				variables.add(var);
			}
		}
		return (PolishVariable[]) variables.toArray( new PolishVariable[ variables.size() ]);
	}
	
	public PolishVariable[] getVisibleVariablesFor( String className) {
		String shortName = null;
		int lastDotIndex = className.lastIndexOf('.');
		if (lastDotIndex != -1) {
			shortName = className.substring(lastDotIndex+1);
		}
		ArrayList variables = new ArrayList();
		Object[] values = this.variablesByName.values().toArray();
		for (int i = 0; i < values.length; i++) {
			PolishVariable var = (PolishVariable) values[i];
			if (var.isVisibleAndDefined() && var.appliesTo(className, shortName)) {
				variables.add(var);
			}
		}
		return (PolishVariable[]) variables.toArray( new PolishVariable[ variables.size() ]);		
	}

	

}
