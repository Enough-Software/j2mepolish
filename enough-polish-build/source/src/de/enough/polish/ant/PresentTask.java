/*
 * Created on Jan 10, 2007 at 7:04:31 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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

import java.io.File;

import org.apache.tools.ant.BuildException;


/**
 * <p>Checks if a file or property is present and stops the build if it is not present.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 10, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PresentTask extends ConditionalTask {
	
	private File file;
	private String property;
	private boolean fail = true;
	private String failMessage;
	/**
	 * @return the fail
	 */
	public boolean isFail() {
		return this.fail;
	}
	/**
	 * @param fail the fail to set
	 */
	public void setFail(boolean fail) {
		this.fail = fail;
	}
	/**
	 * @return the failMessage
	 */
	public String getFailMessage() {
		return this.failMessage;
	}
	/**
	 * @param failMessage the failMessage to set
	 */
	public void setFailMessage(String failMessage) {
		this.failMessage = failMessage;
	}
	/**
	 * @return the file
	 */
	public File getFile() {
		return this.file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	/**
	 * @return the property
	 */
	public String getProperty() {
		return this.property;
	}
	/**
	 * @param property the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		if (this.file != null && !this.file.exists()) {
			String message = this.failMessage;
			if (message == null) {
				message = "File " + this.file.getAbsolutePath() + " not found!";
			}
			throw new BuildException( message );
		}
		if (this.property != null && this.getProject().getProperty(this.property) == null) {
			String message = this.failMessage;
			if (message == null) {
				message = "Property " + this.property + " is not defined!";
			}
			throw new BuildException( message );
		}
	}
	
	
	

}
