/*
 * Created on 07-Oct-2004 at 22:27:36.
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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import de.enough.polish.util.BitMapFont;
import de.enough.polish.util.BitMapFontViewer;
import de.enough.polish.util.Locale;

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
public class World extends LayerManager {
	private final static int CYCLES_BEFORE_GAME_OVER = 80;
	private final static int MESSAGE_CYCLES = 80;
	private final static int ANIMATED_TILE_START_ID = 5;
	private static final int TILE_WIDTH = 32;
	private static final int TILE_HEIGHT = 32;
	private static final int POINTS_LEVEL_FINISHED = 30;
	private static final int POINTS_TARGET_REACHED = 10;
	private static final int POINTS_TARGET_KEY_PICKED_UP = 5;
	private static final String[] TARGET_REACHED_MESSAGES = new String[] {
		Locale.get("message.TargetReached.1"), Locale.get("message.TargetReached.2"),  	
		Locale.get("message.TargetReached.3"), Locale.get("message.TargetReached.4")  	
	};
	private static final String[] KILLED_MESSAGES = new String[] {
			Locale.get("message.Killed.1"), Locale.get("message.Killed.2"),  	
			Locale.get("message.Killed.3"), Locale.get("message.Killed.4")  	
		};
	private int targetMessageReachedIndex;
	private int killedMessageIndex;
	private TiledLayer background;
	protected Hero hero;
	private Sprite originalCarEnemy;
	private Sprite[] carEnemies;
	private int carSpeed;
	private long leftCarDistance;
	private Sprite originalVanEnemy;
	private int vanSpeed;
	private long leftVanDistance;
	private Sprite[] vanEnemies;
	private Sprite originalTruckEnemy;
	private int truckSpeed;
	private Sprite[] truckEnemies;
	private Sprite originalTargetKey;
	private Sprite[] targetKeys;
	private boolean[] targetKeyCollected;
	private final int screenWidth;
	private final int screenHeight;
	protected boolean gameOver;
	private boolean prepareGameOver;
	private int animationCyclesBeforeGameOver = CYCLES_BEFORE_GAME_OVER;
	private String remainingLivesString;
	private int grassAnimationIndex = 0;
	private byte[] grassAnimation;
	protected int numberOfTargets;
	private int lastPlayerX;
	private int lastPlayerY;
	private int backgroundWidth;
	private int backgroundHeight;
	private String message;
	private int messageCountdown = MESSAGE_CYCLES;
	//private boolean messageVisible = true;
	private boolean levelFinished;
	private int currentLevel;
	private final Image statusBarImage;
	private final int statusBarHeight;
	private int points;
	private String pointsString = "0";
	private long levelStartTime;
	private int remainingLevelTime;
	private int availableLevelTime;
	private String remainingTimeString;
	private final Font statusFont;
	//private final Font messageFont;
	private String levelString;
	private boolean updateStatusBar;
	private Image backgroundImage;
	private long leftTruckDistance;
	//#ifdef polish.Bugs.layerManagerSetViewWindow
		private int storedViewX;
		private int storedViewY;
	//#endif
	private final BitMapFont font;
	private BitMapFontViewer viewer;
	
	

	/**
	 * @param screenWidth
	 * @param screenHeight
	 * @throws IOException
	 * 
	 */
	public World( int screenWidth, int screenHeight ) throws IOException {
		super();
		this.statusBarImage = Image.createImage( "/statusbar.png" );
		this.statusBarHeight = this.statusBarImage.getHeight();
		this.statusFont = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL );
		//this.messageFont = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE );
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight - this.statusBarHeight;
		
		
		
		// init car-sprite:
		Image carImage = Image.createImage( "/car.png" );
		this.originalCarEnemy = new Sprite( carImage );
		this.originalCarEnemy.defineReferencePixel(0, carImage.getHeight() );

		// init player / hero:
		Image playerImage = Image.createImage( "/player.png");
		// read basic settings
		InputStream in = getClass().getResourceAsStream("/world.data");
		int idleSequenceLength = in.read();
		int[] idleSequence = new int[ idleSequenceLength ];//{ 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1 };
		for (int i = 0; i < idleSequenceLength; i++) {
			idleSequence[i] = in.read();
		}
		int tiltedIdleSequenceLength = in.read();
		int[] tiltedIdleSequence = new int[ tiltedIdleSequenceLength ]; //{ 8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,9,9,9,9,9,9,9,9,8,8,8,8,8,8,8,8,8,8,8,9,9 };
		for (int i = 0; i < tiltedIdleSequenceLength; i++) {
			tiltedIdleSequence[i] = in.read();
		}
		int jumpSequenceLength = in.read();
		int[] jumpSequence 	= new int[ jumpSequenceLength ]; //{0, 2, 2, 3, 3, 2, 2, 1, 1};
		for (int i = 0; i < jumpSequence.length; i++) {
			jumpSequence[i] = in.read();
		}
		int[] jumpDistances = new int[ jumpSequenceLength ]; //{3, 7,20,28,30,30,31,31,32};
		for (int i = 0; i < jumpSequence.length; i++) {
			jumpDistances[i] = in.read();
		}
		int targetReachedSequenceLength = in.read();
		int[] targetReachedSequence = new int[ targetReachedSequenceLength ]; //{ 3, 6, 6,6,7,7,7,7,7 };
		for (int i = 0; i < targetReachedSequenceLength; i++) {
			targetReachedSequence[i] = in.read();
		}
		int dyingSequenceLength = in.read();
		int[] dyingSequence = new int[dyingSequenceLength]; //{ 3,3,3,3,4,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5 };
		for (int i = 0; i < dyingSequenceLength; i++) {
			dyingSequence[i] = in.read();
		}
		int frameWidth = 30;
		int frameHeight = 30;
		this.hero = new Hero( playerImage, frameWidth, frameHeight, 
				idleSequence, tiltedIdleSequence, jumpSequence, jumpDistances, targetReachedSequence, dyingSequence,
				this );
		this.hero.defineCollisionRectangle( 6, 10, 16, 19);
		this.hero.defineReferencePixel( 14, 19 );
		
		this.remainingLivesString = "" + Hero.LIVES_AT_START;
		
		// init target-keys - these are needed by the hero
		// to reach a level-target:
		Image targetKeyImage = Image.createImage( "/crown.png" );
		this.originalTargetKey = new Sprite( targetKeyImage );
		this.originalTargetKey.defineReferencePixel( 0, targetKeyImage.getHeight() );
		

		// define start-message:
		this.message = Locale.get("message.GameStarted");

		loadNextLevel();
		
		//TODO rob remove test code:
		this.font = BitMapFont.getInstance( "/example.bmf" );
	}
	
	protected void setTargetReached( int col, int row ) {
		this.numberOfTargets--;
		this.points += POINTS_TARGET_REACHED;
		this.background.setCell( col, row, 4);
		if (this.numberOfTargets == 0) {
			this.points += POINTS_LEVEL_FINISHED + this.remainingLevelTime;
			this.message = Locale.get("message.LevelFinished", this.levelString );
			this.levelFinished = true;
		} else {
			this.message = TARGET_REACHED_MESSAGES[ this.targetMessageReachedIndex ];
			this.targetMessageReachedIndex++;
			if (this.targetMessageReachedIndex == TARGET_REACHED_MESSAGES.length ) {
				this.targetMessageReachedIndex = 0;
			}
		}
		this.pointsString = Integer.toString( this.points );
		this.updateStatusBar = true;
	}


	/**
	 * @param ticks
	 * 
	 */
	public void animate( long ticks ) {
		this.hero.animate( ticks );
		int playerX = this.hero.getRefPixelX();
		int playerY = this.hero.getRefPixelY();
		if (this.lastPlayerX != playerX || this.lastPlayerY != playerY) {
			if (playerX < 0 ) {
				playerX = 5;
				this.hero.setRefPixelPosition(playerX, playerY);
			} else if (playerX >= this.backgroundWidth) {
				playerX = this.backgroundWidth - 5;
				this.hero.setRefPixelPosition(playerX, playerY);
			} else if (playerY < this.statusBarHeight) {
				playerY = this.statusBarHeight + 5;
				this.hero.setRefPixelPosition(playerX, playerY);
			} else if (playerY >= this.backgroundHeight) {
				playerY = this.backgroundHeight - 5;
				this.hero.setRefPixelPosition(playerX, playerY);
			}
			
			// check if the player has reached a target:
			final int column = playerX / TILE_WIDTH;
			final int row = playerY / TILE_HEIGHT;
			final int cell = this.background.getCell( column, row );
			if (cell == 3 && this.hero.carriesTargetKey) {
				// the hero hit a level-target!
				this.hero.targetReached( column, row );
				this.hero.carriesTargetKey = false;
			} else if (cell == 2) {
				// player jumped against a wall,
				// so now bounce back:
				this.hero.setRefPixelPosition( playerX, playerY + 10);
			}
			
			// check if the player can pick up a target-key:
			if (!this.hero.carriesTargetKey) { 
				for (int i = 0; i < this.targetKeys.length; i++) {
					if (!this.targetKeyCollected[i]) {
						Sprite key = this.targetKeys[i];
						if ( key.collidesWith(this.hero, false) ) 
						{
							this.points += POINTS_TARGET_KEY_PICKED_UP;
							this.pointsString = Integer.toString( this.points );
							this.updateStatusBar = true;
							this.hero.carriesTargetKey = true;
							remove( key );
							this.targetKeyCollected[i] = true;
						}
					}
				}
			}
			
			// set viewport:
			int viewX = playerX - this.screenWidth / 2;
			if (viewX < 0) {
				//System.out.println("viewX < 0");
				viewX = 0;
			} else if (viewX + this.screenWidth > this.backgroundWidth) {
				//System.out.println("backgroundWidth=" + backgroundWidth);
				viewX = this.backgroundWidth - this.screenWidth;
			}
			int viewY = playerY - this.screenHeight / 2;
			if (viewY < 0) {
				//System.out.println("viewY < 0: " + viewY);
				viewY = 0;
			} else if (viewY + this.screenHeight > this.backgroundHeight ) {
				//System.out.println("viewY to big: backgroundHeight=" + backgroundHeight);
				viewY = this.backgroundHeight - this.screenHeight;
			}
			//System.out.println("setting view-window: " + viewX + ", " + viewY + ", " + this.screenWidth + ", " + this.screenHeight  );
			//#if !polish.Bugs.layerManagerSetViewWindow
				setViewWindow(viewX, viewY, this.screenWidth, this.screenHeight );
			//#else
				this.storedViewX = viewX;
				this.storedViewY = viewY;
			//#endif
			this.lastPlayerX = playerX;
			this.lastPlayerY = playerY;
		} // if player-position has changed
		boolean doCollisionTest = !this.hero.isDying && !this.prepareGameOver && !this.hero.isReachingTarget;
		
		long distancePerMilisecond = (this.carSpeed * ticks) + this.leftCarDistance; 
		int forwardDistance = (int) ( distancePerMilisecond / 1000);
		this.leftCarDistance = distancePerMilisecond - (forwardDistance * 1000);
		for (int i = 0; i < this.carEnemies.length; i++) {
			Sprite enemy = this.carEnemies[i];
			int xPos = (enemy.getX() + forwardDistance);
			if (xPos > this.backgroundWidth ) {
				xPos = xPos % this.backgroundWidth - 50;
			}
			enemy.setPosition( xPos, enemy.getY() );
			// check for collision:
			if (doCollisionTest) {
				if (enemy.collidesWith(this.hero, false)) {
					int lives = this.hero.kill();
					if (lives == -1) {
						this.prepareGameOver = true;
						this.message = Locale.get("message.GameOver");
					} else {
						this.message = KILLED_MESSAGES[ this.killedMessageIndex ];
						this.killedMessageIndex++;
						if (this.killedMessageIndex == KILLED_MESSAGES.length ) {
							this.killedMessageIndex = 0;
						}
						this.remainingLivesString = Integer.toString( lives );
						this.updateStatusBar = true;
					}
				}
			}
		}
		
		// animate vans:
		if (this.vanEnemies != null) {
			distancePerMilisecond = (this.vanSpeed * ticks) + this.leftVanDistance; 
			forwardDistance = (int) ( distancePerMilisecond / 1000);
			this.leftVanDistance = distancePerMilisecond - (forwardDistance * 1000);
			for (int i = 0; i < this.vanEnemies.length; i++) {
				Sprite enemy = this.vanEnemies[i];
				int xPos = (enemy.getX() + forwardDistance);
				if (xPos > this.backgroundWidth ) {
					xPos = xPos % this.backgroundWidth - 50;
				}
				enemy.setPosition( xPos, enemy.getY() );
				// check for collision:
				if (doCollisionTest) {
					if (enemy.collidesWith(this.hero, false)) {
						int lives = this.hero.kill();
						if (lives == -1) {
							this.prepareGameOver = true;
							this.message = Locale.get("message.GameOver");
						} else {
							this.message = KILLED_MESSAGES[ this.killedMessageIndex ];
							this.killedMessageIndex++;
							if (this.killedMessageIndex == KILLED_MESSAGES.length ) {
								this.killedMessageIndex = 0;
							}
							this.remainingLivesString = Integer.toString( lives );
							this.updateStatusBar = true;
						}
					}
				}
			}	
		}

		// animate trucks:
		if (this.truckEnemies != null) {
			distancePerMilisecond = (this.truckSpeed * ticks) + this.leftTruckDistance; 
			forwardDistance = (int) ( distancePerMilisecond / 1000);
			this.leftTruckDistance = distancePerMilisecond - (forwardDistance * 1000);
			for (int i = 0; i < this.truckEnemies.length; i++) {
				Sprite enemy = this.truckEnemies[i];
				int xPos = (enemy.getX() + forwardDistance);
				if (xPos > this.backgroundWidth ) {
					xPos = xPos % this.backgroundWidth - 50;
				}
				enemy.setPosition( xPos, enemy.getY() );
				// check for collision:
				if (doCollisionTest) {
					if (enemy.collidesWith(this.hero, false)) {
						int lives = this.hero.kill();
						if (lives == -1) {
							this.prepareGameOver = true;
							this.message = Locale.get("message.GameOver");
						} else {
							this.message = KILLED_MESSAGES[ this.killedMessageIndex ];
							this.killedMessageIndex++;
							if (this.killedMessageIndex == KILLED_MESSAGES.length ) {
								this.killedMessageIndex = 0;
							}
							this.remainingLivesString = Integer.toString( lives );
							this.updateStatusBar = true;
						}
					}
				}
			}	
		}
		// animate background:
		this.grassAnimationIndex++;
		if (this.grassAnimationIndex == this.grassAnimation.length) {
			this.grassAnimationIndex = 0;
		}
		this.background.setAnimatedTile( -1, this.grassAnimation[ this.grassAnimationIndex ] );
		
		if (this.prepareGameOver) {
			this.animationCyclesBeforeGameOver--;
			if (this.animationCyclesBeforeGameOver == 0) {
				this.animationCyclesBeforeGameOver = CYCLES_BEFORE_GAME_OVER;
				this.gameOver = true;
			}
		} else {
			int remainingTime =   this.availableLevelTime - (int) ((System.currentTimeMillis() - this.levelStartTime) / 1000);
			if (remainingTime != this.remainingLevelTime) {
				this.remainingLevelTime = remainingTime;
				this.remainingTimeString = Integer.toString( remainingTime );
				this.updateStatusBar = true;
				if ( remainingTime < 0 ) {
					this.prepareGameOver = true;
					this.message = Locale.get("message.TimeOut");
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void loadNextLevel() {
		// clear the current level:
		int size = getSize();
		for (int i = 0; i < size; i++ ) {
			remove( getLayerAt(0) );
		}
		
		this.currentLevel++;
		this.levelFinished = false;
		DataInputStream in = null;
		try {
			String levelName = "/level" + this.currentLevel + ".data";
			InputStream inputStream = getClass().getResourceAsStream( levelName );
			if (inputStream == null) {
				//TODO finish game!
				this.currentLevel = 1;
				levelName = "/level1.data";
				inputStream = getClass().getResourceAsStream( levelName );
			}
			in = new DataInputStream( inputStream );
			this.levelString = "" + this.currentLevel; 
			int playerStartX = in.readShort();
			int playerStartY = in.readShort();
			this.hero.setStartPosition( playerStartX, playerStartY );
			// init roads:
			int numberOfRoads = in.readUnsignedByte();
			int[] roadYPositions = new int[ numberOfRoads ];
			for (int i = 0; i < roadYPositions.length; i++) {
				roadYPositions[i] = in.readShort();
			}
			// init cars:
			this.carSpeed = in.readUnsignedByte();
			int numberOfEnemies = in.readUnsignedByte();
			int enemyXPos = 0;
			Sprite[] enemies = new Sprite[ numberOfEnemies ];
			for (int i = 0; i < numberOfEnemies; i++) {
				enemyXPos += in.readUnsignedByte();
				Sprite enemy = new Sprite( this.originalCarEnemy );
				enemy.setRefPixelPosition( enemyXPos, roadYPositions[ in.readUnsignedByte() ] );
				enemies[i] = enemy;
				append( enemy );
			}
			this.carEnemies = enemies;
			// init vans:
			this.vanSpeed = in.readUnsignedByte();
			numberOfEnemies = in.readUnsignedByte();
			if (numberOfEnemies == 0) {
				enemies = null;
			} else {
				enemies = new Sprite[ numberOfEnemies ];
				if (this.originalVanEnemy == null) {
					Image vanImage = Image.createImage("/van.png");
					this.originalVanEnemy = new Sprite( vanImage );
					this.originalVanEnemy.defineReferencePixel(0, vanImage.getHeight() );
				}
			}
			enemyXPos = 0;
			for (int i = 0; i < numberOfEnemies; i++) {
				enemyXPos += in.readUnsignedByte();
				Sprite enemy = new Sprite( this.originalVanEnemy );
				enemy.setRefPixelPosition( enemyXPos, roadYPositions[ in.readUnsignedByte() ] );
				enemies[i] = enemy;
				append( enemy );
			}
			this.vanEnemies = enemies;
			// init trucks:
			this.truckSpeed = in.readUnsignedByte();
			numberOfEnemies = in.readUnsignedByte();
			if (numberOfEnemies == 0) {
				enemies = null;
			} else {
				enemies = new Sprite[ numberOfEnemies ];
				if (this.originalTruckEnemy == null) {
					Image truckImage = Image.createImage("/truck.png");
					this.originalTruckEnemy = new Sprite( truckImage );
					this.originalTruckEnemy.defineReferencePixel(0, truckImage.getHeight() );
				}
			}
			enemyXPos = 0;
			for (int i = 0; i < numberOfEnemies; i++) {
				enemyXPos += in.readUnsignedByte();
				Sprite enemy = new Sprite( this.originalTruckEnemy );
				enemy.setRefPixelPosition( enemyXPos, roadYPositions[ in.readUnsignedByte() ] );
				enemies[i] = enemy;
				append( enemy );
			}
			this.truckEnemies = enemies;
			
			// append the player:
			append( this.hero );
			
			// init targets and target-keys:
			this.numberOfTargets = in.readUnsignedByte();
			this.targetKeyCollected = new boolean[ this.numberOfTargets ];
			this.targetKeys = new Sprite[ this.numberOfTargets ];
			for (int i = 0; i < this.targetKeys.length; i++) {
				Sprite key = new Sprite( this.originalTargetKey );
				key.setRefPixelPosition( in.readShort(), in.readShort() );
				this.targetKeys[i] = key;
				append( key );
			}
			
			// init background:
			int length = in.readUnsignedByte();
			if (length != 0) {
				byte[] buffer = new byte[ length ];
				in.readFully(buffer);
				String imageUrl = new String( buffer );
				this.backgroundImage = Image.createImage(  imageUrl );
			}
			int columns = in.readUnsignedByte();
			int rows = in.readUnsignedByte();
			this.background = new TiledLayer( columns, rows, this.backgroundImage, TILE_WIDTH, TILE_HEIGHT );
			this.background.createAnimatedTile( ANIMATED_TILE_START_ID ); // animateTile.index == -1
			for (int row = 0; row < rows; row++ ) {
				for (int col = 0; col < columns; col++ ) {
					int cellId = in.readByte();
					this.background.setCell(col, row, cellId);
				}
			}
			append( this.background );
			this.backgroundWidth = this.background.getWidth();
			if (this.backgroundWidth < this.screenWidth) {
				this.backgroundWidth = this.screenWidth;
			}
			this.backgroundHeight = this.background.getHeight();
			if (this.backgroundHeight < this.screenHeight) {
				this.backgroundHeight = this.screenHeight;
			}
			
			// load level time:
			this.availableLevelTime = in.readShort();
			this.remainingTimeString = Integer.toString( this.availableLevelTime );
			this.levelStartTime = System.currentTimeMillis();
			
			// load background animation:
			int backgroundAnimationLength = in.readUnsignedByte();
			this.grassAnimationIndex = 0;
			this.grassAnimation = new byte[ backgroundAnimationLength ];
			for (int i=0; i < backgroundAnimationLength; i++ ) {
				this.grassAnimation[i] = in.readByte();
			}
			
			this.updateStatusBar = true;
		} catch (IOException e) {
			//#debug error
			System.out.println("UNABLE TO LOAD LEVEL..." + e);
			this.message = "UNABLE TO LOAD LEVEL...";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void render( Graphics g, int x, int y ) {
		//#ifdef polish.Bugs.layerManagerSetViewWindow
			paint( g, x - this.storedViewX, this.statusBarHeight - this.storedViewY );
		//#else
			paint( g, x, this.statusBarHeight );
		//#endif
		if (this.message != null) {
			if (this.viewer == null) {
				this.viewer = this.font.getViewer(this.message);
			}
			int width = this.viewer.getWidth();
			int height = this.viewer.getHeight();
			int viewX = (this.screenWidth - width)/2;
			int viewY = this.screenHeight/3;
			g.setColor( 0xFFFFFF );
			g.fillRoundRect(viewX - 2, viewY - 2, width + 4, height + 4, 10, 10);
			this.viewer.paint( viewX, viewY, g );
			/*
			g.setColor( 0xFF0000 );
			g.setFont( this.messageFont );
			if (this.messageVisible) {
				g.drawString( this.message, this.screenWidth/2, this.screenHeight/3, Graphics.HCENTER | Graphics.BASELINE );
			}
			*/
			this.messageCountdown--;
			if (this.messageCountdown == 0) {
				this.message = null;
				this.viewer = null;
				this.messageCountdown = MESSAGE_CYCLES;
				//this.messageVisible = true;
				if (this.levelFinished) {
					loadNextLevel();
				}
			}
		} 
		//#if !polish.Bugs.layerManagerSetViewWindow
		if (this.updateStatusBar ){
		//#endif
			// paint status-bar:
			g.setColor( 0 );
			g.setFont( this.statusFont );
			g.drawImage( this.statusBarImage, 0, 0, Graphics.TOP | Graphics.LEFT );
			g.drawString( this.remainingLivesString, 15, 0, Graphics.TOP | Graphics.LEFT );
			g.drawString( this.pointsString, 80, 0, Graphics.TOP | Graphics.RIGHT );
			g.drawString( this.remainingTimeString, 113, 0, Graphics.TOP | Graphics.LEFT );
			g.drawString( this.levelString, 162, 0, Graphics.TOP | Graphics.LEFT );
		//#if !polish.Bugs.layerManagerSetViewWindow
			this.updateStatusBar = false;
		}
		//#endif
	}
	

}
