/*
 * Created on 16-Jul-2004 at 03:39:27.
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

import de.enough.polish.Environment;
import de.enough.polish.Variable;
import de.enough.polish.ant.build.Variables;

/**
 * <p>A base class for settings which accept parameters.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        16-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Setting extends ConditionalElement {
	
	private Variables variables;

	/**
	 * Creates a new empty parameter setting
	 */
	public Setting() {
		// initialisation is done in the subclass
	}
	
	public Setting(Setting parent) {
		super( parent );
		this.variables = parent.variables;
	}

	/**
	 * Adds a parameter to this setting.
	 * 
	 * @param var the parameter with a [name] and a [value] attribute.
	 */
	public void addConfiguredParameter( Variable var ) {
		if (this.variables == null) {
			this.variables = new Variables();
		}
		this.variables.addConfiguredVariable( var );
		/*
		if (this.parameters == null) {
			this.parameters = new ArrayList();
		}
		if (var.containsMultipleVariables()) {
			String ifCondition = var.getIfCondition();
			String unlessCondition = var.getUnlessCondition();
			String type = var.getType();
			Variable[] variables = var.loadVariables();
			for (int i = 0; i < variables.length; i++) {
				Variable variable = variables[i];
				variable.setIf( ifCondition );
				variable.setUnless(unlessCondition);
				variable.setType(type);
				this.parameters.add( variable );
			}
		} else {
			if (var.getName() == null) {
				throw new BuildException("Invalid parameter: please specify the attribute [name] for each <parameter> element.");
			}
			if (var.getValue() == null) {
				throw new BuildException("Invalid parameter: please specify the attribute [value] for each <parameter> element.");
			}
		}
		this.parameters.add( var );
		*/
	}
	
	/**
	 * Determines whether this setting has any parameters.
	 * 
	 * @return true when there are parameters for this setting.
	 */
	public boolean hasParameters() {
		return this.variables != null;
	}
	
	/**
	 * Retrieves the parameters of this setting.
	 * 
	 * @return an array of variable, can be empty but not null.
	 */
	public Variable[] getParameters() {
		if (this.variables == null) {
			return new Variable[ 0 ];
		} else {
			return this.variables.getVariables();
		}
	}
	
	/**
	 * Retrieves the parameters of this setting, this method resolves any file-parameters as well.
	 * 
	 * @param environment the configruation settings
	 * @return an array of variable, can be empty but not null.
	 */
	public Variable[] getAllParameters( Environment environment ) {
		if (this.variables == null) {
			return new Variable[ 0 ];
		} else {
			return this.variables.getAllVariables( environment );
		}
	}

}
