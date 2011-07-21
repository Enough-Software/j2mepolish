//#condition polish.usePolishGui
/*
 * Created on June 21, 2007 at 10:48:13 AM.
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
package de.enough.polish.ui.containerviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;

/**
 * <p>Let items spring into view from under the currently focused element.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        June 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SpringContainerView extends ContainerView {

	private int minSpeed = 2;
	private int maxSpeed = -1;
	private int[] targetYPositions;
	
	/**
	 * Creates a new fish eye view
	 */
	public SpringContainerView() {
		//this.focusedLabel = new StringItem(null, null);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		if (this.targetYPositions != null) {
			Item[] myItems = this.parentContainer.getItems();
			for (int i = 0; i < this.targetYPositions.length; i++) {
				int target = this.targetYPositions[i];
				Item item = myItems[i];
				int current = item.relativeY;
				//System.out.println(i + ": current=" + current + ", target=" + target);
				if (current != target) {
					animated = true;
					this.targetYPositions[i] = calculateCurrent( target, current );
				}
			}
		}
		return animated;
	}
	
	
	private int calculateCurrent(int current, int target) {
		int speed = Math.max( this.minSpeed, Math.abs( current - target ) / 3 );
		if (this.maxSpeed != -1 && speed > this.maxSpeed) {
			speed = this.maxSpeed;
		}
		if (current < target ) {
			current += speed;
			if (current > target) {
				current = target;
			}
		} else {
			current -= speed;
			if (current < target) {
				current = target;
			}
		}
		return current;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Item parentContainerItem, int firstLineWidth, int availWidth, int availHeight) {
		super.initContent(parentContainerItem, firstLineWidth, availWidth, availHeight);
		//#debug
		System.out.println("initContent of " + this + ": restartAnimation=" + this.restartAnimation + ", focusedItem=" + this.focusedItem + ", items.length=" + this.parentContainer.getItems().length);
		// new RuntimeException().printStackTrace();
		if (this.restartAnimation && this.focusedItem != null) {
			setTargets( this.focusedItem.relativeX, this.focusedItem.relativeY, this.parentContainer.getItems() );
		}
	}
	
	



	/**
	 * @param relativeX
	 * @param relativeY
	 * @param myItems
	 */
	private void setTargets(int startX, int startY, Item[] myItems) {
		//#debug
		System.out.println("++++ContainerView: setting targets for " + this );
		int[] targetY;
		if (this.targetYPositions == null || this.targetYPositions.length != myItems.length ) {
			targetY = new int[ myItems.length ];
		} else {
			targetY = this.targetYPositions;
		}
		
		for (int i = 0; i < targetY.length; i++) {
			//Item item = myItems[i];
			targetY[i] = startY; //item.relativeY;
			//item.relativeY = startY;
		}
		this.targetYPositions = targetY;
		this.restartAnimation = false;
	}

	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintContent(de.enough.polish.ui.Container, de.enough.polish.ui.Item[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Container container, Item[] myItems, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		if (this.restartAnimation && this.focusedItem != null) {
			setTargets( this.focusedItem.relativeX, this.focusedItem.relativeY, this.parentContainer.getItems() );
		}
		super.paintContent(container, myItems, x, y, leftBorder, rightBorder, clipX,
				clipY, clipWidth, clipHeight, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#paintItem(de.enough.polish.ui.Item, int, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintItem(Item item, int index, int x, int y, int leftBorder, int rightBorder, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		if (this.targetYPositions != null) {
			y = this.targetYPositions[index] + y - item.relativeY;
		}
		super.paintItem(item, index, x, y, leftBorder, rightBorder, clipX, clipY,
				clipWidth, clipHeight, g);
	}
	
	

	
}
