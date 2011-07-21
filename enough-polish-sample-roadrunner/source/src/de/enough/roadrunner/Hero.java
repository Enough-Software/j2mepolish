/*
 * Created on 07-Oct-2004 at 22:31:40.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.roadrunner;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        07-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Hero extends Sprite {
	protected static final int LIVES_AT_START = 3;
	private int remainingLives = LIVES_AT_START;
	private static final int TARGET_REACHED_INVISIBLE_COUNTER = 50;
	private static final int[] TRANSFORMATIONS_BY_DIRECTION = new int[] { 
			Sprite.TRANS_NONE, // straight forward 
			Sprite.TRANS_NONE, // 45 degrees up-right (tilted frame provided)
			Sprite.TRANS_ROT90, // right
			Sprite.TRANS_MIRROR_ROT180, // 135 degrees down-right (tilted frame)
			Sprite.TRANS_ROT180, // 180 degrees / downwards
			Sprite.TRANS_ROT180,  // 225 degrees / down-left (tilted frame)
			Sprite.TRANS_ROT270, // 270 degrees / left
			Sprite.TRANS_MIRROR  }; // 315 degrees / up-left (tilted frame )
	private final World world;
	private final int[] idleSequence;
	private final int[] tiltedIdleSequence;
	protected boolean isJumping;
	private int jumpYStartPosition;
	private int jumpXStartPosition;
	private final int[] jumpSequence;
	private final int[] jumpDistances;
	protected boolean isReachingTarget;
	protected int[] targetReachedSequence;
	protected boolean isDying;
	private final int[] dyingSequence;
	protected boolean isDead; 
	protected boolean carriesTargetKey;
	private int startX;
	private int startY;
	private int targetCol;
	private int targetRow;
	private boolean isInvisible;
	private int invisibleCounter;
	private int direction;

	/**
	 * Creates a new hero.
	 * 
	 * @param image
	 * @param frameWidth
	 * @param frameHeight
	 * @param idleSequence
	 * @param tiltedIdleSequence
	 * @param jumpSequence
	 * @param jumpDistances
	 * @param targetReachedSequence
	 * @param dyingSequence
	 * @param world
	 */
	public Hero(Image image, int frameWidth, int frameHeight,
			int[] idleSequence, int[] tiltedIdleSequence,
			int[] jumpSequence, int[] jumpDistances,
			int[] targetReachedSequence, int[] dyingSequence, World world) 
	{
		super(image, frameWidth, frameHeight);
		this.world = world;
		this.idleSequence = idleSequence;
		this.tiltedIdleSequence = tiltedIdleSequence;
		this.jumpSequence = jumpSequence;
		this.jumpDistances = jumpDistances;
		this.targetReachedSequence = targetReachedSequence;
		this.dyingSequence = dyingSequence;
		setFrameSequence(idleSequence);
	}
	
	protected boolean handleGameAction( int gameAction) {
		if (this.isJumping || this.isDying || this.isReachingTarget) {
			// ignore multiple key-presses:
			return false;
		}
		boolean doJump = false;
		switch (gameAction) {
			case Canvas.UP:
				if (this.direction == 0) {
					doJump = true;
				} else if (this.direction == 4) {
					this.direction = 0;
					setTransform( TRANSFORMATIONS_BY_DIRECTION[0] );
					doJump = true;
				} else if ( this.direction < 4 ) {
					this.direction--;
				} else {
					this.direction++;
					if (this.direction == 8) {
						this.direction = 0;
					}
				}
				break;
			case Canvas.DOWN:
				if (this.direction == 4) {
					doJump = true;
				} else if (this.direction == 0) {
					this.direction = 4;
					setTransform( TRANSFORMATIONS_BY_DIRECTION[4] );
					doJump = true;
				} else if ( this.direction > 4 ) {
					this.direction--;
				} else {
					this.direction++;
				}
				break;
			case Canvas.RIGHT:
				if (this.direction == 2) {
					doJump = true;
				} else if (this.direction == 6) {
					this.direction = 2;
					setTransform( TRANSFORMATIONS_BY_DIRECTION[2] );
					doJump = true;
				} else if ( this.direction < 6 && this.direction > 2 ) {
					this.direction--;
				} else {
					this.direction++;
					if (this.direction == 8) {
						this.direction = 0;
					}
				}
				break;
			case Canvas.LEFT:
				if (this.direction == 6) {
					doJump = true;
				} else if (this.direction == 2) {
					this.direction = 6;
					setTransform( TRANSFORMATIONS_BY_DIRECTION[6] );
					doJump = true;
				} else if ( this.direction < 6 && this.direction > 2 ) {
					this.direction++;
				} else {
					this.direction--;
					if (this.direction == -1) {
						this.direction = 7;
					}
				}
				break;
		}
		if (doJump) {
			this.isJumping = true;
			this.jumpYStartPosition = getY();
			this.jumpXStartPosition = getX();
			setFrameSequence(this.jumpSequence );
			return false;
		} else {
			// change the direction:
			setTransform( TRANSFORMATIONS_BY_DIRECTION[ this.direction ] );
			if (this.direction % 2 == 1) {
				setFrameSequence(this.tiltedIdleSequence);
			} else {
				setFrameSequence(this.idleSequence);
			}
			return true;
		}

	}

	protected void jumpForward(){
		if (this.isJumping || this.isDying || this.isReachingTarget) {
			// ignore multiple key-presses:
			return;
		}
		this.isJumping = true;
		this.jumpYStartPosition = getY();
		this.jumpXStartPosition = getX();
		setFrameSequence(this.jumpSequence );
	}
	
	protected void turnDirection( boolean toTheRight ) {
		if (this.isJumping || this.isDying || this.isReachingTarget) {
			// ignore multiple key-presses:
			return;
		}
		if (toTheRight) {
			this.direction++;		
			if (this.direction > 7) {
				this.direction = 0;
			}
		} else {
			this.direction--;		
			if (this.direction < 0) {
				this.direction = 7;
			}
		}
		setTransform( TRANSFORMATIONS_BY_DIRECTION[ this.direction ] );
		if (this.direction % 2 == 1) {
			setFrameSequence(this.tiltedIdleSequence);
		} else {
			setFrameSequence(this.idleSequence);
		}
	}
	
	protected int kill() {
		this.remainingLives--;
		this.isDying = true;
		this.isJumping = false;
		setFrameSequence(this.dyingSequence );
		return this.remainingLives;
	}
	
	protected void targetReached(int col, int row) {
		this.isReachingTarget = true;
		this.isJumping = false;
		setFrameSequence(this.targetReachedSequence);
		this.targetCol = col;
		this.targetRow = row;
	}
	
	protected void animate( long ticks ) {
		if (this.isDead) {
			return;
		}
		nextFrame();
		if ( this.isDying ) {
			int frameIndex = getFrame();
			if (frameIndex == (this.dyingSequence.length - 1)) {
				this.isDying = false;
				if (this.remainingLives >= 0) {
					setStartPosition();
				} else {
					this.isDead = true;   
				}
			}
		} else if (this.isJumping ) {
			int frameIndex = getFrame();
			if (frameIndex == (this.jumpSequence.length - 1)) {
				this.isJumping = false;
				if (this.direction % 2 == 0) {
					setFrameSequence(this.idleSequence);
				} else {
					setFrameSequence(this.tiltedIdleSequence);
				}
			}
			int distance = this.jumpDistances[ frameIndex ];
			int halfDistance = distance / 2;
			int x = this.jumpXStartPosition;
			int y = this.jumpYStartPosition;
			switch (this.direction) {
				case 0: y -= distance; break;
				case 1: y -= halfDistance; x += halfDistance; break;
				case 2: x += distance; break;
				case 3: y += halfDistance; x += halfDistance; break;
				case 4: y += distance; break;
				case 5: y += halfDistance; x -= halfDistance; break;
				case 6: x -= distance; break;
				case 7: y -= halfDistance; x -= halfDistance;
			}
			this.setPosition( x, y );
		} else if (this.isInvisible) {
			this.invisibleCounter--;
			if (this.invisibleCounter == 0) {
				this.isReachingTarget = false;
				this.isInvisible = false;
				if (this.world.numberOfTargets > 0) {
					this.world.insert(this, this.world.getSize() - 1);
					setStartPosition();
				}
			}			
		} else if (this.isReachingTarget) {
			int frameIndex = getFrame();
			if (frameIndex == (this.targetReachedSequence.length - 1)) {
				this.isInvisible = true;
				this.invisibleCounter = TARGET_REACHED_INVISIBLE_COUNTER;
				this.world.setTargetReached(this.targetCol, this.targetRow);
				this.world.remove( this );
			}
		}
	}
	


	/**
	 * Sets the start position of the hero.
	 * This position will be used after the hero has died (and right now as well).
	 *  
	 * @param x the horizontal start position
	 * @param y the vertical start position
	 */
	public void setStartPosition(int x, int y) {
		this.startX = x;
		this.startY = y;
		setStartPosition();
	}

	/**
	 * 
	 */
	public void setStartPosition() {
		setTransform( TRANS_NONE );
		this.direction = 0;
		setFrameSequence(this.idleSequence);
		setPosition(this.startX, this.startY);
	}
	
	

}
