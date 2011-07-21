//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 16.09.2005 at 13:15:55.
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
package de.enough.polish.ui.screenanimations;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

public class PixelScreenChangeAnimation extends ScreenChangeAnimation {
	private boolean stillRun = true;
	private int row = 25;
	private int width, height;
	public PixelScreenChangeAnimation() {
		this.useNextCanvasRgb = true;
		this.useLastCanvasRgb = true;
	}

	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward  ) 
	{
			this.stillRun = true;
			this.row = 25;
			this.width = width;
			this.height = height;
			super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable, isForward );
	}

	
	
	protected boolean animate() {
		int row = 0;
		for(int i = 0; i < this.lastCanvasRgb.length;i++){		
			if(i == row){	
				this.lastCanvasRgb[i] = this.nextCanvasRgb[i];
				row += this.row;
			}		
		}
		this.row-=1;
		if(this.row == 0)this.stillRun=false;
		return this.stillRun;
	}

	public void paintAnimation(Graphics g) {
		g.drawRGB(this.lastCanvasRgb,0,this.width,0,0,this.width,this.height,false);
	}

}
