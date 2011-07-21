/*
 * Created on Jun 14, 2006 at 6:37:31 PM.
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
package de.enough.polish.util;

//#if polish.midp || polish.usePolishGui
	import javax.microedition.lcdui.Graphics;
	import javax.microedition.lcdui.Image;
//#endif

/**
 * <p>Allows to manipulate image data.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Jun 14, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RgbImage {
	
	private int[] rgbData;
	private int width;
	private int height;
	private boolean processTransparency;
	
	/**
	 * Creates a new transparent RGB image
	 * @param width the width
	 * @param height the height
	 */
	public RgbImage(int width, int height)
	{
		this( width, height, 0, true );
	}

	
	/**
	 * Creates a new empty RGB image.
	 * 
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param color the ARGB background color of the image, 0 gives a transparent image, 0xFF000000 a black one, 0xFFFFFFFF a white one, etc
	 */
	public RgbImage( int width, int height, int color ) {
		this( width, height, color, true );
	}
	
	/**
	 * Creates a new empty RGB image.
	 * 
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param color the (A)RGB background color of the image, 0 gives a transparent image, 0xFF000000 a black one, 0xFFFFFFFF a white one, etc
	 * @param processTransparency true when the image contains transparent or translucent pixels
	 */
	public RgbImage( int width, int height, int color, boolean processTransparency ) {
		int[] data = new int[ width * height ];
		this.width = width;
		this.height = height;
		if ( (color & 0xFF000000) != 0) {
			for (int i = 0; i < width * height; i++) {
				data[i] = color;
			}
		}
		this.rgbData = data;
		this.processTransparency = processTransparency;
	}
	
	/**
	 * Creates a new transparent RGB image
	 * @param source the source RGB image
	 */
	public RgbImage(RgbImage source)
	{
		setRgbData( source.copyRgbData(), source.width, source.processTransparency );
	}

	/**
	 * Creates a new RGB image.
	 * 
	 * @param rgbData The RGB data array
	 * @param width the width of the image (width of a single row)
	 */
	public RgbImage( int[] rgbData, int width ) {
		this(rgbData, width, true );
	}

	/**
	 * Creates a new RGB image.
	 * 
	 * @param rgbData The RGB data array
	 * @param width the width of the image (width of a single row)
	 * @param processTransparency true when the image contains transparent or translucent pixels
	 */
	public RgbImage( int[] rgbData, int width, boolean processTransparency ) {
		setRgbData(rgbData, width, processTransparency );
	}
	
	//#if polish.midp || polish.usePolishGui
	/**
	 * Creates a new RGB image. WARNING: The extraction of RGB data can only succeed on MIDP 2.0 devices
	 * 
	 * @param image the image
	 * @param processTransparency true when the image contains transparent or translucent pixels
	 */
	public RgbImage( Image image, boolean processTransparency ) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] data = new int[ w * h ];
		//#if polish.midp2
			image.getRGB(data, 0, w, 0, 0, w, h );
		//#endif
		setRgbData(data, w, processTransparency);
	}
	//#endif
	
	//#if polish.usePolishGui && !polish.android && polish.midp2
	/**
	 * Creates a new RGB image. WARNING: The extraction of RGB data can only succeed on MIDP 2.0 devices
	 * 
	 * @param image the image
	 * @param processTransparency true when the image contains transparent or translucent pixels
	 */
	public RgbImage( de.enough.polish.ui.Image image, boolean processTransparency ) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] data = new int[ w * h ];
		image.getRGB(data, 0, w, 0, 0, w, h );
		setRgbData(data, w, processTransparency);
	}
	//#endif

	
	/**
	 * Retrieves the height of this image
	 * 
	 * @return the height of this image
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Retrieves the width of this image
	 * 
	 * @return the width of this image
	 */
	public int getWidth() {
		return this.width;
	}
	

	/**
	 * Determines whether transparency is processsed
	 * 
	 * @return true when there are transparent or translucent pixels
	 */
	public boolean isProcessTransparency() {
		return this.processTransparency;
	}

	/**
	 * Sets whether transparency is processsed
	 * 
	 * @param processTransparency true when there are transparent or translucent pixels
	 */
	public void setProcessTransparency(boolean processTransparency) {
		this.processTransparency = processTransparency;
	}

	/**
	 * Retrieves the original (A)RGB data for processing.
	 * 
	 * @return the original (A)RGB data for processing
	 */
	public int[] getRgbData() {
		return this.rgbData;
	}
	
	/**
	 * Returns a copy of the original RGB data for processing
	 * @return the RGB data in a new int[] array
	 */
	public int[] copyRgbData()
	{
		int[] copy = new int[ this.rgbData.length ];
		System.arraycopy( this.rgbData, 0, copy, 0, copy.length );
		return copy;
	}


	/**
	 * Sets a new (A)RGB data array.
	 * 
	 * @param rgbData The new RGB data array
	 * @param width the length of a single row of the RGB data array
	 */
	public void setRgbData(int[] rgbData, int width) {
		setRgbData( rgbData, width, this.processTransparency );
	}
	

	/**
	 * Sets a new (A)RGB data array.
	 * 
	 * @param rgbData The new RGB data array
	 * @param width the length of a single row of the RGB data array
	 * @param processTransparency true when the image contains transparent or translucent pixels
	 */
	public void setRgbData(int[] rgbData, int width, boolean processTransparency ) {
		this.rgbData = rgbData;
		this.width = width;
		this.height = rgbData.length / width;
		this.processTransparency = processTransparency;
	}
	
	//#if polish.midp || polish.usePolishGui
	/**
	 * Renders this RGB image on MIDP 2.0 devices to the given Graphics context.
	 * 
	 * @param x the horizontal/left start position
	 * @param y the vertical/top start position
	 * @param g the graphics context
	 */
	public void paint( int x, int y, Graphics g ) {
		//#if polish.midp2
			DrawUtil.drawRgb( this.rgbData, x, y, this.width, this.height, this.processTransparency, g );
		//#endif
	}
	//#endif

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Fills this image with the given color.
	 * 
	 * @param color the color for all pixels
	 */
	public void fill(int color) {
		for (int i = 0; i < this.rgbData.length; i++) {
			this.rgbData[i] = color;
		}
	}
	
}
