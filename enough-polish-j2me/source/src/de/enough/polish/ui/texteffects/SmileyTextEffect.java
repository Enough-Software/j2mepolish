//#condition polish.usePolishGui
/*
 * Created on 16-Nov-2005 at 12:22:20.
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
package de.enough.polish.ui.texteffects;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.IntHashMap;

/**
 * <p>Renders textual smileys with images.</p>
 * <p>Activate the smiley text effect by specifying <code>text-effect: smiley;</code> in your polish.css file.
 * </p>
 *
 * <p>Copyright (c) 2009 Enough Software</p>
 * @author Andre Schmidt, j2mepolish@enough.de
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SmileyTextEffect extends TextEffect {
	
	public static class Smiley
	{		
		public String[] smileys;
		public Image image;
		public String description;
		
		public Smiley(String[] smileys, String imageUrl) {
			this( smileys, imageUrl, null);
		}
		public Smiley(String[] smileys, String imageUrl, String description)
		{
			this.smileys = smileys;
			
			try
			{
				this.image = StyleSheet.getImage(imageUrl, this, false);
			}
			catch(IOException e)
			{
				//#debug error
				System.out.println("unable to load smiley image " + e);
			}
			
			this.description = description;
		}
	}
	/** an array of smileys that are used by all SmileyTextEffects */
	public static Smiley[] smileyList = null;		
	static IntHashMap smileyMap;
	static IntHashMap smileyHash;
	static boolean isInitialized;
	
	static int smileyWidth;
	static int smileyHeight;
	
	
	/**
	 * Creates a text with smileys
	 */
	public SmileyTextEffect() {
		super();
		
		//#if smileys:defined
			//#= smileyList = new Smiley[]${smileys};
		//#else
			smileyList = new Smiley[0];
		//#endif
	}
	
	/**
	 * Intializes this effect and loads smileys.
	 */
	public static void init() {

		smileyMap 		= new IntHashMap();
		smileyHash 	= new IntHashMap();
		if (smileyList == null || smileyList.length == 0 ) {
			return;
		}
		Image img = smileyList[0].image;
		if (img != null) {
			smileyWidth	= img.getWidth();
			smileyHeight	= img.getHeight();
		}
		
		for(int i=0; i<smileyList.length; i++)
		{
			Smiley smiley = smileyList[i];
			
			for(int j=0; j< smiley.smileys.length; j++)
			{
				String smileyText = smiley.smileys[j].toLowerCase(); 
				smileyMap.put(smileyText.hashCode(),smiley);
				
				char hash = smileyText.charAt(0);
				ArrayList smileys = (ArrayList)smileyHash.get(hash);
				if(smileys == null)
				{
					smileys = new ArrayList();
					smileys.add(smileyText);
					smileyHash.put(hash, smileys);
				}
				else
				{
					if(!smileys.contains(smiley))
					{
						smileys.add(smileyText);
					}
				}
			}
		}
		
		isInitialized = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#stringWidth(java.lang.String)
	 */
	public int stringWidth(String str) {
		if (!isInitialized) {
			init();
		}
		
		int textStart = 0;
		int stringWidth = 0;
		
		for (int index = 0; index < str.length(); index++) {
			char hash = Character.toLowerCase(str.charAt(index));
			ArrayList smileys = (ArrayList)smileyHash.get(hash);
			if(smileys != null)
			{
				String sequence = getNextSmiley(str, index, smileys);
				if(sequence != null)
				{
					int textWidth = getFont().substringWidth(str, textStart, index - textStart); 
					
					stringWidth += textWidth + smileyWidth;
					
					index += sequence.length();
					
					textStart = index;
					
					index--;
				}
			}
		}
		
		stringWidth += getFont().substringWidth(str, textStart, str.length() - textStart);
		
		return stringWidth;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#getFontHeight()
	 */
	public int getFontHeight() {
		if (!isInitialized) {
			init();
		}
		
		int fontHeight = super.getFontHeight();
		
		if(fontHeight > smileyHeight)
		{
			return fontHeight;
		}
		else
		{
			return smileyHeight;
		}
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(java.lang.String, int, javax.microedition.lcdui.Font, int, int)
	 */
	public String[] wrap(String text, int textColor, Font font,
			int firstLineWidth, int lineWidth)
	{
		return wrap( text, textColor, font, firstLineWidth, lineWidth, -1, null );
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(java.lang.String, int, javax.microedition.lcdui.Font, int, int, int, java.lang.String)
	 */
	public String[] wrap(String text, int textColor, Font font,
			int firstLineWidth, int lineWidth, int maxLines, String maxLinesAppendix)
	{
		if (!isInitialized) {
			init();
		}
		if (firstLineWidth <= 0 || lineWidth <= 0) {
			//#debug error
			System.out.println("INVALID LINE WIDTH FOR SPLITTING " + firstLineWidth + " / " + lineWidth + " ( for string " + text + ")");
			//#if polish.debug.error
				new RuntimeException().printStackTrace();
			//#endif
			return new String[]{ text };
		}
		boolean hasLineBreaks = (text.indexOf('\n') != -1);
		int completeWidth = stringWidth(text);
		if ( (completeWidth <= firstLineWidth && !hasLineBreaks) ) { // || (value.length() <= 1) ) {
			// the given string fits on the first line:
			//if (hasLineBreaks) {
			//	return split( "complete/linebreaks:" + completeWidth + "> " + value, '\n');
			//} else {
				return new String[]{  text };
			//}
		}
		// the given string does not fit on the first line:
		ArrayList lines = new ArrayList();
		if (!hasLineBreaks) {
			wrap( text, font, completeWidth, firstLineWidth, lineWidth, lines );
		} else {
			// now the string will be splitted at the line-breaks and
			// then each line is processed:
			char[] valueChars = text.toCharArray();
			int lastIndex = 0;
			char c =' ';
			for (int i = 0; i < valueChars.length; i++) {
				c = valueChars[i];
				if (c == '\n' || i == valueChars.length -1 ) {
					String line = null;
					if (i == valueChars.length -1) {
						line = new String( valueChars, lastIndex, (i + 1) - lastIndex );
						//System.out.println("wrap: adding last line " + line );
					} else {
						line = new String( valueChars, lastIndex, i - lastIndex );
						//System.out.println("wrap: adding " + line );
					}
					completeWidth = stringWidth(line);
					if (completeWidth <= firstLineWidth ) {
						lines.add( line );						
					} else {
						wrap(line, font, completeWidth, firstLineWidth, lineWidth, lines);
					}
					lastIndex = i + 1;
					// after the first line all line widths are the same:
					firstLineWidth = lineWidth;
				} // for each line
			} // for all chars
			// special case for lines that end with \n: add a further line
			if (c == '\n') {
				lines.add("");
			}
		}
		//#debug
		System.out.println("Wrapped [" + text + "] into " + lines.size() + " rows.");
		if (maxLines != -1 && lines.size() > maxLines) {
			String[] result = (String[]) lines.toArray( new String[ maxLines ]);
			if (maxLinesAppendix != null) {
				String lastLine = result[ maxLines -1 ];
				int width = font.stringWidth(lastLine);
				int appendixWidth = font.stringWidth(maxLinesAppendix);
				while (width + appendixWidth > firstLineWidth && lastLine.length() > 0) {
					lastLine = lastLine.substring(0, lastLine.length() - 1);
					width = font.stringWidth(lastLine);
				}
				result[maxLines-1] = lastLine + maxLinesAppendix;
			}
		}
		return (String[]) lines.toArray( new String[ lines.size() ]);
	}
	
	
	
	/**
	 * Wraps a text chunk without linebreaks
	 * @param value the text chunk
	 * @param font the used font
	 * @param completeWidth the complete width of the text chunk
	 * @param firstLineWidth the available width for the first line
	 * @param lineWidth the available width for subsequent lines
	 * @param list for storing the wrapped text lines
	 */
	public void wrap( String value, Font font, 
			int completeWidth, int firstLineWidth, int lineWidth, 
			ArrayList list ) 
	{
		if (!isInitialized) {
			init();
		}
		
		char[] valueChars = value.toCharArray();
		int startPos = 0;
		int lastSpacePos = -1;
		int lastSpacePosLength = 0;
		int currentLineWidth = 0;
		for (int i = 0; i < valueChars.length; i++) {
			char c = valueChars[i];
			char hash = Character.toLowerCase(c);
			
			String smiley = null;
			ArrayList smileys = (ArrayList)smileyHash.get(hash);
			if(smileys != null)
			{
				smiley = getNextSmiley(value, i, smileys);
			}
			
			int elementWidth = 0;
			
			if(smiley == null)
			{
				elementWidth = font.charWidth( c );
			}
			else
			{
				elementWidth = smileyWidth;
			}
			
			currentLineWidth += elementWidth;
			
			if (c == '\n') {
				list.add( new String( valueChars, startPos, i - startPos ) );
				lastSpacePos = -1;
				startPos = i+1;
				currentLineWidth = 0;
				firstLineWidth = lineWidth; 
				i = startPos;
			} else if (currentLineWidth >= firstLineWidth && i > 0) {
				if ( lastSpacePos == -1 ) {
					//#debug debug
					System.out.println("value=" + value + ", i=" + i + ", startPos=" + startPos);
					list.add( new String( valueChars, startPos, i - startPos ) );
					startPos =  i;
					currentLineWidth = elementWidth;
				} else {
					currentLineWidth -= lastSpacePosLength;
					list.add( new String( valueChars, startPos, lastSpacePos - startPos ) );
					startPos =  lastSpacePos + 1;
					lastSpacePos = -1;
				}
				firstLineWidth = lineWidth; 
			} else if (c == ' ' || c == '\t') {
				lastSpacePos = i;
				lastSpacePosLength = currentLineWidth;
			}
			
			if(smiley != null)
			{
				i += smiley.length() - 1;
			}
		} 
		
		// add tail:
		list.add( new String( valueChars, startPos, valueChars.length - startPos ) );
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y, int orientation,
			Graphics g) 
	{	
		if (!isInitialized) {
			init();
		}
		
		int textStart = 0;
		int drawStart = 0;
		
		for (int index = 0; index < text.length(); index++) {
			char hash = Character.toLowerCase(text.charAt(index));
			ArrayList smileys = (ArrayList)smileyHash.get(hash);
			if(smileys != null)
			{
				String sequence = getNextSmiley(text, index, smileys);
				if(sequence != null)
				{
					Smiley smiley = (Smiley)smileyMap.get(sequence.hashCode());
					
					g.drawSubstring(text, textStart, index - textStart, x + drawStart, y, orientation);
					
					int textWidth = getFont().substringWidth(text, textStart, index - textStart); 
					
					drawStart += textWidth;
					
					g.drawImage(smiley.image, x + drawStart, y, orientation);
					
					drawStart += smileyWidth;
					
					index += sequence.length();
					
					textStart = index;
					
					index--;
				}
			}
		}
		
		g.drawSubstring(text, textStart, text.length() - textStart, x + drawStart, y, orientation);
	}
	
	/**
	 * @param line
	 * @param offset
	 * @param smileys
	 * @return the smiley
	 */
	protected String getNextSmiley(String line, int offset, ArrayList smileys)
	{
		for (int i = 0; i < smileys.size(); i++) {
			String smiley = (String)smileys.get(i);
			if(startsWithSmiley(line,smiley, offset))
				return smiley;
		}
		
		return null;
	}
	
	/**
	 * Returns true if the specified line starts with the specified
	 * smiley at the specified offset
	 * @param line the line
	 * @param smiley the smiley
	 * @param offset the offset 
	 * @return true if the specified line starts with the specified
	 * smiley at the specified offset otherwise false
	 */
	boolean startsWithSmiley(String line, String smiley, int offset)
	{
		for (int i = 0; i < smiley.length(); i++) {
			if((offset + i) < line.length())
			{
				char lineCharacter = Character.toLowerCase(line.charAt(offset + i)); 
				if(smiley.charAt(i) != lineCharacter)
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}

		return true;
	}
}
