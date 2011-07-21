//#condition polish.usePolishGui
/*
 * Created on Mar 22, 2009 at 5:36:47 PM.
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
package de.enough.polish.ui.itemtransitions;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.ItemTransition;
import de.enough.polish.ui.Style;

/**
 * <p> Positions the old and new item next to each other</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PositionItemTransition extends ItemTransition
{
	protected int currentX;
	protected int currentY;
	protected boolean isMoveBothItems;
	

	/**
	 * 
	 */
	public PositionItemTransition()
	{
		// initialize within setStyle();
	}
	
	
	/* @see ItemTransition.animate(long,ClippingRegion) */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		// TODO Auto-generated method stub
		super.animate(currentTime, repaintRegion);
	}



	/* @see ItemTransition.setStyle(Style) */
	public void setStyle(Style style) {
		// TODO Auto-generated method stub
		super.setStyle(style);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemTransition#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int leftBorder, int rightBorder, Graphics g)
	{
		this.oldItem.paint( this.oldX + this.currentX, this.oldY + this.currentY, this.oldX + this.currentX, this.oldX + this.currentX + this.oldWidth, g);

	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemTransition#isFinished()
	 */
	public boolean isFinished()
	{
		// TODO robertvirkus implement isFinished
		return false;
	}

}
