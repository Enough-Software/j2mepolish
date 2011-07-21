/*
 * Created on 26-Jan-2004 at 08:26:38.
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

import de.enough.polish.util.CastUtil;

import org.apache.tools.ant.Task;

/**
 * <p>A task which execution can be triggered and stopped with if and unless parameters.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        26-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ConditionalTask extends Task {

	private boolean isActive = true;

	/**
	 * Creates a new conditional task
	 */
	public ConditionalTask() {
		super();
	}
	
	/**
	 * Sets the ant-property which needs to be defined to allow the execution of this task.
	 *  
	 * @param ifExpr the ant-property which needs to be defined 
	 */
	public void setIf(String ifExpr) {
		this.isActive = this.isActive &&
			CastUtil.getBoolean(getProject().getProperty(ifExpr));
	}
	
	/**
	 * Sets the ant-property which must not be defined to allow the execution of this task.
	 * 
	 * @param unlessExpr the ant-property which must not be defined 
	 */
	public void setUnless(String unlessExpr) {
		this.isActive = this.isActive &&
			! CastUtil.getBoolean(getProject().getProperty(unlessExpr));
	}

	/**
	 * Checks if this task should be executed.
	 * Subclasses should call isActive() to determine whether this task 
	 * should be executed.
	 * 
	 * @return true when this task should be executed.
	 */
	public boolean isActive() {
		return this.isActive;
	}
	

}
