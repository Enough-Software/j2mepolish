//#condition polish.usePolishGui

/*
 * Created on 27-May-2005 at 17:14:01.
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
import javax.microedition.lcdui.Image;

/**
 * <p>Paints a transition of two screens for a nice effect.</p>
 * <p>Using a screen change animation is easy:
 *    <br />
 *    Use the <code>screen-change-animation</code> CSS attribute for specifying which
 *    animation you would like to have. You can also finetune some animations. Note
 *    that some animations have certain conditions like support of the MIDP 2.0 profile.
 * <pre>
 * screen-change-animation: left;
 * left-screen-change-animation-speed: 5;
 * </pre>
 * </p>
 * <p>You can easily implement your own screen change animations by following these
 *    steps:
 * </p>
 * <ol>
 * 	<li>Extend de.enough.polish.ui.ScreenChangeAnimation</li>
 *  <li>Implement the animate() method for doing the animation, use the fields lastCanvasImage 
 *      and nextCanvasImage for your manipulation and consider the isForwardAnimation field.</li>
 *  <li>Implement the paintAnimation(Graphics) method.</li>
 *  <li>Override the show() method if you need to get parameters from the style.</li>
 *  <li>In case you want to manipulate the RGB data, you should set the useNextCanvasRgb and/or useLastCanvasRgb fields
 *  to true - you can then access the nextCanvasRgb and lastCanvasRgb fields for manupulating the data. 
 *  </li>
 * </ol>
 * <p>You can now use your animation by specifying the <code>screen-change-animation</code> CSS attribute 
 *    accordingly:
 * <pre>
 * screen-change-animation: new com.company.ui.MyScreenChangeAnimation();
 * </pre>
 * </p>
 * <p>You can also ease the usage by registering your animation in ${polish.home}/custom-css-attributes:
 * <pre>
 * &lt;attribute name=&quot;screen-change-animation&quot;&gt;
 * 		&lt;mapping from=&quot;myanimation&quot; to=&quot;com.company.ui.MyScreenChangeAnimation()&quot; /&gt;
 * &lt;/attribute&gt;
 * </pre>
 * </p>
 * <p>Now your animation is easier to use:
 * <pre>
 * screen-change-animation: myanimation;
 * </pre>
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        27-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see #onShow(Style, Display, int, int, Displayable, Displayable, boolean)
 * @see #animate()
 */
public abstract class ScreenChangeAnimation
extends Canvas
implements Runnable
{
	//#if (polish.useFullScreen || polish.useMenuFullScreen)
		//#define tmp.useFullScreen
	//#endif
	protected Display display;
	protected Canvas nextCanvas;
	protected Image lastCanvasImage;
	protected int[] lastCanvasRgb;
	/** set to true in subclasses for populating lastCanvasRgb */
	protected boolean useLastCanvasRgb;
	protected Image nextCanvasImage;
	protected int[] nextCanvasRgb;
	/** set to true in subclasses for populating nextCanvasRgb */
	protected boolean useNextCanvasRgb;
	protected int screenWidth;
	protected int screenHeight;
	protected Displayable lastDisplayable;
	protected Displayable nextDisplayable;
	protected boolean isForwardAnimation;
	protected int nextContentX;
	protected int nextContentY;
	protected int lastContentX;
	protected int lastContentY;
	protected boolean supportsDifferentScreenSizes;
	protected boolean abort = false;
	

	/**
	 * Creates a new ScreenChangeAnimation.
	 * All subclasses need to implement the default constructor.
	 */
	public ScreenChangeAnimation() {
		// default constructor
		//#if tmp.useFullScreen
			setFullScreenMode(true);
		//#endif
	}
	
	/**
	 * Starts the animation.
	 * Please note that an animation can be re-used for several screens.
	 * 
	 * @param style the associated style.
	 * @param dsplay the display, which is used for setting this animation
	 * @param width the screen's width
	 * @param height the screen's height
	 * @param lstDisplayable the screen that has been shown until now
	 * @param nxtDisplayable the next screen that should be displayed when this animation finishes (as a Displayable)
	 * @param isForward true when the animation should run in the normal direction/mode - false if it should run backwards
	 */
	protected void onShow( Style style, Display dsplay, final int width, final int height, Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward ) {
		// reset the abort flag
		this.abort = false;
		
		this.screenWidth = width;
		this.screenHeight = height;
		this.display = dsplay;
		this.nextCanvas = (Canvas) nxtDisplayable;
		this.lastDisplayable = lstDisplayable;
		this.nextDisplayable = nxtDisplayable;
		Screen lastScreen = (Screen) (lstDisplayable instanceof Screen ? lstDisplayable : null);
		Screen nextScreen = (Screen) (nxtDisplayable instanceof Screen ? nxtDisplayable : null);
		
		Image lastScreenImage = toImage( lstDisplayable, nextScreen, lastScreen, width, height);
		Image nextScreenImage = toImage( nxtDisplayable, nextScreen, lastScreen, width, height);
		//#if polish.css.repaint-previous-screen
			if (this.supportsDifferentScreenSizes) {
				if (lastScreen != null && nextScreen != null && nextScreen.container != null && nextScreen.style != null) {
					Boolean limitToContentBool = nextScreen.style.getBooleanProperty("repaint-previous-screen");
					if (limitToContentBool != null && limitToContentBool.booleanValue()) {
						// paint title and menubar on the previous screen's image,
						//TODO this should be configurable
						Graphics g = lastScreenImage.getGraphics();
						//#if !polish.Bugs.noTranslucencyWithDrawRgb
							if (nextScreen.previousScreenOverlayBackground != null) {
								nextScreen.previousScreenOverlayBackground.paint( 0, 0, width, height, g);
							}
						//#endif
						nextScreen.paintMenuBar(g);
					}
				}
				//#if !polish.Bugs.noTranslucencyWithDrawRgb
					if (lastScreen != null && nextScreen != null && lastScreen.container != null && lastScreen.style != null) {
						Boolean limitToContentBool = lastScreen.style.getBooleanProperty("repaint-previous-screen");
						if (limitToContentBool != null && limitToContentBool.booleanValue()) {
							Graphics g = nextScreenImage.getGraphics();
							if (lastScreen.previousScreenOverlayBackground != null) {
								lastScreen.previousScreenOverlayBackground.paint( 0, 0, width, nextScreen.contentY + nextScreen.contentHeight, g);
							}
						}
					}
				//#endif
			}
		//#endif

		//#debug
		System.out.println("ScreenAnimation: showing screen transition " + this + " for transition from " + lstDisplayable + " to " + nxtDisplayable);
		
		this.lastCanvasImage = lastScreenImage;
		if (this.useLastCanvasRgb) {
			int lstWidth = lastScreenImage.getWidth();
			int lstHeight = lastScreenImage.getHeight();
			this.lastCanvasRgb = new int[ lstWidth * lstHeight ];
			//#if polish.midp2
				lastScreenImage.getRGB(this.lastCanvasRgb, 0, lstWidth, 0, 0, lstWidth, lstHeight );
			//#endif
		}
		
		this.nextCanvasImage = nextScreenImage;
		if (this.useNextCanvasRgb) {
			int nxtWidth = nextScreenImage.getWidth();
			int nxtHeight = nextScreenImage.getHeight();
			this.nextCanvasRgb = new int[ nxtWidth * nxtHeight ];
			//#if polish.midp2
				nextScreenImage.getRGB(this.nextCanvasRgb, 0, nxtWidth, 0, 0, nxtWidth, nxtHeight );
			//#endif
		}
		this.isForwardAnimation = isForward;
		setStyle( style );
	}
	
	protected Image toImage(Displayable displayable, Screen nextScreen, Screen lastScreen, int width, int height) {
		boolean isLastScreen = (displayable == lastScreen);
		Screen screen = isLastScreen ? lastScreen : nextScreen;
		Image screenImage = null;
		if (!isLastScreen && displayable instanceof Canvas) {
			((Canvas)displayable).showNotify();
		}
		//#if polish.css.repaint-previous-screen
			boolean limitToContent = false;
			int contentX = 0;
			int contentY = 0;
			if (this.supportsDifferentScreenSizes && screen != null && screen.container != null && screen.style != null) {
				Boolean limitToContentBool = screen.style.getBooleanProperty("repaint-previous-screen");
				if (limitToContentBool != null) {
					limitToContent = limitToContentBool.booleanValue();
					if (limitToContent) {
						width = screen.container.itemWidth;
						height = Math.min( screen.contentHeight, screen.container.itemHeight );
						Border border = screen.border;
						if (border != null) {
							width += border.borderWidthLeft + border.borderWidthRight;
							height += border.borderWidthTop + border.borderWidthBottom;
							contentX = border.borderWidthLeft;
							contentY = border.borderWidthTop;
						}
						Item title = screen.getTitleItem();
						if (title != null) {
							height += title.itemHeight;
							contentY += title.itemHeight;
							if (title.itemWidth > width) {
 								width = title.itemWidth;
							}
						}
						// this creates an unmutable image and cannot be used:
//						//#if polish.midp2
//							int[] rgb = new int[ screenWidth * screenHeight ];
//							screenImage = Image.createRGBImage(rgb, screenWidth, screenHeight, true );
//						//#endif
					}
				}
			}
		//#endif
		screenImage = Image.createImage(width, height);
		Graphics g = screenImage.getGraphics(); 
		g.setClip(0, 0, width, height);
		if ( displayable instanceof Canvas) {
			//#debug
			System.out.println("StyleSheet: last screen is painted");
			//#if polish.css.repaint-previous-screen
				if (limitToContent) {
					g.translate( -(screen.container.relativeX - contentX), -(screen.container.relativeY - contentY) );
					screen.paintBackgroundAndBorder(g);
					screen.paintTitleAndSubtitle(g);
					if (isLastScreen) {
						this.lastContentX = -g.getTranslateX();
						this.lastContentY = -g.getTranslateY();
					} else {
						this.nextContentX = -g.getTranslateX();
						this.nextContentY = -g.getTranslateY();
					}
					g.translate( -g.getTranslateX(), -g.getTranslateY() );
					screen.container.paint(contentX, contentY, contentX,  width, g);
				} else {
			//#endif
					if (isLastScreen) {	
						this.lastContentX = 0;
						this.lastContentY = 0;
					} else {
						this.nextContentX = 0;
						this.nextContentY = 0;						
					}
					//#if polish.blackberry && polish.hasPointerEvents
						if (screen != null && screen.getScreenFullHeight() < height) {
							if (screen.background != null) {
								screen.background.paint(0, 0, width, height, g );
							} else {
								g.fillRect( 0, 0, width, height );
							}
						}
					//#endif
					((Canvas)displayable).paint( g );				
			//#if polish.css.repaint-previous-screen
				}
			//#endif
	//#if polish.ScreenChangeAnimation.blankColor:defined
		} else {
			//#= g.setColor( ${polish.ScreenChangeAnimation.blankColor} );
			g.fillRect( 0, 0, width, height );
	//#endif
		}
		return screenImage;
	}

	/**
	 * Sets the style for this animation.
	 * Subclasses can override this for adapting to different design settings.
	 * 
	 * @param style the style
	 */
	protected void setStyle(Style style)
	{
		// let subclasses override this
	}

	/**
	 * Animates this animation.
	 * 
	 * @return true when the animation should continue, when false is returned the animation
	 *         will be stopped and the next screen will be shown instead.
	 */
	protected abstract boolean animate();
	
	/**
	 * Paints the animation.
	 * 
	 * @param g the graphics context
	 */
	protected abstract void paintAnimation( Graphics g );
	
	public final void paint( Graphics g ) {
		try {			
			if (this.nextCanvasImage != null) {
				paintAnimation( g );
				this.display.callSerially( this );
			}	
		} catch (Throwable e) {
			//#debug error
			System.out.println("Unable to paint animation" + e );
		}
	}

	
	
	//#if polish.hasPointerEvents
	/**
	 * Forwards pointer pressed events to the next screen.
	 * 
	 * @param x the horizontal coordinate of the clicked pixel
	 * @param y the vertical coordinate of the clicked pixel
	 * @see #updateNextScreen(Canvas, Image, int[])
	 */
	public boolean _pointerPressed( int x, int y ) {
		boolean handled = false;
		Canvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		if (next != null) {
			handled = next._pointerPressed( x, y );
			updateNextScreen(next, nextImage, this.nextCanvasRgb);
		}
		return handled;
	}
	//#endif
	
	//#if polish.hasPointerEvents
	/**
	 * Forwards pointer pressed events to the next screen.
	 * 
	 * @param x the horizontal coordinate of the clicked pixel
	 * @param y the vertical coordinate of the clicked pixel
	 * @see #updateNextScreen(Canvas, Image, int[])
	 */
	public boolean _pointerReleased( int x, int y ) {
		boolean handled = false;
		Canvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		if (next != null) {
			handled = next._pointerReleased( x, y );
			updateNextScreen(next, nextImage, this.nextCanvasRgb);
		}
		return handled;
	}
	//#endif
	
	//#if polish.hasPointerEvents
	/**
	 * Forwards pointer dragged events to the next screen.
	 * 
	 * @param x the horizontal coordinate of the clicked pixel
	 * @param y the vertical coordinate of the clicked pixel
	 * @see #updateNextScreen(Canvas, Image, int[])
	 */
	public boolean _pointerDragged( int x, int y ) {
		boolean handled = false;
		Canvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		if (next != null) {
			handled = next._pointerDragged( x, y );
			updateNextScreen(next, nextImage, this.nextCanvasRgb);
		}
		return handled;
	}
	//#endif
	
	
	/**
	 * Notifies this animation that it will be shown shortly.
	 * The default implementation switches into fullscreen mode
	 */
	public void showNotify() {
		//#if tmp.useFullScreen
			setFullScreenMode(true);
		//#endif
	}
	
	/**
	 * Notifies this animation that it will be hidden shortly.
	 * This is ignored by the default implementation.
	 */
	public void hideNotify() {
		// ignore
	}
	
	/**
	 * Notifies this animation that the screen space has been changed.
	 * This is ignored by the default implementation.
	 * 
	 * @param width the width
	 * @param height the height
	 */
	public void sizeChanged( int width, int height ) {
		// ignore
	}

	/**
	 * Handles key repeat events.
	 * The implementation forwards this event to the next screen and then updates the nextCanvasImage field.
	 * 
	 * @param keyCode the code of the key
	 * @see #nextCanvasImage
	 * @see #updateNextScreen(Canvas, Image, int[])
	 */
	public boolean _keyRepeated( int keyCode ) {
		boolean handled = false;
		Canvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		try {
			if (next != null) {
				handled = next._keyRepeated( keyCode );
				updateNextScreen( next, nextImage, this.nextCanvasRgb );
			}
		} catch (Exception e) {
			//#debug error
			System.out.println("Error while handling keyRepeated event" + e );
		}
		return handled;
	}

	/**
	 * Handles key released events.
	 * The implementation forwards this event to the next screen and then updates the nextCanvasImage field.
	 * 
	 * @param keyCode the code of the key
	 * @see #nextCanvasImage
	 * @see #updateNextScreen(Canvas, Image, int[])
	 */
	public boolean _keyReleased( int keyCode ) {
		boolean handled = false;
		Canvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		try {
			if (next != null) {
				handled = next._keyReleased( keyCode );
				updateNextScreen( next, nextImage, this.nextCanvasRgb );
			}
		} catch (Exception e) {
			//#debug error
			System.out.println("Error while handling keyReleased event" + e );
		}
		return handled;
	}

	/**
	 * Handles key pressed events.
	 * The implementation forwards this event to the next screen and then updates the nextCanvasImage field.
	 * 
	 * @param keyCode the code of the key
	 * @see #nextCanvasImage
	 * @see #updateNextScreen(Canvas, Image, int[])
	 */
	public boolean _keyPressed( int keyCode ) {
		boolean handled = false;
		Canvas next = this.nextCanvas;
		Image nextImage = this.nextCanvasImage;
		try {
			if (next != null) {
				handled = next._keyPressed( keyCode );
				updateNextScreen( next, nextImage, this.nextCanvasRgb );
			}
		} catch (Exception e) {
			//#debug error
			System.out.println("Error while handling keyPressed event" + e );
		}
		return handled;
	}
	
	
	/**
	 * Updates the image and possibly the RGB data of the next screen.
	 * 
	 * @param next the next screen
	 * @param nextImage the image to which the screen should be painted
	 * @param rgb the RGB data, can be null
	 */
	protected void updateNextScreen( Canvas next, Image nextImage, int[] rgb ) {
		//#if polish.css.repaint-previous-screen
			boolean isFullScreen = true;
			if (this.supportsDifferentScreenSizes) {
				Screen nextScreen = (next instanceof Screen ? (Screen)next : null );
				if (nextScreen != null && nextScreen.style != null) {
					Boolean repaintPrevScreen = nextScreen.style.getBooleanProperty("repaint-previous-screen");
					if (repaintPrevScreen != null && repaintPrevScreen.booleanValue()) {
						this.nextCanvasImage = toImage( next, nextScreen, null, this.screenWidth, this.screenHeight );
						isFullScreen = false;
					}
				}
			}
			if (isFullScreen) {
		//#endif
				Graphics g = nextImage.getGraphics();
				next.paint( g );
		//#if polish.css.repaint-previous-screen
			}
		//#endif
		//#if polish.midp2
			if (rgb != null) {
				nextImage.getRGB(rgb, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
			}
		//#endif
	}
	
	/**
	 * Aborts this screen change animation by setting the abort flag
	 * which is checked for in the run() method. 
	 */
	public void abort()
	{
		this.abort = true;
	}
	
	/**
	 * Runs this animation - subclasses need to ensure to call this.display.callSerially( this ); at the end of the paint method.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			if (this.nextCanvas != null && animate() && !this.abort) {
				repaint();
			} else {
				//#debug
				System.out.println("ScreenChangeAnimation: setting next screen " + this.nextDisplayable);
				this.lastCanvasImage = null;
				this.lastCanvasRgb = null;
				this.nextCanvasImage = null;
				this.nextCanvasRgb = null;
				this.nextCanvas = null;
				Display disp = this.display;
				this.display = null;
				Displayable next = this.nextDisplayable;
				this.nextDisplayable = null;
				//#if !polish.blackberry
					System.gc();
				//#endif
				if (disp != null && !this.abort) {
					// checking out if the animation is still shown is sweet in theory but it fails when there are security dialogs and the like in the way..
					//Displayable current = disp.getCurrent();
					//if (current == this && next != null) {
					if (next != null) {
						disp.setCurrent( next );
					}
				}
			}
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to animate" + e);
			Display disp = this.display;
			Displayable next = this.nextDisplayable;
			if (disp != null && next != null) {
				disp.setCurrent( next );
			}
		}
	}

}
