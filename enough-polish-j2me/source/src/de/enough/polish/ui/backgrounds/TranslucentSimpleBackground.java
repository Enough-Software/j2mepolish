//#condition polish.usePolishGui
/*
 * Created on 19-Nov-2004 at 18:34:29.
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
package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;
//#if polish.api.nokia-ui
	import com.nokia.mid.ui.DirectGraphics;
	import com.nokia.mid.ui.DirectUtils;
//#endif

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.util.DeviceInfo;
import de.enough.polish.util.DrawUtil;

/**
 * <p>Paints a simple translucent background.</p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        19-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslucentSimpleBackground extends Background {

	private int argbColor;
	//#if polish.api.nokia-ui && !polish.Bugs.TransparencyNotWorkingInNokiaUiApi
		//#define tmp.useNokiaUi
		private transient int[] xCoords;
		private transient int[] yCoords;
	//#elif polish.midp2 && !polish.blackberry
		// int MIDP/2.0 the buffer is always used:
		private transient int[] buffer;
		private transient int lastWidth;
		//#if polish.Bugs.drawRgbNeedsFullBuffer || polish.vendor == Generic
			private int lastHeight;
		//#endif
		//#if polish.vendor == Generic
			private boolean needsFullBuffer = DeviceInfo.requiresFullRgbArrayForDrawRgb();
		//#endif
	//#endif

	/**
	 * Creates a new TranslucentBackground
	 * 
	 * @param argbColor the AARRGGBB color with an alpha-channel between 0 and 255.
	 */
	public TranslucentSimpleBackground( int argbColor ) {
		super();
		this.argbColor = argbColor;
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		//#if polish.android 
			g.setColor( this.argbColor );
			g.fillRect(x, y, width, height);
		//#elif polish.blackberry && polish.usePolishGui
			net.rim.device.api.ui.Graphics bbGraphics = null;
			//# bbGraphics = g.g;
			int alpha = this.argbColor >>> 24;
			bbGraphics.setGlobalAlpha( alpha );
			bbGraphics.setColor( this.argbColor );
			bbGraphics.fillRect(x, y, width, height);
			bbGraphics.setGlobalAlpha( 0xff ); // reset to fully opaque		
		//#elif tmp.useNokiaUi
			DirectGraphics dg = DirectUtils.getDirectGraphics(g);

			if(this.xCoords == null || this.yCoords == null )
			{
				this.xCoords = new int[4];
				this.xCoords[0] = Integer.MIN_VALUE;
				this.yCoords = new int[4];
				this.yCoords[0] = Integer.MIN_VALUE;
			}

			if ( this.xCoords[0] != x || this.xCoords[1] != x + width) {
				this.xCoords[0] = x;
				this.xCoords[1] = x + width;
				this.xCoords[2] = x + width;
				this.xCoords[3] = x;
			}
			if ( this.yCoords[0] != y || this.yCoords[2] != y + height ) {
				this.yCoords[0] = y;
				this.yCoords[1] = y;
				this.yCoords[2] = y + height;
				this.yCoords[3] = y + height;
			}
			dg.fillPolygon( this.xCoords, 0, this.yCoords, 0, 4, this.argbColor );
		//#elifdef polish.midp2
			// on the SE K700 for example the translated origin of the graphics 
			// does not seem to used. Instead the real origin is used:
			//#ifdef polish.Bugs.drawRgbOrigin
				x += g.getTranslateX();
				y += g.getTranslateY();
			//#endif
				
			// check if the buffer needs to be created:
			
			//#if polish.Bugs.drawRgbNeedsFullBuffer || polish.vendor == Generic
				//#if polish.vendor == Generic && !polish.Bugs.drawRgbNeedsFullBuffer
					if (this.needsFullBuffer) {
				//#endif
						if (width != this.lastWidth || height != this.lastHeight) {
							this.lastWidth = width;
							this.lastHeight = height;
							int[] newBuffer = new int[ width * height ];
							for (int i = newBuffer.length - 1; i >= 0 ; i--) {
								newBuffer[i] = this.argbColor;
							}
							this.buffer = newBuffer;
						}
				//#if polish.vendor == Generic && !polish.Bugs.drawRgbNeedsFullBuffer
					}
				//#endif
			//#endif
			//#if !polish.Bugs.drawRgbNeedsFullBuffer
				if (width != this.lastWidth) {
					this.lastWidth = width;
					int[] newBuffer = new int[ width ];
					for (int i = newBuffer.length - 1; i >= 0 ; i--) {
						newBuffer[i] = this.argbColor;
					}
					this.buffer = newBuffer;
				}
			//#endif
			//#if polish.Bugs.drawRgbNeedsFullBuffer || polish.vendor == Generic
				//#if polish.vendor == Generic && !polish.Bugs.drawRgbNeedsFullBuffer
					if (this.needsFullBuffer) {
				//#endif
						DrawUtil.drawRgb( this.buffer, x, y, width, height, true, g );
				//#if polish.vendor == Generic && !polish.Bugs.drawRgbNeedsFullBuffer
						return;
					}
				//#endif
			//#endif
			//#if !polish.Bugs.drawRgbNeedsFullBuffer
				if (x < 0) {
					width += x;
					x = 0;
				}
				if (width <= 0) {
					return;
				}
				if (y < 0) {
					height += y;
					y = 0;
				}
				if (height <= 0) {
					return;
				}
				//#if polish.vendor == Generic
					try {
				//#endif
						g.drawRGB(this.buffer, 0, 0, x, y, width, height, true);
				//#if polish.vendor == Generic
					} catch (Exception e) {
						//#debug error
						System.out.println("problem while rendering RGB array: " + e.toString() + ": " + e.getMessage() );
						e.printStackTrace();
						this.needsFullBuffer = true;
						this.lastWidth = width;
						this.lastHeight = height;
						int[] newBuffer = new int[ width * height ];
						for (int i = newBuffer.length - 1; i >= 0 ; i--) {
							newBuffer[i] = this.argbColor;
						}
						this.buffer = newBuffer;
						DrawUtil.drawRgb( newBuffer, x, y, width, height, true, g );
					}
				//#endif
			//#endif
		//#else
			// ignore alpha-value
			g.setColor( this.argbColor );
			g.fillRect(x, y, width, height);
		//#endif
	}

	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this background.
	 */
	public void releaseResources() {
		//#if !tmp.useNokiaUi && !polish.blackberry && polish.midp2
			// int MIDP/2.0 the buffer is always used:
			this.buffer = null;
			this.lastWidth = 0;
		//#endif
	}
	
	//#if polish.css.animations
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Background#setStyle(de.enough.polish.ui.Style)
		 */
		public void setStyle(Style style)
		{
			//#if polish.css.background-simple-translucent-argb-color
				Color col = style.getColorProperty("background-simple-translucent-argb-color");
				if (col != null) {
					this.argbColor = col.getColor();
				}
			//#endif
		}
	//#endif	
}
