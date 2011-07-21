//#condition polish.midp || polish.usePolishGui
/*
 * Created on 08-Nov-2004 at 23:59:52.
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
/*
 * Modified on 14-Feb-2006 by Radu Zah, raduzah@yahoo.com.
 * Added the removeInstance method that will remove the font from cache. 
 * Usefull if you have large fonts and you want to free up some memory.
 */
package de.enough.polish.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Can be used to use any kind of bitmap fonts.</p>
 * <p>
 * If you want to read bitmap fonts not from a resource within the JAR file, you can either call 
 * getInstance( String url, InputStream in ) or you can specify the preprocessing variable
 * <code>polish.classes.BitMapFont.resourceLoader</code>;. This preprocessing variable
 * points to a class that contains the static method <code>getResourceAsStream( String url )</code>.
 * <br />
 * Sample definition: 
 * </p>
 * <pre>
 * 		&lt;variable name=&quot;polish.classes.BitMapFont.resourceLoader&quot; value=&quot;com.company.common.RmsResourceLoader&quot; /&gt;
 * </pre>
 * <p>Sample code:</p>
 * <pre>
 * package com.company.common;
 * import java.io.*;
 * public class RmsResorceLoader {
 *    public static InputStream getResourceAsStream( String url )
 *    throws IOException e 
 *    {
 *    		// TODO implement your solution here
 *    		return null;
 *    }
 * }
 * </pre> 
 *
 * <p>Copyright Enough Software 2004 - 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class BitMapFont {
	private static Hashtable fontsByUrl = new Hashtable();
	
	private String fontUrl;
	private Image fontImage;
	private boolean hasMixedCase;
	private byte[] characterWidths;
	private short[] xPositions;
	private String characterMap;
	private int fontHeight;
	private int spaceIndex;

	/**
	 * Creates a new bitmap font.
	 * 
	 * @param fontUrl the url of the *.bmf file containing the font-specification.
	 */
	private BitMapFont( String fontUrl ) {
		super();
		this.fontUrl = fontUrl;
		//#debug
		System.out.println("Creating bitmap font " + fontUrl );
	}
	
	private void initFont() {
		// try to load the *.bmf file:
		InputStream in = null;
		try {
			//#if polish.classes.BitMapFont.resourceLoader:defined
				//# in = ${polish.classes.BitMapFont.resourceLoader}.getResourceAsStream(this.fontUrl);
			//#else
				in = getClass().getResourceAsStream(this.fontUrl);
			//#endif
			if (in == null && !this.fontUrl.endsWith(".bmf")) {
				this.fontUrl += ".bmf";
				//#if polish.classes.BitMapFont.resourceLoader:defined
					//# in = ${polish.classes.BitMapFont.resourceLoader}.getResourceAsStream(this.fontUrl);
				//#else
					in = getClass().getResourceAsStream(this.fontUrl);
				//#endif
			}
			initFont(in);
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to load bitmap-font [" + this.fontUrl + "]" + e);
		//#ifndef polish.Bugs.ImageIOStreamAutoClose
		} finally {
			if (in != null) {
				try { 
					in.close();
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to close bitmap-font stream" + e);
				}
			}
		//#endif
		}
	}

	/**
	 * Initializes the font with the specified input stream
	 * @param in the input stream
	 * @throws IOException when reading from the input stream fails
	 */
	private void initFont(InputStream in) throws IOException
	{
		if (in == null) {
			throw new IOException();
		}
		DataInputStream dataIn = new DataInputStream( in );
		this.hasMixedCase = dataIn.readBoolean();
		String map = dataIn.readUTF();
		this.characterMap = map;
		this.spaceIndex = map.indexOf(' ');
		int length = map.length();
		this.characterWidths = new byte[ length ];
		this.xPositions = new short[ length ];
		short xPos = 0;
		for (int i = 0; i < length; i++ ) {
			byte width = dataIn.readByte();
			this.characterWidths[i] = width;
			this.xPositions[i] = xPos;
			xPos += width;
		}
		//#if polish.android
		// The method BitmapFactory.decodeStream(stream) resets the stream and thus tries to read the header which is of course not a valid image header.
			byte[] buffer = StreamUtil.readFully(in);
			System.out.print("Image buffer:");
			for(int i = 0; i < 5; i++) {
				System.out.print(buffer[i]+",");
			}
			System.out.println();
			this.fontImage = Image.createImage(buffer, 0, buffer.length);
		//#elif polish.midp2
			this.fontImage = Image.createImage( in );
		//#else
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] pngBuffer = new byte[ 3 * 1024 ];
			int read;
			while ( (read = in.read(pngBuffer, 0, pngBuffer.length)) != -1) {
				out.write(pngBuffer, 0, read );
			}
			pngBuffer = out.toByteArray();
			out = null;
			this.fontImage = Image.createImage(pngBuffer, 0, pngBuffer.length);
		//#endif
		this.fontHeight = this.fontImage.getHeight();
		this.fontUrl = null;
	}
	
	/**
	 * Creates a viewer object for the given text.
	 * 
	 * @param input the input which should be shown.
	 * @return a viewer object which shows the font in a performant manner, null if the bitmap font could not be loaded
	 */
	public BitMapFontViewer getViewer( String input ) {
		return getViewer( input, -1 );
	}
	
	/**
	 * Creates a viewer object for the given text.
	 * 
	 * @param input the input which should be shown.
	 * @param color the desired font color; -1 if the color should be ignored
	 * @return a viewer object which shows the font in a performant manner, null if the bitmap font could not be loaded
	 */
	public BitMapFontViewer getViewer( String input, int color ) {
		if (this.fontImage == null) {
			initFont();
			if (this.fontImage == null) {
				return null;
			}
		}
		//int imageWidth = this.fontImage.getWidth();
		// get the x/y-position and width for each character:
		if (!this.hasMixedCase) {
			input = input.toLowerCase();
		}
		int length = input.length();
		//short[] yPositions = new short[ length ];
		int[] indeces = new int[ length ];
		for (int i = length - 1; i >= 0; i-- ) {
			char inputCharacter = input.charAt(i);
			if (inputCharacter == '\n') {
				indeces[i] = BitMapFontViewer.ABSOLUTE_LINE_BREAK;
			} else {
				indeces[i] = this.characterMap.indexOf( inputCharacter );
			}
		}
		// Colorize font if color is not black.
		if (color != -1 && color != 0)
		{
			return new BitMapFontViewer( this.fontImage, color, indeces, this.xPositions, this.characterWidths, this.fontHeight, this.spaceIndex, 1 );
		}
		else
		{
			return new BitMapFontViewer( this.fontImage, indeces, this.xPositions, this.characterWidths, this.fontHeight, this.spaceIndex, 1 );
		}
	}

	/**
	 * Gets the instance of the specified font.
	 * 
	 * @param url the url of the font
	 * @return the corresponding bitmap font.
	 */
	public static BitMapFont getInstance(String url) {
		return getInstance( url, null);
	}
	
	/**
	 * Gets the instance of the specified font and possibly initializes it..
	 * 
	 * @param url the url of the font
	 * @param in the input stream from which the definition can be read, this can null. If there is an input stram, it will not be closed after reading.
	 * @return the corresponding bitmap font.
	 */
	public static BitMapFont getInstance(String url, InputStream in) {
		BitMapFont font = (BitMapFont) fontsByUrl.get( url );
		String originalUrl = null;
		if (font == null) {
			String nextUrl = url;
			if (url.startsWith("url")) {
				int startIndex = url.indexOf('(');
				int endIndex = url.indexOf(')', startIndex);
				if (startIndex != -1 && endIndex != -1) {
					nextUrl = url.substring(startIndex + 1, endIndex ).trim();
				}
				if (nextUrl.charAt(0) != '/') {
					nextUrl = "/" + nextUrl;
				}
			} else if (url.charAt(0) != '/') {
				nextUrl = "/" + url;
			}
			originalUrl = url;
			url = nextUrl;
			font = new BitMapFont( url );
			fontsByUrl.put( url, font );
			if (originalUrl != null) {
				fontsByUrl.put( originalUrl, font );
			}
			if (in != null) {
				try
				{
					font.initFont( in );
				} catch (IOException e)
				{
					//#debug error
					System.out.println("Unable to initialize bitmap font from input stream" + e);
				}
			}
		}
		return font;
	}
	

	/**
	 * Removes the instance of the specified font from the internal cache.
	 * 
	 * @param url the url of the font
	 */
	public static void removeInstance(String url) {
		fontsByUrl.remove( url );
	}

	/**
	 * Retrieves the width of the given character
	 * @param c the character 
	 * @return -1 if unknown otherwise the width for the given character
	 */
	public int charWidth(char c) {
		if (this.fontImage == null) {
			initFont();
			if (this.fontImage == null) {
				return -1;
			}
		}
		for (int i=0; i<this.characterMap.length(); i++ ) {
			char cm = this.characterMap.charAt(i);
			if (cm == c) {
				return this.characterWidths[i]; 
			}
		}
		return -1;
	}

	/**
	 * Retrieves the width of the given text
	 * @param str the text 
	 * @return -1 if unknown otherwise the width for the given text
	 */
	public int stringWidth(String str) {
		if (this.fontImage == null) {
			initFont();
			if (this.fontImage == null) {
				return -1;
			}
		}
		int width = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			for (int j=0; j<this.characterMap.length(); j++ ) {
				char cm = this.characterMap.charAt(j);
				if (cm == c) {
					width += this.characterWidths[j];
					break;
				}
			}
			
		}
		return width;
	}

	/**
	 * Retrieves the height of this bitmap font
	 * @return the height of the font
	 */
	public int getFontHeight() {
		if (this.fontImage == null) {
			initFont();
			if (this.fontImage == null) {
				return -1;
			}
		}
		return this.fontHeight;
	}

	/**
	 * Draws the specified character using this bitmap font.
	 * 
	 * @param c the character 
	 * @param x horizontal position
	 * @param y vertical position
	 * @param anchor anchor, e.g. Graphics.TOP | Graphics.LEFT
	 * @param g the graphics context
	 */
	public void drawChar(char c, int x, int y, int anchor, Graphics g)
	{
		if (this.fontImage == null) {
			initFont();
			if (this.fontImage == null) {
				return;
			}
		}
		if (!this.hasMixedCase) {
			c = Character.toLowerCase(c);
		}
		int index = this.characterMap.indexOf( c );
		if (index == -1) {
			return;
		}
		if ((anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
			y -= this.fontHeight;
		} else if ((anchor & Graphics.BASELINE) == Graphics.BASELINE) {
			y -= (this.fontHeight * 2) / 3;
		} else if ((anchor & Graphics.VCENTER) == Graphics.VCENTER) {
			y -= this.fontHeight / 2;
		}
		int width = this.characterWidths[index];
		if ((anchor & Graphics.RIGHT) == Graphics.RIGHT) {
			x -= width;
		} else if ((anchor & Graphics.HCENTER) == Graphics.HCENTER) {
			x -= width / 2;
		}
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		g.clipRect( x, y, width, this.fontHeight );
		int imageX = x - this.xPositions[index];
		g.drawImage( this.fontImage, imageX, y, Graphics.TOP | Graphics.LEFT );
		// reset clip:
		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}
}
