/*
 * Created on 24-Jan-2004 at 00:21:06.
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
package de.enough.polish.ant.requirements;

import java.util.ArrayList;
import java.util.Map;

import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.ant.ConditionalElement;

/**
 * <p>A list of requirements which each supported device needs to satisfy.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        24-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Requirements
extends AndRelation
{
	
	ConditionalElement condition;
	private String message;
	/**
	 * Creates a new device requirements list.
	 */
	public Requirements() {
		this( null );
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	//TODO rob addConfiguredGroup( OrRelation orRelation )
	// when requirements should be given in groups
	
	
	/**
	 * Cerates a new device requirements list.
	 * @param buildProperties the build properties
	 */
	public Requirements(Map buildProperties) {
		super(buildProperties);
		this.condition = new ConditionalElement();
	}

	/**
	 * Filters the available devices and only returns those which satisfy all requirements.
	 * 
	 * @param availableDevices array of all available devices.
	 * @return All devices which satisfy the requirements.
	 */
	public Device[] filterDevices( Device[] availableDevices ) {
		ArrayList deviceList = new ArrayList();
		for (int i = 0; i < availableDevices.length; i++) {
			Device device = availableDevices[i];
			if (isMet( device )) {
				// all requirements are met by this device:
				deviceList.add( device );
			}
		}
		return (Device[]) deviceList.toArray( new Device[ deviceList.size() ] );
	}

	/**
	 * Sets the ant-property which needs to be defined to allow the execution of this task.
	 *  
	 * @param ifExpr the ant-property which needs to be defined 
	 */
	public void setIf(String ifExpr) {
		this.condition.setIf( ifExpr );
	}
	
	/**
	 * Sets the ant-property which must not be defined to allow the execution of this task.
	 * 
	 * @param unlessExpr the ant-property which must not be defined 
	 */
	public void setUnless(String unlessExpr) {
		this.condition.setUnless( unlessExpr );
	}

	/**
	 * Checks if this element should be used.
	 * 
	 * @param project The project to which this nested element belongs to.
	 * @return true when this element is valid
	 */
	public boolean isActive( Project project ) {
		boolean active = this.condition.isActive( project );
		if (active && this.message != null) {
			System.out.println(this.message);
		}
		return active;
	}

	/**
	 * Checks if this element should be used.
	 * 
	 * @param evaluator the boolean evaluator.
	 * @return true when this element is valid
	 */
	public boolean isActive(BooleanEvaluator evaluator) {
		boolean active = this.condition.isActive( evaluator );
		if (active && this.message != null) {
			System.out.println(this.message);
		}
		return active;
	}

	
}
