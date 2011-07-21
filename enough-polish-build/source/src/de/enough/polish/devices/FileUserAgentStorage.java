/*
 * Created on Jun 5, 2008 at 10:08:29 AM.
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
package de.enough.polish.devices;

import java.io.File;
import java.io.IOException;

import de.enough.polish.Device;
import de.enough.polish.util.FileUtil;

/**
 * <p>Stores new found user agents to the filesystem</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FileUserAgentStorage implements UserAgentStorage
{

	private final File resolvedUserAgentsFile;
	private final File unresolvedUserAgentsFile;

	/** 
	 * Creates a new storage for "./useragents.txt" and "./useragents_unresolved.txt"
	 */
	public FileUserAgentStorage()
	{
		this( new File("./useragents.txt"), new File("./useragents_unresolved.txt")  );
	}

	/**
	 * Creates a new storage
	 * @param resolvedUserAgentsFile the file into which user agents should be written
	 * @param unresolvedUserAgentsFile the file into which unresolvable user agents are written
	 */
	public FileUserAgentStorage(File resolvedUserAgentsFile, File unresolvedUserAgentsFile)
	{
		this.resolvedUserAgentsFile = resolvedUserAgentsFile;
		this.unresolvedUserAgentsFile = unresolvedUserAgentsFile;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.devices.UserAgentStorage#notifyDeviceResolved(java.lang.String, de.enough.polish.Device)
	 */
	public void notifyDeviceResolved(String userAgent, Device device)
	{
		try
		{
			FileUtil.addLine(this.resolvedUserAgentsFile, device.getIdentifier() + ": " + userAgent );
		} catch (IOException e)
		{
			System.err.println("Unable to store user agent: " + e.toString() );
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.devices.UserAgentStorage#notifyDeviceUnresolved(java.lang.String)
	 */
	public void notifyDeviceUnresolved(String userAgent)
	{
		try
		{
			FileUtil.addLine(this.unresolvedUserAgentsFile, userAgent );
		} catch (IOException e)
		{
			System.err.println("Unable to store user agent: " + e.toString() );
			e.printStackTrace();
		}	}

}
