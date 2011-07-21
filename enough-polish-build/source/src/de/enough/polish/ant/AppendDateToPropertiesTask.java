/*
 * Created on Jul 24, 2007 at 3:18:07 PM.
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
package de.enough.polish.ant;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import de.enough.polish.util.FileUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Jul 24, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AppendDateToPropertiesTask extends Task {
	
	private File source;
	private File target;
	/**
	 * @return the source
	 */
	public File getSource() {
		return this.source;
	}
	
	/**
	 * @param source the source to set
	 */
	public void setSource(File source) {
		this.source = source;
	}
	/**
	 * @return the target
	 */
	public File getTarget() {
		if (this.target != null) {
			return this.target;
		}
		return this.source;
	}
	
	/**
	 * @param target the target to set
	 */
	public void setTarget(File target) {
		this.target = target;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		if (this.source == null) {
			throw new BuildException( "no source is set - please specify the properties file using the \"source\" attribute.");
		}
		if (!this.source.exists()) {
			throw new BuildException( "source " + this.source.getAbsolutePath() + " does not exist. Please check your \"source\" attribute.");
		}

		try {
			Map sourceMap = FileUtil.readProperties(getSource());
			DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" ); 
			String date = " (" + dateFormat.format( new Date() ) + ")";
			Object[] keys = sourceMap.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				Object key = keys[i];
				String value = (String) sourceMap.get(key);
				value += date;
				sourceMap.put( key, value);
			}
			FileUtil.writePropertiesFile( getTarget(), sourceMap);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException( e.toString() );
		}
		
	}
	
	

}
