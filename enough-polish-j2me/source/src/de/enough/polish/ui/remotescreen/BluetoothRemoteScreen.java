//#condition polish.usePolishGui && polish.api.btapi && polish.midp2
/*
 * Created on Oct 30, 2008 at 10:10:13 AM.
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
package de.enough.polish.ui.remotescreen;


import java.io.DataOutputStream;

import javax.bluetooth.L2CAPConnection;

import de.enough.polish.bluetooth.DiscoveryHelper;
import de.enough.polish.bluetooth.L2CapOutputStream;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.RemoteScreen;
import de.enough.polish.util.ZipUtil;

/**
 * <p>Sends screen updates to the remote screen desktop application of J2ME Polish.</p>
 * <p>This can be used for demonstration purposes, for example.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BluetoothRemoteScreen implements RemoteScreen, Runnable
{
	
	private static final int VERSION = 80;

	private static final int MAX_RGB_CHUNK = (32 * 1024) / 3;
	
	private final ScreenUpdate screenUpdate;
	private boolean isConnected;
	private int screenWidth;
	private int screenHeight;
	private int degrees;

	/**
	 * 
	 */
	public BluetoothRemoteScreen()
	{
		this.screenUpdate = new ScreenUpdate();
		(new Thread( this )).start();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RemoteScreen#init(int, int, int)
	 */
	public void init(int width, int height, int degrees)
	{
		this.screenWidth = width;
		this.screenHeight = height;
		this.degrees = degrees;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RemoteScreen#updateScreen(int, int, int, int, int[])
	 */
	public void updateScreen(int x, int y, int width, int height, int[] rgb)
	{
		if (!this.isConnected) {
			return;
		}
		ScreenUpdate update = this.screenUpdate;
		synchronized (update) {
			update.refreshCounter++;
			if (update.refreshCounter == Integer.MAX_VALUE) {
				update.refreshCounter = Integer.MIN_VALUE;
			}
			update.x = x;
			update.y = y;
			update.width = width;
			update.height = height;
			update.rgb = rgb;
			
			update.notify();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		L2CAPConnection con = null;
		try
		{
			boolean storeAndReuseConnectionUrl = true;
			con = (L2CAPConnection) DiscoveryHelper.findAndConnectService("5ab90370a64d11ddad8b0800200c9a66", DiscoveryHelper.SEARCH_MODE_GIAC, DiscoveryHelper.DEVICE_CLASS_MAJOR_PC, storeAndReuseConnectionUrl);
			if (con == null) {
				return;
			}
			if (this.screenWidth == 0) {
				this.screenWidth = Display.getScreenWidth();
				this.screenHeight = Display.getScreenHeight();
			}
			this.isConnected = true;
			L2CapOutputStream l2CapOut = new L2CapOutputStream(con);
			DataOutputStream out = new DataOutputStream( l2CapOut );
			out.writeInt( VERSION );
			out.writeUTF( Display.getInstance().getMidlet().getClass().getName() );
			out.writeInt( this.screenWidth );
			out.writeInt( this.screenHeight );
			out.writeInt( this.degrees );
			out.flush();
			ScreenUpdate update = this.screenUpdate;
			int x, y, width, height;
			int[] rgb;
			byte[] rgbData;
			long lastDuration = 0;
			long transferStart;
			String lastError = null;
			
			while (true) {
				int refreshCounter;
				synchronized (update) {
					refreshCounter = update.refreshCounter;
					x = update.x;
					y = update.y;
					width = update.width;
					height = update.height;
					rgb = update.rgb;
				}
				if (rgb != null) {
					transferStart = System.currentTimeMillis();
					try {
					out.writeUTF( "lastDuration=" + lastDuration + ", error=" + lastError );
					out.writeShort( x );
					out.writeShort( y );
					out.writeShort( width );
					out.writeShort( height );
					int numberOfChunks = rgb.length / MAX_RGB_CHUNK; 
					if (rgb.length % MAX_RGB_CHUNK != 0) {
						numberOfChunks++;
					}
					out.writeShort(numberOfChunks);
					int offset = 0;
					int len = MAX_RGB_CHUNK;
					for (int i=0; i<numberOfChunks; i++) {
						if (offset + len > rgb.length) {
							len = rgb.length - offset;
						}
						rgbData = ZipUtil.convertRgbToByteArray(rgb, offset, len); //ZipUtil.compressRgbArray(rgb, offset, len);
						out.writeShort( rgbData.length );
						out.write( rgbData );
						offset += len;
					}
					//out.writeInt( rgbData.length );
					//out.write(rgbData);
					out.flush();
					} catch (Throwable t) {
						lastError = t.toString();
					}
					lastDuration = System.currentTimeMillis() - transferStart;
					rgbData = null;
				}
				synchronized (update) {
					if (update.refreshCounter == refreshCounter) {
						update.rgb = null;
						update.wait();
					}
				}
			}
		} catch (Exception e)
		{
			//#debug error
			System.out.println("Unable to find or access bluetooth service" + e);
		} finally
		{
			this.isConnected = false;
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	
}

class ScreenUpdate {
	protected int refreshCounter;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected int[] rgb;
}
