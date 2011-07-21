/*
 * Created on Jul 19, 2010 at 7:23:36 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.format.atom;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.Task;

/**
 * <p>Loads images for a specified AtomEntry</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.util.TaskThread
 */
public class ImageLoadTask 
implements Task
{
	
	private final AtomEntry entry;
	private final AtomImageConsumer consumer;
	private final HashMap requestProperties;

	/**
	 * Creates a new feed entry image loading task
	 * @param entry the entry
	 * @param consumer the update consumer for the image
	 */
	public ImageLoadTask( AtomEntry entry, AtomImageConsumer consumer) {
		this( entry, consumer, null);
	}


	/**
	 * Creates a new feed entry image loading task
	 * @param entry the entry
	 * @param consumer the update consumer for the image
	 * @param requestProperties the request properties to be set for each http request (String name, String value)
	 */
	public ImageLoadTask( AtomEntry entry, AtomImageConsumer consumer, HashMap requestProperties) {
		this.entry = entry;
		this.consumer = consumer;
		this.requestProperties = requestProperties;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Task#execute()
	 */
	public void execute() throws Exception {
		this.entry.loadImages(this.consumer, this.requestProperties);
	}
	
}
