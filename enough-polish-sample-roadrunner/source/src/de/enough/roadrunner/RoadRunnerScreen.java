/*
 * Created on 29-Sep-2004 at 22:58:32.
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

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

import de.enough.polish.util.Locale;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        29-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
//#if polish.audio.midi && (polish.api.mmapi || polish.midp2)
	//#define tmp.supportSound
//#endif
public class RoadRunnerScreen 
extends GameCanvas
implements Runnable
{
	private final static int MAX_FRAMES_PER_SECOND = 30;
	private final static int TIME_PER_FRAME = 1000 / MAX_FRAMES_PER_SECOND;
	private Graphics graphics;
	protected World world;
	protected Command replayCommand = new Command( Locale.get("cmd.Replay"), Command.SCREEN, 1 );
	protected Command returnCommand = new Command( Locale.get("cmd.Return"), Command.SCREEN, 2 );

	/**
	 * Creates a new game screen.
	 * 
	 * @param midlet the parent midlet, is used for returning to the main menu,
	 *        when the game is stopped.
	 */
	public RoadRunnerScreen( RoadRunner midlet ) {
		super( true );
		setCommandListener( midlet );
		//#if polish.midp2 || (polish.usePolishGui && polish.classes.fullscreen:defined)
			setTitle( Locale.get("title.GameScreen") );
		//#endif
		// #if !(polish.midp2 && polish.vendor.Nokia)
			this.addCommand( this.replayCommand );
			this.addCommand( this.returnCommand );
		// #endif
	}

	/**
	 * Starts the game
	 */
	public void start() {
		Thread thread = new Thread( this );
		thread.start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// #if polish.midp2 && polish.vendor.Nokia
//			this.removeCommand( this.replayCommand );
//			this.removeCommand( this.returnCommand );
		// #endif
		//#if !polish.api.siemens-color-game-api
		setFullScreenMode( true );
		//#endif
		//setFullScreenMode(true);
		// initialise the game:
		try {
			//#ifdef polish.FullCanvasSize:defined
				//#= int width = ${polish.FullCanvasWidth};
			//#else
				int width = getWidth();
			//#endif
			//#ifdef polish.FullCanvasSize:defined
				//#= int height = ${polish.FullCanvasHeight};
			//#else
				int height = getHeight();
			//#endif
			this.world = new World( width, height );
		} catch ( IOException e) {
			//#debug error
			System.out.println("Unable to initialise world" + e);
			return;
		}
        //main loop:
		Hero player = this.world.hero;
		long lastCycleTime = System.currentTimeMillis();
		long cycleStartTime;
		boolean ignoreDirectionKey = false;
		this.graphics = getGraphics();
		while (!this.world.gameOver) {
			cycleStartTime = System.currentTimeMillis();
			// move players and actors:
			int keyState = getKeyStates();
			if (keyState != 0) {
				if ((keyState & RIGHT_PRESSED) != 0 && !ignoreDirectionKey) {
					ignoreDirectionKey = player.handleGameAction( RIGHT );
				} else if ((keyState & LEFT_PRESSED) != 0  && !ignoreDirectionKey) {
					ignoreDirectionKey = player.handleGameAction( LEFT );
				} else if ((keyState & UP_PRESSED) != 0 && !ignoreDirectionKey) {
					ignoreDirectionKey = player.handleGameAction( UP );
				} else if ((keyState & DOWN_PRESSED) != 0 && !ignoreDirectionKey) {
					ignoreDirectionKey = player.handleGameAction( DOWN );
				} else if ( (keyState & FIRE_PRESSED) != 0 ) {
					this.world.gameOver = true;
				} else {
					ignoreDirectionKey = false;
				}
			} else {
				 ignoreDirectionKey = false;
			}
			this.world.animate( System.currentTimeMillis() - lastCycleTime );
			lastCycleTime = System.currentTimeMillis();
			// paint the whole level:
			this.world.render(this.graphics, 0, 0 );
			
			// redraw the game-screen:
			flushGraphics();
			
			// now check how long we need to sleep:
			long timeForFrame = System.currentTimeMillis() - cycleStartTime;
			if (timeForFrame < TIME_PER_FRAME ) {
				try {
					Thread.sleep( TIME_PER_FRAME - timeForFrame );
				} catch (InterruptedException e) {
					// ignore
				}
			}
		} // while !gameOver
		//#if !polish.vendor.Sony-Ericsson and !polish.api.siemens-color-game-api
			// Sony Ericsson won't switch back to the fullscreen-mode
			setFullScreenMode(false);
		//#endif
		//#if polish.midp2 && polish.vendor.Nokia
			this.addCommand( this.replayCommand );
			this.addCommand( this.returnCommand );
		//#endif
		//setTitle( Locale.get("title.GameOver"));
	}
}
