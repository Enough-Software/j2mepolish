/*
 * Created on May 20, 2008 at 2:37:28 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * <p>Represents a bitmap font</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BitMapFont
{
	private final String text;
	private final Font font;
	private final boolean useAntiAlising;
	private final int characterSpacing;
	private final Color color;
	private final int spaceCharWidth;
	
	private BufferedImage resultImage;
	private int[] resultCharacterWidths;

	/**
	 * Creates a new bitmap font
	 * @param text the text
	 * @param font the font
	 * @param useAntiAlising true when anti alising should be used
	 * @param characterSpacing optional spacing between chars
	 * @param spaceCharWidth the width of the space character
	 * @param color the color of the text
	 */
	public BitMapFont( String text, Font font, boolean useAntiAlising, int characterSpacing, int spaceCharWidth, Color color ){
		this.text = text;
		this.font = font;
		this.useAntiAlising = useAntiAlising;
		this.characterSpacing = characterSpacing;
		this.spaceCharWidth = spaceCharWidth;
		this.color = color;
		render();
	}
	
	private void render() {
		
		if (this.text.length() == 0) {
			return;
		}
		
		// use dummy buffer for get a render context:
		BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		if (this.useAntiAlising) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		FontRenderContext fc = g.getFontRenderContext();
		
		// Rectangle2D bounds = this.derivedFont.getStringBounds(text,fc);
		// double height = bounds.getHeight();
		// double width = bounds.getWidth() /*+ (text.length() * this.characterSpacing)*/;
		//
		// Vorpalware/20060421F
		// 
		// Java docs say that Font.getStringBounds() can't be counted on to give the
		// real pixel bounding box of the rendered text.  For instance, Font.getStringBounds() 
		// seems to return an unrealistically low value for the height when certain, 
		// seemingly arbitrary Unicode chars appear in the string, like the Dagger \u2020; 
		// as in 2.51 instead of 14.58 -- way off.
		//
		// The docs' recommended way of finding the bounding box is something like
		// the following:
		//
		TextLayout fulltl = new TextLayout(this.text, this.font, fc);
		Rectangle2D bounds = fulltl.getBounds();
		int height = (int) Math.ceil(bounds.getHeight());
		int y = (int) Math.ceil(-bounds.getY());
		// int width = (int) Math.ceil(bounds.getWidth()) + (text.length() * this.characterSpacing);
		//
		// When it comes to the width: given the potential for rounding differences between
		// TextLayout.getBounds() and the way we must place characters below (must always 
		// round up if a single character has a fractional width), here's a sure way to 
		// make sure the image is wide enough for all of the characters:
		//
		int width=0;
		String onechar;
		this.resultCharacterWidths = new int[ this.text.length() ];
		for (int i = 0; i < this.text.length(); i++) {
			// bounds = this.derivedFont.getStringBounds(characters, i, i+1, fc);
			onechar = this.text.substring(i,i+1);
			TextLayout tl = new TextLayout( onechar, this.font, fc);
			bounds = tl.getBounds();
			
			int minx = (int) Math.floor(bounds.getMinX());
			if (minx < 0) {
				width -= (int) Math.floor(bounds.getMinX());
			}
			
			bounds = this.font.getStringBounds(onechar, fc);
			// int minx = (int) Math.floor(bounds.getMinX());
			// int charwidth;
			// if (minx < 0) 
				// charwidth = (int) Math.ceil(bounds.getMaxX()) - minx;
			// else
				// charwidth = (int) Math.ceil(bounds.getMaxX());
			int charwidth = (int) Math.ceil(bounds.getMaxX());
			if (charwidth == 0) {
				charwidth = this.spaceCharWidth;
			}
			width += charwidth + this.characterSpacing;
			this.resultCharacterWidths[i] = charwidth + this.characterSpacing;
		}
		
		image = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR);
		g = image.createGraphics();
		Color transparent = new Color( 1, 1, 1, 0);
		g.setBackground( transparent );
		g.clearRect(0, 0, width, height );
		if (this.useAntiAlising) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		g.setFont( this.font );
		g.setColor( this.color );

		int x = 0;
		// char[] characters = text.toCharArray();
		for (int i = 0; i < this.text.length(); i++) {
			
			onechar = this.text.substring(i,i+1);
//			TextLayout tl = new TextLayout( onechar, this.font, fc);
//			bounds = tl.getBounds();
			// int oldx = x;
			
//			// For testing - mark character's origin point
//			Color speck = new Color( 255, 255, 0, 255 );
//			g.setColor( speck );
//			g.drawLine( x,y,x,y );
//			g.setColor( this.currentColor );
			
//			int minx = (int) Math.floor(bounds.getMinX());
//			if (minx < 0) {
//				x -= (int) Math.floor(bounds.getMinX());
//			}
				// Letters like j, p, etc. can hook to the left of their origin point, 
				// especially in italic fonts.  Pre-adjust position to make sure the glyph
				// remains inside the box.
			
			g.drawString( onechar, x, y );
			
//			bounds = this.font.getStringBounds(onechar, fc);
//				// Font's idea of character width gives prettier results than strict pixel bounding box
//				// returned by TextLayout
//			
//			// x += (int) Math.ceil(bounds.width());
//			int charwidth = (int) Math.ceil(bounds.getMaxX());
//			if (charwidth == 0) {
//				// space characters have zero width according to TextLayout.getBounds()
//				charwidth = this.spaceCharWidth;
//			}
			x += this.resultCharacterWidths[i]; // charwidth + this.characterSpacing; 
		}
		this.resultImage = image;
		
	}
	

	/**
	 * Retrieves the result image.
	 * @return the text rendered in the image
	 */
	public BufferedImage getImage() {
		return this.resultImage;
	}
	
	/**
	 * Sets the image from an external source
	 * @param image the image
	 */
	public void setImage( BufferedImage image ) {
		this.resultImage = image;
	}
	
	/**
	 * Retrieves the widths of each character
	 * @return the width of each character of the text
	 */
	public int[] getCharacterWidths() {
		return this.resultCharacterWidths;
	}
	
	/**
	 * Retrieves the text managesd by this font 
	 * @return the text
	 */
	public String getCharacterMap() {
		return this.text;
	}
	

	/**
	 * Determines if this font used mixed case
	 * @return true when it used mixed case
	 */
	public boolean hasMixedCase()
	{
		return hasMixedCase( this.text );
	}
	
	private boolean hasMixedCase( String map ) {
		char[] characters = map.toCharArray();
		for (int i = 0; i < characters.length; i++) {
			char c = characters[i];
			if (Character.isLetter(c)) {
				boolean isLowerCase = Character.isLowerCase(c);
				char searchC = isLowerCase ? Character.toUpperCase(c) : Character.toLowerCase(c);
				for (int j=i + 1; j<characters.length; j++) {
					char d = characters[j];
					if (d == searchC) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	
	/**
	 * Writes this font to the specified stream
	 * @param out the output stream
	 * @throws IOException when writing fails
	 */
	public void write( DataOutputStream out )
	throws IOException 
	{
		if (this.resultImage == null) {
			throw new IOException("Unable to render font");
		}
		boolean hasMixedCase = hasMixedCase();
		out.writeBoolean( hasMixedCase );
		String charMap = this.text;
		if (!hasMixedCase) {
			charMap = this.text.toLowerCase();
		}
		out.writeUTF(charMap);
		for (int i=0; i < this.resultCharacterWidths.length; i++) {
			out.writeByte( this.resultCharacterWidths[i] );
		}
		ImageIO.write( this.resultImage, "png", out );
		out.close();
	}
	
	/**
	 * Writes this font to the specified stream
	 * @param out the output stream
	 * @throws IOException when writing fails
	 */
	public void write( OutputStream out ) throws IOException {
		DataOutputStream dataOut = new DataOutputStream( out );
		write( dataOut );
		out.close();
	}
	
	/**
	 * Writes this font to the specified file
	 * @param file the file
	 * @throws IOException when writing fails
	 */
	public void write( File file ) throws IOException {
		write( new FileOutputStream(file)  );
	}

 }
