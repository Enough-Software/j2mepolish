/*
 * Created on 20-Feb-2004 at 21:15:33.
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
package de.enough.polish.ant;

import java.util.ArrayList;

import de.enough.polish.Environment;
import de.enough.polish.Variable;

/**
 * <p>Represents a Java Application Description file.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        20-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Jad {
	
	
	private ArrayList attributes;
	private Environment environment;

	/**
	 * Creates a new empty JAD
	 * 
	 * @param properties a map of defined variables which can be used in the attribute-values.
	 */
	public Jad(Environment properties ) {
		this.attributes = new ArrayList();
		this.environment = properties;
	}
	
	/**
	 * Adds a single attribute to this Jad file.
	 * 
	 * @param var the variable with a defined name and value which should be added
	 */
	public void addAttribute( Variable var ) {
		addAttribute( var.getName(), var.getValue() );
	}
	
	/**
	 * Adds a single attribute to this Jad file.
	 * 
	 * @param name the name
	 * @param value the value which can contain variables like ${polish.jarName} etc
	 */
	public void addAttribute( String name, String value ) {
		value = this.environment.writeProperties (value );
		this.attributes.add( name + ": " + value );
	}
	
	/**
	 * Gets the whole JAD-file as a String-array.
	 * 
	 * @return all defined attributes and values in a string-array
	 */
	public String[] getContent() {
		String[] lines = (String[]) this.attributes.toArray( new String[ this.attributes.size() ] );
		return lines;
	}

	/**
	 * Adds several variables to this JAD file.
	 * 
	 * @param vars The variables which should be added.
	 */
	public void addAttributes(Variable[] vars) {
		for (int i = 0; i < vars.length; i++) {
			Variable var = vars[i];
			addAttribute( var.getName(), var.getValue() );
		}
	}

	/**
	 * Sets all attributes in one go.
	 * 
	 * @param sortedAttributes the attributes in the correct order. 
	 */
	public void setAttributes(Variable[] sortedAttributes ) {
		this.attributes.clear();
		addAttributes( sortedAttributes );
	}
}
