/*
 * Created on 25-Feb-2004 at 21:40:23.
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

import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Environment;
import de.enough.polish.util.CastUtil;

/**
 * <p>The base class for any nested element which can be conditional.</p>
 * <p>This class supports the attributes [if] and [unless]. 
 *    When the if-attribute is specified, the corresponding property
 *    needs to be defined in ant's build.xml.
 *    When the unless-attribute is defined, the corresponding property
 *    must not be defined in the build.xml.
 *    Classes can check if the conditions of this element are
 *    met by calling isActive().
 *    Nested elements which want to make use of other conditional nested
 *    element needs to have a reference to the ant-project.
 *    This can be done with the help of the create&lt;nested-element-name&gt; method.
 * </p>
 * <p>
 *    The if- and unless-conditions can now also refer to complex terms
 *    and use J2ME Polish variables and symbols.
 *    This has to be supported specifically by the implementation, though -
 *    the implementation needs to call isActive( BooleanEvaluator ). 
 * </p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        25-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ConditionalElement {
	
	private String ifCondition;
	private String unlessCondition;

	/** 
	 * Creates a new conditional element.
	 */
	public ConditionalElement() {
		// initialisation is done via the setter and getter methods.
	}
	
	public ConditionalElement(ConditionalElement parent) {
		this.ifCondition = parent.ifCondition;
		this.unlessCondition = parent.unlessCondition;
	}

	/**
	 * Sets the ant-property which needs to be defined to allow the execution of this task.
	 *  
	 * @param ifExpr the ant-property which needs to be defined 
	 */
	public void setIf(String ifExpr) {
		this.ifCondition = ifExpr;
	}
	
	/**
	 * Sets the ant-property which must not be defined to allow the execution of this task.
	 * 
	 * @param unlessExpr the ant-property which must not be defined 
	 */
	public void setUnless(String unlessExpr) {
		this.unlessCondition = unlessExpr;
	}

	/**
	 * Checks if this element should be used.
	 * 
	 * @param project The project to which this nested element belongs to.
	 * @return true when this element is valid
	 */
	public boolean isActive( Project project ) {
		if (this.unlessCondition != null) {
			if (this.unlessCondition.endsWith(":defined")) {
				String propName = this.unlessCondition.substring( 0, this.unlessCondition.lastIndexOf(':') );
				String propValue = project.getProperty( propName );
				if (propValue != null) {
					return false;
				}
			} else {
				boolean success = CastUtil.getBoolean(project.getProperty(this.unlessCondition));
				if (success) {
					return false;
				}
			}
		}
		if (this.ifCondition != null ) {
			if (this.ifCondition.endsWith(":defined")) {
				String propName = this.ifCondition.substring( 0, this.ifCondition.lastIndexOf(':') );
				String propValue = project.getProperty( propName );
				if (propValue == null) {
					return false;
				}
			} else {
				boolean success = CastUtil.getBoolean(project.getProperty(this.ifCondition));
				if (!success) {
					return false;	
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the conditions for this element are met.
	 * 
	 * @param evaluator the boolean evaluator with the settings for the current device
	 * @param project the Ant project into which this variable is embedded
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isActive(BooleanEvaluator evaluator, Project project) {
		if (this.ifCondition != null) {
			// first check if there is an Ant-attribute:
			String antProperty = project.getProperty( this.ifCondition );
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
			String antProperty = project.getProperty( this.unlessCondition );
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
	/**
	 * Checks if the conditions for this element are met.
	 * 
	 * @param environment the environment settings
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isActive( Environment environment ) {
		return isActive( environment.getBooleanEvaluator() );
	}
	
	/**
	 * Checks if the conditions for this element are met.
	 * 
	 * @param evaluator the boolean evaluator with the settings for the current device
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isActive(BooleanEvaluator evaluator ) {
		if (this.ifCondition != null) {
			boolean success = evaluator.evaluate( this.ifCondition, "build.xml", 0);
			if (!success) {
				return false;
			}
		}
		if (this.unlessCondition != null) {
			boolean success = evaluator.evaluate( this.unlessCondition, "build.xml", 0);
			if (success) {
				return false;
			}
		}
		return true;
	}


	public String getIf() {
		return this.ifCondition;
	}
	
	public String getUnless() {
		return this.unlessCondition;
	}

	public String getCondition() {
		if (this.ifCondition == null) {
			return this.unlessCondition;
		} else if (this.unlessCondition == null) {
			return this.ifCondition;
		} else {
			return "if=" + this.ifCondition + ", unless=" + this.unlessCondition;
		}
	}
	
}
