//#condition polish.usePolishGui && polish.midp2

/*
 * Created on 14.09.2005 at 11:56:03.
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

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.ScreenChangeAnimation;

public class CageScreenChangeAnimation extends ScreenChangeAnimation {
	//row = wo sich das neue image befindet am display
	private int currentColumn = 0;
	
	
	public CageScreenChangeAnimation() {
		super();
		this.useLastCanvasRgb = true;
		this.useNextCanvasRgb = true;
	}
	
	protected boolean animate() {
		boolean doSwitchRow = true;
		int column = 0;
		for(int i = 0; i < this.lastCanvasRgb.length;i++){		
			if(doSwitchRow && column <= this.currentColumn){
				this.lastCanvasRgb[i] = this.nextCanvasRgb[i];
			}
			else if(!doSwitchRow && column >= this.screenWidth-this.currentColumn ){
				this.lastCanvasRgb[i] = this.nextCanvasRgb[i];
			}	
			column = (column + 1) % this.screenWidth;
			if(column == 0){
				doSwitchRow = !doSwitchRow;
			}
		}
		this.currentColumn+=4;
		if(this.currentColumn >= this.screenWidth) {
			this.currentColumn = 0;
			return false;
		}
		return true;
	}

	public void paintAnimation(Graphics g) {
		g.drawRGB(this.lastCanvasRgb,0,this.screenWidth,0,0,this.screenWidth,this.screenHeight,false);
	}

}
