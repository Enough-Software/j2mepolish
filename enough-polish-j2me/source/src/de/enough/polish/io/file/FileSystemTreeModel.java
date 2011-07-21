//#condition polish.api.fileconnection
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

import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import de.enough.polish.ui.TreeModel;
import de.enough.polish.util.ArrayList;

/**
 * Provides the file system of a device as a TreeModel.
 * For working efficiently with a TreeItem, use UiFileSystemTreeModel.
 * 
 * @author Robert Virkus
 * @see de.enough.polish.ui.TreeModel
 * @see de.enough.polish.ui.TreeItem
 * @see UiFileSystemTreeModel
 */
public class FileSystemTreeModel implements TreeModel {

	private FileConnection fileConnection;

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TreeModel#addChildren(java.lang.Object, de.enough.polish.util.ArrayList)
	 */
	public void addChildren(Object parent, ArrayList list) {
		if (parent == this) {
			Enumeration enumeration = FileSystemRegistry.listRoots();
			while (enumeration.hasMoreElements()) {
				String path = (String)enumeration.nextElement();
				list.add( new FileSystemNode( path, null) );
			}
		} else if (!(parent instanceof FileSystemNode)){
			//#debug warn
			System.out.println("Unable to retrieve children for node " + parent);
		} else {
			FileSystemNode node = (FileSystemNode) parent;
			String nodePath = node.getPath();
			boolean establishNewFileConnection = false;
			if (this.fileConnection == null) {
				establishNewFileConnection = true;
			} else {
				String currentPath = this.fileConnection.getPath();
				int pos = nodePath.indexOf(currentPath);
				String newPathName;
				if (pos != -1) {
					// we can re-use the fileconnection easily, just add a directory:
					newPathName = nodePath.substring( pos + currentPath.length() );
				} else {
					// the node is a from a different branch - go all the way up:
					StringBuffer buffer = new StringBuffer();
					for (int i=0; i<currentPath.length(); i++) {
						char c = currentPath.charAt(i);
						if (c == '/') {
							buffer.append("../");
						}
					}
					buffer.append(nodePath);
					newPathName = buffer.toString();
				}
				try {
					this.fileConnection.setFileConnection(newPathName);
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to reset fileconnection to \"" + newPathName + "\"" + e);
					establishNewFileConnection = true;
				}
			}
			if (establishNewFileConnection) {
				if (this.fileConnection != null) {
					try {
						this.fileConnection.close();
					} catch (Exception e) {
						//#debug warn
						System.out.println("Unable to close previous file connection" + e);
					}
				}
				try {
					this.fileConnection = (FileConnection) Connector.open("file:///" + node.getAbsolutePath(), Connector.READ);
					//#debug
					System.out.println("Opened: " + this.fileConnection.getURL());
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to open file connection to \"file:///" + node.getAbsolutePath() + "\"" + e );
					return;
				}
			}
			//System.out.println("Opened "+ this.fileConnection.getURL());
			// file connection is now established - so list the contents:
			try {
				Enumeration enumeration = this.fileConnection.list("*", true);
				while (enumeration.hasMoreElements()) {
					String path = (String) enumeration.nextElement();
					list.add( new FileSystemNode( path, node) );
				}
				if (list.size() == 0) {
					enumeration = this.fileConnection.list();
					while (enumeration.hasMoreElements()) {
						String path = (String) enumeration.nextElement();
						list.add( new FileSystemNode( path, node) );
					}
				}
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to list contents of \"" + this.fileConnection.getPath() + "\"" + e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return this;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		if (node instanceof FileSystemNode) {
			FileSystemNode fsNode = (FileSystemNode) node;
			return !fsNode.isDirectory();
		}
		return true;
	}

}
