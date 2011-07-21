/*
 * Created on 08-Aug-2004 at 15:20:19.
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

import java.util.ArrayList;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.util.CastUtil;
import de.enough.polish.util.PropertyUtil;

/**
 * <p>Is used for integrating the &lt;java&gt; element into the &lt;buid&gt; task. </p>
 * <p>The included arguments can use J2ME Polish variables in their values.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        08-Aug-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class JavaExtension extends Java {
	
	String ifCondition;
	String unlessCondition;
	ArrayList argumentsList;
	String message;


	/**
	 * Creates a new extension of the Java element.
	 * 
	 * @param project the Ant project to which this Java extension belongs to.
	 */
	public JavaExtension( Project project ) {
		super();
		this.argumentsList = new ArrayList();
		setProject(project);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.taskdefs.Java#createArg()
	 */
	public Argument createArg() {
		Argument argument = super.createArg();
		this.argumentsList.add( argument );
		return argument;
	}
	
	/**
	 * Overrides the Java execution for settings the arguments correctly.
	 * @param device the current device
	 * @param variables the variables for the current device
	 */
	public void execute( Device device, Map variables ){
		if (this.message != null) {
			String msg = PropertyUtil.writeProperties(this.message, variables);
			System.out.println( msg );
		}
		// remove all arguments:
		super.clearArgs();
		Argument[] arguments = (Argument[]) this.argumentsList.toArray( new Argument[ this.argumentsList.size() ]);
		for (int i = 0; i < arguments.length; i++) {
			Argument argument = arguments[i];
			String[] parts = argument.getParts();
			StringBuffer buffer = new StringBuffer();
			for (int j = 0; j < parts.length; j++) {
				String part = parts[j];
				/* does not work anyhow
				if (part.indexOf(' ') != -1) {
					part = '"' + part + '"';
				}
				*/
				buffer.append( part );
				if (j != parts.length - 1) {
					buffer.append(" ");
				}
			}
			String line = PropertyUtil.writeProperties(buffer.toString(), variables);
			Argument newArgument = super.createArg();
			newArgument.setLine(line);
		}
		execute();
	}
	
	/**
	 * @param ifCondition The ifCondition to set.
	 */
	public void setIf(String ifCondition) {
		this.ifCondition = ifCondition;
	}
		
	/**
	 * @param unlessCondition The unlessCondition to set.
	 */
	public void setUnless(String unlessCondition) {
		this.unlessCondition = unlessCondition;
	}
	
	/**
	 * Checks if the conditions for this variable are met.
	 * 
	 * @param evaluator the boolean evaluator with the settings for the current device
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isActive(BooleanEvaluator evaluator) {
		if (this.ifCondition != null) {
			// first check if there is an Ant-attribute:
			String antProperty = getProject().getProperty( this.ifCondition );
			if (antProperty != null) {
				boolean success = CastUtil.getBoolean(antProperty );
				if (!success) {
					return false;
				}
			} else {
				boolean success = evaluator.evaluate( this.ifCondition, "build.xml", 0);
				if (!success) {
					return false;
				}
			}
		}
		if (this.unlessCondition != null) {
			// first check if there is an Ant-attribute:
			String antProperty = getProject().getProperty( this.unlessCondition );
			if (antProperty != null) {
				boolean success = CastUtil.getBoolean(antProperty );
				if (success) {
					return false;
				}
			} else {
				boolean success = evaluator.evaluate( this.unlessCondition, "build.xml", 0);
				if (success) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void setMessage( String message ) {
		this.message = message;
	}

}
