/*
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

package de.enough.polish.io.file;

/**
 * Represents  a node (a file or a directory) within the file system.
 * @author Robert Virkus
 *
 */
public class FileSystemNode {
	
	private String path;
	private FileSystemNode parent; 
	
	public FileSystemNode( String path, FileSystemNode parent ) {
		this.path = path;
		this.parent = parent;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public FileSystemNode getParent() {
		return this.parent;
	}
	
	public String getAbsolutePath() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.path);
		FileSystemNode p = this.parent;
		while (p != null) {
			buffer.insert( 0, p.path);
			p = p.parent;
		}
		return buffer.toString();
	}
	
	public String toString() {
		return this.path;
	}

	public boolean isDirectory() {
		return this.path.charAt( this.path.length() - 1) == '/';
	}

}
