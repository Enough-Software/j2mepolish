/*
 * Created on Jun 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.enough.webprocessor;


class IndexEntry {
	public String name;
	public String id;
	public int level;
	public String style;
	private IndexEntry parent;
	public String fullName;
	public String path;
	
	public IndexEntry(String name, String id, int level, String style, String path ) {
		this.name = name;
		this.id = id;
		this.level = level;
		this.style = style;
		this.path = path;
	}
	
	public void setParent( IndexEntry parent ) {
		this.parent = parent;
		IndexEntry p = parent;
		StringBuffer buffer = new StringBuffer();
		while (p != null) {
			buffer.insert( 0, p.name + ": ");
			p = p.parent;
		}
		buffer.append( this.name );
		this.fullName = buffer.toString();
	}
}