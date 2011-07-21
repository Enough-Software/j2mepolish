//#condition polish.usePolishGui && polish.api.mmapi
/*
 * Created on Nov 8, 2007 at 8:26:29 PM.
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
package de.enough.polish.ui.backgrounds;

import java.io.InputStream;

import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Item;

/**
 * <p>Plays a video in an background.</p>
 * <p>Note that you can use this background only when you target device supports the MMAPI 1.1 or higher.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Nov 8, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class VideoBackground extends Background
{

	private final String url;
	private final String mimeType;
	private final int loopCount;
	private transient Player player;
	private final int anchor;
	private final int xOffset;
	private final int yOffset;
	private final int color;

	/**
	 * @param color 
	 * @param url 
	 * @param mimeType 
	 * @param loopCount the number of times the video is being played, -1 will loop it indefinitely 
	 * @param anchor 
	 * @param xOffset 
	 * @param yOffset 
	 * 
	 */
	public VideoBackground( int color,  String url, String mimeType, int loopCount, int anchor, int xOffset, int yOffset )
	{
		this.color = color;
		this.url = url;
		this.mimeType = mimeType;
		this.loopCount = loopCount;
		this.anchor = anchor;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g)
	{
		if (this.color != Item.TRANSPARENT) {
			g.setColor( this.color );
			g.fillRect( x, y, width, height );
		}
		if (this.player == null) {
			InputStream is = openInputStream();
			if (is == null) {
				//#debug error
				System.out.println("did not find video resource " + this.url);
				return;
			}
	        try
	        {
	            this.player = Manager.createPlayer( is, this.mimeType );
	            this.player.realize();
	            this.player.setLoopCount( this.loopCount );
	            VideoControl control = ( VideoControl ) this.player.getControl( "VideoControl" );
	            if (control == null) {
	            	//#debug error
	            	System.out.println("Unable to retrieve VideoControl");
	            	return;
	            }
	            control.initDisplayMode( VideoControl.USE_DIRECT_VIDEO, Display.getInstance() );
	            int sourceWidth = control.getSourceWidth();
	            int sourceHeight = control.getSourceHeight();
				if ( (this.anchor & Graphics.HCENTER) == Graphics.HCENTER) {
					x += (width >> 1) - (sourceWidth >> 1);
				} else if ( (this.anchor & Graphics.RIGHT) == Graphics.RIGHT) {
					x += width - sourceWidth;
				}
				if ( (this.anchor & Graphics.VCENTER) == Graphics.VCENTER) {
					y += (height >> 1) - (sourceHeight >> 1);
				} else if ( (this.anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
					y += height - sourceHeight;
				}
				x += this.xOffset;
				y += this.yOffset;
				control.setDisplayLocation(x, y);
	            control.setVisible( true );
	            this.player.start();
	            //System.out.println("player started on canvas " + canvas);

	        }
	        catch (Exception e)
	        {
	        	//#debug error
	        	System.out.println("unable to start video " + e);
	        }
		}
	}

	protected InputStream openInputStream() {
		return this.getClass().getResourceAsStream( this.url );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#releaseResources()
	 */
	public void releaseResources()
	{
		super.releaseResources();
		if (this.player != null) {
			try
			{
				this.player.stop();
			} catch (Exception e)
			{
				//#debug error
				System.out.println("unable to stop player");
			}
			this.player.deallocate();
			this.player = null;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#hideNotify()
	 */
	public void hideNotify()
	{
		if (this.player != null) {
			try
			{
				this.player.stop();
			} catch (Exception e)
			{
				//#debug error
				System.out.println("unable to stop player " + e );
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#showNotify()
	 */
	public void showNotify()
	{
		if (this.player != null) {
			try
			{
				VideoControl control = (VideoControl) this.player.getControl("VideoControl");
				if (control == null) {
					//#debug warn
					System.out.println("showNotify: unable to get VideoControl");
					return;
				}
				this.player.start();
			} catch (Exception e)
			{
				//#debug error
				System.out.println("unable to stop player " + e );
			}
		}
	}
	
	
	

}
