/*
 * Created on 23-Jan-2003 at 08:19:23.
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
package de.enough.polish.ant.info;


import de.enough.polish.BuildException;

import java.util.ArrayList;

/**
 * <p>A collection of authors.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Authors {
	
	private ArrayList authors;

	/**
	 * Creates a new authors collection.
	 */
	public Authors() {
		this.authors = new ArrayList();
	}
	
	public void addConfiguredAuthor( Author author ) {
		if (author.getName() == null) {
			throw new BuildException("The element [author] needs to have the attribute [name] defined.");
		}
		this.authors.add( author );
	}
	
	public Author[] getAuthors() {
		return (Author[]) this.authors.toArray( new Author[ this.authors.size() ] );
	}

}
