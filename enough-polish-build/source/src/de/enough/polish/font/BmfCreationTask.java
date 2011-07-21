/*
 * Created on Feb 1, 2008 at 12:46:47 AM.
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
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import de.enough.polish.preprocess.css.ColorConverter;


/**
 * Creates bitmapfonts without the UI tool.
 * 
 * @author vera
 */
public class BmfCreationTask extends Task
{
	private static final String TOKEN = ":";

	private String stringToConvert;
	
	private Color attributeColor;
	private boolean attributeAntialiased;
	private int attributeCharacterSpacing;
	private File attributInputTtfFile;
	private File attributOutputBmfFile;
	private int attributeSize;
	
	public void addText(String string)
	{
		this.stringToConvert = string;
	}
	
	/**
	 * Sets the color of the bitmap font
     * @param color color definition of String of format RRR:GGG:BBB:AAA
     */
	public void setColor(String color)
	{
		if (color.indexOf(':') != -1) {
		
			String[] split = color.split(TOKEN);
	
	        if(split.length != 4) {
	            this.attributeColor = Color.BLACK;
	            return;
	        }
	
	        int red = Integer.parseInt(split[0]);
	        int green = Integer.parseInt(split[1]);
	        int blue = Integer.parseInt(split[2]);
	        int alpha = Integer.parseInt(split[3]);
	
	        this.attributeColor = new Color(red, green, blue, alpha);
		} else {
			ColorConverter converter = new ColorConverter();
			long colorL = Long.parseLong(converter.parseColor(color).substring(2), 16 );
			int alpha = (int) ((colorL >>> 24));
			int red = (int) ((colorL >>> 16) & 0x00ff);
			int green = (int) ((colorL >>> 8) & 0x0000ff);
			int blue = (int) ( colorL & 0x000000ff);
			if (alpha == 0) {
				alpha = 255;
			}
			this.attributeColor = new Color( red, green, blue, alpha );
		}
	}

	public void setAntialiased(boolean antialiased)
	{
		this.attributeAntialiased = antialiased;
	}

	public void setCharacterSpacing(int characterSpacing)
	{
		this.attributeCharacterSpacing = characterSpacing;
	}
	
	public void setInput(File input)
	{
		attributInputTtfFile = input;
	}
	
	public void setOutput(File output)
	{
		attributOutputBmfFile = output;
	}
	
	public void setSize(int size)
	{
		attributeSize = size;
	}

	public void execute() throws BuildException
	{
		Font ttfFont;
		try
		{
			ttfFont = Font.createFont(Font.TRUETYPE_FONT, this.attributInputTtfFile).deriveFont((float)this.attributeSize);
			BitMapFont bitMapFont = new BitMapFont( this.stringToConvert, ttfFont, this.attributeAntialiased, this.attributeCharacterSpacing, this.attributeSize/2, this.attributeColor );
			bitMapFont.write( this.attributOutputBmfFile );
//			TrueType2BmfConversion conversion = new TrueType2BmfConversion(ttfFont, attributeColor, stringToConvert, attributeAntialiased, attributeCharacterSpacing, attributOutputBmfFile);
//			conversion.createBmfFont();
		}
		catch(FontFormatException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
