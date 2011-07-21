//#condition polish.usePolishGui && polish.midp2
/*
 * Created on Apr 25, 2007 at 11:45:01 PM.
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

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.ImageUtil;

/**
 * <p>Increases the size of the item.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Apr 25, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SizeIncreaseItemView extends ItemView {
	
	private int[] rgbData;
	private int[] scaledRgbData;
	
	private int targetHeight;
	private int currentHeight;
	
	private int 	sizeIncreaseAmount 			= 20;
	private boolean sizeIncreaseTop				= true;
	private boolean isInitialized;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		if (this.parentItem == parent && this.parentItem.getStyle() == parent.getStyle() && this.isInitialized) {
			return;
		}
		
		initContentByParent(parent, firstLineWidth, availWidth, availHeight);
		
		this.currentHeight 	= 1;
		this.targetHeight 	= this.contentHeight;
		
		int[] itemRgbData = UiAccess.getRgbDataOfContent( parent );
		this.rgbData = itemRgbData;
		initAnimation(this.rgbData, this.currentHeight);
		AnimationThread.addAnimationItem(parent);
		this.isInitialized = true;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate()
	 */
	public boolean animate() {
		int height = this.currentHeight + getAmount();
				
		if (height >= this.targetHeight) {
			height = this.targetHeight;
			AnimationThread.removeAnimationItem(this.parentItem);
			/*if (transparency == 255) {
				removeViewFromParent();
				//#if polish.css.fade-in-next-style
					if (this.nextStyle != null) {
						this.parentItem.setStyle(this.nextStyle);
					}
				//#endif
				this.rgbData = null;
				this.currentHeight = height;
				return true;
			}*/
		}
		int[] data = this.rgbData;
		if (data != null) {
			this.scaledRgbData = ImageUtil.scale(255, data, this.contentWidth, height, this.contentWidth, this.targetHeight);
		}
		
		//this.contentHeight = height;
		this.currentHeight = height;
		this.contentHeight = height;
		this.parentItem.requestInit();
		return true;
	}
	
	/*
	 * Calculates the current increase of the displayed height
	 * @returns the calculated increase
	 */
	protected int getAmount()
	{
		int remaining = this.targetHeight - this.currentHeight;
		
		int result = remaining / (100 / this.sizeIncreaseAmount);
		
		if(result < 1)
			result = 1;
		
		return result; 		
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.sizeincreaseview-amount
			Integer sizeIncreaseAmountInt = style.getIntProperty("sizeincreaseview-amount");
			if (sizeIncreaseAmountInt != null) {
				this.sizeIncreaseAmount = sizeIncreaseAmountInt.intValue();
			}
		//#endif
		//#if polish.css.sizeincreaseview-top
			Boolean sizeIncreaseTopBool = style.getBooleanProperty("sizeincreaseview-top");
			if (sizeIncreaseTopBool != null) {
				this.sizeIncreaseTop = sizeIncreaseTopBool.booleanValue();
			}
		//#endif
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#showNotify()
	 */
	public void showNotify() {
		initAnimation(this.rgbData, this.currentHeight);
		super.showNotify();
	}
	
	private void initAnimation(int[] data, int height) { 
		if (data != null) {
			this.scaledRgbData = ImageUtil.scale(255, data, this.contentWidth, height, this.contentWidth, this.targetHeight);
		}
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		int[] data = this.scaledRgbData;
		if (this.currentHeight == 0) {
			// do not paint anything
		} else if (this.currentHeight != this.targetHeight && data != null) {
			if(this.sizeIncreaseTop)
			{
				DrawUtil.drawRgb( data, x, y, this.contentWidth, this.contentHeight, true, g );
			}
			else
			{
				DrawUtil.drawRgb( data, x, y - (this.currentHeight - this.targetHeight), this.contentWidth, this.contentHeight, true, g );
			}
		} else {
			super.paintContentByParent(parent, x, y, leftBorder, rightBorder, g);
		}
	}

}
