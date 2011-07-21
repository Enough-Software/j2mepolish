//#condition polish.usePolishGui
/*
 * Created on 15-Feb-2005 at 01:39:10.
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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

/**
 * <p>Allows to use sprites within normal forms.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        15-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
	
public class SpriteItem
//#ifdef polish.usePolishGui
	//# extends CustomItem
//#else
	extends javax.microedition.lcdui.CustomItem
//#endif
{
	
	private final Sprite sprite;
	private final long animationInterval;
	private final int defaultFrameIndex;
	private final boolean repeatAnimation;
	private boolean isSpriteItemFocused;
	private int currentStep;
	private int maxStep;
	private long lastAnimationTime;

	/**
	 * Creates a new sprite item.
	 * 
	 * @param label the label of this item
	 * @param sprite the sprite that should be painted
	 * @param animationInterval the interval in milliseconds for animating this item
	 * @param defaultFrameIndex the frame that is shown when the SpriteItem is not focused 
	 * @param repeatAnimation defines whether the animation should be repeated when the last frame
	 *        of the frame-sequence has been reached. 
	 */
	public SpriteItem(String label, Sprite sprite, long animationInterval, int defaultFrameIndex, boolean repeatAnimation ) {
		//#ifdef polish.usePolishGui
			this( label, sprite, animationInterval, defaultFrameIndex, repeatAnimation, null );
		//#else
			//# super( label );
			//# this.sprite = sprite;
			//# sprite.setFrame(defaultFrameIndex);
			//# this.animationInterval = animationInterval;
			//# this.defaultFrameIndex = defaultFrameIndex;
			//# this.repeatAnimation = repeatAnimation;
	    //#endif
	}

	//#ifdef polish.usePolishGui	
	/**
	 * Creates a new sprite item.
	 * 
	 * @param label the label of this item
	 * @param sprite the sprite that should be painted
	 * @param animationInterval the interval in milliseconds for animating this item
	 * @param defaultFrameIndex the frame that is shown when the SpriteItem is not focused 
	 * @param repeatAnimation defines whether the animation should be repeated when the last frame
	 *        of the frame-sequence has been reached. 
	 * @param style the CSS style
	 */
	public SpriteItem(String label, Sprite sprite, long animationInterval, int defaultFrameIndex, boolean repeatAnimation, Style style) {
		//#ifdef polish.usePolishGui
			//# super(label, style);
		//#else
			super( label );
		//#endif
		this.sprite = sprite;
		this.animationInterval = animationInterval;
		this.defaultFrameIndex = defaultFrameIndex;
		this.repeatAnimation = repeatAnimation;
		sprite.setFrame(defaultFrameIndex);
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#getMinContentWidth()
	 */
	protected int getMinContentWidth() {
		return this.sprite.getWidth();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#getMinContentHeight()
	 */
	protected int getMinContentHeight() {
		return this.sprite.getHeight();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#getPrefContentWidth(int)
	 */
	protected int getPrefContentWidth(int height) {
		return this.sprite.getWidth();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#getPrefContentHeight(int)
	 */
	protected int getPrefContentHeight(int width) {
		return this.sprite.getHeight();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.CustomItem#paint(javax.microedition.lcdui.Graphics, int, int)
	 */
	protected void paint(Graphics g, int w, int h) {
		this.sprite.paint(g);
	}

	//#ifdef polish.usePolishGui
	public boolean animate() {
		long time = System.currentTimeMillis();
		if (  time - this.lastAnimationTime >= this.animationInterval ) {
			this.lastAnimationTime = time;
			if ( this.repeatAnimation || this.currentStep < this.maxStep ) {
				this.sprite.nextFrame();
				this.currentStep++;
				return true;
			}
		}
		return false;
	}
	//#endif
	
	

	protected boolean traverse(int direction, int viewportWidth, int viewportHeight, int[] inoutRect) {
		if (this.isSpriteItemFocused) {
			return false;
		} else {
			this.currentStep = 0;
			if (!this.repeatAnimation) {
				this.maxStep = this.sprite.getFrameSequenceLength() - 1;
			}
			this.isSpriteItemFocused = true;
			return true;
		}
	}
	
	protected void traverseOut() {
		this.isSpriteItemFocused = false;
		this.sprite.setFrame( this.defaultFrameIndex );
	}
	}
