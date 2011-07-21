//#condition polish.usePolishGui
/*
 * Created on Sep 16, 2007 at 3:09:31 PM.
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
package de.enough.polish.ui.itemviews;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Splits the affected item into single pixels that quickly expand.</p>
 * <p>usage (e.g. for visualizing a pressed state):
 * <pre>
 * .myItem {
 * 	font-color: blue;
 * }
 * .myItem:hover {
 * 	font-color: red;
 * }
 * .myItem:pressed {
 * 	view-type: particle;
 * }
 * </pre>
 * </p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Sep 16, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExplodingParticlesItemView extends ItemView {

	private int scaleFactor = 260;
	private int steps = 10;
	private int currentStep;
	private int[] originalRgb;
	private int[] scaledRgb;
	private boolean isDirectionUp;
	private int paintWidth;
	private int paintHeight;

	/**
	 * Creates a new view
	 */
	public ExplodingParticlesItemView() {
		// nothing to initialize
	}
	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		initContentByParent(parent, firstLineWidth, availWidth, availHeight);
		//#if polish.midp2
			int width = (this.contentWidth * this.scaleFactor) / 100;
			int height = (this.contentHeight * this.scaleFactor) / 100;
			int[] rgbData = new int[ width * height];
			int x = (width - this.contentWidth) >> 1;
			int y = (height - this.contentHeight) >> 1;
			UiAccess.getRgbDataOfContent(parent, rgbData, x, y, width );
			int[] target = new int[ rgbData.length ];
			System.arraycopy( rgbData, 0, target, 0,  rgbData.length );
			this.paintWidth = width;
			this.paintHeight = height;
			this.isDirectionUp = true;
			this.originalRgb = rgbData;
			this.scaledRgb = target;
		//#endif
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		if (this.scaledRgb == null) {
			return;
		}
		super.animate(currentTime, repaintRegion);
		int step = this.currentStep;
		if (this.isDirectionUp) {
			step++;
			if (step >= this.steps) {
				this.isDirectionUp = false;
			}
		} else {
			step--;
			if (step <= 0) {
				step = 0;
				this.isDirectionUp = true;
			}
		}
		this.currentStep = step;
		int factor = 100 + (this.scaleFactor - 100) * (step * step) / ( (this.steps - 1) * (this.steps - 1));
		ImageUtil.particleScale(factor, this.paintWidth, this.paintHeight, this.originalRgb, this.scaledRgb);
		Item item = this.parentItem;
		repaintRegion.addRegion( 
				item.getAbsoluteX() - ((this.paintWidth - item.itemWidth) >> 1), 
				item.getAbsoluteY() - ((this.paintHeight - item.itemHeight) >> 1), 
				this.paintWidth,
				this.paintHeight 
		);

	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		if (this.scaledRgb == null) {
			super.paintContentByParent(parent, x, y, leftBorder, rightBorder, g);
			return;
		}
		//#if polish.midp2
			DrawUtil.drawRgb(this.scaledRgb, 
				x - ((this.paintWidth - this.contentWidth)>>1), 
				y - ((this.paintHeight - this.contentHeight)>>1), 
				this.paintWidth, this.paintHeight, true, g );
		//#endif
	}

}
