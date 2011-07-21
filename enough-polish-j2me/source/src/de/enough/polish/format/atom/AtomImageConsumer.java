/*
 * Created on Jul 19, 2010 at 5:45:15 PM.
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
 * <p>Informs about images loaded by an atom entry</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see AtomEntry#loadImages(AtomImageConsumer)
 */
public interface AtomImageConsumer {
	
	/**
	 * Notifies about a successful loaded image
	 * @param image the image
	 * @param entry the source AtomEntry
	 */
	void onAtomImageLoaded( AtomImage image, AtomEntry entry );
	
	/**
	 * Notifies about an error that occurred while loading images
	 * @param image the image
	 * @param entry the source AtomEntry
	 * @param exception the exception that occurred
	 */
	void onAtomImageLoadError( AtomImage image, AtomEntry entry, Throwable exception );
	
	/**
	 * Notifies the consumer that all images have been loaded.
	 * @param entry the source AtomEntry
	 */
	void onAtomImageLoadFinished( AtomEntry entry );

}
