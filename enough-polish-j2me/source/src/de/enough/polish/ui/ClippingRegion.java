/*
 * Created on Aug 18, 2007 at 11:42:58 PM.
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
package de.enough.polish.ui;

/**
 * <p>Manages a region that is increased when further regions are added.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Aug 18, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ClippingRegion {
	private int leftX = Integer.MAX_VALUE;
	private int topY = Integer.MAX_VALUE;
	private int rightX = Integer.MIN_VALUE;
	private int bottomY = Integer.MIN_VALUE;
	private boolean containsRegion;
	
	/**
	 * Resets this clipping region.
	 */
	public void reset() {
		this.leftX = Integer.MAX_VALUE;
		this.topY = Integer.MAX_VALUE;
		this.rightX = Integer.MIN_VALUE;
		this.bottomY = Integer.MIN_VALUE;	
		this.containsRegion = false;
	}
	
	/**
	 * Adds a clipping region.
	 * 
	 * @param x horizontal start
	 * @param y vertical start
	 * @param width width of the region
	 * @param height height of the region
	 */
	public void addRegion( int x, int y, int width, int height ) {
		if (x < this.leftX) {
			this.leftX = x;
		}
		if (y < this.topY) {
			this.topY = y;
		}
		if (x + width > this.rightX) {
			this.rightX = x + width;
		}
		if (y + height > this.bottomY) {
			this.bottomY = y + height;
		}
		this.containsRegion = true;
	}
	
	/**
	 * Determines whether a region has been added since the last reset.
	 * @return true when a region has been added since the last reset.
	 */
	public boolean containsRegion() {
		return this.containsRegion;
	}
	
	/**
	 * Retrieves the horizontal start of this region
	 * @return the left x coordinate
	 */
	public int getX() {
		if (this.leftX == Integer.MAX_VALUE) {
			return 0;
		}
		return this.leftX;
	}
	
	/**
	 * Retrieves the vertical start of this region
	 * @return the top y coordinate
	 */
	public int getY() {
		if (this.topY == Integer.MAX_VALUE) {
			return 0;
		}
		return this.topY;
	}
	
	/**
	 * Retrieves the width of this region.
	 * @return the width, 0 when no region has been added
	 */
	public int getWidth() {
		if (this.rightX == Integer.MIN_VALUE) {
			return 0;
		}
		return this.rightX - this.leftX;
	}

	/**
	 * Retrieves the height of this region.
	 * @return the height, 0 when no region has been added
	 */
	public int getHeight() {
		if (this.bottomY == Integer.MIN_VALUE) {
			return 0;
		}
		return this.bottomY - this.topY;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ClipReg.( ")
		.append( this.leftX ).append( ", ")
		.append( this.topY ).append( ", ")
		.append( this.rightX ).append( ", ")
		.append( this.bottomY )
		.append( " ); containsRegion=").append(this.containsRegion).append("] ")
		.append( super.toString() );
		return buffer.toString();
	}

}
