/*
 * Created on Jul 14, 2010 at 10:34:52 PM.
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

/**
 * <p>Consumes updated AtomEntries of an AtomFeed</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see AtomFeed#update(AtomUpdateConsumer, String)
 * @see AtomFeed#updateInBackground(AtomUpdateConsumer, String)
 */
public interface AtomUpdateConsumer {

	/**
	 * Is called when a new AtomEntry has been read
	 * @param feed the parent feed of the entry
	 * @param entry the read entry
	 */
	void onUpdated( AtomFeed feed, AtomEntry entry );
	
	/**
	 * Is called when the update of the feed has been finished
	 * @param feed the updated feed
	 */
	void onUpdateFinished(AtomFeed feed);
	
	/**
	 * Informs the consumer about an error that occurred during the update
	 * @param feed the updated feed
	 * @param exception the exception that occurred
	 */
	void onUpdateError( AtomFeed feed, Throwable exception );
}
