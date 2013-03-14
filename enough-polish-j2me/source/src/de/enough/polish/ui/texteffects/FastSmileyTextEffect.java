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
	private transient final TextSmileyLayout textSmileyLayout = new TextSmileyLayout();
	
	public static void addSmiley( Smiley smiley )
	{
		smileysByText.put(smiley.text, smiley);
		searchTrie.addWord(smiley.text);
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
		// search for smileys, if none are registered or found just paint the text normally,
		// otherwise split up the text, change the spacing/lineheight
		this.textSmileyLayout.startInit(firstLineWidth, lineWidth, maxLines, maxLinesAppendix, maxLinesAppendixPosition, font, parent.getPaddingVertical());
		
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
					// ok, we need to add a new WrappedTextItem:
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
		if (this.isSmileysFound && this.textSmileyLayout.isFirstLineContainsSmiley)
		{
			return smileyHeight;
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
		WrappedText wrappedText;
		int x;
		int y;
		
		public SmileyTextItem(Smiley smiley, int x, int y)
		{
			this.smiley = smiley;
			this.x = x;
			this.y = y;
		}

		public SmileyTextItem(WrappedText wrappedText, int x, int y)
		{
			this.wrappedText = wrappedText;
			this.x = x;
			this.y = y;
		}
	}

	private static class TextSmileyLayout
	{
		public int	width;
		private final ArrayList elementsList = new ArrayList();
		private int initX;
		private int initY;
		private int initLineWidth;
		private int initMaxLines;
		private int initCurrentLine;
		private boolean initCurrentLineContainsSmiley;
		private Font	initFont;
		private int	initFontHeight;
		private int	initPaddingVertical;
		private int height;
		private String	initMaxLinesAppendix;
		private int	initMaxLinesAppendixPosition;
		boolean	isFirstLineContainsSmiley;
		
		public void startInit(int firstLineWidth, int lineWidth, int maxLines, String maxLinesAppendix, int maxLinesAppendixPosition, Font font, int paddingVertical)
		{
			this.initLineWidth = lineWidth;
			this.initMaxLines = maxLines;
			this.initMaxLinesAppendix = maxLinesAppendix;
			this.initMaxLinesAppendixPosition = maxLinesAppendixPosition;
			this.initFont = font;
			this.initFontHeight = font.getHeight();
			this.initPaddingVertical = paddingVertical;
			
			this.initCurrentLine = 0;
			this.initCurrentLineContainsSmiley = false;
			this.initX = lineWidth - firstLineWidth;
			this.initY = 0;
			this.width = 0;
			
			this.elementsList.clear();
			//System.out.println("startInitialization: lineWidth=" + lineWidth  + ", smileyWidth=" + smileyWidth);
		}
		
		public void stopInit()
		{
			this.initFont = null;
			if (this.initX == 0)
			{
				this.height = this.initY;
			}
			else
			{
				this.height = this.initY + this.initFontHeight;
			}
			if (this.initX > this.width)
			{
				this.width = this.initX;
			}
		}
		
		public boolean add( Smiley smiley )
		{
//			System.out.println("adding smiley [" + smiley.text + "], initX=" + this.initX + ", initY=" + this.initY + ", containsSmiley=" + this.initCurrentLineContainsSmiley);
			if (this.initY == 0)
			{
				this.isFirstLineContainsSmiley = true;
			}
			if (this.initX == 0 || this.elementsList.size() == 0)
			{
				this.initY += smileyHeight - this.initFontHeight - this.initPaddingVertical;
				this.initCurrentLineContainsSmiley = true;
			}
			SmileyTextItem item = new SmileyTextItem(smiley, this.initX, this.initY);
			this.elementsList.add(item);
			int x = this.initX + smileyWidth;
			if (x + smileyWidth >= this.initLineWidth) 
			{
				this.initCurrentLine++;
				if (this.initCurrentLine >= this.initMaxLines)
				{
					return false;
				}
				this.initX = 0;
				this.initY += this.initFontHeight + this.initPaddingVertical;
				this.initCurrentLineContainsSmiley = false;
			}
			else
			{
				this.initX = x;
				this.initCurrentLineContainsSmiley = true;
				if (x > this.width)
				{
					this.width = x;
				}
			}
			return true;
		}
		
		public boolean add(String text)
		{
			int maxLines = this.initMaxLines;
			if (maxLines != TextUtil.MAXLINES_UNLIMITED)
			{
				maxLines -= this.initCurrentLine;
			}
			if (this.initX == 0 && text.charAt(0) == ' ')
			{
				if (text.length() == 1)
				{
					return true;
				}
				text = text.substring(1);
			}
			WrappedText wrappedText = new WrappedText();
			TextUtil.wrap(text, this.initFont, this.initLineWidth - this.initX, this.initLineWidth, this.initMaxLines, this.initMaxLinesAppendix, this.initMaxLinesAppendixPosition, wrappedText);
			if (wrappedText.getMaxLineWidth() > this.width)
			{
				this.width = wrappedText.getMaxLineWidth();
			}
			//System.out.println("adding text [" + text + "], initX=" + this.initX + ", initY=" + this.initY + ", lastLineWidth=" + wrappedText.getLineWidth(wrappedText.size()-1));
			SmileyTextItem wrappedTextItem = new SmileyTextItem(wrappedText, this.initX, this.initY);
			this.elementsList.add(wrappedTextItem);
			int wrappedTextSize = wrappedText.size();
			if (wrappedTextSize == 1) // only a single line of text
			{
				int x = this.initX + wrappedText.getMaxLineWidth();
				if (x + smileyWidth >= this.initLineWidth)
				{
					this.initCurrentLine++;
					if (this.initCurrentLine >= this.initMaxLines)
					{
						return false;
					}
					this.initX = 0;
					this.initCurrentLineContainsSmiley = false;
					this.initY += this.initFontHeight + this.initPaddingVertical;
				}
				else // there is still space left on the current line:
				{
					this.initX = x;
					if (!this.initCurrentLineContainsSmiley)
					{
						if (this.initY == 0)
						{
							this.isFirstLineContainsSmiley = true;
						}
						wrappedTextItem.y += smileyHeight - this.initFontHeight - this.initPaddingVertical;
						this.initY += smileyHeight - this.initFontHeight - this.initPaddingVertical;
					}
				}
			}
			else // there is more than one line wrapped:
			{
				int lastLineWidth = wrappedText.getLineWidth(wrappedTextSize-1);
				if (lastLineWidth + smileyWidth >= this.initLineWidth)
				{
					this.initX = 0;
					wrappedTextSize++;
					this.initCurrentLine += wrappedTextSize;
				}
				else
				{
					this.initX = lastLineWidth;
					this.initCurrentLine += wrappedTextSize - 1;	
					wrappedTextSize--;
				}
				int y = this.initY;
				if (this.initCurrentLineContainsSmiley)
				{
					y += smileyHeight + this.initPaddingVertical;
					this.initCurrentLineContainsSmiley = false;
					wrappedTextSize--;
				}
				y += wrappedTextSize * (this.initFontHeight + this.initPaddingVertical) - this.initPaddingVertical;
				this.initY = y;
				if (this.initCurrentLine >= this.initMaxLines)
				{
					return false;
				}
			}
			return true;
		}
		
		public void drawStrings(int x, int y, int leftBorder, int rightBorder, int maxWidth, int layout, Graphics g)
		{
			x = leftBorder;
			boolean isLayoutRight = false;
			boolean isLayoutCenter = false;
			int centerX = 0;
			if ( ( layout & Item.LAYOUT_CENTER ) == Item.LAYOUT_CENTER ) {
				isLayoutCenter = true;
				centerX = leftBorder + (rightBorder - leftBorder) / 2;
			} else if ( ( layout & Item.LAYOUT_RIGHT ) == Item.LAYOUT_RIGHT ) {
				isLayoutRight = true;
			}
			int anchor = Graphics.TOP | Graphics.LEFT;
			// adjust the painting according to the layout:
			if (isLayoutRight) {
				//#if polish.Bugs.needsBottomOrientiationForStringDrawing
					anchor = Graphics.BOTTOM | Graphics.RIGHT;
				//#else
					anchor = Graphics.TOP | Graphics.RIGHT;
				//#endif
			} else if (isLayoutCenter) {
				//#if polish.Bugs.needsBottomOrientiationForStringDrawing
					anchor = Graphics.BOTTOM | Graphics.HCENTER;
				//#else
					anchor = Graphics.TOP | Graphics.HCENTER;
				//#endif
			} else {
				//#if polish.Bugs.needsBottomOrientiationForStringDrawing
					anchor = Graphics.BOTTOM | Graphics.LEFT;
				//#else
					anchor = Graphics.TOP | Graphics.LEFT;
				//#endif
			}
			int lineHeight = this.initFontHeight + this.initPaddingVertical;
			
			Object[] elements = this.elementsList.getInternalArray();
			int elementsSize = this.elementsList.size();
			boolean isLastElement = false;
			for (int elementsIndex = 0; elementsIndex < elementsSize; elementsIndex++)
			{
				if (elementsIndex == elementsSize -1) 
				{
					isLastElement = true;
				}
				SmileyTextItem item = (SmileyTextItem) elements[elementsIndex];
				WrappedText textLines = item.wrappedText;
				if (textLines == null)
				{
					// draw the smiley:
					Image image = item.smiley.getImage();
					if (image != null)
					{
						//#if polish.Bugs.needsBottomOrientiationForStringDrawing
							g.drawImage(image, x + item.x, y + item.y, Graphics.BOTTOM | Graphics.LEFT);
						//#else
							g.drawImage(image, x + item.x, y + item.y, Graphics.TOP | Graphics.LEFT);
						//#endif
					}
				} 
				else
				{
					String lineText;
					int lineX = x + item.x;
					int lineY = y + item.y;

					if (isLayoutRight) 
					{
						lineX = rightBorder;
					} 
					else if (isLayoutCenter) 
					{
						lineX = centerX;
					}
					
					Object[] lineObjects = textLines.getLinesInternalArray();
					int linesSize = textLines.size();
					for (int linesIndex = 0; linesIndex < linesSize; linesIndex++) 
					{
						lineText = (String) lineObjects[linesIndex];
						g.drawString( lineText, lineX, lineY, anchor);
						if ((linesIndex < linesSize - 2) || isLastElement)
						{
							lineY += lineHeight;
						} else {
							lineY += smileyHeight;
						}
						if (!isLayoutCenter && !isLayoutRight)
						{
							lineX = leftBorder;
						}
					}
				}
			}
			
		}

	}


}
