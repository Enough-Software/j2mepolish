//#condition polish.usePolishGui
/*
 * Created on 22-Nov-2012 at 10:51:51.
 * 
 * Copyright (c) 2012 Robert Virkus / Enough Software
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
import java.util.Hashtable;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.Trie;
import de.enough.polish.util.WrappedText;

/**
 * Performant and flexible smiley text effect. All smileys are required to have the same dimensions.
 *  
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FastSmileyTextEffect 
extends TextEffect
{

	private static final Hashtable smileysByText = new Hashtable();
	private static final Trie searchTrie = new Trie();
	
	private static int smileyHeight;
	private static int smileyWidth;
	
	private boolean isSmileysFound;
	//private transient final TextSmileyLayout textSmileyLayout = new TextSmileyLayout();
	private transient final LineBasedLayout textSmileyLayout = new LineBasedLayout();
	
	private static boolean isConfigured;
	
	public static void addSmiley( Smiley smiley )
	{
		isConfigured = true;
		smileysByText.put(smiley.text, smiley);
		searchTrie.addWord(smiley.text);
	}
	
	/**
	 * Allows to specify the smiley's dimension.
	 * Typically this is done automatically when the first smiley
	 * is being resolved - however this method can be useful when
	 * the smileys have different dimensions and you want to make
	 * sure that the biggest smiley is being used for layouting.
	 * 
	 * @param width the width of smileys in pixel
	 * @param height the height of smileys in pixel
	 */
	public static void initSmileyDimension(int width, int height)
	{
		smileyWidth = width;
		smileyHeight = height;
	}
	
	public FastSmileyTextEffect()
	{
		this.isTextSensitive = true;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(de.enough.polish.ui.StringItem, java.lang.String, int, javax.microedition.lcdui.Font, int, int, int, java.lang.String, int, de.enough.polish.util.WrappedText)
	 */
	public void wrap(StringItem parent, String text, int textColor, Font font,
			int firstLineWidth, int lineWidth, int maxLines,
			String maxLinesAppendix, int maxLinesAppendixPosition,
			WrappedText wrappedText)
	{
		if (!isConfigured)
		{
			// add default configuration:
			Smiley smiley;
			smiley = new Smiley(":-)", "/emoticon_smile.png");
			addSmiley(smiley);
			smiley = new Smiley(":)", "/emoticon_smile.png");
			addSmiley(smiley);
			smiley = new Smiley(":-(", "/emoticon_frown.png");
			addSmiley(smiley);
			smiley = new Smiley(":(", "/emoticon_frown.png");
			addSmiley(smiley);
			smiley = new Smiley(":-D", "/emoticon_laugh.png");
			addSmiley(smiley);
			smiley = new Smiley(":D", "/emoticon_laugh.png");
			addSmiley(smiley);
			smiley = new Smiley(":'-(", "/emoticon_cry.png");
			addSmiley(smiley);
			smiley = new Smiley(":'(", "/emoticon_cry.png");
			addSmiley(smiley);
		}
		// search for smileys, if none are registered or found just paint the text normally,
		// otherwise split up the text, change the spacing/lineheight
		
		Trie.TrieSearchResult searchResult = new Trie.TrieSearchResult();
		int startIndex = 0;
		
		boolean found = searchTrie.search(text, startIndex, searchResult);
		
		
		if (!found)
		{
			this.isSmileysFound = false;
			super.wrap(parent, text, textColor, font, firstLineWidth, lineWidth, maxLines,
					maxLinesAppendix, maxLinesAppendixPosition, wrappedText);
		}
		else
		{
			this.textSmileyLayout.startInit(firstLineWidth, lineWidth, maxLines, maxLinesAppendix, maxLinesAppendixPosition, font, parent.getPaddingVertical());
			this.isSmileysFound = true;
			boolean continueLayout = false;
			while (found)
			{
				Smiley smiley = (Smiley) smileysByText.get(searchResult.matchedWord);
				if (smileyHeight == 0)
				{
					Image image = smiley.getImage();
					if (image != null)
					{
						smileyWidth = image.getWidth();
						smileyHeight = image.getHeight();
					}
				}
				if (searchResult.matchedWordIndex > startIndex)
				{
					// ok, we need to insert text first:
					String intermediateText = text.substring(startIndex, searchResult.matchedWordIndex);
					continueLayout = this.textSmileyLayout.add(intermediateText);
					if (!continueLayout)
					{
						break;
					}
				}
				continueLayout = this.textSmileyLayout.add(smiley);
				if (!continueLayout)
				{
					break;
				}

				startIndex = searchResult.matchedWordIndex + searchResult.matchedWord.length();
				found = searchTrie.search(text, startIndex, searchResult);
			}
			if (continueLayout)
			{
				String textTail = text.substring(startIndex);
				this.textSmileyLayout.add(textTail);
			}
			this.textSmileyLayout.stopInit();
			wrappedText.addLine("", this.textSmileyLayout.width);

		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#calculateLinesHeight(de.enough.polish.util.WrappedText, int, int)
	 */
	public int calculateLinesHeight(WrappedText lines, int lineHeight,
			int paddingVertical)
	{
		if (!this.isSmileysFound)
		{
			return super.calculateLinesHeight(lines, lineHeight, paddingVertical);
		}
		else
		{
			return this.textSmileyLayout.height;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#getFontHeightOfFirstLine()
	 */
	public int getFontHeightOfFirstLine()
	{
		if (this.isSmileysFound)
		{
			return ((SmileyTextLine)this.textSmileyLayout.lines.get(0)).lineHeight;
		}
		return super.getFontHeightOfFirstLine();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawStrings(de.enough.polish.util.WrappedText, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawStrings(WrappedText textLines, int textColor, int x, int y,
			int leftBorder, int rightBorder, int lineHeight, int maxWidth,
			int layout, Graphics g)
	{
		//System.out.println("drawStrings: smileysFound=" + this.isSmileysFound);
		if (!this.isSmileysFound)
		{
			super.drawStrings(textLines, textColor, x, y, leftBorder, rightBorder,
				lineHeight, maxWidth, layout, g);
		}
		else
		{
			this.textSmileyLayout.drawStrings( x, y, leftBorder, rightBorder, maxWidth, layout, g);
		}
		
	}




	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int anchor, Graphics g)
	{
		g.drawString(text, x, y, anchor);
	}

	public static class Smiley 
	{
		String text;
		Image image;
		String imageUrl;
		
		public Smiley( String text, Image image)
		{
			if (image == null || text == null)
			{
				throw new IllegalArgumentException();
			}
			this.text = text;
			this.image = image;
		}
		
		public Smiley( String text, String imageUrl)
		{
			if (imageUrl == null || text == null)
			{
				throw new IllegalArgumentException();
			}
			this.text = text;
			this.imageUrl = imageUrl;
		}

		/**
		 * @return the text
		 */
		public String getText()
		{
			return this.text;
		}

		/**
		 * @return the image
		 */
		public Image getImage()
		{
			if (this.image == null && this.imageUrl != null)
			{
				try
				{
					this.image = StyleSheet.getImage(this.imageUrl, this, false);
				} catch (IOException e)
				{
					//#debug error
					System.out.println("Unable to load image " + this.imageUrl + e);
				}
			}
			return this.image;
		}

		/**
		 * @return the imageUrl
		 */
		public String getImageUrl()
		{
			return this.imageUrl;
		}
		
		
	}

	private static class SmileyTextItem 
	{
		Smiley smiley;
		String text;
		int x;
		
		public SmileyTextItem(Smiley smiley, int x)
		{
			this.smiley = smiley;
			this.x = x;
		}

		public SmileyTextItem(String text, int x)
		{
			this.text = text;
			this.x = x;
		}

	}

	
	private static class LineBasedLayout
	{
		private ArrayList lines;
		private SmileyTextLine currentLine;
		public int	width;
		private int height;
		
		private int initLineWidth;
		private int initFirstLineWidth;
		private int initMaxLines;
		private Font	initFont;
		private int	initFontHeight;
		private int	initPaddingVertical;
		private String	initMaxLinesAppendix;
		private int	initMaxLinesAppendixPosition;
		
		public LineBasedLayout()
		{
			this.lines = new ArrayList();
		}
		
		public void startInit(int firstLineWidth, int lineWidth, int maxLines, String maxLinesAppendix, int maxLinesAppendixPosition, Font font, int paddingVertical)
		{
			this.initFirstLineWidth = firstLineWidth;
			this.initLineWidth = lineWidth;
			this.initMaxLines = maxLines;
			this.initMaxLinesAppendix = maxLinesAppendix;
			this.initMaxLinesAppendixPosition = maxLinesAppendixPosition;
			this.initFont = font;
			this.initFontHeight = font.getHeight();
			this.initPaddingVertical = paddingVertical;
			
			this.width = 0;
			this.height = 0;
			
			this.lines.clear();
			this.currentLine = new SmileyTextLine();
			this.lines.add(this.currentLine);
			//System.out.println("startInitialization: lineWidth=" + lineWidth  + ", smileyWidth=" + smileyWidth);
		}
		
		public void stopInit()
		{
			this.initFont = null;
			lineBreak();
		}
		
		private SmileyTextLine checkLineBreak(int width)
		{
			SmileyTextLine current = this.currentLine;
			if (current.lineWidth + width > this.initFirstLineWidth)
			{
				lineBreak();
				current = new SmileyTextLine();
				this.lines.add(current);
				this.currentLine = current;
			}
			return current;
		}
		
		private void lineBreak()
		{
			SmileyTextLine current = this.currentLine;
			if (current.lineWidth > this.width)
			{
				this.width = current.lineWidth;
			}
			this.height += current.lineHeight;
			if (lines.size() > 1)
			{
				this.height +=  this.initPaddingVertical;
			}
			this.initFirstLineWidth = this.initLineWidth;

		}
		public boolean add( Smiley smiley )
		{
			SmileyTextLine current = this.currentLine;
			current.addSmiley(smiley);
			// make sure at least another smiley fits:
			checkLineBreak(smileyWidth);
			if ((this.initMaxLines != TextUtil.MAXLINES_UNLIMITED) &&  (this.lines.size() > this.initMaxLines))
			{
				return false;
			}
			return true;
		}
		
		public boolean add(String text)
		{
			if (text.length() == 0)
			{
				return true;
			}
			int maxLines = this.initMaxLines;
			if (maxLines != TextUtil.MAXLINES_UNLIMITED)
			{
				maxLines -= this.lines.size() - 1;
			}
			SmileyTextLine current = this.currentLine;
			
			// remove space at beginning of a line:
			if (current.lineWidth == 0 && text.charAt(0) == ' ')
			{
				if (text.length() == 1)
				{
					return true;
				}
				text = text.substring(1);
			}
			WrappedText wrappedText = new WrappedText();
			TextUtil.wrap(text, this.initFont, this.initFirstLineWidth - current.lineWidth, this.initLineWidth, maxLines, this.initMaxLinesAppendix, this.initMaxLinesAppendixPosition, wrappedText);
			if (wrappedText.getMaxLineWidth() > this.width)
			{
				this.width = wrappedText.getMaxLineWidth();
			}
			int wrappedTextSize = wrappedText.size();
			if (wrappedTextSize == 1) // only a single line of text
			{
				current.addText(text, wrappedText.getMaxLineWidth(), this.initFontHeight);
			}
			else // there is more than one line wrapped:
			{
				for (int wrappedLineIndex=0; wrappedLineIndex < wrappedTextSize; wrappedLineIndex++)
				{
					String lineText = wrappedText.getLine(wrappedLineIndex);
					int lineWidth = wrappedText.getLineWidth(wrappedLineIndex);
					current.addText(lineText, lineWidth, this.initFontHeight);
					if (wrappedLineIndex < wrappedTextSize - 1)
					{
						lineBreak();
						current = new SmileyTextLine();
						this.lines.add(current);
						this.currentLine = current;
					}
				}

			}
			// make sure at least another smiley fits:
			checkLineBreak(smileyWidth);
			return (maxLines == TextUtil.MAXLINES_UNLIMITED) || (this.lines.size() <= maxLines);
		}
		
		public void drawStrings(int x, int y, int leftBorder, int rightBorder, int maxWidth, int layout, Graphics g)
		{
			//System.out.println("draw");
			//x = leftBorder;
			boolean isLayoutRight = false;
			boolean isLayoutCenter = false;
			if ( ( layout & Item.LAYOUT_CENTER ) == Item.LAYOUT_CENTER ) {
				isLayoutCenter = true;
			} else if ( ( layout & Item.LAYOUT_RIGHT ) == Item.LAYOUT_RIGHT ) {
				isLayoutRight = true;
			}
			
			Object[] lines = this.lines.getInternalArray();
			int linesSize = this.lines.size();
			//#if !polish.Bugs.needsBottomOrientiationForStringDrawing
				y += this.initFontHeight;
			//#endif
			int lineX;
			int availableLineWidth = rightBorder - x;
			int smileyTextDiff = smileyHeight - this.initFontHeight;
			for (int lineIndex = 0; lineIndex < linesSize; lineIndex++)
			{
				SmileyTextLine line = (SmileyTextLine) lines[lineIndex];
				lineX = x;
				if (isLayoutRight)
				{
					lineX = rightBorder - line.lineWidth;
				}
				else if (isLayoutCenter)
				{
					lineX = x + (availableLineWidth - line.lineWidth)/2;
				}
				line.paint(lineX, y, smileyTextDiff, g);
				availableLineWidth = rightBorder - leftBorder;
				x = leftBorder;
				y += line.lineHeight + this.initPaddingVertical;
			}
			
		}
	}
	
	private static class SmileyTextLine
	{
		int lineWidth;
		int lineHeight;
		
		private boolean isContainsSmiley;
		
		private ArrayList elements;
		
		public SmileyTextLine()
		{
			elements = new ArrayList();
		}
		public void addText(String text, int width, int fontHeight)
		{
			SmileyTextItem item = new SmileyTextItem(text, this.lineWidth);
			elements.add(item);
			this.lineWidth += width;
			if (fontHeight > this.lineHeight)
			{
				this.lineHeight = fontHeight;
			}
		}
		
		public void addSmiley(Smiley smiley)
		{
			
			SmileyTextItem item = new SmileyTextItem(smiley, this.lineWidth);
			elements.add(item);
			this.lineWidth += smileyWidth;
			if (smileyHeight > this.lineHeight)
			{
				this.lineHeight = smileyHeight;
			}
			this.isContainsSmiley = true;
		}
		
		public void paint(int x, int y, int smileyTextDiff, Graphics g)
		{
			int size = this.elements.size();
			Object[] items = this.elements.getInternalArray();
			int yAdjust = 0;
			if (this.isContainsSmiley)
			{
				yAdjust = smileyTextDiff;
			}
			for (int i = 0; i < size; i++) 
			{
				SmileyTextItem item = (SmileyTextItem)items[i];
				if (item.text != null)
				{
					g.drawString(item.text, x + item.x, y + yAdjust, Graphics.BOTTOM | Graphics.LEFT);
				}
				else
				{
					g.drawImage(item.smiley.getImage(), x + item.x, y + yAdjust, Graphics.BOTTOM | Graphics.LEFT);
				}
			}
		}
	}

}
