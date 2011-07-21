//#condition polish.usePolishGui
/*
 * Created on Jan 5, 2008 at 7:02:15 PM.
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
package de.enough.polish.ui.borders;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Border;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Sends out square rectangles from around the item to the outside.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SquareSonarBorder extends Border
{
	protected final int innerColor;
	protected final int outerColor;
	protected final int steps;
	protected final int distance;
	protected final int pause;
	protected final int numSignals;
	protected final int speed;
	protected final boolean hasTranslucentColor;
	
	protected final int[] distances;
	protected final int[] colors;
	protected final boolean[] activeSignals;
	protected long lastEmittingTime;
	protected int emittedSignals;
	protected boolean isOutbound = true;
	protected boolean isAnimationStopped;

	/**
	 * Creates a new SquareSonarBorder
	 * 
	 * @param innerColor the color at the inner starting point.
	 * @param outerColor the color at the outer end point.
	 * @param steps the number of animation steps required for moving from the inner point to the outer point 
	 * @param distance the distance from the inner point to the outer point in pixels
	 * @param pause the pause in ms between different sonar signals
	 * @param numSignals the number of signals that are send out. Use -1 for not limiting the number.
	 * @param speed the constant speed by which signals move from the inner to the outer point - use -1 for
	 *        moving fast in the beginning and then slowing down.
	 */
	public SquareSonarBorder( int innerColor, int outerColor, int steps, int distance, int pause, int numSignals, int speed )
	{
		super( 0, 0, 0, 0 );
		this.innerColor = innerColor;
		this.outerColor = outerColor;
		this.steps = steps;
		this.distance = distance;
		this.pause = pause;
		this.numSignals = numSignals;
		this.speed = speed;
		this.hasTranslucentColor = ( ((innerColor >>> 24) != 0) && ((innerColor >>> 24) != 0xff) ) 
								|| ( ((outerColor >>> 24) != 0)  && ((outerColor >>> 24) != 0xff) );
		this.distances = new int[ steps ];
		if (speed != -1) {
			for (int i = 0; i < steps; i++)
			{
				this.distances[i] = i * speed;
			}
		} else {
			int sum = 0;
			int dist = distance;
			for (int i = 0; i < steps; i++)
			{
				int amount = dist / 3;
				sum += amount;
				dist -= amount;
			}
			dist = distance + (distance - sum);
			sum = 0;
			for (int i = 0; i < steps; i++)
			{
				this.distances[i] = sum;
				int amount = dist / 3;
				sum += amount;
				dist -= amount;
			}
		}
		this.activeSignals = new boolean[ steps ];
		this.colors = DrawUtil.getGradient(innerColor, outerColor, steps);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#showNotify()
	 */
	public void showNotify()
	{
		super.showNotify();
		for (int i = 0; i < this.steps; i++)
		{
			this.activeSignals[i] = false;
		}
		this.emittedSignals = 0;
		this.isAnimationStopped = false;
	}

	


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#animate(de.enough.polish.ui.Screen, de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Screen screen, Item parent, long currentTime, ClippingRegion repaintRegion)
	{
		if (parent == null || this.isAnimationStopped) {
			return;
		}
		int maxDist = 0;
		for (int i = this.steps - 1; i > 0; i-- ) {
			boolean active = this.activeSignals[ i - 1];
			this.activeSignals[i] = active;
			if (active && maxDist == 0) {
				maxDist = this.distances[i];
			}
		}
		this.activeSignals[ 0 ] = false;
		if ((currentTime - this.lastEmittingTime > this.pause) && (this.numSignals == -1 || this.emittedSignals < this.numSignals) ){
			this.activeSignals[0] = true;
			this.lastEmittingTime = currentTime;
			if (maxDist == 0) {
				maxDist = this.distances[0];
			}
			this.emittedSignals++;
		}
		if (maxDist == 0 && this.numSignals != -1 && this.emittedSignals >= this.numSignals) {
			// add a last repaint request so that the most outer signal can be removed:
			maxDist = this.distances[ this.steps - 1];
			this.isAnimationStopped = true;
		}
		parent.addRelativeToBackgroundRegion(null, this, repaintRegion, -maxDist, -maxDist, parent.getBackgroundWidth() + (maxDist << 1), parent.getBackgroundHeight() + (maxDist << 1));
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Border#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		for (int i = 0; i < this.steps; i++)
		{
			int dist = this.distances[i];
			if (this.activeSignals[i]) {
				int color = this.colors[ i ];
				if (this.hasTranslucentColor) {
					DrawUtil.drawLine(color, x - dist, y - dist,  x + width + dist, y - dist, g);
					DrawUtil.drawLine(color, x - dist, y + height + dist,  x + width + dist, y + height + dist, g);
					DrawUtil.drawLine(color, x - dist, y - dist, x - dist, y + height + dist, g);
					DrawUtil.drawLine(color, x + width + dist, y - dist, x + width + dist, y + height + dist, g);
				} else {
					g.setColor(color);
					g.drawRect( x - dist, y - dist, width + dist, height + dist );
				}
			}
		}
	}

}
